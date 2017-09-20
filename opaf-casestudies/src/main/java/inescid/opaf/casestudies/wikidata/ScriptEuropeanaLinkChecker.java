package inescid.opaf.casestudies.wikidata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;

import inescid.opaf.casestudies.wikidata.WikidataEntityCache.WikidataEntitySummary;

public class ScriptEuropeanaLinkChecker {

	public static void main(String[] args) throws Exception {
		WikidataEntityCache wikidataClient=new WikidataEntityCache(WikidataEuropeanaConstants.CACHE_WIKIDATA_PROPERTIES);
		
		Reader csvReader=new InputStreamReader(new FileInputStream(WikidataEuropeanaConstants.REFERENCES_CSV), "UTF-8");
		BufferedReader csvBufferedReader = new BufferedReader(csvReader);
		
		ArrayList<String> brokenUrls=new ArrayList<>();
		int checkedCount=0;
		
		boolean isTitleRow=true;
		int sectionOfReport=1;
		while(csvBufferedReader.ready()) {
			String prop = csvBufferedReader.readLine();
			if(isTitleRow) {
				isTitleRow=false;
			} else if (StringUtils.isEmpty(prop)){
				isTitleRow=true;
				sectionOfReport++;
			}else {
				if (sectionOfReport==3) {
				      checkedCount++;
//				      if(checkedCount % 5 != 0)
//				    	  continue;
				      
					String wdId=prop.substring(prop.indexOf(',')+1);
					if(wdId.contains(","))
						wdId=wdId.substring(0,wdId.indexOf(',')-1);
					String europeanaUrlString = prop.substring(1,  prop.indexOf(',')-1);
					URL url=new URL(europeanaUrlString);
					HttpURLConnection  con = (HttpURLConnection) url.openConnection();
				      con.setRequestMethod("HEAD");
				      boolean ok=(con.getResponseCode() == HttpURLConnection.HTTP_OK);
				      if (!ok) {
				    	  System.out.println("broken: "+europeanaUrlString+ " - "+con.getResponseCode());
				    	  brokenUrls.add(europeanaUrlString);
				    	  FileUtils.write(new File("target/wikidata_broken_links_to_europeana.txt"), europeanaUrlString+"\n", "UTF-8", true);
				      }
				      if(checkedCount % 50 ==0) 
				    	  System.out.println("Broken found: "+ brokenUrls.size()+"/"+checkedCount);
				      
				}
			}
		}
		
		csvBufferedReader.close();
	}
	
}
