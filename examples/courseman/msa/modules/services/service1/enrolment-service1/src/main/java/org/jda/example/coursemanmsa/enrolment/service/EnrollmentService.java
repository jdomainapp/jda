package org.jda.example.coursemanmsa.enrolment.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.enrolment.config.ServiceConfig;
import org.jda.example.coursemanmsa.enrolment.model.Student;
import org.jda.example.coursemanmsa.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.enrolment.model.Coursemodule;
import org.jda.example.coursemanmsa.enrolment.repository.EnrollmentRepository;
import org.jda.example.coursemanmsa.enrolment.service.client.StudentRestTemplateClient;
import org.jda.example.coursemanmsa.enrolment.service.client.CourseRestTemplateClient;
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
public class EnrollmentService {

	@Autowired
	MessageSource messages;

	@Autowired
	private EnrollmentRepository repository;

	@Autowired
	ServiceConfig config;


	@Autowired
	StudentRestTemplateClient studentRestClient;
	@Autowired
	CourseRestTemplateClient courseRestClient;

	private static final Logger logger = LoggerFactory.getLogger(EnrollmentService.class);

    
    public Enrolment createEntity(Enrolment arg0) {
        return repository.save(arg0);
    }

    public Page getEntityListByPage(Pageable arg0) {
        return repository.findAll(arg0);
    }
    
//	@CircuitBreaker(name = "enrolmentService", fallbackMethod = "buildFallbackEnrolment")
//	@RateLimiter(name = "enrolmentService", fallbackMethod = "buildFallbackEnrolment")
//	@Retry(name = "retryEnrolmentService", fallbackMethod = "buildFallbackEnrolment")
//	@Bulkhead(name = "bulkheadEnrolmentService", type= Type.THREADPOOL, fallbackMethod = "buildFallbackEnrolment")
	public Enrolment getEntityById(int id) throws TimeoutException{
		Optional<Enrolment> opt = repository.findById(id);
		Enrolment obj = (opt.isPresent()) ? opt.get() : null;
		if (null == obj) {
			throw new IllegalArgumentException(String.format(messages.getMessage("student.search.error.message", null, null),""+ id));	
		}
		Student student = retrieveStudentInfo(obj.getStudentId());
		if (null != student) {
			obj.setStudent(student);
		}
		
		Coursemodule coursemodule = retrieveCourseInfo(obj.getCoursemoduleId());
		if(null != coursemodule) {
			obj.setCoursemodule(coursemodule);
		}

		return obj;
	}

	public Student retrieveStudentInfo(String id) throws TimeoutException {
		Student obj = studentRestClient.getData(id);
		return obj;
	}
	
	public Coursemodule retrieveCourseInfo(int id) throws TimeoutException {
		Coursemodule obj = courseRestClient.getData(id);
		return obj;
	}

	@SuppressWarnings("unused")
	private Enrolment buildFallbackEnrolment(String id, Throwable t){
		Enrolment failEntity = new Enrolment();
		return failEntity;
	}

    public Enrolment updateEntity(int arg0, Enrolment arg1) {
        return repository.save(arg1);
    }

    public void deleteEntityById(int arg0) {
        repository.deleteById(arg0);
    }

    public List<Enrolment> getAllEntities() {
        return (List<Enrolment>) repository.findAll();
    }
    
}
