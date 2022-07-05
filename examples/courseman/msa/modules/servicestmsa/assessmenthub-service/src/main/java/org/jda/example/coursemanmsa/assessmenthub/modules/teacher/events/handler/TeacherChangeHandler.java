package org.jda.example.coursemanmsa.assessmenthub.modules.teacher.events.handler;

import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.events.CustomChannels;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.events.model.ChangeModel;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@EnableBinding(CustomChannels.class)
public class TeacherChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(TeacherChangeHandler.class);

	@Autowired
	TeacherRepository repository;
	
	@Autowired
	RestTemplate restTemplate;
	
	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundTeacherChanges")
	public void processStudentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Teacher entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a SAVE event from the assessmenthub service for Teacher id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Teacher entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a UPDATE event from the assessmenthub service for Teacher id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			repository.deleteById(model.getId());
			logger.debug("Received a DELETE event from the assessmenthub service for Teacher id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the assessmenthub service of type {}", model.getType());
		}
	}
	
	public Teacher getDataByREST(int id) {
		ResponseEntity<Teacher> restExchange = restTemplate.exchange(
				"http://gateway-server/coursemgnt-service/coursemodulemgnt/teacher/{id}", HttpMethod.GET, null, Teacher.class,id);

		return restExchange.getBody();
	}

}
