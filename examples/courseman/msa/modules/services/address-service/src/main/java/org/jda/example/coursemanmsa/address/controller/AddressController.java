package org.jda.example.coursemanmsa.address.controller;

import org.jda.example.coursemanmsa.address.model.Address;
import org.jda.example.coursemanmsa.address.service.AddressService;
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
@RequestMapping(value="v1/address")
public class AddressController {
    @Autowired
    private AddressService service;
   
    @PostMapping()
    public ResponseEntity<Address> createEntity(@RequestBody Address arg0) {
    	return ResponseEntity.ok(service.createEntity(arg0));
    }

    @GetMapping()
    public ResponseEntity<Page<Address>> getEntityListByPage(Pageable arg0) {
        return ResponseEntity.ok(service.getEntityListByPage(arg0));
    }
    
    @GetMapping(value = "/{id}")
    public ResponseEntity<Address> getEntityById(@PathVariable("id") int arg0) {
    	return ResponseEntity.ok(service.getEntityById(arg0));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Address> updateEntity(@PathVariable("id") int arg0, @RequestBody Address arg1) {
    	return ResponseEntity.ok(service.updateEntity(arg0, arg1));
    }

    @DeleteMapping(value = "/{id}")
    public void deleteEntityById(@PathVariable("id") int arg0) {
        service.deleteEntityById(arg0);
    }

}
