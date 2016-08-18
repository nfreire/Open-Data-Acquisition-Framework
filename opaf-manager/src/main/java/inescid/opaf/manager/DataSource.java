package inescid.opaf.manager;

import java.util.Date;

import inescid.opaf.framework.CrawlingSystem;

public interface DataSource {
	
	public String getName();
	
	void synchronizeData(Date lastUpdate, CrawlingSystem crawlingSys);

}
