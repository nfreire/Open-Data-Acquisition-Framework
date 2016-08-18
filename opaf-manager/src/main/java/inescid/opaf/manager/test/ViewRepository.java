package inescid.opaf.manager.test;

import java.io.File;

import org.apache.commons.io.IOUtils;
import org.mapdb.BTreeMap;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;

public class ViewRepository {

	
	public static void main(String[] args) throws Exception {
		Database db;
		db=new Database(new File("target/iiif-crawl-repository"), AccessMode.READ_ONLY);
		for(byte[] mdBytes : db.getAllRecordsData()) {
			IiifPresentationMetadata md = (IiifPresentationMetadata)IoUtil.fromByteArray(mdBytes);
			System.out.println(md);
		}
		db.shutdown();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}
}
