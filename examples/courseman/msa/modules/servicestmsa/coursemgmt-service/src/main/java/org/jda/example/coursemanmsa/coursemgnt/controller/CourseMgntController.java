package org.jda.example.coursemanmsa.coursemgnt.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.utils.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="v1/coursemgnt/")
public class CourseMgntController {
	@RequestMapping(value = "/coursemodulemgnt/**")
	public ResponseEntity handleCoursemodulemgnt(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController controller = ControllerRegistry.getInstance().get("CourseModuleMgnt");
		return controller.handleRequest(req, res, "(.*)coursemgnt/coursemodulemgnt/(.+)");
	}
	
	@RequestMapping(value = "/studentenrolment/**")
	public ResponseEntity handleStudentenrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		DefaultController controller = ControllerRegistry.getInstance().get("StudentEnrolment");
		return controller.handleRequest(req, res, "(.*)coursemgnt/studentenrolment/(.+)");
	}
}
