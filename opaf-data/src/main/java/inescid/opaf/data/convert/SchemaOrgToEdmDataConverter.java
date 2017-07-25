package inescid.opaf.data.convert;

import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.w3c.dom.Document;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.rdf.RdfConversionSpecification;
import inescid.opaf.data.convert.rdf.RdfConverter;
import inescid.opaf.data.convert.rdf.ResourceTypeConversionSpecification;
import inescid.opaf.data.convert.rdf.SchemaOrgToEdmConversionSpecification;
import inescid.util.XmlUtil;

/**
 * @author nfrei
 *
 *	This class is not thread-safe
 *
 */
public class SchemaOrgToEdmDataConverter extends DataConverter {
	
	private static final Charset UTF8=Charset.forName("UTF8");
	
	Map<Resource, Resource> blankNodesMapped=new HashMap<Resource, Resource>();
	
	RdfConverter conv=new RdfConverter(SchemaOrgToEdmConversionSpecification.spec);
	
	String dataProvider;
	
	public SchemaOrgToEdmDataConverter() {
	}
	public SchemaOrgToEdmDataConverter(String dataProvider) {
		this.dataProvider=dataProvider;
	}
	
	
	
	@Override
	public RawDataRecord convert(RawDataRecord source, Model additionalStatements) {
		
		if(source.getContentType().equals("application/json") && source.getFormat().equals("application/ld+json")) {
			Model ldModelRdf = ModelFactory.createDefaultModel();
			String md = new String(source.getContent());
//			ByteArrayInputStream bytesIs = new ByteArrayInputStream(md.getBytes("UTF8"));
			StringReader mdReader = new StringReader(md);
			ldModelRdf.read(mdReader, null, "JSON-LD");
			mdReader.close();
			
			Resource mainTargetResource=conv.convert(ldModelRdf, source.getUrl());
			if(mainTargetResource==null)
				return null;
			ResIterator aggregations = mainTargetResource.getModel().listResourcesWithProperty(RdfReg.RDF_TYPE, RdfReg.ORE_AGGREGATION);
			Resource ag = aggregations.next();
			mainTargetResource.getModel().add(ag, RdfReg.EDM_AGGREGATED_CHO, mainTargetResource);
			
			if(dataProvider!=null) {
				StmtIterator provs = mainTargetResource.getModel().listStatements(ag, RdfReg.EDM_DATA_PROVIDER, (String) null);
				if(!provs.hasNext())
					mainTargetResource.getModel().add(ag, RdfReg.EDM_DATA_PROVIDER, dataProvider);
				provs = mainTargetResource.getModel().listStatements(ag, RdfReg.EDM_PROVIDER, (String) null);
				if(!provs.hasNext())
					mainTargetResource.getModel().add(ag, RdfReg.EDM_PROVIDER, dataProvider);
			}
			
//			System.out.println(additionalStatements);
//			if(additionalStatements!=null)
//			 	ldModelRdf.add( additionalStatements.listStatements());
			EdmRdfToXmlSerializer xmlSerializer = new EdmRdfToXmlSerializer(mainTargetResource);
			Document edmDom = xmlSerializer.getXmlDom();
			RawDataRecord edmRawRecord=new RawDataRecord();
			String domString = XmlUtil.writeDomToString(edmDom);
			System.out.println(domString);
			edmRawRecord.setContent(domString.getBytes(UTF8));
			edmRawRecord.setContentType("application/xml");
			edmRawRecord.setProfile("http://www.europeana.eu/schemas/edm/");
			return edmRawRecord;
		}
		return null;

		
	}
	public String getDataProvider() {
		return dataProvider;
	}
	public void setDataProvider(String dataProvider) {
		this.dataProvider = dataProvider;
	}


}
