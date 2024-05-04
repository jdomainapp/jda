package __outputPackage__.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jda.modules.msacommon.connections.UserContext;
import jda.modules.msacommon.controller.ControllerRegistry;
import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.controller.DefaultController;
import jda.modules.msacommon.controller.RedirectController;
import jda.modules.msacommon.controller.RedirectControllerRegistry;
import jda.modules.msacommon.events.model.ChangeModel;

import __outputPackage__.events.source.SimpleSourceBean;

__foreach(entityModule : entityModules)
import __entityModule.model.outputPackage__.__entityModule.model.Name__;
__endforeach
@RestController
@RequestMapping(value = "/")
public class __model.Name__Controller {

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	SimpleSourceBean sourceBean;

	@Autowired
	private ApplicationContext applicationContext;

	__foreach (service : services)
	@RequestMapping(value = "/__service.name__/**")
	public ResponseEntity<?> handle__service.model.Name__(HttpServletRequest req, HttpServletResponse res) throws IOException {
		String path = ControllerTk.getServiceUri(req);
		String requestData = ControllerTk.getRequestData(req);
		return ControllerTk.invokeService(restTemplate,path, req.getMethod(), requestData);
	}
	__endforeach

	__foreach (coordinatorModule : coordinatorModules)
	@RequestMapping(value = "/__coordinatorModule.name__/**")
	public ResponseEntity handle__coordinatorModule.model.Name__(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		RedirectController controller = RedirectControllerRegistry.getInstance().get("__coordinatorModule.name__");

		ResponseEntity responseEntity = controller.handleRequest(req, res, "/__coordinatorModule.name__");
		String requestMethod = req.getMethod();
		String kafkaPath = ControllerTk.getServiceUri(req, "/__name__") + "/id/{id}";
		String typeName = controller.getDomainClass().getTypeName();
		ChangeModel change = new ChangeModel (typeName, null, null, kafkaPath, UserContext.getCorrelationId());
		ControllerTk.sendKafka(sourceBean, responseEntity, change, requestMethod);

		return responseEntity;
	}
	__endforeach

	__foreach (entityModule : entityModules)
	@RequestMapping(value = "/__entityModule.name__/**")
	public ResponseEntity<?> handleAddress(HttpServletRequest req, HttpServletResponse res) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		DefaultController<__entityModule.model.Name__, Integer> controller = ControllerRegistry.getInstance().get(__entityModule.model.Name__.class);
		return controller != null ? controller.handleRequest(req, res)
				: ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
	__endforeach

	/**
	 * Add a (module) serivce to path
	 */
	@RequestMapping(value = "removemodule/{serviceName}")
	public ResponseEntity<?> removeModule(@PathVariable("serviceName") String serviceName) {

		String servicePath = "/" + serviceName + "/**";
		RequestMappingInfo mappingInfo = RequestMappingInfo.paths(servicePath).build();
		try {
			RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
					.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
			requestMappingHandlerMapping.unregisterMapping(mappingInfo);
		} catch (SecurityException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.METHOD_FAILURE).body("Error when removing child serivce to parent");
		}
		return ResponseEntity.ok("Success");
	}


}