package inescid.opaf.timestamp.memory;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import inescid.opaf.timestamp.Status;
import inescid.opaf.timestamp.TimestampTracker;

public class MemoryTimestampTracker implements TimestampTracker {
	
	InMemoryTimestampStore db=new InMemoryTimestampStore();
	

	@Override
	public void setCollectionTimestamp(String collection, Calendar timestamp) {
		db.setCollectionTimestamp(collection, timestamp);

	}

	@Override
	public void setObjectTimestamp(String collection, String object, Calendar timestamp) {
		db.setObjectTimestamp(collection, object, timestamp);

	}


	@Override
	public Status getCollectionStatus(String collection) {
		return db.getCollectionStatus(collection);
	}

	@Override
	public Status getObjectStatus(String collection, String object) {
		return db.getObjectStatus(collection, object);
	}

	@Override
	public void setObjectTimestamp(String collection, String object, Calendar timestamp, boolean deleted) {
		db.setObjectTimestamp(collection, object, timestamp, deleted);
		
	}


}
