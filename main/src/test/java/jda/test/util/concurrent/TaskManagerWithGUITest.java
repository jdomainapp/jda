package jda.test.util.concurrent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;

import org.junit.Test;

import jda.modules.common.concurrency.Task;
import jda.test.chart.SimpleWindow;

public class TaskManagerWithGUITest extends TaskManagerTest {
  @Test
  public void doTest() throws Exception {
    // create some GUI and start the tasks to see if they block the GUI while running
    SimpleWindow w = new SimpleWindow("Task manager test", 300, 400);
    w.setLayout(new BorderLayout());
    
    JLabel label = new JLabel();
    label.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 30));
    label.setForeground(Color.BLUE);
    label.setText("Running task manager");
    w.getContentPane().add(label, BorderLayout.NORTH);
    
    final JLabel lblTasks = new JLabel();
    lblTasks.setFont(new Font("Arial", Font.BOLD, 26));
    lblTasks.setForeground(Color.RED);
    w.getContentPane().add(lblTasks, BorderLayout.SOUTH);
    
    // a thread to update the running tasks
    new Thread() {
      public void run() {
        int numTasks;
        do {
          numTasks = getRunningTasksCount();
          lblTasks.setText("# running tasks: " + numTasks);
          Task.sleep(100);
        } while (true);
      }
    }.start();
    
    w.pack();
    w.setVisible(true);
    
    super.runTaskManager();
  }
  
}
