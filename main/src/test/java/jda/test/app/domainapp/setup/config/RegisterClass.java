package jda.test.app.domainapp.setup.config;

import org.junit.Test;


public class RegisterClass extends TestSetUpConfig {  
  
  @Test
  public void doTest() throws Exception { 
    ((TestSetUpConfig)instance).registerConfigurationSchema();
  }
}
