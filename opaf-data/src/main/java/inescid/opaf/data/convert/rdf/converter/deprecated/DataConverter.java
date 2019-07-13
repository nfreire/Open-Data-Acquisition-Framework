package inescid.opaf.data.convert.rdf.converter.deprecated;

import org.apache.jena.rdf.model.Model;

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;

@Deprecated
public abstract class DataConverter {
	DataSpec targetSpec;
	
	public abstract RawDataRecord convert(RawDataRecord source, Model additionalStatements);
}
