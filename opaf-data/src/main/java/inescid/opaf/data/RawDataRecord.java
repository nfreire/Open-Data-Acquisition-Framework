package inescid.opaf.data;

import java.io.Serializable;

public class RawDataRecord extends DataSpec implements Serializable {
	private static final long serialVersionUID = 1L;

	String url;
	byte[] content;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String seeAlsoUrl) {
		this.url = seeAlsoUrl;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] seeAlsoContent) {
		this.content = seeAlsoContent;
	}
	@Override
	public String toString() {
		return "RawDataRecord [seeAlsoUrl=" + url + ", seeAlsoContentType=" + contentType
				+ ", format=" + format + ", profile=" + profile
				+ "]";
	}
	public boolean validateSintax() {
		//TODO
		return true;
//			try {
//				if(profile.equals("http://www.europeana.eu/schemas/edm/")) {
//					Document dom = XmlUtil.parseDom(new ByteArrayInputStream(bs));
//					if (dom.getDocumentElement().getLocalName().equals("RDF"))
//						return true;
//					return false;
//				}
//				return true;
//			} catch (Exception e) {
//				return false;
//			}
//		}
//		return false;
	}

}
