package org.jda.example.coursemanmsa.academic.service.client;

import org.jda.example.coursemanmsa.academic.model.Coursemodule;
import org.jda.example.coursemanmsa.academic.service.CourseModuleService;
import org.jda.example.coursemanmsa.academic.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CourseRestTemplateClient {

	@Autowired
	private CourseModuleService service;

	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(CourseRestTemplateClient.class);

	public Coursemodule getData(int id) {
		logger.debug("In AcademicService.getCoursemodule: {}", UserContext.getCorrelationId());
		Coursemodule obj = checkDatabase(id);
		if(obj != null) {
			return obj;
		}
		obj = getDataByREST(id);
		if(obj !=null) {
			service.createEntity(obj);
		}
		return obj;
	}

	private Coursemodule checkDatabase(int id) {
		try {
			Coursemodule obj = service.getEntityById(id);
			return obj;
		} catch (Exception ex) {
			logger.error("Error encountered while trying to retrieve Coursemodule {} check database. Exception {}", id, ex);
			return null;
		}
	}
	
	public Coursemodule getDataByREST(int id) {
		ResponseEntity<Coursemodule> restExchange = restTemplate.exchange(
				"http://gateway-server/course-service/v1/course/view/{id}", HttpMethod.GET, null, Coursemodule.class,id);

		return restExchange.getBody();
	}
}
