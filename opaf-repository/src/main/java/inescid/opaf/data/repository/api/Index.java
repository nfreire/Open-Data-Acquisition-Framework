package inescid.opaf.data.repository.api;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;

import org.mapdb.BTreeKeySerializer;
import org.mapdb.DB;
import org.mapdb.Fun;

import inescid.opaf.data.repository.impl.BaseMapdbImplementation;

public class Index  extends BaseMapdbImplementation {
    
    /**
     * Creates a new instance of this class.
     * @param makeOrGet
     */
    public Index(DB db) {
        super();
        this.db = db;
    }

    /**
     * Creates a new instance of this class.
     * @param aliasIndexHome
     * @param accessMode
     */
    public Index(File aliasIndexHome, AccessMode accessMode) {
        super(aliasIndexHome, accessMode);
    }

    /**
     * adds a number of records to the index and does a single commit at the end of the transaction.
     * 
     * @param records
     * @throws IOException
     * @throws SolrServerException
     */
    public void add(Record... records) throws Exception {
        for (Record record : records) {
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
    public synchronized void add(Collection<Record> records) throws Exception {
        for (Record record : records) {
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
    public void addWithoutCommit(Record... records) throws Exception {
        for (Record record : records) {
            addRecordWithoutComit(record);
        }
    }

    /**
     * @param record
     */
    private void addRecordWithoutComit(Record record) {
//        System.out.println(record.getUid());
            for(Entry<Integer, List<Object>> field: record.getFields().entrySet()) {
                NavigableSet<Fun.Tuple2<?, ?>> multiMap = getMap(field.getKey());
                List<Fun.Tuple2<String, String>> vals=new ArrayList<Fun.Tuple2<String,String>>(field.getValue().size());
                for(Object val: field.getValue()) {
                    if(val!=null) 
                        vals.add(new Fun.Tuple2(val.toString(), record.getUid()));
    //                System.out.println(val);
                }
                multiMap.addAll(vals);
            }
    }

    /**
     * @param field
     * @return
     */
    private NavigableSet<Fun.Tuple2<?, ?>> getMap(Integer field) {
        NavigableSet<Fun.Tuple2<?,?>> multiMap = null;
        String fieldId = toStringFieldId(field);
        if(db.exists(fieldId))
            multiMap = db.getTreeSet(fieldId);
        else
            multiMap = db.createTreeSet(fieldId).serializer(BTreeKeySerializer.TUPLE2).make();
        return multiMap;
    }

    /**
     * @param field
     * @return
     */
    private String toStringFieldId(Integer field) {
        return new String(new char[] { (char)field.intValue() });
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
    public void addWithoutCommit(Collection<? extends Record> records) throws Exception{
        for (Record record : records) {
            addRecordWithoutComit(record);
        }
    }

    /**
     * searches the index for records found to the query. The query is performed with row=0 to
     * ensure that no records are loaded.
     * 
     * @param <T>
     * 
     * @param query
     * @param rows
     * @param type
     * @return the number of results for the query
     * @throws SolrServerException
     */
    public Iterable<Object> search(Integer field, Object value) {
        NavigableSet<Fun.Tuple2<Object, Object>> index = db. get(toStringFieldId(field));
        if(index==null)
            return Collections.emptyList();
        Iterable<Object> findVals2 = Fun.filter(index, value);
        return findVals2;
    }
    
    /**
     * @param field
     * @param value
     * @param classOfValue
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Iterable<T> search(Integer field, Object value, Class<T> classOfValue) {
        NavigableSet<Fun.Tuple2<Object, Object>> index = db. get(toStringFieldId(field));
        if(index==null)
            return Collections.emptyList();
        Iterable<Object> findVals2 = Fun.filter(index, value);
        return (Iterable<T>)findVals2;
    }
    

//    /**
//     * deletes/truncates the whole index.
//     * 
//     * @throws Exception
//     */
//    public void truncate() throws Exception {
//        Map<String, Object> all = db.getAll();
//        for(String idx: all.keySet())
//                db.delete(idx);
//    }

    /**
     * @param field
     * @return set of all vales of a field
     */
    public NavigableSet<Fun.Tuple2<String,String>> set(Integer field) {
        NavigableSet<Fun.Tuple2<String,String>> treeMap = db.getTreeSet(toStringFieldId(field));
        return treeMap;        
    }


}
