## Specifying a linked data dataset for aggregation  by Europeana

Cultural heritage institutions typically publish linked data that covers more resources than the cultural heritage digital objects provided to Europeana. Therefore, it is necessary that data providers make available linked data descriptions of the datasets for aggregation by Europeana.
Several vocabularies are available nowadays to describe datasets. Europeana supports three vocabularies which are suitable to fulfill the requirements for aggregation of linked data: [VoID](https://www.w3.org/TR/void/), [DCAT](https://www.w3.org/TR/vocab-dcat/), and [Schema.org](http://schema.org/Dataset).

Data providers may use classes and properties from any of the three vocabularies to describe each of their datasets. To enable Europeana to aggregate and ingest a dataset, the linked data resource of the dataset must:
 - Be accessible by its URI
 - Specify machine readable licenses and describe their datasets . The main aspects that distinguish them is their capability to support all the options for allowing data providers to specify how Europeana can obtain the dataset (requirement R4).

Data providers will provide to Europeana, the resolvable URI of an RDF resource that describes and specifies the members of the dataset.  


 Data providers will provide to Europeana, dataset descriptions using any of these vocabularies: VOID/DCAT/Schema.org.
    
-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.
    
-   The license for a whole dataset should be specified in dcterms:license or schema:license (with Europeana supported licenses' URIs)
<!--stackedit_data:
eyJoaXN0b3J5IjpbMTM1MjgwODUxNiw5OTc1ODQ2NTgsODkzOT
g4MTA4LC0xNDkwMjA3NjIxLDc5NzEwNTEzMywxNjg2NzY3MDJd
fQ==
-->