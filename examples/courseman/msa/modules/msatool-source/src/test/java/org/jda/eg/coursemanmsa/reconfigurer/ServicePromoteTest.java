package org.jda.eg.coursemanmsa.reconfigurer;

import java.io.FileNotFoundException;

import org.junit.Test;
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
	  new ServicePromoteTest().promoteCModuleMgmt();
	}

  /**
   * @requires 
   *  source and target services are running.
   *  
   * @effects 
   *  sends a RESTful request to the source, requesting it to "promote" module "cmodulemgnt" to become a child service at the target.
   */
	@Test
  private void promoteCModuleMgmt()  throws FileNotFoundException {
	  // prepare request data
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("databaseSchema", new FileSystemResource(ResourceUtils.getFile("classpath:cmodulemgnt.sql")));
    
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    
    HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
    
    /** 
     * execute a RESTful request (using the request data) to for module-promotion:
     *  - source: http://localhost:8098 (represented by this project)
     *  - target: http://localhost:8099 (represented by the ...target project)
     */
    String fullSourseURL = "http://localhost:8098/promote/cmodulemgnt";
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(fullSourseURL)
                .queryParam("targetHost", "http://localhost:8099/");
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> restExchange = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, requestEntity,
                        String.class, "cmodulemgnt");
    
    System.out.printf("Result %s", restExchange.getStatusCode().name());    
  }
}
