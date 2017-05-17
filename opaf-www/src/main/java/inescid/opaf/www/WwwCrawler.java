package inescid.opaf.www;

import inescid.opaf.framework.FetchRequest;
import inescid.opaf.framework.UrlRequest;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;
import inescid.opaf.sitemap.WriterCrawlResourceHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Date;

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

public class WwwCrawler implements Runnable {

	private static Logger log = LoggerFactory.getLogger(WwwCrawler.class);
	
	CrawlingSystem crawler;
	WwwSource wwwSource;
	WwwCrawlHandler handler;
	CollectorOfAsyncResponses collector;
	
	Throwable runError=null;
	
	CrawlingSession session;
	
	public WwwCrawler(WwwSource wwwSource, WwwCrawlHandler handler, CrawlingSystem crawler) {
		super();
		this.wwwSource = wwwSource;
		this.handler = handler;
		this.crawler = crawler;
		this.session= crawler.startSession(3);
		handler.setSession(session);
		collector=session.createCollectorOfAsyncResponses(handler);
	}
	
	public void run() {
		try {
			if(wwwSource.getRobotTxtUrl()!=null)
				session.setRobotsTxtRules(wwwSource.getRobotTxtUrl());
//			session.setWorkers(3);
//			handler.setSession(session);
//			new Thread(handler).start();
			crawlSitemap();
			
			crawlWww(wwwSource.getLastUpdate());
			Thread.sleep(60000);
			session.waitAndClose();
			handler.close();
			log.debug("Run ending: "+this.getClass().getName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			runError=e;
		}
	}
	
	private void crawlWww(Date incrementalCrawlStartDate) throws InterruptedException, IOException {
		for(String srcUrl: wwwSource.getHarvestingIiifUrls()) {
			session.fetchAsyncLowPriority(new UrlRequest(srcUrl, incrementalCrawlStartDate));
		}
		if(!wwwSource.getSitemapsUrls().isEmpty()) {
//			new Thread(new Runnable() {
//				public void run() {
					File sitemapResourceUrlsFile = getSitemapResourcesUrlsFile();
					BufferedReader reader=new BufferedReader(new FileReader(sitemapResourceUrlsFile));
					for (String line=reader.readLine(); line!=null ; line=reader.readLine()) {
						session.fetchAsyncLowPriority(new UrlRequest(line, incrementalCrawlStartDate));
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
		for(String sitemapUrl : wwwSource.getSitemapsUrls() ) {
			SitemapResourceCrawler smCrawler=new SitemapResourceCrawler(sitemapUrl, null, new WriterCrawlResourceHandler(sitemapResourceUrlsFile,true), crawler );
			smCrawler.run(session);
		}
	}

	private File getSitemapResourcesUrlsFile() {
		return new File(wwwSource.getName()+"_urls.txt");
	}
	
}
