package inescid.opaf.sitemap;

import java.io.File;

import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.SiteMapURLExtended;
import inescid.opaf.framework.CrawlingSystem;

public class TestCrawlExtendedSitemap {
	
	public static void main(String[] args) {
		try {
			String sitemapUrl="http://dams.llgc.org.uk/iiif/newspapers/sitemap.xml";
			
			
			CrawlingSystem crawlingSys=new CrawlingSystem(new File("target/crawler_workdir"));
			SitemapResourceCrawler crawler=new SitemapResourceCrawler(sitemapUrl, null, new CrawlResourceHandler() {
				@Override
				public void handleUrl(SiteMapURL subSm) throws Exception {
					System.out.print(subSm.getUrl());	
					if(subSm instanceof SiteMapURLExtended)
						System.out.println(((SiteMapURLExtended) subSm).getIiifManifest());	
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
