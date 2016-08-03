package inescid.opaf.sitemap;

import inescid.opaf.framework.CrawlingSystem;

public class TestCrawl {
	
	public static void main(String[] args) {
		try {
			String sitemapUrl="http://diglit.ub.uni-heidelberg.de/sitemap.xml";
			
			
			CrawlingSystem crawlingSys=new CrawlingSystem();
			SitemapResourceCrawler crawler=new SitemapResourceCrawler(sitemapUrl, new CrawlHandler() {
				
				@Override
				public void handleUrl(String url) throws Exception {
					System.out.println(url);
					
				}
			}, crawlingSys );
			crawler.run();
			crawlingSys.close();
			System.out.println("All done. exiting.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
