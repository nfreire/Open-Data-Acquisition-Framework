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
import inescid.opaf.manager.test.RepositoryExporter;
import inescid.opaf.manager.test.RepositoryRdfDataUsageProfilerSchemaorgEdm;
import inescid.util.XmlUtil;

public class RunExportRepositoryNcsu {
	public static void main(String[] args) throws Exception {
		File repositoryFolder=new File("target/iiif-crawl-repository-ncsu");
		File exportFolder=new File("target/iiif-crawl-repository-ncsu/export");
		if(!exportFolder.exists())
			exportFolder.mkdirs();
		final int maxExportedRecords=100;		
		new RepositoryExporter().run(repositoryFolder, exportFolder, "jsonld", maxExportedRecords);
	}
}
