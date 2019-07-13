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

import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateNotFoundException;
import inescid.opaf.dataset.Dataset;
import inescid.opaf.dataset.IiifDataset;
import inescid.opaf.dataset.LodDataset;
import inescid.opaf.dataset.registry.servlet.Global;

public class LodForm extends DatasetForm {
//	private static final MessageFormat template;
//	
//	static {
//		Class<LodForm> thisClass=LodForm.class;
//		String templateResource = "/"+thisClass.getPackage().getName().replace('.', '/')+"/template/"+thisClass.getSimpleName()+".html";
//		InputStream templateIs = thisClass.getClassLoader().getResourceAsStream(
//				templateResource);
//		try {
//			template=new MessageFormat(IOUtils.toString(templateIs, "UTF-8"));
//		} catch (IOException e) {
//			throw new RuntimeException(e.getMessage(), e);
//		}
//	}
	
	String message=null;
	
	public LodForm() {
		super(new LodDataset());
	}

	public LodForm(HttpServletRequest req) {
		super(new LodDataset());
		((LodDataset)dataset).setUri(req.getParameter("uri"));
		dataset.setOrganization(req.getParameter("organization"));
		dataset.setTitle(req.getParameter("title"));
	}

	public void setUri(String uri) {
		((LodDataset)dataset).setUri(uri);
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getUri() {
		return ((LodDataset)dataset).getUri();
	}
	
	@Override
	public boolean validate() {
		ArrayList<String> errors=new ArrayList<>();
		if(StringUtils.isEmpty(((LodDataset)dataset).getUri())) {
			errors.add("Provide a URI");
		}else if(!Global.urlPattern.matcher(((LodDataset)dataset).getUri()).matches()) 
			errors.add("The URI is in an invalid format");
		if(StringUtils.isEmpty(dataset.getOrganization())) 
			errors.add("Provide the name or the organization");
		if(StringUtils.isEmpty(dataset.getTitle())) 
			errors.add("Provide a title for the dataset");
		if(errors.isEmpty())
			return true;
		StringBuilder sb=new StringBuilder();
		sb.append("The form contains errors. It was not possible to register the dataset. Please check the following:</br><ul>");
		for(String e: errors) {
			sb.append("\n<li>").append(e).append("</li>");
		}
		sb.append("</ul>\n");
		message=sb.toString();
		return false;
	}
	
}
