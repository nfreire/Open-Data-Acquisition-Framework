 package inescid.opaf.manager.test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;

public class RepositoryExporter {

	static final Charset UTF8 = Charset.forName("UTF8");
	static final Pattern STRIP_XML_TOP_ELEMENT = Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);

	public void run(File repositoryFolder, File exportFolder, String exportedFileExtension, final int maxExportedRecords) throws Exception {
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
		
//		UsageProfiler profiler=new UsageProfiler();
		
		int cnt=0;
		int cntExported=0;
		for(Iterator<Entry<Object, byte[]>> it = db.getAllRecords(); it.hasNext() ; ) {
			Entry<Object, byte[]> dbEntry=it.next();
			byte[] mdBytes=dbEntry.getValue();
			if(maxExportedRecords>0 && cnt>maxExportedRecords-1)
				break;
			cnt++;
			try {
				Object metadata = IoUtil.fromByteArray(mdBytes);
				String mdString=null;
				if(metadata instanceof IiifManifest) {
					IiifManifest manifest=(IiifManifest) metadata;
					if (!manifest.getMetadata().getSeeAlso().isEmpty()) 
						mdString = new String(manifest.getMetadata().getSeeAlso().get(0).getContent());
				} else if (metadata instanceof String) {
					mdString=metadata.toString();
				}

				if(mdString!=null) {
					FileOutputStream out=new FileOutputStream(new File(exportFolder, URLEncoder.encode(dbEntry.getKey().toString(), "UTF8")+"."+exportedFileExtension));
					out.write(mdString.getBytes(UTF8));
					out.close();
					cntExported++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(cnt % 50 == 0) 
				System.out.println("\nProgress: "+ cnt+" records ("+cntExported + " exported)\n");
			else if(cnt % 10 == 0) 
				System.out.print(cnt);
			else
				System.out.print(".");
		}
		db.shutdown();
		System.out.println("Export done: "+ cnt+" records ("+cntExported + " exported)");
		
	// Object data =
	// db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
	// System.out.println(data);
	}

}
