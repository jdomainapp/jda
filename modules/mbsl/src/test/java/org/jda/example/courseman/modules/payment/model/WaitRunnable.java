package org.jda.example.courseman.modules.payment.model;

import java.util.Random;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class WaitRunnable implements Runnable {

  private int minTime;
  private int maxTime;
  private Random randTimeRange;
 
  /**
   * 
   * @effects 
   *  initialise this with a random time range <tt>[minTime, maxTime]</tt>
   *  (both <tt>minTime, maxTime</tt> are in seconds)
   */
  public WaitRunnable(int minTime, int maxTime) {
    this.minTime = minTime;
    this.maxTime = maxTime;
    randTimeRange = new Random();
  }
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public void run() {
    // get a random time between minTime and maxTime
    int time = (minTime + randTimeRange.nextInt(maxTime-minTime)) * 1000;
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      // ignore
    }
  }
} /** end {@link WaitRunnable} */