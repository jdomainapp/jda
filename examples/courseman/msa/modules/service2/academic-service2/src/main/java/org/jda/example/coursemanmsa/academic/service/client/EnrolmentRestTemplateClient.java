package org.jda.example.coursemanmsa.academic.service.client;

import java.util.List;

import org.jda.example.coursemanmsa.academic.model.Enrolment;
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

	
	public List<Enrolment> getDataByREST(int id) {
		ResponseEntity<List<Enrolment>> restExchange = restTemplate.exchange(
				"http://gateway-server/enrolment-service/v1/enrolment/coursemodule/{id}", HttpMethod.GET, 
				null, new ParameterizedTypeReference<List<Enrolment>>() {},id);

		return restExchange.getBody();
	}
	
	public Enrolment updateDataByREST(int arg0, Enrolment entity) {
		HttpEntity<Enrolment> requestBody = new HttpEntity<>(entity);
		ResponseEntity<Enrolment> restExchange = restTemplate.exchange(
				"http://gateway-server/enrolment-service/v1/enrolment/{id}", HttpMethod.PUT, 
				requestBody, Enrolment.class,arg0);

		return restExchange.getBody();
	}
}
