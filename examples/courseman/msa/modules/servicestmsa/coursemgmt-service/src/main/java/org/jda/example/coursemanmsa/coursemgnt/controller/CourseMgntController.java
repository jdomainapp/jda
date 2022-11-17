package org.jda.example.coursemanmsa.coursemgnt.controller;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.coursemgnt.events.source.SimpleSourceBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.controller.RedirectController;
import jda.modules.msacommon.controller.RedirectControllerRegistry;
import jda.modules.msacommon.events.model.ChangeModel;
import jda.modules.msacommon.model.MyResponseEntity;

@RestController
@RequestMapping(value = "/")
public class CourseMgntController {

	@Autowired
	SimpleSourceBean sourceBean;
	
	@Autowired
	private ApplicationContext applicationContext;
	
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
			String kafkaPath = ControllerTk.getServiceUri(req, PATH_COURSEMGNT+"-service") + "/{id}";
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
			String kafkaPath = ControllerTk.getServiceUri(req, PATH_COURSEMGNT+"-service") + "/{id}";
//					"http://gateway-server/coursemgnt-service/"+req.getServletPath()+"/{id}";
			changeModel.setPath(kafkaPath);
			sourceBean.publishChange(changeModel);
		}
		return myResponseEntiy.getResponseEntity();
	}

	/**
	 * Add a (module) serivce to path
	 */
	@RequestMapping(value = "removemodule/{serviceName}")
	public ResponseEntity<?> removeModule(@PathVariable("serviceName") String serviceName) {

		String servicePath = "/" + serviceName + "/**";
		RequestMappingInfo mappingInfo = RequestMappingInfo.paths(servicePath).build();
		try {
			RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
					.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
			requestMappingHandlerMapping.unregisterMapping(mappingInfo);
		} catch (SecurityException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.METHOD_FAILURE).body("Error when removing child serivce to parent");
		}
		return ResponseEntity.ok("Success");
	}
}
