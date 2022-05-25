package org.jda.example.coursemanmsa.student.service.client;

import org.jda.example.coursemanmsa.student.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AddressRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;

    public Address getAddress(String addressId){
        ResponseEntity<Address> restExchange =
                restTemplate.exchange(
                        "http://gateway-server/address-service/v1/address/{addressId}",
                        HttpMethod.GET,
                        null, Address.class, addressId);

        return restExchange.getBody();
    }
}
