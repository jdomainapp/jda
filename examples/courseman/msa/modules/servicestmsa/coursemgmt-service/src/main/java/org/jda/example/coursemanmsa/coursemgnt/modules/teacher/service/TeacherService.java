package org.jda.example.coursemanmsa.coursemgnt.modules.teacher.service;

import java.util.Optional;

import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TeacherService {
	
	
    @Autowired
    private TeacherRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(TeacherService.class);
    
    public Teacher createEntity(Teacher arg0){
    	arg0 = repository.save(arg0);
    	return arg0;
    }

    public Teacher getEntityById(int arg0) {
    	Optional<Teacher> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Teacher updateEntity(String arg0, Teacher arg1) {
    	repository.save(arg1);
    	return arg1;
    }

    public void deleteEntityById(int arg0) {
    	repository.deleteById(arg0);
    }
}