package inescid.opaf.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;
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
				logMonitor.info("Processing: "+processing);
//				logMonitor.info(threadManager.printState());
				logMonitor.info(crawlingSystem.printStatus());
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
						processing.add(url);
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
					processing.remove(url);
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
	BigSet<String> harvested;
	TreeSet<String> processing=new TreeSet<String>(); 
//	HashSet<String> processing=new HashSet<>(500); 
	ArrayList<AsyncRequestsWorker> asyncRequestingWorkers=new ArrayList<>();
	CollectorOfAsyncResponses collectorOfAsyncResponses;
	Monitor monitor;
	
	
//	ThreadManager threadManager=new ThreadManager();
	
	Date lastHttpRequestSent=new Date();
	
	public CrawlingSession(CrawlingSystem crawlingSystem, int numberOfParallelFetchers) {
		asyncRequesting=new BigQueue<>("asyncRequestingQueue",crawlingSystem.getWorkingFolder());
		harvested=new BigSet<>("harvestedSet", asyncRequesting.getDb());
		this.crawlingSystem=crawlingSystem;
		for(int i=0; i<numberOfParallelFetchers; i++) {
			AsyncRequestsWorker worker=new AsyncRequestsWorker();
			asyncRequestingWorkers.add(worker);		
			worker.start();
//			threadManager.addConsumers(worker.getRunningThread());
//			threadManager.addProducers(worker.getRunningThread());
		}
		monitor=new Monitor();
		monitor.start();
//		threadManager.addIndependents(monitor.getRunningThread());
		robotRules=new SimpleRobotRules();
		robotRules.setCrawlDelay(MIN_CRAWL_DELAY);
		
		
		asyncRequesting.clear();
		harvested.clear();
	}
//
//	public void setWorkers(int numberOfParallelFetchers) {
//		// TODO Auto-generated method stub
//		
//	}
//
	public FetchRequest fetch(final String url) throws IOException, InterruptedException {
		if(!harvested.addSynchronized(url))
			return null;
		processing.add(url);
//		threadManager.addProducers(Thread.currentThread());
		try {
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
		} finally {
			processing.remove(url);
//			threadManager.removeProducers(Thread.currentThread());
		}
	}
	

	public FetchRequest fetchWithPriority(final String url, String contentTypeToRequest)  throws IOException, InterruptedException {
		if(!harvested.addSynchronized(url))
			return null;
		processing.add(url);
		try {
			if(robotRules.isAllowAll() || robotRules.isAllowed(url)) {
				lastHttpRequestSent=new Date();
				return crawlingSystem.fetchWithPriority(url, contentTypeToRequest);
			}
			return new FetchRequest(url, this, new IOException("Disallowed by robots.txt"));
		} finally {
			processing.remove(url);
		}
		
	}
	
	public FetchRequest fetchWithPriority(String url)  throws IOException, InterruptedException {
		if(!harvested.addSynchronized(url))
			return null;
		processing.add(url);
		try {
			if(robotRules.isAllowAll() || robotRules.isAllowed(url)) {
				lastHttpRequestSent=new Date();
				return crawlingSystem.fetchWithPriority(url);
			}
			return new FetchRequest(url, this, new IOException("Disallowed by robots.txt"));
		} finally {
			processing.remove(url);
		}
		
	}

	public FetchRequest takeAsyncResult() throws InterruptedException {
//		threadManager.addConsumers(Thread.currentThread());
//		try {
			return asyncReady.take();
//		}finally {
//			threadManager.removeConsumers(Thread.currentThread());			
//		}
	}
	
	public FetchRequest takeAsyncResult(long timeout, TimeUnit timeUnit) throws InterruptedException {
//		threadManager.addConsumers(Thread.currentThread());
//		try {
			return asyncReady.poll(timeout, timeUnit);
//		}finally {
//			threadManager.removeConsumers(Thread.currentThread());			
//		}
	}
	
	public void fetchAsync(final String url) throws InterruptedException {
//		if(!harvested.addSynchronized(url))
//			return;
			asyncRequesting.put(url);
	}
	
	public void waitAndClose() {
		
//		threadManager.waitForFinish(false);

//		for(AsyncRequestsWorker w : asyncRequestingWorkers) {
//		w.close();
//	}

		while(!asyncRequesting.isEmpty() || !asyncReady.isEmpty() || !processing.isEmpty())
			try {
				Thread.sleep(750);
//				if(asyncRequesting.isEmpty() && asyncReady.isEmpty() && processing.size()<=1)
//					processing.remove(null);
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
		try {
			if (fetch.getResponse().getStatusLine().getStatusCode()==200) {
				Content content=fetch.getContent();
				SimpleRobotRulesParser parser=new SimpleRobotRulesParser();
//			robotRules = (SimpleRobotRules) parser.parseContent(content.getType().toString(), content.asBytes(), fetch.getUrl(), "Europeana Crawler v0.1");
				robotRules = (SimpleRobotRules) parser.parseContent(content.getType().toString(), content.asBytes(), fetch.getUrl(), "Europeana Crawler v0.1");
				robotRules.setCrawlDelay(Math.max(MIN_CRAWL_DELAY, robotRules.getCrawlDelay()));
			} else {
				throw new FileNotFoundException(robotsTxtUrl+ " ; HTTP Status:"+fetch.getResponse().getStatusLine().getStatusCode() ); 
			}
		} finally {
			try {
				fetch.getResponse().close();
			} catch (Exception e) {
				log.error(fetch.getUrl(), e);
			}	
		}
	}
	public CollectorOfAsyncResponses createCollectorOfAsyncResponses(ResponseHandler handler) {
		if(collectorOfAsyncResponses!=null)
			throw new IllegalStateException("a collector already exists.");
		collectorOfAsyncResponses = new CollectorOfAsyncResponses(this, handler);
		Thread thread = new Thread(collectorOfAsyncResponses);
		thread.start();
//		threadManager.addIndependents(thread);
		return collectorOfAsyncResponses;
	}
	public CollectorOfAsyncResponses getCollectorOfAsyncResponses() {
		return collectorOfAsyncResponses;
	}

	
}
