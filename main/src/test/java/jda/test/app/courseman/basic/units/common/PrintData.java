package jda.test.app.courseman.basic.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.basic.CourseManBasicTester;


public class PrintData extends CourseManBasicTester {
  
  @Test
  public void doTest1() throws DataSourceException { instance.printDataMemory(); }
  
}
