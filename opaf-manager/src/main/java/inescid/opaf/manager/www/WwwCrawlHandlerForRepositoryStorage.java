package inescid.opaf.manager.www;

import java.io.File;
import java.io.StringWriter;

import org.apache.http.client.fluent.Content;
import org.apache.http.entity.ContentType;
import org.apache.jena.rdf.model.Model;

import crawlercommons.sitemaps.SiteMapURL;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.api.Record;
import inescid.opaf.framework.FetchRequest;
import inescid.opaf.framework.UrlRequest;
import inescid.opaf.www.WwwCrawlHandler;
import inescid.util.DevelopementSingleton;

public class WwwCrawlHandlerForRepositoryStorage extends WwwCrawlHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WwwCrawlHandlerForRepositoryStorage.class);
	
	Database db;
	
	int recCount=0;
	
	public WwwCrawlHandlerForRepositoryStorage(File repositoryHome) throws Exception {
		db=new Database(repositoryHome, AccessMode.WRITE);
	}
	
	@Override
	public void handleUrl(Model model, String url) throws Exception {
		if(model!=null && model.size()>0) {
//			if(DevelopementSingleton.DEVEL_TEST) {
//				DevelopementSingleton.RESOURCE_HARVEST_CNT++;
//				if(DevelopementSingleton.stopHarvest()) return;
//			}

			StringWriter jsonWrt=new StringWriter();
			model.write(jsonWrt, "JSON-LD");
			
			Record rec=new Record(url); 
			rec.setData(jsonWrt.toString());
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
