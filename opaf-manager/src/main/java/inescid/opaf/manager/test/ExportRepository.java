package inescid.opaf.manager.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.xml.XMLLayout;
import org.w3c.dom.Document;

import inescid.opaf.data.repository.api.AccessMode;
import inescid.opaf.data.repository.api.Database;
import inescid.opaf.data.repository.impl.IoUtil;
import inescid.opaf.iiif.IiifPresentationMetadata;
import inescid.opaf.iiif.IiifSeeAlsoProperty;
import inescid.util.XmlUtil;

public class ExportRepository {

	private static final Charset UTF8=Charset.forName("UTF8");
	private static final Pattern STRIP_XML_TOP_ELEMENT=Pattern.compile("^.*<rdf:RDF[^>]+>", Pattern.DOTALL);
	
	public static class ToFileEdmExporter {
		File exportFolder;
		int maxExportRecordsPerFile=10;
		int exportRecordsFileCnt=0;
		int exportRecordsCnt=0;
		boolean transformToEdmInternal=false;

		FileOutputStream fileOutputStream = null;
		int fileCnt=0;

		public ToFileEdmExporter(int maxExportRecordsPerFile, File exportFolder) {
			this.maxExportRecordsPerFile=maxExportRecordsPerFile;
			this.exportFolder = exportFolder;
		}		
		
		public void init() {
			
		}
		public void close() throws IOException {
			if(fileOutputStream!=null) {
				IOUtils.write("</rdf:RDF>".getBytes(UTF8), fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
				fileOutputStream=null;
			}

		}
		
		public void export(byte[] contentBytes) throws IOException {
			String contentStr=null;
			if(transformToEdmInternal) {
				Document dom = XmlUtil.parseDom(new ByteArrayInputStream(contentBytes));
				InputStream xsltStream = this.getClass().getClassLoader().getResourceAsStream("EDM_external2internal_v2_udc.xsl");
				Document xsltDom = XmlUtil.parseDom(xsltStream);
				xsltStream.close();
				Document edmInternalDom = XmlUtil.transform(dom, xsltDom);
				contentStr=XmlUtil.writeDomToString(edmInternalDom);
			}else
				contentStr=new String(contentBytes, UTF8);
			contentStr=contentStr.substring(0, contentStr.lastIndexOf('<'));

			Matcher matcher = STRIP_XML_TOP_ELEMENT.matcher(contentStr);
			if(!matcher.find()) 
				throw new RuntimeException("could not clean xml");
			String rdfDecl=matcher.group();
			contentStr=matcher.replaceFirst("");
			
			if(fileOutputStream==null) {
				fileCnt++;
				exportRecordsFileCnt=0;
				File exportFile=new File(exportFolder, String.format("%02d-%s.xml", fileCnt, transformToEdmInternal ? "edm_internal" : "edm"));
				fileOutputStream=new FileOutputStream(exportFile);
				IOUtils.write(rdfDecl.getBytes(UTF8), fileOutputStream);
			}
			IOUtils.write("<ore:aggregates>\n".getBytes(UTF8), fileOutputStream);
			IOUtils.write(contentStr.getBytes(UTF8), fileOutputStream);
			IOUtils.write("</ore:aggregates>\n".getBytes(UTF8), fileOutputStream);
//			IOUtils.write("\n".getBytes(UTF8), fileOutputStream);
			
			exportRecordsCnt++;		
			exportRecordsFileCnt++;		

			if(exportRecordsFileCnt>=maxExportRecordsPerFile) {
				IOUtils.write("</rdf:RDF>".getBytes(UTF8), fileOutputStream);
				fileOutputStream.flush();
				fileOutputStream.close();
				fileOutputStream=null;
			}
		}
		
		public int getExportRecordsCount() {
			return exportRecordsCnt;
		}

		public void setTransformToEdmInternal(boolean transformToEdmInternal) {
			this.transformToEdmInternal = transformToEdmInternal;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		File exportFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin");
		File repositoryFolder=new File("C:\\Users\\nfrei\\Desktop\\UCDublin\\iiif-crawl-repository-ucd");
		String exportMetadataProfile="http://www.europeana.eu/schemas/edm/";
		boolean transformToEdmInternal=true;
		final int maxExportRecords=-100;
		final int maxExportRecordsPerFile=100;
		if(!exportFolder.exists())
			exportFolder.mkdirs();
		
		Database db;
		db=new Database(repositoryFolder, AccessMode.READ_ONLY);
		ToFileEdmExporter metadataExporter=new ToFileEdmExporter(maxExportRecordsPerFile, exportFolder);
		metadataExporter.setTransformToEdmInternal(transformToEdmInternal); 
		metadataExporter.init();
		for(byte[] mdBytes : db.getAllRecordsData()) {
			IiifPresentationMetadata md = (IiifPresentationMetadata)IoUtil.fromByteArray(mdBytes);
//			System.out.println(md);
			System.out.println(md.getManifestUrl());
			if(md.getSeeAlso().isEmpty())
				System.out.println("no seeAlso");
			else {
				for(IiifSeeAlsoProperty seeAlso: md.getSeeAlso()) {
//					filename = URLEncoder.encode(md.getManifestUrl(), "UTF8")+".edm.xml";
					if(seeAlso.getProfile()==null) {
						System.out.println("no profile for "+seeAlso.getSeeAlsoUrl());
						//TODO: Remove
						if(seeAlso.getSeeAlsoUrl().contains("/edm/")) {
							metadataExporter.export(seeAlso.getSeeAlsoContent());
						}
						
					}else {
						if(seeAlso.getProfile().equals(exportMetadataProfile)) {
							metadataExporter.export(seeAlso.getSeeAlsoContent());
						}
					}
				}
			}
			if(maxExportRecords>0 && metadataExporter.getExportRecordsCount()>=maxExportRecords)
				break;
		}
		System.out.println(metadataExporter.getExportRecordsCount() + " records exported");
		metadataExporter.close();
		db.shutdown();
//		Object data = db.getData("http://dams.llgc.org.uk/iiif/newspaper/issue/3101048/manifest.json");
//		System.out.println(data);
	}
}
