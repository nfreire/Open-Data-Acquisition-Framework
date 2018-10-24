# Open-Data-Acquisition-Lab
Experimental project on a framework for acquisition of data from open data sources, and supporting several technologies and protocols.  The Data Aggregation Lab is a work in progress. It aims to gather information from data providers and aggregators, share experimental results, apply prototypes, and provide demonstrators.

Current R&D activities address:

-   Aggregation mechanisms for linked data
-   Aggregation mechanisms for IIIF based on Activity Streams 2.0
-   Aggregation mechanisms for IIIF based on IIIF Collections
-   Aggregation mechanisms for IIIF based on Sitemaps
-   Aggregation mechanisms specific for Schema.org metadata (e.g. HTML Crawlers)
-   Aggregation mechanisms for the WWW (HTML meta, RDFa, RDFaLite, Microdata, etc)
-   Conversion of Schema.org metadata to EDM
-   Metadata profiling

Current operations available in the workbench (for data providers):

-   Register a LOD dataset for aggregation by Europeana - Allows data providers and aggregators to send us the description of a dataset where we will experiment the LOD aggregation mechanisms.  
    The guidelines for describing a LOD dataset for Europeana are available in [this document](https://github.com/nfreire/Open-Data-Acquisition-Framework/blob/master/opaf-documentation/SpecifyingLodDatasetForEuropeana.md).
-   Register a IIIF dataset for aggregation by Europeana - Allows data providers and aggregators to send us the description of a dataset where we will experiment the WWW crawling aggregation mechanisms for extracting structured data within HTML pages.  
    IIIF datasets can be used for aggregation via several harvesting methods: [IIIF Change Discovery API v0.1](http://preview.iiif.io/api/discovery/api/discovery/0.1/), IIIF Collections, and Sitemaps.
-   Register a WWW dataset for aggregation by Europeana - Allows data providers and aggregators to send us the description of a dataset where we will experiment the HTML Crawling and aggregation of micro formats (RDFa, Microdata, etc) and JSON-LD (for example for Schema.org data).  
    (a sitemaps.xml file is required)

Current operations available in the workbench (for Europeana aggregation):

-   Manage and process datasets - Allows the Europeana Aggregation team to manage the registered datasets, execute harvests, metadata analysis and conversion.

<!--stackedit_data:
eyJoaXN0b3J5IjpbLTEyNjg4NTE3ODAsMTY2NTYzMDg3Ml19
-->