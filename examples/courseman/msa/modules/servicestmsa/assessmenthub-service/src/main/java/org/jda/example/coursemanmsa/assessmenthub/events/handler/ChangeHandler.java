package org.jda.example.coursemanmsa.assessmenthub.events.handler;

import org.jda.example.coursemanmsa.assessmenthub.events.CustomChannels;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.common.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.common.controller.DefaultController;
import org.jda.example.coursemanmsa.common.events.model.ChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(CustomChannels.class)
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);

	@StreamListener("inboundCourseChanges")
	public void processCoursemoduleChanges(ChangeModel<Integer> model) {
		logger.debug("Received a message of type " + model.getType());
//		String restPath="http://gateway-server/coursemgnt-service/coursemodulemgnt/coursemodule/{id}";
		DefaultController<CourseModule, Integer> controller = ControllerRegistry.getInstance().get(CourseModule.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundEnrolmentChanges")
	public void processEnrolmentChanges(ChangeModel<Integer> model) {
		logger.debug("Received a message of type " + model.getType());
//		String restPath="http://gateway-server/coursemgnt-service/studentenrolment/enrolment/{id}";
		DefaultController<Enrolment, Integer> controller = ControllerRegistry.getInstance().get(Enrolment.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundStudentChanges")
	public void processStudentChanges(ChangeModel<String> model) {
		logger.debug("Received a message of type " + model.getType());
//		String restPath="http://gateway-server/coursemgnt-service/studentenrolment/student/{id}";
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundTeacherChanges")
	public void processTeacherChanges(ChangeModel<Integer> model) {
		logger.debug("Received a message of type " + model.getType());
//		String restPath="http://gateway-server/coursemgnt-service/coursemodulemgnt/teacher/{id}";
		DefaultController<Teacher, Integer> controller = ControllerRegistry.getInstance().get(Teacher.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}

}