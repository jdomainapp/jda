package org.jda.example.coursemanmsa.academicadmin.utils.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

public abstract class RedirectController {
	
	public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPattern) {
		return ResponseEntity.ok("Call child class to process request");
	}
}
