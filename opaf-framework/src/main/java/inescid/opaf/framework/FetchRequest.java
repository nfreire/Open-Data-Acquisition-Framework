package inescid.opaf.framework;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

public class FetchRequest {
	String url;
	CrawlingSession session;
	Semaphore fetchingSemaphore=new Semaphore(1);
	HttpResponse response;
	Throwable error;

	public FetchRequest(String url) {
		super();
		this.url = url;
		try {
			fetchingSemaphore.acquire();
		} catch (InterruptedException e) {
			// Can't happen
		}
	}

	public FetchRequest(String url, CrawlingSession session) {
		super();
		this.url = url;
		this.session = session;
		try {
			fetchingSemaphore.acquire();
		} catch (InterruptedException e) {
			// Can't happen
		}
	}



	public FetchRequest(String url, CrawlingSession crawlingSession, Throwable error) {
		this(url, crawlingSession);
		this.error=error;
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
	
	public void waitForFetchReady() throws InterruptedException {
		fetchingSemaphore.acquire();
	}
	
	public void fetchReady() {
		fetchingSemaphore.release();
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}

	public Content getContent() throws IOException {
		byte[] byteArray = IOUtils.toByteArray(response.getEntity().getContent());
		ContentType contentType = ContentType.get(response.getEntity());
		return new Content(byteArray, contentType);
	}
}
