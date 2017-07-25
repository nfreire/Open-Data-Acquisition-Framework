package inescid.opaf.casestudies.schemaorg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.sound.midi.Soundbank;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.log4j.xml.XMLLayout;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.DataConversionManager;
import inescid.opaf.data.convert.DataConverter;
import inescid.opaf.data.convert.SchemaOrgToEdmDataConverter;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.manager.test.EdmFileExport;

public class RepositoryToEdmIterator {

	public static abstract class EdmHandler {
		public abstract boolean handle(String recId, byte[] edmXmlStringUtf8Bytes);
		public void init() {};
		public void finish() {};		
	}
	
	static final Charset UTF8=Charset.forName("UTF8");
	static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
	EdmHandler handler;
	
	public RepositoryToEdmIterator(EdmHandler handler) {
		super();
		this.handler = handler;
	}

	public void iterate(File repositoryFolder, boolean transformToEdmInternal, String provider, String dataProvider) throws Exception {
		handler.init();
		
		File dbFile = new File(repositoryFolder, "db.bin");
		if(!dbFile.exists()) {
			System.out.println("Repository is empty. Exiting.");
			return;
		}		
		System.out.println(new Date(dbFile.lastModified()));
		
		Database db;
		db=new Database(repositoryFolder, AccessMode.READ_ONLY);
		
		DataConverter schemaOrgToEdmConverter = DataConversionManager.getInstance().getConverter(new DataSpec("application/ld+json", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "http://schema.org/"), new DataSpec("application/xml", "http://edm", null));
		((SchemaOrgToEdmDataConverter)schemaOrgToEdmConverter).setDataProvider(dataProvider);
		int cnt=0;
		for(Iterator<Entry<Object, byte[]>> it = db.getAllRecords(); it.hasNext() ; ) {
			Entry<Object, byte[]> dbEntry=it.next();
			byte[] mdBytes=dbEntry.getValue();
			cnt++;
			Object metadata = IoUtil.fromByteArray(mdBytes);
			try {
				String jsonldString=null;
				if(metadata instanceof String) {
					jsonldString = metadata.toString();;
				} else if(metadata instanceof IiifManifest) {
					IiifManifest manifest=(IiifManifest) metadata;
					for(RawDataRecord seeAlso: manifest.getMetadata().getSeeAlso()) {
//						if(seeAlso.getProfile()!=null && seeAlso.getProfile().equalsIgnoreCase("http://www.europeana.eu/schemas/edm/")) {
//						} else {//TODO
							jsonldString = new String(seeAlso.getContent(), "UTF8");
							jsonldString = jsonldString.replaceFirst("@context\":\"http://schema.org\"", "@context\":\"http://schema.org/\"");
//						}
//						throw new RuntimeException("TODO");
					} 
				}
				
				RawDataRecord seeAlso=new RawDataRecord();
				seeAlso.setUrl(dbEntry.getKey().toString());
				seeAlso.setContent(jsonldString.getBytes(UTF8));
				seeAlso.setContentType("application/json");
				seeAlso.setFormat("application/ld+json");
				RawDataRecord convertedEdm = schemaOrgToEdmConverter.convert(seeAlso, null);
				handler.handle(dbEntry.getKey().toString(), convertedEdm.getContent());
			} catch (Exception e) {
				System.err.println(metadata);
				e.printStackTrace();
			}
			if(cnt % 1000 == 0) 
				System.out.println("Progress: "+ cnt+" records");
		}
		System.out.println(cnt + " records exported");
		db.shutdown();
		handler.finish();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}


}
