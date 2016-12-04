package inescid.opaf.framework;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.sound.midi.Soundbank;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
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
	
//	Vector<Long> requestTimeStats=null;
	Vector<Long> requestTimeStats=new Vector<>();
	
	
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
			Long startTime = requestTimeStats!=null ? System.nanoTime() : null;
			HttpGet request = new HttpGet(url.getUrl());
			url.addHeaders(request);
			CloseableHttpResponse response = httpClient.execute(request);
			if(startTime!=null) {
				long duration=System.nanoTime() - startTime;
				synchronized (requestTimeStats) {
					requestTimeStats.add(duration);
					if(requestTimeStats.size() % 10 == 0) {
						long sum=0;
						for(int i=requestTimeStats.size()-1; i>=requestTimeStats.size()-10 ; i--)
							sum+=requestTimeStats.get(i);
						long recTime = sum/10;
						float recRate = 60000000000f / recTime;
						float recRateHour = recRate * 60 *60;
						System.out.println("10 Requests done at "+ recTime + "ns/rec - "+recRate+" recs/sec "+ recRateHour+" recs/hour");
					}
				}
			}
			url.setResponse(response);
		}finally {
			fetchSemaphore.release();
		}
	}
//	public void fetchHeadWithPriority(FetchRequest url) throws InterruptedException, IOException {
//		HttpHead request = new HttpHead(url.getUrl());
//		url.addHeaders(request);
//		CloseableHttpResponse response = httpClient.execute(request);
//		url.setResponse(response);
//	}
	
	
	public void fetchWithPriority(FetchRequest url) throws InterruptedException, IOException {
//		fetchWithPrioritySemaphore.acquire();
//		try {
			HttpGet request = new HttpGet(url.getUrl());
			url.addHeaders(request);
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
