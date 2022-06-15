package org.jda.example.coursemanmsa.academic.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.academic.config.ServiceConfig;
import org.jda.example.coursemanmsa.academic.model.Academic;
import org.jda.example.coursemanmsa.academic.model.Coursemodule;
import org.jda.example.coursemanmsa.academic.model.Student;
import org.jda.example.coursemanmsa.academic.repository.AcademicRepository;
import org.jda.example.coursemanmsa.academic.service.client.CourseRestTemplateClient;
import org.jda.example.coursemanmsa.academic.service.client.EnrolmentRestTemplateClient;
import org.jda.example.coursemanmsa.academic.service.client.StudentRestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AcademicService {

	@Autowired
	MessageSource messages;

	@Autowired
	ServiceConfig config;
	
	@Autowired
	EnrolmentRestTemplateClient restClient;
	@Autowired
	StudentRestTemplateClient studentRestClient;
	@Autowired
	CourseRestTemplateClient courseRestClient;
	
	@Autowired
	AcademicRepository repository;

	private static final Logger logger = LoggerFactory.getLogger(AcademicService.class);

	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}
    
    public Academic createEntity(Academic arg0) {
    	arg0 = repository.save(arg0);
        return arg0;
    }

    public Page getEntityListByPage(Pageable arg0) {
        return repository.findAll(arg0);
    }
    
    public List<Academic> getEntityByCoursemoduleId(int id){
    	return repository.findByCoursemoduleId(id);
    }
    
//	@CircuitBreaker(name = "academicService", fallbackMethod = "buildFallbackAcademic")
//	@RateLimiter(name = "academicService", fallbackMethod = "buildFallbackAcademic")
//	@Retry(name = "retryAcademicService", fallbackMethod = "buildFallbackAcademic")
//	@Bulkhead(name = "bulkheadAcademicService", type= Type.THREADPOOL, fallbackMethod = "buildFallbackAcademic")
	public Academic getEntityById(int id) throws TimeoutException{
		Optional<Academic> opt = repository.findById(id);
		Academic obj = (opt.isPresent()) ? opt.get() : null;
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
	private Academic buildFallbackAcademic(String id, Throwable t){
		Academic failEntity = new Academic();
		return failEntity;
	}

    public Academic updateEntity(int arg0, Academic arg1) {
    	arg1 = repository.save(arg1);
        return arg1;
    }

    public void deleteEntityById(int arg0) {
        repository.deleteById(arg0);
    }

    public List<Academic> getAllEntities() {
        return (List<Academic>) repository.findAll();
    }
    
}
