package inescid.opaf.framework.ld.casestudies;

import javax.xml.bind.JAXBElement.GlobalScope;

import inescid.opaf.dataset.LodDataset;
import inescid.opaf.framework.ld.harvester.LdDatasetHarvest;
import inescid.opaf.framework.ld.harvester.LdGlobals;
import inescid.opaf.framework.ld.harvester.Repository;

public class ScriptDatasetFirstHarvest {

	public static void main(String[] args) throws Exception {
		String repositoryFolderPath="lod-harvest-repo";
		String datasetRdfResourceUri="http://data.bibliotheken.nl/id/dataset/rise-childrensbooks";
		if(args.length>=1)
			repositoryFolderPath = args[0];
		if(args.length>=2)
			datasetRdfResourceUri = args[1];
		
		LdGlobals.repository=new Repository();
		LdGlobals.repository.init(repositoryFolderPath);
				
		LdGlobals.httpRequestService.init();
		
		LodDataset dataset=new LodDataset(datasetRdfResourceUri); 

		LdDatasetHarvest harvest=new LdDatasetHarvest(dataset, true/*skip existing*/);
		harvest.run();
	}
}
