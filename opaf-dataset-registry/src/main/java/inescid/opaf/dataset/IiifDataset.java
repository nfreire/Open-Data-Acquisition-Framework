package inescid.opaf.dataset;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

public class IiifDataset extends Dataset {
	public enum IiifCrawlMethod {COLLECTION, SITEMAP, AS20, AUTO};
	
	String uri;
	IiifCrawlMethod crawlMethod=IiifCrawlMethod.AUTO;
	
	public IiifDataset(String uri) {
		super(DatasetType.IIIF);
		this.uri=uri;
	}


	public IiifDataset() {
		super(DatasetType.LOD);
	}


	@Override
	public String toCsv() {
		try {
			StringBuilder sb=new StringBuilder();
			CSVPrinter rec=new CSVPrinter(sb, CSVFormat.DEFAULT);
			rec.printRecord(localId, type.toString(), organization, title, uri, crawlMethod);
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


	public IiifCrawlMethod getCrawlMethod() {
		return crawlMethod;
	}


	public void setCrawlMethod(IiifCrawlMethod crawlMethod) {
		this.crawlMethod = crawlMethod;
	}

	
}
