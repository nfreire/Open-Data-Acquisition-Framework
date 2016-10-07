package inescid.opaf.framework;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;

public class FetchRequest {
	String url;
	String contentTypeToRequest;
	CrawlingSession session;
//	Semaphore fetchingSemaphore=new Semaphore(1);
	CloseableHttpResponse response;
	Content content;
	Throwable error;

	public FetchRequest(String url) {
		super();
		this.url = url;
//		try {
//			fetchingSemaphore.acquire();
//		} catch (InterruptedException e) {
//			// Can't happen
//		}
	}

	public FetchRequest(String url, CrawlingSession session) {
		super();
		this.url = url;
		this.session = session;
//		try {
//			fetchingSemaphore.acquire();
//		} catch (InterruptedException e) {
//			// Can't happen
//		}
	}



	public FetchRequest(String url, CrawlingSession crawlingSession, Throwable error) {
		this(url, crawlingSession);
		this.error=error;
	}

	public FetchRequest(String url, String contentTypeToRequest) {
		this(url);
		this.contentTypeToRequest = contentTypeToRequest;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public CrawlingSession getSession() {
		return session;
	}

	public void setSession(CrawlingSession session) {
		this.session = session;
	}
	
	
	/**
	 * May be used by another thread to wait for the request to be processed
	 * @throws IOException 
	 * @throws UnsupportedOperationException 
	 * 
	 * @throws InterruptedException
	 */
//	public void waitForFetchReady() throws InterruptedException {
//		fetchingSemaphore.acquire();
//		fetchingSemaphore.release();
//	}
	
//	public void fetchReady() {
//		fetchingSemaphore.release();
//	}

	public CloseableHttpResponse getResponse() {
		return response;
	}

	public void setResponse(CloseableHttpResponse response) throws UnsupportedOperationException, IOException {
		this.response = response;
		byte[] byteArray = IOUtils.toByteArray(response.getEntity().getContent());
		ContentType contentType = ContentType.get(response.getEntity());
		response.close();
		this.content=new Content(byteArray, contentType);
	}
		
//		fetchingSemaphore.release();

	public int getResponseStatusCode() {
		return response.getStatusLine().getStatusCode();
	}
	public Content getContent() throws IOException {
//		byte[] byteArray = IOUtils.toByteArray(response.getEntity().getContent());
//		ContentType contentType = ContentType.get(response.getEntity());
//		response.close();
//		return new Content(byteArray, contentType);
		return content;
	}

	public String getContentTypeToRequest() {
		return contentTypeToRequest;
	}

	public void setContentTypeToRequest(String contentTypeToRequest) {
		this.contentTypeToRequest = contentTypeToRequest;
	}
	
}
