package inescid.opaf.dataset.registry.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import inescid.opaf.dataset.Dataset;

public class DatasetRegistryRepository {
	private static final Charset UTF8 = Charset.forName("UTF8");
	
	File requestsLogFile;

	public DatasetRegistryRepository(File requestsLogFile) {
		this.requestsLogFile = requestsLogFile;
	}

	public void registerDataset(Dataset dataset) throws IOException {
		dataset.setLocalId(generateId());
		FileUtils.write(requestsLogFile, dataset.toCsv(), UTF8, true);
	}

	protected static String generateId() {
		return UUID.randomUUID().toString();
	}
	
}
