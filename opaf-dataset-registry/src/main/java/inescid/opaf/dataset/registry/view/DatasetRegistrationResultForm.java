package inescid.opaf.dataset.registry.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import inescid.opaf.dataset.registry.servlet.Global;

public class DatasetRegistrationResultForm {
//	private static final MessageFormat template;
	
//	static {
//		Class<DatasetRegistrationResultForm> thisClass=DatasetRegistrationResultForm.class;
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
	
	public DatasetRegistrationResultForm() {
	}

	public String output() throws Exception{
//		Object[] args=new Object[] {
//				(StringUtils.isEmpty(message) ? "" : "<tr><td colspan='2'>"+message+"</td></tr>")
//		};
//		return template.format(args);
			StringWriter w=new StringWriter();
			Template temp = Global.FREE_MARKER.getTemplate(getClass().getSimpleName()+".html");
			temp.process(this, w);
			w.close();
			return w.toString();
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
