package jda.test.view;

import java.awt.FlowLayout;
import java.awt.Point;

import javax.swing.JFrame;

public class SimpleWindow extends JFrame {
  private static final Point DEF_LOCATION = new Point(100,100);
  private static final int DEF_WIDTH = 200;
  private static final int DEF_HEIGHT = 200;
  
  public SimpleWindow(String title) {
    this(title, DEF_WIDTH, DEF_HEIGHT, DEF_LOCATION);
  }
  
  public SimpleWindow(String title, int width, int height) {
    this(title, width, height, DEF_LOCATION);
  }

  public SimpleWindow(String title, int width, int height, Point location) {
    super(title);
    setSize(width, height);
    setLocation(location);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    // change the flow layout
    setLayout(new FlowLayout(FlowLayout.LEFT));
  }
}
