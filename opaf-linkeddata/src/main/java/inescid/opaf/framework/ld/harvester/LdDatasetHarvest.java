package inescid.opaf.framework.ld.harvester;

import java.io.File;
import java.io.IOException;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import inescid.opaf.data.RdfReg;
import inescid.opaf.dataset.LodDataset;
import inescid.util.DatasetLog;
import inescid.util.LinkedDataUtil;
import inescid.util.ListOnTxtFile;
import inescid.util.RdfResourceAccessException;

public class LdDatasetHarvest {
	private static org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LdDatasetHarvest.class);
	private static long retriesSleepMicrosecs=20;
	private static int retriesMaxAttempts=1;
	
	LodDataset dataset;
	boolean skipExistingResources=false;
	DatasetLog datasetLog;

	public LdDatasetHarvest(LodDataset dataset) {
		super();
		this.dataset = dataset;
		datasetLog=new DatasetLog(dataset.getUri());
	}
	
	public LdDatasetHarvest(LodDataset dataset, boolean skipExistingResources) {
		this(dataset);
		this.skipExistingResources = skipExistingResources;
	}

	public void run() throws RdfResourceAccessException {
		try {
			Resource  dsResource = LinkedDataUtil.getResource(dataset.getUri());
			StmtIterator voidRootResources = dsResource.listProperties(RdfReg.VOID_ROOT_RESOURCE);
			if (voidRootResources!=null && voidRootResources.hasNext()) {
				harvestRootResources(dataset.getUri(), voidRootResources);				
			} else { //try a Distribution of the dataset
				throw new RuntimeException("TODO");
			}
		} catch (InterruptedException | IOException e) {
			datasetLog.logHarvestIssue(dataset.getUri(), "Dataset harvest failed");
		}
	}

	private void harvestRootResources(String datasetUri, StmtIterator voidRootResources) throws IOException {
		ListOnTxtFile list=new ListOnTxtFile(LdGlobals.repository.getDatasetListOfMemberUris(datasetUri));
		list.clear();
		list.openForWrite();
		while(voidRootResources.hasNext()) {
			Statement st=voidRootResources.next();

			RDFNode rootResource = st.getObject();
			if(rootResource.isResource()) {
				Resource rootResourceRs=(Resource)rootResource;
				StmtIterator partsStms=rootResourceRs.listProperties(RdfReg.DCTERMS_HAS_PART);
				while (partsStms.hasNext()) {
					Statement partStm=partsStms.next();
					RDFNode part = partStm.getObject();
					if(part.isURIResource()) {
						list.add(part.asNode().getURI());
					}else
						System.out.println("unsupported RDFNode for dcterms:hasPart: "+part.getClass().getCanonicalName());
				}
			} else if (rootResource.isURIResource()){
				throw new RuntimeException("TODO");
			} else {
				System.out.println("unsupported RDFNode for void:rootResource: "+rootResource.getClass().getCanonicalName());
			}
		}
		list.close();
		
		//harvest resources without paralelism
		list.openForRead();
		while(list.hasNext()) {
			String uriOfRec=list.next();
			File rdfResourceFile = LdGlobals.repository.getRdfResourceFile(datasetUri, uriOfRec);
			if(skipExistingResources && rdfResourceFile.exists()) {
				datasetLog.logSkippedRdfResource();
				continue;
			}
			int retries=retriesMaxAttempts;
			while (retries>=0) {
				try {
					Resource  dsResource = LinkedDataUtil.getAndStoreResource(uriOfRec, rdfResourceFile);
					datasetLog.logHarvestSuccess();
					break;
				} catch (InterruptedException e) {
					break;
				} catch (Exception e) {
					retries--;
					if(retries<0) {
						log.error(uriOfRec, e);
						datasetLog.logHarvestIssue(uriOfRec, e.getMessage());
					}else {
						log.debug(uriOfRec, e);
						try {
//							log.debug("Harvester sleeping", e);
							Thread.sleep((retriesMaxAttempts-retries)*retriesSleepMicrosecs);
						} catch (InterruptedException ei) {
							log.warn(uriOfRec, ei);
							break;
						}
					}
				}
			}
		}
		datasetLog.logFinish();
		list.close();
	}
	
}
