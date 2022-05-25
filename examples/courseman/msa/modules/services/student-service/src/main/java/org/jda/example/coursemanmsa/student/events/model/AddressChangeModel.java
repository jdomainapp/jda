package org.jda.example.coursemanmsa.student.events.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class AddressChangeModel {
	private String type;
	private String action;
	int addressId;
	private String correlationId;

	public AddressChangeModel(String type, String action, int addressId, String correlationId) {
		super();
		this.type = type;
		this.action = action;
		this.addressId = addressId;
		this.correlationId = correlationId;
	}
}
