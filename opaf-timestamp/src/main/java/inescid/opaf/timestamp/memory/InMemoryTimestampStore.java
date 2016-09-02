package inescid.opaf.timestamp.memory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import inescid.opaf.timestamp.Status;

public class InMemoryTimestampStore {
	Map<String, Map<String, Long>> db=new HashMap<>();
	Map<String, Long> dbCollection=new HashMap<>();
	Set<String> dbDeletedCollection=new HashSet<>();
	Map<String, Set<String>> dbDeleted=new HashMap<>();
	
	public void setCollectionTimestamp(String collection, Calendar timestamp) {
		dbCollection.put(collection, timestamp.getTimeInMillis());
	}

	public void setObjectTimestamp(String collection, String object, Calendar timestamp) {
		setObjectTimestamp(collection, object, timestamp, false);
	}

	public Status getCollectionStatus(String collection) {
		Long ret = dbCollection.get(collection);
		if(ret==null)
			return null;
		Status s=new Status(ret, dbDeletedCollection.contains(collection));
		return s;
	}

	public Status getObjectStatus(String collection, String object) {
		Map<String, Long> ret = db.get(collection);
		if(ret==null)
			return null;
		Long tmst = ret.get(object);
		if(tmst==null)
			return null;
		Status s=new Status(tmst, dbDeleted.get(collection).contains(object));
		return s;
	}

	public void setObjectTimestamp(String collection, String object, Calendar timestamp, boolean deleted) {
		Map<String, Long> colMap = db.get(collection);
		if(colMap==null) {
			colMap=new HashMap<>();
			db.put(collection, colMap);
			dbDeleted.put(collection, new HashSet<>());
			
		}
		colMap.put(object, timestamp.getTimeInMillis());
		if(deleted)
			dbDeleted.get(collection).add(object);
		else
			dbDeleted.get(collection).remove(object);
	}
	
	
}
