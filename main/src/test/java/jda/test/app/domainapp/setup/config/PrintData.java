package jda.test.app.domainapp.setup.config;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;


public class PrintData extends TestSetUpConfig {
  
  @Test
  public void doTest1() throws DataSourceException { 
    instance.printDataMemory(); 
  }
  
}
