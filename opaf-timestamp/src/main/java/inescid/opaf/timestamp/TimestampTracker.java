package inescid.opaf.timestamp;

import java.util.Calendar;

public interface TimestampTracker {
	
	public void setCollectionTimestamp(String collection, Calendar timestamp);
	public void setObjectTimestamp(String collection, String object, Calendar timestamp);
	public void setObjectTimestamp(String collection, String object, Calendar timestamp, boolean deleted);

	public Status getCollectionStatus(String collection);
	public Status getObjectStatus(String collection, String object);
	
}
