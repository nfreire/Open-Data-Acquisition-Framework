package inescid.opaf.data.convert.rdf.converter;

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
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.w3c.dom.Document;

import inescid.opaf.data.RawDataRecord;
import inescid.opaf.data.RdfReg;
import inescid.opaf.data.convert.rdf.converter.RdfConversionSpecification;
import inescid.opaf.data.convert.rdf.converter.ResourceTypeConversionSpecification;
import inescid.opaf.data.convert.rdf.converter.SchemaOrgToEdmConversionSpecification;
import inescid.opaf.data.convert.rdf.converter.deprecated.DataConverter;
import inescid.util.RdfUtil;
import inescid.util.XmlUtil;

/**
 * @author nfrei
 *
 *	This class is not thread-safe
 *
 */
public class RdfConverter {
	
	private static final Charset UTF8=Charset.forName("UTF8");
	
	Map<String, Map<ResourceTypeConversionSpecification, Resource>> nodesMapped=new HashMap<String, Map<ResourceTypeConversionSpecification, Resource>>();
	
	RdfConversionSpecification spec;

	Resource mainTargetResource=null;
	
	public RdfConverter(RdfConversionSpecification spec) {
		this.spec=spec;
	}
	
	public Resource convert(Resource srcRoot) {
		Model source=srcRoot.getModel();
		nodesMapped.clear();
		mainTargetResource=null;
		Model targetModelRdf = ModelFactory.createDefaultModel();		
		
		
		
		boolean firstType=true;
		Resource resType=null;

		StmtIterator stms = source.listStatements();
		Statement typeStmPrev = null;
		while (stms.hasNext()) {
			Statement typeStm = stms.next();
			System.out.println(typeStm);
			System.out.println(typeStm.getSubject() == srcRoot);
			System.out.println(typeStm.getSubject().equals(srcRoot));
			if(typeStmPrev!=null) {
				System.out.println(typeStmPrev.getSubject() == typeStm.getSubject());
				System.out.println(typeStmPrev.getSubject().equals(typeStm.getSubject()));
			}
			typeStmPrev=typeStm;
			
//			resType=typeStm.getObject().asResource();
//			Resource[] rootResourceTypeMapping = spec.getRootResourceTypeMapping(resType);
//			if (rootResourceTypeMapping!=null)
//				break;
		} 
		
		
//		StmtIterator propTypesStms = source.listStatements(srcRoot.getProperty(), RdfReg.RDF_TYPE, (RDFNode) null);
		StmtIterator propTypesStms = srcRoot.listProperties(RdfReg.RDF_TYPE);
		while (propTypesStms.hasNext()) {
			Statement typeStm = propTypesStms.next();
			resType=typeStm.getObject().asResource();
			Resource[] rootResourceTypeMapping = spec.getRootResourceTypeMapping(resType);
			if (rootResourceTypeMapping!=null)
				break;
		} 
		
		for (Resource trgType: spec.getRootResourceTypeMapping(resType)) {
			String uri=srcRoot.getURI();
			if(firstType)
				firstType=false;
			else
				uri+="#"+getElementName(trgType.getURI());
				
			ResourceTypeConversionSpecification trgResourceMap = spec.getTypePropertiesMapping(trgType);
			
			Resource trgResource=convert(srcRoot, source, targetModelRdf, uri, trgResourceMap, spec);
			if (mainTargetResource==null)
				mainTargetResource=trgResource;
		} 
//	StmtIterator choStms = targetModelRdf.listStatements();
//	while (choStms.hasNext()) 
//		System.out.println(choStms.next());

return mainTargetResource;

	}
	public Resource convert(Model ldModelRdf) {
		nodesMapped.clear();
		mainTargetResource=null;
		Model targetModelRdf = ModelFactory.createDefaultModel();
//		if(source.getContentType().equals("application/json") && source.getFormat().equals("application/ld+json")) {
//			Model ldModelRdf = ModelFactory.createDefaultModel();

		Set<Resource> rootTypes=spec.getRootResourceTypes();
			
			for(Resource resType: rootTypes) {
				ResIterator roots = ldModelRdf.listSubjectsWithProperty(RdfReg.RDF_TYPE, resType);
				while(roots.hasNext()) {
					Resource srcRoot = roots.next();
					boolean firstType=true;
					for (Resource trgType: spec.getRootResourceTypeMapping(resType)) {
						String uri=srcRoot.getURI();
						if(firstType)
							firstType=false;
						else
							uri+="#"+getElementName(trgType.getURI());
							
						ResourceTypeConversionSpecification trgResourceMap = spec.getTypePropertiesMapping(trgType);
						
						Resource trgResource=convert(srcRoot, ldModelRdf, targetModelRdf, uri, trgResourceMap, spec);
						if (mainTargetResource==null)
							mainTargetResource=trgResource;
					} 
					return mainTargetResource;
				}
				
//				StmtIterator choStms = targetModelRdf.listStatements();
//				while (choStms.hasNext()) 
//					System.out.println(choStms.next());
			}
			
			return null;
	}

	public Resource convert(Model source, String rootResourceUri) {
		if(rootResourceUri==null)
			return convert(source);
//		if(source.getContentType().equals("application/json") && source.getFormat().equals("application/ld+json")) {
//			Model ldModelRdf = ModelFactory.createDefaultModel();
		boolean rootResourceUriExists=source.contains(ResourceFactory.createResource(rootResourceUri), null, (RDFNode) null);
		if(! rootResourceUriExists)
			return convert(source);

		Resource srcRoot = source.getResource(rootResourceUri);
		return convert(srcRoot);
		
	}
	
	
	private Resource convert(Resource srcResource, Model ldModelRdf, Model targetModelRdf, String uri, ResourceTypeConversionSpecification trgResourceMap, RdfConversionSpecification spec) {
		String srcResUriOrId = RdfUtil.getUriOrId(srcResource);
		if(nodesMapped.containsKey(srcResUriOrId)) {
			Map<ResourceTypeConversionSpecification, Resource> typeMapped = nodesMapped.get(srcResUriOrId);
			if(typeMapped.containsKey(trgResourceMap)) 
				return typeMapped.get(trgResourceMap);
		}
		Resource trgResource=null;
		if(uri!=null)
			trgResource=targetModelRdf.createResource(uri, trgResourceMap.getType());
		else if(srcResource.isURIResource())
			trgResource=targetModelRdf.createResource(srcResource.getURI(), trgResourceMap.getType());
		else {
			if(trgResourceMap.getPropertiesMappingToUri().isEmpty()) {
				trgResource=targetModelRdf.createResource();
				trgResource.addProperty(RdfReg.RDF_TYPE, trgResourceMap.getType());
			} else {
				for(Property p: trgResourceMap.getPropertiesMappingToUri()) {
					StmtIterator cwStms = ldModelRdf.listStatements(srcResource, p, (RDFNode) null);
					while (cwStms.hasNext()) {
						Statement st = cwStms.next();
						if(st.getObject().isURIResource()) {
							trgResource=targetModelRdf.createResource(st.getObject().asResource().getURI(), trgResourceMap.getType());
							break;
						}
					}
				}
				if(trgResource==null) {
					trgResource=targetModelRdf.createResource();
					trgResource.addProperty(RdfReg.RDF_TYPE, trgResourceMap.getType());
				}
			}
		}
		
		//add resource to mapped resources to avoid infinit recursion in some cases
		Map<ResourceTypeConversionSpecification, Resource> resTypeMapped = nodesMapped.get(srcResUriOrId);
		if(resTypeMapped==null) {
			resTypeMapped=new HashMap<>();
			nodesMapped.put(srcResUriOrId, resTypeMapped);
		}
		resTypeMapped.put(trgResourceMap, trgResource);
		
		
		
		StmtIterator cwStms = ldModelRdf.listStatements(srcResource, (Property) null, (RDFNode) null);
		while (cwStms.hasNext()) {
			Statement st = cwStms.next();
//			System.out.println(st);
			Property propMap = trgResourceMap.getPropertyMapping(st.getPredicate());
			if(propMap!=null) {
				if(st.getObject().isLiteral()) {
					Property[] propertyMerge = trgResourceMap.getPropertyMerge(st.getPredicate());
					if(propertyMerge!=null) {
						String mergedLiteral=st.getObject().asLiteral().getString();
						for(Property mergeProp: propertyMerge) {
							StmtIterator mergeStms = ldModelRdf.listStatements(srcResource, mergeProp, (RDFNode) null);
							while (mergeStms.hasNext()) {
								Statement mergest = mergeStms.next();
								if(mergest.getObject().isLiteral()) {
									mergedLiteral+=", "+ mergest.getObject().asLiteral().getString();
								}
							}
						}
						trgResource.addProperty(propMap, targetModelRdf.createLiteral(mergedLiteral));	
					} else
						trgResource.addProperty(propMap, st.getObject());	
				} else {
					boolean hasSubMap=false;
					StmtIterator propTypesStms = ldModelRdf.listStatements(st.getObject().asResource(), RdfReg.RDF_TYPE, (RDFNode) null);
					boolean hasStatements=propTypesStms.hasNext();
					while (propTypesStms.hasNext()) {
						Statement typeStm = propTypesStms.next();
//						typeStm.getObject().asResource()
						ResourceTypeConversionSpecification propertyMappingFromReferencedResource = trgResourceMap.getPropertyMappingFromReferencedResource(st.getPredicate(), typeStm.getObject().asResource());
						if (propertyMappingFromReferencedResource!=null) {
							hasSubMap=true;
							convert(trgResource, st.getObject().asResource(), ldModelRdf, propertyMappingFromReferencedResource);
						}
					}
					if(!hasSubMap) {
						boolean isTargetAnon=true;
						if(st.getObject().isURIResource()) {
							trgResource.addProperty(propMap, st.getObject());
							isTargetAnon=false;
						}
						if (hasStatements) {
							propTypesStms = ldModelRdf.listStatements(st.getObject().asResource(), RdfReg.RDF_TYPE, (RDFNode) null);
							while (propTypesStms.hasNext()) {
								Statement typeStm = propTypesStms.next();
								if(typeStm.getObject().equals(RdfReg.RDF_TYPE))
									continue;
								Resource convertedType=spec.getTypeMapping((Resource) typeStm.getObject());
								if(convertedType==null) {
									System.out.println("No mapping found for Entity "+typeStm.getObject());
									continue;
								}
								ResourceTypeConversionSpecification trgSubResourceMap = spec.getTypePropertiesMapping(convertedType);
								
								Resource objRes=st.getObject().asResource();
//								String uriOrId=null;
//								if (objRes.getURI()==null) {
//									uriOrId= objRes.getId().getBlankNodeId().getLabelString();
//									trgResource.addProperty(propMap, st.getObject());									
//								} else
//									objRes.getURI();
								if(trgSubResourceMap!=null && !srcResource.equals(objRes)) {
									Resource createdTrgSubResource=convert(objRes, ldModelRdf, targetModelRdf, null, trgSubResourceMap, spec);
									if(isTargetAnon && createdTrgSubResource!=null) {
										trgResource.addProperty(propMap,createdTrgSubResource);
									}
								} 
//								(Resource srcResource, Model ldModelRdf, Model targetModelRdf, String uri, ResourceTypeConversionSpecification trgResourceMap, RdfConversionSpecification spec) {

							}
						}
					}
				}
			} else if(!st.getPredicate().equals(RdfReg.RDF_TYPE)){
				System.out.println("No mapping found for Property "+st.getPredicate()+ " in " + trgResourceMap.getType());
				System.out.println("Property Value:"+st.getObject());
			}
		}	
		
		return trgResource;
	}

	private void convert(Resource trgResource, Resource src, Model srcModel,
			ResourceTypeConversionSpecification mapping) {
		StmtIterator srcStms = srcModel.listStatements(src, (Property) null, (RDFNode) null);
		while (srcStms.hasNext()) {
			Statement st = srcStms.next();
//			System.out.println(st);
			Property propMap = mapping.getPropertyMapping(st.getPredicate());
			if(propMap!=null) {
				if(st.getObject().isLiteral()) {
					trgResource.addProperty(propMap, st.getObject());								
				} else {
					StmtIterator propTypesStms = srcModel.listStatements(st.getObject().asResource(), RdfReg.RDF_TYPE, (RDFNode) null);
					while (propTypesStms.hasNext()) {
						Statement typeStm = propTypesStms.next();
						ResourceTypeConversionSpecification propertyMappingFromReferencedResource = mapping.getPropertyMappingFromReferencedResource(st.getPredicate(), typeStm.getObject().asResource());
						if (propertyMappingFromReferencedResource!=null) {
							convert(trgResource, (Resource)st.getObject(), srcModel, propertyMappingFromReferencedResource);
						}
					}
				}
			}
		}
	}

	private String getElementName(String uri) {
		int lastPath=uri.lastIndexOf('/');
		int lastFrag=uri.lastIndexOf('#');
		return uri.substring(Math.max(lastPath, lastFrag)+1);
	}


}
