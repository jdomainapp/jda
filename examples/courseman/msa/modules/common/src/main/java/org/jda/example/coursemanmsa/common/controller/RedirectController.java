package org.jda.example.coursemanmsa.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.springframework.http.ResponseEntity;

public abstract class RedirectController {
	
	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPattern) {
		return new MyResponseEntity(ResponseEntity.ok("Call child class to process request"), null);
	}
}