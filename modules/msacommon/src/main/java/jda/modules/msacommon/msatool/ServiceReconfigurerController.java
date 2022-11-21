package jda.modules.msacommon.msatool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import jda.modules.msacommon.msatool.ServiceReconfigurer;

@Controller
public class ServiceReconfigurerController {

	@Value("${removeModuleUri:}")
	 private String removeModuleUri;
	
	@Autowired
	RestTemplate restTemplate;
	
	//1 input target, config file
	@PostMapping(value = "/promote/{module}") 
	public ResponseEntity<?> promote(@RequestPart MultipartFile databaseSchema ,@RequestParam String targetHost, @PathVariable String module) throws FileNotFoundException{
		File inputContextFile = ResourceUtils.getFile("classpath:serviceInfo");
		//1.1
		String serviceDir = ServiceReconfigurer.transformToService(inputContextFile.getPath(), databaseSchema);
		if(serviceDir ==null) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
//		if(!ServiceReconfigurer.packageServiceToJar(new File(serviceDir))) {
//			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build().toString();
//		}

		File[] jarFiles = new File (serviceDir+File.separator+"target").listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".jar");
		    }
		});
		
		if(jarFiles == null || jarFiles.length==0) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}

		String result= sendFile(targetHost, module,jarFiles[0]);
		if(!result.equals(HttpStatus.OK.name())) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
		
		result= runService(targetHost, module, jarFiles[0].getName());
		if(!result.equals(HttpStatus.OK.name())) {
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
		result= promoteCompleted(module);
		if(!result.equals(HttpStatus.OK.name())) {
			return ResponseEntity.ok("Fail to promote a module to service");
		}
		return ResponseEntity.ok("Success to promote a module to service");
	}
	//1.2
	public String sendFile (String targetHost, String module, File jarFile) {
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("file", new FileSystemResource(jarFile));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<LinkedMultiValueMap<String, Object>>(map, headers);
		String fullTargerURL = targetHost+"/receive/"+module;
		ResponseEntity<String> restExchange = restTemplate.exchange(fullTargerURL, HttpMethod.POST, requestEntity,
		                    String.class, module);
		return restExchange.getStatusCode().name();
	}
	
	@PostMapping(value = "/receive/{module}")
	public ResponseEntity<?> receiveFile(@RequestPart("file") MultipartFile file, @PathVariable String module) {

		String fileName = file.getOriginalFilename();
		String receiverLocation = System.getProperty("user.dir") + File.separator + "execute" + File.separator + module;
		File fileFolder = new File(receiverLocation);
		if (!fileFolder.exists()) {
			fileFolder.mkdirs();
		}
		try {
			File jarFile = new File(fileFolder.getPath() + File.separator + fileName);
			file.transferTo(jarFile);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok("Success to receive jar file");

	}
	
	//1.3
	public String runService(String targetHost, String module, String jarFileName) {
		String fullTargerURL = targetHost+"/runService/"+module;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(fullTargerURL)
                .queryParam("jarFileName", jarFileName);
		ResponseEntity<String> restExchange = restTemplate.exchange( uriBuilder.toUriString(), HttpMethod.POST, null, String.class, module);
		return restExchange.getStatusCode().name();
	}
	
	@PostMapping(value = "/runService/{module}")
	public ResponseEntity<?> runService(@RequestParam String jarFileName,@PathVariable String module) {
		
		String jarFilePath = System.getProperty("user.dir") + File.separator + "execute" + File.separator + module
				+ File.separator + jarFileName;
		File jarFile = new File(jarFilePath);

//		boolean isServiceStared = ServiceReconfigurer.runServiceFromJar(jarFile);
//		if (!isServiceStared) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fail to run child service!!")
//					.getBody();
//		}
		
		//1.4
		return ResponseEntity.ok("Succees to run service");
	}
	
	public String promoteCompleted(String module) {
		String removeUri = removeModuleUri+module;
		ResponseEntity<String> restExchange = restTemplate.exchange( removeUri, HttpMethod.POST, null, String.class,module);
		return restExchange.getStatusCode().name();
	}
	
	
}
