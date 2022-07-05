package org.jda.example.coursemanmsa.coursemgnt.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.utils.controller.RedirectController;
import org.jda.example.coursemanmsa.coursemgnt.utils.controller.RedirectControllerRegistry;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/")
public class CourseMgntController {
	
	public final static String PATH_COURSEMODULEMGNT="/coursemodulemgnt/";
	public final static String PATH_STUDENTENROLMENT="/studentenrolment/";
	
	@RequestMapping(value = PATH_COURSEMODULEMGNT+"**")
	public ResponseEntity handleCoursemodulemgnt(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("coursemodulemgnt");
		return controller.handleRequest(req, res, PATH_COURSEMODULEMGNT);
	}
	
	@RequestMapping(value = PATH_STUDENTENROLMENT+"**")
	public ResponseEntity handleStudentenrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("studentenrolment");
		return controller.handleRequest(req, res,PATH_STUDENTENROLMENT);
	}
}
