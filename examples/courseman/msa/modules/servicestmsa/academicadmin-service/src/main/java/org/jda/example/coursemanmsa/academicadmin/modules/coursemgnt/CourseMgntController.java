package org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt;

import jda.modules.msacommon.connections.UserContext;
import jda.modules.msacommon.controller.*;
import jda.modules.msacommon.events.model.ChangeModel;
import org.jda.example.coursemanmsa.academicadmin.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.academicadmin.modules.coursemgnt.modules.studentenrolment.modules.studentclass.model.StudentClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@Controller
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
	/* ducmle: commented out because it is conflict with academic-admin-controller.PATH_ADDRESS
	public final static String PATH_ADRESS="/address";
	*/
	public final static String PATH_CLASS="/class";

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
	

	/*
	ducmle: commented out because it is conflict with academic-admin-controller.PATH_ADDRESS

	@RequestMapping(value = PATH_ADRESS + "/**")
	public ResponseEntity<?> handleAddress(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DefaultController<Address, Integer> controller = ControllerRegistry.getInstance().get(Address.class);
		return controller != null ? controller.handleRequest(req, res)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}*/
	
	@RequestMapping(value = PATH_CLASS + "/**")
	public ResponseEntity<?> handleClass(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DefaultController<StudentClass, Integer> controller = ControllerRegistry.getInstance().get(StudentClass.class);
		return controller != null ? controller.handleRequest(req, res)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 * Add a (module) serivce to path
	 *//*
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
	}*/
}
