package org.jda.example.coursemanmsa.academic.events.handler;

import org.jda.example.coursemanmsa.academic.events.CustomChannels;
import org.jda.example.coursemanmsa.academic.events.model.ChangeModel;
import org.jda.example.coursemanmsa.academic.model.Academic;
import org.jda.example.coursemanmsa.academic.model.Coursemodule;
import org.jda.example.coursemanmsa.academic.model.Student;
import org.jda.example.coursemanmsa.academic.service.AcademicService;
import org.jda.example.coursemanmsa.academic.service.CourseModuleService;
import org.jda.example.coursemanmsa.academic.service.StudentService;
import org.jda.example.coursemanmsa.academic.service.client.CourseRestTemplateClient;
import org.jda.example.coursemanmsa.academic.service.client.EnrolmentRestTemplateClient;
import org.jda.example.coursemanmsa.academic.service.client.StudentRestTemplateClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);

	@Autowired
	private AcademicService service;

	@Autowired
	EnrolmentRestTemplateClient restClient;
	
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

	@StreamListener("inboundEnrolmentChanges")
	public void processEnrolmentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			logger.debug("Received a SAVE event from the enrolment service for enrolment id {}", model.getId());
			Academic createdObj = restClient.getEntityByIdAndREST(Integer.parseInt(model.getId()));
			service.createEntity(createdObj);
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			logger.debug("Received a UPDATE event from the enrolment service for enrolment id {}", model.getId());
			Academic updatedObj = restClient.getEntityByIdAndREST(Integer.parseInt(model.getId()));
			service.updateEntity(Integer.parseInt(model.getId()), updatedObj);
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			logger.debug("Received a DELETE event from the enrolment service for enrolment id {}", model.getId());
			service.deleteEntityById(Integer.parseInt(model.getId()));
		} else {
			logger.error("Received an UNKNOWN event from the enrolment service of type {}", model.getType());
		}
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
