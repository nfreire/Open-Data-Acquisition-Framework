package inescid.opaf.iiif;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import inescid.opaf.data.RawDataRecord;

public class IiifPresentationMetadata implements Serializable{
	private static final long serialVersionUID = 1L;
	String manifestUrl;
	String shownByUrl;
	String shownAtUrl;
	String shownByService;
	String license;
	String title;
	String navDate;
	final List<IiifMetadataElement> metadata=new ArrayList<>();
	
	List<RawDataRecord> seeAlso=new ArrayList<>();
	
	public IiifPresentationMetadata(String manifestUrl) {
		super();
		this.manifestUrl = manifestUrl;
	}

	public String getManifestUrl() {
		return manifestUrl;
	}
 
	public void setManifestUrl(String manifestUrl) {
		this.manifestUrl = manifestUrl;
	}

	public List<IiifMetadataElement> getMetadata() {
		return metadata;
	}
	
	public void addMetadata(IiifMetadataElement element) {
		metadata.add(element);
	}

	public void addMetadata(Collection<IiifMetadataElement> element) {
		metadata.addAll(element);
	}
	
	public void addSeeAlso(RawDataRecord element) {
		seeAlso.add(element);
	}

	public void addSeeAlso(Collection<RawDataRecord> element) {
		seeAlso.addAll(element);
	}
	

	@Override
	public String toString() {
		return "IiifPresentationMetadata [manifestUrl=" + manifestUrl + ", metadata=" + metadata + ", seeAlso="
				+ seeAlso + "]";
	}

	public List<RawDataRecord> getSeeAlso() {
		return seeAlso;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNavDate() {
		return navDate;
	}

	public void setNavDate(String navDate) {
		this.navDate = navDate;
	}

	public String getShownByUrl() {
		return shownByUrl;
	}

	public void setShownByUrl(String shownByUrl) {
		this.shownByUrl = shownByUrl;
	}

	public String getShownByService() {
		return shownByService;
	}

	public void setShownByService(String shownByService) {
		this.shownByService = shownByService;
	}

	public String getShownAtUrl() {
		return shownAtUrl;
	}

	public void setShownAtUrl(String shownAtUrl) {
		this.shownAtUrl = shownAtUrl;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}


}