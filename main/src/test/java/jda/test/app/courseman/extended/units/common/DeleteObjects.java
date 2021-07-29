package jda.test.app.courseman.extended.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.extended.CourseManExtendedTester;

public class DeleteObjects extends CourseManExtendedTester {

  @Test
  public void doTest() throws DataSourceException {
    instance.deleteObjects();
  }
}
