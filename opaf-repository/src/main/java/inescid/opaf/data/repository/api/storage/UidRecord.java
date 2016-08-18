package inescid.opaf.data.repository.api.storage;


/**
 * A record
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 06/11/2013
 */
public class UidRecord {
    
    private Object uid;
    
    private byte[] value;
    
    /**
     * Creates a new instance of this class.
     */
    public UidRecord() {
        super();
    }

    
    /**
     * Creates a new instance of this class.
     * @param uid2
     */
    public UidRecord(Object uid) {
        this.uid = uid;
    }

    public UidRecord(Object uid, byte[] value) {
        this.uid = uid;
        this.value = value;
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
     * Returns the value.
     * @return the value
     */
    public final byte[] getValue() {
        return value;
    }


    /**
     * Sets the value
     * @param value the value to set
     */
    public final void setValue(byte[] value) {
        this.value = value;
    }


}
