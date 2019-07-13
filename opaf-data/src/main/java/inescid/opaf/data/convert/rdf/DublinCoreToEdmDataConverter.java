package inescid.opaf.data.convert.rdf;

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
import inescid.opaf.data.convert.EdmRdfToXmlSerializer;
import inescid.util.XmlUtil;

public class DublinCoreToEdmDataConverter extends DataConverter {
	@Override
	public Resource convert(Resource source, Model additionalStatements) {
		return null;
	}
}
//public class DublinCoreToEdmDataConverter extends DataConverter {
//
//	class JsonLdGraphListener extends GraphListenerBase {
//		Node mainSubject;
//		@Override
//		protected void deleteEvent(Triple arg0) {
//		}
//		@Override
//		protected void addEvent(Triple t) {
//			if(mainSubject==null) 
//				mainSubject=t.getSubject();
////			dest.add(t.to);
//		}
//		public Node getMainSubject() {
//			return mainSubject;
//		}
//	};
//	
//	private static final Charset UTF8=Charset.forName("UTF8");
//	
//	public DublinCoreToEdmDataConverter() {
//	}
//	
//	
//	@Override
//	public RawDataRecord convert(Model source, Model additionalStatements) {
//		if(source.getContentType().equals("application/ld+json")) {
//			Model dcModelRdf = ModelFactory.createDefaultModel();
//			ByteArrayInputStream bytesIs = new ByteArrayInputStream(source.getContent());
//			
//			JsonLdGraphListener listener = new JsonLdGraphListener();
//			dcModelRdf.getGraph().getEventManager().register(listener);
//			
//			RDFDataMgr.read(dcModelRdf, bytesIs, source.getUrl(), Lang.JSONLD);
//			try {
//				bytesIs.close(); 
//			} catch (IOException e) {
//				throw new RuntimeException(e.getMessage(), e); 
//			}
//				
//			
//			Resource choResource=null;
//			if(listener.getMainSubject().isBlank()) {
////				Node_Blank choBlNode= (Node_Blank) listener.getMainSubject();
////				System.out.println(choBlNode.getBlankNodeLabel());
//
//				Resource newChoResource = dcModelRdf.createResource(source.getUrl(), RdfReg.EDM_PROVIDED_CHO);
//
//				ArrayList<Statement> toRemove=new ArrayList<>();
//				StmtIterator choStms = dcModelRdf.listStatements();
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
//					dcModelRdf.remove(st);
//				choResource=newChoResource;
//			} else {
//				choResource=dcModelRdf.getResource(listener.getMainSubject().getURI());
//				choResource.addProperty(RdfReg.RDF_TYPE, RdfReg.EDM_PROVIDED_CHO);
//			}
//			
//			
//			System.out.println(additionalStatements);
//			if(additionalStatements!=null)
//			 	dcModelRdf.add( additionalStatements.listStatements());
//			
//			addAdditionalStatement(additionalStatements);
//			
//			EdmRdfToXmlSerializer xmlSerializer = new EdmRdfToXmlSerializer(choResource);
//			Document edmDom = xmlSerializer.getXmlDom();
//			RawDataRecord edmRawRecord=new RawDataRecord();
//			String domString = XmlUtil.writeDomToString(edmDom);
////			System.out.println(domString);
//			edmRawRecord.setContent(domString.getBytes(UTF8));
//			edmRawRecord.setContentType("application/xml");
//			edmRawRecord.setProfile("http://www.europeana.eu/schemas/edm/");
//			return edmRawRecord;
//		}
//		return null;
//
//		
//	}
//
//
//
//}
