package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule;

import org.jda.example.coursemanmsa.coursemgnt.events.model.ChangeModel;
import org.jda.example.coursemanmsa.coursemgnt.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.utils.KafkaChangeAction;
import org.jda.example.coursemanmsa.coursemgnt.utils.UserContext;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CoursemoduleController extends DefaultController<Coursemodule, Integer>{
	
	@Override
	public ResponseEntity<Coursemodule> createEntity(Coursemodule inputEntity) {
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
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		return super.deleteEntityById(id);
	}


}