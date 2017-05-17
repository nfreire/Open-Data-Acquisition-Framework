package inescid.opaf.manager.test;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifManifest;
import inescid.opaf.iiif.IiifPresentationMetadata;

public class ViewRepository {

	
	public static void main(String[] args) throws Exception {
//		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin-mods");
		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\NCSU-SCHEMA.ORG");
		final int max_export_records=100;
		if(!exportFolder.exists())
			exportFolder.mkdirs();
		
		Database db;
//		db=new Database(new File("target/iiif-crawl-repository-ucd"), AccessMode.READ_ONLY);
//		db=new Database(new File("target/iiif-crawl-repository-nlw"), AccessMode.READ_ONLY);
		db=new Database(new File("target/iiif-crawl-repository-ncsu"), AccessMode.READ_ONLY);
//		db=new Database(new File("target/iiif-crawl-repository"), AccessMode.READ_ONLY);
		int cnt=0;
		for(byte[] mdBytes : db.getAllRecordsData()) {
			cnt++;
			Object metadata = IoUtil.fromByteArray(mdBytes);
			try {
				if(metadata instanceof String) {
//					String fromByteArray = (String) metadata;
//					if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
//						break;
				} else if(metadata instanceof IiifPresentationMetadata) {
//					IiifPresentationMetadata iiifMetadata = (IiifPresentationMetadata) metadata;
//					for(Iterator<RawDataRecord> it=iiifMetadata.getSeeAlso().iterator(); it.hasNext() ; ) {
//						RawDataRecord seeAlso = it.next();
//						try {
////							System.out.println(iiifMetadata.getManifestUrl());
////					System.out.println(seeAlso.getFormat());
////							System.out.println(seeAlso.getProfile());
//							if(seeAlso.getProfile()!=null && seeAlso.getProfile().equalsIgnoreCase("http://www.europeana.eu/schemas/edm/")) {
//								String fromByteArray = new String(seeAlso.getContent(), "UTF-8");
//								metadataExporter.export(fromByteArray);
//								if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
//									break;
//							}
//						} catch (Exception e) {
//							System.err.println(seeAlso);
//							System.err.println(new String(seeAlso.getContent(), "UTF-8"));
//							e.printStackTrace();
//						}
//					}
				} else if(metadata instanceof IiifManifest) {
					IiifManifest manifest=(IiifManifest) metadata;
					for(RawDataRecord seeAlso: manifest.getMetadata().getSeeAlso()) {
						if(seeAlso.getProfile()!=null && seeAlso.getProfile().equalsIgnoreCase("http://www.europeana.eu/schemas/edm/")) {
//							String fromByteArray = new String(seeAlso.getContent(), "UTF-8");
//							metadataExporter.export(fromByteArray);
//							if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
//								break;
						} else {//TODO
							//testing DC data from wellcome
							String md = new String(seeAlso.getContent());
							System.out.println(md);
						}
					}
				}
			} catch (Exception e) {
				System.err.println(metadata);
				e.printStackTrace();
			}
//			if(cnt % 1000 == 0) 
//				System.out.println("Progress: "+ cnt+" records ("+metadataExporter.getExportRecordsCount() + " exported)");
		}
		
//		
//		
//		
//		int export_records_cnt=0;
//		for(byte[] mdBytes : db.getAllRecordsData()) {
//			IiifPresentationMetadata md = (IiifPresentationMetadata)IoUtil.fromByteArray(mdBytes);
////			System.out.println(md);
//			System.out.println(md.getManifestUrl());
//			if(md.getSeeAlso().isEmpty())
//				System.out.println("no seeAlso");
//			else {
//				for(RawDataRecord seeAlso: md.getSeeAlso()) {
//					String filename = URLEncoder.encode(md.getManifestUrl(), "UTF8")+".mods.xml";
//					if(seeAlso.getProfile().equals("http://www.europeana.eu/schemas/edm/"))
//						filename = URLEncoder.encode(md.getManifestUrl(), "UTF8")+".edm.xml";
//					else if (seeAlso.getProfile().startsWith("http://www.loc.gov/mods/")) 
//						filename = URLEncoder.encode(md.getManifestUrl(), "UTF8")+".mods.xml";
//						
//					File exportFile=new File(exportFolder, filename);
//					FileOutputStream fileOutputStream = new FileOutputStream(exportFile);
//					IOUtils.write(seeAlso.getContent(), fileOutputStream);
//					fileOutputStream.flush();
//					fileOutputStream.close();					
//				}
//				export_records_cnt++;
//			}
//			if(export_records_cnt>=max_export_records)
//				break;
//		}
		db.shutdown();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}
}
