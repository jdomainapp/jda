package jda.mosa.view.assets.drawing.activity;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

/**
 * @overview 
 *  Draws a forked structure, consisting of an input edge, the forked node, 
 *  and an output edge structure consisting of two output edges and a '...' label in-between the two edges.
 *  
 * @author Duc Minh Le (ducmle)
 *  
 * @version 5.2
 */
public class ForkedLabel extends ActStrucLabel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = 8997834874621924157L;

  /**
   *  Forked node settings:
   *  
   *  (x0,y0): left-most point of the node
   *   2*w = node's width 
   *  requires: x0+w > 0.5*length("...") /\ 
   *            x0 > ArrowWidth, ArrowHeight /\ 
   *            y0 >= l
   *  prefers: 
   */
  private static final int 
      x0 = 10, 
      y0 = 20, 
      w = 15;
  
  /**
   * l : length of input edge (and output edge)
   * requires: l <= y0
   * prefers: l % 2 = 0
   */
  private static final int l = 15;

  /**
   * lengths of the line segments that make up the output edge structure
   */
  private static final int 
      l1 = l; // output edge's height
  
  /**
   * The expected drawing size of the decisional structure
   */
  private Dimension drawSz;
  
  @Override
  protected void paintComponent(Graphics g) {
    // settings
    Graphics2D g2 = (Graphics2D) g;
    g2.setStroke(StrokeWidth);
    g.setColor(StrokeColor);
    
    // input edge
    int x1 = x0+w, y1=y0-l;
    drawVertArrowLineDown(g, x1, y1, l);
    
    // forked node
    drawHorzLine(g, x0, y0, 2*w);
    
    // output edges
    drawVertArrowLinesDown(g, x0, y0, l1, 2*w);
    
    // draw the 'dots' label
    String dots = "...";
    int dlen = SwingUtilities.computeStringWidth(g.getFontMetrics(), dots);
    int dx = x0+w-((int)(dlen/2)), dy = y0+l1;
    g.drawString(dots, dx, dy);
  }

  /**
   * @effects 
   *  return the expected drawing size of this
   */
  @Override
  public Dimension getDrawingSize() {
    if (drawSz == null) {
      //TODO: scale this to correct size using factors
      double wfactor = 1.5d, hfactor = 1d;
      drawSz = new Dimension();
      drawSz.setSize(2*w * wfactor + 10, (l + l1) * hfactor + 10);
    }
    
    return drawSz;
  }
}