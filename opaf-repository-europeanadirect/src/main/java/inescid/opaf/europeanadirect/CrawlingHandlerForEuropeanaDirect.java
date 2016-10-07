package inescid.opaf.europeanadirect;

import java.util.Date;
import java.util.concurrent.Semaphore;

import org.apache.jena.rdf.model.Resource;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.europeana.europeanadirect.model.Object;
import inescid.opaf.data.repository.europeanadirect.IiifPresentationMetadataConverterToDirectObject;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.ManifestCrawlHandler;

public class CrawlingHandlerForEuropeanaDirect extends ManifestCrawlHandler {
	RecordHandler recordHandler;
	private static ObjectMapper mapper = new ObjectMapper(); 
	boolean exit=false;
	public CrawlingHandlerForEuropeanaDirect() {
		super();

	}
	
	@Override
	public void close() {
	}

	@Override
	protected void handleMetadata(IiifPresentationMetadata metadata) throws Exception {
		Object directMd = IiifPresentationMetadataConverterToDirectObject.convert(metadata, "en");
		String jsonString=mapper.writeValueAsString(directMd);
		synchronized (recordHandler) {
			if(exit)
				return;
			exit=exit || !recordHandler.handleRecord(jsonString);
		}
	}

	public RecordHandler getRecordHandler() {
		return recordHandler;
	}

	public void setRecordHandler(RecordHandler recordHandler) {
		this.recordHandler = recordHandler;
	}

}
