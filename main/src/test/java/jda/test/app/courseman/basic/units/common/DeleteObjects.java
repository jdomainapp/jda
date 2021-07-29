package jda.test.app.courseman.basic.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.basic.CourseManBasicTester;


public class DeleteObjects extends CourseManBasicTester {  
  
  @Test
  public void doTest() throws DataSourceException { instance.deleteObjects(); }
  }
