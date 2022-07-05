package org.jda.example.coursemanmsa.coursemgnt.modules.enrolment;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.events.source.EnrolmentSourceBean;
import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.utils.KafkaChangeAction;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class EnrolmentController extends DefaultController<Enrolment, Integer>{

	@Autowired
	EnrolmentSourceBean sourceBean;
	
	@Override
	public ResponseEntity<Enrolment> createEntity(Enrolment inputEntity) {
		ResponseEntity<Enrolment> result=super.createEntity(inputEntity);
		sourceBean.publishChange(KafkaChangeAction.CREATED, result.getBody().getId());
		return result;
	}

	@Override
	public ResponseEntity<Page<Enrolment>> getEntityListByPage(Pageable pagingModel) {
		// TODO Auto-generated method stub
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public ResponseEntity<Enrolment> getEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.getEntityById(id);
	}

	@Override
	public ResponseEntity<Enrolment> updateEntity(Integer id, Enrolment updatedInstance) {
		sourceBean.publishChange(KafkaChangeAction.UPDATED, id.intValue());
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		sourceBean.publishChange(KafkaChangeAction.DELETED, id.intValue());
		return super.deleteEntityById(id);
	}

}
