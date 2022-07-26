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

	@RequestMapping(value = "/assessmenthub/**")
	public ResponseEntity handleAssessment(HttpServletRequest req, HttpServletResponse res) throws IOException {
	  // ducmle: to generalise
		String path = 
//				Error
//				ControllerTk.getServiceUri(req, "assessmenthub"); 
		    "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "");
		String requestData = ControllerTk.getRequestData(req);
		    //req.getReader().lines().collect(Collectors.joining()).trim();
		
		//ducmle: renamed
		return invokeService(path, req.getMethod(), requestData);
	}
	
  @RequestMapping(value = "/coursemgnt/**")
	public ResponseEntity handleCourseManagement(HttpServletRequest req, HttpServletResponse res) throws IOException {
    // ducmle: to generalise
		String path = 
//				Error
//				ControllerTk.getServiceUri(req, "coursemgnt"); 
		     "http://gateway-server/coursemgnt-service/"+req.getServletPath().replace("/coursemgnt/", "");
		String requestData = ControllerTk.getRequestData(req); 
		    //req.getReader().lines().collect(Collectors.joining()).trim();
		return invokeService(path, req.getMethod(), requestData);
	}
	
	public ResponseEntity invokeService(String path, String method, String body) {
		ResponseEntity restExchange = restTemplate.exchange(path, 
		    HttpMethod.resolve(method), new HttpEntity<String>(body), String.class);
		return restExchange;
	}

}
