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
	
	public void init() throws Exception {
		crawlingSystem=new CrawlingSystem();
		sources=new HashMap<>();
		
		ManifestCrawlHandler manifHandler=null;
		manifHandler=new CrawlingHandlerForRepositoryStorage(new File("target/iiif-crawl-repository"));
//		manifHandler=new ManifestCrawlHandler() {
//			@Override
//			protected void handleMetadata(IiifPresentationMetadata metadata) {
//				System.out.println(metadata);
//			}
//		};
		
		
		DataSourceIiif dsIiifNlwPotterNewspaper=new DataSourceIiif(new IiifSource("Nat. Lib. of Wales - Newspapers",
				"http://dams.llgc.org.uk/iiif/newspapers/3100020.json"), manifHandler);
				
		sources.put(dsIiifNlwPotterNewspaper.getName(), dsIiifNlwPotterNewspaper);
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
