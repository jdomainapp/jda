package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.assessmenthub.config.ServiceConfig;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.repository.EnrolmentRepository;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EnrolmentService {

	@Autowired
	MessageSource messages;

	@Autowired
	private EnrolmentRepository repository;

	@Autowired
	ServiceConfig config;

	
//	@Autowired
//	SimpleSourceBean simpleSourceBean;
	
	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	private static final Logger logger = LoggerFactory.getLogger(EnrolmentService.class);

    
    public Enrolment createEntity(Enrolment arg0) {
    	arg0 = repository.save(arg0);
//    	simpleSourceBean.publishChange(ActionEnum.CREATED.name(),""+arg0.getId());
        return arg0;
    }

    public Page getEntityListByPage(Pageable arg0) {
        return repository.findAll(arg0);
    }
    
    public List<Enrolment> getEntityByCoursemoduleId(int id){
    	return repository.findByCoursemoduleId(id);
    }
    
//	@CircuitBreaker(name = "enrolmentService", fallbackMethod = "buildFallbackEnrolment")
//	@RateLimiter(name = "enrolmentService", fallbackMethod = "buildFallbackEnrolment")
//	@Retry(name = "retryEnrolmentService", fallbackMethod = "buildFallbackEnrolment")
//	@Bulkhead(name = "bulkheadEnrolmentService", type= Type.THREADPOOL, fallbackMethod = "buildFallbackEnrolment")
	public Enrolment getEntityById(int id) throws TimeoutException{
		Optional<Enrolment> opt = repository.findById(id);
		return (opt.isPresent()) ? opt.get() : null;
	}

	@SuppressWarnings("unused")
	private Enrolment buildFallbackEnrolment(String id, Throwable t){
		Enrolment failEntity = new Enrolment();
		return failEntity;
	}

    public Enrolment updateEntity(int arg0, Enrolment arg1) {
    	arg1 = repository.save(arg1);
//    	simpleSourceBean.publishChange(ActionEnum.UPDATED.name(),""+arg0);
        return arg1;
    }

    public void deleteEntityById(int arg0) {
        repository.deleteById(arg0);
//        simpleSourceBean.publishChange(ActionEnum.DELETED.name(),""+arg0);
    }

    public List<Enrolment> getAllEntities() {
        return (List<Enrolment>) repository.findAll();
    }
    
}
