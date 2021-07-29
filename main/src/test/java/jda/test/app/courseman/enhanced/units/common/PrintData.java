package jda.test.app.courseman.enhanced.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.enhanced.CourseManEnhancedTester;


public class PrintData extends CourseManEnhancedTester {
  
  @Test
  public void doTest1() throws DataSourceException { instance.printDataMemory(); }
  
  @Test
  public void doTest2() throws DataSourceException { instance.printDataDB(); }
  
}
