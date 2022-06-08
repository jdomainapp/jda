package org.jda.example.coursemanmsa.enrolment.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.enrolment.config.ServiceConfig;
import org.jda.example.coursemanmsa.enrolment.model.Student;
import org.jda.example.coursemanmsa.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.enrolment.model.Coursemodule;
import org.jda.example.coursemanmsa.enrolment.repository.EnrolmentRepository;
import org.jda.example.coursemanmsa.enrolment.service.client.StudentRestTemplateClient;
import org.jda.example.coursemanmsa.enrolment.service.client.CourseRestTemplateClient;
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

	@Autowired
	StudentRestTemplateClient studentRestClient;
	@Autowired
	CourseRestTemplateClient courseRestClient;

	private static final Logger logger = LoggerFactory.getLogger(EnrolmentService.class);

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
	public Enrolment getEntityById(int id) throws TimeoutException {
		Optional<Enrolment> opt = repository.findById(id);
		Enrolment entity = (opt.isPresent()) ? opt.get() : null;
		if (null == entity) {
			throw new IllegalArgumentException(
					String.format(messages.getMessage("student.search.error.message", null, null), "" + id));
		}
		Student student = retrieveStudentInfo(entity.getStudentId());
		if (null != student) {
			entity.setStudent(student);
		}

		Coursemodule coursemodule = retrieveCourseInfo(entity.getCoursemoduleId());
		if (null != coursemodule) {
			entity.setCoursemodule(coursemodule);
		}

		return entity;
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
	private Enrolment buildFallbackEnrolment(String id, Throwable t) {
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

	public List<Enrolment> getEntityListByCoursemoduleId(int id) throws TimeoutException {
		List<Enrolment> entityList = repository.findByCoursemoduleId(id);
		for (Enrolment entity : entityList) {
			Student student = retrieveStudentInfo(entity.getStudentId());
			if (null != student) {
				entity.setStudent(student);
			}

			Coursemodule coursemodule = retrieveCourseInfo(entity.getCoursemoduleId());
			if (null != coursemodule) {
				entity.setCoursemodule(coursemodule);
			}
		}
		return entityList;
	}

}
