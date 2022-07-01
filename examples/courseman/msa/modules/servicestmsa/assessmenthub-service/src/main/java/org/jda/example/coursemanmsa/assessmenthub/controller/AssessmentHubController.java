package org.jda.example.coursemanmsa.assessmenthub.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.assessmenthub.utils.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.assessmenthub.utils.controller.DefaultController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class AssessmentHubController {

	@RequestMapping(value = "/student/**")
	public ResponseEntity handleStudent(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		return controller.handleRequest(req, res, "/assessmenthub/student/");
	}
	
	@RequestMapping(value = "/teacher/**")
	public ResponseEntity handleTeacher(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Teacher, String> controller = ControllerRegistry.getInstance().get(Teacher.class);
		return controller.handleRequest(req, res, "/assessmenthub/teacher/");
	}
	
	@RequestMapping(value = "/coursemodule/**")
	public ResponseEntity handleCoursemodule(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Coursemodule, String> controller = ControllerRegistry.getInstance().get(Coursemodule.class);
		return controller.handleRequest(req, res, "/assessmenthub/coursemodule/");
	}
	
	@RequestMapping(value = "/enrolment/**")
	public ResponseEntity handleEnrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Enrolment, String> controller = ControllerRegistry.getInstance().get(Enrolment.class);
		return controller.handleRequest(req, res, "/assessmenthub/enrolment/(.+)");
	}
}
