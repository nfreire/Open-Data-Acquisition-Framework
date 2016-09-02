package inescid.opaf.framework;

import java.io.File;
import java.io.IOException;

import org.mapdb.DB;

public class CrawlingSystem {
	DB mapDb;
	HttpFetcher fetcher=new HttpFetcher();
	File workingFolder;
	
//	public CrawlingSystem() {
		public CrawlingSystem(File workingFolder) {
//		mapDb=new 
		this.workingFolder=workingFolder;
		
		fetcher.init();
	}
	public CrawlingSession startSession(int numberOfParallelFetchers) {
		return new CrawlingSession(this, numberOfParallelFetchers);
	}

	public FetchRequest fetch(String url) throws IOException, InterruptedException {
		FetchRequest req=new FetchRequest(url);
		fetcher.fetch(req);
		return req;
	}
	public FetchRequest fetchWithPriority(String url) throws IOException, InterruptedException {
		FetchRequest req=new FetchRequest(url);
		fetcher.fetchWithPriority(req);
		return req;
	}
	public void close() throws Exception {
		fetcher.close();
	}
	public File getWorkingFolder() {
		return workingFolder;
	}
	
	
	

}
