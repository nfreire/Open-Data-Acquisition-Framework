package inescid.opaf.framework;

import java.util.concurrent.TimeUnit;

public class CollectorOfAsyncResponses implements Runnable {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CollectorOfAsyncResponses.class);
	
	private Thread running;
	private boolean close;
	private CrawlingSession session;
	private ResponseHandler handler;
	Throwable runError=null;
	
	protected CollectorOfAsyncResponses(CrawlingSession session, ResponseHandler handler) {
		super();
		this.session = session;
		this.handler = handler;
	}

	protected void close() {
		this.close=true;
	}
	
	@Override
	public void run() {
		try {
			running=Thread.currentThread();
			while(!close) {
				FetchRequest fetch=null;
				try {
					fetch = session.takeAsyncResult(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					close=true;
				}
				if(fetch!=null) {
					final FetchRequest fetchFinal=fetch;					
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								handler.handle(fetchFinal);
							} catch (Exception e) {
								log.error(fetchFinal.getUrl(), e);
							}
							
						}
					}).start();
				}
			}
		} catch (Throwable e) {
			runError=e;
			log.error("Error in Response Collector. Exiting collector", e);
		}
		log.debug("Exiting "+getClass().getSimpleName());
	}

	public Thread getRunning() {
		return running;
	}

	public Throwable getRunError() {
		return runError;
	}
	
	
}
