package inescid.opaf.manager.test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Statement;
import org.mapdb.BTreeMap;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.IiifSeeAlsoProperty;

public class ViewRepository {

	
	public static void main(String[] args) throws Exception {
		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin-mods");
		final int max_export_records=100;
		if(!exportFolder.exists())
			exportFolder.mkdirs();
		
		Database db;
		db=new Database(new File("target/iiif-crawl-repository-ucd"), AccessMode.READ_ONLY);
//		db=new Database(new File("target/iiif-crawl-repository-nlw"), AccessMode.READ_ONLY);
//		db=new Database(new File("target/iiif-crawl-repository"), AccessMode.READ_ONLY);
		int export_records_cnt=0;
		for(byte[] mdBytes : db.getAllRecordsData()) {
			IiifPresentationMetadata md = (IiifPresentationMetadata)IoUtil.fromByteArray(mdBytes);
//			System.out.println(md);
			System.out.println(md.getManifestUrl());
			if(md.getSeeAlso().isEmpty())
				System.out.println("no seeAlso");
			else {
				for(IiifSeeAlsoProperty seeAlso: md.getSeeAlso()) {
					String filename = URLEncoder.encode(md.getManifestUrl(), "UTF8")+".mods.xml";
					if(seeAlso.getProfile().equals("http://www.europeana.eu/schemas/edm/"))
						filename = URLEncoder.encode(md.getManifestUrl(), "UTF8")+".edm.xml";
					else if (seeAlso.getProfile().startsWith("http://www.loc.gov/mods/")) 
						filename = URLEncoder.encode(md.getManifestUrl(), "UTF8")+".mods.xml";
						
					File exportFile=new File(exportFolder, filename);
					FileOutputStream fileOutputStream = new FileOutputStream(exportFile);
					IOUtils.write(seeAlso.getSeeAlsoContent(), fileOutputStream);
					fileOutputStream.flush();
					fileOutputStream.close();					
				}
				export_records_cnt++;
			}
			if(export_records_cnt>=max_export_records)
				break;
		}
		db.shutdown();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}
}
