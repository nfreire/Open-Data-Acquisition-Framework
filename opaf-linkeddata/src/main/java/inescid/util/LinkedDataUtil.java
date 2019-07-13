package inescid.util;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.fluent.Content;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.Lang;

import inescid.opaf.framework.UrlRequest;
import inescid.opaf.framework.ld.harvester.HttpRequest;
import inescid.opaf.framework.ld.harvester.LdGlobals;

public class LinkedDataUtil {

	public static Resource getResource(String resourceUri) throws RdfResourceAccessException, InterruptedException {
		Model dsModel = readRdf(getResourceRdfBytes(resourceUri));
		Resource resource = dsModel.getResource(resourceUri);
		if(resource==null)
			throw new RdfResourceAccessException(resourceUri, "Response to dataset RDF resource did not contain a RDF description of the resource");
		return resource;
	}
	private static Content getResourceRdfBytes(String resourceUri) throws RdfResourceAccessException, InterruptedException {
		UrlRequest ldReq=new UrlRequest(resourceUri, "Accept", "application/rdf+xml, application/ld+json, application/x-turtle, text/turtle");
		HttpRequest rdfResourceRequest = new HttpRequest(ldReq);
		try {
			LdGlobals.httpRequestService.fetch(rdfResourceRequest);
			if (rdfResourceRequest.getResponseStatusCode() != 200) 
				throw new RdfResourceAccessException(resourceUri, rdfResourceRequest.getResponseStatusCode());
			return rdfResourceRequest.getContent();
		} catch (IOException e) {
			throw new RdfResourceAccessException(resourceUri, e);
		}
	}
	
	private static Model readRdf(Content content) {
		Model model = null;
		Lang rdfLang=null;
		if (content.getType()!=null && content.getType().getMimeType()!=null){
			rdfLang=RdfUtil.fromMimeType(content.getType().getMimeType());
			if (rdfLang!=null) {
	//		byte[] fileBytes = FileUtils.readFileToByteArray(schemaorgFile);
				model = ModelFactory.createDefaultModel();
				model.read(content.asStream(), null, rdfLang.getName());
			} else {
				for(Lang l: new Lang[] { Lang.RDFXML, Lang.TURTLE, Lang.JSONLD}) {
					try {
						model = ModelFactory.createDefaultModel();
						model.read(content.asStream(), null, rdfLang.getName());
						break;
					} catch (Exception e){
						//ignore and try another reader
					}
				}
			}
		}
		return model;
	}

	public static Resource getAndStoreResource(String resourceUri, File storeFile) throws IOException, InterruptedException, RdfResourceAccessException {
		Content resourceRdfBytes = getResourceRdfBytes(resourceUri);
		Model dsModel = readRdf(resourceRdfBytes);
		FileUtils.writeByteArrayToFile(storeFile, resourceRdfBytes.asBytes(), false);		
		Resource resource = dsModel.getResource(resourceUri);
		if(resource==null)
			throw new RdfResourceAccessException(resourceUri, "Response to dataset RDF resource did not contain a RDF description of the resource");
		return resource;
	}
}
