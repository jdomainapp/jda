package org.jda.example.coursemanmsa.academicadmin.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jda.modules.msacommon.controller.ControllerRegistry;
import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.controller.ModuleReconfigurable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping(value="/")
public class AcademicAdminController implements ModuleReconfigurable {

	private static final Logger logger = LoggerFactory.getLogger(AcademicAdminController.class);

	@Autowired
	RestTemplate restTemplate;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	ServletContext context;

//	private final ControllerRegistry ctrlRegistry =
//			ControllerRegistry.getInstance();

	@Value("${spring.gateway.server}")
	private String gatewayServer;

	public final static String PATH_ASSESSMENTHUB="/assessmenthub";
	public final static String PATH_COURSEMGNT="/coursemgnt";
	public final static String PATH_ADRESS="/address";
	public final static String PATH_CLASS="/class";

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public ControllerRegistry getControllerRegistry() {
		return ControllerRegistry.getInstance();
	}

	@RequestMapping(value = PATH_ASSESSMENTHUB+"/**")
	public ResponseEntity handleAssessment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req); 
		String requestData = ControllerTk.getRequestData(req);
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
	
	@RequestMapping(value = PATH_COURSEMGNT+"/**")
	@CircuitBreaker(name = "courseManagement", fallbackMethod = "buildFallbackCourse")
	@Retry(name = "retryCallCourse", fallbackMethod = "buildFallbackCourse")
	@Bulkhead(name = "bulkheadCourseService", type= Type.SEMAPHORE, fallbackMethod = "buildFallbackCourse")
	public ResponseEntity<?> handleCourseManagement(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req); 
		String requestData = ControllerTk.getRequestData(req); 
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
	
	private ResponseEntity<?> buildFallbackCourse(HttpServletRequest req, HttpServletResponse res, Throwable t){
		String error = "Failed to invoke fall-back: CourseManagement";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
/* ducmle: replaced by handleAddressService
	@RequestMapping(value = PATH_ADRESS+"/**")
	public ResponseEntity handleAddress(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(gatewayServer, req);
		String requestData = ControllerTk.getRequestData(req);
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}*/
	
/*
ducmle: commented out because it is conflict with coursemgnt-controller
@RequestMapping(value = PATH_CLASS+"/**")
	public ResponseEntity handleClass(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req); 
		String requestData = ControllerTk.getRequestData(req);
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}*/

	/**
	 * @effects
	 *	invoked when the <tt>handleCourseMgmtService</tt> path mapping is requested. This path mapping was registered programmatically by {@link #registerChildService(String)}
	 *
	 * @version 1.0
	 */
  public ResponseEntity handleCourseMgmtService(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(gatewayServer, req);
		String requestData = ControllerTk.getRequestData(req);
		ResponseEntity response = ControllerTk.invokeService(restTemplate, path, req.getMethod(), requestData);
		return response;
  }

	/**
	 * @effects
	 *	invoked when the <tt>handleAddressService</tt> path mapping is requested. This path mapping was registered programmatically by {@link #registerChildService(String)}
	 *
	 * @version 1.0
	 */
	public ResponseEntity handleAddressService(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(gatewayServer, req);
		String requestData = ControllerTk.getRequestData(req);
		ResponseEntity response = ControllerTk.invokeService(restTemplate, path, req.getMethod(), requestData);
		return response;
	}
}
