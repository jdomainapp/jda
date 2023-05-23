package jda.modules.msacommon.events.model;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangeModel2 {
	private String type;
	private String action;
	int id;
	private String path;
	private String correlationId;

	public ChangeModel2(String type, String action, int id, String path, String correlationId) {
		super();
		this.action = action;
		this.id = id;
		this.path=path;
		this.correlationId = correlationId;
	}
	
	public ChangeModel2() {
		
	}
}
