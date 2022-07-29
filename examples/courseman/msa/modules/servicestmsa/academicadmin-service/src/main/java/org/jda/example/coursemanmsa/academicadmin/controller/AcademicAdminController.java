package org.jda.example.coursemanmsa.academicadmin.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.controller.ControllerTk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value="/")
public class AcademicAdminController {
	
	@Autowired
	RestTemplate restTemplate;
	
	public final static String PATH_ASSESSMENTHUB="/assessmenthub";
	public final static String PATH_COURSEMGNT="/coursemgnt";
	
	@RequestMapping(value = PATH_ASSESSMENTHUB+"/**")
	public ResponseEntity handleAssessment(HttpServletRequest req, HttpServletResponse res) throws IOException {
	  // ducmle: to generalise
		String path = ControllerTk.getServiceUri(req, PATH_ASSESSMENTHUB); 
//		    "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "");
		String requestData = ControllerTk.getRequestData(req);
		    //req.getReader().lines().collect(Collectors.joining()).trim();
		
		//ducmle: renamed
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
	
  @RequestMapping(value = PATH_COURSEMGNT+"/**")
	public ResponseEntity handleCourseManagement(HttpServletRequest req, HttpServletResponse res) throws IOException {
    // ducmle: to generalise
		String path = ControllerTk.getServiceUri(req, PATH_COURSEMGNT); 
//		     "http://gateway-server/coursemgnt-service/"+req.getServletPath().replace("/coursemgnt/", "");
		String requestData = ControllerTk.getRequestData(req); 
		    //req.getReader().lines().collect(Collectors.joining()).trim();
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}

}
