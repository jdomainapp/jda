package jda.mosa.view.assets.drawing;

import java.awt.Graphics;
import java.awt.Point;

import jda.modules.dcsl.syntax.DClass;

@DClass(serialisable=false)
public interface Drawable {
  
  public void setCurrentPosition(Point p);
  
  public void draw(Graphics g);
  
}
