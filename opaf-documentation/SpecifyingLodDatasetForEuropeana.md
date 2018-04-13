## Specifying a linked data dataset for aggregation  by Europeana

Cultural heritage institutions typically publish linked data that covers more resources than the cultural heritage digital objects provided to Europeana. Therefore, it is necessary that data providers make available linked data descriptions of the datasets for aggregation by Europeana.
Several vocabularies are available nowadays to describe datasets. Europeana supports three vocabularies which are suitable to fulfill the requirements for aggregation of linked data: [VoID](https://www.w3.org/TR/void/), [DCAT](https://www.w3.org/TR/vocab-dcat/), and [Schema.org](http://schema.org/Dataset).

Data providers may use classes and properties from any of the three vocabularies to describe each of their datasets. To enable Europeana to aggregate and ingest a dataset, the linked data resource of the dataset...:
 - **Must** be accessible by its URI.
 - **Must** be encoded in RDF.
 - **Must** have a title property.
 - **Must** specify a technical mechanism that allows the dataset to be automatically harvested by Europeana.
 - **May** specify a machine readable license that applies to all metadata  
The following sections will provide further details on each of these points.

## Dataset RDF resource accessible by its URI
The description of the dataset in RDF must itself be published as linked open data. 
When ingesting the dataset in Europeana, the URI of the dataset must be provided to Europeana. It will function as the entry point for the Europeana LOD Harvester to reach all linked data descriptions of the the cultural heritage objects that belong in the dataset. 
The dataset description is used 
The provider 

    
-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.
    
-   The license for a whole dataset should be specified in dcterms:license or schema:license (with Europeana supported licenses' URIs)
<!--stackedit_data:
eyJoaXN0b3J5IjpbMTgwMTgzNDYzMCwtMTQwNDY5MTE3MCwyMD
Y0MzIzODQ0LDE0Njg0MzA4NjYsOTk3NTg0NjU4LDg5Mzk4ODEw
OCwtMTQ5MDIwNzYyMSw3OTcxMDUxMzMsMTY4Njc2NzAyXX0=
-->