package inescid.opaf.manager.iiif;

import java.util.Date;

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

	public DataSourceIiif(IiifSource src, ManifestCrawlHandler handler) {
		this.src=src;
		this.handler=handler;
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
	

}
