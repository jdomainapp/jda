package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.events.source.CoursemoduleSourceBean;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.utils.KafkaChangeAction;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CoursemoduleController extends DefaultController<Coursemodule, Integer>{
	@Autowired
	CoursemoduleSourceBean sourceBean;
	
	@Override
	public ResponseEntity<Coursemodule> createEntity(Coursemodule inputEntity) {
		ResponseEntity<Coursemodule> result=super.createEntity(inputEntity);
		sourceBean.publishChange(KafkaChangeAction.CREATED, result.getBody().getId());
		return result;
	}

	@Override
	public ResponseEntity<Page<Coursemodule>> getEntityListByPage(Pageable pagingModel) {
		// TODO Auto-generated method stub
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public ResponseEntity<Coursemodule> getEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.getEntityById(id);
	}

	@Override
	public ResponseEntity<Coursemodule> updateEntity(Integer id, Coursemodule updatedInstance) {
		sourceBean.publishChange(KafkaChangeAction.UPDATED, id.intValue());
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		sourceBean.publishChange(KafkaChangeAction.DELETED, id.intValue());
		return super.deleteEntityById(id);
	}


}