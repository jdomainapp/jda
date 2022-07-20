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

import jda.modules.dodm.dsm.DSM;

@RestController
@RequestMapping(value="/")
public class CourseMgntController {
	
	@Autowired
	SimpleSourceBean sourceBean;
	
	/** ducmle: renamed to avoid path matching error:
	public final static String PATH_COURSEMODULEMGNT="/coursemodulemgnt/";
	public final static String PATH_STUDENTENROLMENT="/studentenrolment/";
	*/
  public final static String PATH_COURSEMODULEMGNT="/cmodulemgnt/";
  public final static String PATH_STUDENTENROLMENT="/stenrolment/";
	
	@RequestMapping(value = PATH_COURSEMODULEMGNT+"**")
	public ResponseEntity handleCourseModuleMgnt(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("cmodulemgnt");

		MyResponseEntity myResposeEntiy =  controller.handleRequest(req, res, PATH_COURSEMODULEMGNT);
		ChangeModel changeModel = myResposeEntiy.getChangeModel();
		if(changeModel!=null) {
			if(changeModel.getId()==null) {
        Object result = myResposeEntiy.getResponseEntity().getBody();
			  /* ducmle: use generic code
				if(result instanceof Coursemodule) {
					int id = ((Coursemodule) result).getId();
					changeModel.setId(id);
				}else if(result instanceof Teacher) {
					int id = ((Teacher) result).getId();
					changeModel.setId(id);
				}
				*/
			  int id = DSM.doGetterMethod(result.getClass(), result, "id", Integer.class);
        changeModel.setId(id);
			}
			sourceBean.publishChange(changeModel);
		}
		return myResposeEntiy.getResponseEntity();
	}
	
	@RequestMapping(value = PATH_STUDENTENROLMENT+"**")
	public ResponseEntity handleStudentEnrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("stenrolment");
		MyResponseEntity myResposeEntiy =  controller.handleRequest(req, res, PATH_STUDENTENROLMENT);
		ChangeModel changeModel = myResposeEntiy.getChangeModel();
		if(changeModel!=null) {
			if(changeModel.getId()==null) {
				Object result = myResposeEntiy.getResponseEntity().getBody();
        /* ducmle: use generic code
         * if(result instanceof Student) { String id = ((Student)
         * result).getId(); changeModel.setId(id); }else if(result instanceof
         * Enrolment) { int id = ((Enrolment) result).getId();
         * changeModel.setId(id); }
         */
				int id = DSM.doGetterMethod(result.getClass(), result, "id", Integer.class);
        changeModel.setId(id);
			}
			sourceBean.publishChange(changeModel);
		}
		return myResposeEntiy.getResponseEntity();
	}
}
