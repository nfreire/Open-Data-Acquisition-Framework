package inescid.opaf.sitemap;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.FetchRequest;

public abstract class CrawlResourceHandler {

	private static Logger log = LoggerFactory.getLogger(CrawlResourceHandler.class);
		
		private CrawlingSession session;
		
		public void close() {
		}
		
//		public interface CrawlHandler {
		public abstract void handleUrl(String url) throws Exception;

		public void setSession(CrawlingSession session) {
			this.session = session;
		} 
	}