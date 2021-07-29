package jda.mosa.view.assets.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;

import javax.swing.JTextArea;

/**
 * @overview
 *  A sub-class of <tt>JTextArea</tt> that supports text wrapping. 
 *  
 * @author dmle
 */
public class JLabel extends JTextArea
{
  /** maximum number of points */
  private int maxWidth;
  
  private static final int DEFAULT_MAX_WIDTH=100;
  
  private static final String HTML_START = "<html><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><td>";
  
  private static final String HTML_END = "</td></table></html>";
  
  public JLabel(String text, int maxWidth) {
    super(text);
    setEditable(false);
    setBorder(null);
    setOpaque(false);
    setLineWrap(true);
    setWrapStyleWord(true);
    
    if (maxWidth>0)
      this.maxWidth=maxWidth;
    else
      this.maxWidth=DEFAULT_MAX_WIDTH;    
    
    updateSize(text);
  }
  
  public JLabel() {
    this(null,DEFAULT_MAX_WIDTH);
  }
  
  public JLabel(String text) {
    this(text,DEFAULT_MAX_WIDTH);
  }
  
  public JLabel(int maxWidth) {
    this(null,maxWidth);
  }

  public int getMaxWidth() {
    return maxWidth;
  }

  public void setMaxWidth(int maxWidth) {
    this.maxWidth = maxWidth;
  }
  
  @Override
  public void setText(String text) {
    updateSize(text);
    super.setText(text);
  }
  
//  @Override
//  public void repaint() {
//    updateSize(getText());
//    super.repaint();
//  }

  /**
   * @effects 
   *  if text != null
   *    update this.size according to maxWidth (wrap text if necessary)
   */
  public void updateSize(String text) {
    // set the text and adjust preferred width to wrap
    Dimension size;
    if (text != null) {
      Font f = getFont();  
      FontMetrics fm = null;
//      try {
      fm = getFontMetrics(f);
//      } catch (NullPointerException e) {
//        // skip
//        return;
//      }
      
      int fontHeight = fm.getHeight();  
      int lineHeight = fontHeight+2;
      float stringWidth = fm.stringWidth(text);
      Insets is = getInsets();
      int width = maxWidth+is.left+is.right;

      if (stringWidth > maxWidth) {
        int linesCount = (int) (stringWidth / maxWidth);
        if (linesCount*maxWidth<stringWidth)
          linesCount += 1; 
        
        //System.out.println("lines count: " + linesCount);
        
        int height = lineHeight*linesCount;
        height += is.top+is.bottom;
        
        size = new Dimension(width,height);
        setPreferredSize(size);
      } else {
        size = getPreferredSize();
        // if necessary, adjust to size(maxWidth,lineHeight)
        if (size.getHeight() > lineHeight) {
          setPreferredSize(new Dimension(maxWidth,lineHeight));
        }
      }
    }    
  }
}
