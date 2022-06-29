package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.utils.controller.DefaultSpringController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public class CourseModuleController extends DefaultSpringController<Coursemodule, Integer>{

	@Override
	public Coursemodule createEntity(Coursemodule inputEntity) {
		return super.createEntity(inputEntity);
	}

	@Override
	public Page<Coursemodule> getEntityListByPage(Pageable pagingModel) {
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public Coursemodule getEntityById(Integer id) {
		return super.getEntityById(id);
	}

	@Override
	public Coursemodule updateEntity(Integer id, Coursemodule updatedInstance) {
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public void deleteEntityById(Integer id) {
		super.deleteEntityById(id);
	}

}