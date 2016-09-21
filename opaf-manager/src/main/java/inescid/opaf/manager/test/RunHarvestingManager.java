package inescid.opaf.manager.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.DataSource;
import inescid.opaf.manager.DataSourceManager;
import inescid.opaf.manager.iiif.CrawlingHandlerForRepositoryStorage;
import inescid.opaf.manager.iiif.DataSourceIiif;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;

public class RunHarvestingManager {
	
	public static void main(String[] args) {
		try {
			FileInputStream propsIs=new FileInputStream(args[0]);
			Properties prop=new Properties();
			prop.load(propsIs);
			runManager(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void runManager(Properties prop) {
		try {
			DataSourceManager manager=new DataSourceManager();
			manager.init(prop);
			manager.syncAll();
			manager.close();
			System.out.println("All done. exiting.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
