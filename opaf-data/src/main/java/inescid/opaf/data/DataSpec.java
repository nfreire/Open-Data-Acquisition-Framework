package inescid.opaf.data;

import java.io.Serializable;

public class DataSpec implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected String contentType;
	protected String format;
	protected String profile;
	
	public DataSpec() {
	}
	
	public DataSpec(String contentType, String format, String profile) {
		super();
		this.contentType = contentType;
		this.format = format;
		this.profile = profile;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String seeAlsoContentType) {
		this.contentType = seeAlsoContentType;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
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
	@Override
	public String toString() {
		return "DataSpec [contentType=" + contentType + ", format=" + format + ", profile=" + profile + "]";
	}

}
