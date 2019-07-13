package inescid.opaf.data.convert.rdf;

import org.apache.log4j.Logger;

import inescid.opaf.data.DataSpec;

public class DataConversionManager {
	
	public enum DataProfile { EDM, SCHEMA_ORG, DC};
	
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
	
	public DataConverter getConverter(DataProfile from, DataProfile to) {
		if(from==DataProfile.SCHEMA_ORG && to==DataProfile.EDM)
			return new SchemaOrgToEdmDataConverter();
		throw new IllegalArgumentException("No converter available for "+ from+" to "+to);
//		if(from==null)
//			return new DublinCoreToEdmDataConverter();
//		if(from.getContentType().equals("application/ld+json"))
//		return null;
	}
}
