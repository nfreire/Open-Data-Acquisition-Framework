package inescid.opaf.dataset.registry.view;

import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import freemarker.template.Template;
import inescid.opaf.dataset.Dataset;
import inescid.opaf.dataset.IiifDataset;
import inescid.opaf.dataset.LodDataset;
import inescid.opaf.dataset.registry.servlet.Global;

public abstract class DatasetForm {

	Dataset dataset=null;
	String message=null;
	
	public DatasetForm(Dataset dataset) {
		super();
		this.dataset = dataset;
	}

	public String output() throws Exception {
	//		Object[] args=new Object[] {
	//				(StringUtils.isEmpty(dataset.getUri()) ? "" : dataset.getUri()),
	//				(StringUtils.isEmpty(dataset.getOrganization()) ? "" : dataset.getOrganization()),
	//				(StringUtils.isEmpty(dataset.getTitle()) ? "" : dataset.getTitle()),
	//				(StringUtils.isEmpty(message) ? "" : "<tr><td colspan='2' style='color:#2E86C1'>"+message+"</td></tr>")
	//		};
	//		return template.format(args);
			StringWriter w=new StringWriter();
			Template temp = Global.FREE_MARKER.getTemplate(getClass().getSimpleName()+".html");
			temp.process(this, w);
			w.close();
			return w.toString();
		}

	public abstract boolean validate() ;

	public String getOrganization() {
		return dataset.getOrganization();
	}

	public void setOrganization(String organization) {
		dataset.setOrganization(organization);
	}

	public String getTitle() {
		return dataset.getTitle();
	}

	public void setTitle(String title) {
		dataset.setTitle(title);
	}

	public Dataset toDataset() {
		return dataset;
	}

	public String getMessage() {
		return message;
	}

}