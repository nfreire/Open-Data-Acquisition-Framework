package inescid.opaf.data.repository.europeanadirect;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.europeanadirect.model.Object;
import eu.europeana.europeanadirect.model.ObjectLanguageAware;
import eu.europeana.europeanadirect.model.ObjectLanguageNonAware.TypeEnum;
import eu.europeana.europeanadirect.model.WebLink;
import inescid.opaf.iiif.IiifMetadataElement;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.LocalizedLiteral;

public class IiifPresentationMetadataConverterToDirectObject {
	private static final Map<String, ObjectField> labelLowerCasedToFieldMap=new HashMap<String, ObjectField>() {{
		for(ObjectField f: ObjectField.values())
			put(f.name().toLowerCase(), f);
		}
	};
	

	public static Object convert(IiifPresentationMetadata iifMeta) {
		Object ret=new Object();
		ret.getWebLinks().add(new WebLink().link(iifMeta.getManifestUrl())
//				.type(eu.europeana.europeanadirect.model.WebLink.TypeEnum.DIRECT)
				);
		for(IiifMetadataElement el: iifMeta.getMetadata()) {
			ObjectField objField = suggestMatchingField(el.getLabels());
			setValues(ret, objField, el);
		}
		if(ret.getLanguageNonAwareFields().getType()==null)
			ret.getLanguageNonAwareFields().setType(TypeEnum.IMAGE);
		return ret;
	}

	private static void setValues(Object ret, ObjectField objField, IiifMetadataElement el) {
		for(LocalizedLiteral lit : el.getValues()) {
			if(StringUtils.isEmpty(lit.getLanguage())) {
				MetadataUtilDirect.addValue(MetadataUtilDirect.getLanguageNonAwareField(ret), objField, lit.getValue(), MetadataUtilIiif.getLabelFor(null, el) );
			} else {
				ObjectLanguageAware writeToOla = MetadataUtilDirect.getLanguageField(ret, lit.getLanguage());
				MetadataUtilDirect.addValue(writeToOla, objField, lit.getValue(), MetadataUtilIiif.getLabelFor(lit.getLanguage(), el));
			}
		}
	}

	private static ObjectField suggestMatchingField(List<LocalizedLiteral> labels) {
		//try to match a label in English, then try in a label without language, labels in other languages are not matched
		for(LocalizedLiteral lit: labels) {
			if(lit.getLanguage()!=null && (lit.getLanguage().equals("en") || lit.getLanguage().startsWith("en-"))){
				ObjectField matchedField = labelLowerCasedToFieldMap.get(lit.getValue().toLowerCase());
				if(matchedField!=null && matchedField!=ObjectField.CUSTOM)
					return matchedField;
			}
		}
		for(LocalizedLiteral lit: labels) {
			if(lit.getLanguage()==null){
				ObjectField matchedField = labelLowerCasedToFieldMap.get(lit.getValue().toLowerCase());
				if(matchedField!=null && matchedField!=ObjectField.CUSTOM)
					return matchedField;
			}
		}
		return ObjectField.CUSTOM;
	}
	
	
	
	
	
}
