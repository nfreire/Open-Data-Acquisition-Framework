package inescid.opaf.manager.iiif;

import java.io.File;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.api.Record;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.ManifestCrawlHandler;

public class CrawlingHandlerForRepositoryStorage extends ManifestCrawlHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CrawlingHandlerForRepositoryStorage.class);
	Database db;
	
	int recCount=0;
	
	public CrawlingHandlerForRepositoryStorage(File repositoryHome) throws Exception {
		db=new Database(repositoryHome, AccessMode.WRITE);
	}
	

	@Override
	protected void handleMetadata(IiifManifest manifestWithMetada) throws Exception {
		Record rec=new Record(manifestWithMetada.getUri()); 
		rec.setData(manifestWithMetada);
//		for(IiifSeeAlsoProperty sa: metadata.getSeeAlso()) {
//			rec.addField(0, sa.getSeeAlsoUrl());
//		}
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
