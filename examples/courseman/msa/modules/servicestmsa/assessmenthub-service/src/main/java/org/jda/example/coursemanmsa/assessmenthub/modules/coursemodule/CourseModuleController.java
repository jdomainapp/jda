package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.utils.controller.DefaultController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CourseModuleController extends DefaultController<Coursemodule, Integer>{

	@Override
	public ResponseEntity<Coursemodule> createEntity(Coursemodule inputEntity) {
		// TODO Auto-generated method stub
		return super.createEntity(inputEntity);
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
		// TODO Auto-generated method stub
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.deleteEntityById(id);
	}


}