package inescid.opaf.iiif;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

public class TEST {
	public static void main(String[] args) throws Exception {
		Model modelRdf = ModelFactory.createDefaultModel();
		RDFDataMgr.read(modelRdf, new FileInputStream("c:/users/nuno/desktop/ivrla-29884"), Lang.JSONLD);
	}
}
