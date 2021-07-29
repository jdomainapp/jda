package jda.test.modules.tool;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.setup.model.SetUpConfigBasic;


public class PrintDBConfiguration extends TestDomainAppTool {
  
  @Test
  public void doTest2() throws DataSourceException {
    Class[] cfgClasses = getConfigurationSchema();
    instance.printDataDB(cfgClasses); 
  }
  
}
