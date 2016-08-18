package inescid.opaf.manager.test;

import inescid.opaf.iiif.IiifSource;

/**
 * Configuration file for the harvest, and the samples extracted 
 * 
 * @author Nuno
 *
 */
public class HackweekMetadataSamplerConfig {
	public static final int SAMPLE_SIZE_PER_ENDPOINT = 10;

	public static final IiifSource[] IIIF_TOP_COLLECTIONS = new IiifSource[] {
			new IiifSource("Digital.Bodleian",
			"http://iiif.bodleian.ox.ac.uk/iiif/collection/All"),
			
			new IiifSource("e-codices - Virtual Manuscript Library of Switzerland",
			"http://www.e-codices.unifr.ch/metadata/iiif/collection.json"),
			new IiifSource("From The Pages",
			"http://fromthepage.com/iiif/collections"),
			new IiifSource("Nat. Lib. of Wales - Newspapers",
			"http://dams.llgc.org.uk/iiif/newspapers/3100020.json"), 
			new IiifSource("Nat. Lib. of Wales - Manuscripts",
			"http://dams.llgc.org.uk/iiif/collection/saints.json"),
			new IiifSource("Sentences Commentary Text Archive",
			"http://scta.info/iiif/collection/scta"),
			new IiifSource("Stanford",
			"https://graph.global/static/data/universes/iiif/stanford.json"),
			new IiifSource("Text Grid Lab",
			"http://textgridlab.org/1.0/iiif/manifests/collection.json"),
//			"https://textgridlab.org/1.0/iiif/manifests/collection.json",
			new IiifSource("Villanova Library",
			"http://digital.library.villanova.edu/Collection/vudl:3/IIIF"),
			new IiifSource("Wellcome Library - Artists",
//			"https://wellcomelibrary.org/service/collections/", 
			"http://wellcomelibrary.org/service/collections/genres/Artists%20with%20mental%20disabilities/"), 
			new IiifSource("Wellcome Library - Caricatures",
			"http://wellcomelibrary.org/service/collections/genres/Caricatures/"), 
			new IiifSource("World Digital Library",
			"https://www.wdl.org/en/item/11849/manifest"),
			new IiifSource("Yale Digital Collections Center",
			"http://manifests.ydc2.yale.edu/manifest"),
			new IiifSource("Bavarian State Library",
			"http://www.digitale-sammlungen.de/mirador/rep/bsb00088321/bsb00088321.json") 
			};

	public static final int MAX_IMAGES_PER_MANIFEST = 10;
}
