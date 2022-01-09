package org.jda.example.coursemanrestful.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @overview A toolkit class for the software.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DToolkit {
  
  public static final Date MIN_DOB = getTime(1,0,1900);

  private DToolkit() {}

  /**
   * @requires 
   * d in [0,31], m in [0,11]
   * 
   * @effects 
   *   return the Date object representing d/m/y.
   *   <br>Note: m starts from 0  
   */
  public static Date getTime(int d, int m, int y) {
    Calendar cal = Calendar.getInstance();
    cal.set(y, m, d);
    return cal.getTime();
  }
}
