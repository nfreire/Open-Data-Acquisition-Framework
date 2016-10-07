package inescid.opaf.framework;

public interface ResponseHandler {
	
	public void handle(FetchRequest respondedFetchRequest);
	
	public void close();
}
