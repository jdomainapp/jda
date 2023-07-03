package org.jda.example.coursemanmsa.studentclass.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.studentclass.events.source.SimpleSourceBean;
import org.jda.example.coursemanmsa.studentclass.modules.model.StudentClass;
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
public class ClassMgntController {
	public final static String PATH = "/class";
	
	@Autowired
	SimpleSourceBean sourceBean;

	@RequestMapping(value = PATH + "/**")
	public ResponseEntity<?> handleRequest(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DefaultController<StudentClass, Integer> controller = ControllerRegistry.getInstance().get(StudentClass.class);
		ResponseEntity<?>  responseEntity = controller.handleRequest(req, res);
		String requestMethod = req.getMethod();
		String kafkaPath = ControllerTk.getServiceUri(req, PATH+"-service") + "/id/{id}";
		String typeName = controller.getGenericType().getTypeName();
		ChangeModel change = new ChangeModel (typeName, null, null, kafkaPath, UserContext.getCorrelationId());
		ControllerTk.sendKafka(sourceBean, responseEntity, change, requestMethod);
		
		return responseEntity;
	}
	
	
}