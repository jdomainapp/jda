package jda.test.util.datetime;

import java.util.Calendar;
import java.util.Date;

import jda.modules.common.Toolkit;

public class DateCountInPeriod {
  public static void main(String[] args) {
    Date d1 = Toolkit.getDateZeroTime(14, 9, 2015);
    
    Date d2 = Toolkit.getDateZeroTime(30, 9, 2015);
    
    int dayLabel = Calendar.SUNDAY;
    
    int count = Toolkit.dateCountInPeriod(d1, d2, dayLabel);
    
    System.out.printf("Date range: [%s, %s]%n  count(%d) = %d%n", d1, d2, dayLabel, count);
  }
}
