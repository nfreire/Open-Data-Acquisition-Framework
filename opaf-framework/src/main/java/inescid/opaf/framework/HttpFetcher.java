package inescid.opaf.framework;

import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public class HttpFetcher {
	CloseableHttpClient httpClient;
	DomainsManager domainsManager;
//	ArrayList<FetchRequest> requestsQueue=new ArrayList<>(50);
	Semaphore fetchSemaphore=new Semaphore(5);
	Semaphore fetchWithPrioritySemaphore=new Semaphore(10);
	
	public void init() {
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(41);

		httpClient = 
				HttpClients.custom()
				.setSSLHostnameVerifier(new NoopHostnameVerifier())
				.setConnectionManager(cm) 
				.build();
	}	
	
	public void close() throws Exception {
		httpClient.close();
	}
	
	public void start() throws Exception {
		
	}
	public void stop() throws Exception {
		
	}

	public void fetch(FetchRequest url) throws InterruptedException, IOException {
		fetchSemaphore.acquire();
		try {
			HttpGet request = new HttpGet(url.getUrl());
			CloseableHttpResponse response = httpClient.execute(request);
			url.setResponse(response);
		}finally {
			fetchSemaphore.release();
		}
	}
	public void fetchWithPriority(FetchRequest url) throws InterruptedException, IOException {
//		fetchWithPrioritySemaphore.acquire();
//		try {
			HttpGet request = new HttpGet(url.getUrl());
			if (url.getContentTypeToRequest()!=null)
				request.addHeader(HttpHeaders.ACCEPT, url.getContentTypeToRequest());
			CloseableHttpResponse response = httpClient.execute(request);
			url.setResponse(response);
//		}finally {
//			fetchWithPrioritySemaphore.release();
//		}
	}

	public String printStatus() {
		return String.format("Http Fetcher Status: %d avail., %d queued [Priority: %d avail., %d queued]",fetchSemaphore.availablePermits(), fetchSemaphore.getQueueLength(),fetchWithPrioritySemaphore.availablePermits(), fetchWithPrioritySemaphore.getQueueLength()); 
	}
	
}
