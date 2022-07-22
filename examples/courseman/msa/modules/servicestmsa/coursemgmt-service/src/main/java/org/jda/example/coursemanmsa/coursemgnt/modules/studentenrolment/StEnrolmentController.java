package org.jda.example.coursemanmsa.coursemgnt.modules.studentenrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.common.controller.DefaultController;
import org.jda.example.coursemanmsa.common.controller.RedirectController;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**ducmle: renamed to match path update */
@Controller
//public class StudentEnrolmentController 
// FIXME: refactor this class to use PathMap and match...Path functions, similar to CModuleMgntController 
// 
public class StEnrolmentController
extends RedirectController{
	
	
	public final static String PATH_STUDENT="/student";
	public final static String PATH_ENROLMENT="/enrolment";
	
	@Override
	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPattern) {
		String path = req.getServletPath();
		if(path.matches("(.*)"+PATH_STUDENT+"(.*)")) {
			return handleStudent(req, res);
		}else if(path.matches("(.*)"+PATH_ENROLMENT+"(.*)")) {
			return handleEnrolment(req, res);
		}else {
			return new MyResponseEntity (ResponseEntity.ok("No method for request URL"), null);
		}
	}
	
	public MyResponseEntity handleStudent(HttpServletRequest req, HttpServletResponse res){
		DefaultController<Student, String> controller = ControllerRegistry.getInstance().get(Student.class);
		String path = req.getServletPath();
		String id= null;
		if(path.matches("(.*)"+PATH_STUDENT+"/(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = pathVariable;
		}
		return controller.handleRequest(req, res, id);
	}
	
	public MyResponseEntity handleEnrolment(HttpServletRequest req, HttpServletResponse res){
		DefaultController<Enrolment, Integer> controller = ControllerRegistry.getInstance().get(Enrolment.class);
		String path = req.getServletPath();
		Integer id= null;
		if(path.matches("(.*)"+PATH_ENROLMENT+"/(.+)")) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = Integer.parseInt(pathVariable);
		}
		return controller.handleRequest(req, res, id);
	}
}