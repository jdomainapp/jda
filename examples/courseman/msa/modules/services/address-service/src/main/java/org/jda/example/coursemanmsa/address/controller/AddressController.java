package org.jda.example.coursemanmsa.address.controller;

import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.address.model.Address;
import org.jda.example.coursemanmsa.address.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="v1/address")
public class AddressController {
    @Autowired
    private AddressService service;


    @RequestMapping(value="/{addressId}",method = RequestMethod.GET)
    public ResponseEntity<Address> getAddress( @PathVariable("addressId") int addressId) throws TimeoutException {
        return ResponseEntity.ok(service.findById(addressId));
    }

    @RequestMapping(value="/{addressId}",method = RequestMethod.PUT)
    public void updateAddress( @PathVariable("addressId") int id, @RequestBody Address address) {
        service.update(address);
    }

    @PostMapping
    public ResponseEntity<Address>  saveAddress(@RequestBody Address address) {
    	return ResponseEntity.ok(service.create(address));
    }

    @RequestMapping(value="/{addressId}",method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress( @PathVariable("addressId") int id,  @RequestBody Address address) {
        service.delete(address);
    }

}
