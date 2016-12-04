/* RdfXmlResourceIterator.java - created on 07/11/2013, Copyright (c) 2011 The European Library, all rights reserved */
package inescid.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class InnerElementXmlIterator implements Iterator<Document>{
    
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(InnerElementXmlIterator.class.getName());

    InputStream xmlFis;
    LinkedBlockingQueue<Document> queue=new LinkedBlockingQueue<Document>(3);
    Thread parserThread=null;
    SaxParserClient saxParserClient;

//    public void nextRecord(Document rec) throws Exception {
//        processRecord(rec);
//        counter++;
//    };
    
//    public int getRecordCount() {
//        return counter;
//    }
    
    boolean iteratorWasInterruptedInTheMiddle=false;
    
    public InnerElementXmlIterator(InputStream xmlFile, String rootElementNamespace, String rootElementName) throws FileNotFoundException, SAXException{
        this.xmlFis=xmlFile;
        saxParserClient=new SaxParserClient() {
            @Override
            protected void processRecord(Document rec) {
                if (!iteratorWasInterruptedInTheMiddle) {
                    try {
                        if (!queue.offer(rec, 60L*30L, TimeUnit.SECONDS)) {
                            log.finest("interrupted");
                            iteratorWasInterruptedInTheMiddle=true;
                        }
                    } catch (InterruptedException e) {
                        log.finest("interrupted");
                        iteratorWasInterruptedInTheMiddle=true;
                    }
                }
            }
        };
        
        parserThread=new Thread(new SaxParserRunner(xmlFis, saxParserClient, rootElementNamespace, rootElementName));
        parserThread.start();
    }

    
    public boolean hasNext() {
        log.finest("hasnext");
        while (queue.isEmpty() && !saxParserClient.isParseFinished()) {
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
            }
        }
        if(!queue.isEmpty())
            return true;
        else {      
            if(parserThread!=null)
                parserThread.interrupt();
            parserThread=null;
            return false;           
        }
    }

    
    public Document next() {      
        log.finest("next");
        try {
            Document take = queue.take();
            if(take==null)
                if(hasNext())
                    take=queue.take();
            return take;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    
    public void remove() {
        throw new RuntimeException("not implemented");      
    }

    
    public void close() {
        iteratorWasInterruptedInTheMiddle=true;
    }
    
    class SaxParserRunner implements Runnable{
        InputStream fis;
        SaxParserClient client;
        String rootElementNamespace; 
        String rootElementName;
        Exception exception;
        
        public SaxParserRunner(InputStream fis, SaxParserClient client, String rootElementNamespace, String rootElementName) {
            super();
            this.fis = fis;
            this.client = client;
            this.rootElementName = rootElementName;
            this.rootElementNamespace = rootElementNamespace;
        }


        public void run() {
            try {
                SaxParser.parse(fis, client, "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description");
            } catch (SAXException e) {
                e.printStackTrace();
                this.exception=e;
                client.signalParseFinished();
            }
        }
    }
    
}



