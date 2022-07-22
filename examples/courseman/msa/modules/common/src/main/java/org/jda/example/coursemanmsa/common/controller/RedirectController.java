package org.jda.example.coursemanmsa.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.springframework.http.ResponseEntity;

public abstract class RedirectController {
	
	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPattern) {
		return new MyResponseEntity(ResponseEntity.ok("Call child class to process request"), null);
	}
	
  /**
   * @effects 
   *  if {@link #matchDescendantModulePath(String, String)} on <tt>(urlPath, pathElement)</tt> and 
   *  that the last path segment of <tt>urlPath</tt> specifies an object id then
   *    return true
   *  else
   *    return false
   * 
   */
  protected boolean matchDescendantModulePathWithId(String urlPath, String pathElement) {
    // FIXME: fix path matching to correctly implement the behaviour specification (above)
    return urlPath != null && pathElement != null && 
        urlPath.matches("(.*)"+pathElement+"/(.+)");
  }

  /**
   * @effects 
   *  if <tt>pathElement</tt> is found in a subpath of <tt>urlPath</tt> that corresponds to 
   *  a descendant module of this module in the service tree then
   *    return true
   *  else
   *    return false
   */
  protected boolean matchDescendantModulePath(String urlPath, String pathElement) {
    // FIXME: fix path matching to correctly implement the behaviour specification (above)
    return urlPath != null && pathElement != null && 
        urlPath.matches("(.*)"+pathElement+"(.*)");
  }
}
