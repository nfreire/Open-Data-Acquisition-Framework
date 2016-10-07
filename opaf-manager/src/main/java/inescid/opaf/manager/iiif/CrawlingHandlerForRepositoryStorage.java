package inescid.opaf.manager.iiif;

import java.io.File;

import org.apache.http.client.fluent.Content;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.api.Record;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.IiifSeeAlsoProperty;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.iiif.RdfReg;

public class CrawlingHandlerForRepositoryStorage extends ManifestCrawlHandler {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CrawlingHandlerForRepositoryStorage.class);
	Database db;
	
	int recCount=0;
	
	public CrawlingHandlerForRepositoryStorage(File repositoryHome) throws Exception {
		db=new Database(repositoryHome, AccessMode.WRITE);
	}
	

	@Override
	protected void handleMetadata(IiifPresentationMetadata metadata) throws Exception {
		Record rec=new Record(metadata.getManifestUrl()); 
		rec.setData(metadata);
		for(IiifSeeAlsoProperty sa: metadata.getSeeAlso()) {
			rec.addField(0, sa.getSeeAlsoUrl());
		}
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
