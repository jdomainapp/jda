package jda.test.security.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.security.TestMainSecurity;


public class PrintDataDB extends TestMainSecurity {
  
  @Test
  public void doTest2() throws DataSourceException { instance.printDataDB(); }
  
}
