package jda.test.security.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.security.TestMainSecurity;

public class DeleteObjects extends TestMainSecurity {

  @Test
  public void doTest() throws DataSourceException {
    instance.deleteObjects();
  }
}
