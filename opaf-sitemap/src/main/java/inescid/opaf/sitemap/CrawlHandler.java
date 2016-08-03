package inescid.opaf.sitemap;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.FetchRequest;

public abstract class CrawlHandler implements Runnable {

	private static Logger log = LoggerFactory.getLogger(CrawlHandler.class);
		
		private Thread running;
		private boolean close;
		private CrawlingSession session;
		
		public void close() {
			this.close=true;
		}
		
		@Override
		public void run() {
			running=Thread.currentThread();
			while(!close) {
				String url=null;
				try {
					FetchRequest fetch = session.takeAsyncResult(30, TimeUnit.SECONDS);
					if(fetch!=null)
						url = fetch.getUrl();
				} catch (InterruptedException e) {
					close=true;
				}
				if(url!=null)
					try {
						handleUrl(url);
					} catch (Exception e) {
						log.error(url, e);
					}
			}
		}
		
//		public interface CrawlHandler {
		public abstract void handleUrl(String url) throws Exception;

		public void setSession(CrawlingSession session) {
			this.session = session;
		} 
	}