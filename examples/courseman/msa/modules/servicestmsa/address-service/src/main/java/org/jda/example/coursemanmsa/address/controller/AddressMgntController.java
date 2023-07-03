package org.jda.example.coursemanmsa.address.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.address.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.address.modules.model.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jda.modules.msacommon.connections.UserContext;
import jda.modules.msacommon.controller.ControllerRegistry;
import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.controller.DefaultController;
import jda.modules.msacommon.events.model.ChangeModel;

@RestController
@RequestMapping(value = "/")
public class AddressMgntController {
	public final static String PATH = "/address";
	
	@Autowired
	SimpleSourceBean sourceBean;

	@RequestMapping(value = PATH + "/**")
	public ResponseEntity<?> handleRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DefaultController<Address, Integer> controller = ControllerRegistry.getInstance().get(Address.class);
		ResponseEntity<?>  responseEntity = controller.handleRequest(req, res);
		String requestMethod = req.getMethod();
		String kafkaPath = ControllerTk.getServiceUri(req, PATH+"-service") + "/id/{id}";
		String typeName = controller.getGenericType().getTypeName();
		ChangeModel change = new ChangeModel (typeName, null, null, kafkaPath, UserContext.getCorrelationId());
		ControllerTk.sendKafka(sourceBean, responseEntity, change, requestMethod);
		
		return responseEntity;
	}
	
	
}