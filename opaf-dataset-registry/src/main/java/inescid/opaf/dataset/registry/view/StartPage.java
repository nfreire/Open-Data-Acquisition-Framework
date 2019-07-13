package inescid.opaf.dataset.registry.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import inescid.opaf.dataset.Dataset;
import inescid.opaf.dataset.LodDataset;

public class StartPage {
	private static final String staticHtml;
	
	
	static {
		Class<StartPage> thisClass=StartPage.class;
		String templateResource = "/"+thisClass.getPackage().getName().replace('.', '/')+"/template/"+thisClass.getSimpleName()+".html";
		InputStream templateIs = thisClass.getClassLoader().getResourceAsStream(
				templateResource);
		try {
			staticHtml=IOUtils.toString(templateIs, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public StartPage() {
	}

	public String output() {
		return staticHtml;
	}

}
