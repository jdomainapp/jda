package org.jda.example.coursemanmsa.academicadmin.controller;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.bulkhead.annotation.Bulkhead.Type;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jda.modules.msacommon.controller.ControllerTk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

@RestController
@RequestMapping(value="/")
public class AcademicAdminController {
	
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	ServletContext context; 
	
	public final static String PATH_ASSESSMENTHUB="/assessmenthub";
	public final static String PATH_COURSEMGNT="/coursemgnt";
	public final static String PATH_ADRESS="/address";
	public final static String PATH_CLASS="/class";
	
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
		String error = "Ko goi duoc  CourseManagement";
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
	}
	
	@RequestMapping(value = PATH_ADRESS+"/**")
	public ResponseEntity handleAddress(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req); 
		String requestData = ControllerTk.getRequestData(req);
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
	
	@RequestMapping(value = PATH_CLASS+"/**")
	public ResponseEntity handleClass(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req); 
		String requestData = ControllerTk.getRequestData(req);
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
  
  /**
   * Add a (module) serivce to path
   */
  @RequestMapping(value="registerChildService")
  public ResponseEntity registerChildService(@RequestPart("childName") String serviceName){
	  String servicePath = "/"+serviceName+"/**";
	  RequestMappingInfo mappingInfo = RequestMappingInfo.paths(servicePath).build();
	  Method handleMethod;
		try {
			Class[] methodArgs = new Class[2];
			methodArgs[0]= HttpServletRequest.class;
			methodArgs[1]= HttpServletResponse.class;
			handleMethod = AcademicAdminController.class.getDeclaredMethod("handleModuleService", methodArgs);

			RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
					.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
			requestMappingHandlerMapping.registerMapping(mappingInfo, this, handleMethod);
		} catch (SecurityException | NoSuchMethodException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.METHOD_FAILURE).body("Error when adding child service to parent");
		}
		return  ResponseEntity.status(HttpStatus.OK).body("Success");
  }
  
  public ResponseEntity handleModuleService(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req); 
		String requestData = ControllerTk.getRequestData(req); 
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
  }

}
