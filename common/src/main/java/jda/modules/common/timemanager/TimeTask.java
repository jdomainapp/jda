package jda.modules.common.timemanager;

/**
 * @overview Represents a time-counting task, which records the start and stop time of some process. It computes the {@link #duration(TimeUnit)}
 *  normalised by a {@link TimeUnit}.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TimeTask {

  private static final double CR = 1000;    // micro = 10^(-6)
  private static final double CI = CR * 1000; // milli = 10^(-3)
  private static final double CS = CI * 1000; // second
  private static final double CM = CS * 60;
  private static final double CH = CM * 60;
  private static final double CD = CH * 24;
  private static final double CT = CD * 30;
  private static final double CY = CT * 12;
  
  private boolean started;
  private long startTime;
  private boolean stoped;
  private long stopTime;
  private String timerName;

  /**
   * @effects 
   *  initialise this with <tt>timerName</tt> and {@link #start()}
   */
  public TimeTask(String timerName) {
    this.timerName = timerName;
    start();
  }



  /**
   * @effects 
   *  start and record the start time
   */
  public void start() {
    startTime = System.nanoTime(); 
    started = true;
  }
  
  /**
   * @effects 
   *  stop and record the stop time
   */
  public void stop() {
    stopTime = System.nanoTime();
    stoped = true;
  }
  
  /**
   * @effects 
   *  If this has been stoped by {@link #stop()}
   *    return the time duration (i.e <tt>stopTime - startTime</tt>), normalised 
   *    using the specified <tt>unit</tt>. 
   *  else
   *    return -1 
   */
  public double duration(TimeUnit unit) {
    if (!stoped) return -1;
    
    Long duration = stopTime - startTime;
    
    switch (unit) {
      case Nanosecond:
      default:
        return duration.doubleValue();
      case Millisecond:
        return duration.doubleValue() / CI;
      case Microsecond:
        return duration.doubleValue() / CR;
      case Second:
        return duration.doubleValue() / CS;
      case Minute:
        return duration.doubleValue() / CM;
      case Hour:
        return duration.doubleValue() / CH;
      case Day:
        return duration.doubleValue() / CD;
      case Month:
        return duration.doubleValue() / CT;
      case Year:
        return duration.doubleValue() / CY;
    }
  }

}
