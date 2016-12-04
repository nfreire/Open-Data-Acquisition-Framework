package crawlercommons.sitemaps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import crawlercommons.sitemaps.SiteMapURL;
import crawlercommons.sitemaps.SiteMapURL.ChangeFrequency;

public class SiteMapURLExtended extends SiteMapURL{

    private static Logger LOG = LoggerFactory.getLogger(SiteMapURLExtended.class);
	 
	protected URL iiifManifest;
	protected URL iiifCollection;
	protected URL edmMetadata;

	
	
	public URL getIiifManifest() {
		return iiifManifest;
	}

	public void setIiifManifest(URL iiifManifest) {
		this.iiifManifest = iiifManifest;
	}
	public void setIiifManifest(String iiifManifest) {
        try {
            this.iiifManifest = new URL(iiifManifest);
        } catch (MalformedURLException e) {
            LOG.error("Bad url: [{}], Exception: {}", iiifManifest, e.toString());
            this.iiifManifest = null;
        }
	}

	public SiteMapURLExtended(String url, boolean valid) {
		super(url, valid);
	}

	public SiteMapURLExtended(String url, String lastModified, String changeFreq, String priority, boolean valid) {
		super(url, lastModified, changeFreq, priority, valid);
	}
	public SiteMapURLExtended(String url, String lastModified, String changeFreq, String priority, boolean valid, String manifest) {
		super(url, lastModified, changeFreq, priority, valid);
		setIiifManifest(manifest);
	}

	public SiteMapURLExtended(URL url, boolean valid) {
		super(url, valid);
	}

	public SiteMapURLExtended(URL url, Date lastModified, ChangeFrequency changeFreq, double priority, boolean valid) {
		super(url, lastModified, changeFreq, priority, valid);
	}

	public SiteMapURLExtended(URL url, Date lastModified, ChangeFrequency changeFreq, double priority, boolean valid, String manifest) {
		super(url, lastModified, changeFreq, priority, valid);
		setIiifManifest(manifest);
		
	}
	
	 @Override
	    public String toString() {
	        StringBuilder sb = new StringBuilder(super.toString());
	        sb.append(", iiifManifest = ").append(iiifManifest);
	        return sb.toString();
	    }

	public URL getIiifCollection() {
		return iiifCollection;
	}

	public void setIiifCollection(URL iiifCollection) {
		this.iiifCollection = iiifCollection;
	}

	public URL getEdmMetadata() {
		return edmMetadata;
	}

	public void setEdmMetadata(URL edmMetadata) {
		this.edmMetadata = edmMetadata;
	}

	public void setIiifCollection(String colectionUrl) {
        try {
            this.iiifCollection = new URL(colectionUrl);
        } catch (MalformedURLException e) {
            LOG.error("Bad url: [{}], Exception: {}", iiifCollection, e.toString());
            this.iiifCollection = null;
        }
		
	}
	public void setEdmMetadata(String edmUrl) {
		try {
			this.edmMetadata = new URL(edmUrl);
		} catch (MalformedURLException e) {
			LOG.error("Bad url: [{}], Exception: {}", edmMetadata, e.toString());
			this.iiifCollection = null;
		}
		
	}
}
