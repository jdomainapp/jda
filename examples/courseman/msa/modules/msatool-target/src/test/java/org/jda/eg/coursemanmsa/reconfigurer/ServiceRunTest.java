package org.jda.eg.coursemanmsa.reconfigurer;

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
        "/data/projects/jda/examples/courseman/msa/modules/services/address-service/target/address-service-0.0.1-SNAPSHOT.jar");
    
    ServiceReconfigurer sr = new ServiceReconfigurer();
    
    boolean result = ServiceReconfigurer.runServiceFromJar(jarFile);
    
    System.out.printf("Executing service jar file: %s%n  result: %b%n", jarFile.getPath(), result);
    
  }
}
