package jda.modules.common.test.timemanager;

import org.junit.Test;

import jda.modules.common.timemanager.TimeManager;
import jda.modules.common.timemanager.TimeUnit;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TimeManagerTest {
  @Test
  public void run() {
    String domainName = "TestDomain";
    String task1 = "task1", task2 = "task2";
    
    TimeManager.start(task1);
    task1();
    TimeManager.stop(task1);
    
    TimeManager.start(task2);
    task2();
    TimeManager.stop(task2);
    
    String ts = TimeManager.popAndFormatTimes(domainName, 
        // TimeUnit.Microsecond, "%.2f",  // in microsecs 
        TimeUnit.Second, "%.1f",  // in secs
        task1, task2);
    System.out.println(ts);
    
    // again (to test that tasks have been removed)
    System.out.println("Has time accounting been removed?\n");
    
    ts = TimeManager.popAndFormatTimes(domainName, TimeUnit.Microsecond, "%.2f", task1, task2);
    System.out.println(ts);

  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private static void task1() {
    // do something
    System.out.println("Executing task 1...");
    try {
      Thread.sleep(2500);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }    
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  private static void task2() {
    System.out.println("Executing task 2...");

    // do something else
    try {
      Thread.sleep(1200);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
