package org.jda.example.coursemanmsa.assessmenthub.events.handler;

import org.jda.example.coursemanmsa.assessmenthub.events.CustomChannels;
import org.jda.example.coursemanmsa.assessmenthub.modules.address.model.Address;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.jda.example.coursemanmsa.assessmenthub.modules.studentclass.model.StudentClass;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import jda.modules.msacommon.controller.ControllerRegistry;
import jda.modules.msacommon.controller.DefaultController;
import jda.modules.msacommon.events.model.ChangeModel;

@EnableBinding(CustomChannels.class)
public class ChangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(ChangeHandler.class);

	@StreamListener("inboundCourseChanges")
	public void processCoursemoduleChanges(ChangeModel<Integer> model) {
		logger.debug("Received a message of type " + model.getType());
		DefaultController<CourseModule, Integer> controller = ControllerRegistry.getInstance().get(CourseModule.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundEnrolmentChanges")
	public void processEnrolmentChanges(ChangeModel<Integer> model) {
		logger.debug("Received a message of type " + model.getType());
		DefaultController<Enrolment, Integer> controller = ControllerRegistry.getInstance().get(Enrolment.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundStudentChanges")
	public void processStudentChanges(ChangeModel<String> model) {
		logger.debug("Received a message of type " + model.getType());
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundTeacherChanges")
	public void processTeacherChanges(ChangeModel<Integer> model) {
		logger.debug("Received Kafka message {} for "+ model.getType()+" Id: {}", model.getAction(), model.getId());
	    DefaultController<Teacher, Integer> controller = ControllerRegistry.getInstance().get(Teacher.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundAddressChanges")
	public void processAddressChanges(ChangeModel<Integer> model) {
		logger.debug("Received Kafka message {} for "+ model.getType()+" Id: {}", model.getAction(), model.getId());
	    DefaultController<Address, Integer> controller = ControllerRegistry.getInstance().get(Address.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}
	
	@StreamListener("inboundClassChanges")
	public void processClassChanges(ChangeModel<Integer> model) {
		logger.debug("Received Kafka message {} for "+ model.getType()+" Id: {}", model.getAction(), model.getId());
	    DefaultController<StudentClass, Integer> controller = ControllerRegistry.getInstance().get(StudentClass.class);
		controller.executeReceivedEvent(model.getAction(), model.getId(), model.getPath());
	}

}
