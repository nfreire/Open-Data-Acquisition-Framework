package inescid.opaf.casestudies.dpla;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ScriptLinkDplaWithSourceSchemaOrg {
	
	interface Matcher {
		public boolean filterOut(CSVRecord dplaData);
		public boolean match(CSVRecord dplaData, CSVRecord sourceData);
	}
	
	static class LinkerDplaWithSourceSchemaOrg {
		File dplaCsvFile;
		File sourceCsvFile;
		Matcher matcher;
		
		public LinkerDplaWithSourceSchemaOrg(File dplaCsvFile, File sourceCsvFile, Matcher matcher) {
			super();
			this.dplaCsvFile = dplaCsvFile;
			this.sourceCsvFile = sourceCsvFile;
			this.matcher = matcher;
		}
		
		public List<Pair<String, String>> findMatches() throws IOException{
			
			
			//brute force algorithm comparing all x all
			List<Pair<String, String>> matches=new ArrayList<>();
			
			FileReader dplaReader=new FileReader(dplaCsvFile);
			CSVParser dplaCsvParser=new CSVParser(dplaReader, CSVFormat.DEFAULT);
			for(CSVRecord dplaEntry  : dplaCsvParser) {
				String matchedWith=null;
				if(!matcher.filterOut(dplaEntry)) {
					FileReader sourceReader=new FileReader(sourceCsvFile);
					CSVParser sourceCsvParser=new CSVParser(sourceReader, CSVFormat.DEFAULT);
					for(CSVRecord sourceEntry  : sourceCsvParser) {
						if(matcher.match(dplaEntry, sourceEntry)) {
							if(matchedWith==null)
								matchedWith=sourceEntry.get(0);
							else {
								matchedWith=null;
								System.out.println("Abiguous match discarded for " + dplaEntry.get(0));							
								break;
							}
	//						matches.add(new ImmutablePair(dplaEntry.get(0), sourceEntry.get(0)));
						}
					}
					if(matchedWith!=null) {
						matches.add(new ImmutablePair(dplaEntry.get(0), matchedWith));					
						System.out.println("Matched:\n\t" + dplaEntry.get(0)+"\n\t" + matchedWith);
					}
					sourceReader.close();
					sourceCsvParser.close();
				}
			}
			dplaReader.close();
			dplaCsvParser.close();
			return matches;
		}
	}
	
	public static void main(String[] args) throws Exception {
		Matcher ld4scMatcher=new Matcher() {
			@Override
			public boolean filterOut(CSVRecord dplaData) {
				return false;
			}
  			@Override
			public boolean match(CSVRecord dplaData, CSVRecord sourceData) {
//  				System.out.println(dplaData.get(3) +"---"+sourceData.get(3));
				return dplaData.get(3).equals(sourceData.get(3)) && dplaData.get(2).contains("/motley/");
			}
		};
		Matcher ncsuMatcher=new Matcher() {
			@Override
			public boolean filterOut(CSVRecord dplaData) {
				return ! dplaData.get(2).contains("/catalog");
			}
			@Override
			public boolean match(CSVRecord dplaData, CSVRecord sourceData) {
				try {
					return dplaData.get(2).equals(sourceData.get(2));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		};
		{
			//No matches found...
			
//			LinkerDplaWithSourceSchemaOrg linker=new LinkerDplaWithSourceSchemaOrg(new File("target/digitalnc.ids.csv"), new File("target/ncsu_schemaorg.ids.csv"), ncsuMatcher);
//			List<Pair<String, String>> ncsuMatches = linker.findMatches();
//			System.out.println(ncsuMatches.size()+" found for NCSU");
		}
		{
			LinkerDplaWithSourceSchemaOrg linker=new LinkerDplaWithSourceSchemaOrg(new File("target/uiuc.ids.csv"), new File("target/ld4sc_schemaorg.ids.csv"), ld4scMatcher);
			List<Pair<String, String>> ld4scMatches = linker.findMatches();
			FileWriter csvWriter=new FileWriter("target/ld4sc_dpla_matches.csv");
			CSVPrinter writer=new CSVPrinter(csvWriter, CSVFormat.DEFAULT);
			for(Pair<String, String> match : ld4scMatches) {
				writer.printRecord(match.getLeft(), match.getRight());
			}
			writer.close();
			csvWriter.close();
			System.out.println(ld4scMatches.size()+" found for LD4SC");
		}		
	}
	
}
