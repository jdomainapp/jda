package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodulemgmt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.RedirectController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CourseModuleMgntController extends RedirectController{
	
	@Override
	public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPattern) {
		String path = req.getServletPath();
		if(path.matches("(.*)"+pathPattern+"coursemodule/(.*)")) {
			return handleCoursemodule(req, res, pathPattern+"coursemodule/");
		}else if(path.matches("(.*)"+pathPattern+"teacher/(.*)")) {
			return handleTeacher(req, res, pathPattern+"teacher/");
		}else {
			return ResponseEntity.ok("No method for request URL");
		}
	}
	
	public ResponseEntity handleCoursemodule(HttpServletRequest req, HttpServletResponse res, String pathPatern){
		DefaultController<Coursemodule, String> controller = ControllerRegistry.getInstance().get(Coursemodule.class);
		return controller.handleRequest(req, res, pathPatern);
	}
	
	public ResponseEntity handleTeacher(HttpServletRequest req, HttpServletResponse res, String pathPatern){
		DefaultController<Teacher, String> controller = ControllerRegistry.getInstance().get(Teacher.class);
		return controller.handleRequest(req, res, pathPatern);
	}
}