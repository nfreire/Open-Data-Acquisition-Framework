package inescid.opaf.sitemap;

import inescid.opaf.framework.FetchRequest;
import inescid.opaf.framework.UrlRequest;
import inescid.util.DevelopementSingleton;

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
import crawlercommons.sitemaps.SiteMapParserWithExtentions;
import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.UnknownFormatException;
import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.CrawlingSystem;

public class SitemapResourceCrawler {

	private static Logger log = LoggerFactory.getLogger(SitemapResourceCrawler.class);
	
	CrawlingSystem crawler;
	String sitemapUrl;
	String robotsTxtUrl;
	CrawlResourceHandler handler;
	
	Throwable runError=null;
	
	CrawlingSession session;
	
	public SitemapResourceCrawler(String sitemapUrl, String robotsTxtUrl, CrawlResourceHandler handler, CrawlingSystem crawler) {
		super();
		this.sitemapUrl = sitemapUrl;
		this.handler = handler;
		this.crawler = crawler;
		this.robotsTxtUrl = robotsTxtUrl;
	}
	
	public void run(CrawlingSession reuseSession) {
		try {
			if(reuseSession!=null)
				session=reuseSession;
			else
				session = crawler.startSession(3);
			if(robotsTxtUrl!=null)
				session.setRobotsTxtRules(robotsTxtUrl);
			handler.setSession(session);
//			session.setWorkers(3);
//			handler.setSession(session);
//			new Thread(handler).start();
			fetchSitemap(sitemapUrl);
			
			if(reuseSession==null)
				session.waitAndClose();
			handler.close();
			log.debug("Run ending: "+this.getClass().getName());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			runError=e;
		}
	}
	
	private void fetchSitemap(String sitemapUrl) throws IOException, InterruptedException {
		log.debug(sitemapUrl);
		FetchRequest req = session.fetch(new UrlRequest(sitemapUrl));
		try {
			if(log.isDebugEnabled())
				log.debug(sitemapUrl+" - Response code: "+req.getResponse().getStatusLine().getStatusCode());
			log.debug(sitemapUrl);
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
					for(AbstractSiteMap subSm : smIdx.getSitemaps()) {
						if(DevelopementSingleton.DEVEL_TEST) {	
							if(DevelopementSingleton.RESOURCE_HARVEST_CNT > 5) break;
						}

						fetchSitemap(subSm.getUrl().toString());
					}
				} else {
					SiteMap smIdx=(SiteMap) siteMap;
					for(SiteMapURL subSm : smIdx.getSiteMapUrls()) {
						if(DevelopementSingleton.DEVEL_TEST) {	
							DevelopementSingleton.RESOURCE_HARVEST_CNT++;
							if(DevelopementSingleton.stopHarvest()) break;
						}
						try {
							handler.handleUrl(subSm);
						} catch (Exception e) {
							log.error(e.getMessage(), e);
						}
					}
				}
			}
		} finally {
			try {
				req.getResponse().close();
			} catch (Exception e) { 
				log.error(req.getUrl(), e);
			}
		}	
		
	}

	private static AbstractSiteMap parseSiteMap(FetchRequest req) throws IOException, UnknownFormatException {
		Content content=req.getContent();
		SiteMapParserWithExtentions parser=new SiteMapParserWithExtentions(false);
		AbstractSiteMap siteMap = parser.parseSiteMap(content.getType().toString(), content.asBytes(), new URL(req.getUrl()));
		return siteMap;
	}
	
}
