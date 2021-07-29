package jda.test.app.courseman.basic.units.common;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.app.courseman.basic.CourseManBasicTester;

/**
 * simply prints a spacer line between test units
 * 
 * @author dmle
 *
 */
public class Spacer extends CourseManBasicTester {

  @Test
  public void doTest() throws DataSourceException {
    final int length = 80;
    final char c = '-';
    
    for (int i = 0; i < length; i++) {
      System.out.print(c);
    }
    System.out.println();
  }
}
