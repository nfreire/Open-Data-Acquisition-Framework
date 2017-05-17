package inescid.opaf.manager.www;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Properties;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifPresentationApiCrawler;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.DataSource;
import inescid.opaf.manager.iiif.CrawlingHandlerForRepositoryStorage;
import inescid.opaf.www.WwwCrawlHandler;
import inescid.opaf.www.WwwCrawler;
import inescid.opaf.www.WwwSource;

public class DataSourceWww implements DataSource {
	WwwSource src;
	WwwCrawlHandler handler;

	public DataSourceWww() {
	}
	
	public void init(Properties props) throws Exception {
//			IiifSource src, ManifestCrawlHandler handler) {
		this.src=new WwwSource(props.getProperty("name"));
		
		
		if(props.containsKey("WwwCrawlHandler.class")) {
			this.handler = (WwwCrawlHandler) Class.forName(
					props.getProperty("WwwCrawlHandler.class").trim()).newInstance();
		}else
			this.handler=new WwwCrawlHandlerForRepositoryStorage(new File(props.getProperty("repository.folder")));
		
		
		if(props.containsKey("sitemap"))
			src.getSitemapsUrls().add(props.getProperty("sitemap"));

		if(props.containsKey("lastSynchronization"))
			src.setLastUpdate(new SimpleDateFormat("yyyy-MM-dd").parse(props.getProperty("lastSynchronization")));
	}

	@Override
	public String getName() {
		return src.getName();
	}

	@Override
	public void synchronizeData(CrawlingSystem crawlingSys) {
		WwwCrawler crawler=new WwwCrawler(src, handler, crawlingSys);
		crawler.run();
	}

	public WwwCrawlHandler getHandler() {
		return handler;
	}

	public void setHandler(WwwCrawlHandler handler) {
		this.handler = handler;
	}
	
}
