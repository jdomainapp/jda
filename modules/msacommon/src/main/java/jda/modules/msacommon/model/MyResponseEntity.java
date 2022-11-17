package jda.modules.msacommon.model;

import org.springframework.http.ResponseEntity;

import jda.modules.dodm.dsm.DSM;
import jda.modules.msacommon.events.model.ChangeModel;

public class MyResponseEntity<T, ID> {
	private ResponseEntity responseEntity;
	private ChangeModel<ID> changeModel;

	public MyResponseEntity(ResponseEntity responseEntity, ChangeModel<ID> changeModel) {
		super();
		this.responseEntity = responseEntity;
		this.changeModel = changeModel;
	}

	public ResponseEntity getResponseEntity() {
		return responseEntity;
	}

	public void setResponseEntity(ResponseEntity responseEntity) {
		this.responseEntity = responseEntity;
	}

	public ChangeModel<ID> getChangeModel() {
		if (changeModel != null) {
			if (changeModel.getId() == null) {
				Object result = responseEntity.getBody();
				// ducmle: use generic code
				// TODO: check type of ID

				ID id = (ID) DSM.doGetterMethod(result.getClass(), result, "id", Object.class);
				changeModel.setId(id);
			}
		}
		return changeModel;
	}

	public void setChangeModel(ChangeModel<ID> changeModel) {
		this.changeModel = changeModel;
	}

}
