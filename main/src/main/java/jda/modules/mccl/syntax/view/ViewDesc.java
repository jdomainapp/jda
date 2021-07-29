package jda.modules.mccl.syntax.view;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.mosa.view.assets.layout.BasicTwoColumnLayoutBuilder;
import jda.mosa.view.assets.panels.DefaultPanel;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface ViewDesc {
  /**
   * Whether or not view descriptor is being used
   * 
   * <b>Default: <tt>true</tt>
   * @version 
   * - 3.1: ignore this property
   * @deprecated as of version 3.1
   */
  public boolean on() default true;
  
  /**
   * The title of the this that is displayed to the user
   */
  public String formTitle() default CommonConstants.EmptyString;
  
  /**
   * The user-friendly string literal of the domain class name (whose objects are displayed 
   * by this object form.). It should be written in the default language of the application.
   * This label is used in the message dialogs that are displayed to the user. 
   * 
   * <p>Default: {@link CommonConstants#NullString}
   * @version 2.7.3
   */
  public String domainClassLabel() default CommonConstants.NullString;
  
  public String imageIcon() default CommonConstants.NullString;
  
  public Class view() default Null.class;

  public RegionType viewType() default RegionType.Null;
  

  /**
   * @effects 
   *  whether or not this view is editable, i.e. users are allowed to edit fields contained 
   *  on this view. 
   *  <p>Typically, this must be set to <tt>false</tt> if the domain class associated to this view 
   *  (specified by {@link #domainClassLabel()}) has <tt>DClass.mutable=false</tt> 
   *  
   *  <br> Default: <tt>true</tt>
   * @version 5.2 
   */
  boolean editable() default true;
  
  public RegionName parent() default RegionName.Null;
  
  public RegionName[] children() default {}; 
  
  public RegionName[] excludeComponents() default {}; 

  public StyleName style() default StyleName.Null;

  public Class topContainerType() default DefaultPanel.class;

  /** x-coordinate of top-left corner of the GUI that takes values in the following ranges:
   * <ul>
   *  <li> 0.5: middle of the x-axis
   *  <li> (0,1] \ {0.5}: <b>relative</b> to the top-left of the display area (normally the desktop)
   *  <li> (1,inf): <b>absolute</tt> from the top-left of the display area 
   * </ul>
   * 
   * <p>Default: {@link MCCLConstants#DEFAULT_TOP_X}
   * */
  public double topX() default MCCLConstants.DEFAULT_TOP_X;

  /** y-coordinate of top-left corner of the GUI that takes values in the following ranges:
   * <ul>
   *  <li> 0.5: middle of the y-axis
   *  <li> (0,1] \ {0.5}: <b>relative</b> to the top-left of the display area (normally the desktop)
   *  <li> (1,inf): <b>absolute</tt> from the top-left of the display area 
   * </ul>
   * 
   * <p>Default: {@link MCCLConstants#DEFAULT_TOP_Y}
   * */
  public double topY() default MCCLConstants.DEFAULT_TOP_Y;

  /** 
   * the absolute height of the GUI
   *  
   *  {@link MCCLConstants#DEFAULT_HEIGHT}
   */
  public int height() default MCCLConstants.DEFAULT_HEIGHT;
  
  /** 
   * the absolute width of the GUI
   * 
   * <p>Default: {@link MCCLConstants#DEFAULT_WIDTH}
   */
  public int width() default MCCLConstants.DEFAULT_WIDTH;

  /** 
   * the relative height of the GUI, <b>must be</b> in (0,1]
   *  
   *  {@link MCCLConstants#DEFAULT_HEIGHT_RATIO}
   */
  public float heightRatio() default MCCLConstants.DEFAULT_HEIGHT_RATIO;
  
  /** 
   * the relative width of the GUI: , <b>must be</b> in (0,1]
   * 
   * <p>Default: {@link MCCLConstants#DEFAULT_WIDTH_RATIO}
   */
  public float widthRatio() default MCCLConstants.DEFAULT_WIDTH_RATIO;

  /**
   * whether or not this gui is resizable 
   * 
   * <br>Default: <tt>true</tt>
   */
  public boolean resizable() default true;

  /**
   * whether or not this gui is relocatable (i.e. its location can be changed) 
   * 
   * <br>Default: <tt>true</tt>
   */
  public boolean relocatable() default true;

  /**
   * The name of the icon file (relative to the application's images directory) that is used 
   * to display next to the form title
   * <br>Default {@link MCCLConstants#DEFAULT_FORM_ICON} 
   */
  public String formTitleIcon() default MCCLConstants.DEFAULT_FORM_ICON;

  /**
   * the layout builder class that is used to layout the components contained in this
   * 
   * <br>Default {@link MCCLConstants.DEFAULT_LAYOUT_BUILDER}
   * 
   * @version 2.7.4
   */
  Class layoutBuilderType() default BasicTwoColumnLayoutBuilder.class;

  /**
   * The additional properties that are associated with this
   * 
   * <br>Default: <tt>{}</tt>
   * 
   *  @version 3.0
   */
  PropertyDesc[] props() default {};

//  /**
//   * @effects 
//   * The position of the side panel, which is one of the constants specified by {@link AlignmentX}.
//   * If not specified (which is the default) then the side panel is not used.
//   * 
//   * <p>Default: {@link AlignmentX#Nil}
//   * 
//   * @version 5.2
//   */
//  public AlignmentX sidePanel() default AlignmentX.Nil;
}
