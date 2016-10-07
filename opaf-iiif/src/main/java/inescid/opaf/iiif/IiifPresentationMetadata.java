package inescid.opaf.iiif;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IiifPresentationMetadata implements Serializable{
	private static final long serialVersionUID = 1L;
	String manifestUrl;
	String title;
	String navDate;
	final List<IiifMetadataElement> metadata=new ArrayList<>();
	
	List<IiifSeeAlsoProperty> seeAlso=new ArrayList<>();
	
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
	
	public void addSeeAlso(IiifSeeAlsoProperty element) {
		seeAlso.add(element);
	}

	public void addSeeAlso(Collection<IiifSeeAlsoProperty> element) {
		seeAlso.addAll(element);
	}
	

	@Override
	public String toString() {
		return "IiifPresentationMetadata [manifestUrl=" + manifestUrl + ", metadata=" + metadata + ", seeAlso="
				+ seeAlso + "]";
	}

	public List<IiifSeeAlsoProperty> getSeeAlso() {
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

}