package org.jda.example.coursemanmsa.coursemgnt.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.controller.ControllerTk;
import org.jda.example.coursemanmsa.common.controller.RedirectController;
import org.jda.example.coursemanmsa.common.controller.RedirectControllerRegistry;
import org.jda.example.coursemanmsa.common.events.model.ChangeModel;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.jda.example.coursemanmsa.coursemgnt.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jda.modules.dodm.dsm.DSM;

@RestController
@RequestMapping(value = "/")
public class CourseMgntController {

	@Autowired
	SimpleSourceBean sourceBean;

	/**
	 * ducmle: renamed to avoid path matching error: public final static String
	 * PATH_COURSEMODULEMGNT="/coursemodulemgnt/"; public final static String
	 * PATH_STUDENTENROLMENT="/studentenrolment/";
	 */
	public final static String PATH_COURSEMGNT = "/coursemgnt";
	public final static String PATH_COURSEMODULEMGNT = "/cmodulemgnt";
	public final static String PATH_STUDENTENROLMENT = "/stenrolment";

	@RequestMapping(value = PATH_COURSEMODULEMGNT + "/**")
	public ResponseEntity handleCourseModuleMgnt(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("cmodulemgnt");

		MyResponseEntity myResponseEntiy = controller.handleRequest(req, res, PATH_COURSEMODULEMGNT);
		ChangeModel changeModel = myResponseEntiy.getChangeModel();
		/**
		 * TODO: can we move the following id update of ChangeModel to MyResponseEntity,
		 * when the ResponseEntity is set ?
		 */
		if (changeModel != null) {
			String kafkaPath = ControllerTk.getServiceUri(req, PATH_COURSEMGNT) + "/{id}";
//					"http://gateway-server/coursemgnt-service/"+req.getServletPath()+"/{id}";
			changeModel.setPath(kafkaPath);
			sourceBean.publishChange(changeModel);
		}
		return myResponseEntiy.getResponseEntity();
	}

	@RequestMapping(value = PATH_STUDENTENROLMENT + "/**")
	public ResponseEntity handleStudentEnrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("stenrolment");
		MyResponseEntity myResponseEntiy = controller.handleRequest(req, res, PATH_STUDENTENROLMENT);
		ChangeModel changeModel = myResponseEntiy.getChangeModel();
		if (changeModel != null) {
			String kafkaPath = ControllerTk.getServiceUri(req, PATH_COURSEMGNT) + "/{id}";
//					"http://gateway-server/coursemgnt-service/"+req.getServletPath()+"/{id}";
			changeModel.setPath(kafkaPath);
			sourceBean.publishChange(changeModel);
		}
		return myResponseEntiy.getResponseEntity();
	}
}
