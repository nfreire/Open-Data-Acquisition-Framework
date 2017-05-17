package inescid.opaf.manager.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.ReaderRIOT;
import org.apache.jena.riot.system.StreamRDFLib;
import org.apache.jena.sparql.util.Context;
import org.apache.jena.sparql.util.Symbol;
import org.apache.log4j.xml.XMLLayout;


import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.DataConversionManager;
import inescid.opaf.data.convert.DataConverter;
import inescid.opaf.data.profile.UsageProfiler;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.IiifPresentationMetadata;

public class RepositoryRdfDataUsageProfiler {

	static final Charset UTF8 = Charset.forName("UTF8");
	static final Pattern STRIP_XML_TOP_ELEMENT = Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);

	public void run(File repositoryFolder, final int maxProfiledRecords) throws Exception {
//		Context jsonldContext=new Context();
//		jsonldContext.set(Symbol.create(""), "http://schema.org/");
//		jsonldContext.put("", "http://schema.org/");
//		ReaderRIOT jsonldJenaReader = RDFDataMgr.createReader(Lang.JSONLD);
		
		File dbFile = new File(repositoryFolder, "db.bin");
		if(!dbFile.exists()) {
			System.out.println("Repository is empty. Exiting.");
			return;
		}
		System.out.println(new Date(dbFile.lastModified()));
		
		Database db;
		db=new Database(repositoryFolder, AccessMode.READ_ONLY);
		
		UsageProfiler profiler=new UsageProfiler();
		
		Model ldModelRdf = ModelFactory.createDefaultModel();
		int cnt=0;
		int cntProfiled=0;
		for(byte[] mdBytes : db.getAllRecordsData()) {
			cnt++;
			if(maxProfiledRecords>0 && cnt>=maxProfiledRecords)
				break;
			try {
				Object metadata = IoUtil.fromByteArray(mdBytes);
				if(metadata instanceof IiifManifest) {
					IiifManifest manifest=(IiifManifest) metadata;
					for(RawDataRecord seeAlso: manifest.getMetadata().getSeeAlso()) {
						String md = new String(seeAlso.getContent());
//						ByteArrayInputStream bytesIs = new ByteArrayInputStream(md.getBytes("UTF8"));
						StringReader mdReader = new StringReader(md);
						try { 
//							jsonldJenaReader.read(bytesIs, null, null , StreamRDFLib.graph(ldModelRdf.getGraph()), jsonldContext);
							ldModelRdf.read(mdReader, null, "JSON-LD");
							mdReader.close();
							//								RDFDataMgr.read(ldModelRdf, bytesIs, seeAlso.getUrl(), Lang.JSONLD);
						} catch (IllegalArgumentException e) {
							mdReader.close();
//continue;
							System.out.println("WARN: Reading JSONLD - "+md);
							e.printStackTrace(System.out);
							Matcher m=Pattern.compile("(\"genre\":\\[\"[^\"]+\"\\])").matcher(md);
							if(m.find()) {
								md=m.replaceFirst(m.group(1).replace(' ', '_'));
								mdReader = new StringReader(md);
								ldModelRdf.read(mdReader, null, "JSON-LD");
								mdReader.close();
//								bytesIs = new ByteArrayInputStream(md.getBytes("UTF8"));
//								RDFDataMgr.read(ldModelRdf, bytesIs, seeAlso.getUrl(), Lang.JSONLD);
							}
						}
//						System.out.println(md);
						profiler.collect(ldModelRdf);
						cntProfiled++;
						ldModelRdf.removeAll();
					}
				} else if (metadata instanceof String) {
					String md=metadata.toString();
					StringReader mdReader = new StringReader(md);
					try { 
//						jsonldJenaReader.read(bytesIs, null, null , StreamRDFLib.graph(ldModelRdf.getGraph()), jsonldContext);
						ldModelRdf.read(mdReader, null, "JSON-LD");
						mdReader.close();
						//								RDFDataMgr.read(ldModelRdf, bytesIs, seeAlso.getUrl(), Lang.JSONLD);
					} catch (IllegalArgumentException e) {
						mdReader.close();
//continue;
						System.out.println("WARN: Reading JSONLD - "+md);
						e.printStackTrace(System.out);
						Matcher m=Pattern.compile("(\"genre\":\\[\"[^\"]+\"\\])").matcher(md);
						if(m.find()) {
							md=m.replaceFirst(m.group(1).replace(' ', '_'));
							mdReader = new StringReader(md);
							ldModelRdf.read(mdReader, null, "JSON-LD");
							mdReader.close();
//							bytesIs = new ByteArrayInputStream(md.getBytes("UTF8"));
//							RDFDataMgr.read(ldModelRdf, bytesIs, seeAlso.getUrl(), Lang.JSONLD);
						}
					}
//					System.out.println(md);
					profiler.collect(ldModelRdf);
					cntProfiled++;
					ldModelRdf.removeAll();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(cnt % 50 == 0) 
				System.out.println("\nProgress: "+ cnt+" records ("+cntProfiled + " profiled)\n"+profiler.getUsageStats()+ " profiled)\n"+profiler.getUsageStats().toCsv()+"\n");
			else if(cnt % 10 == 0) 
				System.out.print(cnt);
			else
				System.out.print(".");
		}
		ldModelRdf.close();
		System.out.println("DATASET PROFILE:");
		System.out.println(profiler.getUsageStats());
		System.out.println(profiler.getUsageStats().toCsv());
		db.shutdown();

	// Object data =
	// db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
	// System.out.println(data);
	}

}
