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

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.profile.UsageProfiler;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.manager.test.RepositoryRdfDataUsageProfilerSchemaorgEdm;
import inescid.util.XmlUtil;

public class RunProfileRdfDataUsageLd4sc {

	
	public static void main(String[] args) throws Exception {
		RepositoryRdfDataUsageProfilerSchemaorgEdm profileRunner = null;
		int maxProfiledRecords=1000;		
		maxProfiledRecords=370;		

		profileRunner = new RepositoryRdfDataUsageProfilerSchemaorgEdm();
		{
			File repositoryFolder=new File("work_in_progress/www-crawl-repository-ld4sc");
			profileRunner.run(repositoryFolder, maxProfiledRecords, "University of Illinois at Urbana–Champaign");
		}
		UsageProfiler schemaorgStatsLd4sc = profileRunner.getSchemaorgProfile();
		UsageProfiler edmStatsLd4sc = profileRunner.getEdmProfile();
//		System.out.println("LD4SC DATASET PROFILE SCHEMA.ORG:");
//		System.out.println(schemaorgStatsLd4sc.getUsageStats().toCsv());
		System.out.println("LD4SCDATASET PROFILE EDM:");
		System.out.println(edmStatsLd4sc.getUsageStats().toCsv());
		
		FileUtils.write(new File("target/profile_schemaorg_ld4sc.csv"), schemaorgStatsLd4sc.getUsageStats().toCsv(), "UTF-8");
		FileUtils.write(new File("target/profile_edm_ld4sc.csv"), edmStatsLd4sc.getUsageStats().toCsv(), "UTF-8");
	}
}
