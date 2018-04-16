# Specifying a LOD dataset for aggregation by Europeana

Cultural heritage institutions typically publish linked data that covers more resources than the cultural heritage digital objects provided to Europeana. Therefore, it is necessary that data providers make available linked data descriptions of the datasets for aggregation by Europeana.
Several vocabularies are available nowadays to describe datasets. Europeana supports three vocabularies which are suitable to fulfill the requirements for aggregation of linked data: [VoID](https://www.w3.org/TR/void/), [DCAT](https://www.w3.org/TR/vocab-dcat/), and [Schema.org](http://schema.org/Dataset).

Data providers may use classes and properties from any of the three vocabularies to describe each of their datasets. To enable Europeana to aggregate and ingest a dataset, the linked data resource of the dataset...:
 - **Must** be accessible by its URI.
 - **Must** be encoded in RDF.
 - **Must** have a title property.
 - **Must** specify the technical mechanism that allows the dataset to be automatically harvested by Europeana.
 - **May** specify a machine readable license that applies to all metadata  
The following sections will provide further details on each of these points.

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

The RDF resource of the dataset must have a title, and the title may be provided in several languages. The titles should be in dc:title properties, schema:title , dcat? and the corresponding language in a xml:lang attribute of the title property.

# Specifying the technical mechanism for LOD harvesting

A LOD dataset for Europeana, is constituted, in its core, by RDF resources of the class edm:ProvidedCHO. In addition, a dataset contains all other resources used to describe the cultural object and aggregation metadata, as specified in the EDM (i.e. resources of types such as ore:Aggregation, edm:WebResource, edm:Agent, etc.).
All these resources will be harvested by Europeana's LOD harvester. The harvester will use the RDF description of the dataset to know which RDF resources to harvest and the mechanism to harvest them. 
Data providers may choose one of the mechanisms, typically used for LOD: 
 - Data dumps containing all data within the dataset.
 - Listing of the URIs of all ore:Aggregation resources within the dataset.
The mechanism that should be applied to a LOD dataset is indicated by the data provider in the properties of the RDF description of the dataset, using any of the supported vocabularies: [VoID](https://www.w3.org/TR/void/), [DCAT](https://www.w3.org/TR/vocab-dcat/), and [Schema.org](http://schema.org/Dataset).

## Specifying data dumps

| Vocabulary| Specification Sections |
|--|--|
| VoID | See section "[3.3 RDF data dumps](https://www.w3.org/TR/void/#dumps)" describing the void:dataDump property. |
| DCAT | See section "[5.4 Class: Distribution](https://www.w3.org/TR/vocab-dcat/#class-distribution)", particularly the properties dcat:downloadURL and dcat:mediaType.
| Schema.org | see the definition of the property [schema:distribution](http://schema.org/distribution) of the schema:Dataset class.<br> see also the class [schema:DataDownload](http://schema.org/DataDownload) and its properties [schema:contentUrl](http://schema.org/contentUrl) and schema:encodingFormat |

The files that constitute the data dump of the dataset, must contain the RDF data encoded in one the RDF encodings suported by Europeana:  [RDF/XML](https://www.w3.org/TR/rdf-syntax-grammar/), [JSON-LD](https://www.w3.org/TR/json-ld/)  or [Turtle](https://www.w3.org/TR/turtle/) 
The files may possibly be compressed. Currently, Europeana supports only the GZip compression algorithm.

## Specifying listings of URIs
void:rootResource
The URIs of Ch objects in the dataset may be specified in a LOD record (referenced from the dataset record with void:rootResource), 

 

# Dataset level license
The RDF resource of the dataset may optionally indicate a license that applies to the whole dataset. If the dataset provides the licensing information, individual metadata records may still override it, by specifying a license as defined in EDM.
 

specifies it 

pecify a machine readable license that applies to all metadata 

 


 

-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.


-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.
    
-   The license for a whole dataset should be specified in dcterms:license or schema:license (with Europeana supported licenses' URIs)
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTE4MDQyOTk4MjIsLTM3MTgzNTQ5NCwxNj
I4MjY4OTExLDE4MDUyMjYwMDQsLTExMzcwMDA0NzIsLTE3NDI5
NTIxMzAsODg2NjY4MjI2LC0yMDU3MTgxOTA2LC0xMDQ0MTg4NT
k4LDE1NTUxNjQyNTAsLTE5NzAzNzgzOTUsMTE1MTIyNTE0Niwt
ODg5Nzc2MTIyLC0zNzY5MDIyNjksLTU5OTE4NjgxMywxNzE0Nj
k0NjQyLC0xNDM1OTU0ODUyLC0xMzc3ODA4ODAsLTIwODM5Njg5
MjcsMTgyNzIxMDgxM119
-->