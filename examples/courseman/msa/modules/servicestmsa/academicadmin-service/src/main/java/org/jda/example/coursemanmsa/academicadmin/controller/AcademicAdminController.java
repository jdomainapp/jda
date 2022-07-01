package org.jda.example.coursemanmsa.academicadmin.controller;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping(value="/")
public class AcademicAdminController {
	
	@Autowired
	RestTemplate restTemplate;

	@RequestMapping(value = "/assessmenthub/**")
	public ResponseEntity handleCoursemodulemgnt(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "");
		String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
		return getDataByREST(path, req.getMethod(), requestData);
	}
	
	@RequestMapping(value = "/coursemgnt/**")
	public ResponseEntity handleStudentenrolment(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = "http://gateway-server/coursemgnt-service/"+req.getServletPath().replace("/coursemgnt/", "");;
		String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
		return getDataByREST(path, req.getMethod(), requestData);
	}
	
	public ResponseEntity getDataByREST(String path, String method, String body) {
		ResponseEntity restExchange = restTemplate.exchange(path, HttpMethod.resolve(method), new HttpEntity<String>(body), String.class);
		return restExchange;
	}

}
