package inescid.opaf.manager.sitemaps;

import java.io.File;
import java.util.Properties;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.manager.DataSource;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;

public class DataSourceSitemapsEuropeanaExtension implements DataSource {
	String sitemapUrl;
	CrawlResourceHandler handler;
	String name;
	

	public DataSourceSitemapsEuropeanaExtension() {
	}
	
	public void init(Properties props) throws Exception {
//			IiifSource src, ManifestCrawlHandler handler) {
		this.name=props.getProperty("name");
		
		if(props.containsKey("CrawlResourceHandler.class")) {
			this.handler = (CrawlResourceHandler) Class.forName(
					props.getProperty("CrawlResourceHandler.class").trim()).newInstance();
		}else
			this.handler=new CrawlResourceHandlerForRepositoryStorage(new File(props.getProperty("repository.folder")));
		
		sitemapUrl=props.getProperty("sitemap");
	}

	@Override
	public String getName() {
		return name;
	}
 
	@Override
	public void synchronizeData(CrawlingSystem crawlingSys) {
		SitemapResourceCrawler smCrawler=new SitemapResourceCrawler(sitemapUrl, null, handler, crawlingSys );
		smCrawler.run(null);
	}

	public CrawlResourceHandler getHandler() {
		return handler;
	}

	public void setHandler(CrawlResourceHandler handler) {
		this.handler = handler;
	}

}
