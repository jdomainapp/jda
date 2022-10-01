package org.jda.example.coursemanmsa.academicadmin.controller;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.controller.ControllerTk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.servlet.ControllerEndpointHandlerMapping;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

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
	
	@RequestMapping(value = PATH_ASSESSMENTHUB+"/**")
	public ResponseEntity handleAssessment(HttpServletRequest req, HttpServletResponse res) throws IOException {
	  // ducmle: to generalise
		String path = ControllerTk.getServiceUri(req); 
//		    "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "");
		String requestData = ControllerTk.getRequestData(req);
		    //req.getReader().lines().collect(Collectors.joining()).trim();
		
		//ducmle: renamed
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
	
	@RequestMapping(value = PATH_COURSEMGNT+"/**")
	public ResponseEntity handleCourseManagement(HttpServletRequest req, HttpServletResponse res) throws IOException {
    // ducmle: to generalise
		String path = ControllerTk.getServiceUri(req); 
//		     "http://gateway-server/coursemgnt-service/"+req.getServletPath().replace("/coursemgnt/", "");
		String requestData = ControllerTk.getRequestData(req); 
		    //req.getReader().lines().collect(Collectors.joining()).trim();
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
  
  /**
   * Add a (module) serivce to path
   */
  @RequestMapping(value="registerchild/{serviceName}")
  public ResponseEntity registerChild(@PathVariable("serviceName") String serviceName){
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
		return ResponseEntity.status(HttpStatus.METHOD_FAILURE).body("Error when adding child serivce to parent");
	}
	return  ResponseEntity.status(HttpStatus.OK).body("Sucess");
  }
  
  public ResponseEntity handleModuleService(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req); 
		String requestData = ControllerTk.getRequestData(req); 
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
  }

}
