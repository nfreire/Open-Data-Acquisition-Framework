package inescid.opaf.framework.ld.casestudies;

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

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.EdmRdfToXmlSerializer;
import inescid.opaf.data.convert.rdf.DataConversionManager;
import inescid.opaf.data.convert.rdf.DataConversionManager.DataProfile;
import inescid.opaf.framework.ld.harvester.LdGlobals;
import inescid.opaf.data.convert.rdf.RdfDeserializer;
import inescid.opaf.data.convert.rdf.SchemaOrgToEdmDataConverter;
import inescid.util.EdmFileExport;
import inescid.util.XmlUtil;

public class SchemaOrgFromRepositoryToEdmFileExport {

	static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
	boolean transformToEdmInternal;
	String provider;
	String dataProvider;
	
	inescid.opaf.data.convert.rdf.DataConverter schemaOrgToEdmConverter;

	public SchemaOrgFromRepositoryToEdmFileExport(boolean transformToEdmInternal,
			String provider, String dataProvider) throws Exception {
		super();
		this.transformToEdmInternal = transformToEdmInternal;
		this.provider = provider;
		this.dataProvider = dataProvider;

		schemaOrgToEdmConverter = DataConversionManager.getInstance().getConverter(DataProfile.SCHEMA_ORG, DataProfile.EDM);
//				new DataSpec("application/ld+json", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "http://schema.org/"), new DataSpec("application/xml", "http://edm", null));
		((SchemaOrgToEdmDataConverter)schemaOrgToEdmConverter).setDataProvider(dataProvider);
		((SchemaOrgToEdmDataConverter)schemaOrgToEdmConverter).setProvider(provider);
	}



	public void export(String datasetUri, File exportFolder, final int maxExportRecords) throws Exception {
		final int maxExportRecordsPerFile=300;
		if(!exportFolder.exists())
			exportFolder.mkdirs();
		EdmFileExport metadataExporter=new EdmFileExport(maxExportRecordsPerFile, exportFolder);
		metadataExporter.setTransformToEdmInternal(transformToEdmInternal); 
		metadataExporter.init();
		
		int cnt=0;
		for(Iterator<Entry<String, File>> it = LdGlobals.repository.getAllDatasetResourcesFiles(datasetUri).entrySet().iterator(); it.hasNext() ; ) {
			Entry<String, File> dbEntry=it.next();
			byte[] mdBytes=FileUtils.readFileToByteArray(dbEntry.getValue());
			cnt++;
			
			byte[] edm=getEdmRecord((String)dbEntry.getKey().replace("/doc/", "/id/"), mdBytes);
			if(edm!=null) {
				String fromByteArray = new String(edm, "UTF-8");
				metadataExporter.export(fromByteArray);
				
//				String baseFilename=URLEncoder.encode(dbEntry.getKey().toString(), "UTF-8");
//				FileUtils.writeByteArrayToFile(new File(exportFolder, baseFilename+".edm..xml"), edm);
				
				if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
					break;
				if(cnt % 1000 == 0) 
					System.out.println("Progress: "+ cnt+" records ("+metadataExporter.getExportRecordsCount() + " exported)");
			}
		}
		System.out.println(metadataExporter.getExportRecordsCount() + " records exported");
		metadataExporter.close();
	}

	private byte[] getEdmRecord(String resUri, byte[] mdBytes) {
		try {
			Model fromRdfXml = RdfDeserializer.fromRdfXml(mdBytes, resUri);
			Resource mainTargetResource = schemaOrgToEdmConverter.convert(fromRdfXml.createResource(resUri), null);
			EdmRdfToXmlSerializer xmlSerializer = new EdmRdfToXmlSerializer(mainTargetResource);
			Document edmDom = xmlSerializer.getXmlDom();
			String domString = XmlUtil.writeDomToString(edmDom);
			return domString.getBytes(LdGlobals.charset);
		} catch (Exception e) {
			System.err.println(resUri);
			e.printStackTrace();
			return null;
		}
	}
	

}
