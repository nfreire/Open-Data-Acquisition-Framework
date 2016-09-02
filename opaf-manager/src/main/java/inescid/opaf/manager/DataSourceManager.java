package inescid.opaf.manager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.iiif.CrawlingHandlerForRepositoryStorage;
import inescid.opaf.manager.iiif.DataSourceIiif;

public class DataSourceManager {
	Map<String, DataSource> sources;
	CrawlingSystem crawlingSystem;
	
	public void init(File workingFolder) throws Exception {
		crawlingSystem=new CrawlingSystem(workingFolder);
		sources=new HashMap<>();
	}
	
	public void addSource(DataSource src) {
		sources.put(src.getName(), src);
	}
	
	public Collection<DataSource> getDataSources(){
		return sources.values();
	}
	
	public void syncAll() {
		for(DataSource ds: sources.values() ) {
			ds.synchronizeData(null, crawlingSystem);
		}
	}
	
	public void close() throws Exception {
		crawlingSystem.close();
	}
}
