package inescid.opaf.iiif;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class IiifMetadataElement implements Serializable{
	private static final long serialVersionUID = 1L;
	
	final List<LocalizedLiteral> values=new ArrayList<>();
	final List<LocalizedLiteral> labels=new ArrayList<>();
	
	public List<LocalizedLiteral> getValues() {
		return values;
	}

	public void addValue(LocalizedLiteral element) {
		values.add(element);
	}

	public void addValue(Collection<LocalizedLiteral> element) {
		values.addAll(element);
	}
	public void addLabel(LocalizedLiteral element) {
		labels.add(element);
	}
	
	public void addLabel(Collection<LocalizedLiteral> element) {
		labels.addAll(element);
	}

	public List<LocalizedLiteral> getLabels() {
		return labels;
	}

	@Override
	public String toString() {
		return "Element[val=" + values + ", lab=" + labels + "]";
	}
	
}
