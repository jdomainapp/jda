package org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.events.handler;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.events.CustomChannels;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.events.model.ChangeModel;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.repository.CoursemoduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@EnableBinding(CustomChannels.class)
public class CoursemoduleChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(CoursemoduleChangeHandler.class);
	
	@Autowired
	CoursemoduleRepository repository;
	
	@Autowired
	RestTemplate restTemplate;

	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundCoursemoduleChanges")
	public void processCoursemoduleChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Coursemodule entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a SAVE event from the assessmenthub service for coursemodule id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Coursemodule entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a UPDATE event from the assessmenthub service for coursemodule id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			repository.deleteById(model.getId());
			logger.debug("Received a DELETE event from the assessmenthub service for coursemodule id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the assessmenthub service of type {}", model.getType());
		}
	}
	
	public Coursemodule getDataByREST(int id) {
		ResponseEntity<Coursemodule> restExchange = restTemplate.exchange(
				"http://gateway-server/coursemgnt-service/coursemodulemgnt/coursemodule/{id}", HttpMethod.GET, null, Coursemodule.class,id);

		return restExchange.getBody();
	}

}
