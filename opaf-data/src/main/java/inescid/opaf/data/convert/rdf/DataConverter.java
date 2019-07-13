package inescid.opaf.data.convert.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;

public abstract class DataConverter {

	Resource mainTargetResource=null;
	
	public DataConverter() {
	}

	protected void addAdditionalStatements(Model additionalStatements) {
		if(additionalStatements!=null)
			mainTargetResource.getModel().add( additionalStatements.listStatements());
	}
	
	public abstract Resource convert(Resource source, Model additionalStatements);
}
