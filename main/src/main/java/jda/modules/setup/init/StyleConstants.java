package jda.modules.setup.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.conceptmodel.view.StyleName;

public class StyleConstants {
  private static final int DefaultFontSize = 14; 
  
  private static final String DefTextFgColor = null; // default //"0,0,0";  // black

  public static final String ColorBlue = "0,100,255"; // light blue
  public static final String ColorDarkBlue = "0,0,125"; // dark blue
  public static final String ColorBlack = "0,0,0";
  public static final String ColorRed = "255,0,0";
  public static final String ColorOrange = "255,124,11"; // orange
  public static final String ColorLightYellow = "255,255,170";
  public static final String ColorYellow = "210,210,120";
  public static final String ColorDarkYellow = "173,173,88";
  public static final String ColorLightGray = "170,170,120";
  public static final String ColorGray = "170,170,170";
  public static final String ColorDarkGray = "140,140,140";


  private static final String DefHeadingFgColor = ColorBlack;//ColorBlue; 
  private static final String DefHeading1FgColor = ColorBlue; 

  private static final Object DefLinkFgColor = ColorBlue;
  
  private static final String DefFontName = "Verdana"; //"Arial";
  
  // v2.7.4: moved to static initialiser
//  /**style: default*/
//  public static final Style Default = new Style(StyleName.Default.name(), "Arial,"+DefaultFontSize+",0",null,DefTextFgColor);
//
//  /**style: default, on white background */
//  public static final Style DefaultOnWhite = new Style(StyleName.DefaultOnWhite.name(),null,"255,255,255",null);
//
//  /**style: default, bold*/
//  public static final Style DefaultBold = new Style(StyleName.DefaultBold.name(), "Arial,"+DefaultFontSize+",1",null,null);
//
//  /**style: default, blue*/
//  public static final Style DefaultBlue = new Style(StyleName.DefaultBlue.name(), "Arial,"+DefaultFontSize+",0",null,DefHeadingFgColor);
//
//  /**style: default, bold, on white*/
//  public static final Style DefaultBoldOnWhite = new Style(StyleName.DefaultBoldOnWhite.name(), "Arial,"+DefaultFontSize+",1","255,255,255",null);
//
//
//  /**style: Heading 1 (font 20, on default, in light-blue color)*/
//  public static final Style Heading1 = new Style(StyleName.Heading1.name(),"Arial,20,1", null,DefHeadingFgColor);
//
//  /**style: Heading 1 (font 20, bold, on white, in light-blue color)*/
//  public static final Style Heading1OnWhite = new Style(StyleName.Heading1OnWhite.name(),"Arial,20,1", "255,255,255",DefHeadingFgColor);
//
//  /**style: Heading 2 (font 18, bold, on default, in default)*/
//  public static final Style Heading2 = new Style(StyleName.Heading2.name(),"Arial,18,1",null,null);
//
//  /**style: Heading 3 (font 14, bold, on default, in light-blue)*/
//  public static final Style Heading3 = new Style(StyleName.Heading3.name(),"Arial,16,1", null,DefHeadingFgColor);
//
//  /**style: Heading 4 (font default, on default, in light-blue)*/  
//  public static final Style Heading4 = new Style(StyleName.Heading4.name(),"Arial,14,1", null,DefHeadingFgColor);
//  
//  public static final Style HeadingTitle = new Style(StyleName.HeadingTitle.name(),"Arial,18,0", null,DefHeadingFgColor);
  //
  
  protected static final StyleConstants instance;
  
  /**style: default*/
  public static final Style Default;

  /**style: default font with Arial font name
   * @version 3.1*/
  public static final Style DefaultTechnical;

  /**style: default, on white background */
  public static final Style DefaultOnWhite;

  /**style: default, on gray background */
  public static final Style DefaultOnLightGray;

  /**style: default, on dark gray background */
  public static final Style DefaultOnYellow;

  /**style: default, on light gray background */
  public static final Style DefaultOnLightYellow;
  
  /**style: default, bold*/
  public static final Style DefaultBold;

  /**style: default, blue*/
  public static final Style DefaultBlue;

  /**style: link */
  public static final Style Link;
  
  /**style: default, bold, on white*/
  public static final Style DefaultBoldOnWhite;


  /**style: Heading 1 (font 20, on default, in light-blue color)*/
  public static final Style Heading1;

  /**style: Heading 1 (font 20, bold, on white, in light-blue color)*/
  public static final Style Heading1OnWhite;

  /**style: Heading 2 (font 18, bold, on default, in default)*/
  public static final Style Heading2;

  /**style: Heading 3 (font 14, bold, on default, in light-blue)*/
  public static final Style Heading3;

  /**style: Heading 4 (font default, on default, in light-blue)*/  
  public static final Style Heading4;

  /**style: Heading 4 with fore-ground color RED
   * @version 3.1
   * */  
  public static final Style Heading4Red;

  /**style: Heading 4 with fore-ground color DARK RED
   * @version 3.2
   * */  
  public static final Style Heading4DarkYellow;

  public static final Style HeadingTitle;


  static {
    
    // load the default styles
    instance = new StyleConstants();
    
    final int fontSize = instance.getProperty("Text.size", Integer.class);
    final String textFgColor = instance.getProperty("Text.fgColor", String.class);
    final String linkFgColor = instance.getProperty("Text.linkFgColor", String.class);
    final String head1FgColor = instance.getProperty("Heading1.fgColor", String.class);
    final String headFgColor = instance.getProperty("Heading.fgColor", String.class);
    
    Default = new Style(StyleName.Default.name(), DefFontName+","+fontSize+",0",null,textFgColor);
    
    instance.mapPropertyToStyle("Text.fgColor", Default);
    instance.mapPropertyToStyle("Text.font", Default);

    // v3.1:
    DefaultTechnical = new Style(StyleName.DefaultTechnical.name(), "Monospaced,"+fontSize+",0",null,textFgColor);
    instance.mapPropertyToStyle("Text.fgColor", DefaultTechnical);
    // exlude this so that font name is fixed:
    // dinstance.mapPropertyToStyle("Text.font", DefaultArial);

    DefaultOnWhite = new Style(StyleName.DefaultOnWhite.name(),null,"255,255,255",null);
    instance.mapPropertyToStyle("Text.font", DefaultOnWhite);

    // v3.2
    DefaultOnLightGray = new Style(StyleName.DefaultOnLightGray.name(),null,ColorLightGray,null);
    instance.mapPropertyToStyle("Text.font", DefaultOnLightGray);

    DefaultOnYellow = new Style(StyleName.DefaultOnYellow.name(),null,ColorYellow,null);
    instance.mapPropertyToStyle("Text.font", DefaultOnYellow);

    DefaultOnLightYellow = new Style(StyleName.DefaultOnLightYellow.name(),null,ColorLightYellow,null);
    instance.mapPropertyToStyle("Text.font", DefaultOnLightYellow);

    DefaultBlue = new Style(StyleName.DefaultBlue.name(), DefFontName+","+fontSize+",0",null, ColorBlue);
    instance.mapPropertyToStyle("Text.font", DefaultBlue);
    
    DefaultBold = new Style(StyleName.DefaultBold.name(), DefFontName+","+fontSize+",1",null,null);
    instance.mapPropertyToStyle("Text.fontBold", DefaultBold);

    DefaultBoldOnWhite = new Style(StyleName.DefaultBoldOnWhite.name(), DefFontName+","+fontSize+",1","255,255,255",null);
    instance.mapPropertyToStyle("Text.fontBold", DefaultBoldOnWhite);

    Link = new Style(StyleName.Link.name(), DefFontName+","+fontSize+",0",null, linkFgColor);
    instance.mapPropertyToStyle("Text.linkFgColor", Link);
    instance.mapPropertyToStyle("Text.linkFont", Link);

    Heading1 = new Style(StyleName.Heading1.name(),DefFontName+",20,1", null,head1FgColor);
    instance.mapPropertyToStyle("Heading1.fgColor", Heading1);
    instance.mapPropertyToStyle("Heading1.font", Heading1);

    Heading1OnWhite = new Style(StyleName.Heading1OnWhite.name(),DefFontName+",20,1", "255,255,255",headFgColor);
    instance.mapPropertyToStyle("Heading.fgColor", Heading1OnWhite);
    //instance.mapPropertyToStyle("Heading.font", Heading1OnWhite);

    Heading2 = new Style(StyleName.Heading2.name(),DefFontName+",18,1",null,null);
    //instance.mapPropertyToStyle("Heading.font", Heading2);

    Heading3 = new Style(StyleName.Heading3.name(),DefFontName+",16,1", null,headFgColor);
    instance.mapPropertyToStyle("Heading.fgColor", Heading3);
    //instance.mapPropertyToStyle("Heading.font", Heading3);

    Heading4 = new Style(StyleName.Heading4.name(),DefFontName+",14,1", null,headFgColor);
    instance.mapPropertyToStyle("Heading.fgColor", Heading4);
    instance.mapPropertyToStyle("Heading4.font", Heading4);

    Heading4Red = new Style(StyleName.Heading4Red.name(),DefFontName+",14,1", null,ColorRed);
    instance.mapPropertyToStyle("Heading.fgColor", Heading4Red);
    instance.mapPropertyToStyle("Heading4.font", Heading4Red);

    Heading4DarkYellow = new Style(StyleName.Heading4DarkYellow.name(),DefFontName+",14,1", null,ColorDarkBlue);
    instance.mapPropertyToStyle("Heading.fgColor", Heading4DarkYellow);
    instance.mapPropertyToStyle("Heading4.font", Heading4DarkYellow);

    
    HeadingTitle = new Style(StyleName.HeadingTitle.name(),DefFontName+",18,0", null,headFgColor);
    instance.mapPropertyToStyle("Heading.fgColor", HeadingTitle);
    instance.mapPropertyToStyle("HeadingTitle.font", HeadingTitle);
  }

  
  // v2.7.4
  private Properties props;

  // map property name to Styles affected by that property
  private Map<String,Set<Style>> propStyleMap;
  
  protected StyleConstants() {
    props = new Properties();
    propStyleMap = new HashMap<>();
    loadProperties();
  }
  
  private void loadProperties() {
    props.put("Heading1.fgColor", DefHeading1FgColor);
    props.put("Heading.fgColor", DefHeadingFgColor);
    
    if (DefTextFgColor != null)
      props.put("Text.fgColor", DefTextFgColor);
    
    props.put("Text.linkFgColor", DefLinkFgColor);
    props.put("Text.size", DefaultFontSize);
  }

  private Object setProperty(String propName, Object val) {
    Object oldVal = props.put(propName, val);
    
    return oldVal;
  }
  
  private <T> T getProperty(String propName, Class<T> valType) {
    Object val = props.get(propName);
    
    if (val != null && (valType.isAssignableFrom(val.getClass()))) {
      return (T) val;
    } else {
      return null;
    }
  }
  
  private void mapPropertyToStyle(String prop, Style style) {
    Set<Style> styles = propStyleMap.get(prop);
    if (styles == null) {
      styles = new HashSet<Style>();
      propStyleMap.put(prop, styles);
    }
    
    styles.add(style);
  }
  
  private Set<Style> getStyles(String prop) {
    return propStyleMap.get(prop);
  }
  
  /**
   * @modifies this
   * @effects 
   *  change property named <tt>propName</tt> to <tt>val</tt>; 
   *  change any <tt>Style s</tt> in this that is mapped to <tt>propName</tt> such that
   *  <tt>s.fgColor = val</tt> 
   */
  public void setStyleFgColor(String propName, String val) {
    setProperty(propName, val);
    
    Set<Style> styles = getStyles(propName);
    
    if (styles != null) {
      for (Style s : styles) {
        s.setFgColor(val);
      }
    }
  }

  public void setStyleFont(String propName, String val) {
    setProperty(propName, val);
    
    Set<Style> styles = getStyles(propName);
    
    if (styles != null) {
      for (Style s : styles) {
        s.setFont(val);
      }
    }
  }
  
}
