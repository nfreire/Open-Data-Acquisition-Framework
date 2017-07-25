package inescid.opaf.data.repository.api.storage;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.mapdb.BTreeMap;
import org.mapdb.DB;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.impl.BaseMapdbImplementation;

public class UidRepository extends BaseMapdbImplementation{

    private static final String INDEX_NAME="_";

    BTreeMap<Object, byte[]> treeMap;
    /**
     * Creates a new instance of this class.
     * @param makeOrGet
     */
    public UidRepository(DB db) {
        super();
        this.db=db;
        treeMap = (BTreeMap<Object, byte[]>) db.treeMap(INDEX_NAME).createOrOpen();
    }

    
    public UidRepository(File home, AccessMode mode) {
        super(home, mode);
        treeMap = (BTreeMap<Object, byte[]>) db.treeMap(INDEX_NAME).createOrOpen();
    }

    /**
     * adds a number of records to the index and does a single commit at the end of the transaction.
     * 
     * @param records
     * @throws IOException
     * @throws SolrServerException
     */
    public void add(UidRecord... records) throws Exception {
        for (UidRecord record : records) {
            addRecordWithoutComit(record);
        }
        commit();
    }

    /**
     * adds a collection of index records to the server and commits at the end
     * 
     * @param records
     * @throws IOException
     * @throws SolrServerException
     */
    public synchronized void add(Collection<UidRecord> records) throws Exception {
        for (UidRecord record : records) {
            addRecordWithoutComit(record);
        }
        commit();
    }

    /**
     * adds a number of of index records without a commit. To make the record visible in the index
     * you either have to submit a "commit" afterwards or wait for the autocommit feature to step in
     * (see solrconf.xml)
     * 
     * @param records
     * @throws IOException
     * @throws SolrServerException
     */
    public void addWithoutCommit(UidRecord... records) throws Exception {
        for (UidRecord record : records) {
            addRecordWithoutComit(record);
        }
    }

    /**
     * @param record
     */
    private void addRecordWithoutComit(UidRecord record) {
                BTreeMap<Object, byte[]> treeMap = (BTreeMap<Object, byte[]>) db.treeMap(INDEX_NAME).createOrOpen();
//                BTreeMap<Object, byte[]> treeMap = db.treeMap(INDEX_NAME).createOrOpen();
                treeMap.put(record.getUid(), record.getValue());
    }


    /**
     * adds a collection of index records without a commit. To make the record visible in the index
     * you either have to submit a "commit" afterwards or wait for the autocommit feature to step in
     * (see solrconf.xml)
     * 
     * @param records
     * @throws IOException
     * @throws SolrServerException
     */
    public void addWithoutCommit(Collection<? extends UidRecord> records) throws Exception{
        for (UidRecord record : records) {
            addRecordWithoutComit(record);
        }
    }

    
    public byte[] getRecord(Object uid) {
        return  treeMap.get(uid);
    }


    /**
     * deletes/truncates the whole index.
     * 
     * @throws IOException
     */
    public void truncate() throws Exception {
//        db.delete(INDEX_NAME);
    }

    /**
     * @param mdrIdString
     * @return
     */
    public boolean containsRecord(String uid) {
        return treeMap.containsKey(uid);
    }

    public BTreeMap<Object, byte[]> setOfRecords() {
        return treeMap;
    }

    public int size() {
    	return treeMap.size();
    }
}
