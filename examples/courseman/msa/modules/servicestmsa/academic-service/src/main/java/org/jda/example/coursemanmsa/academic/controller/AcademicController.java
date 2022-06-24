package org.jda.example.coursemanmsa.academic.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.academic.service.AcademicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="v1/academic/")
public class AcademicController {


//    @GetMapping()
//    public ResponseEntity<Page<Academic>> getEntityListByPage(Pageable arg0) {
//        return ResponseEntity.ok(service.getEntityListByPage(arg0));
//    }
//    
//    @GetMapping(value = "/{id}")
//    public ResponseEntity<Academic> getEntityById(@PathVariable("id") int arg0) throws TimeoutException{
//        return ResponseEntity.ok(service.getEntityById(arg0));
//    }
//    
//    @GetMapping(value = "/coursemodule/{id}")
//    public ResponseEntity<List<Academic>> getEntityByCoursemoduleId(@PathVariable("id") int arg0) {
//        return ResponseEntity.ok(service.getEntityByCoursemoduleId(arg0));
//    }
//
//    @PutMapping(value = "/{id}")
//    public ResponseEntity<Academic> updateEntity(@PathVariable("id") int arg0, @RequestBody Academic arg1) {
//    	return ResponseEntity.ok(service.updateEntity(arg0, arg1));
//    }

}
