package inescid.opaf.manager.iiif;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Properties;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifPresentationApiCrawler;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.DataSource;

public class DataSourceIiif implements DataSource {
	IiifSource src;
	ManifestCrawlHandler handler;

	public DataSourceIiif() {
	}
	
	public void init(Properties props) throws Exception {
//			IiifSource src, ManifestCrawlHandler handler) {
		this.src=new IiifSource(props.getProperty("name"));
		
		if(props.containsKey("ManifestCrawlHandler.class")) {
//			if(props.containsKey("opaf.datasource.iiif.properties.ManifestCrawlHandler.class")) {
			this.handler = (ManifestCrawlHandler) Class.forName(
					props.getProperty("ManifestCrawlHandler.class").trim()).newInstance();
		}else
			this.handler=new CrawlingHandlerForRepositoryStorage(new File(props.getProperty("repository.folder")));
		if(props.containsKey("sitemap"))
			src.getSitemapsUrls().add(props.getProperty("sitemap"));

		if(props.containsKey("collection"))
			src.getHarvestingIiifUrls().add(props.getProperty("collection"));

		if(props.containsKey("lastSynchronization"))
			src.setLastUpdate(new SimpleDateFormat("yyyy-MM-dd").parse(props.getProperty("lastSynchronization")));
	}

	@Override
	public String getName() {
		return src.getName();
	}

	@Override
	public void synchronizeData(CrawlingSystem crawlingSys) {
		IiifPresentationApiCrawler crawler=new IiifPresentationApiCrawler(src, handler, crawlingSys);
		crawler.run();
	}

	public ManifestCrawlHandler getHandler() {
		return handler;
	}

	public void setHandler(ManifestCrawlHandler handler) {
		this.handler = handler;
	}
	
}
