package inescid.opaf.framework;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


public class HttpFetcher {
	CloseableHttpClient httpClient;
	DomainsManager domainsManager;
//	ArrayList<FetchRequest> requestsQueue=new ArrayList<>(50);
	Semaphore fetchSemaphore=new Semaphore(5);
	
	public void init() {
		httpClient = 
				HttpClients.custom()
				.setSSLHostnameVerifier(new NoopHostnameVerifier())
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
			HttpResponse response = httpClient.execute(request);
			url.setResponse(response);
		}finally {
			fetchSemaphore.release();
		}
	}
	
}
