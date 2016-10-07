/* DatabaseImporter.java - created on 26/05/2014, Copyright (c) 2011 The European Library, all rights reserved */
package inescid.opaf.data.repository.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 26/05/2014
 */
public class DatabaseImporter {
    
    Database db;
    Iterator<Record> source; 
    boolean backupInMiddle;
    
    /**
     * Creates a new instance of this class.
     * @param db
     * @param source
     * @param backupInMiddle
     * @throws Exception
     */
    public DatabaseImporter(Database db, Iterator<Record> source, boolean backupInMiddle) throws Exception {
        this.db=db;
        this.backupInMiddle = backupInMiddle;
        this.source = source;
    }
    
    
    /**
     * @throws Exception
     */
    public void runImport() throws Exception {
        StoreWorker worker=new StoreWorker(db);
        Thread workerThread = new Thread(worker);
        workerThread.start();
        try {
            List<Record> recsBatch=new ArrayList<Record>(1000);
            Date start=new Date();
            int recCnt=0;
            while ( source.hasNext() ) {
                Record rec=source.next();
                if(rec!=null) {
                    recCnt++;
                    recsBatch.add(rec);
                    if(recsBatch.size()==3000) {
                        boolean backupNow=false;
                        if(backupInMiddle && (recCnt % 1000000 == 0 || recCnt % 1000000 == 1000 || recCnt % 1000000 == 2000)) {
                            backupNow=true;
                        }
                        
                        worker.store(recsBatch, false, backupNow);
                        recsBatch=new ArrayList<Record>(3000);
                        System.out.println("Import speed: "+ (int)(3000 / ((double)(new Date().getTime() - start.getTime()) / 3000))+ " recs./sec.");
                        start=new Date();
                    }
                }
            }
            if(!recsBatch.isEmpty())
                worker.store(recsBatch, true, false);
            worker.finish();
            worker.waitFinished();
            if(worker.getFailure()!=null)
                throw new Exception("Import failed", worker.getFailure());
        } finally {
            if(workerThread.isAlive())
                workerThread.interrupt();
        }
    }
    
    /**
     * 
     * 
     * @author Nuno Freire (nfreire@gmail.com)
     * @since 22/07/2014
     */
    class StoreWorker implements Runnable {
        SynchronousQueue<List<Record>> request=new SynchronousQueue<List<Record>>();
        boolean finished;
        boolean backup=false;
        boolean running=true;
        Throwable failure=null;
        Database store;
        
        /**
         * @param store
         * @throws Exception 
         */
        public StoreWorker(Database store) throws Exception {
            super();
            this.store = store;
        }


        /**
         * 
         */
        public void waitFinished() {
            while (running) try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }


        /**
         * 
         */
        public void finish() {
            finished=true;
        }


        /**
         * @param recsBatch
         * @param finish 
         * @param backupNow 
         */
        public void store(List<Record> recsBatch, boolean finish, boolean backupNow) {
            try {
                request.put(recsBatch);
                this.finished=finish;
                this.backup=backupNow;
            } catch (InterruptedException e) {
                e.printStackTrace();
                e.printStackTrace(System.out);  
                throw new RuntimeException(e.getMessage(), e);
            }
        }


        @Override
        public void run() {
            try {
                while(!finished) {
                    List<Record> recsBatch;
                    try {
                        recsBatch = request.poll(2, TimeUnit.SECONDS);
                        if(recsBatch!=null) {
                            store.add(recsBatch);
                            
                            if(backup) {
                                store.optimize();
                                store.shutdown();
                                store.backup();
                                backup=false;
                            }
                        }
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                        e.printStackTrace(System.out);                    
                        throw e;
                    }
                }
                System.out.println("Import finished. Optimizing now...");
                store.optimize();
            } catch (Throwable e) {
                e.printStackTrace();
                e.printStackTrace(System.out);
                failure=e;
                throw new RuntimeException(e.getMessage(), e);
            }finally {
                running=false;
            }
        }


        /**
         * Returns the failure.
         * @return the failure
         */
        public final Throwable getFailure() {
            return failure;
        }


    }
}
