package org.jda.example.coursemanmsa.studentclass.controller;

import org.jda.example.coursemanmsa.studentclass.model.StudentClass;
import org.jda.example.coursemanmsa.studentclass.service.ClassService;
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
@RequestMapping(value="v1/class")
public class ClassController {
    @Autowired
    private ClassService service;

    @PostMapping
    public ResponseEntity<StudentClass> createEntity(@RequestBody StudentClass obj) {
    	return ResponseEntity.ok(service.createEntity(obj));
    }
    
    @GetMapping()
    public ResponseEntity<Page> getEntityListByPage(Pageable arg0) {
        return ResponseEntity.ok(service.getEntityListByPage(arg0));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<StudentClass> getEntityById(@PathVariable("id") int arg0) {
        return ResponseEntity.ok(service.getEntityById(arg0));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<StudentClass> updateEntity(@PathVariable("id") int arg0, @RequestBody StudentClass arg1) {
        return ResponseEntity.ok(service.updateEntity(arg0, arg1));
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEntityById(@PathVariable("id") int arg0) {
    	service.deleteEntityById(arg0);
    }

}
