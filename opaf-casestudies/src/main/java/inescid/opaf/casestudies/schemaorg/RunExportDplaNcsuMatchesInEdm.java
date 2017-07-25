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
import org.apache.commons.lang3.tuple.Pair;
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

public class RunExportDplaNcsuMatchesInEdm {

	public static void main(String[] args) throws Exception {
//		File repositoryFolder=new File("C:\\Users\\nfrei\\Desktop\\iiif-crawl-repository-nlw-ingestion_1st");
		File exportFolder=new File("target/edm_schemaorg_comparison_ncsu");
		boolean transformToEdmInternal=false;
//		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin\\Poetry-2");
//		File repositoryFolder=new File("target/sitemaps-crawl-repository-ucd-poetry");
//		boolean transformToEdmInternal=true;
		final int maxExportRecords=100;

		if(!exportFolder.exists())
			exportFolder.mkdirs();
		
		SchemaOrgNcsuWebServiceToEdmFileExport repoExporter = new SchemaOrgNcsuWebServiceToEdmFileExport(transformToEdmInternal, "NCSU Libraries", "NCSU Libraries");
		Map<String, String> dplaToSourceIdMap=new HashMap<String, String>();
		CSVParser parser=new CSVParser(new FileReader("target/digitalnc.ids.csv"), CSVFormat.DEFAULT);
		for (CSVRecord rec: parser) {
			String recordNcsuId = rec.get(2);
			if(!recordNcsuId.startsWith("https://d.lib.ncsu.edu/collections/catalog/") && !recordNcsuId.startsWith("http://d.lib.ncsu.edu/collections/catalog/"))
				continue;
			if(recordNcsuId.startsWith("http://"))
				recordNcsuId="https://"+recordNcsuId.substring(7);
			Pair<byte[], byte[]> metadata=repoExporter.getRecordInSchemaOrgAndEdm(recordNcsuId);
			FileOutputStream fileOutputStream = new FileOutputStream(new File(exportFolder, URLEncoder.encode(recordNcsuId, "UTF8")+".schemaorg.jsonld"));
			IOUtils.write(metadata.getLeft(), fileOutputStream);
			fileOutputStream.close();
			
			fileOutputStream = new FileOutputStream(new File(exportFolder, URLEncoder.encode(recordNcsuId, "UTF8")+".edm.xml"));
			IOUtils.write(metadata.getRight(), fileOutputStream);
			fileOutputStream.close();
			dplaToSourceIdMap.put(rec.get(0), recordNcsuId);
			if(maxExportRecords>0 && maxExportRecords<=dplaToSourceIdMap.size())
				break;
		}
		parser.close();

		
		
		File fromJson=new File("src/data/digitalnc.json.gz");
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
