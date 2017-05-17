package inescid.opaf.manager.test;

import inescid.util.DevelopementSingleton;

public class RunHarvestingUcdPoetryVideos {

	public static void main(String[] args) {
		RunHarvestingManager.main(new String[] {"src/config/opaf_data_sources_ucd_poetry.properties"});
		if(DevelopementSingleton.DEVEL_TEST)
			System.out.println(DevelopementSingleton.RESOURCE_HARVEST_CNT + " recs harvested");
	}
}
