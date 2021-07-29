package jda.test.util.datetime;

import java.util.Calendar;
import java.util.Date;

import jda.modules.common.Toolkit;
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dodm.DODMToolkit;

public class DateCountSaturdaysInMonth {
  public static void main(String[] args) {
    Calendar cal = Calendar.getInstance();
    //cal.set(Calendar.MONTH, 9);
    
    Date monthOfYear = cal.getTime();
    
    int dayLabel = Calendar.SATURDAY;

    int num = Toolkit.dateCountInMonth(monthOfYear, dayLabel);
    
    System.out.printf("Month %s has %d (saturdays)", 
        DODMToolkit.dateToMonthString(monthOfYear, Format.MonthOfYear), num);
  }
}
