package jda.modules.msacommon.msatool;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.msacommon.controller.ControllerTk;
import jda.modules.msacommon.model.ModuleDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

@Controller
public abstract class ServiceReconfigurerController extends ServiceReconfigurerControllerBasic {

	private static final Logger logger = LoggerFactory.getLogger(ServiceReconfigurerController.class);

	@Value("${spring.gateway.server}")
	private String gatewayServer;

	/**
	 * @effects
	 *	handle HTTP-POST request for promoting the specified <tt>module</tt>, which is a descendant of the service <tt>sourceServ</tt>, to become a child of <tt>targetServ</tt>
	 *
	 * @version 1.0
	 */
	@PostMapping(value = "/promote")
	public ResponseEntity<?> promote(@RequestParam String sourceServ,
		@RequestParam String module, @RequestParam String targetServ) {
		
		ModuleDesc md = transform(sourceServ, module);

		try {
			String sr2 = lookUpReconfigurer(sourceServ);

			String servName = initRunService(sr2, targetServ, md);

			promoteCompleted(sourceServ, md.getPid());

			return ResponseEntity.ok(String.format("Module '%s' promoted to service '%s': success", module, servName));
		} catch (NotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		}
	}

	/**
	 * @effects	creates a microservice-project whose code structure is defined based on ModuleDesc object, that represents the serialised details of <tt>module</tt> of the <tt>sourceServ</tt> service.
	 * <br> The structural details include module's id, name and the deployed .jar
	 *
	 * @version 1.0
	 */
	private ModuleDesc transform(String sourceServ, String module) {
		String pid = module;
		String jarFile = transformModuleToDeployableService(sourceServ, module);
		return new ModuleDesc(pid, module, jarFile);
	}

	/**
	 * @effects	creates a microservice-project whose code structure is transformed from the code of the specified <tt>module</tt>. Return the built JAR file of the microservice project, which is the result of executing this Maven command <tt>mvn clean package spring-boot:repackage</tt>
	 *
	 * @version 1.0
	 */
	private String transformModuleToDeployableService(String sourceServ, String module) {
		// let deployServName = lowerCase(module) + "-service"
		String deployServName = getServiceNameFromModule(module);
		File dservDeployPath = getDeployServicePath(deployServName, true);

		// todo: for now assume file deploy.jar exists in the dservDeployPath folder
		// let dm = Folder(module), ds = Folder(sourceServ)
//		File dm = null;
//		File ds = null;
		// let dservices = ds.parent
		// create service dir for deployServ: dserv = dservices.mkdir("deployServName")
//		File dservices = ds.getParentFile();
//		File dserv = new File(dservices.getPath(), deployServName);
//		File dservTarget = new File(dserv.getPath(), "target");
//		dservTarget.mkdirs();

		// copy: dm -> dserv
		// transform dserv.src to become a service
		// create deployable jar in dserv.target using cmd `mvn clean package spring-boot:repackage`

		// let fdeploy = File(dserv.target, deployServName + ".jar")
		// return fdeploy
		String deployServJarName = deployServName + ".jar";
		File fdeploy = new File(dservDeployPath.getPath(), deployServJarName);

		return fdeploy.toString();
	}

	/**
	 * @effects requests the specified service-reconfigurer <tt>targetSR</tt> to create and deploy a service, whose structure is created from the module represented by <tt>md</tt> and that the new service is a child of <tt>targetServ</tt>.
	 *
	 * If succeeded then return the new service name, otherwise return null
	 *
	 * @version 1.0
	 */
	public String initRunService(String targetSR, String targetServ, ModuleDesc md) {
		// send md to targetSR to run service
		HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity = getRunRequest(targetServ, md);
		String pathAction = "runService";
//		String gw = getApplicationContextEnv().getProperty("spring.gateway.server");
		String fullTargetURL = ControllerTk.getServicePath(gatewayServer, targetSR, pathAction);

		ResponseEntity<String> restExchange = restTemplate.exchange(fullTargetURL, HttpMethod.POST, reqEntity, String.class);

		//restExchange.getStatusCode()
		return restExchange.getStatusCode().name();
	}

	@PostMapping(value = "/runService")
	public ResponseEntity<?> runService(
			@RequestPart("targetServ") String targetServ,
			@RequestPart("pid") String pid,
			@RequestPart("module") String module,
			@RequestPart("file") MultipartFile file) {

		// save file to deploy-path
		String deployServName = getServiceNameFromModule(module);
		File deployServicePath = getDeployServicePath(deployServName, false);
		File dservFile = new File(deployServicePath.getPath(), deployServName + ".jar");
		if (!deployServicePath.exists()) {
			deployServicePath.mkdirs();
		} else if (dservFile.exists()) {	// delete existing files
			dservFile.delete();
		}

		try {
			file.transferTo(dservFile);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}

		// execute service
		String serviceUrl = getServiceUrl(deployServName);
		boolean result = executeService(serviceUrl, dservFile);

		if (result) {
			// register service as child of targetServ
			HttpStatus status = registerAsChild(deployServName, targetServ);

			if (status.equals(HttpStatus.OK)) {
				return ResponseEntity.ok("Run service: success");
			} else {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
						String.format("Failed to execute service: %s (%s)", dservFile, status ));
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to execute service: " + dservFile);
		}
	}

	private String getServiceUrl(String serviceName) {
		String serviceUrl = gatewayServer + "/" + serviceName;
		return serviceUrl;
	}

	/**
	 *
	 * @effects
	 *	execute the service contained in <tt>deployJarFile</tt>, using <tt>java -jar</tt>
	 * @version 1.0
	 */
	public boolean executeService(String serviceUrl, File deployJarFile) {
		String cmd = "java -jar " + deployJarFile.getPath();
		File workDir = null;
		Environment env = getApplicationContextEnv();
		String logFileName = env.getProperty("service.shell.logFile");
		File logFile = new File(Objects.requireNonNull(logFileName));

		// create a service monitor function to check service for "Up" after
		// starting it
		Function<Object, Integer> servMonitorFunc = null;
		try {
			String healthCheckUrl = serviceUrl + "/actuator/health";
			int timeOut = 60; //secs
			servMonitorFunc = new ServiceMonitor(healthCheckUrl, timeOut,
					ServiceMonitor.healthContentHandler)
					.getMonitorFunc();
		} catch (URISyntaxException e) {
			// should not happen
		}

		boolean waitFor = false;
		boolean isServiceStared = ToolkitIO.executeSysCommand(cmd, workDir, logFile, waitFor, servMonitorFunc);

		if (isServiceStared)
			logger.info("Service execution: completed");
		else
			logger.info("Service execution: NOT completed (successfully)");

		return isServiceStared;
	}

	/**
	 * @effects Register <tt>deployServName</tt> as the child of <tt>targetServ</tt>.
	 * Return the {@link HttpStatus}.
	 *
	 * @version 1.0
	 */
	private HttpStatus registerAsChild(String deployServName, String targetServ) {
		String targetServUrl = gatewayServer + "/" + targetServ;
		String registerChildPath = targetServUrl + "/registerChildService";

		// send a request to targetServ
		HttpEntity<LinkedMultiValueMap<String, Object>> reqEntity =
				getRequestEntityWithParams("childName", deployServName);

		ResponseEntity<String> restExchange = restTemplate.exchange(registerChildPath, HttpMethod.POST, reqEntity, String.class);

		return restExchange.getStatusCode();
	}

	/**
	 * @effects
	 *	perform post-tasks relating to the promotion of <tt>module</tt>. This includes removing <tt>module</tt> from the service tree of <tt>sourceServ</tt>
	 *
	 * @version 1.0
	 */
	public void promoteCompleted(String sourceServ, String module) {
		// TODO: 14/08/2023
	}

	protected HttpEntity<LinkedMultiValueMap<String, Object>> getRunRequest (String targetServ, ModuleDesc module) {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

		map.add("targetServ", targetServ);
		map.add("pid", module.getPid());
		map.add("module", module.getModule());
		map.add("file", new FileSystemResource(new File(module.getJarFile())));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		return requestEntity;
	}

	/**
	 * A more general version of {@link #getRunRequest(String, ModuleDesc)} that supports arbitrary array of name-value pairs
	 *
	 * @effects
	 *	create and return a {@link HttpEntity} representing the HTTP request, containing parameters
	 * specified by <tt>nameValuePairs</tt>.
	 * @version 1.0
	 */
	protected HttpEntity<LinkedMultiValueMap<String, Object>> getRequestEntityWithParams (String...nameValuePairs) {
		if (nameValuePairs == null || nameValuePairs.length == 0 || (nameValuePairs.length % 2 != 0)) {
			throw new IllegalArgumentException(Arrays.toString(nameValuePairs));
		}

		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

		for (int i = 0; i < nameValuePairs.length; i=i+2) {
			map.add(nameValuePairs[i], nameValuePairs[i+1]);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		return requestEntity;
	}

	private String getServiceNameFromModule(String module) {
		return module.toLowerCase() + "-service";
	}

	private File getDeployServicePath(String deployServName, boolean sourceOrTarget) {
		Environment env = getApplicationContextEnv();
		String propName = sourceOrTarget ? "path.service.deploySource" : "path.service.deployTarget";
		String serviceDeployPath = env.getProperty(propName);
		File dservDeployPath = new File(serviceDeployPath, deployServName);

		return dservDeployPath;
	}

	/**
	 * @effects
	 * 	requests service <tt>sourceServ</tt> for the URL of the <tt>reconfigurer-service</tt> running on its machine,
	 * 	and return the URL as output
	 */
	protected abstract String lookUpReconfigurer(String sourceServ);

	/**
	 * @effects
	 *	return the run-time {@link ApplicationContext} of this
	 * @version 1.0
	 */
	protected abstract Environment getApplicationContextEnv();

}
