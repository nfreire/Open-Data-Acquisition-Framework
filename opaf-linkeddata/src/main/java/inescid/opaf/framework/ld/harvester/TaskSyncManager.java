package inescid.opaf.framework.ld.harvester;

import java.util.concurrent.Semaphore;

public class TaskSyncManager {
	Semaphore httpFetchSemaphore=new Semaphore(5);
	
	public void acquireHttpFetch() throws InterruptedException {
		httpFetchSemaphore.acquire();
	}
	public void releaseHttpFetch() throws InterruptedException {
		httpFetchSemaphore.release();
	}
}
