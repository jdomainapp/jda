package org.jda.eg.coursemanmsa.msatool;

public class ServiceTransformTest {

	public static void main(String[] args) {
		String contextFilePath = "/home/vietdo/Ha/JDA/Git/jda/examples/courseman/msa/modules/msatool/src/main/resources/serviceInfo";
		ServiceReconfigurer sr= new ServiceReconfigurer();
		boolean result = sr.transformToService(contextFilePath);
		
		System.out.printf("Transform module to service: %s%n  result: %b%n", contextFilePath, result);	
	}
}
