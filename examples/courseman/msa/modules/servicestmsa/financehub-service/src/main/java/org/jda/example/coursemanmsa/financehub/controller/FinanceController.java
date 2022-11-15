package org.jda.example.coursemanmsa.financehub.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import jda.modules.msacommon.controller.ControllerTk;

@RestController
@RequestMapping(value="/")
public class FinanceController {
	
	@Autowired
	RestTemplate restTemplate;
	
	public final static String PATH_ASSESSMENTHUB="assessmenthub";

	@RequestMapping(value = "/"+PATH_ASSESSMENTHUB+"/**")
	public ResponseEntity handleAssessment(HttpServletRequest req, HttpServletResponse res) throws IOException {
	  // ducmle: to generalise
		String path = ControllerTk.getServiceUri(req, PATH_ASSESSMENTHUB); 
//		    "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "");
		String requestData = ControllerTk.getRequestData(req);
		    //req.getReader().lines().collect(Collectors.joining()).trim();
		
		//ducmle: renamed
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
}
