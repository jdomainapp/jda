package jda.mosa.view.assets.drawing.activity;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.SwingUtilities;

/**
 * @overview 
 *  Draws a decisional structure, consisting of an input edge, the decision node, 
 *  and an output edge structure consisting of two output edges and a '...' label in-between the two edges.
 *  
 * @author Duc Minh Le (ducmle)
 *  
 * @version 5.2
 */
public class DecisionalLabel extends ActStrucLabel {

  /**
   * auto-generated
   */
  private static final long serialVersionUID = -1610871798693612893L;
  
  /**
   *  Decision node (diamond) settings:
   *  
   *  (x0,y0): left-most point of the node
   *   2*w = node's width
   *  requires: x0+w > 0.5*length("...") /\ 
   *            x0 > ArrowWidth, ArrowHeight /\ 
   *            y0 >= l+w
   *  prefers: w % 3 = 0
   */
  private static final int 
      x0 = 10, 
      y0 = 35, 
      w = 15;
  
  /**
   * l : length of input edge (and output edge)
   * requires: l <= y0-w
   * prefers: l % 2 = 0
   */
  private static final int l = 15;

  /**
   * lengths of the line segments that make up the output edge structure
   */
  private static final int 
      l1 = (int) (l/2), // top line segment
      l2 = (int) (l/3), // diag line segment' height
      l3 = w,           // diag line segment's width
      l4 = l;           // bottom line segment
  
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
    int x1 = x0+w, y1=y0-(l+w);
    drawVertArrowLineDown(g, x1, y1, l);
    
    // decision node
    drawDiamond(g, x0, y0, w);
    
    // output edges
    int x2 = x0+w, y2=y0+w;
    drawOutputEdgeStruc(g, x2, y2, l1, l2, l3, l4);
    
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
      drawSz.setSize(2*w * wfactor + 10, (l + 2*w + l1 + l2 +l4) * hfactor + 10);
    }
    
    return drawSz;
  }
}