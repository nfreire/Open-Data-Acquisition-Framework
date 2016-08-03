package inescid.opaf.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.fluent.Content;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.BaseRobotsParser;
import crawlercommons.robots.SimpleRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import crawlercommons.sitemaps.AbstractSiteMap;
import crawlercommons.sitemaps.SiteMapParser;


public class CrawlingSession {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CrawlingSession.class);
	private static final long MIN_CRAWL_DELAY=300;
	
	
	class AsyncRequestsWorker implements Runnable {
		boolean close=false;
		@Override
		public void run() {
			while(!close) {
				FetchRequest fetch=null;
				String url=null;
				try {
					url=asyncRequesting.poll(30, TimeUnit.SECONDS);
					if(url==null) continue;
				} catch (InterruptedException e) {
					log.warn(url, e);
					return;
				}
				log.warn("Comment for TEST: fetch "+url);
//				try {
//					fetch = fetch(url);
//				} catch (IOException | InterruptedException e) {
//					return;
//				}
					try {
						asyncReady.put(fetch);
					} catch (InterruptedException e) {
						log.warn(url, e);
						return;
//						fetch=new FetchRequest(url, CrawlingSession.this, e);
					}
			}
		}

		public void close() {
			close=true;
		};
		
	}
	
	CrawlingSystem crawlingSystem;
	SimpleRobotRules robotRules;
//	List<FetchRequest> asyncReady=new ArrayList<>(10); 
	BlockingQueue<FetchRequest> asyncReady=new ArrayBlockingQueue<>(10); 
	BlockingQueue<String> asyncRequesting=new ArrayBlockingQueue<>(200); 
	ArrayList<AsyncRequestsWorker> asyncRequestingWorkers=new ArrayList<>();
	
	Date lastHttpRequestSent=new Date();
	
	public CrawlingSession(CrawlingSystem crawlingSystem, int numberOfParallelFetchers) {
		this.crawlingSystem=crawlingSystem;
		for(int i=0; i<numberOfParallelFetchers; i++) {
			AsyncRequestsWorker worker=new AsyncRequestsWorker();
			asyncRequestingWorkers.add(worker);		
		}
		
		robotRules=new SimpleRobotRules();
		robotRules.setCrawlDelay(MIN_CRAWL_DELAY);
	}
//
//	public void setWorkers(int numberOfParallelFetchers) {
//		// TODO Auto-generated method stub
//		
//	}
//
	public FetchRequest fetch(String url) throws IOException, InterruptedException {
		if(robotRules.isAllowAll() || robotRules.isAllowed(url)) {
			synchronized (lastHttpRequestSent) {
				long wait=new Date().getTime() - lastHttpRequestSent.getTime();
				if(wait>0)
					Thread.sleep(wait);
				lastHttpRequestSent=new Date();
			}
			return crawlingSystem.fetch(url);
			
		}
		return new FetchRequest(url, this, new IOException("Disallowed by robots.txt"));
	}

	public FetchRequest takeAsyncResult() throws InterruptedException {
		return asyncReady.take();
	}
	
	public FetchRequest takeAsyncResult(long timeout, TimeUnit timeUnit) throws InterruptedException {
		return asyncReady.poll(timeout, timeUnit);
	}
	
	public void fetchAsync(final String url) throws InterruptedException {
			asyncRequesting.put(url);
	}
	
	public void waitAndClose() {
		while(!asyncRequesting.isEmpty())
			try {
				Thread.sleep(750);
			} catch (InterruptedException e) {
				return;
			}
		for(AsyncRequestsWorker w : asyncRequestingWorkers) {
			w.close();
		}
		
	}
	
	public void setRobotsTxtRules(String robotsTxtUrl) throws IOException, InterruptedException {
		FetchRequest fetch = fetch(robotsTxtUrl);
		if (fetch.getResponse().getStatusLine().getStatusCode()==200) {
			Content content=fetch.getContent();
			SimpleRobotRulesParser parser=new SimpleRobotRulesParser();
//			robotRules = (SimpleRobotRules) parser.parseContent(content.getType().toString(), content.asBytes(), fetch.getUrl(), "Europeana Crawler v0.1");
			robotRules = (SimpleRobotRules) parser.parseContent(content.getType().toString(), content.asBytes(), fetch.getUrl(), "Europeana Crawler v0.1");
			robotRules.setCrawlDelay(Math.max(MIN_CRAWL_DELAY, robotRules.getCrawlDelay()));
		} else {
			throw new FileNotFoundException(robotsTxtUrl+ " ; HTTP Status:"+fetch.getResponse().getStatusLine().getStatusCode() ); 
		}
	}

	
}
