package inescid.opaf.data.convert;

import org.apache.jena.rdf.model.Model;

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;

public abstract class DataConverter {
	DataSpec targetSpec;
	
	public abstract RawDataRecord convert(RawDataRecord source, Model additionalStatements);
}
