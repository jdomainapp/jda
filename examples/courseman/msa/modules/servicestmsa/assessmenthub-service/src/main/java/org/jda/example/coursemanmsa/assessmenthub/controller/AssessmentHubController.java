package org.jda.example.coursemanmsa.assessmenthub.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="v1/assessmenthub/")
public class AssessmentHubController {

	@RequestMapping(value="student/")
	public void handleStudent(HttpServletRequest req, HttpServletResponse res) {
		System.out.println("Test");
	}
}
