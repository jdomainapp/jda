package org.jda.example.coursemanmsa.student.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.student.model.Address;
import org.jda.example.coursemanmsa.student.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AddressService {
	
	
    @Autowired
    private AddressRepository repository;

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    
    public Address createEntity(Address arg0){
    	arg0 = repository.save(arg0);
    	return arg0;
    }

    public Address getEntityById(int arg0) {
    	Optional<Address> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Address updateEntity(int arg0, Address arg1) {
    	repository.save(arg1);
    	return arg1;
    }

    public void deleteEntityById(int arg0) {
    	repository.deleteById(arg0);
    }
}