package inescid.opaf.manager.test;

import java.io.FileInputStream;
import java.util.Properties;

import inescid.opaf.manager.DataSourceManager;

public class RunHarvestingManager {
	
	public static void main(String[] args) {
		try {
			FileInputStream propsIs=new FileInputStream(args[0]);
			Properties prop=new Properties();
			prop.load(propsIs);
			runManager(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void runManager(Properties prop) {
		try {
			DataSourceManager manager=new DataSourceManager();
			manager.init(prop);
			manager.syncAll();
			manager.close();
			System.out.println("All done. exiting.");
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}

}
