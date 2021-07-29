package jda.mosa.view.assets.drawing.activity;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

/**
 * @overview 
 *  Draws a merged structure, consisting of an input edge structure (consisting of two input edges and a "..." label in-between these edges), 
 *  the merged node, and an output edge. 
 *  
 * @author Duc Minh Le (ducmle)
 *  
 * @version 5.2
 */
public class MergedLabel extends ActStrucLabel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = 4375597357735889778L;

  /**
   *  Merged node settings:
   *  
   *  (x0,y0): left-most point of the node
   *   2*w = node's width 
   *  requires: x0+w > 0.5*length("...") /\ 
   *            x0 > ArrowWidth, ArrowHeight /\ 
   *            y0 >= l+w
   *  prefers: 
   */
  private static final int 
      x0 = 10, 
      y0 = 35, 
      w = 15;
  
  /**
   * l : length of output edge 
   * requires: l <= y0-w
   * prefers: l % 2 = 0
   */
  private static final int l = 15;

  /**
   * lengths of the line segments that make up the output edge structure
   */
  private static final int 
      l1 = 10, // input edge's height (3)
      l2 = 10, // input edge's height (2)
      l3 = w,           // input edge's width 
      l4 = l;           // input edge's height (1)
  
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
    
    // output edge
    int x1 = x0+w, y1=y0-w;
    drawVertArrowLineUp(g, x1, y1, l);
    
    // merged node
    drawDiamond(g, x0, y0, w);
    
    // input edges
    int x2a = x0+w-2, y2a=y0+w-2,
        x2b = x0+w+2, y2b=y0+w-2;
    drawInputEdgeStruc(g, x2a, y2a, x2b, y2b, l1, l2, l3, l4);
    
    // draw the 'dots' label
    String dots = "...";
    int dlen = SwingUtilities.computeStringWidth(g.getFontMetrics(), dots);
    int dx = x0+w-((int)(dlen/2)), dy = y0+w+l1+l2+l4;
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
      drawSz.setSize(2*w * wfactor + 10, (l + 2*w + l1 + l3) * hfactor + 10);
    }
    
    return drawSz;
  }
}