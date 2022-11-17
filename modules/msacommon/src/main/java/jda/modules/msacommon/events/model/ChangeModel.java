package jda.modules.msacommon.events.model;


public class ChangeModel<ID> {
	private String type;
	private String action;
	ID id;
	private String path;
	private String correlationId;

	public ChangeModel(String type, String action, ID id, String path, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.id = id;
		this.path=path;
		this.correlationId = correlationId;
	}
	
	public ChangeModel() {
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}
	
}
