package org.jda.example.coursemanmsa.assessmenthub.modules.student.service;

import java.util.Optional;

import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentService {
	
	
    @Autowired
    private StudentRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    
    public Student createEntity(Student arg0){
    	arg0 = repository.save(arg0);
    	return arg0;
    }

    public Student getEntityById(String arg0) {
    	Optional<Student> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Student updateEntity(String arg0, Student arg1) {
    	repository.save(arg1);
    	return arg1;
    }

    public void deleteEntityById(String arg0) {
    	repository.deleteById(arg0);
    }
}