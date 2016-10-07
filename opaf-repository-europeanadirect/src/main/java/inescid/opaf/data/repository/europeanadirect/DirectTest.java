package inescid.opaf.data.repository.europeanadirect;

import java.math.BigDecimal;

import eu.europeana.ApiException;
import eu.europeana.europeanadirect.ObjectApi;
import eu.europeana.europeanadirect.model.ObjectLanguageAware;

public class DirectTest {
	public static void main(String[] args) {
		eu.europeana.europeanadirect.model.Object cho=new eu.europeana.europeanadirect.model.Object();
		ObjectLanguageAware choLangFields = new ObjectLanguageAware();
		choLangFields.setTitle("Titulo inventado");
		cho.getLanguageAwareFields().add(choLangFields);
		
		ObjectApi apiInstance = new ObjectApi();
		apiInstance.getApiClient().setBasePath("http://europeana-direct.semantika.eu/ED/api");
//		apiInstance.setApiClient(apiInstance.getApiClient());
		try {
		    BigDecimal result = apiInstance.objectPost(cho);
		    System.out.println(result);
		} catch (ApiException e) {
		    System.err.println("Exception when calling ObjectApi#objectPost");
		    e.printStackTrace();
		}
	}
}
