package jda.modules.mccl.syntax.view;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.Select;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentY;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.mosa.view.assets.layout.BasicTwoColumnLayoutBuilder;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD, ANNOTATION_TYPE})
@Documented
@Repeatable(AttributeDescs.class)
public @interface AttributeDesc {
  
  //public static final Class Null_Type = Null.class;
  
  //public static final String Null = "\u0000";
  /**
   * The <b>name</b> of the view field defined by this. This is <i>not</i> for defining 
   * the view fields in a module. It is <i>only</i> used in custom configuration 
   * of the descendant modules in a containment tree. 
   * 
   * <p>Default: {@link CommonConstants#NullString} (i.e. not used).
   * 
   * @version 5.1 
   */
  public String id() default CommonConstants.NullString;

  /***
   * The text that will be used to label this field
   * Default: <tt>{@link CommonConstants#EmptyString}</tt>
   */
  public String label() default CommonConstants.EmptyString;// Null;
  
  /** 
   * a {@link Select} annotation which describes the domain field 
   * of another domain class to which this view refers.
   * Default: @Select() */
  public Select ref() default @Select();
  
  /**
   * The GUI component class that will be used to display the value of this field
   * on a GUI. This may differ from {@link #printConfig()} which describes the component that is responsible
   * for displaying the value of this field on printing. 
   * 
   * <p>Default: {@link MCCLConstants#DEFAULT_DISPLAY_CLASS}
   */
  public Class type() default Null.class;

  /**
   * <b>ONLY</b> applicable to container-typed attribute. 
   * 
   * <br>The layout builder class that is used to layout the components contained in this
   * 
   * <br>Default {@link MCCLConstants.DEFAULT_LAYOUT_BUILDER}
   * 
   * @version 2.7.4
   */
  Class layoutBuilderType() default BasicTwoColumnLayoutBuilder.class;
  
  /**
   * The display width of the GUI component object of this field
   * Default: {@link MCCLConstants#DEFAULT_FIELD_WIDTH}
   */
  public int width() default MCCLConstants.DEFAULT_FIELD_WIDTH; //-1;

  /**
   * The display height of the GUI component object of this field
   * Default: {@link MCCLConstants#DEFAULT_FIELD_HEIGHT}
   */
  public int height() default MCCLConstants.DEFAULT_FIELD_HEIGHT; //-1;
  
  /**
   * Specifies the horizontal alignment of the value of this field on display
   * 
   * <p>Default: {@link MCCLConstants.AlignmentX#Left}
   * @version 2.7.2
   */
  public AlignmentX alignX() default AlignmentX.Left;
  
  /**
   * Specifies the vertical alignment of the value of this field on display
   * 
   * <p>Default: {@link MCCLConstants.AlignmentY#Top}
   * @version 2.7.2
   */
  public AlignmentY alignY() default AlignmentY.Top;
  
  /**
   * The name of the display style that is applied to the label of this field
   * Default: StyleName.Null
   */
  public StyleName styleLabel() default StyleName.Null; 

  /**
   * The name of the display style that is applied to the GUI component 
   * of this field
   * Default: StyleName.Null
   */
  public StyleName styleField() default StyleName.Null; 
  
  /**
   * <b>Note</b>: This attribute is NOT currently being used for data field!!!
   * 
   * <p>true if the associated component is a listener for application state events. 
   * false if otherwise.
   * <br>Default: false
   */
  public boolean isStateListener() default false;
  
  /**
   *   This is used together with {@link ControllerDesc#isDataFieldStateListener()} 
   *   of the module containing this attribute to signify that the 
   *   the data field associated to this attribute will be an event source, whose 
   *   (user's action) events will be listened to and handled by the module's controller
   *   
   *   <p><b>NOTE:</b> ONLY use this property for <i>special</i> data fields 
   *   (e.g. non-editable or image) whose editing events cannot be handled in the usual way.
   *   
   *   <p>This property differs from {@link #isStateListener()} in that it makes
   *   the associated data field the event source, while the other property makes it an 
   *   event handler. Also the event types under these cases also differ.
   *   
   *   <p>Default: <tt>false</tt>
   */
  public boolean isStateEventSource() default false;
  
  /**
   * this overrides the mutable() value fields of the <tt>DomainConstraint</tt> 
   * of the domain attribute thats of the domain class associated to this module.
   * <tt>true</tt> if the associated view components are editable on the GUI that uses this view 
   * </tt>false</tt> if otherwise.
   * Default: <tt>true</tt>
   */
  public boolean editable() default true;

  /**
   * Whether or not this attribute is visible on the view of the primary module.
   * <br>When <tt>false</tt> <b>the attribute is still displayed but hidden from the user.</b>
   * <p>To completely exclude the attribute from the view, define a property (using {@link #props()}) named {@link PropertyName#view_objectForm_dataField_visible}.
   * 
   * <p>Default: <tt>true</tt>
   */
  boolean isVisible() default true;
  
  /**
   * Applies only to container-type attributes. 
   * 
   * <p>Specifies the behaviour of the child controller that is responsible for managing the objects of the referenced 
   * domain type that are displayed on the sub-container of this attribute.
   * 
   * <p>A similar specification is used in {@link ModuleDescriptor}, but there the behaviour is specified for the 
   * primary (root) data controller of the referenced domain type. 
   * 
   * <p>Default: {@link ControllerDesc()}
   * @version 2.6.4.b
   */
  public ControllerDesc controllerDesc() default @ControllerDesc();

  /**
   * Similar to {@link #controllerDesc()}, this applies only to container-type attributes. 
   * 
   * <p>Specifies the properties of the referenced domain class of the objects that are displayed on the sub-container of this attribute.
   * 
   * <p>Default: {@link ModelDesc()}
   * @version 2.7.2
   */
  ModelDesc modelDesc() default @ModelDesc();

  /**
   * This applies only to bounded attribute. If <tt>true</tt>,
   * the bounded value is to be displayed together with the object id (Oid).
   * <br>Default: <tt>false</tt> (i.e. the bounded value is displayed without the Oid)
   */
  public boolean displayOidWithBoundValue() default false;

  /**
   * This applies only to bounded attribute. If <tt>true</tt>,
   * the bounded value is to be loaded together with the object id (Oid).
   * <br>Default: <tt>false</tt> (i.e. the bounded value is loaded without the Oid)
   * 
   * <p>Note: this property must be set to <tt>true</tt> if property 
   * {@link #displayOidWithBoundValue()} is set to <tt>true</tt> (however, 
   * it may be set either to <tt>true</tt> OR <tt>false</tt> if otherwise)  
   */
  public boolean loadOidWithBoundValue() default false;

  /**
   * This property is typically used for adding section labels on an object form.<br>
   * 
   * Whether or not only the label of this field is displayed on the object form.
   * 
   * <br>Default: <tt>false</tt> (i.e. both label and data field are displayed)
   * @version 2.7.4
   */
  public boolean labelOnly() default false;

  /**
   * The additional properties that are associated with this
   * 
   * <br>Default: <tt>{}</tt>
   * 
   *  @version 3.0
   */
  PropertyDesc[] props() default {};

  /**
   * UNUSED
   * 
   * Applies only to container-type attribute.
   * 
   * Default: {@link MetaConstants#NullType}
   * @deprecated UNUSED
   */
  /*v2.7.2: not used */
//  public Class dataController() default Null.class;

  /**
   * Used for bounded field only.
   * True of the bounded values are loaded when this field is displayed. 
   * False if otherwise.
   * Default: false
   * @deprecated to be removed
   */
  /*v2.7.2: not used */
//  public boolean loadBoundValues() default false;
}
