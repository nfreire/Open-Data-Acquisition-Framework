package inescid.opaf.manager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import inescid.opaf.framework.CrawlingSystem;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.IiifSource;
import inescid.opaf.iiif.ManifestCrawlHandler;
import inescid.opaf.manager.iiif.CrawlingHandlerForRepositoryStorage;
import inescid.opaf.manager.iiif.DataSourceIiif;

public class DataSourceManager {
	private static Pattern SOURCE_CLASS_PATTERN=Pattern.compile("opaf\\.datasource\\.([^\\.]+)\\.class"); 
	
	Map<String, DataSource> sources;
	CrawlingSystem crawlingSystem;
	
	public void init(Properties prop) throws Exception {
		File workingFolder=new File(prop.getProperty("opaf.workingdir").trim());


		crawlingSystem=new CrawlingSystem(workingFolder);
		sources=new HashMap<>();
		
		for(Object keyO: prop.keySet()) {
			String key=(String) keyO;
			Matcher m=SOURCE_CLASS_PATTERN.matcher(key);
			if(m.matches()) {
				String propsPrefix="opaf.datasource."+m.group(1)+".properties.";
				Properties dataSourceProperties=new Properties();
				for(Object keyO2: prop.keySet()) {
					String key2=(String) keyO2;
					if(key2.startsWith(propsPrefix)) 
						dataSourceProperties.setProperty(key2.substring(propsPrefix.length()), prop.getProperty(key2).trim());
				}
				DataSource dsInstance = (DataSource) Class.forName(prop.getProperty(key).trim()).newInstance();
				dsInstance.init(dataSourceProperties);
				addSource(dsInstance);
			}
		}
		
		
		
	}
	
	public void addSource(DataSource src) {
		sources.put(src.getName(), src);
	}
	
	public Collection<DataSource> getDataSources(){
		return sources.values();
	}
	
	public void syncAll() {
		for(DataSource ds: sources.values() ) {
			ds.synchronizeData(crawlingSystem);
		}
	}
	
	public void close() throws Exception {
		crawlingSystem.close();
	}
}
