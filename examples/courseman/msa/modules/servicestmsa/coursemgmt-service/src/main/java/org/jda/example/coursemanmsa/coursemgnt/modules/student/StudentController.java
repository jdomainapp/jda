package org.jda.example.coursemanmsa.coursemgnt.modules.student;

import org.jda.example.coursemanmsa.coursemgnt.events.model.ChangeModel;
import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.utils.KafkaChangeAction;
import org.jda.example.coursemanmsa.coursemgnt.utils.UserContext;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class StudentController extends DefaultController<Student, String>{
	
	@Override
	public ResponseEntity<Student> createEntity(Student inputEntity) {
		return super.createEntity(inputEntity);
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
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(String id) {
		return super.deleteEntityById(id);
	}

	
}