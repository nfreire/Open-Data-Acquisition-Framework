package inescid.opaf.framework.ld.casestudies;

import java.io.File;

import javax.xml.bind.JAXBElement.GlobalScope;

import inescid.opaf.dataset.LodDataset;
import inescid.opaf.framework.ld.harvester.LdGlobals;
import inescid.opaf.framework.ld.harvester.Repository;

public class ScriptSchemaOrgEdmConvertion {

	public static void main(String[] args) throws Exception {
		String repositoryFolderPath="lod-harvest-repo";
		String datasetRdfResourceUri="http://data.bibliotheken.nl/id/dataset/rise-childrensbooks";
		String exportFolderPath="lod-edm-repo";
		if(args.length>=1)
			repositoryFolderPath = args[0];
		if(args.length>=2)
			datasetRdfResourceUri = args[1];
		if(args.length>=3)
			exportFolderPath = args[2];
		
		LdGlobals.repository=new Repository();
		LdGlobals.repository.init(repositoryFolderPath);
		
		SchemaOrgFromRepositoryToEdmFileExport export= new SchemaOrgFromRepositoryToEdmFileExport(false, "NDE", "KB");

		export.export(datasetRdfResourceUri, new File(exportFolderPath), 10);
	}
}
