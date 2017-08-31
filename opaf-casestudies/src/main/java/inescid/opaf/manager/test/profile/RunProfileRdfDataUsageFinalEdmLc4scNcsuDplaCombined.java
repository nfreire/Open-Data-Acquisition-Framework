package inescid.opaf.manager.test.profile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.w3c.dom.Document;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.rdf.RdfConverter;
import inescid.opaf.data.convert.rdf.SchemaOrgToEdmConversionSpecification;
import inescid.opaf.data.profile.UsageProfiler;
import inescid.opaf.data.profile.UsageStats;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.manager.test.RepositoryRdfDataUsageProfilerSchemaorgEdm;
import inescid.util.XmlUtil;

public class RunProfileRdfDataUsageFinalEdmLc4scNcsuDplaCombined {

//	private static final int MAX_RECORDS=-1;
	private static final int MAX_RECORDS=1;
	
	UsageStats providerEdmDataProfile;
	UsageStats dplaEdmDataProfile;
	
    public RunProfileRdfDataUsageFinalEdmLc4scNcsuDplaCombined() {
    	if(MAX_RECORDS>0)
    		System.err.println("WARNING: MAXRECORDS set to "+MAX_RECORDS);
	}
    
    public void run(File inFolder, String dataProvider) throws Exception {
    	UsageProfiler profilerEdm=new UsageProfiler();
    	UsageProfiler profilerEdmDpla=new UsageProfiler();
    	int cnt=0;
		for (File schemaorgFile: inFolder.listFiles()) {
			if(MAX_RECORDS>0 && cnt>=MAX_RECORDS)
				break;
			if(!schemaorgFile.getName().endsWith(".schemaorg.jsonld")) 
				continue;
			cnt++;
			
			String uriEncoded = schemaorgFile.getName().substring(0, 
					schemaorgFile.getName().length()-".schemaorg.jsonld".length());
			File dplaEdmFile=new File(schemaorgFile.getParentFile(), 
					uriEncoded+"_dpla.json");

			Model schemaorgModel = ModelFactory.createDefaultModel();
			Model edmModelDpla = ModelFactory.createDefaultModel();
			
			byte[] fileBytes = FileUtils.readFileToByteArray(schemaorgFile);
			ByteArrayInputStream mdReader = new ByteArrayInputStream(fileBytes);
			schemaorgModel.read(mdReader, null, "JSON-LD");
			mdReader.close();

			Resource edmModelProvider = convertSchemaOrgToEdm(schemaorgModel, URLDecoder.decode(uriEncoded, "UTF-8"), dataProvider);

			profilerEdm.collect(edmModelProvider.getModel());
			
			fileBytes = FileUtils.readFileToByteArray(dplaEdmFile);
			mdReader = new ByteArrayInputStream(fileBytes);
			edmModelDpla.read(mdReader, null, "JSON-LD");
			mdReader.close();

			Map<Property, Resource> dplaRdfTypeMapping=new HashMap<>();
			dplaRdfTypeMapping.put(RdfReg.DCTERMS_IS_PART_OF, RdfReg.DCMITYPE_COLLECTION);
			dplaRdfTypeMapping.put(RdfReg.DCTERMS_TEMPORAL, RdfReg.EDM_TIMESPAN);
			dplaRdfTypeMapping.put(RdfReg.DCTERMS_SPATIAL, RdfReg.EDM_PLACE);
			dplaRdfTypeMapping.put(RdfReg.EDM_PROVIDER, RdfReg.EDM_AGENT);
			dplaRdfTypeMapping.put(RdfReg.EDM_HAS_VIEW, RdfReg.EDM_WEB_RESOURCE);
			dplaRdfTypeMapping.put(edmModelDpla.createProperty("http://dp.la/terms/SourceResource"), RdfReg.EDM_PROVIDED_CHO);
			
			
//			//debug
//			{
//				System.out.println("DPLA_EDM");
//				StmtIterator propTypesStms = edmModelDpla.listStatements();
//				while (propTypesStms.hasNext()) {
//					Statement stm = propTypesStms.next();
//					System.out.println(stm);
//				}			
//				System.out.println("PROVIDER_EDM");
//				 propTypesStms = edmModelProvider.getModel().listStatements();
//				while (propTypesStms.hasNext()) {
//					Statement stm = propTypesStms.next();
//					System.out.println(stm);
//				}
//			}
			
			
			StmtIterator propTypesStms = edmModelDpla.listStatements();
			while (propTypesStms.hasNext()) {
				Statement stm = propTypesStms.next();
				if(stm.getObject() instanceof Resource) {
					Resource classOfObject=dplaRdfTypeMapping.get(stm.getPredicate());
					if(classOfObject != null) {
						Statement typeStm = edmModelDpla.createStatement((Resource) stm.getObject(), RdfReg.RDF_TYPE, classOfObject);
						edmModelDpla.add(typeStm);
						System.out.println("JSON-LD adding type: "+ classOfObject.getURI());
					}
				}
			} 

			
			profilerEdmDpla.collect(edmModelDpla);
		}
		providerEdmDataProfile=profilerEdm.getUsageStats();
		dplaEdmDataProfile=profilerEdmDpla.getUsageStats();
//		
//		RepositoryRdfDataUsageProfilerSchemaorgEdm profileRunner = new RepositoryRdfDataUsageProfilerSchemaorgEdm();
//		//		final int maxProfiledRecords=-100;		
////		final int maxProfiledRecords=5;		
//			final int maxProfiledRecords=1000;		
//		{
//			File repositoryFolder=new File("src/data/firstSample/iiif-crawl-repository-ncsu");
//			profileRunner.run(repositoryFolder, maxProfiledRecords, "NC State University Libraries");
//		}
//		UsageProfiler schemaorgStatsNcsu = profileRunner.getSchemaorgProfile();
//		UsageProfiler edmStatsNcsu = profileRunner.getEdmProfile();
//		System.out.println("NCSU DATASET PROFILE SCHEMA.ORG:");
//		System.out.println(schemaorgStatsNcsu.getUsageStats().toCsv());
//		System.out.println("NCSU DATASET PROFILE EDM:");
//		System.out.println(edmStatsNcsu.getUsageStats().toCsv());
//
//		profileRunner = new RepositoryRdfDataUsageProfilerSchemaorgEdm();
//		{
//			File repositoryFolder=new File("src/data/firstSample/www-crawl-repository-ld4sc");
//			profileRunner.run(repositoryFolder, maxProfiledRecords, "University of Illinois at Urbana–Champaign");
//		}
//		UsageProfiler schemaorgStatsLd4sc = profileRunner.getSchemaorgProfile();
//		UsageProfiler edmStatsLd4sc = profileRunner.getEdmProfile();
//		System.out.println("LD4SC DATASET PROFILE SCHEMA.ORG:");
//		System.out.println(schemaorgStatsLd4sc.getUsageStats().toCsv());
//		System.out.println("LD4SCDATASET PROFILE EDM:");
//		System.out.println(edmStatsLd4sc.getUsageStats().toCsv());
//		System.out.println("COMBINED DATASETS PROFILE SCHEMA.ORG:");
//		String csvCombinedSchemaorg = schemaorgStatsNcsu.getUsageStats().toCsvCombined(schemaorgStatsLd4sc.getUsageStats());
//		System.out.println(csvCombinedSchemaorg);
//		String csvCombinedEdm = edmStatsNcsu.getUsageStats().toCsvCombined(edmStatsLd4sc.getUsageStats());
//		System.out.println(csvCombinedEdm);
//		
//		FileUtils.write(new File("target/profile_schemaorg_combined.csv"), csvCombinedSchemaorg, "UTF-8");
//		FileUtils.write(new File("target/profile_edm_combined.csv"), csvCombinedEdm, "UTF-8");
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
	
	public UsageStats getProviderEdmDataProfile() {
		return providerEdmDataProfile;
	}
	
	public UsageStats getDplaEdmDataProfile() {
		return dplaEdmDataProfile;
	}
	
	public static void main(String[] args) throws Exception {
		File ncsuFolder=new File("src/data/schemaorgCaseStudyNcsuLd4sc/edm_schemaorg_comparison_ncsu");
		String dataProvider="NC State University Libraries";
		RunProfileRdfDataUsageFinalEdmLc4scNcsuDplaCombined runner = new RunProfileRdfDataUsageFinalEdmLc4scNcsuDplaCombined();
		runner.run(ncsuFolder, dataProvider);
		String csvCombinedEdm = runner.getProviderEdmDataProfile().toCsvCombined(runner.getDplaEdmDataProfile());
		System.out.println(csvCombinedEdm);
		FileUtils.write(new File("target/profile_edm_ncsu_dpla_combined.csv"), csvCombinedEdm, "UTF-8");
		
//		File ld4scFolder=new File("src/data/schemaorgCaseStudyNcsuLd4sc/edm_schemaorg_comparison_ld4sc");
//		dataProvider="University of Illinois at Urbana–Champaign";
//		runner.run(ld4scFolder, dataProvider);
//		csvCombinedEdm = runner.getProviderEdmDataProfile().toCsvCombined(runner.getDplaEdmDataProfile());
//		System.out.println(csvCombinedEdm);
//		FileUtils.write(new File("target/profile_edm_ld4sc_dpla_combined.csv"), csvCombinedEdm, "UTF-8");
	}

}
