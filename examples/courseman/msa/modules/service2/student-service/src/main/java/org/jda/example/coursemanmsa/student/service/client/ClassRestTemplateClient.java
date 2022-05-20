package org.jda.example.coursemanmsa.student.service.client;

import org.jda.example.coursemanmsa.student.model.StudentClass;
import org.jda.example.coursemanmsa.student.service.StudentClassService;
import org.jda.example.coursemanmsa.student.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ClassRestTemplateClient {

	@Autowired
	private StudentClassService service;

	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(ClassRestTemplateClient.class);

	public StudentClass getData(int id) {
		logger.debug("In StudentService.getData: {}", UserContext.getCorrelationId());
		StudentClass obj = checkDatabase(id);
		if(obj != null) {
			return obj;
		}
		obj = getDataByREST(id);
		if(obj !=null) {
			service.createEntity(obj);
		}
		return obj;
	}

	private StudentClass checkDatabase(int id) {
		try {
			StudentClass obj = service.getEntityById(id);
			return obj;
		} catch (Exception ex) {
			logger.error("Error encountered while trying to retrieve studentClass {} check database. Exception {}", id, ex);
			return null;
		}
	}
	
	public StudentClass getDataByREST(int id) {
		ResponseEntity<StudentClass> restExchange = restTemplate.exchange(
				"http://gateway-server/class-service/v1/class/{id}", HttpMethod.GET, null, StudentClass.class,id);

		return restExchange.getBody();
	}
}
