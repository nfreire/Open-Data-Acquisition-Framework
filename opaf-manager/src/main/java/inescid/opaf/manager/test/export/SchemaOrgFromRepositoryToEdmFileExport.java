package inescid.opaf.manager.test.export;

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

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.rdf.converter.deprecated.DataConversionManager;
import inescid.opaf.data.convert.rdf.converter.deprecated.DataConverter;
import inescid.opaf.data.convert.rdf.converter.deprecated.SchemaOrgToEdmDataConverter;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.manager.test.EdmFileExport;

public class SchemaOrgFromRepositoryToEdmFileExport {

	static final Charset UTF8=Charset.forName("UTF8");
	static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
	public void run(File repositoryFolder, File exportFolder, boolean transformToEdmInternal, final int maxExportRecords, String provider, String dataProvider) throws Exception {
		File dbFile = new File(repositoryFolder, "db.bin");
		if(!dbFile.exists()) {
			System.out.println("Repository is empty. Exiting.");
			return;
		}		
		System.out.println(new Date(dbFile.lastModified()));
		
		final int maxExportRecordsPerFile=300;
		if(!exportFolder.exists())
			exportFolder.mkdirs();
		
		Database db;
		db=new Database(repositoryFolder, AccessMode.READ_ONLY);
		EdmFileExport metadataExporter=new EdmFileExport(maxExportRecordsPerFile, exportFolder);
		metadataExporter.setTransformToEdmInternal(transformToEdmInternal); 
		metadataExporter.init();
		
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
						if(seeAlso.getProfile()!=null && seeAlso.getProfile().equalsIgnoreCase("http://www.europeana.eu/schemas/edm/")) {
							String fromByteArray = new String(seeAlso.getContent(), "UTF-8");
							metadataExporter.export(fromByteArray);
							if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
								break;
						} else {//TODO
							jsonldString = new String(seeAlso.getContent());
						}
					} 
				}
				System.out.println(jsonldString);
				
				RawDataRecord seeAlso=new RawDataRecord();
				seeAlso.setUrl(dbEntry.getKey().toString());
				seeAlso.setContent(jsonldString.getBytes(UTF8));
				seeAlso.setContentType("application/json");
				seeAlso.setFormat("application/ld+json");
				RawDataRecord convertedEdm = schemaOrgToEdmConverter.convert(seeAlso, null);
				String fromByteArray = new String(convertedEdm.getContent(), "UTF-8");
				metadataExporter.export(fromByteArray);
				
				String baseFilename=URLEncoder.encode(dbEntry.getKey().toString(), "UTF-8");
				FileUtils.writeByteArrayToFile(new File(exportFolder, baseFilename+".xml"), convertedEdm.getContent());
				FileUtils.writeByteArrayToFile(new File(exportFolder, baseFilename+".jsonld"), seeAlso.getContent());
				 
				if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
					break;
			} catch (Exception e) {
				System.err.println(metadata);
				e.printStackTrace();
			}
			if(cnt % 1000 == 0) 
				System.out.println("Progress: "+ cnt+" records ("+metadataExporter.getExportRecordsCount() + " exported)");
		}
		System.out.println(metadataExporter.getExportRecordsCount() + " records exported");
		metadataExporter.close();
		db.shutdown();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}


}
