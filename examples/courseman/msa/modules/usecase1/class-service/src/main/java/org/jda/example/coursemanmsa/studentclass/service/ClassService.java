package org.jda.example.coursemanmsa.studentclass.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.studentclass.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.studentclass.model.StudentClass;
import org.jda.example.coursemanmsa.studentclass.repository.ClassRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ClassService {
	
	public enum ActionEnum{
		GET,
		CREATED,
		UPDATED,
		DELETED
	}
	
    @Autowired
    private ClassRepository repository;
    
    @Autowired
    SimpleSourceBean simpleSourceBean;

    private static final Logger logger = LoggerFactory.getLogger(ClassService.class);
    
    
    public StudentClass createEntity(StudentClass arg0) {
    	arg0 = repository.save(arg0);
    	simpleSourceBean.publishChange(ActionEnum.CREATED.name(),arg0.getId());
    	return arg0;
    }

    public Page getEntityListByPage(Pageable arg0) {
        return repository.findAll(arg0);
    }

    public StudentClass getEntityById(int arg0) {
    	Optional<StudentClass> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public StudentClass updateEntity(int arg0, StudentClass arg1) {
    	repository.save(arg1);
    	simpleSourceBean.publishChange(ActionEnum.UPDATED.name(),arg0);
    	return arg1;
    }

    public void deleteEntityById(int arg0) {
    	repository.deleteById(arg0);
    	simpleSourceBean.publishChange(ActionEnum.DELETED.name(),arg0);
    }

    public List<StudentClass> getAllEntities() {
        return (List<StudentClass>) repository.findAll();
    }
}