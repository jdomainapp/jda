package org.jda.example.coursemanmsa.common.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ChangeModel<ID> {
	private String type;
	private String action;
	ID id;
	private String correlationId;

	public ChangeModel(String type, String action, ID id, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.id = id;
		this.correlationId = correlationId;
	}
	
	public ChangeModel() {
		
	}
}
