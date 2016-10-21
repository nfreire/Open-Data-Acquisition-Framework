package inescid.util;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;

import inescid.opaf.iiif.IiifMetadataElement;
import inescid.opaf.iiif.LocalizedLiteral;
import inescid.opaf.iiif.RdfReg;

public class RdfUtil {
	public static Resource findResource(Resource startResource, Property... propertiesToFollow) {
		Resource curRes=startResource;
		for(int i=0; i<propertiesToFollow.length; i++) {
			Statement propStm = curRes.getProperty(propertiesToFollow[i]);
			if(propStm==null)
				return null;
			curRes=(Resource) propStm.getObject();
		}
		return curRes;
	}
}
