package org.jda.example.coursemanmsa.coursemgnt.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import jda.modules.msacommon.connections.UserContext;
import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.controller.RedirectController;
import jda.modules.msacommon.controller.RedirectControllerRegistry;
import jda.modules.msacommon.events.model.ChangeModel;

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
	public ResponseEntity handleCourseModuleMgnt(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("cmodulemgnt");

		ResponseEntity responseEntity = controller.handleRequest(req, res, PATH_COURSEMODULEMGNT);
		String requestMethod = req.getMethod();
		String kafkaPath = ControllerTk.getServiceUri(req, PATH_COURSEMGNT+"-service") + "/id/{id}";
		String typeName = controller.getDomainClass().getTypeName();
		ChangeModel change = new ChangeModel (typeName, null, null, kafkaPath, UserContext.getCorrelationId());
		ControllerTk.sendKafka(sourceBean, responseEntity, change, requestMethod);
	
		return responseEntity;
	}

	@RequestMapping(value = PATH_STUDENTENROLMENT + "/**")
	public ResponseEntity handleStudentEnrolment(HttpServletRequest req, HttpServletResponse res) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("stenrolment");
		ResponseEntity responseEntity = controller.handleRequest(req, res, PATH_STUDENTENROLMENT);
		String requestMethod = req.getMethod();
		String kafkaPath = ControllerTk.getServiceUri(req, PATH_COURSEMGNT+"-service") + "/id/{id}";
		String typeName = controller.getDomainClass().getTypeName();
		ChangeModel change = new ChangeModel (typeName, null, null, kafkaPath, UserContext.getCorrelationId());
		ControllerTk.sendKafka(sourceBean, responseEntity, change, requestMethod);
	
		
		return responseEntity;
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
