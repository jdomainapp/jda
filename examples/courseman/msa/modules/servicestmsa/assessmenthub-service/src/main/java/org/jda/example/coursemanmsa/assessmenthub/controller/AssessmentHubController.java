package org.jda.example.coursemanmsa.assessmenthub.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.assessmenthub.modules.address.model.Address;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.CompulsoryModule;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.assessmenthub.modules.coursemodule.model.ElectiveModule;
import org.jda.example.coursemanmsa.assessmenthub.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.assessmenthub.modules.student.model.Student;
import org.jda.example.coursemanmsa.assessmenthub.modules.studentclass.model.StudentClass;
import org.jda.example.coursemanmsa.assessmenthub.modules.teacher.model.Teacher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jda.modules.msacommon.controller.ControllerRegistry;
import jda.modules.msacommon.controller.DefaultController;

@RestController
@RequestMapping(value = "/")
public class AssessmentHubController {
	
	public final static String PATH_STUDENT="/student/";
	public final static String PATH_TEACHER="/teacher/";
	public final static String PATH_COURSEMODULE="/coursemodule/";
	public final static String PATH_ELECTIVEMODULE="/electivemodule/";
	public final static String PATH_COMPULSORYMODULE="/compulsory/";
	public final static String PATH_ENROLMENT="/enrolment/";
	public final static String PATH_ADRESS="/address";
	public final static String PATH_CLASS="/class";

	@RequestMapping(value = PATH_STUDENT+"**")
	public ResponseEntity handleStudent(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		String path = req.getServletPath();
		String id= null;
		if(path.matches("(.*)"+PATH_STUDENT+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = pathVariable;
		}
		return controller.handleRequest(req, res, id);
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
		return controller.handleRequest(req, res, id);
	}
	
	@RequestMapping(value = PATH_COURSEMODULE+"**")
	public ResponseEntity handleCoursemodule(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<CourseModule, Integer> controller = ControllerRegistry.getInstance().get(CourseModule.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_COURSEMODULE+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id);
	}
	
	@RequestMapping(value = PATH_ELECTIVEMODULE+"**")
	public ResponseEntity handleElectivemodule(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<ElectiveModule, Integer> controller = ControllerRegistry.getInstance().get(ElectiveModule.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_ELECTIVEMODULE+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id);
	}
	
	@RequestMapping(value = PATH_COMPULSORYMODULE+"**")
	public ResponseEntity handleCompulsorymodule(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController<CompulsoryModule, Integer> controller = ControllerRegistry.getInstance().get(CompulsoryModule.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_COMPULSORYMODULE+"(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id);
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
		return controller.handleRequest(req, res, id);
	}
	
	@RequestMapping(value = PATH_ADRESS + "/**")
	public ResponseEntity<?> handleAddress(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DefaultController<Address, Integer> controller = ControllerRegistry.getInstance().get(Address.class);
		return controller != null ? controller.handleRequest(req, res)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	
	@RequestMapping(value = PATH_CLASS + "/**")
	public ResponseEntity<?> handleClass(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DefaultController<StudentClass, Integer> controller = ControllerRegistry.getInstance().get(StudentClass.class);
		return controller != null ? controller.handleRequest(req, res)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
}
