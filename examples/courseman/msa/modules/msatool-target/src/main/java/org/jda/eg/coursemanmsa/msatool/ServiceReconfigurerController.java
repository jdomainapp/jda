package org.jda.eg.coursemanmsa.msatool;

import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ServiceReconfigurerController {

	@Autowired
	RestTemplate restTemplate;

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
}
