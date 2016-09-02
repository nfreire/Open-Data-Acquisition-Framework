package inescid.opaf.manager.iiif;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifPresentationApiCrawler;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;

public class DataSourceIiifWithSitemaps extends DataSourceIiif {
	String sitemapUrl;
	String robotsTxtUrl;
	
	public DataSourceIiifWithSitemaps(IiifSource src, String sitemapUrl, String robotsTxtUrl, ManifestCrawlHandler handler) {
		super(src, handler);
		this.sitemapUrl = sitemapUrl;
		this.robotsTxtUrl = robotsTxtUrl;
	}

	
	@Override
	public void synchronizeData(Date lastUpdate, CrawlingSystem crawlingSys) {
//		final File urlsFromSitemapFile=File.createTempFile("sitemapsUrl", ".urls");
//		final BufferedWriter writer=new BufferedWriter(new FileWriter(urlsFromSitemapFile));
//		SitemapResourceCrawler crawler=new SitemapResourceCrawler(sitemapUrl, robotsTxtUrl, new CrawlResourceHandler() {
//			@Override
//			public void handleUrl(String url) throws Exception {
//				writer.write(url);
//				writer.write('\n');
//			}
//		}, crawlingSys );
//		crawler.run();
//		writer.flush();
//		writer.close();
//		
//		src.setSitemapsUrls(null);
//		IiifPresentationApiCrawler crawlerIiif=new IiifPresentationApiCrawler(src, handler, crawlingSys);
////		crawlerIiif.
//		crawlerIiif.run();

	}
}
