package org.jda.example.coursemanmsa.assessmenthub.events.handler;

import org.jda.example.coursemanmsa.assessmenthub.events.CustomChannels;
import org.jda.example.coursemanmsa.assessmenthub.events.model.ChangeModel;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.repository.CoursemoduleRepository;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.repository.EnrolmentRepository;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.repository.StudentRepository;
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
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);
	
	@Autowired
	CoursemoduleRepository coursemoduleRepository;
	
	@Autowired
	EnrolmentRepository enrolmentRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	TeacherRepository teacherRepository;
	
	@Autowired
	RestTemplate restTemplate;

	public enum ActionEnum {
		GET, CREATED, UPDATED, DELETED
	}

	@StreamListener("inboundCoursemoduleChanges")
	public void processCoursemoduleChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Coursemodule entity = getCoursemoduleByREST(model.getId());
			coursemoduleRepository.save(entity);
			logger.debug("Received a SAVE event from the coursemgnt service for coursemodule id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Coursemodule entity = getCoursemoduleByREST(model.getId());
			coursemoduleRepository.save(entity);
			logger.debug("Received a UPDATE event from the coursemgnt service for coursemodule id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			coursemoduleRepository.deleteById(Integer.parseInt(model.getId()));
			logger.debug("Received a DELETE event from the coursemgnt service for coursemodule id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the coursemgnt service of type {}", model.getType());
		}
	}
	
	@StreamListener("inboundEnrolmentChanges")
	public void processEnrolmentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Enrolment entity = getEnrolmentByREST(model.getId());
			enrolmentRepository.save(entity);
			logger.debug("Received a SAVE event from the coursemgnt service for enrolment id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Enrolment entity = getEnrolmentByREST(model.getId());
			enrolmentRepository.save(entity);
			logger.debug("Received a UPDATE event from the coursemgnt service for enrolment id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			enrolmentRepository.deleteById(Integer.parseInt(model.getId()));
			logger.debug("Received a DELETE event from the coursemgnt service for enrolment id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the coursemgnt service of type {}", model.getType());
		}
	}
	
	@StreamListener("inboundStudentChanges")
	public void processStudentChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Student entity = getStudentByREST(model.getId());
			studentRepository.save(entity);
			logger.debug("Received a SAVE event from the coursemgnt service for student id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Student entity = getStudentByREST(model.getId());
			studentRepository.save(entity);
			logger.debug("Received a UPDATE event from the coursemgnt service for student id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			studentRepository.deleteById(model.getId());
			logger.debug("Received a DELETE event from the coursemgnt service for student id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the assessmenthub service of type {}", model.getType());
		}
	}
	
	@StreamListener("inboundTeacherChanges")
	public void processTeacherChanges(ChangeModel model) {

		logger.debug("Received a message of type " + model.getType());

		if (model.getAction().equals(ActionEnum.CREATED.name())) {
			Teacher entity = getTeacherByREST(model.getId());
			teacherRepository.save(entity);
			logger.debug("Received a SAVE event from the coursemgnt service for Teacher id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.UPDATED.name())) {
			Teacher entity = getTeacherByREST(model.getId());
			teacherRepository.save(entity);
			logger.debug("Received a UPDATE event from the coursemgnt service for Teacher id {}", model.getId());
			
		} else if (model.getAction().equals(ActionEnum.DELETED.name())) {
			teacherRepository.deleteById(Integer.parseInt(model.getId()));
			logger.debug("Received a DELETE event from the coursemgnt service for Teacher id {}", model.getId());
			
		} else {
			logger.error("Received an UNKNOWN event from the coursemgnt service of type {}", model.getType());
		}
	}
	
	public Coursemodule getCoursemoduleByREST(String id) {
		ResponseEntity<Coursemodule> restExchange = restTemplate.exchange(
				"http://gateway-server/coursemgnt-service/coursemodulemgnt/coursemodule/{id}", HttpMethod.GET, null, Coursemodule.class,id);

		return restExchange.getBody();
	}
	
	public Enrolment getEnrolmentByREST(String id) {
		ResponseEntity<Enrolment> restExchange = restTemplate.exchange(
				"http://gateway-server/coursemgnt-service/studentenrolment/coursemodule/{id}", HttpMethod.GET, null, Enrolment.class,id);

		return restExchange.getBody();
	}
	
	public Student getStudentByREST(String id) {
		ResponseEntity<Student> restExchange = restTemplate.exchange(
				"http://gateway-server/coursemgnt-service/studentenrolment/student/{id}", HttpMethod.GET, null, Student.class,id);

		return restExchange.getBody();
	}
	
	public Teacher getTeacherByREST(String id) {
		ResponseEntity<Teacher> restExchange = restTemplate.exchange(
				"http://gateway-server/coursemgnt-service/coursemodulemgnt/teacher/{id}", HttpMethod.GET, null, Teacher.class,id);

		return restExchange.getBody();
	}

}
