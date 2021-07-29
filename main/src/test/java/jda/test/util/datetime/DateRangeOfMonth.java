package jda.test.util.datetime;

import java.util.Date;

import jda.modules.common.Toolkit;

public class DateRangeOfMonth {
  public static void main(String[] args) {
    Date month = Toolkit.getDateZeroTime(5, 10, 2015);
    
    Date first = Toolkit.getFirstDayOfMonth(month);
    Date last = Toolkit.getLastDayOfMonth(month);
    
    System.out.printf("Month: %s%n  first day: %s%n  last day: %s%n", month, first, last);
  }
}
