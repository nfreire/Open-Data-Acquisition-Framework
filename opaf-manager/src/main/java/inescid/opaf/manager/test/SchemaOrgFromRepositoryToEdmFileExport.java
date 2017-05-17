package inescid.opaf.manager.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.sound.midi.Soundbank;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.log4j.xml.XMLLayout;

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.DataConversionManager;
import inescid.opaf.data.convert.DataConverter;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.IiifPresentationMetadata;

public class SchemaOrgFromRepositoryToEdmFileExport {

	static final Charset UTF8=Charset.forName("UTF8");
	static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
	public void run(File repositoryFolder, File exportFolder, boolean transformToEdmInternal, final int maxExportRecords, String provider, String dataProvider) throws Exception {
		File dbFile = new File(repositoryFolder, "db.bin");
		if(!dbFile.exists()) {
			System.out.println("Repository is empty. Exiting.");
			return;
		}		
		System.out.println(new Date(dbFile.lastModified()));
		
		final int maxExportRecordsPerFile=300;
		if(!exportFolder.exists())
			exportFolder.mkdirs();
		
		Database db;
		db=new Database(repositoryFolder, AccessMode.READ_ONLY);
		EdmFileExport metadataExporter=new EdmFileExport(maxExportRecordsPerFile, exportFolder);
		metadataExporter.setTransformToEdmInternal(transformToEdmInternal); 
		metadataExporter.init();
		
		DataConverter schemaOrgToEdmConverter = DataConversionManager.getInstance().getConverter(new DataSpec("application/ld+json", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "http://schema.org/"), new DataSpec("application/xml", "http://edm", null));
		
		int cnt=0;
		for(byte[] mdBytes : db.getAllRecordsData()) {
			cnt++;
			Object metadata = IoUtil.fromByteArray(mdBytes);
			try {
				if(metadata instanceof String) {
					String fromByteArray = (String) metadata;
					metadataExporter.export(fromByteArray);
					if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
						break;
				} else if(metadata instanceof IiifPresentationMetadata) {
					IiifPresentationMetadata iiifMetadata = (IiifPresentationMetadata) metadata;
					for(Iterator<RawDataRecord> it=iiifMetadata.getSeeAlso().iterator(); it.hasNext() ; ) {
						RawDataRecord seeAlso = it.next();
						try {
//							System.out.println(iiifMetadata.getManifestUrl());
//					System.out.println(seeAlso.getFormat());
//							System.out.println(seeAlso.getProfile());
							if(seeAlso.getProfile()!=null && seeAlso.getProfile().equalsIgnoreCase("http://www.europeana.eu/schemas/edm/")) {
								String fromByteArray = new String(seeAlso.getContent(), "UTF-8");
								metadataExporter.export(fromByteArray);
								if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
									break;
							}
						} catch (Exception e) {
							System.err.println(seeAlso);
							System.err.println(new String(seeAlso.getContent(), "UTF-8"));
							e.printStackTrace();
						}
					}
				} else if(metadata instanceof IiifManifest) {
					IiifManifest manifest=(IiifManifest) metadata;
					for(RawDataRecord seeAlso: manifest.getMetadata().getSeeAlso()) {
						if(seeAlso.getProfile()!=null && seeAlso.getProfile().equalsIgnoreCase("http://www.europeana.eu/schemas/edm/")) {
							String fromByteArray = new String(seeAlso.getContent(), "UTF-8");
							metadataExporter.export(fromByteArray);
							if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
								break;
						} else {//TODO
							String md = new String(seeAlso.getContent());
							System.out.println(md);
							
							seeAlso.setUrl(manifest.getUri());
							RawDataRecord convertedEdm = schemaOrgToEdmConverter.convert(seeAlso, getEdmStatements(manifest, provider, dataProvider));
							String fromByteArray = new String(convertedEdm.getContent(), "UTF-8");
							metadataExporter.export(fromByteArray);
						}
					}
				}
			} catch (Exception e) {
				System.err.println(metadata);
				e.printStackTrace();
			}
			if(cnt % 1000 == 0) 
				System.out.println("Progress: "+ cnt+" records ("+metadataExporter.getExportRecordsCount() + " exported)");
		}
		System.out.println(metadataExporter.getExportRecordsCount() + " records exported");
		metadataExporter.close();
		db.shutdown();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}


	private Model getEdmStatements(IiifManifest manifest, String provider, String dataProvider) {
		Model modelRdf = ModelFactory.createDefaultModel();
		String aggregationUri = manifest.getUri()+"#aggregation";
		Resource aggregation=modelRdf.createResource(aggregationUri, RdfReg.ORE_AGGREGATION);
		Statement st = null; 
		
		String shownByUrl = manifest.getMetadata().getShownByUrl();
		if(shownByUrl != null) {
			Resource webResource=modelRdf.createResource(shownByUrl, RdfReg.EDM_WEB_RESOURCE);
			if(manifest.getMetadata().getShownByService() != null) {
				Resource serviceResource = modelRdf.createResource(manifest.getMetadata().getShownByService(), RdfReg.SVCS_SERVICE);
				st = modelRdf.createStatement(webResource, RdfReg.SVCS_HAS_SERVICE, 
						serviceResource);
				modelRdf.add(st);
				st=modelRdf.createStatement(webResource, RdfReg.DCTERMS_IS_REFERENCED_BY, 
						modelRdf.createResource(manifest.getUri()));
				modelRdf.add(st);
				
				st=modelRdf.createStatement(serviceResource, RdfReg.DCTERMS_CONFORMS_TO, 
						modelRdf.createResource("http://iiif.io/api/image/2/level1.json"));
				modelRdf.add(st);
				
				st=modelRdf.createStatement(serviceResource, RdfReg.IIIF_PROFiLE_DOAP_IMPLEMENTS, 
						modelRdf.createResource("http://iiif.io/api/image"));
				modelRdf.add(st);
				
//				<edm:WebResource rdf:about="http://dams.llgc.org.uk/iiif/2.0/image/3100027/full/512,/0/default.jpg">
//				<svcs:has_service>
//				<svcs:Service rdf:about="http://dams.llgc.org.uk/iiif/2.0/image/3100027">
//				<dcterms:conformsTo rdf:resource="http://iiif.io/api/image/2/level1.json"/>
//				<doap:implements rdf:resource="http://iiif.io/api/image"/>
//				</svcs:Service>
//				</svcs:has_service>
//				</edm:WebResource>
				
				
			}
			st=modelRdf.createStatement(aggregation, RdfReg.EDM_IS_SHOWN_BY, 
					webResource);
			modelRdf.add(st);
			st=modelRdf.createStatement(aggregation, RdfReg.EDM_OBJECT , 
					webResource);
			modelRdf.add(st);
		}
		if(manifest.getMetadata().getShownAtUrl() != null) {
			st=modelRdf.createStatement(aggregation, RdfReg.EDM_IS_SHOWN_AT, 
					modelRdf.createResource(manifest.getMetadata().getShownAtUrl()));
			modelRdf.add(st);
		}
		if(manifest.getMetadata().getLicense() != null) {
			st=modelRdf.createStatement(aggregation, RdfReg.EDM_RIGHTS, 
					modelRdf.createResource(manifest.getMetadata().getLicense()));
			modelRdf.add(st);
		}
		if(provider != null) {
			st=modelRdf.createStatement(aggregation, RdfReg.EDM_PROVIDER, 
					modelRdf.createLiteral(provider));
			modelRdf.add(st);
		}
		if(dataProvider != null) {
			st=modelRdf.createStatement(aggregation, RdfReg.EDM_DATA_PROVIDER, 
					modelRdf.createLiteral(dataProvider));
			modelRdf.add(st);
		}
		st=modelRdf.createStatement(aggregation, RdfReg.EDM_AGGREGATED_CHO, 
				modelRdf.createResource(manifest.getUri(), RdfReg.EDM_PROVIDED_CHO));
		modelRdf.add(st);
		
		return modelRdf;
	}
}
