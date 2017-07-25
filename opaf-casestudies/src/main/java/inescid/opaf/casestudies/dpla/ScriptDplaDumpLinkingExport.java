package inescid.opaf.casestudies.dpla;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import inescid.opaf.casestudies.dpla.DplaDumpJsonReader.JsonRecordHandler;

public class ScriptDplaDumpLinkingExport {


	File jsonFile;
	DplaDumpJsonReader.JsonRecordHandler handler;

	public ScriptDplaDumpLinkingExport(File jsonFile, DplaDumpJsonReader.JsonRecordHandler handler) {
		super();
		this.jsonFile = jsonFile;
		this.handler = handler;
	}


	
	private static void exportIds(File fromJson, File toCsv, int recCntLimit) throws IOException {
		System.out.println("Starting "+fromJson.getName());
		FileWriter writer=new FileWriter(toCsv);
		CSVPrinter writerCsv=new CSVPrinter(writer, CSVFormat.DEFAULT);
		
		DplaDumpJsonReader reader = new DplaDumpJsonReader(fromJson, new JsonRecordHandler() {
			int recCnt = 0;
			@Override
			public boolean handle(JsonNode recNode) {
				try {
					recCnt++;
					JsonNode sourceNode = recNode.get("_source");
					JsonNode titleNode = sourceNode.get("sourceResource").get("title");
					if(titleNode!=null) {
						titleNode=titleNode.isArray() ? titleNode.elements().next() : titleNode;
					}
//					JsonNode sourceResourceNode = sourceNode.get("sourceResource").get("title");
					
					
	//				String id=sourceNode.get("@id").asText();
					String id=sourceNode.get("id").asText();
					JsonNode isShownAt=sourceNode.get("isShownAt");	
					if(isShownAt!=null)
						isShownAt=isShownAt.isArray() ? isShownAt.elements().next() : isShownAt;
					JsonNode isShownBy=sourceNode.get("isShownBy");
					if(isShownBy!=null)
						isShownBy=isShownBy.isArray() ? isShownBy.elements().next() : isShownBy;
//					String csv=String.format("%s,\"%s\",\"%s,\"%s\"\n", id, isShownBy==null ? "" : isShownBy.asText()
//							, isShownAt==null ? "" : isShownAt.asText(), titleNode==null ? "" : titleNode.asText().replaceAll("\"", "\\\""));
//						writer.write(csv);
						writerCsv.printRecord(id, isShownBy==null ? "" : isShownBy.asText()
								, isShownAt==null ? "" : isShownAt.asText(), titleNode==null ? "" : titleNode.asText());
					if(recCnt % 100 ==0)
						System.out.println(recCnt);
					return recCntLimit<=0 || recCnt < recCntLimit;
				} catch (IOException e) {
					e.printStackTrace();
					return true;
				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
			}
		});
		reader.parse();

		
		writer.close();
		writerCsv.close();
	}	
	public static void main(String[] args) throws IOException {
		int recCntLimit=0;
		exportIds(new File("src/data/uiuc.json.gz"), new File("target/uiuc.ids.csv"), recCntLimit);
		exportIds(new File("src/data/digitalnc.json.gz"), new File("target/digitalnc.ids.csv"), recCntLimit);
//		DplaDumpJsonReader reader = new DplaDumpJsonReader(new File("src/data/uiuc.json.gz"), new JsonRecordHandler() {
		
	}
}

//			ObjectMapper mapper = new ObjectMapper();
//				for(Iterator<String> i=sourceNode.fieldNames(); i.hasNext() ;) {
//					String name=i.next(); 
//					System.out.print(name);
//					System.out.println(sourceNode.textValue());
//					System.out.println(sourceNode.getNodeType());
//				}
//				try {
//					System.out.println(mapper.writeValueAsString(source));
//				} catch (JsonProcessingException e) {
//					e.printStackTrace();
//				}