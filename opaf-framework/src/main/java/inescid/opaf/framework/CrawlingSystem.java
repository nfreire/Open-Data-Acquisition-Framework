package inescid.opaf.framework;

import java.io.IOException;

public class CrawlingSystem {
	HttpFetcher fetcher=new HttpFetcher();
	
	public CrawlingSystem() {
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
	public void close() throws Exception {
		fetcher.close();
	}
	
	
	

}
