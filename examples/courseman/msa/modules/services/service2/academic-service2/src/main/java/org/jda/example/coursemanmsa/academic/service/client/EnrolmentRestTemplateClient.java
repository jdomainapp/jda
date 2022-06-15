package org.jda.example.coursemanmsa.academic.service.client;

import java.util.List;

import org.jda.example.coursemanmsa.academic.model.Academic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EnrolmentRestTemplateClient {

	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(EnrolmentRestTemplateClient.class);

	
	public List<Academic> getEntityByCoursemoduleIdAndREST(int id) {
		ResponseEntity<List<Academic>> restExchange = restTemplate.exchange(
				"http://gateway-server/enrolment-service/v1/enrolment/coursemodule/{id}", HttpMethod.GET, 
				null, new ParameterizedTypeReference<List<Academic>>() {},id);

		return restExchange.getBody();
	}
	
	public Academic updateDataByREST(int arg0, Academic entity) {
		HttpEntity<Academic> requestBody = new HttpEntity<>(entity);
		ResponseEntity<Academic> restExchange = restTemplate.exchange(
				"http://gateway-server/enrolment-service/v1/enrolment/{id}", HttpMethod.PUT, 
				requestBody, Academic.class,arg0);

		return restExchange.getBody();
	}
	
	public Academic getEntityByIdAndREST(int id) {
		ResponseEntity<Academic> restExchange = restTemplate.exchange(
				"http://gateway-server/enrolment-service/v1/enrolment/{id}", HttpMethod.GET, 
				null, Academic.class,id);

		return restExchange.getBody();
	}
	

}
