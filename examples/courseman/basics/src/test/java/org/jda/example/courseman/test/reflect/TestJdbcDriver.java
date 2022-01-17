package org.jda.example.courseman.test.reflect;

import org.junit.Test;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TestJdbcDriver {
  private String test;
  
  @Test
  public void test() throws ClassNotFoundException {
    // the auto-load driver of Derby
    String driverName = "org.apache.derby.iapi.jdbc.AutoloadedDriver";
    Class.forName(driverName);
  }
}
