package jda.test.app.domainapp.setup.config;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;


public class PrintDataDB extends TestSetUpConfig {
  
  @Test
  public void doTest2() throws DataSourceException { instance.printDataDB(); }
  
}
