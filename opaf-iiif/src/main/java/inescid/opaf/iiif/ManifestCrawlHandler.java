package inescid.opaf.iiif;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.FetchRequest;
import inescid.opaf.framework.ResponseHandler;

public abstract class ManifestCrawlHandler implements ResponseHandler {

	private static Logger log = LoggerFactory.getLogger(ManifestCrawlHandler.class);

	CrawlingSession session;
	
	public ManifestCrawlHandler() {
		super();
	}

	public void setSession(CrawlingSession session) {
		this.session = session;
	}
	@Override
	public void handle(FetchRequest respondedFetchRequest) {
		try {
			int statusCode = respondedFetchRequest.getResponse().getStatusLine().getStatusCode();
			if(statusCode==200) {
				Content content = respondedFetchRequest.getContent();
				handleJsonld(respondedFetchRequest, content.asBytes());
			} else {
				log.info("Invalid response for manifest request: "+statusCode);
			}
		} catch (IOException e) {
			handleJsonldError(e, respondedFetchRequest);
		} 
	}

	public void handleJsonld(FetchRequest respondedFetchRequest, byte[] bytesOfManifest) {
		Model modelRdf = ModelFactory.createDefaultModel();
		ByteArrayInputStream bytesIs = new ByteArrayInputStream(bytesOfManifest);
		RDFDataMgr.read(modelRdf, bytesIs, Lang.JSONLD);
		try {
			bytesIs.close();
		} catch (IOException e) {
			handleJsonldError(e, null);
		}
		Resource resource = modelRdf.getResource(respondedFetchRequest.getUrl());
		if(resource==null) {
			log.warn("url not found as subject in RDF: "+respondedFetchRequest.getUrl());
		} else {
			Resource type = resource.getPropertyResourceValue(RdfReg.RDF_TYPE);
			if(type!=null && type.getURI().endsWith("#Collection")) {
				log.debug("Harvesting IIIF Collection: "+ respondedFetchRequest.getUrl());
				handleCollection(respondedFetchRequest, modelRdf);
			} else {
				log.debug("Processing IIIF Manifest: "+ respondedFetchRequest.getUrl());
				try {
					handleManifest(respondedFetchRequest, modelRdf, resource);
				} catch (Exception e) {
					log.error("Error in "+respondedFetchRequest.getUrl(), e);
				}
			}
		}
	}
	
	protected void handleJsonldError(Throwable e, FetchRequest respondedFetchRequest) {
		if(respondedFetchRequest==null)
			log.error(e.getMessage(), e);
		else
			log.error(respondedFetchRequest.getUrl(), e);
	}

	protected void handleCollection(FetchRequest respondedFetchRequest, Model model) {
		ResIterator manifests = model.listResourcesWithProperty(
				RdfReg.RDF_TYPE,
				RdfReg.IIIF_MANIFEST);
		
		while (manifests.hasNext()) {
			Resource manif = manifests.next();
			try {
				session.fetchAsync(manif.getURI());
			} catch (InterruptedException e) {
				log.info(e.getMessage(), e);
				break;
			}
		}		
	}
	protected void handleManifest(FetchRequest respondedFetchRequest, Model modelManifest, Resource manif) throws Exception {
		IiifPresentationMetadata md = parseMetadata(respondedFetchRequest, modelManifest, manif);
		
		StmtIterator seeAlso = manif.listProperties(RdfReg.RDFS_SEE_ALSO);
		while (seeAlso.hasNext()) {
			Statement s = seeAlso.next();
			String seeAlsoUrl = s.getObject().toString();
			// System.out.println("seeAlso:
			// "+s.getObject().toString());
			try {
				FetchRequest seeAlsoFetched = session.fetch(seeAlsoUrl);
				if (seeAlsoFetched.getResponseStatusCode()==200) {
					Content seeAlsoContent = seeAlsoFetched.getContent();
					ContentType type = seeAlsoContent.getType();
					if (type.equals(ContentType.APPLICATION_XML) 
							|| type.equals(ContentType.APPLICATION_JSON)) {
						
						IiifSeeAlsoProperty sa = new IiifSeeAlsoProperty();
						sa.setSeeAlsoContent(seeAlsoContent.asBytes());
						sa.setSeeAlsoUrl(seeAlsoUrl);
						sa.setSeeAlsoContentType(seeAlsoContent.getType().getMimeType());
						md.addSeeAlso(sa);
					}
				}
			} catch (IOException e) {
				log.error("error fetching seeAlso "+seeAlsoUrl, e);
			} catch (InterruptedException e) {
				log.info(e.getMessage(), e);
				break;
			}
		}
		handleMetadata(md);
	}
	
	protected IiifPresentationMetadata parseMetadata(FetchRequest manifestRequest, Model modelManifest, Resource manifResource) {
		IiifPresentationMetadata md=new IiifPresentationMetadata(manifestRequest.getUrl());
		Statement metadataPropVal = manifResource.getProperty(RdfReg.IIIF_METADATA_LABELS);
		if(metadataPropVal!=null) {
			Resource mtds = (Resource)metadataPropVal.getObject();
			
			RDFList rdfList = mtds.as( RDFList.class );
            ExtendedIterator<RDFNode> items = rdfList.iterator();
            while ( items.hasNext() ) {
            	IiifMetadataElement m=new IiifMetadataElement();
                Resource item = items.next().asResource();
                StmtIterator allStms = item.listProperties();
    			while (allStms.hasNext()) {
    				Statement stm = allStms.next();
    				if(stm.getPredicate().equals(RdfReg.RDFS_LABEL)) {
    					m.addLabel(new LocalizedLiteral(stm.getLiteral().getString(),stm.getLiteral().getLanguage()));
    				}if(stm.getPredicate().equals(RdfReg.RDF_VALUE)) {
    					m.addValue(new LocalizedLiteral(stm.getLiteral().getString(),stm.getLiteral().getLanguage()));
    				}
    			}
            	md.addMetadata(m);
    			
//                RDFNode value1 = item.getRequiredProperty( myitemvalue1 ).getObject();
//                RDFNode value2 = item.getRequiredProperty( myitemvalue2 ).getObject();
//                System.out.println( item+" has:\n\tvalue1: "+value1+"\n\tvalue2: "+value2 );
            }
			
//
//			StmtIterator allStms = mtds.listProperties();
//			while (allStms.hasNext()) {
//				Statement stm = allStms.next();
//					System.out.println(stm);
//			}
//			List<RDFNode> metadataLabels = ((RDFList)metadataPropVal.getObject()).asJavaList();
//			for(RDFNode mlNode : metadataLabels) {
//				System.out.println(mlNode.getClass());
//				System.out.println(mlNode);
//			}
		}
		
		return md;
		
	}
	
	protected abstract void handleMetadata(IiifPresentationMetadata metadata) throws Exception;

	
}