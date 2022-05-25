package org.jda.example.coursemanmsa.address.service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import org.jda.example.coursemanmsa.address.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.address.model.Address;
import org.jda.example.coursemanmsa.address.repository.AddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
	
	public enum ActionEnum{
		GET,
		CREATED,
		UPDATED,
		DELETED
	}
	
    @Autowired
    private AddressRepository repository;
    
    @Autowired
    SimpleSourceBean simpleSourceBean;

    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    
    public Address findById(int addressId){
    	Optional<Address> opt = repository.findById(addressId);
    	simpleSourceBean.publishAddressChange(ActionEnum.GET.name(),addressId);
        return (opt.isPresent()) ? opt.get() : null;
    }
    

    public Address create(Address address){
    	address = repository.save(address);
    	simpleSourceBean.publishAddressChange(ActionEnum.CREATED.name(),address.getId());
    	return address;
    	
    }
    

    public void update(Address address){
    	repository.save(address);
    	simpleSourceBean.publishAddressChange(ActionEnum.UPDATED.name(),address.getId());
    }

    public void delete(Address address){
    	repository.deleteById(""+address.getId());
    	simpleSourceBean.publishAddressChange(ActionEnum.DELETED.name(),address.getId());
    }
}