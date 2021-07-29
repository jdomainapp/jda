package jda.test.app.courseman.enhanced.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.enhanced.CourseManEnhancedTester;


public class LoadObjects extends CourseManEnhancedTester {  
  
  @Test
  public void doTest() throws DataSourceException { instance.loadObjects(); }
  }
