package jda.test.security.units;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.security.TestMainSecurity;


public class CreateObjects extends TestMainSecurity {
  
  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
    
  @Test
  public void doTest() throws DataSourceException { instance.createObjects(); }  
}
