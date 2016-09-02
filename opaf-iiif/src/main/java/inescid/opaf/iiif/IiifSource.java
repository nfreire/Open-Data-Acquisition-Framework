package inescid.opaf.iiif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IiifSource {

	String robotTxtUrl;
	
	List<String> sitemapsUrls=new ArrayList<String>();

	List<String> harvestingIiifUrls=new ArrayList<String>();
	
	String name;
	
	public List<String> getSitemapsUrls() {
		return sitemapsUrls;
	}

	public String getRobotTxtUrl() {
		return robotTxtUrl;
	}
	
	
	public void setRobotTxtUrl(String robotTxtUrl) {
		this.robotTxtUrl = robotTxtUrl;
	}

	public void setSitemapsUrls(List<String> sitemapsUrls) {
		this.sitemapsUrls = sitemapsUrls;
	}


	
	public IiifSource(String name, List<String> harvestingIiifUrls) {
		super();
		this.name = name;
		this.harvestingIiifUrls = harvestingIiifUrls;
	}
	
	
	public IiifSource(String name, String... harvestingIiifUrls) {
		super();
		this.name = name;
		this.harvestingIiifUrls = Arrays.asList(harvestingIiifUrls);
	}


	public Iterable<String> getHarvestingIiifUrls() {
		return harvestingIiifUrls;
	}


	public void setHarvestingIiifUrls(List<String> harvestingIiifUrls) {
		this.harvestingIiifUrls = harvestingIiifUrls;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
