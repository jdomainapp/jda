package org.jda.example.coursemanmsa.course.controller;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.course.model.domain.Compulsorymodule;
import org.jda.example.coursemanmsa.course.model.domain.CourseModule;
import org.jda.example.coursemanmsa.course.model.domain.Electivemodule;
import org.jda.example.coursemanmsa.course.model.view.CoursemoduleView;
import org.jda.example.coursemanmsa.course.service.CompulsoryModuleService;
import org.jda.example.coursemanmsa.course.service.CourseModuleService;
import org.jda.example.coursemanmsa.course.service.ElectiveModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="v1/course")
public class CourseController {
    @Autowired
    private CourseModuleService service;
    
    @Autowired
    private CompulsoryModuleService compulsoryModuleService;
    
    @Autowired
    private ElectiveModuleService electiveModuleService;

    
    @GetMapping()
    public Page getEntityListByPage(Pageable arg0) {
        return service.getEntityListByPage(arg0);
    }

    @GetMapping(value = "/type")
    public Page getEntityListByTypeAndPage(@RequestParam(value = "type", required = false) String arg0, Pageable arg1) { 	
    	return service.getEntityListByTypeAndPage(arg0, arg1);
    }
    
    @PostMapping()
    public ResponseEntity<CourseModule> createEntity(@RequestBody CourseModule arg0) {
    	return ResponseEntity.ok(service.createEntity(arg0));
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CourseModule> getEntityById(@PathVariable("id") int arg0) {
        return ResponseEntity.ok(service.getEntityById(arg0));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CourseModule> updateEntity(@PathVariable("id") int arg0, @RequestBody CourseModule arg1) {
    	return ResponseEntity.ok(service.updateEntity(arg0, arg1));
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEntityById(@PathVariable("id") int arg0) {
        service.deleteEntityById(arg0);
    }
    
    //Return result for academic-service
    @RequestMapping(value="/view/{id}",method = RequestMethod.GET)
    public ResponseEntity<CoursemoduleView> getEntityViewById( @PathVariable("id") int id) throws TimeoutException {
        return ResponseEntity.ok(service.getEntityViewById(id));
    }
    
    @RequestMapping(value="/compulsorymodules",method = RequestMethod.GET)
    public ResponseEntity<List<CourseModule>> getCompulsorymodules(){
        return ResponseEntity.ok(compulsoryModuleService.findAll());
    }
    
    @RequestMapping(value="/compulsorymodule/{id}",method = RequestMethod.GET)
    public ResponseEntity<CourseModule> getCompulsorymodule(@PathVariable("id") int id){
        return ResponseEntity.ok(compulsoryModuleService.getEntityById(id));
    }
    
    @RequestMapping(value="/electivemodules",method = RequestMethod.GET)
    public ResponseEntity<List<CourseModule>> getElectivemodules(){
        return ResponseEntity.ok(electiveModuleService.findAll());
    }
    
    @RequestMapping(value="/electivemodule/{id}",method = RequestMethod.GET)
    public ResponseEntity<CourseModule> getElectivemodule(@PathVariable("id") int id){
        return ResponseEntity.ok(electiveModuleService.getEntityById(id));
    }

}
