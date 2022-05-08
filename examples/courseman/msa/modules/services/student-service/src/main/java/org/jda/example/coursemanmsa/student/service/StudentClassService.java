package org.jda.example.coursemanmsa.student.service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.student.model.Address;
import org.jda.example.coursemanmsa.student.model.StudentClass;
import org.jda.example.coursemanmsa.student.repository.AddressRepository;
import org.jda.example.coursemanmsa.student.repository.StudentClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentClassService {
	
	
    @Autowired
    private StudentClassRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(StudentClassService.class);
    
    public StudentClass createEntity(StudentClass arg0){
    	arg0 = repository.save(arg0);
    	return arg0;
    }

    public StudentClass getEntityById(int arg0) {
    	Optional<StudentClass> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public StudentClass updateEntity(int arg0, StudentClass arg1) {
    	repository.save(arg1);
    	return arg1;
    }

    public void deleteEntityById(int arg0) {
    	repository.deleteById(arg0);
    }
}