package inescid.opaf.data.profile;

import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import inescid.opaf.data.RdfReg;
import inescid.opaf.data.profile.UsageStats.ClassUsageStats;

public class UsageProfiler {
	
	UsageStats usageStats=new UsageStats();
	
	public void collect(Model model) {
		ResIterator subjs = model.listSubjects();
		
		ArrayList<ClassUsageStats> classesOfSubject=new ArrayList<>(3);
		ArrayList<String> classesOfSubjectUris=new ArrayList<>(3);
		for(Resource r: subjs.toList()) {
//			System.out.println(r.getURI());
			StmtIterator typeProperties = model.listStatements(r, RdfReg.RDF_TYPE, (RDFNode)null);
			for(Statement st : typeProperties.toList()) {
//				System.out.println(st);
				String clsUri = st.getObject().asNode().getURI();
				ClassUsageStats classStats = usageStats.getClassStats(clsUri);
				classStats.incrementClassUseCount();
				classesOfSubject.add(classStats);
			
				classesOfSubjectUris.add(clsUri);
			}
			
			if(classesOfSubject.isEmpty()) {
				classesOfSubject.add(usageStats.getClassStats(RdfReg.RDFS_RESOURCE.getURI()));
				ClassUsageStats classStats = usageStats.getClassStats(RdfReg.RDFS_RESOURCE.getURI());
				classStats.incrementClassUseCount();
			} else if(classesOfSubjectUris.size()>1)
				System.out.println("WARN: resource has multiple types: "+ classesOfSubjectUris);
			r.listProperties(RdfReg.RDF_TYPE);
			StmtIterator properties = model.listStatements(r, null, (RDFNode)null);
			for(Statement st : properties.toList()) {
				if(st.getPredicate().equals(RdfReg.RDF_TYPE)) continue;
				for(ClassUsageStats stat : classesOfSubject)
					stat.getPropertiesStats().incrementTo(st.getPredicate().getURI());
			}
			classesOfSubject.clear();
			classesOfSubjectUris.clear();
		}
	}


	public UsageStats getUsageStats() {
		return usageStats;
	}
	
}
