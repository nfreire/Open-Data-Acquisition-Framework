package inescid.opaf.manager.test;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.manager.DataSource;
import inescid.opaf.manager.DataSourceManager;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;

public class RunHarvesting {

	public static void main(String[] args) {
		try {
			DataSourceManager manager=new DataSourceManager();
			manager.init();
			manager.syncAll();
			manager.close();
			System.out.println("All done. exiting.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		setup cawler, repository, datasource tpe, db_of_state
	}
}
