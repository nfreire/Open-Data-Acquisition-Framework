package inescid.opaf.dataset.registry.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import inescid.opaf.dataset.Dataset;
import inescid.opaf.dataset.registry.view.DatasetRegistrationResultForm;
import inescid.opaf.dataset.registry.view.IiifForm;
import inescid.opaf.dataset.registry.view.LodForm;
import inescid.opaf.dataset.registry.view.StartPage;

public class MainHandlerServlet extends HttpServlet {
	enum RequestOperation {
		DISPLAY_LOD_DATASET_REGISTRATION_FORM, DISPLAY_IIIF_DATASET_REGISTRATION_FORM, DISPLAY_CONTACT_FORM, REGISTER_DATASET, REGISTER_CONTACT, VIEW_DATASET_STATUS, DISPLAY_START_PAGE;

		public static RequestOperation fromHttpRequest(HttpServletRequest req) {
//			System.out.println("req.getPathInfo() " + req.getPathInfo());
//			System.out.println("req.getServletPath() " + req.getServletPath());
			if (req.getPathInfo()!=null) {
				if (req.getPathInfo().endsWith("/lod-dataset-register")) {
					if(StringUtils.isEmpty(req.getParameter("registration")))
						return RequestOperation.DISPLAY_LOD_DATASET_REGISTRATION_FORM;
					return RequestOperation.REGISTER_DATASET;
				} else if (req.getPathInfo().endsWith("/iiif-dataset-register")) {
						if(StringUtils.isEmpty(req.getParameter("registration")))
							return RequestOperation.DISPLAY_LOD_DATASET_REGISTRATION_FORM;
						return RequestOperation.REGISTER_DATASET;
				}
			}
			return RequestOperation.DISPLAY_START_PAGE;
		}
	};

	DatasetRegistryRepository repository;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		String repositoryFolder = config.getServletContext()
				.getInitParameter("opaf.dataset-registry.repository.folder");
		if (repositoryFolder.equals("${opaf.dataset-registry.repository.folder}"))
			repositoryFolder = "C:\\Users\\nfrei\\Desktop";
		System.out.println("Init: " + repositoryFolder);
		File requestsLogFile = new File(repositoryFolder, "dataset-registry-requests.csv");
		repository=new DatasetRegistryRepository(requestsLogFile);
	}

	protected void doGetOrPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			RequestOperation operation = RequestOperation.fromHttpRequest(req);
			System.out.println(operation);
			switch (operation) {
			case DISPLAY_CONTACT_FORM:
				break;
			case DISPLAY_LOD_DATASET_REGISTRATION_FORM:{
				LodForm form = new LodForm();
				sendResponse(resp, 200, form.output());
				break;
			}case DISPLAY_IIIF_DATASET_REGISTRATION_FORM:{
				IiifForm form = new IiifForm();
				sendResponse(resp, 200, form.output());
				break;
			}case REGISTER_CONTACT:
				break;
			case REGISTER_DATASET:{
				if("lod".equals(req.getParameter("type"))) {
					LodForm formIn = new LodForm(req);
					if(formIn.validate()) {
						Dataset dataset = formIn.toDataset();
						repository.registerDataset(dataset);
						DatasetRegistrationResultForm formRes = new DatasetRegistrationResultForm();
						formRes.setMessage("Dataset registered with ID: "+dataset.getLocalId());
					} 
						sendResponse(resp, 200, formIn.output());
				}
				break;
			} case DISPLAY_START_PAGE:{
				StartPage form = new StartPage();
				sendResponse(resp, 200, form.output());
				break;
			} case VIEW_DATASET_STATUS:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			sendResponse(resp, 500, "Internal error: " + e.getMessage());
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGetOrPost(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGetOrPost(req, resp);
	}



	protected void sendResponse(HttpServletResponse resp, int httpStatus, String body) throws IOException {
		resp.setStatus(httpStatus);
		if (body != null && !body.isEmpty()) {
			ServletOutputStream outputStream = resp.getOutputStream();
			outputStream.write(body.getBytes(Global.UTF8));
			resp.setContentType("text/html; charset=utf-8");
		}
	}
}
