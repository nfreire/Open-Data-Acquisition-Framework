package inescid.opaf.casestudies.schemaorg;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;

import org.w3c.dom.Document;

import inescid.opaf.casestudies.schemaorg.RepositoryToEdmIterator.EdmHandler;
import inescid.opaf.data.EdmUtil;
import inescid.util.XmlUtil;

public class ScriptExportNcsuLinkingToDpla {

	public static class RepositoryEdmHandler extends EdmHandler {
		int maxRecords;
		int recordsCnt;
		FileWriter csvWriter;
		public RepositoryEdmHandler(File exportCsvFile, int maxRecords) throws IOException {
			csvWriter=new FileWriter(exportCsvFile);
		}

		@Override
		public boolean handle(String recId, byte[] edmXmlStringUtf8Bytes) {
			try {
				recordsCnt++;
				ByteArrayInputStream stream = new ByteArrayInputStream(edmXmlStringUtf8Bytes);
				Document edmDom = XmlUtil.parseDom(stream);
				stream.close();

				URI isShownBy = EdmUtil.getIsShownBy(edmDom);
				URI isShownAt = EdmUtil.getIsShownAt(edmDom);
				String title = EdmUtil.getTitleOrDescription(edmDom);
				
				String csv=String.format("%s,\"%s\",\"%s\",\"%s\"\n", recId, isShownBy==null ? "" : isShownBy.toString()
						, isShownAt==null ? "" : isShownAt.toString(), title==null ? "" : title.replaceAll("\"", "\\\""));
				csvWriter.write(csv);
				
				return maxRecords>0 && recordsCnt < maxRecords;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		@Override
		public void finish() {
			try {
				csvWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		File repositoryFolder=new File("../opaf-manager/target/iiif-crawl-repository-ncsu");
		boolean transformToEdmInternal=false;
		final int maxExportRecords=100;
		File exportFile=new File("target/ncsu_schemaorg.ids.csv");

		RepositoryEdmHandler handler = new RepositoryEdmHandler(exportFile, maxExportRecords);
		RepositoryToEdmIterator it=new RepositoryToEdmIterator(handler);
		it.iterate(repositoryFolder,  transformToEdmInternal, "NC State University", "NC State University");
	}
}
