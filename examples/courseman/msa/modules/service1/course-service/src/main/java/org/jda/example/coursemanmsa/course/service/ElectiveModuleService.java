package org.jda.example.coursemanmsa.course.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jda.example.coursemanmsa.course.model.domain.CourseModule;
import org.jda.example.coursemanmsa.course.model.domain.Electivemodule;
import org.jda.example.coursemanmsa.course.repository.ElectiveModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ElectiveModuleService {
	
    @Autowired
    private ElectiveModuleRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(ElectiveModuleService.class);
    
    public List<CourseModule> findAll(){
    	List<CourseModule> list = new ArrayList<>();
    	List<Electivemodule> tempList = (List<Electivemodule>) repository.findAll();
    	for (Electivemodule item : tempList) {
			list.add(item.getCoursemodule());
		}
    	return list;
    }
    
    public CourseModule getEntityById(int id){
    	Optional<Electivemodule> opt = repository.findById(id);
        return (opt.isPresent()) ? opt.get().getCoursemodule() : null;
    }

    public Electivemodule createEntity(Electivemodule obj){
    	obj = repository.save(obj);
    	return obj;
    }


    public void deleteEntityById(int id){
    	repository.deleteById(id);
    }
}