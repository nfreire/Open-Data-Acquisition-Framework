package inescid.opaf.sitemap;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.sitemaps.SiteMapURL;
import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.FetchRequest;

public abstract class CrawlResourceHandler {

	private static Logger log = LoggerFactory.getLogger(CrawlResourceHandler.class);
		
		protected CrawlingSession session;
		
		public void close() {
		}
		
//		public interface CrawlHandler {
		public abstract void handleUrl(SiteMapURL subSm) throws Exception;

		public void setSession(CrawlingSession session) {
			this.session = session;
		}

	}