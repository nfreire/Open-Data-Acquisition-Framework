package inescid.opaf.data.profile;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class UsageStats {
	
	public class ClassUsageStats {
		MapOfInts<String> propertiesStats=new MapOfInts();
		int classUseCount=0;
		
		public MapOfInts<String> getPropertiesStats() {
			return propertiesStats;
		}
		
		public int getClassUseCount() {
			return classUseCount;
		}

		public void incrementClassUseCount() {
			classUseCount++;
		}
	}
	
	/**
	 * Map of Class URIs -> Property URIs -> #uses
	 */
	Map<String, ClassUsageStats> stats=new HashMap<>();

	public ClassUsageStats getClassStats(String classURI) {
		ClassUsageStats ret=stats.get(classURI);
		if(ret==null) {
			ret=new ClassUsageStats();
			stats.put(classURI, ret);
		}
		return ret;
	}
	
	public String toString() {
		StringBuilder sb=new StringBuilder();
		for(String cls: stats.keySet()) {
			ClassUsageStats clsStats = stats.get(cls);
			sb.append("Class: ").append(clsStats.getClassUseCount()).append(" - ").append(cls).append("\n");
			for(Entry<String, Integer> prop: clsStats.getPropertiesStats().getSortedEntries()) {
				sb.append(String.format("  %5d - %s\n", prop.getValue(), prop.getKey()));
			}
		}
		return sb.toString();
	}
	public String toCsv() {
		StringBuilder sb=new StringBuilder();
		sb.append("class,class count,property,property count,edm mapping class,edm mapping property,mapping notes\n");
		for(String cls: stats.keySet()) {
			ClassUsageStats clsStats = stats.get(cls);
			sb.append(cls).append(",").append(clsStats.getClassUseCount()).append(",");
			if(clsStats.getPropertiesStats().isEmpty())
				sb.append(",\n");
			else {
				boolean first=true;
				for(Entry<String, Integer> prop: clsStats.getPropertiesStats().getSortedEntries()) {
					if(first) { 
						first=false;
					} else 
						sb.append(",,");
					sb.append(String.format("%s,%d\n", prop.getKey(), prop.getValue()));
				}
			}
		}
		return sb.toString();
	}
	
}