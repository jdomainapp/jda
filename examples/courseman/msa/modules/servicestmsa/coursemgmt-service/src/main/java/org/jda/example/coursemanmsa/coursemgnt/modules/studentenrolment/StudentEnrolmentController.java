package org.jda.example.coursemanmsa.coursemgnt.modules.studentenrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.RedirectController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class StudentEnrolmentController extends RedirectController{
	
	
	public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPattern){
		String path = req.getServletPath();
		if(path.matches("(.*)"+pathPattern+"student/(.*)")) {
			return handleStudent(req, res, pathPattern+"student/");
		}else if(path.matches("(.*)"+pathPattern+"enrolment/(.*)")) {
			return handleEnrolment(req, res, pathPattern+"enrolment/");
		}else {
			return ResponseEntity.ok("No method for request URL");
		}
	}
	
	public ResponseEntity handleStudent(HttpServletRequest req, HttpServletResponse res, String pathPatern){
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		return controller.handleRequest(req, res, pathPatern);
	}
	
	public ResponseEntity handleEnrolment(HttpServletRequest req, HttpServletResponse res, String pathPatern){
		DefaultController<Enrolment, String> controller = ControllerRegistry.getInstance().get(Enrolment.class);
		return controller.handleRequest(req, res, pathPatern);
	}
}