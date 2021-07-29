package jda.modules.common.datetime;

import java.util.Calendar;

/**
 * @overview 
 *  Constants that maps {@link Calendar#DAY_OF_WEEK} (i.e. {@link Calendar#MONDAY}, ..., {@link Calendar#SUNDAY})
 *  to Vietnamese short names for them as follows:
 *  <pre>
 *    {@link Calendar#MONDAY} -> "H"
 *    {@link Calendar#TUESDAY} -> "B"
 *    {@link Calendar#WEDNESDAY} -> "T"
 *    {@link Calendar#THURSDAY} -> "N"
 *    {@link Calendar#FRIDAY} -> "S"
 *    {@link Calendar#SATURDAY} -> "B"
 *    {@link Calendar#SUNDAY} -> "BN"
 *  </pre>
 *  
 * @author dmle
 *
 */
public enum VietnameseKinderGartenShortDayLabel implements ShortDayLabel {
  Monday(Calendar.MONDAY,"H"),
  Tuesday(Calendar.TUESDAY,"B"),
  Wednesday(Calendar.WEDNESDAY,"T"),
  Thursday(Calendar.THURSDAY,"N"),
  Friday(Calendar.FRIDAY,"S"),
  Saturday(Calendar.SATURDAY,"B"),
  Sunday(Calendar.SUNDAY,"BN")
  ;
  
  private int dayOfWeek;
  private String label;
  
  private VietnameseKinderGartenShortDayLabel(int dayOfWeek, String label) {
    this.dayOfWeek = dayOfWeek;
    this.label = label;
  }
  

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public int getDayOfWeek() {
    return dayOfWeek;
  }
}