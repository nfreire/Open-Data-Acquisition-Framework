package inescid.opaf.data;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

public final class RdfReg {
	public static String NsEdm="http://www.europeana.eu/schemas/edm/";
	public static String NsRdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String NsXml="http://www.w3.org/XML/1998/namespace";
	public static String NsRdfs="http://www.w3.org/2000/01/rdf-schema#";
	public static String NsDc="http://purl.org/dc/elements/1.1/";
	public static String NsDcterms="http://purl.org/dc/terms/";
	public static String NsOre="http://www.openarchives.org/ore/terms/";
	public static String NsIiif="http://iiif.io/api/presentation/2#";
	public static String NsSkos="http://www.w3.org/2004/02/skos/core#";
	public static String NsCc="http://creativecommons.org/ns#";
	public static String NsSvcs="http://rdfs.org/sioc/services#";
	public static String NsDoap="http://usefulinc.com/ns/doap#";
	public static String NsWgs84="http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static String NsOwl="http://www.w3.org/2002/07/owl#";
	public static String NsRdaGr2="http://rdvocab.info/ElementsGr2/"; 
	public static String NsFoaf="http://xmlns.com/foaf/0.1/";
	
	public static final Property RDF_TYPE=ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
	public static final Property RDFS_SEE_ALSO=ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
	public static final Resource RDFS_RESOURCE=ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#Resource");
	public static final Resource IIIF_MANIFEST = ResourceFactory.createResource("http://iiif.io/api/presentation/2#Manifest");
	public static final Resource EDM_PROVIDED_CHO = ResourceFactory.createResource("http://www.europeana.eu/schemas/edm/ProvidedCHO");
	public static final Resource EDM_PLACE = ResourceFactory.createResource("http://www.europeana.eu/schemas/edm/Place");
	public static final Resource EDM_AGENT = ResourceFactory.createResource("http://www.europeana.eu/schemas/edm/Agent");
	public static final Resource EDM_TIMESPAN = ResourceFactory.createResource("http://www.europeana.eu/schemas/edm/TimeSpan");
	public static final Resource IIIF_COLLECTION = ResourceFactory.createResource("http://iiif.io/api/presentation/2#Collection");
	public static final Resource ORE_AGGREGATION = ResourceFactory.createResource("http://www.openarchives.org/ore/terms/Aggregation");
	public static final Property IIIF_METADATA_LABELS = ResourceFactory.createProperty("http://iiif.io/api/presentation/2#metadataLabels");
	public static final Property IIIF_PROFiLE_DOAP_IMPLEMENTS = ResourceFactory.createProperty("http://usefulinc.com/ns/doap#implements");
	public static final Property RDFS_LABEL = ResourceFactory.createProperty("http://www.w3.org/2000/01/rdf-schema#label");
	public static final Property RDF_VALUE = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#value");
	public static final Property DCTERMS_HAS_FORMAT = ResourceFactory.createProperty("http://purl.org/dc/terms/hasFormat");
	public static final Property DC_FORMAT = ResourceFactory.createProperty("http://purl.org/dc/elements/1.1/format");
	public static final Property IIIF_NAV_DATE = ResourceFactory.createProperty("http://iiif.io/api/presentation/2#presentationDate");
	public static final Property IIIF_HAS_SEQUENCES = ResourceFactory.createProperty("http://iiif.io/api/presentation/2#hasSequences");
	public static final Property RDF_FIRST = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
	public static final Property IIIF_HAS_CANVASES = ResourceFactory.createProperty("http://iiif.io/api/presentation/2#hasCanvases");
	public static final Property IIIF_HAS_IMAGE_ANNOTATIONS = ResourceFactory.createProperty("http://iiif.io/api/presentation/2#hasImageAnnotations");
	public static final Property OA_HAS_BODY = ResourceFactory.createProperty("http://www.w3.org/ns/oa#hasBody");
	public static final Property SVCS_HAS_SERVICE = ResourceFactory.createProperty("http://rdfs.org/sioc/services#has_service");
	public static final Property DCTERMS_RIGHTS = ResourceFactory.createProperty("http://purl.org/dc/terms/rights");
	public static final Property DCTERMS_RELATION = ResourceFactory.createProperty("http://purl.org/dc/terms/relation");
	public static final Property DCTERMS_IS_REFERENCED_BY = ResourceFactory.createProperty("http://purl.org/dc/terms/isReferencedBy");
	public static final Property EDM_IS_SHOWN_BY = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/isShownBy");
	public static final Property EDM_IS_SHOWN_AT = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/isShownAt");
	public static final Property EDM_OBJECT = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/object");
	public static final Property EDM_AGGREGATED_CHO  = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/aggregatedCHO");
	public static final Property EDM_RIGHTS  = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/rights");
	public static final Resource EDM_WEB_RESOURCE = ResourceFactory.createResource("http://www.europeana.eu/schemas/edm/WebResource");
	public static final Resource EDM_EVENT = ResourceFactory.createResource("http://www.europeana.eu/schemas/edm/Event");
	public static final Resource EDM_PHYSICAL_THING = ResourceFactory.createResource("http://www.europeana.eu/schemas/edm/PhysicalThing");
	public static final Resource SKOS_CONCEPT = ResourceFactory.createResource("http://www.w3.org/2004/02/skos/core#Concept");
	public static final Resource SKOS_CONCEPT_SCHEME = ResourceFactory.createResource("http://www.w3.org/2004/02/skos/core#ConceptScheme");
	public static final Resource CC_LICENSE  = ResourceFactory.createResource("http://creativecommons.org/ns#License");
	public static final Resource SVCS_SERVICE  = ResourceFactory.createResource("http://rdfs.org/sioc/services#Service");
	public static final Property EDM_PROVIDER = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/provider");
	public static final Property EDM_DATA_PROVIDER = ResourceFactory.createProperty("http://www.europeana.eu/schemas/edm/dataProvider");
	public static final Property DCTERMS_CONFORMS_TO = ResourceFactory.createProperty("http://purl.org/dc/terms/conformsTo");
	public static final Resource SCHEMAORG_CREATIVE_WORK  = ResourceFactory.createResource("http://schema.org/CreativeWork");
	
}