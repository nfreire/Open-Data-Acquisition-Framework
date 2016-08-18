package inescid.opaf.data.repository.europeanadirect;

import org.apache.commons.lang3.StringUtils;

import inescid.opaf.iiif.IiifMetadataElement;
import inescid.opaf.iiif.LocalizedLiteral;

public class MetadataUtilIiif {

	public static String getLabelFor(String language, IiifMetadataElement el) {
		String enLabel=null;
		for(LocalizedLiteral l : el.getLabels()) {
			if(StringUtils.isEmpty(language)) {
				if(StringUtils.isEmpty(l.getLanguage()))
					return l.getValue();
				else if(l.getLanguage().equals("en") || l.getLanguage().startsWith("en-"))
					enLabel=l.getValue();
			} else {
				if(!StringUtils.isEmpty(l.getLanguage()) && l.getLanguage().equals(language))
					return l.getValue();				
			}
		}
		if(enLabel!=null)
			return enLabel;
		if(el.getLabels().isEmpty())
			return "";
		return el.getLabels().get(0).getValue();
	}

}
