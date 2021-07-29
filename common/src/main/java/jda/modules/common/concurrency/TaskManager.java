package jda.modules.common.concurrency;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jda.modules.common.Toolkit;
import jda.modules.common.concurrency.Task.TaskName;

/**
 * @overview
 *  A helper class responsible for managing {@link Task}s.
 *  
 *  <p>There can be any number of {@link Task}s being executed at the same time and in any order.
 *  
 *  <p>{@link Task}s that need to be executed in certain order need to be placed in a {@link RunnableQueue}.
 *  To create a task queue, first invoke {@link #createTaskQueue()} which returns an empty {@link RunnableQueue} object. 
 *  Then add the tasks to this queue using methods of the {@link RunnableQueue} class. 
 *  Finally, invoke {@link #runTaskQueue(RunnableQueue)} to execute the tasks in queue.
 *  
 * @author dmle
 */
public class TaskManager {
  /** a repository of tasks managed by this */
  private Map<TaskName, Task> taskRepository;

  /**
   * the task runner which manages a pool of reusable thread objects that are used to run tasks
   */
  private TaskRunner runner;
    
  private static final boolean debug = Toolkit.getDebug(TaskManager.class);
  
  public TaskManager() {
    taskRepository = new HashMap<>();
    
    runner = new TaskRunner();
  }
  
  /**
   * @effects 
   *  register <tt>t</tt> to task repository
   */
  public void registerTask(Task t) {
    taskRepository.put(t.getName(), t);
  }
  
  /**
   * @effects 
   *  if exists task with the specified name in the task repository
   *    return task
   *  else
   *    return null
   */
  public Task getTask(TaskName name) {
    return taskRepository.get(name);
  }
  
  /**
   * @effects 
   *  if exists a task with the specified name in the task repository
   *    execute the task on a separate thread of execution
   *  else
   *    do nothing
   */
  public void run(TaskName name) {
    Task t = getTask(name);
    
    if (t != null) {
      run(t);
    }
  }
  
  /**
   * @requires 
   *  t != null
   * @effects 
   *  execute the specified task on a separate thread of execution
   */
  public void run(Task t) {
    runner.run(t, false);
  }
  
  /**
   * @requires 
   *  t != null
   * @effects 
   *  execute the specified task on a separate thread of execution and wait 
   *  for it to finish
   */
  public void runAndWait(Task t) {
    runner.run(t,true);
  }
  
  /**
   * @effects
   *  creates and return an empty <tt>Queue</tt> of <tt>Task</tt>
   */
  public RunnableQueue<Task> createTaskQueue() {
    return new RunnableQueue<Task>() {
      /**
       * see <tt>runTaskQueue</tt> for details.
       * @effects 
       *  run task t on a separated thread in the thread pool, waiting for it to finish.
       *  When the task is completed, remove it from pool.
       */
      @Override
      public void processElement(Task t) {
        runner.runQueuedTask(t);
      }
    };
  }
  
  /**
   * @modifies queue
   * 
   * @effects
   *  execute <tt>Task</tt>s in <tt>queue</tt> in a separate thread with each <tt>Task</tt>
   *  being executed in its own thread and in the specified order; i.e. 
   *  a <tt>Task</tt> that appears later in the queue is executed after those before it.
   *  
   *  <p>Each <tt>Task</tt> is removed from <tt>queue</tt> before execution.
   *  
   *  <p>Return immediately after starting the queue.
   *  
   * <p>Note that the execution order of Tasks in <tt>queue</tt>s is guaranteed but the order
   * in relation to other <tt>Task</tt>s that may be running is not. 
   */
  public void runTaskQueue(RunnableQueue<Task> queue) {
    runner.runQueue(queue, false);
  }
  
  /**
   * @effects
   *  execute <tt>Task</tt>s in <tt>queue</tt> in a separate thread with each <tt>Task</tt>
   *  being executed in its own thread and in the specified order.
   *  A <tt>Task</tt> that appears later in the queue is executed after those before it.
   *  
   *  <p>Wait for the queue to finish execution before return
   *  
   * <p>Note that the execution order of Tasks in <tt>queue</tt>s is guaranteed but the order
   * in relation to other <tt>Task</tt>s that may be running is not. 
   */
  public void runTaskQueueAndWait(RunnableQueue<Task> queue) {
    runner.runQueue(queue, true);
  }

  /**
   * @effects 
   *  if there are running tasks in pool 
   *    return the number of these
   *  else
   *    return -1
   */
  public int getRunningTasksCount() {
    return runner.getRunningTasksCount();
  }
  
  /**
   * @effects 
   *  if task <tt>t</tt> is running
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isRunning(Task t) {
    return !t.isStopped();
  }

  /**
   * @effects 
   *  if there are running tasks
   *    return true
   *  else
   *    return false
   */
  public boolean isRunning() {
    return getRunningTasksCount() > -1;
  }
  
  /**
   * This method differs from invoking {@link #runAndWait(Task, boolean)} with the second option <tt> = true</tt>
   * in that waiting is performed on a separate thread of execution from the task-invocation thread.
   * This has two advantages. 
   * First, it waits not only for the task to complete its execution but also for it to be removed from 
   * the task pool. 
   * Second, it allows any <tt>JOptionType</tt>-typed message dialogs that are displayed by the task 
   * to be properly rendered on the screen.   
   * @effects 
   *  wait for the specified task to complete (and removed from the task pool) 
   *  or until <tt>maxWaitTime</tt>  (in milliseconds) is reached
   *   
   *  <p>If <tt>t</tt> was successfully stopped 
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>  
   * @version 
   * - 3.0 added maxRunTime 
   */
  public boolean waitFor(Task t, int maxWaitTime) {
    return runner.waitFor(t, maxWaitTime);
  }
  
  /**
   * @effects 
   *  if there tasks being executed
   *    wait for them to finish before return
   *  else
   *    do nothing
   */
  public void waitForAll() {
    runner.waitForAll();
  }
  
  /**
   * @effects 
   *  if there tasks being executed
   *    wait for them to finish and up to the specified number of seconds before return
   *  else
   *    do nothing
   */
  public void waitForAll(int secs) {
    runner.waitForAll(secs);
  }
  
  /**
   * @overview
   *    Represents a Queue that performs a specific task which can be executed in a thread.
   *     
   * @author dmle
   */
  public static abstract class RunnableQueue<T> extends LinkedList<T> implements Runnable {
    //
    public void run() {
      T element;
      while (!isEmpty()) {
        element = remove();
        processElement(element);
      }
    }
    
    abstract void processElement(T e);
  } // end RunnableQueue

  /**
   * @overview
   *    The task runner responsible for executing the tasks managed by {@link TaskManager}
   *     
   * @author dmle
   */
  private static class TaskRunner {
    /**
     * the task runner which manages a pool of reusable thread objects that are used to run tasks
     */
    private ExecutorService executor;
    
    /** record all currently running tasks (if any)*/
    private RunPool<Task> runPool; 
    
    public TaskRunner() {
      runPool =  new RunPool<Task>() {
        @Override
        boolean candidateForRemoval(Task t) {
          // return true if task is completed
          return t.isStopped();
        }
      };
      
      executor = Executors.newCachedThreadPool();
    }

    /**
     * @effects 
     *  if there are running tasks in pool 
     *    return the number of these
     *  else
     *    return -1
     */    
    public int getRunningTasksCount() {
      return runPool.size();
    }

    /**
     * @effects 
     * <pre>
     *  run t on a thread in a thread pool and monitors t (in the background) for completion.
     *  if waitForTask = true
     *    wait for t to finish
     *  else
     *    return immediately after adding t to pool
     *    
     *  When t is completed, remove it from the pool   </pre>
     */
    void run(Task t, boolean waitForTask) {
      // mark the task is running (need to do this before adding to pool to avoid it from being 
      // removed prematurely)
      t.setIsStopped(false);
      
      // add task to pool 
      //TODO: needs to check if t is still in runPool (possibly from a previous execution)
      runPool.add(t);
      
      if (waitForTask) {
        // wait for task to finish
        try {
          executor.submit(t).get();
          //TODO: should we also need to wait for task to be removed from pool? (see TODO above)
        } catch (Exception e) {
          //TODO: log it?
          e.printStackTrace();
        }
      } else {
        // return immediately 
        executor.submit(t);
      }
    }
    
    /**
     * @modifies queue
     * @effects <pre>
     *  run each tasks in <tt>queue</tt> (in order) on a thread in a thread pool and monitors them (in the background) for completion.
     *  if waitForQueue = true
     *    wait for the entire <tt>queue</tt> to finish before return
     *  else
     *    return immediately after start running the <tt>queue</tt>
     *    
     *  When a task is completed, remove it from <tt>queue</tt> and from the run pool.</pre>   
     */
    void runQueue(RunnableQueue<Task> queue, boolean waitForQueue) {
      // mark each task in queue as running (need to do this before adding to pool to avoid it from being 
      // removed prematurely)
      for (Task t : queue) t.setIsStopped(false);
      
      if (debug)
        System.out.printf("%s task queue: %s%n", TaskManager.class.getSimpleName(), queue);
      
      // add queue tasks to pool to monitor
      runPool.add(queue);
      
      // run the entire queue in a thread
      if (waitForQueue) {
        // wait for queue to finish
        try {
          executor.submit(queue).get();
        } catch (Exception e) {
          // TODO: log it?
          e.printStackTrace();
        }
      } else {
        // return immediately
        executor.submit(queue);
      }
    }
    
    /**
     * @effects 
     *  executes a queued task (initially added by {@link #runQueue(RunnableQueue)}) and 
     *  <b>waits</b> for it to complete.
     *  When the task is completed, remove it from pool.
     */
    void runQueuedTask(Task t) {
      Future ft = executor.submit(t);
      try {
        ft.get(); // wait for t to finish
      } catch (InterruptedException | ExecutionException e) {
        // debug
        e.printStackTrace();
      } finally {
        // remove task from pool (queued tasks were initially added by runQueue()) 
        runPool.remove(t);
      }
    }

    /**
     * This method differs from invoking {@link #run(Task, boolean)} with the second option <tt> = true</tt>
     * in that waiting is performed on a separate thread of execution from the task-invocation thread.
     * This has two advantages. 
     * First, it waits not only for the task to complete its execution but also for it to be removed from 
     * the task pool. 
     * Second, it allows any <tt>JOptionType</tt>-typed message dialogs that are displayed by the task 
     * to be properly rendered on the screen.   
     *  
     * @effects 
     *  wait for the specified task to complete (and removed from the task pool) 
     *  or until <tt>maxWaitTime</tt>  (in milliseconds) is reached
     *   
     *  <p>If <tt>t</tt> was successfully stopped 
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt>
     *       
     * @version 
     * - 3.0 added maxRunTime   
     */
    boolean waitFor(Task t, int maxWaitTime) {
      return runPool.waitFor(t, maxWaitTime);
    }
    
    /**
     * @effects 
     *  wait for tasks in pool to complete
     */
    void waitForAll() {
      runPool.waitUntilEmpty();
    }
    
    /**
     * @effects 
     *  if there tasks being executed
     *    wait for them to finish and up to the specified number of seconds before return
     *  else
     *    do nothing
     */
    public void waitForAll(int secs) {
      runPool.waitUntilMaxTime(secs);
    }
  } // end TaskRunner
  
  /**
   * @effects
   *   Represents a pool of elements typed <tt>T</tt>. The elements are not kept for ever in the pool. 
   *   They are monitored by a garbage collection thread (method: {@link RunPool#garbageCollect()}), 
   *   which periodically checks whether each element can be removed 
   *   (method: {@link RunPool#candidateForRemoval(Object)}). All elements that satisfy this check 
   *   are removed from the pool.   
   *     
   * @author dmle
   */
  private static abstract class RunPool<T> extends LinkedList<T> {
    
    /**time interval (millisecs) to run the garbage collection task*/
    private static final int GARBAGE_CYCLE = 500;  // millisecs
    
    /**
     * time interval (millisecs) to run each waiting task. 
     * Defined to be smaller than {@link #GARBAGE_CYCLE}
     */
    private static final int WAIT_CYCLE = 200;   // 
    
    /**
     * @effects 
     *  initialises this as empty, 
     *  runs the garbage collection thread
     */
    public RunPool() {
      super();
      garbageCollect();
    }
    
    //
    void garbageCollect() {
      // run garbage collect thread
      new Thread() {
        public void run() {
          while (true) {  // runs forever in the background
            while (!isEmpty()) {
              // find all elements that can be removed
              List<T> removeList = null;
              
//              synchronized (RunPool.this) {
//                // wait while tasks being added 
//              }
              
              int sz = size();
              T element;
              try { 
                // TODO: a temporary solution
                // this try/catch is provided because this loop access the list element in a non-thread safe 
                // manner 
                for (int i = 0; i < sz; i++) {
                    element=get(i);
                    if (candidateForRemoval(element)) {
                      if (removeList==null) removeList=new LinkedList<>();
                      removeList.add(element);
                    }
                } // end for
              } catch (RuntimeException e) {
                // ignore
              }
              
              // remove candidates (if any)
              if (removeList != null) {
                //FIXME: this loop is to temporarily overcome the ConcurrentModificationException  
                while(true) {
                  try {
                    removeAll(removeList);
                    break;  // succeeded normally
                  } catch (Exception e) {
                    // ignore an retry...
                  }
                }
              }
            } // end while empty
            
            // no elements in pool
            Task.sleep(GARBAGE_CYCLE);  // wait a bit
          } // end while true
        }
      }.start();
    }
    
    void add(RunnableQueue<T> queue) {
      addAll(queue);
    }
    
    abstract boolean candidateForRemoval(T element);

    /**
     * @requires maxWaitTime > -1 
     * @effects 
     *  wait until <tt>element</tt> is removed from this
     *  or until <tt>maxWaitTime</tt> (in milliseconds) is reached 
     *  
     *  <p>If <tt>element</tt> is removed from this
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt> 
     *  
     * @version 
     * - 3.0 added maxRunTime 
     */
    boolean waitFor(T element, int maxWaitTime) {
      int waitTime = 0;
      while (this.contains(element) && waitTime <= maxWaitTime) {
        Task.sleep(WAIT_CYCLE);
        waitTime += WAIT_CYCLE;
      }
      
      if (this.contains(element)) {
        // element is still in this
        return false;
      } else {
        return true;
      }
    }
    
    /**
     * @effects 
     *  wait until all elements of this are removed (i.e. this is empty)
     */
    void waitUntilEmpty() {
      while (!isEmpty()) {
        Task.sleep(WAIT_CYCLE);
      }
    }
    
    void waitUntilMaxTime(int secs) {
      int waitTime = 0;
      int maxTime = secs * 1000;
      int sleepTime = WAIT_CYCLE;
      while (!isEmpty() && waitTime < maxTime) {
        waitTime += sleepTime;
        Task.sleep(sleepTime);
      }
    }
  } // end RunPool

}
