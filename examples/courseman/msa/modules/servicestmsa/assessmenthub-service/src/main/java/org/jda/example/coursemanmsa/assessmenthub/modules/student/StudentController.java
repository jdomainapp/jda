package org.jda.example.coursemanmsa.assessmenthub.modules.student;

import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.DefaultController;

@Controller
public class StudentController extends DefaultController<Student, String>{

	@Override
	public ResponseEntity<Student> createEntity(Student inputEntity) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(String id) {
		// TODO Auto-generated method stub
		return super.deleteEntityById(id);
	}

}