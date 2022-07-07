package org.jda.example.coursemanmsa.coursemgnt.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.controller.RedirectController;
import org.jda.example.coursemanmsa.common.controller.RedirectControllerRegistry;
import org.jda.example.coursemanmsa.common.events.model.ChangeModel;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.jda.example.coursemanmsa.coursemgnt.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.coursemgnt.modules.coursemodule.model.Coursemodule;
import org.jda.example.coursemanmsa.coursemgnt.modules.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.coursemgnt.modules.student.model.Student;
import org.jda.example.coursemanmsa.coursemgnt.modules.teacher.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/")
public class CourseMgntController {
	
	@Autowired
	SimpleSourceBean sourceBean;
	
	public final static String PATH_COURSEMODULEMGNT="/coursemodulemgnt/";
	public final static String PATH_STUDENTENROLMENT="/studentenrolment/";
	
	@RequestMapping(value = PATH_COURSEMODULEMGNT+"**")
	public ResponseEntity handleCoursemodulemgnt(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("coursemodulemgnt");

		MyResponseEntity myResposeEntiy =  controller.handleRequest(req, res, PATH_COURSEMODULEMGNT);
		ChangeModel changeModel = myResposeEntiy.getChangeModel();
		if(changeModel!=null) {
			if(changeModel.getId()==null) {
				Object result = myResposeEntiy.getResponseEntity().getBody();
				if(result instanceof Coursemodule) {
					int id = ((Coursemodule) result).getId();
					changeModel.setId(id);
				}else if(result instanceof Teacher) {
					int id = ((Teacher) result).getId();
					changeModel.setId(id);
				}
			}
			sourceBean.publishChange(changeModel);
		}
		return myResposeEntiy.getResponseEntity();
	}
	
	@RequestMapping(value = PATH_STUDENTENROLMENT+"**")
	public ResponseEntity handleStudentenrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("studentenrolment");
		MyResponseEntity myResposeEntiy =  controller.handleRequest(req, res, PATH_STUDENTENROLMENT);
		ChangeModel changeModel = myResposeEntiy.getChangeModel();
		if(changeModel!=null) {
			if(changeModel.getId()==null) {
				Object result = myResposeEntiy.getResponseEntity().getBody();
				if(result instanceof Student) {
					String id = ((Student) result).getId();
					changeModel.setId(id);
				}else if(result instanceof Enrolment) {
					int id = ((Enrolment) result).getId();
					changeModel.setId(id);
				}
			}
			sourceBean.publishChange(changeModel);
		}
		return myResposeEntiy.getResponseEntity();
	}
}
