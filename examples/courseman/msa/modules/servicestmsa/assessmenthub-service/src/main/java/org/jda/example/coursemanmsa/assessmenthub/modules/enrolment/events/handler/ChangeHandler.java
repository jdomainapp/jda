package org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.events.handler;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.events.CustomChannels;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.events.model.ChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);

	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundStudentChanges")
	public void processStudentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			logger.debug("Received a SAVE event from the student service for address id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			logger.debug("Received a UPDATE event from the student service for address id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			logger.debug("Received a DELETE event from the student service for address id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the student service of type {}", model.getType());
		}
	}
	
	@StreamListener("inboundCourseChanges")
	public void processCourseChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			logger.debug("Received a SAVE event from the course service for class id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			logger.debug("Received a UPDATE event from the course service for class id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			logger.debug("Received a DELETE event from the course service for class id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the course service of type {}", model.getType());
		}
	}

}
