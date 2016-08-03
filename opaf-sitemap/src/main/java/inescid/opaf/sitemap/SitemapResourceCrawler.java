package inescid.opaf.sitemap;

import inescid.opaf.framework.FetchRequest;

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
import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.CrawlingSystem;

public class SitemapResourceCrawler implements Runnable {

	private static Logger log = LoggerFactory.getLogger(SitemapResourceCrawler.class);
	
	CrawlingSystem crawler;
	String sitemapUrl;
	CrawlHandler handler;
	
	Throwable runError=null;
	
	CrawlingSession session;
	
	public SitemapResourceCrawler(String sitemapUrl, CrawlHandler handler, CrawlingSystem crawler) {
		super();
		this.sitemapUrl = sitemapUrl;
		this.handler = handler;
		this.crawler = crawler;
	}
	
	public void run() {
		try {
			session = crawler.startSession(3);
			session.setRobotsTxtRules("http://digi.ub.uni-heidelberg.de/robots.txt");
//			session.setWorkers(3);
//			handler.setSession(session);
//			new Thread(handler).start();
			fetchSitemap(sitemapUrl);
			session.waitAndClose();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			runError=e;
		}
	}
	
	private void fetchSitemap(String sitemapUrl) throws IOException, InterruptedException {
		FetchRequest req = session.fetch(sitemapUrl);
		if(req.getResponse().getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
			AbstractSiteMap siteMap;
			try {
				siteMap = parseSiteMap(req);
			} catch (UnknownFormatException e) {
				log.warn(sitemapUrl, e);
				return;
			}
			if (siteMap.isIndex()) {
				SiteMapIndex smIdx=(SiteMapIndex) siteMap;
				int subSmCnt=0;//TODO: remove this variable. used for testing only
				for(AbstractSiteMap subSm : smIdx.getSitemaps()) {
					subSmCnt++;
					if(subSmCnt>5)break;
					fetchSitemap(subSm.getUrl().toString());
				}
			} else {
				SiteMap smIdx=(SiteMap) siteMap;
				int subSmCnt=0;//TODO: remove this variable. used for testing only
				for(SiteMapURL subSm : smIdx.getSiteMapUrls()) {
					subSmCnt++;
					if(subSmCnt>2)break;
//					session.fetchAsync(subSm.getUrl().toString());	
					
					try {
						handler.handleUrl(subSm.getUrl().toString());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		}
		
		
	}

	private static AbstractSiteMap parseSiteMap(FetchRequest req) throws IOException, UnknownFormatException {
		Content content=req.getContent();
		SiteMapParser parser=new SiteMapParser();
		AbstractSiteMap siteMap = parser.parseSiteMap(content.getType().toString(), content.asBytes(), new URL(req.getUrl()));
		return siteMap;
	}
	
}
