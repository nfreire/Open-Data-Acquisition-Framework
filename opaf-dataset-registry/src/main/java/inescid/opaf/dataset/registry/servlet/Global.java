package inescid.opaf.dataset.registry.servlet;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class Global {
	public static Pattern urlPattern=Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
	public static final Charset UTF8 = Charset.forName("UTF8");
	public static final Configuration FREE_MARKER=new Configuration(Configuration.VERSION_2_3_27); 
	static {
		Global.FREE_MARKER.setClassLoaderForTemplateLoading(MainHandlerServlet.class.getClassLoader(), "inescid/opaf/dataset/registry/view/template");
		Global.FREE_MARKER.setDefaultEncoding(Global.UTF8.toString());
		Global.FREE_MARKER.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		Global.FREE_MARKER.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);	
	}

}
