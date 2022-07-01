package org.jda.example.coursemanmsa.coursemgnt.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.CoursemoduleController;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodulemgmt.CourseModuleMgntController;
import org.jda.example.coursemanmsa.coursemgnt.modules.studentenrolment.StudentEnrolmentController;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.ControllerRegistry;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.DefaultController;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.RedirectController;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.RedirectControllerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/")
public class CourseMgntController {
	
	@RequestMapping(value = "/coursemodulemgnt/**")
	public ResponseEntity handleCoursemodulemgnt(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("coursemodulemgnt");
		return controller.handleRequest(req, res, "/coursemodulemgnt/");
	}
	
	@RequestMapping(value = "/studentenrolment/**")
	public ResponseEntity handleStudentenrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("coursemodulemgnt");
		return controller.handleRequest(req, res,"/studentenrolment/");
	}
}
