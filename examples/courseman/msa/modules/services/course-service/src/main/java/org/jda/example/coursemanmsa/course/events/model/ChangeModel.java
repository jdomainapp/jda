package org.jda.example.coursemanmsa.course.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class ChangeModel {
	private String type;
	private String action;
	int id;
	private String correlationId;

	public ChangeModel(String type, String action, int addressId, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.id = addressId;
		this.correlationId = correlationId;
	}
}
