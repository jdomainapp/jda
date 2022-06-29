package org.jda.example.coursemanmsa.assessmenthub.modules.teacher;

import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.assessmenthub.utils.controller.DefaultSpringController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class TeacherController extends DefaultSpringController<Teacher, Integer>{

	@Override
	public Teacher createEntity(Teacher inputEntity) {
		// TODO Auto-generated method stub
		return super.createEntity(inputEntity);
	}

	@Override
	public Page<Teacher> getEntityListByPage(Pageable pagingModel) {
		// TODO Auto-generated method stub
		return super.getEntityListByPage(pagingModel);
	}

	@Override
	public Teacher getEntityById(Integer id) {
		// TODO Auto-generated method stub
		return super.getEntityById(id);
	}

	@Override
	public Teacher updateEntity(Integer id, Teacher updatedInstance) {
		// TODO Auto-generated method stub
		return super.updateEntity(id, updatedInstance);
	}

	@Override
	public void deleteEntityById(Integer id) {
		// TODO Auto-generated method stub
		super.deleteEntityById(id);
	}

}
