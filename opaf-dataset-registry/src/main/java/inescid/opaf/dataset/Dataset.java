package inescid.opaf.dataset;

public abstract class Dataset {
	enum DatasetType {
		LOD,
		IIIF
	};
	
	protected String localId;
	protected DatasetType type;
	protected String organization;
	protected String title;


	public Dataset(String localId, DatasetType type) {
		this.localId = localId;
		this.type = type;
	}

	public Dataset(DatasetType type) {
		this.type = type;
	}

	public DatasetType getType() {
		return type;
	}

	public void setType(DatasetType type) {
		this.type = type;
	}
	
	public abstract String toCsv();

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getLocalId() {
		return localId;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
