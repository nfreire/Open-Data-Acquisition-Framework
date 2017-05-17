package inescid.opaf.data.convert;

import org.apache.log4j.Logger;

import inescid.opaf.data.DataSpec;

public class DataConversionManager {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataConversionManager.class);
	
	private static DataConversionManager INSTANCE;
	
	public static DataConversionManager getInstance() {
		if(INSTANCE==null) {
			synchronized (log) {
				if(INSTANCE==null) 
					INSTANCE=new DataConversionManager();
			}
		}
		return INSTANCE;
	}
	
	public DataConverter getConverter(DataSpec from, DataSpec to) {
		if(from==null)
			return new DublinCoreToEdmDataConverter();
		if(from.getContentType().equals("application/ld+json"))
			return new SchemaOrgToEdmDataConverter();
		return null;
			
	}
}
