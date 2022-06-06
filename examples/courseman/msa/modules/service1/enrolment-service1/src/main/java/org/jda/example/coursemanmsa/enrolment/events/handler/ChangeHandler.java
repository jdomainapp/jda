package org.jda.example.coursemanmsa.enrolment.events.handler;

import org.jda.example.coursemanmsa.enrolment.events.CustomChannels;
import org.jda.example.coursemanmsa.enrolment.events.model.ChangeModel;
import org.jda.example.coursemanmsa.enrolment.model.Student;
import org.jda.example.coursemanmsa.enrolment.model.Coursemodule;
import org.jda.example.coursemanmsa.enrolment.service.StudentService;
import org.jda.example.coursemanmsa.enrolment.service.CourseModuleService;
import org.jda.example.coursemanmsa.enrolment.service.client.StudentRestTemplateClient;
import org.jda.example.coursemanmsa.enrolment.service.client.CourseRestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);

	@Autowired
	private StudentService studentService;

	@Autowired
	StudentRestTemplateClient studentRestClient;
	
	@Autowired
	private CourseModuleService courseService;

	@Autowired
	CourseRestTemplateClient courseRestClient;

	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundStudentChanges")
	public void processStudentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			logger.debug("Received a SAVE event from the student service for address id {}", model.getId());
			Student createdObj = studentRestClient.getDataByREST(model.getId());
			studentService.createEntity(createdObj);
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			logger.debug("Received a UPDATE event from the student service for address id {}", model.getId());
			Student updatedObj = studentRestClient.getDataByREST(model.getId());
			studentService.updateEntity(model.getId(), updatedObj);
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			logger.debug("Received a DELETE event from the student service for address id {}", model.getId());
			studentService.deleteEntityById(model.getId());
		} else {
			logger.error("Received an UNKNOWN event from the student service of type {}", model.getType());
		}
	}
	
	@StreamListener("inboundCourseChanges")
	public void processCourseChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			logger.debug("Received a SAVE event from the course service for class id {}", model.getId());
			Coursemodule createdObj = courseRestClient.getDataByREST(Integer.parseInt(model.getId()));
			courseService.createEntity(createdObj);
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			logger.debug("Received a UPDATE event from the course service for class id {}", model.getId());
			Coursemodule updatedObj = courseRestClient.getDataByREST(Integer.parseInt(model.getId()));
			courseService.updateEntity(Integer.parseInt(model.getId()), updatedObj);
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			logger.debug("Received a DELETE event from the course service for class id {}", model.getId());
			courseService.deleteEntityById(Integer.parseInt(model.getId()));
		} else {
			logger.error("Received an UNKNOWN event from the course service of type {}", model.getType());
		}
	}

}
