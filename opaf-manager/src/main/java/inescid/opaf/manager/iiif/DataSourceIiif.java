package inescid.opaf.manager.iiif;

import java.io.File;
import java.util.Date;
import java.util.Properties;

import org.apache.http.client.fluent.Content;
import org.apache.http.impl.io.SessionOutputBufferImpl;
import org.apache.jena.rdf.model.Model;

import inescid.opaf.framework.CrawlingSession;
import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifPresentationApiCrawler;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.DataSource;

public class DataSourceIiif implements DataSource {
	IiifSource src;
	ManifestCrawlHandler handler;

	public DataSourceIiif() {
	}
	
	public void init(Properties props) throws Exception {
//			IiifSource src, ManifestCrawlHandler handler) {
		this.src=new IiifSource(props.getProperty("name"));
		
		if(props.containsKey("ManifestCrawlHandler.class")) {
//			if(props.containsKey("opaf.datasource.iiif.properties.ManifestCrawlHandler.class")) {
			this.handler = (ManifestCrawlHandler) Class.forName(
					props.getProperty("ManifestCrawlHandler.class").trim()).newInstance();
		}else
			this.handler=new CrawlingHandlerForRepositoryStorage(new File(props.getProperty("repository.folder")));
		src.getSitemapsUrls().add(props.getProperty("sitemap"));
	}

	@Override
	public String getName() {
		return src.getName();
	}

	@Override
	public void synchronizeData(Date lastUpdate, CrawlingSystem crawlingSys) {

		IiifPresentationApiCrawler crawler=new IiifPresentationApiCrawler(src, handler, crawlingSys);
		crawler.run();
	}

	public ManifestCrawlHandler getHandler() {
		return handler;
	}

	public void setHandler(ManifestCrawlHandler handler) {
		this.handler = handler;
	}
	

}
