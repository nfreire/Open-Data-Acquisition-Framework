package inescid.opaf.iiif;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public final class RdfReg {
	public static final Property RDF_TYPE=ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	public static final Property RDFS_SEE_ALSO=ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
	public static final Resource IIIF_MANIFEST = ResourceFactory.createResource("http://iiif.io/api/presentation/2#Manifest");
	public static final Property IIIF_METADATA_LABELS = ResourceFactory.createProperty("http://iiif.io/api/presentation/2#metadataLabels");
	public static final Property RDFS_LABEL = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
	public static final Property RDF_VALUE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
}