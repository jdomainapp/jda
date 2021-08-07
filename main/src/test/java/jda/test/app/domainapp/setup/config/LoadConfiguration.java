package jda.test.app.domainapp.setup.config;

import java.io.IOException;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;


public class LoadConfiguration extends TestSetUpConfig {  
  
  @Test
  public void doTest() throws DataSourceException, IOException {
    TestSetUpConfig testMain = ((TestSetUpConfig) instance); 
    
    testMain.registerConfigurationSchema();
    
    testMain.loadObjects();
  }
}
