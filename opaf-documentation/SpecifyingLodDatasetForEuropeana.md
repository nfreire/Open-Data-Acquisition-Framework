## Specifying a LOD dataset for aggregation  by Europeana

Cultural heritage institutions typically publish linked data that covers more resources than the cultural heritage digital objects provided to Europeana. Therefore, it is necessary that data providers make available linked data descriptions of the datasets for aggregation by Europeana.
Several vocabularies are available nowadays to describe datasets. Europeana supports three vocabularies which are suitable to fulfill the requirements for aggregation of linked data: [VoID](https://www.w3.org/TR/void/), [DCAT](https://www.w3.org/TR/vocab-dcat/), and [Schema.org](http://schema.org/Dataset).

Data providers may use classes and properties from any of the three vocabularies to describe each of their datasets. To enable Europeana to aggregate and ingest a dataset, the linked data resource of the dataset...:
 - **Must** be accessible by its URI.
 - **Must** be encoded in RDF.
 - **Must** have a title property.
 - **Must** specify the technical mechanism that allows the dataset to be automatically harvested by Europeana.
 - **May** specify a machine readable license that applies to all metadata  
The following sections will provide further details on each of these points.

## Dataset RDF resource accessible by its URI
The description of the dataset in RDF must itself be published as linked open data. 
When ingesting the dataset in Europeana, the URI of the dataset must be provided to Europeana. It will function as the entry point for the Europeana LOD Harvester to reach all linked data descriptions of the the cultural heritage objects that belong in the dataset. 
The dataset description is used during the first ingestion of the dataset in to Europeana, and later, for incremental updates of the dataset.
The data provider must maintain the dataset description updated over time, to allow incremental updates of the dataset in Europeana. 
## Dataset resource encoded in a supported RDF format
The Europeana LOD Harvester accesses the RDF resource of the dataset by sending an HTTP request to the URI that includes the Accept header with the supported mime -types for RDF encoding. The response may use any of the supported encodings to send the RDF description of the dataset.
The following are the supported mime-types:

| Format | Mime-type | Specification
|--|--|--|
| RDF/XML | application/rdf+xml | https://www.w3.org/TR/rdf-syntax-grammar/
| JSON-LD | application/ld+son | [https://www.w3.org/TR/json-ld/](https://www.w3.org/TR/json-ld/) 
| Turtle | application/x-turtle | [https://www.w3.org/TR/turtle/](https://www.w3.org/TR/turtle/) |

## Title of the dataset
The RDF resource of the dataset must have a title, and the title may be provided in several languages. The titles should be in dc:title properties, and the corresponding language in a xml:lang attribute of the title property.

## Specifying the available technical mechanism for LOD harvesting
A LOD dataset for Europeana, 

-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.


-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.
    
-   The license for a whole dataset should be specified in dcterms:license or schema:license (with Europeana supported licenses' URIs)
<!--stackedit_data:
eyJoaXN0b3J5IjpbMjAxNTE1ODE1NCwtNTk5MTg2ODEzLDE3MT
Q2OTQ2NDIsLTE0MzU5NTQ4NTIsLTEzNzc4MDg4MCwtMjA4Mzk2
ODkyNywxODI3MjEwODEzLDIwNDY4NzYxMjAsLTE1OTAxOTcyOT
MsMTIxOTY2MjQ1MywyMDU4OTg4Mzk4LDE5NjE4NzQ1OCwtMTc2
NTQ2NzQwOSwtODI5MzE4MzAxLDE4MzU1NjI5OCwtMTI2OTU4Nz
U5MywtMTQwNDY5MTE3MCwyMDY0MzIzODQ0LDE0Njg0MzA4NjYs
OTk3NTg0NjU4XX0=
-->