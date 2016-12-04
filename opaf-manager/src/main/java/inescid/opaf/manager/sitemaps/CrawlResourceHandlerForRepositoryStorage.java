package inescid.opaf.manager.sitemaps;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Content;
import org.apache.http.entity.ContentType;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.SiteMapURLExtended;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.api.Record;
import inescid.opaf.framework.FetchRequest;
import inescid.opaf.framework.UrlRequest;
import inescid.opaf.iiif.IiifSeeAlsoProperty;
import inescid.opaf.iiif.RdfReg;
import inescid.opaf.sitemap.CrawlResourceHandler;

public class CrawlResourceHandlerForRepositoryStorage extends CrawlResourceHandler{
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CrawlResourceHandlerForRepositoryStorage.class);
	
	Database db;
	
	int recCount=0;
	
	public CrawlResourceHandlerForRepositoryStorage(File repositoryHome) throws Exception {
		db=new Database(repositoryHome, AccessMode.WRITE);
	}
	
	@Override
	public void handleUrl(SiteMapURL url) throws Exception {
		SiteMapURLExtended urlEx=(SiteMapURLExtended) url;
//		if(urlEx.getIiifManifest()!=null) {
//			//TODO
//		} else 
		if(urlEx.getEdmMetadata()!=null) {
			String uriStr = urlEx.getEdmMetadata().toString();

			FetchRequest seeAlsoFetched;
				seeAlsoFetched = session.fetchWithPriority(new UrlRequest(uriStr));
			try {
//				System.out.println("Fetching prio: "+seeAlsoUrl+"DONE");
				if (seeAlsoFetched.getResponseStatusCode()==200) {
					Content seeAlsoContent = seeAlsoFetched.getContent();
					ContentType type = seeAlsoContent.getType();
					if (type.getMimeType().equals(ContentType.APPLICATION_XML.getMimeType()) 
						|| type.getMimeType().equals("application/rdf+xml")
						|| type.getMimeType().equals(ContentType.TEXT_XML.getMimeType())
						|| type.getMimeType().equals(ContentType.APPLICATION_JSON.getMimeType())) {
						
						Record rec=new Record(uriStr); 
						rec.setData(new String(seeAlsoContent.asBytes(), "UTF8"));
						db.add(rec);
						synchronized (db) {
							
						recCount++;
						if(recCount % 1000 == 0) {
							db.commit();
							if(recCount % 10000 == 0) {
								db.optimize();
							}
							}
						}
					} else {
						log.debug("Discarding see also: type not supported:"+type);
					}
				} else {
					log.info("Error fetching seeAlso: "+uriStr+" http status"+ seeAlsoFetched.getResponseStatusCode());						
				}
			}finally {
				try {
					seeAlsoFetched.getResponse().close();
				} catch (Exception e) {
					log.error(seeAlsoFetched.getUrl(), e);
				}
			}
		} else {
			//ignore it
			return;
		}
	}
	
	@Override
	public void close() {
		try {
			db.commit();
			db.optimize();
			db.shutdown();
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
		}
	}
}
