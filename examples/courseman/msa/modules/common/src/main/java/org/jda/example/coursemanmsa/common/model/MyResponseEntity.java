package org.jda.example.coursemanmsa.common.model;

import org.jda.example.coursemanmsa.common.events.model.ChangeModel;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MyResponseEntity<T,ID>{
	private ResponseEntity responseEntity;
	private ChangeModel<ID> changeModel;
	public MyResponseEntity(ResponseEntity responseEntity, ChangeModel<ID> changeModel) {
		super();
		this.responseEntity = responseEntity;
		this.changeModel = changeModel;
	}
	
	
}
