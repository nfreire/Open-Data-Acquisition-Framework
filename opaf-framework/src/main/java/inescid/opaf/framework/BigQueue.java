package inescid.opaf.framework;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class BigQueue<T> {
	String name;
	BlockingQueue<T> queue;
	

    protected DB db;
    protected File homeFolder;
    
    public BigQueue(String name, File home) {
        this.homeFolder=home;
        this.name = name;
        if(!home.exists()) 
            home.mkdirs();
        db=createDb(true);  
		if(db.exists(name))
			queue=db.get(name);
		else {
			queue = (BlockingQueue<T>) db.createQueue(name, Serializer.JAVA, true);
		}
    }
	
	public BigQueue(String name, DB mapDb) {
		super();
		this.name = name; 
		this.db = mapDb;
		if(mapDb.exists(name))
			queue=mapDb.get(name);
		else {
			queue = (BlockingQueue<T>) mapDb.createQueue(name, Serializer.JAVA, true);
		}
	}
    protected DB createDb(boolean retry) {
    	try {
            DBMaker<?> dbmaker = DBMaker.newFileDB(new File(homeFolder, "db.bin")).mmapFileEnable();
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
	public BlockingQueue<T> getQueue() {
		return queue;
	}

	public synchronized T poll(int i, TimeUnit seconds) throws InterruptedException {
		return queue.poll(i, seconds);
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public int size() {
		return queue.size();
	}

	public void put(T url) throws InterruptedException {
		queue.put(url);
		
		
	}
	
	
}

