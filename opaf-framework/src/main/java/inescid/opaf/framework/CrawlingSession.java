package inescid.opaf.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
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
	private static org.slf4j.Logger logMonitor = org.slf4j.LoggerFactory.getLogger(Monitor.class);
	private static final long MIN_CRAWL_DELAY=300;
	
	

	class Monitor implements Runnable {
		Thread runningIn;
		boolean close=false;
		@Override
		public void run() {
			while(!close) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					close=true;
				}
				logMonitor.info("Ready: "+ asyncReady.size());
				logMonitor.info("Requesting: "+!asyncRequesting.isEmpty());
				logMonitor.info("Processing: "+asyncProcessing);
			}
			log.debug("Exiting "+getClass().getSimpleName());
		}
		
		public void close() {
			close=true;
		};
		
		public void start() {
			runningIn=new Thread(this);
			runningIn.start();
		}

		public Thread getRunningThread() {
			return runningIn;
		}
		

	}

	class AsyncRequestsWorker implements Runnable {
		Thread runningIn;
		
		boolean close=false;
		@Override
		public void run() {
			boolean abort=false;
			while(!abort && (!close || !asyncRequesting.isEmpty())) {
				FetchRequest fetch=null;
				String url=null;
				try {
					url=asyncRequesting.poll(30, TimeUnit.SECONDS);
					if(url==null) continue;
				} catch (InterruptedException e) {
					log.warn(url, e);
					abort=true;
				}
//				log.warn("Comment for TEST: fetch "+url);
				try {
					try {
						asyncProcessing.add(url);
						fetch = fetch(url);
					} catch (IOException e) {
						log.warn(url, e);
					} catch (InterruptedException e) {
						log.warn(url, e);
						abort=true;
					}
					if(fetch!=null) try {
						asyncReady.put(fetch);
					} catch (InterruptedException e) {
						log.warn(url, e);
						abort=true;
	//						fetch=new FetchRequest(url, CrawlingSession.this, e);
					}
				}finally {
					asyncProcessing.remove(url);
				}
			}
			log.debug("Exiting "+getClass().getSimpleName());
		}

		public void close() {
			close=true;
		};
		
		public void start() {
			runningIn=new Thread(this);
			runningIn.start();
		}

		public Thread getRunningThread() {
			return runningIn;
		}
		
	}
	
	CrawlingSystem crawlingSystem;
	SimpleRobotRules robotRules;
//	List<FetchRequest> asyncReady=new ArrayList<>(10); 
	BlockingQueue<FetchRequest> asyncReady=new ArrayBlockingQueue<>(200); 
	BigQueue<String> asyncRequesting; 
//	BlockingQueue<String> asyncRequesting=new ArrayBlockingQueue<>(500); 
	HashSet<String> asyncProcessing=new HashSet<>(500); 
	ArrayList<AsyncRequestsWorker> asyncRequestingWorkers=new ArrayList<>();
	CollectorOfAsyncResponses collectorOfAsyncResponses;
	Monitor monitor;
	
	Date lastHttpRequestSent=new Date();
	
	public CrawlingSession(CrawlingSystem crawlingSystem, int numberOfParallelFetchers) {
		asyncRequesting=new BigQueue<>("asyncRequestingQueue",crawlingSystem.getWorkingFolder());
		this.crawlingSystem=crawlingSystem;
		for(int i=0; i<numberOfParallelFetchers; i++) {
			AsyncRequestsWorker worker=new AsyncRequestsWorker();
			asyncRequestingWorkers.add(worker);		
			worker.start();
		}
		monitor=new Monitor();
		monitor.start();
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
	

	public FetchRequest fetchWithPriority(String url)  throws IOException, InterruptedException {
		if(robotRules.isAllowAll() || robotRules.isAllowed(url)) {
			lastHttpRequestSent=new Date();
			return crawlingSystem.fetchWithPriority(url);
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
		while(!asyncRequesting.isEmpty() || !asyncReady.isEmpty() || !asyncProcessing.isEmpty())
			try {
				Thread.sleep(750);
			} catch (InterruptedException e) {
				return;
			}

		log.debug("Preparing exit "+getClass().getSimpleName());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			return;
		}
		
		if (collectorOfAsyncResponses!=null) {
			collectorOfAsyncResponses.close();

			while(collectorOfAsyncResponses.getRunning().isAlive()) try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
		}
		
		for(AsyncRequestsWorker w : asyncRequestingWorkers) {
			w.close();
		}
		
		boolean allClosed=false;
		while(!allClosed) {
			allClosed=true;
			for(AsyncRequestsWorker w : asyncRequestingWorkers) {
				if(w.getRunningThread().isAlive()) {
					allClosed=false;
					break;
				}
			}
			
//			allClosed=allClosed && !collectorOfAsyncResponses.getRunning().isAlive();
			if(!allClosed)		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return;
			}
			monitor.close();
		}
		log.debug("Exiting "+getClass().getSimpleName());
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
	public CollectorOfAsyncResponses createCollectorOfAsyncResponses(ResponseHandler handler) {
		if(collectorOfAsyncResponses!=null)
			throw new IllegalStateException("a collector already exists.");
		collectorOfAsyncResponses = new CollectorOfAsyncResponses(this, handler);
		new Thread(collectorOfAsyncResponses).start();
		return collectorOfAsyncResponses;
	}
	public CollectorOfAsyncResponses getCollectorOfAsyncResponses() {
		return collectorOfAsyncResponses;
	}

	
}
