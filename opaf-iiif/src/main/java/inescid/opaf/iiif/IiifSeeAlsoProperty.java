package inescid.opaf.iiif;

import java.util.Arrays;

public class IiifSeeAlsoProperty {

	String seeAlsoUrl;
	String seeAlsoContentType;
	byte[] seeAlsoContent;
	String format;
	String profile;
	
	
	public String getSeeAlsoUrl() {
		return seeAlsoUrl;
	}
	public void setSeeAlsoUrl(String seeAlsoUrl) {
		this.seeAlsoUrl = seeAlsoUrl;
	}
	public String getSeeAlsoContentType() {
		return seeAlsoContentType;
	}
	public void setSeeAlsoContentType(String seeAlsoContentType) {
		this.seeAlsoContentType = seeAlsoContentType;
	}
	public byte[] getSeeAlsoContent() {
		return seeAlsoContent;
	}
	public void setSeeAlsoContent(byte[] seeAlsoContent) {
		this.seeAlsoContent = seeAlsoContent;
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
	@Override
	public String toString() {
		return "IiifSeeAlsoProperty [seeAlsoUrl=" + seeAlsoUrl + ", seeAlsoContentType=" + seeAlsoContentType
				+ ", seeAlsoContent=" + Arrays.toString(seeAlsoContent) + ", format=" + format + ", profile=" + profile
				+ "]";
	}
	
	
}
