package org.jda.example.coursemanmsa.coursemgnt.modules.coursemodulemgmt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class CourseModuleMgntController {
	public ResponseEntity handleCoursemodule(HttpServletRequest req, HttpServletResponse res, String pathPatern) throws IOException {
		DefaultController<Coursemodule, String> controller = ControllerRegistry.getInstance().get(Coursemodule.class);
		return controller.handleRequest(req, res, pathPatern);
	}
	
	public ResponseEntity handleTeacher(HttpServletRequest req, HttpServletResponse res, String pathPatern) throws IOException {
		DefaultController<Teacher, String> controller = ControllerRegistry.getInstance().get(Teacher.class);
		return controller.handleRequest(req, res, pathPatern);
	}
}