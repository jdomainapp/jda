package org.jda.example.coursemanmsa.coursemgnt.modules.student;

import org.jda.example.coursemanmsa.coursemgnt.modules.student.events.source.StudentSourceBean;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.utils.KafkaChangeAction;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class StudentController extends DefaultController<Student, String>{

	@Autowired
	StudentSourceBean sourceBean;
	
	@Override
	public ResponseEntity<Student> createEntity(Student inputEntity) {
		ResponseEntity<Student> result=super.createEntity(inputEntity);
		sourceBean.publishChange(KafkaChangeAction.CREATED, result.getBody().getId());
		return result;
	}

	@Override
	public ResponseEntity<Page<Student>> getEntityListByPage(Pageable pagingModel) {
		// TODO Auto-generated method stub
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public ResponseEntity<Student> getEntityById(String id) {
		// TODO Auto-generated method stub
		return super.getEntityById(id);
	}

	@Override
	public ResponseEntity<Student> updateEntity(String id, Student updatedInstance) {
		sourceBean.publishChange(KafkaChangeAction.UPDATED, id);
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(String id) {
		sourceBean.publishChange(KafkaChangeAction.DELETED, id);
		return super.deleteEntityById(id);
	}

	
}