package org.jda.example.coursemanmsa.student.controller;

import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.student.model.Student;
import org.jda.example.coursemanmsa.student.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping(value="v1/student/")
public class StudentController {
	
	private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

	@Autowired
	private StudentService service;
    
    @PostMapping()
    public ResponseEntity<Student> createEntity(@RequestBody Student arg0) {
    	return ResponseEntity.ok(service.createEntity(arg0));
    }

    @GetMapping()
    public ResponseEntity<Page<Student>> getEntityListByPage(Pageable arg0) {
        return ResponseEntity.ok(service.getEntityListByPage(arg0));
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<Student> getEntityById(@PathVariable("id") String arg0) throws TimeoutException{
        return ResponseEntity.ok(service.getEntityById(arg0));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Student> updateEntity(@PathVariable("id") String arg0, @RequestBody Student arg1) {
    	return ResponseEntity.ok(service.updateEntity(arg0, arg1));
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEntityById(@PathVariable("id") String arg0) {
        service.deleteEntityById(arg0);
    }

}
