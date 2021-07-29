package jda.test.security.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.security.TestMainSecurity;


public class PrintDataMemory extends TestMainSecurity {
  
  @Test
  public void doTest1() throws DataSourceException { instance.printDataMemory(); }
}
