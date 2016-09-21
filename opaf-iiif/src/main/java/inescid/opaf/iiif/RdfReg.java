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
	public static final Property IIIF_PROFiLE_DOAP_IMPLEMENTS = ResourceFactory.createProperty("http://usefulinc.com/ns/doap#implements");
	public static final Property RDFS_LABEL = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
	public static final Property RDF_VALUE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
	public static final Property DCTERMS_HAS_FORMAT = ResourceFactory.createProperty("http://purl.org/dc/terms/hasFormat");
	public static final Property DC_FORMAT = ResourceFactory.createProperty("http://purl.org/dc/elements/1.1/format");
	public static final Property IIIF_NAV_DATE = ResourceFactory.createProperty("http://iiif.io/api/presentation/2#presentationDate");
}