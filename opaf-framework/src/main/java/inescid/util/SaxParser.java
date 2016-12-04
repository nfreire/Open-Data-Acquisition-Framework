/*
 * UnimarcSaxParser.java
 *
 * Created on 28 de Abril de 2004, 17:32
 */

package inescid.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Stack;


/** Parses marc records contained in an xml ImputStream 
 * 
 * @author  Nuno Freire
 */
public class SaxParser extends DefaultHandler{
    protected Document currentRecord;
    protected SaxParserClient client;
    protected String rootElementNamespace;
    protected String rootElementName;
    Stack<Element> currentRecordElementStack;

    public SaxParser(SaxParserClient client, String rootElementNamespace, String rootElementName) {
    	this.client = client;
    	this.rootElementNamespace = rootElementNamespace;
    	this.rootElementName = rootElementName;
    	currentRecordElementStack=new Stack<Element>();
    }
    
    public void startDocument() throws SAXException {
    }
    
    public void endDocument() throws SAXException {
  		client.signalParseFinished();
    }

    private Element createElement(String uri, String localName, String qName, Attributes attributes) {
        Element el;
        if(uri==null)
            el=currentRecord.createElement(localName);
        else
            el=currentRecord.createElementNS(uri,localName);
        for (int i=0; i<attributes.getLength(); i++) {
            if(attributes.getURI(i)!=null)
                el.setAttributeNS(attributes.getURI(i), attributes.getLocalName(i), attributes.getValue(i));
            else
                el.setAttribute(attributes.getLocalName(i), attributes.getValue(i));
        }
        currentRecordElementStack.push(el);
        return el;
    }

    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException  {
        if(currentRecord==null) {
            if ((rootElementNamespace==null || rootElementNamespace.equals(uri)) &&
                    localName.equals(rootElementName) ){
                currentRecord=XmlUtil.newDocument();
                Element el=createElement(uri, localName, qName, attributes);
                currentRecord.appendChild(el);
            } 
        } else {
            Element el=createElement(uri, localName, qName, attributes);
            currentRecordElementStack.peek().appendChild(el);
        }
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException  {
        if(currentRecord!=null) {
            currentRecordElementStack.pop();
            if(currentRecordElementStack.isEmpty()) {
                client.processRecord(currentRecord);
                currentRecord=null;
            }
        }
    }
    
    public void characters(char buf[], int offset, int len) throws SAXException {
        if(currentRecord!=null) {
            Text text = currentRecord.createTextNode(new String(buf,offset,len));
            currentRecordElementStack.peek().appendChild(text);
        }
    }
    
   


   /**
	 * @param in The InputStream with the records in xml
	 * @param client The class that will process the parsed records
	 * @throws SAXException
	 */
	public static void parse(InputStream in, SaxParserClient client, String rootElementNamespace, String rootElementName) throws SAXException{
       try{
           SaxParser handler = new SaxParser(client, rootElementNamespace, rootElementName);
           SAXParserFactory factory = SAXParserFactory.newInstance();
           SAXParser saxParser = factory.newSAXParser();
           XMLReader parser = saxParser.getXMLReader();
           parser.setContentHandler(handler);
           InputSource inSource=new InputSource(in);
           parser.parse(inSource);
       }catch(Exception e){
           throw new SAXException(e);
       }
    }
	
   
}
