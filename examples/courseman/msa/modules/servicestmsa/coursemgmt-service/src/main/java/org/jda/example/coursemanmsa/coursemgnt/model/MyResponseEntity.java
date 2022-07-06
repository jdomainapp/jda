package org.jda.example.coursemanmsa.coursemgnt.model;

import org.jda.example.coursemanmsa.coursemgnt.events.model.ChangeModel;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MyResponseEntity{
	private ResponseEntity responseEntity;
	private ChangeModel changeModel;
	public MyResponseEntity(ResponseEntity responseEntity, ChangeModel changeModel) {
		super();
		this.responseEntity = responseEntity;
		this.changeModel = changeModel;
	}
	
	
}
