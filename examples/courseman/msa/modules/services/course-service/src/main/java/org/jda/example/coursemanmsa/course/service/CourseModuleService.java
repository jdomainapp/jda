package org.jda.example.coursemanmsa.course.service;

import java.util.List;
import java.util.Optional;
import org.jda.example.coursemanmsa.course.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.course.model.domain.CourseModule;
import org.jda.example.coursemanmsa.course.model.view.CoursemoduleView;
import org.jda.example.coursemanmsa.course.repository.CourseModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseModuleService {
	
	public enum ActionEnum{
		GET,
		CREATED,
		UPDATED,
		DELETED
	}
	
    @Autowired
    private CourseModuleRepository repository;
    
    @Autowired
    SimpleSourceBean simpleSourceBean;

    private static final Logger logger = LoggerFactory.getLogger(CourseModuleService.class);
  
    
    public CourseModule createEntity(CourseModule arg0) {
    	arg0 = repository.save(arg0);
    	simpleSourceBean.publishChange(ActionEnum.CREATED.name(),arg0.getId());
    	return arg0;
    }

    public Page getEntityListByPage(Pageable arg0) {
        return repository.findAll(arg0);
    }

    public CourseModule getEntityById(int arg0) {
    	Optional<CourseModule> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public CourseModule updateEntity(int arg0, CourseModule arg1) {
    	repository.save(arg1);
    	simpleSourceBean.publishChange(ActionEnum.UPDATED.name(),arg0);
    	return arg1;
    }

    public void deleteEntityById(int arg0) {
    	repository.deleteById(arg0);
    	simpleSourceBean.publishChange(ActionEnum.DELETED.name(),arg0);
    }

    public List<CourseModule> getAllEntities() {
        return (List<CourseModule>) repository.findAll();
    }
    
    public CoursemoduleView findById(int id){
    	Optional<CourseModule> opt = repository.findById(id);
    	CourseModule obj = (opt.isPresent()) ? opt.get() : null;
    	if(null ==obj) {
    		return null;
    	}
    	CoursemoduleView viewObj = new CoursemoduleView(obj);
    	if(obj.getElectivemodule()!=null) {
    		viewObj.setCoursemoduletype("Electivemodule");
    		viewObj.setDeptname(obj.getElectivemodule().getDeptname());
    	}else {
    		viewObj.setCoursemoduletype("Compulsorymodule");
    	}
    	return viewObj;
    }
}