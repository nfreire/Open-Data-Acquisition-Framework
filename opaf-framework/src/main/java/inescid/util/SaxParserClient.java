/*
 * Created on 2006/08/17
 *
 */
package inescid.util;

import org.w3c.dom.Document;


public abstract class SaxParserClient {
	boolean parseFinished=false;
	int counter=0;
	public void nextRecord(Document rec) throws Exception {
		processRecord(rec);
		counter++;
	};
	public int getRecordCount() {
		return counter;
	}
	
	public void signalParseFinished() {
		parseFinished=true;
	}
	
	
	
	/**
     * Returns the parseFinished.
     * @return the parseFinished
     */
    public final boolean isParseFinished() {
        return parseFinished;
    }
    protected abstract void processRecord(Document rec) ;
}
