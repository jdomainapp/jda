package org.jda.example.coursemanmsa.coursemgnt.modules.teacher;

import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.events.source.TeacherSourceBean;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.coursemgnt.utils.KafkaChangeAction;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class TeacherController extends DefaultController<Teacher, Integer>{

	@Autowired
	TeacherSourceBean sourceBean;
	
	@Override
	public ResponseEntity<Teacher> createEntity(Teacher inputEntity) {
		ResponseEntity<Teacher> result=super.createEntity(inputEntity);
		sourceBean.publishChange(KafkaChangeAction.CREATED, Integer.parseInt(result.getBody().getId()));
		return result;
	}

	@Override
	public ResponseEntity<Page<Teacher>> getEntityListByPage(Pageable pagingModel) {
		// TODO Auto-generated method stub
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public ResponseEntity<Teacher> getEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.getEntityById(id);
	}

	@Override
	public ResponseEntity<Teacher> updateEntity(Integer id, Teacher updatedInstance) {
		sourceBean.publishChange(KafkaChangeAction.UPDATED, id.intValue());
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		sourceBean.publishChange(KafkaChangeAction.DELETED, id.intValue());
		return super.deleteEntityById(id);
	}

}
