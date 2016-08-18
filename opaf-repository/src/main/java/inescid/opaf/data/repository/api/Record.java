package inescid.opaf.data.repository.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A record
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 06/11/2013
 */
public class Record implements Serializable {
    
    private Object uid;
    
    private Object data;
    
    private Map<Integer, List<Object>> fields;
    
    /**
     * Creates a new instance of this class.
     */
    public Record() {
        super();
    }

    
    /**
     * Creates a new instance of this class.
     * @param uid2
     */
    public Record(Object uid) {
        this.uid = uid;
    }


    /**
     * Creates a new instance of this class.
     * @param uid
     * @param data
     */
    public Record(Object uid, Object data) {
        super();
        this.uid = uid;
        this.data = data;
    }


    /**
     * Returns the uid.
     * @return the uid
     */
    public final Object getUid() {
        return uid;
    }



    /**
     * Sets the uid
     * @param uid the uid to set
     */
    public final void setUid(Object uid) {
        this.uid = uid;
    }

    /**
     * Returns the FieldsMulti.
     * @return the FieldsMulti
     */
    public final Map<Integer, List<Object>> getFields() {
        if(fields==null)
            fields=new HashMap<Integer, List<Object>>();
        return fields;
    }

    /**
     * Sets the FieldsMulti
     * @param fieldsMulti the FieldsMulti to set
     */
    public final void setFields(Map<Integer, List<Object>> fieldsMulti) {
        this.fields = fieldsMulti;
    }
    
    /**
     * @param field
     * @param classOfValue
     * @return values 
     */
    @SuppressWarnings("unchecked")
    public final <T> List<T> getField(Integer field, Class<T> classOfValue) {
        List<Object> ret = getFields().get(field);
        if(ret==null)
            return new ArrayList<T>(0);
        return (List<T>)ret;
    }

    public final List<Object> getField(Enum field) {
        return getField(field.ordinal());
    }
    /**
     * Returns the FieldsMulti.
     * @param field 
     * @return the FieldsMulti
     */
    public final List<Object> getField(Integer field) {
        List<Object> ret = (List<Object>)getFields().get(field);
        if(ret==null)
            return new ArrayList<Object>(0);
        return ret;
    }
    
    public void addField(Enum field, Object... values) {
        addField(field.ordinal(), values);
    }
    public void addField(Integer field, Object... values) {
        Map<Integer, List<Object>> fieldsMulti = getFields();
        List<Object> vals = fieldsMulti.get(field);
        if(vals==null) {
            vals = new ArrayList<Object>(values.length);
            fieldsMulti.put(field, vals);
        }
        for(Object v: values) 
            vals.add(v);
    }
    public void addField(Enum field, Collection<?> values) {
        addField(field.ordinal(), values);
    }
    public void addField(Integer field, Collection<?> values) {
        Map<Integer, List<Object>> fieldsMulti = getFields();
        List vals = fieldsMulti.get(field);
        if(vals==null) {
            vals = new ArrayList<String>(values.size());
            fieldsMulti.put(field, vals);
        }
        for(Object v: values) 
            vals.add(v);
    }

    @Override
    public String toString() {
        return "Record [uid=" + uid + ", fields=" + fields + "]";
    }


    /**
     * Returns the data.
     * @return the data
     */
    public Object getData() {
        return data;
    }


    /**
     * Sets the data
     * @param data the data to set
     */
    public void setData(Object data) {
        this.data = data;
    }


    /**
     * 
     */
    public void setDataToFields() {
        this.data=getFields();
    }
    



    
}
