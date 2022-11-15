package org.jda.example.coursemanmsa.coursemgnt.modules.studentenrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.springframework.stereotype.Controller;

import jda.modules.msacommon.controller.RedirectController;
import jda.modules.msacommon.model.MyResponseEntity;

@Controller
public class StEnrolmentController<ID> extends RedirectController<ID> {

	@Override
	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {
		getPathmap().put("/student", Student.class);
		getPathmap().put("/enrolment", Enrolment.class);
		return super.handleRequest(req, res, parentElement);
	}
}