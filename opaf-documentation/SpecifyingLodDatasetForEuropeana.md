# Specifying a LOD dataset for aggregation by Europeana

Cultural heritage institutions typically publish linked data that covers more resources than the cultural heritage digital objects provided to Europeana. Therefore, it is necessary that data providers make available linked data descriptions of the datasets for aggregation by Europeana.
Several vocabularies are available nowadays to describe datasets. Europeana supports three vocabularies which are suitable to fulfill the requirements for aggregation of linked data: [VoID](https://www.w3.org/TR/void/), [DCAT](https://www.w3.org/TR/vocab-dcat/), and [Schema.org](http://schema.org/Dataset).

Data providers may use classes and properties from any of the three vocabularies to describe each of their datasets. To enable Europeana to aggregate and ingest a dataset, the linked data resource of the dataset should follow the following points:
 - **Must** be accessible by its URI.
 - **Must** be encoded in RDF.
 - **Must** have a title property.
 - **Must** specify the technical mechanism that allows the dataset to be automatically harvested by Europeana.
 - **May** specify a machine readable license that applies to all metadata  

The sections bellow provide details on how of these points can be provided.

# Dataset RDF resource accessible by its URI

The description of the dataset in RDF must itself be published as linked open data. 
When ingesting the dataset in Europeana, the URI of the dataset must be provided to Europeana. It will function as the entry point for the Europeana LOD Harvester to reach all linked data descriptions of the the cultural heritage objects that belong in the dataset. 
The dataset description is used during the first ingestion of the dataset in to Europeana, and later, for incremental updates of the dataset.
The data provider must maintain the dataset description updated over time, to allow incremental updates of the dataset in Europeana. 

# Dataset resource encoded in a supported RDF format

The Europeana LOD Harvester accesses the RDF resource of the dataset by sending an HTTP request to the URI that includes the Accept header with the supported mime -types for RDF encoding. The response may use any of the supported encodings to send the RDF description of the dataset.
The following are the supported mime-types:

| Format | Mime-type | Specification
|--|--|--|
| RDF/XML | application/rdf+xml | https://www.w3.org/TR/rdf-syntax-grammar/
| JSON-LD | application/ld+son | [https://www.w3.org/TR/json-ld/](https://www.w3.org/TR/json-ld/) 
| Turtle | application/x-turtle | [https://www.w3.org/TR/turtle/](https://www.w3.org/TR/turtle/) |

# Title of the dataset

The RDF resource of the dataset must have a title, and the title may be provided in several languages. The titles should be provided by one of these properties: dc:title, dcterms:title,  schema:title. The language of the title should be represented in a xml:lang attribute of the title property.

# Specifying the technical mechanism for LOD harvesting

A LOD dataset for Europeana, is constituted, in its core, by RDF resources of the class edm:ProvidedCHO. In addition, a dataset contains all other resources used to describe the cultural object and aggregation metadata, as specified in the EDM (i.e. resources of types such as ore:Aggregation, edm:WebResource, edm:Agent, etc.).
All these resources will be harvested by Europeana's LOD harvester. The harvester will use the RDF description of the dataset to know which RDF resources to harvest and the mechanism to harvest them. 
Data providers may choose one of the mechanisms, typically used for LOD: 
 - Dataset distribution containing all data within the dataset.
 - Listing of the URIs of all ore:Aggregation resources within the dataset.
The mechanism that should be applied to a LOD dataset is indicated by the data provider in the properties of the RDF description of the dataset, using any of the supported vocabularies: [VoID](https://www.w3.org/TR/void/), [DCAT](https://www.w3.org/TR/vocab-dcat/), and [Schema.org](http://schema.org/Dataset).

## Option A - Specifying a downloadable dataset distribution 
All three vocabularies are capable of representing the required information for allowing Europeana to automatically obtain a dataset by  downloading a distribution containing all data within the dataset.
The following table points to the most relevant parts of the vocabularies that specify how a dataset distribution can be represented.

| Vocabulary| Specifications parts|
|--|--|
| VoID | See section "[3.3 RDF data dumps](https://www.w3.org/TR/void/#dumps)" describing the void:dataDump property. |
| DCAT | See section "[5.4 Class: Distribution](https://www.w3.org/TR/vocab-dcat/#class-distribution)", particularly the properties dcat:downloadURL and dcat:mediaType.
| Schema.org | see the definition of the property [schema:distribution](http://schema.org/distribution) of the [schema:Dataset](http://schema.org/Dataset) class.<br> see also the class [schema:DataDownload](http://schema.org/DataDownload) and its properties [schema:contentUrl](http://schema.org/contentUrl) and [schema:encodingFormat](http://schema.org/encodingFormat) |

For the requirements of Europeana, when using dataset distributions, data providers must follow the following points:
 - The files that constitute the data dump of the dataset, must contain the RDF data encoded in one the RDF encodings suported by Europeana:  [RDF/XML](https://www.w3.org/TR/rdf-syntax-grammar/), [JSON-LD](https://www.w3.org/TR/json-ld/)  or [Turtle](https://www.w3.org/TR/turtle/) 
- The files may be compressed. Currently, Europeana supports only the GZip compression algorithm.
- When using DCAT or Schema.org, the values of properties dcat:mediaType and schema:encodingFormat should only use mime-types supported by Europeana for RDF encoding: '*application/rdf+xml*', '*application/ld+son*', or '*application/x-turtle*'.
## Option B - Specifying a listing of URIs
Only the VoID](https://www.w3.org/TR/void/) vocabulary includes a property to indicate a RDF resource that lists all the resources within a dataset.
VoID defines the property void:rootResource, that may be used by Europeana data providers to provide this information. See section "[3.4 Root resources](https://www.w3.org/TR/void/#root-resource)" describing the  void:rootResource property, for the general use of the property.
For the requirements of Europeana, when using a listing of URIs, data providers must provide a void:rootResource that contains dcterms:hasPart properties with the URI’s of the cultural objects’s ore:Aggregations (EDM).
# Dataset level license
The RDF resource of the dataset may optionally indicate a license that applies to the whole dataset. If the dataset provides the licensing information, individual metadata records may still override it, by specifying a license as defined in EDM.
The license for a whole dataset should be specified in dcterms:license or schema:license (with Europeana supported licenses' URIs)

# Examples

    <?xml version="1.0"?>
    <rdf:RDF xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:dcat="http://www.w3.org/ns/dcat#"
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
      <rdf:Description rdf:about="http://example.org/dataset/children_books">
          <rdf:type rdf:resource="http://www.w3.org/ns/dcat#Dataset">
          <dcterms:title>Children books</dc:title>
          <dcat:distribution>
            <rdf:Description rdf:about="http://example.org/dataset_distribution/children_books/">
              <rdf:type rdf:resource="http://www.w3.org/ns/dcat#Distribution">
              <dcat:downloadURL rdf:resource="http://example.org/downloads/our_dataset_2018-April.xml.gz"/>
              <dcat:mediaType>application/rdf+xml</dcat:mediaType> 
            </rdf:Description>
          </dcat:distribution>
          <dcterms:license rdf:resource="http://creativecommons.org/publicdomain/mark/1.0/"/>
        </rdf:Description>



    <?xml version="1.0"?>
    <rdf:RDF xmlns:dcterms="http://purl.org/dc/terms/"
      xmlns:schema="http://schema.org/"
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
      <rdf:Description rdf:about="http://example.org/dataset/children_books">
          <rdf:type rdf:resource="http://schema.org/Dataset">
          <dcterms:title>Children books</dc:title>
          <schema:distribution>
            <rdf:Description rdf:about="http://example.org/dataset_distribution/children_books/">
              <rdf:type rdf:resource="http://schema.org/DataDownload">
              <schema:contentUrl rdf:resource="http://example.org/downloads/our_dataset_2018-April.xml.gz"/>
              <schema:mediaType>application/rdf+xml</dcat:mediaType> 
            </rdf:Description>
          </dcat:distribution>
          <dcterms:license rdf:resource="http://creativecommons.org/publicdomain/mark/1.0/"/>
        </rdf:Description>



<!--stackedit_data:
eyJoaXN0b3J5IjpbLTE3NDg2MTAyMDIsLTczNDk3ODA5OCwxNz
c1MDc0MTAsODkzNTU5OTUsLTI2OTc5MTM1MSwtMTA5NTE2OTcz
MywxMjQzMDYwOTUwLDE4NzA4Njg0ODAsMTk3NTIxNjA5OSw3Nj
gwODA4MjMsLTE0Mzc0NDM2MDQsLTExODM4NDIwOTQsLTE2NTk5
ODEzMCwxNTM2NDgxNDcsLTI0ODYwMDAyOSwtMTUyMzk4ODI3MS
wtMTA4ODkwMjgzOSw4NzE4OTIwNDAsLTE0NjM3NTA5NTQsLTk1
MzMyODIyM119
-->