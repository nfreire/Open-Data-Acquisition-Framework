package inescid.opaf.data.convert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.GraphListener;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Node_Blank;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.util.graph.GraphListenerBase;
import org.w3c.dom.Document;

import com.github.andrewoma.dexx.collection.ArrayList;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.util.XmlUtil;

public class SchemaOrgToEdmDataConverter extends DataConverter {

	
	private static final Charset UTF8=Charset.forName("UTF8");
	
	public SchemaOrgToEdmDataConverter() {
	}
	
	
	@Override
	public RawDataRecord convert(RawDataRecord source, Model additionalStatements) {
		Model edmModelRdf = ModelFactory.createDefaultModel();
		if(source.getContentType().equals("application/json") && source.getFormat().equals("application/ld+json")) {
			Model ldModelRdf = ModelFactory.createDefaultModel();
			ByteArrayInputStream bytesIs = new ByteArrayInputStream(source.getContent());
			
			RDFDataMgr.read(ldModelRdf, bytesIs, source.getUrl(), Lang.JSONLD);
			try {
				bytesIs.close(); 
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e); 
			}
			
			ResIterator cWorks = ldModelRdf.listSubjectsWithProperty(RdfReg.RDF_TYPE, RdfReg.SCHEMAORG_CREATIVE_WORK);
			while(cWorks.hasNext()) {
				Resource cw = cWorks.next();
				
				Resource choResource=edmModelRdf.createResource(cw.getURI(), RdfReg.EDM_PROVIDED_CHO);
				StmtIterator cwStms = cw.listProperties();
				while (cwStms.hasNext()) {
					Statement st = cwStms.next();
					choResource.addProperty(st.getPredicate(), st.getObject());
					System.out.println(st);
				}
			}
//			StmtIterator choStms = ldModelRdf.listStatements();
//			
//			
//			
//			if(listener.getMainSubject().isBlank()) {
////				Node_Blank choBlNode= (Node_Blank) listener.getMainSubject();
////				System.out.println(choBlNode.getBlankNodeLabel());
//
//				Resource newChoResource = ldModelRdf.createResource(source.getUrl(), RdfReg.EDM_PROVIDED_CHO);
//
//				ArrayList<Statement> toRemove=new ArrayList<>();
//				StmtIterator choStms = ldModelRdf.listStatements();
//				while (choStms.hasNext()) {
//					Statement st = choStms.next();
//					if(st.getSubject().toString().equals(listener.getMainSubject().toString())) {
////						System.out.println(st.getSubject());
////						System.out.println(st.getSubject().getId().getBlankNodeId());
//						newChoResource.addProperty(st.getPredicate(), st.getObject());
//					}
//					
//				}
//				for(Statement st:  toRemove)
//					ldModelRdf.remove(st);
//				choResource=newChoResource;
//			} else {
//				choResource=ldModelRdf.getResource(listener.getMainSubject().getURI());
//				choResource.addProperty(RdfReg.RDF_TYPE, RdfReg.EDM_PROVIDED_CHO);
//			}
			
			
//			System.out.println(additionalStatements);
//			if(additionalStatements!=null)
//			 	ldModelRdf.add( additionalStatements.listStatements());
//			EdmRdfToXmlSerializer xmlSerializer = new EdmRdfToXmlSerializer(choResource, ldModelRdf);
//			Document edmDom = xmlSerializer.getXmlDom();
//			RawDataRecord edmRawRecord=new RawDataRecord();
//			String domString = XmlUtil.writeDomToString(edmDom);
////			System.out.println(domString);
//			edmRawRecord.setContent(domString.getBytes(UTF8));
//			edmRawRecord.setContentType("application/xml");
//			edmRawRecord.setProfile("http://www.europeana.eu/schemas/edm/");
//			return edmRawRecord;
		}
		return null;

		
	}

}
