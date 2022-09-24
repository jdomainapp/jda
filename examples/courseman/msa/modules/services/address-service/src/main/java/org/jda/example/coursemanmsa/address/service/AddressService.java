package org.jda.example.coursemanmsa.address.service;

import java.util.List;
import java.util.Optional;
import org.jda.example.coursemanmsa.address.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.address.model.Address;
import org.jda.example.coursemanmsa.address.repository.AddressRepository;
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
    

    public Address createEntity(Address arg0){
    	arg0 = repository.save(arg0);
    	simpleSourceBean.publishChange(ActionEnum.CREATED.name(),arg0.getId());
    	return arg0;
    }
    
    public Page<Address> getEntityListByPage(Pageable arg0) {
    	return repository.findAll(arg0);
    }
    

    public Address getEntityById(int arg0) {
    	Optional<Address> opt = repository.findById(arg0);
        return (opt.isPresent()) ? opt.get() : null;
    }

    public Address updateEntity(int arg0, Address arg1) {
    	repository.save(arg1);
    	simpleSourceBean.publishChange(ActionEnum.UPDATED.name(),arg0);
    	return arg1;
    }

    public void deleteEntityById(int arg0) {
    	repository.deleteById(arg0);
    	simpleSourceBean.publishChange(ActionEnum.DELETED.name(),arg0);
    }

    public List<Address> getAllEntities() {
        return (List<Address>) repository.findAll();
    }
}