package org.jda.eg.coursemanmsa.msatool;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jda.example.coursemanmsa.common.msatool.ServiceReconfigurer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ServiceTransformTest {

	public static void main(String[] args) {
		String contextFilePath = "/home/vietdo/Ha/JDA/Git/jda/examples/courseman/msa/modules/msatool-source/src/main/resources/serviceInfo";
		Path path = Paths.get("/home/vietdo/Ha/JDA/Git/jda/examples/courseman/msa/modules/msatool-source/src/main/resources/cmodulemgnt.sql");
		String name = "data.sql";
		String originalFileName = "cmodulemgnt.sql";
		String contentType = "text/plain";
		byte[] content = null;
		try {
		    content = Files.readAllBytes(path);
		} catch (final IOException e) {
		}
		MultipartFile file = new MockMultipartFile(name,
		                     originalFileName, contentType, content);
		
		ServiceReconfigurer sr= new ServiceReconfigurer();
		String servicePath = sr.transformToService(contextFilePath, file);
		
		System.out.printf("Transform module to service: %s%n  result: %s%n", contextFilePath, servicePath);	
	}
}
