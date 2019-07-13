package inescid.opaf.dataset;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class LodDataset extends Dataset {
	
	String uri;
	
	public LodDataset(String uri) {
		super(DatasetType.LOD);
		this.uri=uri;
	}


	public LodDataset() {
		super(DatasetType.LOD);
	}


	@Override
	public String toCsv() {
		try {
			StringBuilder sb=new StringBuilder();
			CSVPrinter rec=new CSVPrinter(sb, CSVFormat.DEFAULT);
			rec.printRecord(localId, type.toString(), organization, title, uri);
			rec.close();
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}



	public String getUri() {
		return uri;
	}



	public void setUri(String uri) {
		this.uri = uri;
	}

}
