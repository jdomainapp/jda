package org.jda.example.coursemanmsa.enrolment.controller;

import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.enrolment.model.Enrolment;
import org.jda.example.coursemanmsa.enrolment.service.EnrollmentService;
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
@RequestMapping(value="v1/enrolment/")
public class EnrolmentController {

	@Autowired
	private EnrollmentService service;
    
    @PostMapping()
    public ResponseEntity<Enrolment> createEntity(@RequestBody Enrolment arg0) {
    	return ResponseEntity.ok(service.createEntity(arg0));
    }

    @GetMapping()
    public ResponseEntity<Page<Enrolment>> getEntityListByPage(Pageable arg0) {
        return ResponseEntity.ok(service.getEntityListByPage(arg0));
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<Enrolment> getEntityById(int arg0) throws TimeoutException{
        return ResponseEntity.ok(service.getEntityById(arg0));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Enrolment> updateEntity(@PathVariable("id") int arg0, @RequestBody Enrolment arg1) {
    	return ResponseEntity.ok(service.updateEntity(arg0, arg1));
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEntityById(@PathVariable("id") int arg0) {
        service.deleteEntityById(arg0);
    }

}
