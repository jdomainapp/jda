package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.CourseModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.DefaultController;

@Controller
public class CoursemoduleController extends DefaultController<CourseModule, Integer>{
	
	@Override
	public ResponseEntity<CourseModule> createEntity(CourseModule inputEntity) {
		
		return super.createEntity(inputEntity);
	}

	@Override
	public ResponseEntity<Page<CourseModule>> getEntityListByPage(Pageable pagingModel) {
		// TODO Auto-generated method stub
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public ResponseEntity<CourseModule> getEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.getEntityById(id);
	}

	@Override
	public ResponseEntity<CourseModule> updateEntity(Integer id, CourseModule updatedInstance) {
		// TODO Auto-generated method stub
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.deleteEntityById(id);
	}

}