package jda.mosa.view.assets.drawing.activity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Stroke;

import javax.swing.JLabel;

/**
 * @overview 
 *  Draws a activity structure that reflects an activity design pattern (decisional, forked, joined, merged).
 *  
 * @author Duc Minh Le (ducmle)
 *  
 * @version 5.2
 */
public abstract class ActStrucLabel extends JLabel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = 902208274777050656L;

  protected static final Color StrokeColor = Color.GRAY;
  protected static final Stroke StrokeWidth = new BasicStroke(3);

  /**
   * Arrow settings
   */
  private static final int ArrowHeight = 5;
  private static final int ArrowWidth = 5; 
  
  @Override
  protected abstract void paintComponent(Graphics g);

  /**
   * @effects 
   *  draw the output edge structure
   */
  protected void drawOutputEdgeStruc(Graphics g, int x0, int y0, 
      int l1, int l2, int l3, int l4) {
    // left branch:
    int x = x0-l3, y1=y0+l1, y2=y0+l1+l2, y3=y0+l1+l2+l4;
    int[] xs = { x0, x0, x, x };
    int[] ys = { y0, y1, y2, y3};
    int n = xs.length;
    g.drawPolyline(xs, ys, n);
    drawArrowHeadsDown(g, x, y3);
    
    // right branch:
    x = x0+l3;
    xs = new int[] { x0, x, x };
    ys = new int[] { y1, y2, y3};
    n = xs.length;
    g.drawPolyline(xs, ys, n);
    drawArrowHeadsDown(g, x, y3);
  }


  /**
   * @effects 
   *  draw the input edge structure
   */
  protected void drawInputEdgeStruc(Graphics g, int x0a, int y0a, int x0b, int y0b, int l1, int l2,
      int l3, int l4) {
    // left branch:
    int x = x0a-l3, y1=y0a+l1, y2=y1+l2, y3=y2+l4;
    int[] xs = { x0a, x0a, x, x };
    int[] ys = { y0a, y1, y2, y3};
    int n = xs.length;
    g.drawPolyline(xs, ys, n);
    drawArrowHeadsUp(g, x0a, y0a);
    
    // right branch:
    x = x0b+l3;
    xs = new int[] { x0b, x0b, x, x };
    ys = new int[] { y0b, y1, y2, y3};
    n = xs.length;
    g.drawPolyline(xs, ys, n);
    drawArrowHeadsUp(g, x0b, y0b);
  }

  /**
   * @effects 
   *  draw the diamond
   */
  protected void drawDiamond(Graphics g, int x0, int y0, int w) {
    int[] xPoints = new int[] {x0, x0+w, x0+2*w, x0+w}; 
    int[] yPoints = new int[] {y0, y0-w, y0, y0+w};
    int n = xPoints.length;     
    g.drawPolygon(xPoints, yPoints, n);
  }

  /**
   * @effects 
   *  draw a vertical arrowed edge (the arrow head is facing downward)
   */
  protected void drawVertArrowLineDown(Graphics g, int x0, int y0, int length) {
    int x1 = x0, y1 = y0+length;
    g.drawLine(x0, y0, x1, y1);
    drawArrowHeadsDown(g, x1, y1);
  }

  /**
   * @effects 
   *  draw a vertical arrowed edge (the arrow head is facing upward)
   */
  protected void drawVertArrowLineUp(Graphics g, int x0, int y0, int length) {
    int x1 = x0, y1 = y0-length;
    g.drawLine(x0, y0, x1, y1);
    
    drawArrowHeadsUp(g, x1, y1);
  }
  
  /**
   * @effects 
   *  Draw 2 parallel vertical arrowed edges (facing downwards) that are <tt>d</tt> units apart.
   *  The first edge is drawn at (x0, y0) and has length <tt>l</tt>, using {@link #drawVertArrowLineDown(Graphics, int, int, int)}.
   */
  protected void drawVertArrowLinesDown(Graphics g, int x0, int y0, int l,
      int d) {
    // first arrowed edge
    drawVertArrowLineDown(g, x0, y0, l);
    
    // second arrowed edge
    drawVertArrowLineDown(g, x0 + d, y0, l);
  }

  /**
   * @effects 
   *  draw line <tt>(x0, y0, x0+d, y0)</tt>
   */
  protected void drawHorzLine(Graphics g, int x0, int y0, int d) {
    g.drawLine(x0, y0, x0+d, y0);
  }
  
  /**
   * @effects 
   *  draw the two heads of an arrow at point (x,y), facing downward
   */
  protected void drawArrowHeadsDown(Graphics g, int x, int y) {
    g.drawLine(x, y, x-ArrowWidth, y-ArrowHeight);
    g.drawLine(x, y, x+ArrowWidth, y-ArrowHeight);    
  }


  /**
   * @effects 
   *  draw the two heads of an arrow at point (x,y), facing upward
   */
  protected void drawArrowHeadsUp(Graphics g, int x, int y) {
    g.drawLine(x, y, x-ArrowWidth, y+ArrowHeight);
    g.drawLine(x, y, x+ArrowWidth, y+ArrowHeight);    
  }
  
  /**
   * @effects 
   *  return the expected drawing size of this
   */
  public abstract Dimension getDrawingSize();
}