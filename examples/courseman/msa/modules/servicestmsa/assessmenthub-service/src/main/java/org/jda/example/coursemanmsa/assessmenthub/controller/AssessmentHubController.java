package org.jda.example.coursemanmsa.assessmenthub.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.common.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.common.controller.DefaultController;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class AssessmentHubController {
	
	public final static String PATH_STUDENT="/student/";
	public final static String PATH_TEACHER="/teacher/";
	public final static String PATH_COURSEMODULE="/coursemodule/";
	public final static String PATH_ENROLMENT="/enrolment/";

	@RequestMapping(value = PATH_STUDENT+"**")
	public ResponseEntity handleStudent(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		String path = req.getServletPath();
		String id= null;
		if(path.matches("(.*)"+PATH_STUDENT+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = pathVariable;
		}
		return controller.handleRequest(req, res, id).getResponseEntity();
	}
	
	@RequestMapping(value = PATH_TEACHER+"**")
	public ResponseEntity handleTeacher(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Teacher, Integer> controller = ControllerRegistry.getInstance().get(Teacher.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_TEACHER+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id).getResponseEntity();
	}
	
	@RequestMapping(value = PATH_COURSEMODULE+"**")
	public ResponseEntity handleCoursemodule(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Coursemodule, Integer> controller = ControllerRegistry.getInstance().get(Coursemodule.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_COURSEMODULE+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id).getResponseEntity();
	}
	
	@RequestMapping(value = PATH_ENROLMENT+"**")
	public ResponseEntity handleEnrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Enrolment, Integer> controller = ControllerRegistry.getInstance().get(Enrolment.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_ENROLMENT+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id).getResponseEntity();
	}
}
