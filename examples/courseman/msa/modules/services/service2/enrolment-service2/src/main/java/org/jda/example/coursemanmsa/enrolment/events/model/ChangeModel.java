package org.jda.example.coursemanmsa.enrolment.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ChangeModel {
	private String type;
	private String action;
	String id;
	private String correlationId;

	public ChangeModel(String type, String action, String id, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.id = id;
		this.correlationId = correlationId;
	}
	
	public ChangeModel() {
		
	}
}
