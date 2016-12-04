package inescid.opaf.manager.test;

import java.io.File;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.DataSource;
import inescid.opaf.manager.DataSourceManager;
import inescid.opaf.manager.iiif.CrawlingHandlerForRepositoryStorage;
import inescid.opaf.manager.iiif.DataSourceIiif;
import inescid.opaf.sitemap.CrawlResourceHandler;
import inescid.opaf.sitemap.SitemapResourceCrawler;

public class RunHarvestingUcdPoetryVideos {

	public static void main(String[] args) {
		RunHarvestingManager.main(new String[] {"src/config/opaf_data_sources_ucd_poetry.properties"});
		
	}
}
