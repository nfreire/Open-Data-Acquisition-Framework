package inescid.opaf.europeanadirect;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class Example {
	public static void main(String[] args) {
		try {
			final int maxRecords=10;
			
			//Harvesting the IIIF endpoint of the National Library of Wales
			IiifEndpointHarvester iiifHarvester=new IiifEndpointHarvester(
					new URL("http://dams.llgc.org.uk/iiif/newspapers/sitemap.xml"),
					new File("target/iiif_harvester_workdir"));

			iiifHarvester.runHarvest(new RecordHandler() {			
				int recordCount=0;
				
				@Override
				public boolean handleRecord(String jsonRecord) {
					System.out.print(jsonRecord);
					recordCount++;
					
					//return false when maxRecords is reached to stop the harvest in the middle
					return recordCount<maxRecords;
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
