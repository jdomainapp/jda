package jda.modules.msacommon.controller;

import com.google.gson.Gson;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.common.javac.JavaC;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.msacommon.events.model.ChangeModel;
import jda.modules.msacommon.messaging.kafka.IPublishSource;
import jda.modules.msacommon.messaging.kafka.KafkaChangeAction;
import org.apache.commons.io.FileUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @overview Implement shared features for controllers.
 * 
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class ControllerTk {
	private ControllerTk() {
	}

	/**
	 * @effects return the full service URI that would be used to invoke the service
	 *          via the gateway
	 * 
	 */
	public static String getServiceUri(HttpServletRequest req) {
		// e.g
		// "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/",
		// "")
		// ducmle: invoke a more generic method
		return getServiceUri("http://gateway-server", req);
		/**
		final String gwUri = "http://gateway-server";
		// ducmle:
		// String serviceUriPattern = "%s/%s-service%s";
		String serviceUriPattern = "%s/%s%s";

//    String reqPath = req.getServletPath().replace(serviceName+"/", "");
		String fullRequestPath = req.getServletPath().substring(1);
		String serviceName = "";
		String reqPath = "";
		if (fullRequestPath.contains("/")) {
			serviceName = fullRequestPath.substring(0, fullRequestPath.indexOf("/"));
			reqPath = fullRequestPath.substring(fullRequestPath.indexOf("/"));
		} else {
			serviceName = fullRequestPath;
		}

		// ducmle:
		if (!serviceName.endsWith("-service")) {
			serviceName = serviceName + "-service";
		}

		String serviceUri = String.format(serviceUriPattern, gwUri, serviceName, reqPath);
		return serviceUri;
		 */
	}

	public static String getServiceUri(String gwUri, HttpServletRequest req) {
		// e.g
		// "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/",
		// "")
		// ducmle:
		// String serviceUriPattern = "%s/%s-service%s";
		String serviceUriPattern = "%s/%s%s";

//    String reqPath = req.getServletPath().replace(serviceName+"/", "");
		String fullRequestPath = req.getServletPath().substring(1);
		String serviceName = "";
		String reqPath = "";
		if (fullRequestPath.contains("/")) {
			serviceName = fullRequestPath.substring(0, fullRequestPath.indexOf("/"));
			reqPath = fullRequestPath.substring(fullRequestPath.indexOf("/"));
		} else {
			serviceName = fullRequestPath;
		}

		// ducmle:
		if (!serviceName.endsWith("-service")) {
			serviceName = serviceName + "-service";
		}

		String serviceUri = String.format(serviceUriPattern, gwUri, serviceName, reqPath);
		return serviceUri;
	}

	public static String getServiceUri(HttpServletRequest req, String serviceName) {
		// e.g
		// "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/",
		// "")
		final String gwUri = "http://gateway-server";
//	    String serviceUriPattern = "%s%s-service/%s";
//	    String reqPath = req.getServletPath().replace(serviceName+"/", "");
		String serviceUriPattern = "%s/%s%s";
		String reqPath = req.getServletPath();
		String serviceUri = String.format(serviceUriPattern, gwUri, serviceName, reqPath);
		return serviceUri;
	}

	public static String getServiceUri(String serviceName, String module) {
		// e.g
		// "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/",
		// "")
		final String gwUri = "http://gateway-server";
		String serviceUriPattern = "%s/%s%s";
		String serviceUri = String.format(serviceUriPattern, gwUri, serviceName, module);
		return serviceUri;
	}

	public static String getServicePath(String gatewayUri, String...pathElements) {
		// e.g
		// "http://gateway-server/assessmenthub-service/"+req.getServletPath().replace("/assessmenthub/",
		// "")
		final StringBuilder serviceUri = new StringBuilder(gatewayUri);
		for (String e : pathElements) {
			serviceUri.append("/").append(e);
		}
		return serviceUri.toString();
	}

	/**
	 *
	 * @effects
	 *	create and return {@link File} object representing the source or target deployment directory path for <tt>deployedArtifactName</tt>. Whether or not it is source or target is specified in <tt>sourceOrTarget</tt>.
	 *
	 * @version 1.0
	 */
	public static File getDeployPath(Environment env, String deployedArtifactName, boolean sourceOrTarget) {
		String propName = sourceOrTarget ? "path.service.deploySource" : "path.service.deployTarget";
		String serviceDeployPath = env.getProperty(propName);
		File dservDeployPath = new File(serviceDeployPath, deployedArtifactName);

		return dservDeployPath;
	}

	/**
	 * 
	 * @effects return the String representation of the request data of <tt>req</tt>
	 *          or throws IOException if failed to get the request data.
	 */
	public static String getRequestData(HttpServletRequest req) throws IOException {
		return req.getReader().lines().collect(Collectors.joining()).trim();
	}

	public static ResponseEntity invokeService(RestTemplate restTemplate, String path, HttpMethod method, String body) {
		ResponseEntity restExchange = restTemplate.exchange(path, method,
				body == null ? null : new HttpEntity<String>(body), String.class);
		return restExchange;
	}
	
	public static ResponseEntity invokeService(RestTemplate restTemplate, String path, Object requestEntity) {
		ResponseEntity restExchange = restTemplate.postForEntity(path, requestEntity, String.class);
		return restExchange;
	}

	public static ResponseEntity invokeService(RestTemplate restTemplate, String path, String method, String body) {
/* ducmle:
		ResponseEntity restExchange = restTemplate.exchange(path, HttpMethod.valueOf(method),
				body == null ? null : new HttpEntity<String>(body), String.class);
*/
		try {
			ResponseEntity restExchange = restTemplate.exchange(path, HttpMethod.valueOf(method),
					(body == null) ? null : new HttpEntity<String>(body), String.class);

			return restExchange;
		} catch (Exception e) {
			// should not happen: error
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(String.format("%s: %s%nUrl: %s; Method: %s%n%s",
							e.getClass().getSimpleName(), e.getMessage(),
							path, method,
							ToolkitIO.getStackTrace(e, null)
					));
		}
	}

	/**
	 * 
	 * @effects parse <tt>idVal</tt> into a value compatible to ID-typed, which is
	 *          suitable for the domain id field: <tt>cls.idFieldName</tt>.
	 * 
	 *          <br>
	 *          Throws NotFoundException if <tt>cls.idFieldName</tt> is not a valid
	 *          field.
	 */
	public static <ID> ID parseDomainId(Class<?> cls, String idFieldName, String idVal) throws NotFoundException {
		// get the id field
		Field idField = DClassTk.getField(cls, idFieldName, true);

		if (idField == null)
			throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_ID_NOT_FOUND,
					new Object[] { idFieldName, cls.getSimpleName() });
		// convert idVal to idField's type
		Object val = DClassTk.convertToTypeValue(idVal, idField.getType());

		// cast to OD
		try {
			return (ID) val;
		}catch (Exception e) {
			return null;
		}
		
		// return (ID) Integer.getInteger(idVal);
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

	public static List<Integer> findIntegers(String path) {
		List<Integer> ids = new ArrayList<>();
		String pathAfterRemovingChar = path.replaceAll("[^0-9]+", " ");
		for (String i : pathAfterRemovingChar.trim().split(" ")) {
			if (i.isEmpty()) {
				continue;
			}
			ids.add(Integer.parseInt(i));
		}
		return ids;
	}

	public static int getLastIdInPath(String path) {
		List<Integer> ids = findIntegers(path);
		return ids.isEmpty() ? 0 : ids.get(ids.size() - 1);
	}

	public static boolean isPathContainModuleAndId(String moduleName, String fullPath) {
		return fullPath.matches(".*" + moduleName + "\\/\\d+");
	}
	
	public static boolean isPathFindAll(String path) {
		return path.lastIndexOf("/") == 0;
	}

	public static boolean isPathContainModule(String moduleName, String fullPath) {
		return fullPath.matches(".*" + moduleName + "(\\/[a-zA-z]*\\/\\d+)*");
	}

	public static boolean checkParentChildService(String moduleName, String childModule, String fullPath) {
		String pattern = ".*"+moduleName+ "(\\/[a-zA-z]*\\/\\d+)*("+childModule+"(\\/[a-zA-z]*\\/\\d+)*)*";
		return fullPath.matches(pattern);
	}

	public static String getPropertyNameInPath(String path) {
		String pathRemoveId = path.substring(0, path.lastIndexOf("/"));
		return pathRemoveId.substring(pathRemoveId.lastIndexOf("/")+1);
	}
	
	public static String getPropertyValueInPath(String path) {
		return path.substring(path.lastIndexOf("/")+1);
	}

	public static void sendKafka(IPublishSource sourceBean, ResponseEntity<?> responseEntity, ChangeModel change, String requestMethod) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object id = null;
		String action = null;
		if(responseEntity.getStatusCodeValue() != 200) {
			return;
		}
		if (requestMethod.equals(RequestMethod.POST.toString())) {
			Method getIdMethod = responseEntity.getBody().getClass().getMethod("getId");
			id = getIdMethod.invoke(responseEntity.getBody(), null);
			action = KafkaChangeAction.CREATED;
		} else if (requestMethod.equals(RequestMethod.PUT.toString())) {
			Method getIdMethod = responseEntity.getBody().getClass().getMethod("getId");
			id = getIdMethod.invoke(responseEntity.getBody(), null);
			action = KafkaChangeAction.UPDATED;
		} else if (requestMethod.equals(RequestMethod.DELETE.toString())) {
			id=responseEntity.getBody();
			action = KafkaChangeAction.DELETED;
		}else {
			return;
		}
		
		change.setId(id);
		change.setAction(action);
		sourceBean.publishChange(change);
	}
	
	public static Method findMethodInClass(Class<?> clazz, String findMethod) {
		Method[] methods = clazz.getMethods();
		 
	    for (Method method: methods) {
	    	
	    	if(method.getName().equalsIgnoreCase(findMethod)) {
	    		 try {
	    			 return method;
				} catch (Exception e) {
					return null;
				}
	    	}
	    }
	    return null;
	}
	
	
	public static boolean isIntegerNumeric(String strNum) {
	    if (strNum == null) {
	        return false;
	    }
	    try {
	        int d = Integer.parseInt(strNum);
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	    return true;
	}
	
	public static void saveFile(MultipartFile fileUpload, String filePath) {
		try {
			FileUtils.copyInputStreamToFile(fileUpload.getInputStream(), new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String convertObjectToJSON(Object object) {
		Gson gson = new Gson();

		return gson.toJson(object);
	}

	/**
	 *
	 * @effects
	 *	create and return a new DefaultController instance, using the default constructor.
	 */
  public static DefaultController<?,?> newControllerInstance(Class<DefaultController<?,?>> controllerCls)  throws NotPossibleException {
		return DClassTk.createObjectDefault(controllerCls);
  }

	/**
	 * @requires
	 * 	<tt>moduleJarFile</tt> contains the standard JDA module and was created using the command
	 * 	<tt>mvn clean package</tt>.
	 *
	 * @effects
	 *	extracts from the specified <tt>moduleJarFile</tt>, representing a JDA module,
	 *	the Java classes that make up the module, loads these classes into the JRE and returns
	 *  the controller class (typed {@link DefaultController}) of the module.
	 *
	 *  <p>Throws {@link NotPossibleException} if failed.
	 *
	 * @example A JDA module for the domain class <tt>Address</tt> has the following structure:
	 * <pre>
	 *   + model:
	 *   	- Address.java
	 *   + repository:
	 *   	- AddressRepository.java
	 *   - AddressController.java
	 * </pre>
	 * @version 1.0
	 */
	public static Class<DefaultController<?,?>> loadModuleFromJarFile(ClassLoader loader, File moduleJarFile) throws NotPossibleException, NotFoundException {
		// extract jar file
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File extractedFolder = ToolkitIO.extractJarFile(moduleJarFile, tempDir);

		// load all classes
		Map<String, Class<?>> loadedClasses = JavaC.loadClasses(extractedFolder);

		Class<DefaultController<?,?>> controllerCls = null;
		for (Map.Entry<String,Class<?>> e: loadedClasses.entrySet()) {
//				System.out.printf("... loaded: %s -> %s)%n", e.getKey(), e.getValue());
			//String clsName = e.getKey();
			Class cls = e.getValue();
			String clsName = cls.getSimpleName();
			if (DefaultController.class.isAssignableFrom(cls)){
				// controller class
				controllerCls = cls;
				break;
			}
		}

		// return DefaultController class of the module
		return controllerCls;
	}

	/**
	 * Requires: org.apache.commons.commons-text
	 * @effects
	 *	return the standard Camel case text for <tt>text</tt> that has <tt>delimiters[]</tt> as
	 *  its word delimiter(s).
	 *
	 * @version 1.0
	 */
	public static String toCamelCase(String text, char... delimiters) {
		boolean upperCamel = false;
		// FIXME: cannot use CaseUtils.toCamelCase here because text has no delimiters!
		// return CaseUtils.toCamelCase(text, upperCamel, delimiters);
		// assume text is already in CamelCase
		return text;
	}

	/**
	 * Requires: org.apache.commons.commons-text
	 * @effects
	 *	return the standard Camel case text for <tt>text</tt>
	 * @version 1.0
	 */
	public static String toCamelCase(String text) {
		if (text.contains("-service")) {
			text = text.replace("-service", "Service");
		}

		char defDelim = '\u0000';
		return toCamelCase(text, defDelim);
	}
}