package org.jda.example.coursemanmsa.coursemgnt.modules.studentenrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.RedirectController;

@Controller
public class StEnrolmentController<ID> extends RedirectController<ID> {

	public StEnrolmentController() {
		super();
		getPathmap().put("/student", Student.class);
		getPathmap().put("/enrolment", Enrolment.class);
	}

	@Override
	public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {
		return super.handleRequest(req, res, parentElement);
	}
}