package jda.modules.msacommon.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public abstract class InterfaceController<T, ID> extends DefaultController<T, ID>  {
	private String targetPath;

	@Override
	public ResponseEntity<T> getEntityById(ID id) {
		return forwardRequest(targetPath, HttpMethod.GET, "id="+id);
	}
	
	public ResponseEntity<T> forwardRequest(String targetPath, HttpMethod method, String requestData) {
		
		return ControllerTk.invokeService(restTemplate, targetPath, method, requestData);
	}
	
	public ResponseEntity<T> forwardRequest(String targetPath, Object requestEntity) {
		
		return ControllerTk.invokeService(restTemplate, targetPath, requestEntity);
	}
	
}