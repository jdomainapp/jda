package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment;

import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.DefaultController;

@Controller
public class EnrolmentController extends DefaultController<Enrolment, Integer>{

	@Override
	public ResponseEntity<Enrolment> createEntity(Enrolment inputEntity) {
		// TODO Auto-generated method stub
		return super.createEntity(inputEntity);
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
		// TODO Auto-generated method stub
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.deleteEntityById(id);
	}

}
