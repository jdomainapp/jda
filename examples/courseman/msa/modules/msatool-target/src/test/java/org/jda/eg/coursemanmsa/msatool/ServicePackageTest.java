package org.jda.eg.coursemanmsa.msatool;

import java.io.File;

import org.jda.example.coursemanmsa.common.msatool.ServiceReconfigurer;

public class ServicePackageTest {

	public static void main(String[] args) {
		File projectFile = new File("/home/vietdo/Ha/JDA/Git/jda/examples/courseman/msa/modules/servicestmsa/example-service");
		ServiceReconfigurer sr= new ServiceReconfigurer();
		boolean result = sr.packageServiceToJar(projectFile);
		
		System.out.printf("Packaging service to jar file: %s%n  result: %b%n", projectFile.getPath(), result);	
	}
}
