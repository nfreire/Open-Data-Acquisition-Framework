package inescid.opaf.casestudies.schemaorg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.util.XmlUtil;

public class RunExportEdmToFileNcsu {

	private static final Charset UTF8=Charset.forName("UTF8");
	private static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
	public static void main(String[] args) throws Exception {
		File repositoryFolder=new File("target/iiif-crawl-repository-ncsu_bck");
//		File repositoryFolder=new File("C:\\Users\\nfrei\\Desktop\\iiif-crawl-repository-nlw-ingestion_1st");
		File exportFolder=new File("target/iiif-crawl-repository-ncsu/export");
		boolean transformToEdmInternal=false;
//		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin\\Poetry-2");
//		File repositoryFolder=new File("target/sitemaps-crawl-repository-ucd-poetry");
//		boolean transformToEdmInternal=true;
		final int maxExportRecords=-100;
		
		new SchemaOrgFromRepositoryToEdmFileExport(repositoryFolder, transformToEdmInternal, "NC State University Libraries", "NC State University Libraries").export(exportFolder, maxExportRecords);
	}
}
