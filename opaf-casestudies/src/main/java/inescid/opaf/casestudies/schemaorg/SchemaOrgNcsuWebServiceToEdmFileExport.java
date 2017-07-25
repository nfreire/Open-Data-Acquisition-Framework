package inescid.opaf.casestudies.schemaorg;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import inescid.opaf.data.DataSpec;
import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.convert.DataConversionManager;
import inescid.opaf.data.convert.DataConverter;
import inescid.opaf.data.convert.SchemaOrgToEdmDataConverter;

public class SchemaOrgNcsuWebServiceToEdmFileExport {

	static final Charset UTF8=Charset.forName("UTF8");
	static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
//	File repositoryFolder;
	boolean transformToEdmInternal;
	String provider;
	String dataProvider;
	
	DataConverter schemaOrgToEdmConverter;

	public SchemaOrgNcsuWebServiceToEdmFileExport(boolean transformToEdmInternal,
			String provider, String dataProvider) throws Exception {
		super();
//		this.repositoryFolder = repositoryFolder;
		this.transformToEdmInternal = transformToEdmInternal;
		this.provider = provider;
		this.dataProvider = dataProvider;

		schemaOrgToEdmConverter = DataConversionManager.getInstance().getConverter(new DataSpec("application/ld+json", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", "http://schema.org/"), new DataSpec("application/xml", "http://edm", null));
		((SchemaOrgToEdmDataConverter)schemaOrgToEdmConverter).setDataProvider(dataProvider);
	}


	/**
	 * @param recordId
	 * @return edm record in XML, UTF8 bytes
	 * @throws IOException 
	 */
	public Pair<byte[], byte[]> getRecordInSchemaOrgAndEdm(String recordNcsuId) throws IOException {
		URL ncsuWsUrl=new URL(recordNcsuId+"/schemaorg.json");
		byte[] schemaOrg = IOUtils.toByteArray(ncsuWsUrl);

		RawDataRecord seeAlso=new RawDataRecord();
		seeAlso.setUrl(recordNcsuId);
		seeAlso.setContent(schemaOrg);
		seeAlso.setContentType("application/json");
		seeAlso.setFormat("application/ld+json");
		RawDataRecord convertedEdm = schemaOrgToEdmConverter.convert(seeAlso, null);
		byte[] edm=convertedEdm.getContent();
		
		return new ImmutablePair<byte[], byte[]>(schemaOrg, edm);
	}
	
	

}
