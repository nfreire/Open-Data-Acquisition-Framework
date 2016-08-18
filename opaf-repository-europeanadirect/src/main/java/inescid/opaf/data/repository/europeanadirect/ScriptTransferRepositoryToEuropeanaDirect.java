package inescid.opaf.data.repository.europeanadirect;

import java.io.File;
import java.math.BigDecimal;

import org.apache.commons.io.IOUtils;
import org.mapdb.BTreeMap;

import eu.europeana.ApiException;
import eu.europeana.europeanadirect.ObjectApi;
import eu.europeana.europeanadirect.model.Object;
import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;

public class ScriptTransferRepositoryToEuropeanaDirect {

	
	public static void main(String[] args) throws Exception {
		ObjectApi apiInstance = new ObjectApi();
		apiInstance.getApiClient().setDebugging(true);
		apiInstance.getApiClient().setBasePath("http://europeana-direct.semantika.eu/ED/api");

		Database db;
		db=new Database(new File("../opaf-manager/target/iiif-crawl-repository"), AccessMode.READ_ONLY);
		for(byte[] mdBytes : db.getAllRecordsData()) {
			IiifPresentationMetadata md = (IiifPresentationMetadata)IoUtil.fromByteArray(mdBytes);
			System.out.println(md);
			
			Object directMd = IiifPresentationMetadataConverterToDirectObject.convert(md);
			try {
			    BigDecimal result = apiInstance.objectPost(directMd);
			    System.out.println(result);
			} catch (ApiException e) {
			    System.err.println("Exception when calling ObjectApi#objectPost");
			    e.printStackTrace();
			}
		}
		db.shutdown();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}
}
