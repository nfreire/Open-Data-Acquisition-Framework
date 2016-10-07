package inescid.opaf.iiif;

import inescid.opaf.framework.FetchRequest;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;
import inescid.opaf.sitemap.WriterCrawlResourceHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Content;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapIndex;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.UnknownFormatException;
import inescid.opaf.framework.CollectorOfAsyncResponses;
import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.CrawlingSystem;

public class IiifPresentationApiCrawler implements Runnable {

	private static Logger log = LoggerFactory.getLogger(IiifPresentationApiCrawler.class);
	
	CrawlingSystem crawler;
	IiifSource iiifSource;
	ManifestCrawlHandler handler;
	CollectorOfAsyncResponses collector;
	
	Throwable runError=null;
	
	CrawlingSession session;
	
	public IiifPresentationApiCrawler(IiifSource iiifSource, ManifestCrawlHandler handler, CrawlingSystem crawler) {
		super();
		this.iiifSource = iiifSource;
		this.handler = handler;
		this.crawler = crawler;
		this.session= crawler.startSession(3);
		handler.setSession(session);
		collector=session.createCollectorOfAsyncResponses(handler);
	}
	
	public void run() {
		try {
			if(iiifSource.getRobotTxtUrl()!=null)
				session.setRobotsTxtRules(iiifSource.getRobotTxtUrl());
//			session.setWorkers(3);
//			handler.setSession(session);
//			new Thread(handler).start();
			crawlSitemap();
			crawlPresentationApi();
			Thread.sleep(60000);
			session.waitAndClose();
			handler.close();
			log.debug("Run ending: "+this.getClass().getName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			runError=e;
		}
	}
	
	private void crawlPresentationApi() throws InterruptedException, IOException {
		for(String srcUrl: iiifSource.getHarvestingIiifUrls()) {
			session.fetchAsyncLowPriority(srcUrl);
		}
		if(!iiifSource.getSitemapsUrls().isEmpty()) {
//			new Thread(new Runnable() {
//				public void run() {
					File sitemapResourceUrlsFile = getSitemapResourcesUrlsFile();
					BufferedReader reader=new BufferedReader(new FileReader(sitemapResourceUrlsFile));
					for (String line=reader.readLine(); line!=null ; line=reader.readLine()) {
						session.fetchAsyncLowPriority(line);
					}
					reader.close();
//				}
//			}).start();
		}
	}

	private void crawlSitemap() throws IOException, InterruptedException {
		File sitemapResourceUrlsFile = getSitemapResourcesUrlsFile();
		if(sitemapResourceUrlsFile.exists())
			sitemapResourceUrlsFile.delete();
		for(String sitemapUrl : iiifSource.getSitemapsUrls() ) {
			SitemapResourceCrawler smCrawler=new SitemapResourceCrawler(sitemapUrl, null, new WriterCrawlResourceHandler(sitemapResourceUrlsFile,true), crawler );
			smCrawler.run();
		}
	}

	private File getSitemapResourcesUrlsFile() {
		return new File(iiifSource.getName()+"_urls.txt");
	}
	
	
	

}
