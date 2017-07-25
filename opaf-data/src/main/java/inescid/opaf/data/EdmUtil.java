package inescid.opaf.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import inescid.util.XPathUtil;
import inescid.util.XmlUtil;


public class EdmUtil {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EdmUtil.class);
	
	public static final Map<String, String> xpathEdmPrefixMap=new HashMap<String, String>() {{
		put("edm", RdfReg.NsEdm);
		put("dc", RdfReg.NsDc);
		put("dcterms", RdfReg.NsDcterms);
		put("ore", RdfReg.NsOre);
	}};
	
	
//	public static boolean hasOpenRights(Document edmDom, NewspaperEcloudRecord ecloudRec) {
//		Element rights;
//		try {
//			rights = XPathUtil.queryDomForElement(XmlNsUtil.xpathEdmPrefixMap, "//edm:rights", edmDom);
//		} catch (XPathExpressionException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		}
//		if (rights!=null) { 
//			String rightsTxt=rights.getAttributeNS(XmlNsUtil.RDF, "resource");
//			if(StringUtils.isEmpty(rightsTxt))
//				rightsTxt=XmlUtil.getElementText(rights);
//			if(!StringUtils.isEmpty(rightsTxt)) {
//				if(rightsTxt.contains("creativecommons.org") && (rightsTxt.contains("/publicdomain/") || rightsTxt.contains("/by/")))
//					return true;
//				else	
//					log.debug("Record without open rights: "+ecloudRec.getCloudId()+ " " + rightsTxt);
//			} else 
//				log.warn("record without edm:rights: "+ecloudRec.getCloudId()+ " " + rightsTxt);
//		} else 
//			log.warn("Record without edm:rights: "+ecloudRec.getCloudId());
//		return false;
//	}
	
	public static String getIssuedDate(Document edm) {
		try {
			NodeList types = XPathUtil.queryDom(xpathEdmPrefixMap, "//edm:ProvidedCHO/dcterms:issued", edm);
			if (types.getLength()>0) 
				return XmlUtil.getElementText(((Element)types.item(0))).trim();
			return null;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static String getTitleOrDescription(Document edm) {
		try {
			NodeList types = XPathUtil.queryDom(xpathEdmPrefixMap, "//edm:ProvidedCHO/dc:title", edm);
			if (types.getLength()>0) 
				return XmlUtil.getElementText(((Element)types.item(0))).trim();
			types = XPathUtil.queryDom(xpathEdmPrefixMap, "//edm:ProvidedCHO/dc:description", edm);
			if (types.getLength()>0) 
				return XmlUtil.getElementText(((Element)types.item(0))).trim();
			return null;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static String getEdmRights(Document edm) {
		try {
			String titleText = null;
			NodeList types = XPathUtil.queryDom(xpathEdmPrefixMap, "//ore:Aggregation/edm:rights", edm);
			if (types.getLength()>0) {
				titleText = ((Element)types.item(0)).getAttributeNS(RdfReg.NsRdf, "resource");
				if(StringUtils.isEmpty(titleText))
					titleText = XmlUtil.getElementText(((Element)types.item(0))).trim();
			}
			return titleText;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static URI getIsShownByOrAt(Document edm) {
		try {
			NodeList els = XPathUtil.queryDom(xpathEdmPrefixMap, "(//ore:Aggregation/edm:isShownBy || //ore:Aggregation/edm:isShownAt)", edm);
			if (els.getLength()>0) 
				return XmlUtil.getElementUriFromRdfResourceOrText(((Element)els.item(0)));
			return null;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static URI getIsShownBy(Document edm) {
		try {
			NodeList els = XPathUtil.queryDom(xpathEdmPrefixMap, "//ore:Aggregation/edm:isShownBy", edm);
			if (els.getLength()>0) 
				return XmlUtil.getElementUriFromRdfResourceOrText(((Element)els.item(0)));
			return null;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static URI getIsShownAt(Document edm) {
		try {
			NodeList els = XPathUtil.queryDom(xpathEdmPrefixMap, "//ore:Aggregation/edm:isShownAt", edm);
			if (els.getLength()>0) 
				return XmlUtil.getElementUriFromRdfResourceOrText(((Element)els.item(0)));
			return null;
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Element getEdmRightsElement(Document edm) {
		try {
			NodeList hits = XPathUtil.queryDom(xpathEdmPrefixMap, "//ore:Aggregation/edm:rights", edm);
			if (hits.getLength()>0) 
				return (Element)hits.item(0);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return null;
	}

	public static List<Integer> getIssuedDates(Document edmDom) {
		String issuedDateStr = getIssuedDate(edmDom);
		List<Integer> ret=new ArrayList<>(2);
		if(issuedDateStr!=null) {
			if(issuedDateStr.length()==10) {//yyyy-MM-dd
				try {
					ret.add(Integer.parseInt(issuedDateStr.substring(0, 4)));
				} catch (NumberFormatException e) {
					log.info(issuedDateStr,e);
				}
			} else if(issuedDateStr.length()==22) {//yyyy-MM-dd - yyyy-MM-dd
				try {
					ret.add(Integer.parseInt(issuedDateStr.substring(0, 4)));
					ret.add(Integer.parseInt(issuedDateStr.substring(13, 17)));
				} catch (NumberFormatException e) {
					log.info(issuedDateStr,e);
				}
				
			}
		}
		return ret;
	}
}
