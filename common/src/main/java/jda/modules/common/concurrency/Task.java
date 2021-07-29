package jda.modules.common.concurrency;

/**
 * @overview
 *  Represents a {@link Runnable} program task that is to be executed on a <tt>Thread</tt> 
 *  
 * @author dmle
 *
 */
public abstract class Task implements Runnable {
  
  public static enum TaskName {
    UpdateGUI,
    OpenChildren,
    UpdateGUIButtons,
    CreateAssociatedChildObjects, 
    // v3.1
    ChangeGUILanguage, UpdateRegionsOnLanguageChange,
    /**
     * Execute a model that is associated to this
     * @version 4.0*/
    ExecuteDomainModel,
    // for testing
    //Test1,Test2,Test3,Test4,Test5,Test6,Test7,Test8,Test9,Test10
  }

  /**time interval (in milliseconds) to wait for this to stop 
   * (used by {@link #waitUntilStopped(int)}
   * */
  private static final long WAIT_INTERVAL = 500;
  
  private TaskName name;
  
  private boolean isStopped;
  
  public Task(TaskName name) {
    this.name = name;
    isStopped = true; // task is stopped initially
  }
  
  public TaskName getName() {
    return name;
  }
  
  public boolean isStopped() {
    return isStopped;
  }


  protected final void setIsStopped(boolean isStopped) {
    this.isStopped = isStopped;
  }

  /**
   * @param maxCycleTime 
   * @effect
   *  wait until this thread is stopped
   * @version 3.0: added maxWaitTime
   */
  public void waitUntilStopped(final int maxWaitTime) {
    // assumes this has not stopped when invoked
    int waitTime = 0;
    
    do {
      sleep(WAIT_INTERVAL);
      waitTime += WAIT_INTERVAL;
    } while (!isStopped() && waitTime <= maxWaitTime);
  }
  
  public static void sleep(long millisecs) {
    try {
      Thread.sleep(millisecs);
    } catch (InterruptedException e) {
    }
  }
  
  @Override
  public String toString() {
    return "Task ("+ name + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Task other = (Task) obj;
    if (name != other.name)
      return false;
    return true;
  }
}
