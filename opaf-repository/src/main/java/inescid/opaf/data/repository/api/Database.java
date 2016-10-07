/* KeyDb.java - created on 10/11/2013, Copyright (c) 2011 The European Library, all rights reserved */
package inescid.opaf.data.repository.api;

import java.io.File;
import java.util.Collection;
import java.util.NavigableSet;

import org.mapdb.BTreeMap;
import org.mapdb.Fun.Tuple2;

import inescid.opaf.data.repository.api.storage.UidRecord;
import inescid.opaf.data.repository.api.storage.UidRepository;
import inescid.opaf.data.repository.impl.BaseMapdbImplementation;
import inescid.opaf.data.repository.impl.IoUtil;

/**
 * 
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 10/11/2013
 */
public class Database extends BaseMapdbImplementation {
    Index index;
    UidRepository repository;
    
    /**
     * Creates a new instance of this class.
     * @param home
     * @param mode
     * @throws Exception
     */
    public Database(File home, AccessMode mode) throws Exception {
        super(home, mode);
        index=new Index(db);
        repository= new UidRepository(db);
    }


    
    /**
     * adds a number of records to the index and does a single commit at the end of the transaction.
     * 
     * @param records
     * @throws Exception
     */
    public void add(Record... records) throws Exception {
        for (Record record : records) {
            addRecordWithoutComit(record);
        }
        db.commit();
    }

    /**
     * adds a collection of index records to the server and commits at the end
     * 
     * @param records
     * @throws Exception
     */
    public synchronized void add(Collection<Record> records) throws Exception {
        for (Record record : records) {
            addRecordWithoutComit(record);
        }
        db.commit();
    }

    /**
     * adds a number of of index records without a commit. To make the record visible in the index
     * you either have to submit a "commit" afterwards or wait for the autocommit feature to step in
     * (see solrconf.xml)
     * 
     * @param records
     * @throws Exception
     */
    public void addWithoutCommit(Record... records) throws Exception {
        for (Record record : records) {
            addRecordWithoutComit(record);
        }
    }

    /**
     * @param record
     * @throws Exception 
     */
    private void addRecordWithoutComit(Record record) throws Exception {
        index.addWithoutCommit(record);
        if(record.getData()!=null)
            repository.addWithoutCommit(new UidRecord(record.getUid(), IoUtil.toByteArray(record.getData())));
    }



    public Iterable<Object> search(Enum field, Object value) {
        return search(field.ordinal(), value);
    }
    /**
     * @param string
     * @param labelForLcsh
     * @return
     */
    public Iterable<Object> search(Integer field, Object value) {
        return index.search(field, value);
    }

    public <T> Iterable<T> search(Enum field, Object value, Class<T> classOfId) {
//        System.out.println(field.ordinal()+" "+value);
        return search(field.ordinal(), value, classOfId);
    }
        
        /**
     * @param field
     * @param value
     * @param classOfId
     * @return ids of matching records
     */
    public <T> Iterable<T> search(Integer field, Object value, Class<T> classOfId) {
        return index.search(field, value, classOfId);
    }
    
    public NavigableSet<Tuple2<String, String>> set(Enum field) {
        return set(field.ordinal());
    }
    /**
     * @param field
     * @return ids of matching records
     */
    public NavigableSet<Tuple2<String, String>> set(Integer field) {
        return index.set(field);
    }


    /**
     * @param id
     * @return record data (the indexed fields are not retrievable)
     */
    public Object getData(Object id) {
        byte[] record = repository.getRecord(id);
        if(record==null)
            return null;
        return deserialize(record);
    }
    
    public static Object deserialize(byte[] data) {;
    	return IoUtil.fromByteArray(data);
    }

    protected BTreeMap<Object, byte[]> setOfRecords() {
    	return repository.setOfRecords();
    }
    
    public Collection<byte[]> getAllRecordsData() {
    	return setOfRecords().values();
    }
}
