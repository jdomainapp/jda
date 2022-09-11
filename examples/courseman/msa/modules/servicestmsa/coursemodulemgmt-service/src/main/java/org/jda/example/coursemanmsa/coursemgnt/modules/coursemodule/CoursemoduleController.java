package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule;

import org.jda.example.coursemanmsa.common.controller.DefaultController;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.CourseModule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CoursemoduleController extends DefaultController<CourseModule, Integer>{

// ducmle: simplifies code body (no specific overriden logics)
//	@Override
//	public ResponseEntity<Coursemodule> createEntity(Coursemodule inputEntity) {
//		return super.createEntity(inputEntity);
//	}
//
//	@Override
//	public ResponseEntity<Page<Coursemodule>> getEntityListByPage(Pageable pagingModel) {
//		// TODO Auto-generated method stub
//		return super.getEntityListByPage(pagingModel);
//	}
//
//	@Override
//	public ResponseEntity<Coursemodule> getEntityById(Integer id) {
//		// TODO Auto-generated method stub
//		return super.getEntityById(id);
//	}
//
//	@Override
//	public ResponseEntity<Coursemodule> updateEntity(Integer id, Coursemodule updatedInstance) {
//		return super.updateEntity(id, updatedInstance);
//	}
//
//	@Override
//	public ResponseEntity<String> deleteEntityById(Integer id) {
//		return super.deleteEntityById(id);
//	}


}