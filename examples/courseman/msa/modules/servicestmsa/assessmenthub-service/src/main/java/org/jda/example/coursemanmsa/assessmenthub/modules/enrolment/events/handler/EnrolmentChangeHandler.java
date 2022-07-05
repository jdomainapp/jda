package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.events.handler;

import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.events.CustomChannels;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.events.model.ChangeModel;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.repository.EnrolmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@EnableBinding(CustomChannels.class)
public class EnrolmentChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(EnrolmentChangeHandler.class);
	
	@Autowired
	EnrolmentRepository repository;
	
	@Autowired
	RestTemplate restTemplate;

	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundEnrolmentChanges")
	public void processEnrolmentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Enrolment entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a SAVE event from the assessmenthub service for enrolment id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Enrolment entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a UPDATE event from the assessmenthub service for enrolment id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			repository.deleteById(model.getId());
			logger.debug("Received a DELETE event from the assessmenthub service for enrolment id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the assessmenthub service of type {}", model.getType());
		}
	}
	
	public Enrolment getDataByREST(int id) {
		ResponseEntity<Enrolment> restExchange = restTemplate.exchange(
				"http://gateway-server/coursemgnt-service/studentenrolment/enrolment/{id}", HttpMethod.GET, null, Enrolment.class,id);

		return restExchange.getBody();
	}

}
