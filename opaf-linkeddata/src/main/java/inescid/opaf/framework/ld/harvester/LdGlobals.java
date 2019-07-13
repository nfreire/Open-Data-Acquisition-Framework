package inescid.opaf.framework.ld.harvester;

import java.io.File;
import java.nio.charset.Charset;

public class LdGlobals {
	public static final Charset charset=Charset.forName("UTF-8");
	public static TaskSyncManager taskSyncManager=new TaskSyncManager();
	public static HttpRequestService httpRequestService=new HttpRequestService();
	public static Repository repository=new Repository();
}
