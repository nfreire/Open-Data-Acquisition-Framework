## Specifying a linked data dataset for aggregation  by Europeana

Cultural heritage institutions typically publish linked data that covers more resources than the cultural heritage digital objects provided to Europeana. Therefore, it is necessary that data providers make available linked data descriptions of the datasets for aggregation by Europeana.


Data providers will provide to Europeana, the resolvable URI of an RDF resource that describes and specifies the members of the dataset.  


 Data providers will provide to Europeana, dataset descriptions using any of these vocabularies: VOID/DCAT/Schema.org.
    
-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.
    
-   The license for a whole dataset should be specified in dcterms:license or schema:license (with Europeana supported licenses' URIs)
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTE1Mjk1NDkyOTgsLTE0OTAyMDc2MjEsNz
k3MTA1MTMzLDE2ODY3NjcwMl19
-->