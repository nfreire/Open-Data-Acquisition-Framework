package inescid.opaf.framework;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class CollectorOfAsyncResponses implements Runnable {
	private static final int MAX_HANDLINGS = 10;

	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CollectorOfAsyncResponses.class);
	
	private Thread running;
	private boolean close;
	private CrawlingSession session;
	private ResponseHandler handler;
	Throwable runError=null;
	private final Set<FetchRequest> handling=Collections.synchronizedSet(new HashSet<>()); 
	
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
					while(handling.size()>MAX_HANDLINGS) {
						Thread.sleep(100);
					}
					handling.add(fetchFinal);
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								handler.handle(fetchFinal);
							} catch (Exception e) {
								log.error(fetchFinal.getUrl(), e);
							}finally{
								handling.remove(fetchFinal);
								try {
									fetchFinal.getResponse().close();
								} catch (Exception e) {
									log.error(fetchFinal.getUrl(), e);
								}									
							}
						}
					}).start();
				}
			}
			//wait for handlers to finish
			while(!handling.isEmpty()) {
				Thread.sleep(500);
				log.debug("Waiting to exit. Responses pending: "+handling.size());
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
