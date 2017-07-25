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
import org.apache.commons.csv.CSVRecord;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DplaDumpJsonReader {

	public interface JsonRecordHandler {
		// return true to continue to next record; False to start the JSON
		// parsing
		public boolean handle(JsonNode recNode);
	}

	File jsonFile;
	JsonRecordHandler handler;

	public DplaDumpJsonReader(File jsonFile, JsonRecordHandler handler) {
		super();
		this.jsonFile = jsonFile;
		this.handler = handler;
	}

	public void parse() throws IOException {
		JsonFactory f = new MappingJsonFactory();
		InputStream jsonSourceInputStream = null;
		JsonParser jp = null;
		if (jsonFile.getName().endsWith(".gz") || jsonFile.getName().endsWith(".GZ")
				|| jsonFile.getName().endsWith(".gzip")) {
			FileInputStream jsonIs = new FileInputStream(jsonFile);
			jsonSourceInputStream = new GZIPInputStream(jsonIs);
			jp = f.createParser(jsonSourceInputStream);
		} else
			jp = f.createParser(jsonSourceInputStream);
		JsonToken current;
		current = jp.nextToken();
		if (current == JsonToken.START_ARRAY) {
			current=jp.nextToken();
			while (current != JsonToken.END_ARRAY) {
				if (current == JsonToken.START_OBJECT) {
					JsonNode node = jp.readValueAsTree();
					if (!handler.handle(node))
						break;
				}
				current=jp.nextToken();
			}
		}
		jsonSourceInputStream.close();
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