package inescid.opaf.manager.test.profile;

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
import inescid.opaf.manager.test.RepositoryRdfDataUsageProfilerSchemaorgEdm;
import inescid.util.XmlUtil;

public class RunProfileRdfDataUsageLd4sc {

	
	public static void main(String[] args) throws Exception {
		File repositoryFolder=new File("src/data/firstSample/www-crawl-repository-ld4sc");
//		final int maxProfiledRecords=-100;		
		final int maxProfiledRecords=1000;		
		new RepositoryRdfDataUsageProfilerSchemaorgEdm().run(repositoryFolder, maxProfiledRecords, "University of Illinois at Urbana–Champaign");
	}
}
