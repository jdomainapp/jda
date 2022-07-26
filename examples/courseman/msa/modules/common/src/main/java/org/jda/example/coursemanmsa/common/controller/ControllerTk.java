package org.jda.example.coursemanmsa.common.controller;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @overview 
 *  Implement shared features for controllers.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ControllerTk {
  private ControllerTk() {}
  
  /**
   * @effects 
   *  return the full service URI that would be used to invoke the service via the gateway 
   *  
   */
  public static String getServiceUri(HttpServletRequest req, String serviceName) {
    // e.g "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "")
    final String gwUri = "http://gateway-server";
    String serviceUriPattern = "%s/%s-service/%s";
    String serviceUri = String.format(serviceUriPattern, gwUri, 
        req.getServletPath().replace("/"+serviceName+"/", ""));
    return serviceUri;
  }
  
  /**
   * 
   * @effects 
   *  return the String representation of the request data of <tt>req</tt> 
   *  or throws IOException if failed to get the request data.
   */
  public static String getRequestData(HttpServletRequest req) throws IOException {
    return req.getReader().lines().collect(Collectors.joining()).trim();
  }
  
  public static ResponseEntity invokeService(RestTemplate restTemplate, String path, String method, String body) {
		ResponseEntity restExchange = restTemplate.exchange(path, 
		    HttpMethod.resolve(method), new HttpEntity<String>(body), String.class);
		return restExchange;
	}
}
