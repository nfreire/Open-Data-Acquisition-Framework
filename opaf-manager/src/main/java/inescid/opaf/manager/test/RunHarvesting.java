package inescid.opaf.manager.test;

import java.io.File;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.DataSource;
import inescid.opaf.manager.DataSourceManager;
import inescid.opaf.manager.iiif.CrawlingHandlerForRepositoryStorage;
import inescid.opaf.manager.iiif.DataSourceIiif;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;

public class RunHarvesting {

	public static void main(String[] args) {
		try {
			File workingFolder=new File("target/crawler_workdir");
			DataSourceManager manager=new DataSourceManager();
			manager.init(workingFolder);
			
			ManifestCrawlHandler manifHandler=null;
//			manifHandler=new CrawlingHandlerForRepositoryStorage(new File("target/iiif-crawl-repository-nlw"));
//			IiifSource nlwSrc = new IiifSource("Nat. Lib. of Wales - Newspapers",
//					"http://dams.llgc.org.uk/iiif/newspapers/3100020.json");
//			DataSourceIiif dsIiifNlwPotterNewspaper=new DataSourceIiif(nlwSrc, manifHandler);
//			manager.addSource(dsIiifNlwPotterNewspaper);
			
			manifHandler=new CrawlingHandlerForRepositoryStorage(new File("target/iiif-crawl-repository-ucd"));
			IiifSource ucdSrc = new IiifSource("University College Dublin");
			ucdSrc.getSitemapsUrls().add("https://data.ucd.ie/sitemap_index_europeana.xml");
			DataSourceIiif dsIiifUcd=new DataSourceIiif(ucdSrc, manifHandler);
			manager.addSource(dsIiifUcd);
			
			manager.syncAll();
			manager.close();
			System.out.println("All done. exiting.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		setup cawler, repository, datasource tpe, db_of_state
	}
}
