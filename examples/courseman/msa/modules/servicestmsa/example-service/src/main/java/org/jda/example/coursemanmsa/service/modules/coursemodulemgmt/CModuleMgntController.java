package org.jda.example.coursemanmsa.service.modules.coursemodulemgmt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.controller.ControllerTk;
import org.jda.example.coursemanmsa.common.controller.RedirectController;
import org.jda.example.coursemanmsa.common.events.model.ChangeModel;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.jda.example.coursemanmsa.service.events.source.ServiceSourceBean;
import org.jda.example.coursemanmsa.service.modules.coursemodule.model.CourseModule;
import org.jda.example.coursemanmsa.service.modules.teacher.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**ducmle: renamed to match path update */
@Controller
public class CModuleMgntController<ID> extends RedirectController<ID>{
	@Autowired
	ServiceSourceBean sourceBean;
	
	 @Value("${spring.application.name}")
	 private String serviceName;
	 
	 /**
	  * Added method in origin module to catch request
	  */
	 @RequestMapping(value = "/**")
	 public ResponseEntity prehandleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {
		 return handleRequest(req, res, "").getResponseEntity();
	 }
	
	@Override
	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {
		getPathmap().put("/teacher", Teacher.class);
		getPathmap().put("/coursemodule", CourseModule.class);
		
		MyResponseEntity myResponseEntiy = super.handleRequest(req, res, parentElement);
		
		ChangeModel changeModel = myResponseEntiy.getChangeModel();
		/**
		 * TODO: can we move the following id update of ChangeModel to MyResponseEntity,
		 * when the ResponseEntity is set ?
		 */
		if (changeModel != null) {
			String kafkaPath = ControllerTk.getServiceUri(req, serviceName) + "/{id}";
//					"http://gateway-server/coursemgnt-service/"+req.getServletPath()+"/{id}";
			changeModel.setPath(kafkaPath);
			sourceBean.publishChange(changeModel);
		}
		
		return myResponseEntiy;
	}
}