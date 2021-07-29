package jda.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import jda.modules.common.datetime.ShortDayLabel;


public class Toolkit {
  /**
   * @modifies {@link #sharedCalendarInstance}
   * @effects 
   *  return a {@link Collection} of {@link LabelledValue} of the form <tt>(d,l)</tt>
   *  where <tt>d = day number, l = day label</tt> for each day of <tt>monthOfYear</tt>
   * @example
   *  <pre>monthOfYear = Date("06/2015")
   *  -> result = {LabelledValue(1,"H"), LabelledValue(1,"B"),...,LabelledValue(30,"B")}
   *  
   *  ("H" = "Hai", "B" = "Ba"/"Bảy", ...)
   *   </pre>
   * @requires 
   *  {@link #sharedCalendarInstance} is not being modified
   *   
   */
  public static Collection<LabelledValue> getLabelledDaysOfMonth(
      Class<? extends ShortDayLabel> shortDayLabelCls,
      Date monthOfYear) {
    Collection<LabelledValue> dls = new ArrayList();
    
    Calendar cal = jda.modules.common.Toolkit.getSharedCalendarInstance(); //Calendar.getInstance();

    // set calendar to the specified month
    cal.setTime(monthOfYear);
    
    int minDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
    int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    
    LabelledValue dl;
    String label;
    
    // rewind calendar to first day of month
    cal.set(Calendar.DAY_OF_MONTH, minDay);
    int dayOfWeek;
    for (int i = minDay; i <= maxDay; i++) {
      dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
      label = jda.modules.common.Toolkit.getShortDayLabelFor(shortDayLabelCls, dayOfWeek);
      dl = new LabelledValue(i+"", label);
      dls.add(dl);
      
      // next day
      cal.add(Calendar.DAY_OF_MONTH, 1);
    }
    
    return dls;
  }
}
