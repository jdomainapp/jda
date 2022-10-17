package org.jda.eg.coursemanmsa.msatool;

import org.jda.example.coursemanmsa.common.msatool.ServiceReconfigurer;

public class ServiceOnDeploymentCompletedTest {

	public static void main(String[] args) {
		String removeChildPath="http://localhost:8072/academicadmin-service/coursemgnt/removemodule/cmodulemgnt";
		ServiceReconfigurer sr= new ServiceReconfigurer();
		boolean result = sr.onDeploymentCompleted(removeChildPath);
		
		System.out.printf("onDeploymentCompleted: result: %b%n", result);	
	}
}
