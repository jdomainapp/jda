package org.jda.example.coursemanmsa.student.service.client;

import org.jda.example.coursemanmsa.student.model.Address;
import org.jda.example.coursemanmsa.student.service.AddressService;
import org.jda.example.coursemanmsa.student.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AddressRestTemplateClient {

	@Autowired
	private AddressService service;

	@Autowired
	RestTemplate restTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(AddressRestTemplateClient.class);

	public Address getData(int id) {
		logger.debug("In StudentService.getAddress: {}", UserContext.getCorrelationId());
		Address obj = checkDatabase(id);
		if(obj != null) {
			return obj;
		}
		obj = getDataByREST(id);
		if(obj !=null) {
			service.createEntity(obj);
		}
		return obj;
	}

	private Address checkDatabase(int id) {
		try {
			Address obj = service.getEntityById(id);
			return obj;
		} catch (Exception ex) {
			logger.error("Error encountered while trying to retrieve address {} check database. Exception {}", id, ex);
			return null;
		}
	}
	
	public Address getDataByREST(int id) {
		ResponseEntity<Address> restExchange = restTemplate.exchange(
				"http://gateway-server/address-service/v1/address/{id}", HttpMethod.GET, null, Address.class,id);

		return restExchange.getBody();
	}
}
