package inescid.opaf.europeanadirect;

/**
 * Interface for consuming the harvested metadata records
 * 
 * @author Nuno
 *
 */
public interface RecordHandler {
	
	/**
	 * @param jsonRecord one metadata record encoded in JSON
	 * @return true if the harvesting should continue, false to abort the harvester after this record
	 */
	public boolean handleRecord(String jsonRecord);
}
