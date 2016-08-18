/* AbstractMapdbImplementation.java - created on 27/05/2014, Copyright (c) 2011 The European Library, all rights reserved */
package inescid.opaf.data.repository.impl;

import java.io.File;
import java.io.IOException;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.storage.UidRepository;

/**
 * 
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 27/05/2014
 */
public class BaseMapdbImplementation {
    protected DB db;
    protected AccessMode dbMode;
    protected File homeFolder;
    
    /**
     * Creates a new instance of this class.
     * @param home
     * @param mode
     */
    public BaseMapdbImplementation(File home, AccessMode mode) {
        this.homeFolder=home;
        if(!home.exists()) {
            home.mkdirs();
            if(mode==AccessMode.READ_ONLY) {
                db = createDb(AccessMode.WRITE, true);            
                db.close();
            }
        }
        db=createDb(mode, true);        
    }

    /**
     * Creates a new instance of this class.
     */
    public BaseMapdbImplementation() {
    }

    /**
     * optimize whole index.
     * 
     * @throws Exception
     */
    public void optimize() throws Exception {
        db.compact();
    }
    
    /**
     * 
     */
    public void backup() {
        if(!db.isClosed())
            db.close();
        
        System.out.println("backing up");
        try {
            File dbFile = new File(homeFolder, "db.bin");
            File dbFileBck = new File(homeFolder, "db.bin.bck");
            IoUtil.copyFileUsingFileChannels(dbFile, dbFileBck);
            dbFile = new File(homeFolder, "db.bin.p");
            dbFileBck = new File(homeFolder, "db.bin.p.bck");
            IoUtil.copyFileUsingFileChannels(dbFile, dbFileBck);
            dbFile = new File(homeFolder, "db.bin.t");
            dbFileBck = new File(homeFolder, "db.bin.t.bck");
            IoUtil.copyFileUsingFileChannels(dbFile, dbFileBck);
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace(System.out);
        }
    }
    
    

    /**
     * @return
     */
    protected DB createDb(AccessMode mode, boolean retry) {
        this.dbMode=mode;
//        return DBMaker.newFileDB(new File(homeFolder, "db.bin")).transactionDisable().make();
//        return DBMaker.newFileDB(new File(homeFolder, "db.bin")).make();
        try {
            DBMaker<?> dbmaker = DBMaker.newFileDB(new File(homeFolder, "db.bin")).mmapFileEnable();
            switch (mode) {
            case READ_ONLY:
                dbmaker=dbmaker.readOnly();
                break;
            case WRITE:
                break;
            case WRITE_TRANSACTION_DISABLE:
                dbmaker=dbmaker.transactionDisable();
                break;
            default:
                break;
            }
            return dbmaker.make();
        } catch (Throwable e) {
            if(retry) {
                clearTransactionFile();
                return createDb(mode, false);
            } else
                throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * @throws Exception 
     * 
     */
    public void shutdown() throws Exception {
        if(db!=null && !db.isClosed()) {
            if(dbMode!=AccessMode.READ_ONLY)
                db.commit();
            db.close();
        }
    }

    /**
     * 
     */
    public void clear() {
        if(db!=null && !db.isClosed())
            db.close();
        
        File dbFile = new File(homeFolder, "db.bin");
        dbFile.delete();
        dbFile = new File(homeFolder, "db.bin.p");
        dbFile.delete();
        dbFile = new File(homeFolder, "db.bin.t");
        dbFile.delete();
    }

    /**
     * 
     */
    private void clearTransactionFile() {
        if(db!=null && !db.isClosed())
            throw new RuntimeException("Cannot delete transaction file on open connection");
        
        File dbFile = new File(homeFolder, "db.bin.t");
        dbFile.delete();
    }
    
    

    

    /**
     * commit last changes
     * 
     * @throws SolrServerException
     * @throws IOException
     */
    public void commit() throws Exception {
        if(dbMode!=AccessMode.READ_ONLY)
            db.commit();
    }

    /**
     * Returns the db.
     * @return the db
     */
    public final DB getDb() {
        return db;
    }

    /**
     * Returns the dbMode.
     * @return the dbMode
     */
    public final AccessMode getDbMode() {
        return dbMode;
    }

    /**
     * Returns the homeFolder.
     * @return the homeFolder
     */
    public final File getHomeFolder() {
        return homeFolder;
    }


    
    
}
