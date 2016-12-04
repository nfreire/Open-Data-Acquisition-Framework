package crawlercommons.sitemaps;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.xml.XMLLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import crawlercommons.sitemaps.SiteMap;
import crawlercommons.sitemaps.SiteMapParser;
import crawlercommons.sitemaps.SiteMapURL;
import inescid.util.XmlUtil;
import crawlercommons.sitemaps.AbstractSiteMap.SitemapType;

public class SiteMapParserWithExtentions extends SiteMapParserProtected {

	public SiteMapParserWithExtentions() {
		super();
	}
	
	public SiteMapParserWithExtentions(boolean strict) {
		super(strict);
	}
	
	   /**
     * Parse XML that contains a valid Sitemap. Example of a Sitemap: <?xml
     * version="1.0" encoding="UTF-8"?> <urlset
     * xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"> <url>
     * <loc>http://www.example.com/</loc> <lastmod>2005-01-01</lastmod>
     * <changefreq>monthly</changefreq> <priority>0.8</priority> </url> <url>
     * <loc
     * >http://www.example.com/catalog?item=12&amp;desc=vacation_hawaii</loc>
     * <changefreq>weekly</changefreq> </url> </urlset>
     * 
     * @param doc
     */
	@Override
    protected SiteMap parseXmlSitemap(URL sitemapUrl, Document doc) {

        SiteMap sitemap = new SiteMap(sitemapUrl);
        sitemap.setType(SitemapType.XML);

        NodeList list = doc.getElementsByTagName("url");

        // Loop through the <url>s
        for (int i = 0; i < list.getLength(); i++) {

            Node n = list.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) n;
                String loc = getElementValue(elem, "loc");
                try {
                    URL url = new URL(loc);
                    String lastMod = getElementValue(elem, "lastmod");
                    String changeFreq = getElementValue(elem, "changefreq");
                    String priority = getElementValue(elem, "priority");
                    String manifest = getElementValue(elem, "http://iiif.io/api/presentation/2/", "manifest");
                    String colection = getElementValue(elem, "http://iiif.io/api/presentation/2/", "within");
                    String edm = getResourceSyncLn(elem, "describedby", "http://www.europeana.eu/schemas/edm/");;
                   
                    if(manifest==null) 
                        manifest = getResourceSyncLn(elem, "alternate", "http://iiif.io/api/presentation/2.1/");
                    if(colection==null) 
                    	colection = getResourceSyncLn(elem, "colection", "http://iiif.io/api/presentation/2.1/");
                   
                    boolean valid = urlIsLegal(sitemap.getBaseUrl(), url.toString());

                    if (valid || !isStrict()) {
                    	SiteMapURLExtended sUrl = new SiteMapURLExtended(url.toString(), lastMod, changeFreq, priority, valid);
                    		if(manifest!=null) 
                    			sUrl.setIiifManifest(manifest);
                    		if(colection!=null) 
                    			sUrl.setIiifCollection(colection);
                    		if(edm!=null)
                    			sUrl.setEdmMetadata(edm);
                    		
                    		
                    		sitemap.addSiteMapUrl(sUrl);
                    		LOG.debug("  {}. {}", (i + 1), sUrl);
                    }
                } catch (MalformedURLException e) {
                    LOG.debug("Bad url: [{}]", loc);
                    LOG.trace("Can't create an entry with a bad URL", e);
                }
            }
        }
        sitemap.setProcessed(true);
        return sitemap;
    }
	
    private String getResourceSyncLn(Element elem, String rel, String conformsTo) {
       	//a hack since commonscrawler is not using namespaces
        NodeList list = elem.getChildNodes();
        for(int i=0; i<list.getLength(); i++) {
        	if(list.item(i) instanceof Element) {
        		Element el=(Element)list.item(i);
        		if(el.getNodeName().endsWith(":ln"))
//        			System.out.println(el.getAttribute("rel")+" "+el.getAttribute("dcterms:conformsTo"));
//        		System.out.println(rel+" "+conformsTo);
        			if(el.getAttribute("rel").equals(rel)
        				&& (conformsTo==null || el.getAttribute("dcterms:conformsTo").equals(conformsTo))
        				) {
        			return el.getAttribute("href");
        		}
        	}
        }
        return null;
	}

	protected String getElementValue(Element elem, String namespace, String elementName) {
    	//a hack since commonscrawler is not using namespaces
        NodeList list = elem.getChildNodes();
        for(int i=0; i<list.getLength(); i++) {
        	if(list.item(i) instanceof Element) {
        		if(list.item(i).getNodeName().endsWith(":"+elementName))
        			return  XmlUtil.getElementText((Element)list.item(i));
        	}
        }
        return null;
    }
}
