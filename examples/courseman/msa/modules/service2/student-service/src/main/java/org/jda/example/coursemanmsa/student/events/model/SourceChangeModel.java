package org.jda.example.coursemanmsa.student.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class SourceChangeModel {
	private String type;
	private String action;
	String id;
	private String correlationId;

	public SourceChangeModel(String type, String action, String id, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.id = id;
		this.correlationId = correlationId;
	}
}
