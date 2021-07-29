package jda.test.util.datetime;

import java.util.Calendar;
import java.util.Date;

import jda.modules.common.Toolkit;
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dodm.DODMToolkit;

public class DateCountInMonth {
  public static void main(String[] args) {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MONTH, 1);
    
    Date monthOfYear = cal.getTime();
    
    int num = Toolkit.dateCountInMonth(monthOfYear);
    
    System.out.printf("Month %s has %d (days)", 
        DODMToolkit.dateToMonthString(monthOfYear, Format.MonthOfYear), num);
  }
}
