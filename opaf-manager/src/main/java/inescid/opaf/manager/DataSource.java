package inescid.opaf.manager;

import java.util.Properties;

import inescid.opaf.framework.CrawlingSystem;

public interface DataSource {
	
	public void init(Properties props) throws Exception;
	
	public String getName();
	
	void synchronizeData(CrawlingSystem crawlingSys);

}
