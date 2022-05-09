package org.jda.example.coursemanmsa.course.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.course.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.course.model.domain.Compulsorymodule;
import org.jda.example.coursemanmsa.course.model.domain.CourseModule;
import org.jda.example.coursemanmsa.course.model.domain.Electivemodule;
import org.jda.example.coursemanmsa.course.model.view.CoursemoduleView;
import org.jda.example.coursemanmsa.course.repository.CompulsoryModuleRepository;
import org.jda.example.coursemanmsa.course.repository.CourseModuleRepository;
import org.jda.example.coursemanmsa.course.repository.ElectiveModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CompulsoryModuleService {
	
	public enum ActionEnum{
		GET,
		CREATED,
		UPDATED,
		DELETED
	}
	
    @Autowired
    private CompulsoryModuleRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(CompulsoryModuleService.class);
    
    public List<CourseModule> findAll(){
    	List<CourseModule> list = new ArrayList<>();
    	List<Compulsorymodule> tempList= (List<Compulsorymodule>) repository.findAll();
    	for (Compulsorymodule item : tempList) {
    		list.add(item.getCoursemodule());
		}
    	return list;
    }
    
    public CourseModule findById(int id){
    	Optional<Compulsorymodule> opt = repository.findById(id);
    	return (opt.isPresent()) ? opt.get().getCoursemodule() : null;
    }

    public Compulsorymodule create(Compulsorymodule obj){
    	obj = repository.save(obj);
    	return obj;
    }


    public void delete(int id){
    	repository.deleteById(id);
    }
}