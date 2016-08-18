package inescid.opaf.iiif;

import java.io.Serializable;
import java.util.Locale;

public class LocalizedLiteral implements Serializable{
	private static final long serialVersionUID = 1L;

  private final String value;
  private final String language;

  public LocalizedLiteral(String value, Locale locale) {
    this.value = value;
    this.language = locale.getLanguage();
  }

  public LocalizedLiteral(String value, String language) {
    this.value = value;
    this.language = language;
  }

  public String getValue() {
    return value;
  }

  public String getLanguage() {
    return language;
  }

@Override
public String toString() {
	return "\"" + value + "\"" + (language==null?"":language) + "";
}
}
