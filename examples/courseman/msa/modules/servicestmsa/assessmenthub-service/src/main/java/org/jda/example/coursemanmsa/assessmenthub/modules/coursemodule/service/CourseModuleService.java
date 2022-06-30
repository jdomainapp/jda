package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.service;

import java.util.Optional;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.repository.CourseModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CourseModuleService {
	
	
    @Autowired
    private CourseModuleRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(CourseModuleService.class);
    
    
    public Coursemodule createEntity(Coursemodule arg0){
    	arg0 = repository.save(arg0);
    	return arg0;
    }

    public Coursemodule getEntityById(int arg0) {
    	Optional<Coursemodule> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Coursemodule updateEntity(int arg0, Coursemodule arg1) {
    	repository.save(arg1);
    	return arg1;
    }

    public void deleteEntityById(int arg0) {
    	repository.deleteById(arg0);
    }
}