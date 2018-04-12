## Specifying a linked data dataset for aggregation  by Europeana

Data providers will provide to Europeana, the resolvable URI of an RDF resource that describes and specifies the members of the dataset.  


 Data providers will provide to Europeana, dataset descriptions using any of these vocabularies: VOID/DCAT/Schema.org.
    
-   The URIs of Ch objects in the dataset may either be specified in a LOD record (referenced from the dataset record with void:rootResource), or dump file(s) in RDF (any well know encoding of RDF).
    
-   When using void:rootResource, the resource must contain dcterms:hasPart properties with the URI’s of the CH objects’s ore:Aggregations (EDM), or URI’s to linked data resources describing CH objects with Schema.org properties.
    
-   The license for a whole dataset should be specified in dcterms:license or schema:license (with Europeana supported licenses' URIs)
<!--stackedit_data:
eyJoaXN0b3J5IjpbNzU3NTY5NTQsLTE0OTAyMDc2MjEsNzk3MT
A1MTMzLDE2ODY3NjcwMl19
-->