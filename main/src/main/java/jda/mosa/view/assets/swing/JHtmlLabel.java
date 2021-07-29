package jda.mosa.view.assets.swing;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *  A sub-class of <tt>JLabel</tt> that supports text wrapping using HTML format. 
 *  
 * @author dmle
 */
public class JHtmlLabel extends javax.swing.JLabel 
{
  /** maximum number of points */
  private int maxWidth;
  
  private static final int DEFAULT_MAX_WIDTH=100;
  
  private static final String HTML_START = "<html><table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"><td>";
  
  private static final String HTML_END = "</td></table></html>";
  
  private static final char SPACE = ' ';
  
  public static final String[] ALLOWED_TAGS = {
    "<br>"  // line break
  };
  
  public JHtmlLabel(String text, int maxWidth) {
    super(text);
    if (maxWidth>0)
      this.maxWidth=maxWidth;
    else
      this.maxWidth=DEFAULT_MAX_WIDTH;    
    
    //updateSize(text);
  }
  
  public JHtmlLabel() {
    this(null,DEFAULT_MAX_WIDTH);
  }
  
  public JHtmlLabel(String text) {
    this(text,DEFAULT_MAX_WIDTH);
  }
  
  public JHtmlLabel(int maxWidth) {
    this(null,maxWidth);
  }


  /**
   * @effects 
   *  create and return a <tt>JHtmlLabel</tt> whose type is <tt>displayClass</tt> and that 
   *  uses <tt>paramTypes</tt>.
   *  @version 2.7.4
   */
  public static JHtmlLabel createInstance(Class<? extends JHtmlLabel> displayClass) {
    try {
      // invoke the constructor to create object 
      JHtmlLabel instance = displayClass.getConstructor().newInstance();
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {displayClass.getSimpleName(), ""});
    }
  }
  
  public int getMaxWidth() {
    return maxWidth;
  }

  public void setMaxWidth(int maxWidth) {
    this.maxWidth = maxWidth;
  }
  
  @Override
  public void setText(String text) {
    //updateSize(text);
    /*
     * v2.7.4: add null check
     */
    if (text == null)
      super.setText(null);
    else
      super.setText(HTML_START+text+HTML_END);   
  }

  /**
   * @effects 
   *   if this.text != null
   *    return the raw text without HTML tags
   *   else
   *    return null
   */
  public String getTextRaw() {
    // remove the HTML tags
    String text = super.getText();
    StringBuffer raw = null;
    if (text != null) {
      // remove HTML outer tags
      raw = new StringBuffer(text.substring(HTML_START.length(),
          text.lastIndexOf(HTML_END)));
      // remove other tags if any
//      int pos;
//      String replace;
//      for (String tag : ALLOWED_TAGS) {
//        
//        do {
//          pos = raw.indexOf(tag);
//          if (pos > -1) {
//            replace = "";
//            if (pos > 0 && raw.charAt(pos-1) != SPACE)
//              replace = SPACE + replace;
//            else if (pos == 0 && pos < raw.length()-1 && raw.charAt(pos+1) != SPACE)
//              replace = replace + SPACE;
//            
//            raw=raw.replace(pos, pos+tag.length(), replace);
//          }
//        } while (pos > -1);
//      }
      removeHtmlTags(raw);
    }
 
    return (raw != null) ? raw.toString() : null;
  }
  
  /**
   * @modifies raw
   * @effects 
   *  remove all Html tags in the list {@link #ALLOWED_TAGS} from <tt>raw</tt> 
   */
  public static final void removeHtmlTags(StringBuffer raw) {
    int pos;
    String replace;
    for (String tag : ALLOWED_TAGS) {
      
      do {
        pos = raw.indexOf(tag);
        if (pos > -1) {
          replace = "";
          if (pos > 0 && raw.charAt(pos-1) != SPACE)
            replace = SPACE + replace;
          else if (pos == 0 && pos < raw.length()-1 && raw.charAt(pos+1) != SPACE)
            replace = replace + SPACE;
          
          raw=raw.replace(pos, pos+tag.length(), replace);
        }
      } while (pos > -1);
    }
  }
  
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
      try {
        fm = getFontMetrics(f);
      } catch (NullPointerException e) {
        // skip
        return;
      }
      
      int fontHeight = fm.getHeight();  
      int lineHeight = fontHeight+2;
      float stringWidth = fm.stringWidth(text);
      Insets is = getInsets();
      int width = maxWidth+is.left+is.right;

      if (stringWidth > maxWidth) {
        int linesCount = (int) (stringWidth / maxWidth);
        if (linesCount*maxWidth<stringWidth)
          linesCount += 2;  // 1 does not work well
        
        //System.out.println("lines count: " + linesCount);
        
        //TODO: actual text wrapping may still introduce extra lines
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
