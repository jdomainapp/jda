package org.jda.example.coursemanmsa.student.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.student.config.ServiceConfig;
import org.jda.example.coursemanmsa.student.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.student.model.Student;
import org.jda.example.coursemanmsa.student.model.StudentClass;
import org.jda.example.coursemanmsa.student.model.Address;
import org.jda.example.coursemanmsa.student.repository.StudentRepository;
import org.jda.example.coursemanmsa.student.service.client.AddressRestTemplateClient;
import org.jda.example.coursemanmsa.student.service.client.ClassRestTemplateClient;
import org.jda.example.coursemanmsa.student.utils.UserContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class StudentService {

	@Autowired
	MessageSource messages;

	@Autowired
	private StudentRepository repository;

	@Autowired
	ServiceConfig config;


	@Autowired
	AddressRestTemplateClient addressRestClient;
	@Autowired
	ClassRestTemplateClient classRestClient;
	
	@Autowired
	SimpleSourceBean simpleSourceBean;
	
	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    
    public Student createEntity(Student arg0) {
    	arg0 = repository.save(arg0);
    	simpleSourceBean.publishChange(ActionEnum.UPDATED.name(),arg0.getId());
    	return arg0;
    }

    public Page getEntityListByPage(Pageable arg0) {
        return repository.findAll(arg0);
    }
    
    @CircuitBreaker(name = "studentService", fallbackMethod = "buildFallbackStudent")
	@RateLimiter(name = "studentService", fallbackMethod = "buildFallbackStudent")
	@Retry(name = "retryStudentService", fallbackMethod = "buildFallbackStudent")
//	@Bulkhead(name = "bulkheadStudentService", type= Type.THREADPOOL, fallbackMethod = "buildFallbackStudent")
	public Student getEntityById(String arg0) throws TimeoutException{
		Optional<Student> opt = repository.findById(arg0);
		Student obj = (opt.isPresent()) ? opt.get() : null;
		if (null == obj) {
			throw new IllegalArgumentException(String.format(messages.getMessage("student.search.error.message", null, null),""+ arg0));	
		}
		logger.debug("getStudent Correlation id: {}",UserContextHolder.getContext().getCorrelationId());
		Address address = retrieveAddressInfo(obj.getAddressId());
		if (null != address) {
			obj.setAddressName(address.getName());
		}
		
		StudentClass studentClass = retrieveClassInfo(obj.getStudentclassId());
		if(null != studentClass) {
			obj.setStudentClassName(studentClass.getName());
		}

		return obj;
	}
    
    public Address retrieveAddressInfo(int id) throws TimeoutException {
    	return addressRestClient.getData(id);
	}
	
	public StudentClass retrieveClassInfo(int id) throws TimeoutException {
		return classRestClient.getData(id);
	}

	@SuppressWarnings("unused")
	private Student buildFallbackStudent(String id, Throwable t){
		Student failEntity = new Student();
		failEntity.setStudentName("Resilience");
		return failEntity;
	}

    public Student updateEntity(String arg0, Student arg1) {
    	repository.save(arg1);
    	simpleSourceBean.publishChange(ActionEnum.UPDATED.name(),arg0);
    	return arg1;
    }

    public void deleteEntityById(String arg0) {
    	repository.deleteById(arg0);
    	simpleSourceBean.publishChange(ActionEnum.DELETED.name(),arg0);
    }

    public List<Student> getAllEntities() {
        return (List<Student>) repository.findAll();
    }

    public void updateTopic(Student obj){
    	simpleSourceBean.publishChange(ActionEnum.UPDATED.name(),obj.getId());
    }
    
    public List<Student> findByAddressId (int addressId){
    	return repository.findByAddressId(addressId);
    }
    
    public List<Student> findByStudentclassId (int studentclassId){
    	return repository.findByStudentclassId(studentclassId);
    }

}
