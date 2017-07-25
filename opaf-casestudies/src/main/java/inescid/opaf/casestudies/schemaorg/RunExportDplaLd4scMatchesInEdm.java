package inescid.opaf.casestudies.schemaorg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import inescid.opaf.casestudies.dpla.DplaDumpJsonReader;
import inescid.opaf.casestudies.dpla.DplaDumpJsonReader.JsonRecordHandler;
import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.util.XmlUtil;

public class RunExportDplaLd4scMatchesInEdm {

	public static void main(String[] args) throws Exception {
		File repositoryFolder=new File("../opaf-manager/target/www-crawl-repository-ld4sc");
//		File repositoryFolder=new File("C:\\Users\\nfrei\\Desktop\\iiif-crawl-repository-nlw-ingestion_1st");
		File exportFolder=new File("target/edm_schemaorg_comparison_ld4sc");
		boolean transformToEdmInternal=false;
//		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin\\Poetry-2");
//		File repositoryFolder=new File("target/sitemaps-crawl-repository-ucd-poetry");
//		boolean transformToEdmInternal=true;
		final int maxExportRecords=100;

		if(!exportFolder.exists())
			exportFolder.mkdirs();
		
		SchemaOrgFromRepositoryToEdmFileExport repoExporter = new SchemaOrgFromRepositoryToEdmFileExport(repositoryFolder, transformToEdmInternal, "University of Illinois at Urbana–Champaign", "University of Illinois at Urbana–Champaign");
		Map<String, String> dplaToSourceIdMap=new HashMap<String, String>();
		CSVParser parser=new CSVParser(new FileReader("target/ld4sc_dpla_matches.csv"), CSVFormat.DEFAULT);
		for (CSVRecord rec: parser) {
			String schemaOrg=repoExporter.getRecordInSchemaOrg(rec.get(1));
			FileOutputStream fileOutputStream = new FileOutputStream(new File(exportFolder, URLEncoder.encode(rec.get(1), "UTF8")+".schemaorg.jsonld"));
			IOUtils.write(schemaOrg, fileOutputStream, "UTF8");
			fileOutputStream.close();
			
			byte[] edmBytes=repoExporter.getRecord(rec.get(1));
			fileOutputStream = new FileOutputStream(new File(exportFolder, URLEncoder.encode(rec.get(1), "UTF8")+".edm.xml"));
			IOUtils.write(edmBytes, fileOutputStream);
			fileOutputStream.close();
			dplaToSourceIdMap.put(rec.get(0), rec.get(1));
			if(maxExportRecords>0 && maxExportRecords<=dplaToSourceIdMap.size())
				break;
		}
		parser.close();

		File fromJson=new File("src/data/uiuc.json.gz");
		ObjectMapper jsonMapper = new ObjectMapper();
		DplaDumpJsonReader reader = new DplaDumpJsonReader(fromJson, new JsonRecordHandler() {
			@Override
			public boolean handle(JsonNode recNode) {
				try {
//					System.out.println(jsonMapper.writeValueAsString(recNode));
					JsonNode sourceNode = recNode.get("_source");
					String id=sourceNode.get("id").asText();
					String idAtSource = dplaToSourceIdMap.get(id);
					if(idAtSource!=null) {
	//					JsonNode sourceRes = sourceNode.get("sourceResource");
						byte[] edmBytes=jsonMapper.writeValueAsBytes(sourceNode);
						FileOutputStream fileOutputStream = new FileOutputStream(new File(exportFolder, URLEncoder.encode(idAtSource, "UTF8")+"_dpla.json"));
						IOUtils.write(edmBytes, fileOutputStream);
						fileOutputStream.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
				return true;
			}
		});
		reader.parse();
	}
}
