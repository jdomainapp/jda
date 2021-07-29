package jda.modules.common.timemanager;

import java.util.HashMap;
import java.util.Map;

/**
 * @overview Manages one or more timers and provide an API for extracting time accounting information of each timer.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2c
 */
public class TimeManager {

  /***
   * a set of pairs (timerName: String, task: {@link TimeTask})
   */
  private static Map<String, TimeTask> timers = new HashMap<>();
  
  /**
   * @effects 
   *  If a timer named <tt>timerName</tt> has not been started
   *    record the start time of <tt>timerName</tt> in this
   *  else
   *    do nothing
   */
  public static void start(String timerName) {
    if (timerName == null) return;
    
    if (!timers.containsKey(timerName)) {
      // not yet started
      TimeTask tt = new TimeTask(timerName);
      timers.put(timerName, tt);
    }
  }

  /**
   * @effects 
   *  If a timer named <tt>timerName</tt> has been started by {@link #start(String)}
   *    stop it and 
   *    return the time duration (i.e <tt>stopTime - startTime</tt>) of <tt>timerName</tt>
   *    which is recorded in this.
   *  else
   *    do nothing
   */
  public static void stop(String timerName) {
    if (timerName == null || !timers.containsKey(timerName)) return;
    
    TimeTask tt = timers.get(timerName);
    
    tt.stop();
  }
  
  /**
   * @modifies this
   * @effects 
   *  If a timer named <tt>timerName</tt> has been stoped by this (i.e. has been stoped by {@link #stop(String)})
   *    return the time duration (i.e <tt>stopTime - startTime</tt>) of <tt>timerName</tt>, normalised 
   *    using the specified <tt>unit</tt>.
   *    Remove the timer from this. 
   *  else
   *    return -1
   */
  public static double popTime(String timerName, TimeUnit unit) {
    if (timerName == null || unit == null || !timers.containsKey(timerName)) return -1;
     
    TimeTask tt = timers.remove(timerName);
    
    return tt.duration(unit);
  }

  /**
   * @modifies this
   * @effects 
   *  generate and return a user-friendly tabular-valued string containing the time durations for 
   *  the timers in the task domain <tt>domainName</tt>. 
   *  The timer names are specified by <tt>timerNames</tt>. 
   *  The time values are computed using <tt>unit</tt> and are 
   *  formated using <tt>timeValFormat</tt>. 
   *  
   *  <p>The timers specified by <tt>timerNames</tt> are removed from this.
   */
  public static String popAndFormatTimes(String domainName,
      TimeUnit unit, String timeValFormat, String...timerNames) {
    StringBuilder ts = new StringBuilder("Time accounting for: "+ domainName + "\n");
    
    if (unit == null)
      unit = TimeUnit.Microsecond;
    
    if (timeValFormat == null)
      timeValFormat = "%.2f";
    
    for (String tn : timerNames) {
      double td = TimeManager.popTime(tn, unit);
      String dur;
      if (td < 0) {
        dur = "N/A";
      } else {
        dur = String.format(timeValFormat, td);
      }
      ts.append("  " + tn + ": " + dur + " ("+unit.simpleName()+"(s))\n");
    }
    
    return ts.toString();
  }
}
