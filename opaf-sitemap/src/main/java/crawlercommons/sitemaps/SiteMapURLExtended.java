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
	 
    /** URL found in Sitemap (required) */
	protected URL iiifManifest;

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
}
