package inescid.opaf.manager.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;
import org.apache.log4j.xml.XMLLayout;

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.rdf.converter.RdfConverter;
import inescid.opaf.data.convert.rdf.converter.SchemaOrgToEdmConversionSpecification;
import inescid.opaf.data.convert.rdf.converter.deprecated.DataConversionManager;
import inescid.opaf.data.convert.rdf.converter.deprecated.DataConverter;
import inescid.opaf.data.convert.rdf.converter.deprecated.SchemaOrgToEdmDataConverter;
import inescid.opaf.data.profile.UsageProfiler;
import inescid.opaf.data.profile.UsageStats;
import inescid.opaf.data.profile.UsageStats.ClassUsageStats;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.IiifPresentationMetadata;

public class RepositoryRdfDataUsageProfilerSchemaorgEdm {

	static final Charset UTF8 = Charset.forName("UTF8");

	UsageProfiler profilerSchemaorg=new UsageProfiler();
	UsageProfiler profilerEdm=new UsageProfiler();
	
	public void run(File repositoryFolder, final int maxProfiledRecords, String dataProvider) throws Exception {
		File dbFile = new File(repositoryFolder, "db.bin");
		if(!dbFile.exists()) {
			System.out.println("Repository is empty. Exiting.");
			return;
		}		
		System.out.println(new Date(dbFile.lastModified()));
		
		Database db;
		db=new Database(repositoryFolder, AccessMode.READ_ONLY);
		
		Model ldModelRdf = ModelFactory.createDefaultModel();
		
//		DataConverter schemaOrgToEdmConverter = DataConversionManager.getInstance().getConverter(new DataSpec("application/ld+json", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "http://schema.org/"), new DataSpec("application/xml", "http://edm", null));
//		((SchemaOrgToEdmDataConverter)schemaOrgToEdmConverter).setDataProvider(dataProvider);
		int cnt=0;
		int cntProfiled=0;
		for(Iterator<Entry<Object, byte[]>> it = db.getAllRecords(); it.hasNext() ; ) {
			Entry<Object, byte[]> dbEntry=it.next();
			byte[] mdBytes=dbEntry.getValue();
			cnt++;
			
//			if (cnt<361 || cnt>361)
//				continue;
			
			
			if(maxProfiledRecords>0 && cnt>maxProfiledRecords)
				break;
			Object metadata = IoUtil.fromByteArray(mdBytes);
			try {
				String jsonldString=null;
				if(metadata instanceof String) {
					jsonldString = metadata.toString();
				} else if(metadata instanceof IiifManifest) {
					IiifManifest manifest=(IiifManifest) metadata;
					for(RawDataRecord seeAlso: manifest.getMetadata().getSeeAlso()) {
						jsonldString = new String(seeAlso.getContent());
					} 
				}
				System.out.println(jsonldString);
				StringReader mdReader = new StringReader(jsonldString);
				try { 
//					jsonldJenaReader.read(bytesIs, null, null , StreamRDFLib.graph(ldModelRdf.getGraph()), jsonldContext);
					ldModelRdf.read(mdReader, null, "JSON-LD");
					mdReader.close();
					//								RDFDataMgr.read(ldModelRdf, bytesIs, seeAlso.getUrl(), Lang.JSONLD);
				} catch (IllegalArgumentException e) {
					mdReader.close();
					System.out.println("WARN: Reading JSONLD - "+jsonldString);
					e.printStackTrace(System.out);
					Matcher m=Pattern.compile("(\"genre\":\\[\"[^\"]+\"\\])").matcher(jsonldString);
					if(m.find()) {
						jsonldString=m.replaceFirst(m.group(1).replace(' ', '_'));
						mdReader = new StringReader(jsonldString);
						ldModelRdf.read(mdReader, null, "JSON-LD");
						mdReader.close();
					}
				}
				Resource edmModel = convertSchemaOrgToEdm(ldModelRdf, dbEntry.getKey().toString(), dataProvider);

				//debug
//					StmtIterator typeProperties = edmModel.getModel().listStatements();
//					for(Statement st : typeProperties.toList()) {
//						System.out.println(st);
//					}
					
				profilerSchemaorg.collect(ldModelRdf);
				profilerEdm.collect(edmModel.getModel());
				
				cntProfiled++;
				ldModelRdf.removeAll();
			} catch (Exception e) {
				System.err.println(metadata);
				e.printStackTrace();
			}

			if(cnt % 50 == 0) 
				System.out.println("\nProgress: "+ cnt+" records ("+cntProfiled + " profiled)\n"+profilerSchemaorg.getUsageStats()+ " profiled)\n"+profilerSchemaorg.getUsageStats().toCsv()+"\n");
			else if(cnt % 10 == 0) 
				System.out.print(cnt);
			else
				System.out.print(".");
		}
		db.shutdown();

	}

	RdfConverter conv=new RdfConverter(SchemaOrgToEdmConversionSpecification.spec);
	private Resource convertSchemaOrgToEdm(Model ldModelRdf, String rootResourceUri, String dataProvider) {		
		Resource mainTargetResource=conv.convert(ldModelRdf, rootResourceUri);
		if(mainTargetResource==null)
			return null;
		ResIterator aggregations = mainTargetResource.getModel().listResourcesWithProperty(RdfReg.RDF_TYPE, RdfReg.ORE_AGGREGATION);
		Resource ag = aggregations.next();
		mainTargetResource.getModel().add(ag, RdfReg.EDM_AGGREGATED_CHO, mainTargetResource);
		
		if(dataProvider!=null) {
			StmtIterator provs = mainTargetResource.getModel().listStatements(ag, RdfReg.EDM_DATA_PROVIDER, (String) null);
			if(!provs.hasNext())
				mainTargetResource.getModel().add(ag, RdfReg.EDM_DATA_PROVIDER, dataProvider);
			provs = mainTargetResource.getModel().listStatements(ag, RdfReg.EDM_PROVIDER, (String) null);
			if(!provs.hasNext())
				mainTargetResource.getModel().add(ag, RdfReg.EDM_PROVIDER, dataProvider);
		}		
		return mainTargetResource;
	}
	
	
	public UsageProfiler getSchemaorgProfile() {
		return profilerSchemaorg;
	}	
	public UsageProfiler getEdmProfile() {
		return profilerEdm;
	}	
}
