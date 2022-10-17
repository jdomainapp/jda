package org.jda.example.coursemanmsa.common.controller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.dodm.dsm.DSMBasic;

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
  public static String getServiceUri(HttpServletRequest req) {
    // e.g "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "")
    final String gwUri = "http://gateway-server";
    String serviceUriPattern = "%s/%s-service%s";
//    String reqPath = req.getServletPath().replace(serviceName+"/", "");
    String fullRequestPath = req.getServletPath().substring(1);
    String serviceName="";
    String reqPath="";
    if(fullRequestPath.contains("/")) {
    	serviceName = fullRequestPath.substring(0, fullRequestPath.indexOf("/"));
    	reqPath = fullRequestPath.substring(fullRequestPath.indexOf("/"));
    }else {
    	serviceName=fullRequestPath;
    }
    String serviceUri = String.format(serviceUriPattern, gwUri,serviceName, reqPath);
    return serviceUri;
  }

  public static String getServiceUri(HttpServletRequest req, String serviceName) {
	    // e.g "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/", "")
	    final String gwUri = "http://gateway-server";
//	    String serviceUriPattern = "%s%s-service/%s";
//	    String reqPath = req.getServletPath().replace(serviceName+"/", "");
	    String serviceUriPattern = "%s/%s%s";
	    String reqPath = req.getServletPath();
	    String serviceUri = String.format(serviceUriPattern, gwUri,serviceName, reqPath);
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
  
  /**
   * 
   * @effects 
   *  parse <tt>idVal</tt> into a value compatible to ID-typed, 
   *    which is suitable for the domain id field: <tt>cls.idFieldName</tt>.
   *  
   *  <br>Throws NotFoundException if <tt>cls.idFieldName</tt> is not a valid field.
   */
  public static <ID> ID parseDomainId(Class <?> cls, String idFieldName, String idVal) throws NotFoundException {
    // get the id field
    Field idField = DClassTk.getField(cls, idFieldName, true);
    
    if (idField == null)
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_ID_NOT_FOUND, new Object[] {idFieldName, cls.getSimpleName()});
    // convert idVal to idField's type
    Object val = DClassTk.convertToTypeValue(idVal, idField.getType());
		
    // cast to OD
    return (ID) val;
    //return (ID) Integer.getInteger(idVal);
	}
  

  public static String getFullURL(HttpServletRequest request) {
	    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
	    String queryString = request.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}
}
