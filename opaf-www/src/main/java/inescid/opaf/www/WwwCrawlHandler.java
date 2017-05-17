package inescid.opaf.www;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.any23.Any23;
import org.apache.any23.extractor.ExtractionException;
import org.apache.any23.extractor.ExtractorGroup;
//import org.apache.any23.extractor.html.EmbeddedJSONLDExtractorFactory;
import org.apache.any23.writer.NTriplesWriter;
import org.apache.any23.writer.TripleHandlerException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.FetchRequest;
import inescid.opaf.framework.ResponseHandler;

public abstract class WwwCrawlHandler implements ResponseHandler {
	private static final Logger log = LoggerFactory.getLogger(WwwCrawlHandler.class);
	
	CrawlingSession session;
	Any23 any23=new Any23(new ExtractorGroup(new ArrayList() {{ 
//		add(new MicrodataExtractorFactory());
		add(new EmbeddedJSONLDExtractorFactory());
	}}));
	
	public WwwCrawlHandler() {
		super();
	}

	public void setSession(CrawlingSession session) {
		this.session = session;
	}
	@Override
	public void handle(FetchRequest fetched) {
		try {
			int statusCode = fetched.getResponse().getStatusLine().getStatusCode();
			if(statusCode==200) {
				fetched.getContent().asString();				
				try {
					String charset = "UTF8";
					if (fetched.getContent().getType().getCharset()!=null)
						charset = fetched.getContent().getType().getCharset().name();
					
//					Jsoup.parse(html)
					ByteArrayOutputStream decodedInput = new ByteArrayOutputStream();
					NTriplesWriter triples=new NTriplesWriter(decodedInput);
//					NTriplesWriter triples=new NTriplesWriter(decodedInput);
					any23.extract(fetched.getContent().asString(), fetched.getUrl(), fetched.getContent().getType().getMimeType(), charset, 
							triples);			
//							new CountingTripleHandler() {
//						@Override
//						public void receiveTriple(Resource arg0, IRI arg1, Value arg2, IRI arg3, ExtractionContext arg4)
//								throws TripleHandlerException {
//						System.out.println("Triple: "+ arg0.toString());
//						System.out.println("1 : "+ arg1.toString());
//						System.out.println("2: "+ arg2.toString());
//						System.out.println("3: "+ arg3);
//						}});
					triples.close();
					decodedInput.close();
					Model model = ModelFactory.createDefaultModel();
					model.read(new ByteArrayInputStream(decodedInput.toByteArray()), null, "N-TRIPLE");

					handleUrl(model, fetched.getUrl());
				} catch (ExtractionException e) {
					handleError(e, fetched);
				} catch (TripleHandlerException e) {
					handleError(e, fetched);
				} catch (Exception e) {
					log.debug(fetched.getContent().asString());
					handleError(e, fetched);
				}
			} else if(statusCode==304) {
				log.info("not modified, skipped: "+fetched.getUrl());
			} else {
				handleError(fetched);
			}
		} catch (IOException e) {
			handleError(e, fetched);
		}
	}

	public abstract void handleUrl(Model model, String url) throws Exception;

	protected void handleError(Throwable e, FetchRequest respondedFetchRequest) {
		if(respondedFetchRequest==null)
			log.error(e.getMessage(), e);
		else {
			log.error(respondedFetchRequest.getUrl(), e);
		}
	}
	protected void handleError(FetchRequest respondedFetchRequest) {
		log.error(respondedFetchRequest.getUrl()+" - HTTP Status: "+respondedFetchRequest.getResponseStatusCode());
	}
	
	@Override
	public void close() {
	}
}