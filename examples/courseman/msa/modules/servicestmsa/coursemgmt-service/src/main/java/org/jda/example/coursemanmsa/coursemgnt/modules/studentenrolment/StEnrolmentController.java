package org.jda.example.coursemanmsa.coursemgnt.modules.studentenrolment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.controller.RedirectController;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.springframework.stereotype.Controller;

@Controller
public class StEnrolmentController<ID> extends RedirectController<ID> {

	@Override
	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {
		getPathmap().put("/student", Student.class);
		getPathmap().put("/enrolment", Enrolment.class);
		return super.handleRequest(req, res, parentElement);
	}
}