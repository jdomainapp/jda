package org.jda.eg.coursemanmsa.msatool;

import java.io.File;

import org.junit.Test;

import jda.modules.msacommon.msatool.ServiceReconfigurer;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ServiceRunTest {
  @Test
  public void test() {
    File jarFile = new File(
        "/home/vietdo/Ha/JDA/Git/jda/examples/courseman/msa/modules/servicestmsa/example-service/target/cmodulemgnt-service-0.0.1-SNAPSHOT.jar");
    
    ServiceReconfigurer sr = new ServiceReconfigurer();
    
    boolean result = sr.runServiceFromJar(jarFile);
    
    System.out.printf("Executing service jar file: %s%n  result: %b%n", jarFile.getPath(), result);
    
  }
  
  public static void main(String[] args) {
	  ServiceRunTest t = new ServiceRunTest();
	  t.test();
  }
}
