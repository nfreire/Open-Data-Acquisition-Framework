package inescid.opaf.sitemap;

import java.io.File;

import crawlercommons.sitemaps.SiteMapURL;
import inescid.opaf.framework.CrawlingSystem;

public class TestCrawl {
	
	public static void main(String[] args) {
		try {
			String sitemapUrl="http://diglit.ub.uni-heidelberg.de/sitemap.xml";
			
			
			CrawlingSystem crawlingSys=new CrawlingSystem(new File("target/crawler_workdir"));
			SitemapResourceCrawler crawler=new SitemapResourceCrawler(sitemapUrl, "http://digi.ub.uni-heidelberg.de/robots.txt", new CrawlResourceHandler() {
				
				@Override
				public void handleUrl(SiteMapURL url) throws Exception {
					System.out.println(url);
					
				}
			}, crawlingSys );
			crawler.run(null);
			crawlingSys.close();
			System.out.println("All done. exiting.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
