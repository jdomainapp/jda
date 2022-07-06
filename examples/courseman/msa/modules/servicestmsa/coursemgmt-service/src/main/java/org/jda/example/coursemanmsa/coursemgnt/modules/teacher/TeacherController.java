package org.jda.example.coursemanmsa.coursemgnt.modules.teacher;

import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class TeacherController extends DefaultController<Teacher, Integer>{
	
	@Override
	public ResponseEntity<Teacher> createEntity(Teacher inputEntity) {
		return super.createEntity(inputEntity);
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
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public ResponseEntity<String> deleteEntityById(Integer id) {
		return super.deleteEntityById(id);
	}

}
