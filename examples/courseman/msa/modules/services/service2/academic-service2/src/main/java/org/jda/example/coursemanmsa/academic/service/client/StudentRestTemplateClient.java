package org.jda.example.coursemanmsa.academic.service.client;

import org.jda.example.coursemanmsa.academic.model.Student;
import org.jda.example.coursemanmsa.academic.service.StudentService;
import org.jda.example.coursemanmsa.academic.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class StudentRestTemplateClient {

	@Autowired
	private StudentService service;

	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentRestTemplateClient.class);

	public Student getData(String id) {
		logger.debug("In EnrolmentService.getStudent: {}", UserContext.getCorrelationId());
		Student obj = checkDatabase(id);
		if(obj != null) {
			return obj;
		}
		obj = getDataByREST(id);
		if(obj !=null) {
			service.createEntity(obj);
		}
		return obj;
	}

	private Student checkDatabase(String id) {
		try {
			Student obj = service.getEntityById(id);
			return obj;
		} catch (Exception ex) {
			logger.error("Error encountered while trying to retrieve Student {} check database. Exception {}", id, ex);
			return null;
		}
	}
	
	public Student getDataByREST(String id) {
		ResponseEntity<Student> restExchange = restTemplate.exchange(
				"http://gateway-server/student-service/v1/student/{id}", HttpMethod.GET, null, Student.class,id);

		return restExchange.getBody();
	}
}
