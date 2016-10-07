package inescid.opaf.data.repository.europeanadirect;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eu.europeana.europeanadirect.model.KeyValuePair;
import eu.europeana.europeanadirect.model.Object;
import eu.europeana.europeanadirect.model.ObjectLanguageAware;
import eu.europeana.europeanadirect.model.ObjectLanguageNonAware;
import eu.europeana.europeanadirect.model.ObjectLanguageNonAware.TypeEnum;

public class MetadataUtilDirect {

	public static ObjectLanguageAware getLanguageField(Object obj, String lang) {
		if(lang.length()>2 && lang.charAt(2)=='-')
			lang=lang.substring(0,2);
		for(ObjectLanguageAware ola : obj.getLanguageAwareFields()) {
			if(ola.getLanguage().equals(lang))
				return ola;
		}
		ObjectLanguageAware ola=new ObjectLanguageAware().language(lang); 
		obj.getLanguageAwareFields().add(ola);
		return ola;
	}

//	  @SerializedName("type")
//	  private TypeEnum type = null;
//
//	  @SerializedName("owner")
//	  private String owner = null;
//
//	  @SerializedName("identifier")
//	  private List<String> identifier = new ArrayList<String>();
//
//	  @SerializedName("relation")
//	  private List<String> relation = new ArrayList<String>();
//
//	  @SerializedName("customFields")
//	  private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();
//
	
	public static void addValue(ObjectLanguageNonAware languageNonAwareFields, ObjectField objField, String value,
			String label) {
		switch (objField) {
		case ALTERNATIVE:
		case CREATED:
		case CUSTOM:
		case DESCRIPTION:
		case EXTENT:
		case FORMAT:
		case ISSUED:
		case MEDIUM:
		case PROVENANCE:
		case PUBLISHER:
		case SOURCE:
		case TITLE:
			addValueToCustom(languageNonAwareFields.getCustomFields(), objField, value, label);
			break;
		case OWNER:
			languageNonAwareFields.setOwner(value);
			break;
		case IDENTIFIER:
			languageNonAwareFields.getIdentifier().add(value);
			break;
		case RELATION:
			languageNonAwareFields.getRelation().add(value);
			break;
		case TYPE:
			try {
				languageNonAwareFields.setType(TypeEnum.valueOf(value));
			} catch (IllegalArgumentException  e) {
				addValueToCustom(languageNonAwareFields.getCustomFields(), objField, value, label);
			}
			break;
		}
		
	}

	public static void addValue(ObjectLanguageAware writeToOla, ObjectField objField, String value, String labelFor) {
		switch (objField) {
		case ALTERNATIVE:
			writeToOla.getAlternative().add(value);
			break;
		case CREATED:
			if(StringUtils.isEmpty(writeToOla.getCreated()))
				writeToOla.setCreated(value);
			else 
				addValueToCustom(writeToOla.getCustomFields(), objField, value, labelFor);
			break;
		case CUSTOM:
			addValueToCustom(writeToOla.getCustomFields(), objField, value, labelFor);
			break;
		case DESCRIPTION:
			writeToOla.setDescription(value);
			break;
		case EXTENT:
			writeToOla.getExtent().add(value);
			break;
		case FORMAT:
			writeToOla.getFormat().add(value);
			break;
		case ISSUED:
			if(StringUtils.isEmpty(writeToOla.getIssued()))
				writeToOla.setIssued(value);
			else 
				addValueToCustom(writeToOla.getCustomFields(), objField, value, labelFor);
			break;
		case MEDIUM:
			writeToOla.getMedium().add(value);
			break;
		case PROVENANCE:
			writeToOla.getProvenance().add(value);
			break;
		case PUBLISHER:
			writeToOla.getPublisher().add(value);
			break;
		case SOURCE:
			writeToOla.getSource().add(value);
			break;
		case TITLE:
			if(StringUtils.isEmpty(writeToOla.getTitle()))
				writeToOla.setTitle(value);
			else 
				writeToOla.getAlternative().add(value);
			break;
		case OWNER:
		case IDENTIFIER:
		case RELATION:
		case TYPE:
			addValueToCustom(writeToOla.getCustomFields(), objField, value, labelFor);
			break;
		}
	}

	private static void addValueToCustom(List<KeyValuePair> customFields, ObjectField objField, String value,
			String labelFor) {
		if (StringUtils.isEmpty(labelFor)) {
			customFields.add(
					new KeyValuePair().key(StringUtils.capitalize(objField.name())).value(value));
		} else {
			customFields.add(
					new KeyValuePair().key(labelFor).value(value));				
		}
	}

	public static ObjectLanguageNonAware getLanguageNonAwareField(Object obj) {
		if(obj.getLanguageNonAwareFields()==null) {
			obj.setLanguageNonAwareFields(new ObjectLanguageNonAware() );
		}
		return obj.getLanguageNonAwareFields();
	}

	public static void addValueToCustomFields(Object ret, String value, String label) {
		addValueToCustom(getLanguageNonAwareField(ret).getCustomFields(), null, value, label);
	}
}
