package jda.test.app.courseman.extended.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.extended.CourseManExtendedTester;


public class PrintDataDB extends CourseManExtendedTester {
  
  @Test
  public void doTest2() throws DataSourceException { instance.printDataDB(); }
  
}
