package inescid.opaf.manager.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.sound.midi.Soundbank;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.log4j.xml.XMLLayout;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.DataConversionManager;
import inescid.opaf.data.convert.DataConverter;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.IiifPresentationMetadata;

public class RunExportEdmToFileNlw {

	static final Charset UTF8=Charset.forName("UTF8");
	static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
	public static void main() throws Exception {
		File repositoryFolder=new File("C:\\Users\\nfrei\\Desktop\\iiif-crawl-repository-nlw-ingestion");
//		File repositoryFolder=new File("C:\\Users\\nfrei\\Desktop\\iiif-crawl-repository-nlw-ingestion_1st");
		File exportFolder=new File("target/sitemaps-crawl-repository-nlw-photographs");
		boolean transformToEdmInternal=false;
//		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin\\Poetry-2");
//		File repositoryFolder=new File("target/sitemaps-crawl-repository-ucd-poetry");
//		boolean transformToEdmInternal=true;
		final int maxExportRecords=-100;
		
		
		new EdmFromRepositoryToFileExport().run(repositoryFolder, exportFolder, transformToEdmInternal, maxExportRecords, null, null);
	}
}
