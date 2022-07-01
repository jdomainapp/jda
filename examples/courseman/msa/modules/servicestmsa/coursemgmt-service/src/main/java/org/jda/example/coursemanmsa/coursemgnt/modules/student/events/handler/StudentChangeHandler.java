package org.jda.example.coursemanmsa.coursemgnt.modules.student.events.handler;

import org.jda.example.coursemanmsa.coursemgnt.modules.student.events.CustomChannels;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.events.model.ChangeModel;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@EnableBinding(CustomChannels.class)
public class StudentChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(StudentChangeHandler.class);

	@Autowired
	StudentRepository repository;
	
	@Autowired
	RestTemplate restTemplate;
	
	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundStudentChanges")
	public void processStudentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Student entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a SAVE event from the assessmenthub service for student id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Student entity = getDataByREST(model.getId());
			repository.save(entity);
			logger.debug("Received a UPDATE event from the assessmenthub service for student id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			repository.deleteById(model.getId());
			logger.debug("Received a DELETE event from the assessmenthub service for student id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the assessmenthub service of type {}", model.getType());
		}
	}
	
	public Student getDataByREST(String id) {
		ResponseEntity<Student> restExchange = restTemplate.exchange(
				"http://gateway-server/assessmenthub-service/student/{id}", HttpMethod.GET, null, Student.class,id);

		return restExchange.getBody();
	}

}
