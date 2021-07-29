package jda.test.util.concurrent;

import static jda.modules.common.concurrency.Task.TaskName.CreateAssociatedChildObjects;
import static jda.modules.common.concurrency.Task.TaskName.OpenChildren;
import static jda.modules.common.concurrency.Task.TaskName.UpdateGUI;
import static jda.modules.common.concurrency.Task.TaskName.UpdateGUIButtons;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import jda.modules.common.concurrency.Task;
import jda.modules.common.concurrency.TaskManager;
import jda.modules.common.concurrency.Task.TaskName;
import jda.modules.common.concurrency.TaskManager.RunnableQueue;

public class TaskManagerTest {
  private int numTasks;
  
  @Test
  public void doTest() throws Exception {
    runTaskManager();
  }

  protected void runTaskManager() {

    final TaskManager tm = new TaskManager();
    System.out.println("Created task manager");

    Collection<Task> tasks = new LinkedList<>();
    
    Task1 t1 = new Task1(CreateAssociatedChildObjects);
    tasks.add(t1);
    
    // create a task queue
    Task2 t2 = new Task2(UpdateGUI);
    Task3 t3 = new Task3(OpenChildren);
    RunnableQueue<Task> queue = tm.createTaskQueue();
    queue.add(t2);
    queue.add(t3);
    
    // queue 
    System.out.println("Queue (created): \n" + queue);
    
    Task4 t4 = new Task4(UpdateGUIButtons);
    tasks.add(t4);
    
    
    System.out.println("Registering tasks");
    tm.registerTask(t1);
    tm.registerTask(t2);
    tm.registerTask(t3);
    tm.registerTask(t4);

    // test tasks
    Task testTask;
    for (TaskName testName : TaskName.values()) {
      if (testName.name().startsWith("Test")) {
        testTask = new Task4(testName);
        
        tasks.add(testTask);
        
        tm.registerTask(testTask);
      }
    }
    
    // run tasks and queue
    System.out.println("Running tasks");

    for (Task t : tasks) {
      tm.run(t);
    }
    
    // run queue
    tm.runTaskQueue(queue);
    
    // wait until no more tasks running in queue
    // wait using two ways: (1) using task manager (2) using task count

    Thread countTasks = new Thread() {
      public void run() {
        numTasks = tm.getRunningTasksCount();
        do {
          System.out.printf("---#tasks--> %d%n",numTasks);
          Task.sleep(500);
          numTasks = tm.getRunningTasksCount();
        } while (numTasks > 0);
        System.out.println("---#tasks--> 0");
      }
    };
    countTasks.start();
    
    tm.waitForAll();
    
    System.out.println("Complete");

    // wait for count task to finish
    while(countTasks.isAlive()) {
      Task.sleep(100);
    }
    
    // check queue: 
    System.out.println("Queue (after): \n" + queue);
  }
  
  public int getRunningTasksCount() {
    return numTasks;
  }
  
  class Task1 extends Task {

    public Task1(TaskName name) {
      super(name);
    }

    @Override
    public void run() {
      setIsStopped(false);
      
      System.out.printf("START: %s(%s)%n", this.getClass().getSimpleName(), getName());
      
      // sleep random time 
      sleep((long)(3000*Math.random()));
      
      // stop
      setIsStopped(true);
      
      System.out.printf("  STOP: %s(%s)%n", this.getClass().getSimpleName(), getName());
    }
  }
  
  class Task2 extends Task {

    public Task2(TaskName name) {
      super(name);
    }

    @Override
    public void run() {
      setIsStopped(false);
      
      System.out.printf("START: %s(%s)%n", this.getClass().getSimpleName(), getName());
      
      // sleep random time 
      sleep((long)(5000
          *Math.random()
          ));
      
      // stop
      setIsStopped(true);
      
      System.out.printf("  STOP: %s(%s)%n", this.getClass().getSimpleName(), getName());

    }
  }
  
  class Task3 extends Task {

    public Task3(TaskName name) {
      super(name);
    }

    @Override
    public void run() {
      setIsStopped(false);
      
      System.out.printf("START: %s(%s)%n", this.getClass().getSimpleName(), getName());
      
      // sleep random time 
      sleep((long)(3000*Math.random()));
      
      // stop
      setIsStopped(true);
      
      System.out.printf("  STOP: %s(%s)%n", this.getClass().getSimpleName(), getName());

    }
  }
  
  class Task4 extends Task {

    public Task4(TaskName name) {
      super(name);
    }

    @Override
    public void run() {
      setIsStopped(false);
      
      System.out.printf("START: %s(%s)%n", this.getClass().getSimpleName(), getName());
      
      // sleep random time 
      sleep((long)(3000*Math.random()));
      
      // stop
      setIsStopped(true);
      
      System.out.printf("  STOP: %s(%s)%n", this.getClass().getSimpleName(), getName());
    }
  }
}
