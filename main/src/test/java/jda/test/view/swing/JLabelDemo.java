package jda.test.view.swing;

import java.awt.Color;
import java.awt.Point;

import javax.swing.BorderFactory;

import jda.mosa.view.assets.swing.JLabel;
import jda.test.view.SimpleWindow;

public class JLabelDemo {
  public static void main(String[] args) {
    JLabel label1 = new JLabel("Tỉ lệ kích thước cửa sổ chính");
 
    // set up 
    setUp(label1);
    
    // mouse events
    //label1.addMouseListener(new SimpleMouseListener());
    
    // key events
    //label1.setFocusable(true); // not needed if other components are available
    //label1.addKeyListener(new SimpleKeyListener());
    
    // the window
    SimpleWindow w = new SimpleWindow("Demo label", 200, 200, new Point(100,100));
    w.add(label1);
    w.setVisible(true);
    
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {}
    
    // change text labels and see what happens
    String[] texts = {
      "a cup of Java",
      "to be or not to be",
      "a quick brown fox jumps over a lazy cat",
    };
    
    for (String t : texts) {
      label1.setText(t);
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {}
    }
  }
  
  private static void setUp(JLabel label) {
    //label.setPreferredSize(new Dimension(100, 15));
    label.setBorder(BorderFactory.createLineBorder(Color.BLUE,1));
  }
}
