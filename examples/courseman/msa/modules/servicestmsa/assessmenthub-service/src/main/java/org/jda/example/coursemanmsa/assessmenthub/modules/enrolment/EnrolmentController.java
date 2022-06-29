package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment;

import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.utils.controller.DefaultSpringController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class EnrolmentController extends DefaultSpringController<Enrolment, Integer>{

	@Override
	public Enrolment createEntity(Enrolment inputEntity) {
		// TODO Auto-generated method stub
		return super.createEntity(inputEntity);
	}

	@Override
	public Page<Enrolment> getEntityListByPage(Pageable pagingModel) {
		// TODO Auto-generated method stub
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public Enrolment getEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.getEntityById(id);
	}

	@Override
	public Enrolment updateEntity(Integer id, Enrolment updatedInstance) {
		// TODO Auto-generated method stub
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public void deleteEntityById(Integer id) {
		// TODO Auto-generated method stub
		super.deleteEntityById(id);
	}
	
}
