package inescid.opaf.framework;

import java.io.File;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.DBMaker.Maker;
import org.mapdb.Serializer;

public class BigSet<T> {
	String name;
	Set<T> set;
	

    protected DB db;
    protected File homeFolder;
    
    public BigSet(String name, File home) {
        this.homeFolder=home;
        this.name = name;
        if(!home.exists()) 
            home.mkdirs();
        db=createDb(true);  
//		if(db.exists(name))
//			set=db.get(name);
//		else {
			set = (Set<T>) db.hashSet(name).createOrOpen();
//		}
    }
	
	public BigSet(String name, DB mapDb) {
		super();
		this.name = name; 
		this.db = mapDb;
//		if(mapDb.exists(name))
//			set=mapDb.get(name);
//		else {
			set = (Set<T>) mapDb.hashSet(name).createOrOpen();
//		}
	}
    protected DB createDb(boolean retry) {
    	try {
            Maker dbmaker = DBMaker.fileDB(new File(homeFolder, "db.bin"));
//            		().fileMmapEnable();
            return dbmaker.make();
        } catch (Throwable e) {
            if(retry) {
                File dbFile = new File(homeFolder, "db.bin.t");
                dbFile.delete();
                return createDb(false);
            } else
                throw new RuntimeException(e.getMessage(), e);
        }
    }
	public Set<T> getSet() {
		return set;
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public int size() {
		return set.size();
	}

	public synchronized boolean addSynchronized(T url) throws InterruptedException {
		if(set.contains(url))
			return false;
		set.add(url);
		return true;
	}
	

	public void clear() {
		set.clear();
	}
}

