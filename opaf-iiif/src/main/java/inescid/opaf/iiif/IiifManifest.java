package inescid.opaf.iiif;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.iterator.ExtendedIterator;

import inescid.opaf.data.RdfReg;
import inescid.opaf.framework.FetchRequest;

public class IiifManifest implements Serializable{
	String jsonld;
	String uri;
	transient Model modelRdf;
	transient Resource resource;
	IiifPresentationMetadata metadata;
	
	public IiifManifest(String uri, String jsonld) {
		super();
		this.uri = uri;
		this.jsonld = jsonld;
	}


	public Model getRdfModel() {
		if(modelRdf==null) {
			modelRdf = ModelFactory.createDefaultModel();
			StringReader bytesIs = new StringReader(jsonld);
			RDFDataMgr.read(modelRdf, bytesIs, uri, Lang.JSONLD);
			bytesIs.close();
		}
		return modelRdf;
	}

	public String getUri() {
		return uri;
	}

	public Resource getResource() {
		if(resource==null) 
			resource = getRdfModel().getResource(uri);
		return resource;
	}
	
	public IiifPresentationMetadata getMetadata() {
		if(metadata==null) {
			IiifPresentationMetadata md=new IiifPresentationMetadata(uri);
			Statement rightsPropVal = getResource().getProperty(RdfReg.DCTERMS_RIGHTS);
			if(rightsPropVal!=null )
				md.setLicense(rightsPropVal.getObject().toString());
			Statement labelPropVal = getResource().getProperty(RdfReg.RDFS_LABEL);
			if(labelPropVal!=null )
				md.setTitle(labelPropVal.getObject().toString());
			Statement navDatePropVal = resource.getProperty(RdfReg.IIIF_NAV_DATE);
			if(navDatePropVal!=null )
				md.setNavDate(navDatePropVal.getObject().toString());
			Statement metadataPropVal = resource.getProperty(RdfReg.IIIF_METADATA_LABELS);
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
			metadata=md;
		}			
		return metadata;
		
	}
	
}
