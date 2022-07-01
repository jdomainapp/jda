package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ChangeModel {
	private String type;
	private String action;
	int id;
	private String correlationId;

	public ChangeModel(String type, String action, int id, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.id = id;
		this.correlationId = correlationId;
	}
	
	public ChangeModel() {
		
	}
}
