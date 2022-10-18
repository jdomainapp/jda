package org.jda.eg.coursemanmsa.msatool;

import java.io.FileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class ServicePromoteTest {

	public static void main(String[] args) throws FileNotFoundException {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("databaseSchema", new FileSystemResource(ResourceUtils.getFile("classpath:cmodulemgnt.sql")));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		String fullTargerURL = "http://localhost:8098/promote/cmodulemgnt";
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(fullTargerURL)
                .queryParam("targetHost", "http://localhost:8098/");
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> restExchange = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity,
		                    String.class, "cmodulemgnt");
		System.out.printf("Result %s", restExchange.getStatusCode().name());
	}
}
