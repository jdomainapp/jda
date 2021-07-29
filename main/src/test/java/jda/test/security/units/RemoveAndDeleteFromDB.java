package jda.test.security.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.security.TestMainSecurity;


public class RemoveAndDeleteFromDB extends TestMainSecurity {
  
  @Test 
  public void doTest() throws DataSourceException { instance.removeClassAndDeleteFromDB(); }
}
