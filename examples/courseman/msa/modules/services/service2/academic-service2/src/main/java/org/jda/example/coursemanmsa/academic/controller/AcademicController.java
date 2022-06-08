package org.jda.example.coursemanmsa.academic.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.academic.model.Enrolment;
import org.jda.example.coursemanmsa.academic.service.AcademicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="v1/academic/")
public class AcademicController {

	@Autowired
	private AcademicService service;
    
	@GetMapping(value = "/coursemodule/{id}")
    public ResponseEntity<List<Enrolment>> getEntityList(@PathVariable("id") int arg0) {
		return ResponseEntity.ok(service.getEntityList(arg0));
    }

    @PutMapping(value = "/enrolment/{id}")
    public ResponseEntity<Enrolment> updateEntity(@PathVariable("id") int arg0, @RequestBody Enrolment arg1) {
    	return ResponseEntity.ok(service.updateEntity(arg0, arg1));
    }

}
