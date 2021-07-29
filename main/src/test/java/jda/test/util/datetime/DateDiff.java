package jda.test.util.datetime;

import java.util.Date;

import jda.modules.common.Toolkit;

public class DateDiff {
  public static void main(String[] args) {
    Date d1 = Toolkit.getDateZeroTime(1, 9, 2015);
    
    Date d2 = Toolkit.getDateZeroTime(1, 10, 2015);
    
    int diff = Toolkit.dateDiff(d1, d2);
    
    System.out.printf("Date 1: %s%nDate 2: %s%n  Diff: %d%n", d1, d2, diff);
  }
}
