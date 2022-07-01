package org.jda.example.coursemanmsa.coursemgnt.modules.studentenrolment;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class StudentEnrolmentController {
	public ResponseEntity handleStudent(HttpServletRequest req, HttpServletResponse res, String pathPatern) throws IOException {
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		return controller.handleRequest(req, res, pathPatern);
	}
	
	public ResponseEntity handleEnrolment(HttpServletRequest req, HttpServletResponse res, String pathPatern) throws IOException {
		DefaultController<Enrolment, String> controller = ControllerRegistry.getInstance().get(Enrolment.class);
		return controller.handleRequest(req, res, pathPatern);
	}
}