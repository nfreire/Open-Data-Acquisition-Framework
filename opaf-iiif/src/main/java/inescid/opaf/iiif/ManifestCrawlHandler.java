package inescid.opaf.iiif;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.cookie.RFC2965DiscardAttributeHandler;
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
import org.apache.jena.riot.RiotException;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.FetchRequest;
import inescid.opaf.framework.ResponseHandler;
import inescid.opaf.framework.UrlRequest;
import inescid.util.DevelopementSingleton;
import inescid.util.RdfUtil;
import inescid.util.XmlUtil;

public abstract class ManifestCrawlHandler implements ResponseHandler {
	private static final Charset UTF8=Charset.forName("UTF8");
	private static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);

	private static final Logger log = LoggerFactory.getLogger(ManifestCrawlHandler.class);
	private static final Logger logManifestProblems = LoggerFactory.getLogger("APPLICATIONAL_LOG_IIIF_MANIFEST_PROBLEMS");
	
	private static final boolean DOWNLOAD_SEEALSO=true;
	private static final boolean VALIDATE_SEEALSO_CONTENT_SINTAX=true;
	
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
				IiifManifest manifest=new IiifManifest(respondedFetchRequest.getUrl(), respondedFetchRequest.getContent().asString());
				handlePresentationResponse(respondedFetchRequest, manifest);
			} else if(statusCode==304) {
				log.info("not modified, skipped: "+respondedFetchRequest.getUrl());
			} else {
				handleJsonldError(respondedFetchRequest);
			}
		} catch (IOException e) {
			handleJsonldError(e, respondedFetchRequest);
		} 
	}

	public void handlePresentationResponse(FetchRequest respondedFetchRequest, IiifManifest manifest) {
		Resource resource = manifest.getResource();
		if(resource==null) {
			log.warn("url not found as subject in RDF: "+respondedFetchRequest.getUrl());
		} else {
			Resource type = resource.getPropertyResourceValue(RdfReg.RDF_TYPE);
			try {
				if(type!=null && type.equals(RdfReg.IIIF_COLLECTION)) {
					log.debug("Harvesting IIIF Collection: "+ respondedFetchRequest.getUrl());
					handleCollection(respondedFetchRequest, manifest);
				} else {
 					log.debug("Processing IIIF Manifest: "+ respondedFetchRequest.getUrl());
						handleManifest(respondedFetchRequest, manifest);
				}
			} catch (Exception e) {
				handleJsonldError(e, respondedFetchRequest);
			}
		}
	}
	
	protected void handleJsonldError(Throwable e, FetchRequest respondedFetchRequest) {
		if(respondedFetchRequest==null)
			log.error(e.getMessage(), e);
		else {
			log.error(respondedFetchRequest.getUrl(), e);
			logManifestProblems.error(respondedFetchRequest.getUrl() + " ; " + e.getMessage());
		}
	}
	protected void handleJsonldError(FetchRequest respondedFetchRequest) {
		log.error(respondedFetchRequest.getUrl()+" - HTTP Status: "+respondedFetchRequest.getResponseStatusCode());
		logManifestProblems.error(respondedFetchRequest.getUrl() + " ; " + "HTTP Status: "+respondedFetchRequest.getResponseStatusCode());
	}

	protected void handleCollection(FetchRequest respondedFetchRequest, IiifManifest manifest) throws IOException {
		ResIterator manifests = manifest.getRdfModel().listResourcesWithProperty(
				RdfReg.RDF_TYPE,
				RdfReg.IIIF_MANIFEST);
		
		while (manifests.hasNext()) {
			if(DevelopementSingleton.DEVEL_TEST) {	
				DevelopementSingleton.RESOURCE_HARVEST_CNT++;
				if(DevelopementSingleton.stopHarvest()) break;
			}
			Resource manif = manifests.next();
			try {
//				session.fetchWithPriority(manif.getURI());
				session.fetchAsync(new UrlRequest(manif.getURI()));
			} catch (InterruptedException e) {
				log.info(e.getMessage(), e);
				break;
			}
		}	
		
		ResIterator colls = manifest.getRdfModel().listResourcesWithProperty(
				RdfReg.RDF_TYPE,
				RdfReg.IIIF_COLLECTION);
		
		while (colls.hasNext()) {
			Resource col = colls.next();				
			if(DevelopementSingleton.DEVEL_TEST) {	
				if(DevelopementSingleton.RESOURCE_HARVEST_CNT > 5) break;
			}
			try {
//				session.fetchWithPriority(manif.getURI());
				session.fetchAsync(new UrlRequest(col.getURI()));
			} catch (InterruptedException e) {
				log.info(e.getMessage(), e);
				break;
			}
		}		
	}
	protected void handleManifest(FetchRequest respondedFetchRequest, IiifManifest manifest) throws Exception {
		IiifPresentationMetadata md = manifest.getMetadata();

		Resource r = RdfUtil.findResource(manifest.getResource(), RdfReg.IIIF_HAS_SEQUENCES, RdfReg.RDF_FIRST,
				RdfReg.IIIF_HAS_CANVASES, RdfReg.RDF_FIRST,
				RdfReg.IIIF_HAS_IMAGE_ANNOTATIONS, RdfReg.RDF_FIRST,
				RdfReg.OA_HAS_BODY, RdfReg.SVCS_HAS_SERVICE);
		if(r!=null) {
			md.setShownByUrl(r.getURI()+"/full/512,/0/default.jpg");
			md.setShownByService(r.getURI());
		}

		StmtIterator relations = manifest.getRdfModel().listStatements(manifest.getResource(), RdfReg.DCTERMS_RELATION, (RDFNode) null);
		while (relations.hasNext()) {
			Statement s = relations.next();
			if(s.getObject().isResource()) {
				Statement format = ((Resource)s.getObject()).getProperty(RdfReg.DC_FORMAT);
				if(format!=null && format.getObject().toString().equals("text/html"))
					md.setShownAtUrl(format.getSubject().getURI());
			}
		}
		
		if(DOWNLOAD_SEEALSO) {
			StmtIterator seeAlso = manifest.getRdfModel().listStatements(manifest.getResource(), RdfReg.RDFS_SEE_ALSO, (RDFNode) null);
			while (seeAlso.hasNext()) {
				Statement s = seeAlso.next();
				String seeAlsoUrl = s.getObject().toString();
				Resource seeAlsoResource = manifest.getRdfModel().getResource(seeAlsoUrl);
				Statement formatProperty = seeAlsoResource.getProperty(RdfReg.DC_FORMAT);
				if( fetchSeeAlso( seeAlsoResource )) {
						System.out.println("fetching seeAlso:"+s.getObject().toString()+" "+formatProperty);
					try {
	//					System.out.println("Fetching prio: "+seeAlsoUrl);
						FetchRequest seeAlsoFetched;
						if(formatProperty != null)
							seeAlsoFetched = session.fetchWithPriority(new UrlRequest(seeAlsoUrl, (Date) null, formatProperty.getObject().toString()));
						else
							seeAlsoFetched = session.fetchWithPriority(new UrlRequest(seeAlsoUrl));
						try {
			//				System.out.println("Fetching prio: "+seeAlsoUrl+"DONE");
							if (seeAlsoFetched.getResponseStatusCode()==502 || seeAlsoFetched.getResponseStatusCode()==503) {
								//retry
								if(formatProperty != null)
									seeAlsoFetched = session.fetchWithPriority(new UrlRequest(seeAlsoUrl, (Date) null, formatProperty.getObject().toString()));
								else
									seeAlsoFetched = session.fetchWithPriority(new UrlRequest(seeAlsoUrl));
							}
							if (seeAlsoFetched.getResponseStatusCode()==200) {
								Content seeAlsoContent = seeAlsoFetched.getContent();
								ContentType type = seeAlsoContent.getType();
								if (type.getMimeType().equals(ContentType.APPLICATION_XML.getMimeType()) 
									|| type.getMimeType().equals("application/rdf+xml")
									|| type.getMimeType().equals("application/ld+json")
									|| type.getMimeType().equals(ContentType.TEXT_XML.getMimeType())
									|| type.getMimeType().equals(ContentType.APPLICATION_JSON.getMimeType())) {
									
									String profile=null;
									StmtIterator seeAlsoStms = manifest.getRdfModel().listStatements(seeAlsoResource, RdfReg.IIIF_PROFiLE_DOAP_IMPLEMENTS, (RDFNode) null);
									
									while (seeAlsoStms.hasNext()) {
										Statement st = seeAlsoStms.next();
										profile=st.getObject().toString();
									}
									
									RawDataRecord sa = new RawDataRecord();
									sa.setContent(seeAlsoContent.asBytes());
									sa.setProfile(profile);
									sa.setUrl(seeAlsoUrl);
									sa.setContentType(seeAlsoContent.getType().getMimeType());
									sa.setFormat(formatProperty==null? "null" : formatProperty.getObject().toString());
									if(VALIDATE_SEEALSO_CONTENT_SINTAX) {
										if(!sa.validateSintax()) {
											UrlRequest req = new UrlRequest(seeAlsoUrl, (Date) null, sa.getFormat());
											req.setRefresh(true);
											seeAlsoFetched = session.fetchWithPriority(req);
											if (seeAlsoFetched.getResponseStatusCode()==200) {
												seeAlsoContent = seeAlsoFetched.getContent();
												sa.setContent(seeAlsoContent.asBytes());
												sa.setContentType(seeAlsoContent.getType().getMimeType());
												if(!sa.validateSintax()) 
													logManifestProblems.error(respondedFetchRequest.getUrl() + " ; " + "Discarding invalid see also");
												else
													md.addSeeAlso(sa);
											} else
												logManifestProblems.error(respondedFetchRequest.getUrl() + " ; " + "Discarding invalid see also");
										} else
											md.addSeeAlso(sa);
									}else
										md.addSeeAlso(sa);
								} else {
									log.debug("Discarding see also: type not supported:"+type);
								}
							} else {
								logManifestProblems.error("Error fetching seeAlso: "+seeAlsoUrl+" http status"+ seeAlsoFetched.getResponseStatusCode());
							}
						} catch (Exception e) {
							e.printStackTrace(); 
							log.debug(e.getMessage(), e);
							throw e;
						}finally {
							try {
								seeAlsoFetched.getResponse().close();
							} catch (Exception e) {
								log.error(seeAlsoFetched.getUrl(), e);
							}
						}
					} catch (IOException e) {
						log.error("error fetching seeAlso "+seeAlsoUrl, e);
					} catch (InterruptedException e) {
						log.info(e.getMessage(), e);
						break;
					}
				}
			}
		}
		handleMetadata(manifest);
	}
	

//	protected abstract boolean fetchSeeAlso(Resource resource);
	protected boolean fetchSeeAlso(Resource resource) {
		StmtIterator properties = resource.getModel().listStatements(resource, RdfReg.IIIF_PROFiLE_DOAP_IMPLEMENTS, (RDFNode) null);
		while (properties.hasNext()) {
			Statement s = properties.next();
			if(s.getObject().toString().startsWith("http://www.loc.gov/mods/") ||
				s.getObject().toString().equals("http://www.europeana.eu/schemas/edm/") ||
				s.getObject().toString().equals("http://iiif.io/community/profiles/discovery/dc") 
				)
				return true;
//			else 
//				log.debug("Profile not supported: "+s.getObject().toString());
		}
		properties = resource.getModel().listStatements(resource, RdfReg.DC_FORMAT, (RDFNode) null);	
//		{"@id":"https://d.lib.ncsu.edu/collections/catalog/mc00066-001-bx0001-001-001/schemaorg.json","format":"application/ld+json","label":"Schema.org metadata as JSON-LD"}
		while (properties.hasNext()) {
			Statement s = properties.next();
			if(s.getObject().toString().equals("application/ld+json") )
				return true;
//			else 
//				log.debug("Profile not supported: "+s.getObject().toString());
		}
		
		
//		log.debug("No supported seeAlso profile available for: "+resource.getURI());
		return false;
	}


	
	protected abstract void handleMetadata(IiifManifest manifestWithMetadata) throws Exception;

	
}