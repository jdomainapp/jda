package jda.modules.common.timemanager;

/**
 * @overview Represents the standard time units: nanosecond, millisecond, second, minute, hour, day, month, year
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2c
 */
public enum TimeUnit {
  Nanosecond,
  Millisecond,
  Microsecond,
  Second,
  Minute, 
  Hour, 
  Day, 
  Month, 
  Year;

  /**
   * @effects 
   *  return the simple (user-friendly) name of this
   */
  public String simpleName() {
    return name().toLowerCase();
  }
}
