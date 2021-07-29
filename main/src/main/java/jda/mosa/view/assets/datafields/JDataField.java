package jda.mosa.view.assets.datafields;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.InfoCode;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JDataField.DataCellEditor;
import jda.mosa.view.assets.tables.UpdatableCellEditor;
import jda.mosa.view.assets.util.function.value.DataFieldValueFunction;
import jda.util.events.ValueChangeListener;

/**
 * A sub-class of <code>JPanel</code> to represent a data field, whose
 * presentation varies based on the configuration on the its data values.
 * 
 * <p>
 * A first basic usage of <code>JDataField</code> is to display an
 * auto-validated text box for a basic attribute of a class, so that the values
 * entered into this text box by the user are automatically validated against
 * the <code>DomainConstraint</code> of this attribute. A small red error icon
 * is displayed next to the text box if its value failed the validation.
 * 
 * To achieve this, use the following code:
 * 
 * <pre>
 * // define the domain constraint
 * class MyClass {
 *   &#064;DomainConstraint(name = &quot;year&quot;, type = Type.Integer, length = 4, min = 1990, max = 9999)
 *   private Integer year;
 * }
 * 
 * // ...
 * // get the domain constraint object
 * DomainConstraint dcYear = MyClass.class.getDeclaredField(&quot;year&quot;).getAnnotation(
 *     DomainConstraint.class);
 * // create the data field
 * JDataField df = new JDataField(dcYear);
 * </pre>
 * 
 * In the above code, an initial value can also be specified using the
 * constructor {@see #JDataField(Object, DomainConstraint)}. Further, if the
 * text field is only used for display value, not for editing then use this
 * constructor instead: {@see #JDataField(Object, DomainConstraint,boolean)},
 * passing in <code>false</code> as the third argument.
 * 
 * It is likely that <code>MyClass</code> is a different class from the class
 * that uses <code>JDataField</code>. In which case, use the
 * <code>DomainSchema</code> class to extract the <code>DomainConstraint</code>
 * object of the year field, as follows:
 * 
 * <pre>
 * DomainConstraint dcYear = schema.getDomainConstraint(MyClass.class, &quot;year&quot;);
 * </pre>
 * 
 * <p>
 * Another basic usage of <code>JDataField</code> is to display a pre-defined
 * list of values from which the user can choose one. In the above example, if
 * we dont want the user to type in a year value, only to select from a list of
 * pre-defined years, then use the following code:
 * 
 * <pre>
 * // define the domain constraint (as above)
 * // ...
 * // get the domain constraint object (as above)
 * // ...
 * // create the data field
 * List&lt;Integer&gt; years = new ArrayList&lt;Integer&gt;();
 * Collections.addAll(years, new Integer[] { 1990, 1991, 1992 }); // list of
 * // allowed values
 * boolean editable = false;
 * Integer initVal = years.get(0);
 * JDataField df = new JDataField(initVal, years, dcYear, editable);
 * </pre>
 * 
 * In the above code, if we want to allow the user to enter new year values as
 * well then we set <code>editable = true</code>.
 * 
 * A variation of the above usage is that in which the list of pre-defined
 * values is editable but is initially empty. We want the user to manually type
 * in the values or to set a new non-empty list later on. The first case can be
 * achieved by changing the above code so that the value list is initialised but
 * empty, <code>editable = true</code>, and <code>initVal = null</code>. The
 * second case uses this same data field initialise code as the first case. To
 * set up the data field to use a new list of values, use the
 * <code>setValues</code> method of the data field object:
 * 
 * <pre>
 * List<Integer> newList = ... // creates a new non-empty list
 * df.setValues(newList);
 * </pre>
 * 
 * <p>
 * A <code>JDataField</code> can be configured to be bound to an attribute of a
 * domain class, so that its values can be extracted from this attribute of a
 * <code>List</code> of domain objects of that class. For example, if data field
 * is bound to the attribute <code>Student.id</code>, then it can be used to
 * display a list of ids of <code>Student</code> objects.
 * 
 * <p>
 * To achieve the above, the <code>Student.id</code> field must be annotated
 * with a <code>DomainConstraint</code>, whose type is defined using one of the
 * built-in types in the enum <code>DomainConstraint.Type</code>. For the sake
 * of the example, suppose that this is set to
 * <code>DomainConstraint.Type.String</code> (i.e. <code>Student.id</code> has a
 * string-type). Next, define a getter method <code>Student.getId()</code> for
 * this attribute.
 * 
 * Then use the following code to create the above data field:
 * 
 * <pre>
 *    List<Student> students = ... ; // a List of student objects
 *    // get the domain constraint of the bound attribute
 *    DomainConstraint boundConstraint = schema.getDomainConstraint(Student.class, "id");
 *    // create the data field
 *    Student initVal = students.get(0);
 *    JDataField df = new JDataField(schema, initId, students, boundConstraint);
 * </pre>
 * 
 * 
 * @author dmle
 * 
 */
public abstract class JDataField<C> extends JPanel 
{
  /**the data container that contains this field */
  private JDataContainer parent;
  
  /**(cached) ancestor Window-typed {@link Container} of this (if any)
   * @version 3.2 
   */
  private Container ancestorWindow;
  
  // the domain schema
  //protected DomainSchema schema;
  
  // v5.1c: changed to private
  private DataValidator validator;
  
  // the configuration object
  private Configuration config;
  
  // v2.7: support for field-specific view config
  private RegionDataField dataFieldConfig;
  
  // v2.7.3: derived from dataFieldConfig 
  private Dimension configuredDim;

  private C initVal; // the initial value (if any)

  /***
   * the initial value function that is used to support complex run-time value computation. 
   * <p>Either this or {@link #initVal} is used, not both!
   * @version 3.2c  
   */
  private DataFieldValueFunction initValFunction;  
  
  // v5.1c: changed to private
  private C value;
  
  // v5.1c: changed to private
  private DAttr dconstraint;
  
  // v5.1c: changed to private
  private boolean editable;
  
  // v5.1c: changed to private
  private boolean autoValidation;
  
  // whether or not the value has been validated
  // v5.1c: changed to private
  private boolean validated;

  // the display component
  // v5.1c: changed to private
  private JComponent display;
  
  // the indicator label
  private JLabel labelIndi;

  // a cell editor wrapper for this field
  // v5.1c: changed to private
  private DataCellEditor dataCellEditor;

  // listeners for state change events
  private List<ChangeListener> listeners;
  
  //v2.7:  the display style
  private Style style;
  
  // v2.7.4
  private ChangeEvent valueChangeEvent;
  private ValueChangeListener valueListener;

  // constants
  /**
   * the default display width of this field 
   */
  protected static final int DEFAULT_FIELD_WIDTH = 10;  // v2.7

  public final static int MAX_DISPLAYABLE_TEXT_WIDTH = 30;
  protected final static int MAX_DISPLAYABLE_TEXT_HEIGHT = 5;
  protected final static int DEFAULT_TEXT_WIDTH = 15;
  
  protected static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
  
  private static ImageIcon BLANK_ICON;
  private static ImageIcon ERROR_ICON;

  /**
   * Added support for cursors that will be used for the ancestor window in special cases 
   * 
   * @version 3.2
   */
  protected static Cursor DEFAULT_CURSOR;
  protected static Cursor HAND_CURSOR;
  
  public final static Object Nil = "";

  protected final static boolean debug = Toolkit.getDebug(JDataField.class);

  // //// constructor methods /////
  /**
   * The complete constructor
   */
  protected JDataField(DataValidator validator, 
        Configuration config, 
        C val, 
        DAttr domainConstraint, 
        boolean editable, 
        boolean autoValidation) throws ConstraintViolationException {
    super();

    // the components
    this.validator = validator;
    this.config = config;
    /*v2.7.4: 
     * let value = null at the start, will be set to initVal later on (e.g. by reset())
     * v3.0: the reson is because a data field is meant to be used in an object form
     *  for a number of purposes, not just for getting input from the user;
     *  the value of this field depends on the user's action. Therefore, initially 
     *  the data field's value is null. When in input mode, value is set to the initVal 
     *  (if specified). In other modes, value may be set to null again.
     *    
    this.value = val;
    */
    this.value=null;
    this.initVal = val;
    this.dconstraint = domainConstraint;
    this.editable = editable;
    this.autoValidation = autoValidation;
    // even with auto-validation, the data value may not have been
    // auto-validated
    // e.g. due to the user not pressing the enter key after entering the input
    // thus, we need this additional attribute (initially it is true)
    this.validated = false;

    // initialise the GUI resources
    initGUI();
    
    listeners = new ArrayList<ChangeListener>();
    
    // set up the display component
    Type type = null;
    if (dconstraint != null) {
      type = dconstraint.type();

      setName(dconstraint.name()); // the name of this is the name of the domain
                                   // constraint
    }

    //v2.7.4: only create label indicator of domain constriant is specified
    if (dconstraint != null)
      labelIndi = new JLabel(BLANK_ICON);
    
    // labelIndi.setBorder(BorderFactory.createLineBorder(Color.RED,4));
  }

  private void initGUI() {
    // load icons
    if (ERROR_ICON == null) { // v3.2: added this check because the icons are static fields
      String ecFile = "exclamation16.png";
      String bcFile = "blank16.png";
      ERROR_ICON = GUIToolkit.getImageIcon(ecFile, "error");
      BLANK_ICON = GUIToolkit.getImageIcon(bcFile, "blank");  
    }
    
    // v3.2: added support for cursors
    if (DEFAULT_CURSOR == null) { // added this check because the cursors are static fields
      DEFAULT_CURSOR = Cursor.getDefaultCursor();
      HAND_CURSOR = GUIToolkit.getCursor("handcursor.gif", new Point(0, 0),
          "hand");
    }
    
    // v3.2: make background style of this transparent
    setOpaque(false);
  }
  
  /**
   * Invoke this method after invoking all the setter methods for the configuration settings. 
   * 
   * @requires 
   *  all setter methods for configuration settings have been invoked
   *  
   * @effects 
   *  initialise the display component and configure it
   */
  public void initField() {
    
    DataFieldInputHelper tfh = new DataFieldInputHelper();

    JComponent actualDisplay = createDisplayComponent(tfh);

    // sets up this layout a bit
    this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

    /*v3.0: moved to initLayout()
    // uncomment the followings to use the basic flowlayout
    FlowLayout layout = (FlowLayout) getLayout();
    layout.setAlignment(FlowLayout.LEFT); // align display component to the left
    layout.setVgap(0);
    layout.setHgap(0);
    // add components to this
    add(actualDisplay);
    
    if (labelIndi != null) // v2.7.4: added this check
      add(labelIndi);
    */
    initLayout(actualDisplay);
    
    /*
     * uncomment the followings to use GridBagLayout 
     * 
     * // use grid layout
     * GridBagLayout layout = new GridBagLayout(); setLayout(layout);
     * GridBagConstraints c = new GridBagConstraints();
     * 
     * // display component: (0,0), resizable both horizontally and vertically to fill all extra spaces
     * c.weightx = 1; // all extra horiz.space for display component 
     * c.weighty = 1; // all extra vertical space 
     * c.fill = GridBagConstraints.BOTH; 
     * c.gridx = 0; c.gridy = 0; 
     * add(actualDisplay, c);
     * 
     * // label indicator: (1,0) & only resizable vertically to fill all extra space
     * c.fill = GridBagConstraints.VERTICAL; // only fills vertically 
     * c.weightx = 0; // no extra horiz. space for label 
     * c.weighty = 1; // all extra vertical space 
     * c.gridx = 1; c.gridy = 0; 
     * add(labelIndi, c);
     */
  }
  
//  /**
//   * Creates an auto-validated text field constrained by the domain constraint
//   * <code>dc</code>.
//   * 
//   * <p>
//   * This is a short cut for
//   * {@link #JDataField(DomainSchema, DomainConstraint, boolean)}, with the last
//   * argument is set to <code>true</code>.
//   */
//  protected JDataField(DomainSchema schema, DomainConstraint dc) {
//    this(schema, dc, true);
//  }

//  /**
//   * Creates a text field constrained by the domain constraint <code>dc</code>
//   * and auto-validation is determined by <code>autoValidation</code>.
//   */
//  protected JDataField(DomainSchema schema, DomainConstraint dc,
//      boolean autoValidation) {
//    this(schema, null, dc, true, autoValidation);
//  }
//
//  /**
//   * Creates a normal text field (with no constraints).
//   */
//  protected JDataField(DomainSchema schema, boolean editable) {
//    this(schema, null, null, editable, true);
//  }

//  protected JDataField(DomainSchema schema, C val, boolean editable) {
//    this(schema, val, null, editable, true);
//  }
  
  /**
   * This is invoked as part of {@link #initField()} to initialise the display of this.
   * 
   * <p>The default implementation uses {@link FlowLayout} which honors the prefered sizes
   * of both the display component and the label indicator.
   * 
   * <p>Special data fields which need a different behaviour should override this method 
   * to use a different layout. 
   * 
   * @effects 
   *  creates this.layout suitable for <tt>displayComp</tt> and label indicator {@link #labelIndi}
   * @version 3.0 
   */
  protected void initLayout(JComponent displayComp) {
    // use a basic flowlayout containing (displayComp, labelIndi)
    // the preferred dimensions of both are honored, even when the object form containing this field
    // is resized.
    FlowLayout layout = (FlowLayout) getLayout();
    layout.setAlignment(FlowLayout.LEFT); // align display component to the left
    layout.setVgap(0);
    layout.setHgap(0);
    // add components to this
    add(displayComp);
    
    if (labelIndi != null) // v2.7.4: added this check
      add(labelIndi);    
  }

  /**
   * This is an abstract method, which must be implemented by the sub-classes of this class. 
   * It is invoked by the constructor to create the main display component. 
   * 
   * @effects 
   *  initialise <tt>display</tt> and returns the actual component (which may be different from <tt>display</tt>) 
   *  that will be displayed on <tt>this</tt>.
   */
  protected abstract JComponent createDisplayComponent(DataFieldInputHelper tfh); 

  /**
   * This method is primarily used for read-only data field (e.g. label, etc.)  
   * 
   * @effects 
   *  call {@link #getValue()} to update {@link #value} 
   *  if this.value is null
   *    return true
   *  else
   *    return false
   */
  public boolean isNullValue() {
    return (getValue()==null);
  }

  /**
   * @modifies <tt>this</tt>
   * @effects 
   *  if validated = false
   *    validate the display value
   *    if succeeds
   *      set <tt>value</tt> to the validated value
   *      return <tt>value</tt>
   *    else
   *      throw ConstraintViolationException
   *  else
   *    return <tt>value</tt>
   *    
   * @version 
   * - 3.2: changed return type from C to Object
   */
  public abstract Object getValue() throws ConstraintViolationException;
  
  /**
   * @modifies <tt>this</tt>
   * @effects 
   *  if raw = true
   *    return {@link #getRawValue()}
   *    throws NotFoundException if value is not one of the allowed values 
   *  else
   *    return {@link #getValue()}
   *    throws ConstraintViolationException if value violates domain-specific constraint
   * @version 
   * - 3.2: changed return type from C to Object
   */
  public Object getValue(boolean raw) throws NotFoundException, ConstraintViolationException {
    if (raw) {
      // get the raw value first
      try {
        Object rawValue = getRawValue();
        return rawValue;
      } catch (NotFoundException e) {
        // raw value is not one of the allowed values
        displayError(e.getCode(), e// null
            , true, false, false);
        throw e;
      }
    } else {
      // return actual value
      return getValue();
    }
  }
  
  /**
   * This method is used to provide access to the currently displayed object, without having to worry about 
   * whether or not it is the intended one for the applicatioin. It <b>SHOULD ONLY BE USED</b> for the purpose of displaying 
   * context-specific information about the display object so that the user can better decide whether to 
   * use it.  
   * 
   * <p>The actual value of this field must still be obtained via {@link #getValue()}. 
   * 
   * <p>The default behaviour of this method is to return {@link #getValue()}, printing out any error 
   * that occured. Sub-types should override this method to provide their own method of obtaining the raw value from 
   * the actual display component. 
   * 
   * @effects 
   *  return the non-validated value of this or <tt>null</tt> if no value is specified.
   *  
   *  <p>Throws NotFoundException if the raw value is not one of the allowed values.
   * @version 
   * - 3.2: changed return type from C to Object
   */
  protected Object getRawValue() throws NotFoundException {
    Object val;
    try {
      val = getValue();
    } catch (ConstraintViolationException e) {
      val = null;
      displayError(e.getCode(), e// e.getMessage()
          , false, false, true);
    }
    
    return val;
  }

  /**
   * @effects 
   *  if the initial value of this is specified
   *    return it
   *  else
   *    return <tt>null</tt.
   * @version 
   * - 3.0: created <br>
   * - v3.2: improved to use {@link #initValFunction} if specified
   */
  public final C getInitValue() {
    // v3.2: return initVal;
    if (initValFunction != null) {
      try {
        return (C) initValFunction.eval();
      } catch (ApplicationRuntimeException e) {
        logError(e);
        // something wrong (ignored)
        return null;
      }
    } else {
      return initVal;
    }
  }

  /**
   * @effects 
   *  set {@link #initValFunction} to <tt>initValFunction</tt>
   * @version 3.2c
   */
  public void setInitValFunction(DataFieldValueFunction initValFunction) {
    this.initValFunction = initValFunction;
  }

  /**
   * <b>IMPORTANT</b>: Only use this method within this or sub-types and when it is absolutely
   * sure that the argument is valid. 
   * 
   * <p>Application code must use {@link #setValue(Object)} to change this.value.
   * 
   * @effects 
   *  set this.value = val
   * @version 3.0
   */
  protected final void setValueDirectly(C val) {
    value = val;
  }
  
  /**
   * A short-cut for {@link #setValueDirectly(Object)} when argument is <tt>null</tt>.
   * 
   * @version 5.1c 
   */
  protected final void setNullValueDirectly() {
    setValueDirectly(null);
  }
  
  /**
   * @effect
   *  return this.value
   * @version 3.1
   */
  protected final C getValueDirectly() {
    return value;
  }
  
  /**
   * @modifies <tt>this</tt>
   * @requires <tt>val != null /\ val is valid</tt>
   * @effects <pre>
   *            if val != null
   *              sets this.value = val
   *              updates <tt>this.display</tt>
   *            else
   *              reset
   * </pre>
   */
  public abstract void setValue(C val);

  /**
   * This method is invoked only by the sub-types when the values to be set are known
   * to be valid.  
   *  
   * @requires <tt>val != null /\ val is validated</tt>
   * @effects <pre>
   *            invokes setValue(val)
   *            sets this.validated = true
   *          </pre>  
   */
  protected void setValidatedValue(C val) {
    setValue(val);
    setIsValidated(true);
//    if (!validated)
//      validated = true;
  }

  /**
   * @effects
   *  sets validated = tf
   */
  protected void setIsValidated(boolean tf) {
    if (validated != tf)
      validated = tf;
  }
  
  /**
   * @effects 
   *  if this supports formatted value 
   *    return true
   *  else 
   *    return false
   */
  public boolean isSupportValueFormatting() {
    return false;
  }
  
  /**
   * This method is paired with {@link #parseFormattedValue(String)}
   * 
   * @requires
   *  value != null
   * @effects 
   *  if this supports value formatting
   *    format the value of this field and display it
   *  else
   *    do nothing 
   */
  protected void displayFormattedValue() {
    // empty (sub-classes that support formatting should override if needed)
  }

  /**
   * This method is paired with {@link #displayFormattedValue()}
   * 
   * @effects 
   *  if this supports value formatting
   *    convert the formatted value <tt>val</tt> into the actual value object
   *    and return it
   *  else
   *    return val 
   */
  protected Object parseFormattedValue(String val) throws ParseException {
    // sub-classes that support formatting should override if needed
    return val;
  }

  /**
   * @requires val !=null && this supports value formatting 
   * (i.e. {@link #isSupportValueFormatting()} = true))
   * @effects 
   *  if this supports value formatting
   *    return the formatted value of <tt>val</tt>
   *  else
   *    throws NotPossibleException
   */
  public String getFormattedValue(Object val) throws NotPossibleException {
    throw new NotPossibleException(NotPossibleException.Code.CANNOT_FORMAT_VALUE, 
        new Object[] {this, val, null}); 
  }

  /**
   * @effects 
   *  if this represents an id attribute
   *    return true
   *  else
   *    return false
   */
  public boolean isId() {
    return (dconstraint != null && dconstraint.id());
  }
  
  /**
   * @effects 
   *  if this.dconstraint is not null and it specifies a derived field
   *    return true
   *  else
   *    return false
   *  @version 2.7.2
   */
  public boolean isDerived() {
    return (dconstraint != null && dconstraint.derivedFrom().length > 0);
  }
  
  /**
   * @effects 
   *  if this.domainConstraint is not null 
   *    return true
   *  else
   *    return false
   * @version 2.7.4
   */
  public boolean isConstrained() {
    return dconstraint != null;
  }


  /**
   * @effects 
   *  if this field is configured with a {@link #validator} to validate value
   *    return <tt>true</tt>  
   *  else
   *    return <tt>false</tt>
   * @version 3.2
   */
  protected boolean isValueValidable() {
    return validator != null;
  }

  /**
   * @effects 
   *  if {@link #validated} = true
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2
   */
  protected boolean isValidated() {
    return validated;
  }

  /**
   * @effects 
   *  if this is configured to auto-validate values as user enter them in this
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * @version 3.2
   */
  protected boolean isAutoValidation() {
    return autoValidation;
  }
  

  /**
   *  For certain type of data fields (e.g. JComboField, JListField) the mouse event occurs at the same time as 
   *  value changed (e.g when the user clicks on a combo item to change it). However, the mouse click event 
   *  either 
   *  is consumed by the field (e.g. b/c the event source is a value button component of the data field
   *  which is consumed by this field) OR
   *  is this event is detected but its handling logic is supposed to be subsumed by that of the value change event handler.
   *  
   *  <p>An example of the second case is <tt>JListField</tt>, which does not consume the mouse-event but this event 
   * is fired concurrently with value changed. The mouse click event handler
   * is performed after the value-change event handler, causing the owner data container's state is overriden by 
   * the OnFocus state, instead of being set to Editing. This in turn causes problems in updating the tool bar buttons.
   * 
   * @effects 
   *  if in this type of data field mouse click event is consumable by value change event
   *    return true
   *  else 
   *    return false 
   *  
   * @version 3.2 
   */
  public boolean isMouseClickConsumableByValueChanged() {
    return false;
  }

  /**
   * @effects 
   *  return {@link #validator}
   * @version 3.2
   */
  protected DataValidator getValidator() {
    return validator;
  }

  /**
   * @effects if <code>value</code> is NOT valid w.r.t the domain constraint
   *              <code>dconstraint</code>
   *              set validated = false 
   *              throw <code>ConstraintViolationException</code>.
   *           else
   *            set validated = true
   *            return the validated value
   * 
   * @modifies <tt>this</tt>
   */
  protected C validateValue(Object v, DAttr dconstraint)
      throws ConstraintViolationException {
    
    try {
      C validatedVal = null;
      
      if (validator != null) {
        validatedVal = (C) validator.validateDomainValue(dconstraint, v);
      } else {
        // no validator, use the value
        validatedVal = (C) v;
      }
      
      updateGUI(false);
      validated = true;
      return validatedVal;
    } catch (ConstraintViolationException e) {
      validated = false;
      displayError(e.getCode(), e// e.getMessage()
          , true, false, false);
      throw e;
    }
  }

  /**
   * @effects returns the domain constraint object
   */
  public DAttr getDomainConstraint() {
    return dconstraint;
  }

  /**
   * @effects 
   *  return the name of the domain attribute whose value is rendered by this
   * @version 3.0
   */
  protected Object getAttributeName() {
    String attribName = null;
    
    if (dconstraint != null)
      attribName = dconstraint.name();

    return attribName;
  }
  
  /**
   * @effects 
   *  return the domain-specified field width or {@link #DEFAULT_FIELD_WIDTH} if non is specified.
   */
  protected int getDomainFieldWidth() {
    int length = DEFAULT_FIELD_WIDTH;
    if (dconstraint != null && dconstraint.length() > 0) {
      length = dconstraint.length();
    }
    
    return length;
  }
  
  /**
   * This method allows applications to differentiate between single-valued and multi-valued 
   * data fields. A multi-valued data field (e.g. list field) is a data field whose {@link #value} is set to a 
   * {@link Collection} of values.  
   * 
   * @effects 
   *  if this is a multi-valued data field (i.e. {@link #dconstraint}<tt>.type.isCollection() = true</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *      
   * @version 3.2
   */
  public final boolean isMultiValued() {
    return (dconstraint != null && dconstraint.type().isMultiValued());
  }
  
  /**
   * @effects 
   *  if this.{@link #dataFieldConfig} is specified with width and height
   *    return these as Dimension
   *  else
   *    return <tt>null</tt>
   *  @version 2.7.3
   */
  protected Dimension getConfiguredDimension() {
    if (configuredDim == null) {
      RegionDataField df = getDataFieldConfiguration();
      if (df != null && df.isSizeConfigured()) {
        int width, height;
        width = df.getWidth();
        height = df.getHeight();
        configuredDim = new Dimension(width, height);
      }  
    }
    return configuredDim;
  }
  
  /**
   * @effects returns the actual <code>display</code> component that <code>this</code>
   *          is presenting to the user. 
   *          
   *           <p>It could be a <code>JTextComponent</code> or a <code>JSpinner</code> depending 
   *           how this data field was created.
   */
  public JComponent getGUIComponent() {
    return display;
  }

  /**
   * @effects 
   *  sets {@link #display} to display
   * @version 3.2
   */
  protected final void setGUIComponent(JComponent display) {
    this.display = display;
  }

  /**
   * @effects 
   *  if {@link #display} != null
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2
   */
  protected boolean hasGUIComponent() {
    return display != null;
  }
  
  /**
   * @effects 
   *  return the label indicator of this
   *  
   * @version 3.0
   */
  protected JComponent getLabelIndicator() {
    return labelIndi;
  }

  /**
   * Overrides parent's method to enable/disable the display component as well.
   */
  @Override  
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    display.setEnabled(enabled);
  }

  /**
   * Sub-classes should override this method to set the editability of their display components.
   * The overriden code must invoke <tt>super.setEditable(state)</tt> first.
   * 
   * @effects 
   *  set this.editable=state and 
   *  change the editable state of <tt>display</tt> to <tt>state</tt>
   */
  public void setEditable(boolean state) {
    editable = state;
    
    // color the text of non-editable fields differently
    /* v5.1c: moved to method
    if (!editable) {
      display.setForeground(GUIToolkit.COLOUR_FOREGROUND_DISABLED);
    } else {
      Color fg = GUIToolkit.COLOUR_FOREGROUND;
      if (display.getForeground() != fg)
        display.setForeground(fg);
    }*/
    setEditableColor(display, state);
  }
  
  /**
   * @effects
   *  if this is editable
   *    return true
   *  else
   *    return false 
   */
  public boolean isEditable() {
    return editable;
  }
  
  /**
   * @effects
   *  return {@link #editable}.
   * @version 5.1c
   */
  public boolean getEditable() {
    //return editable;
    return isEditable();
  }

  /**
   * @effects 
   *  if {@link #display} is specified
   *    return {@link #display}.<tt>isEnabled</tt>
   *  else
   *    return false
   * @version 5.2 
   */
  public boolean isViewEnabled() {
    if (display != null) {
      if (display instanceof JTextComponent) {
        return ((JTextComponent)display).isEditable();
      } else { 
        return display.isEnabled();
      }
    } else {
      return false;
    }
  }
  
  /**
   * Sub-classes should override this method if their display component's editability is not 
   * set using either <tt>setEditable</tt> or <tt>setEnabled</tt>.
   * 
   * @effects 
   *  if {@link #display} supports <tt>setEditable(boolean)</tt> then
   *    invoke this method with <tt>tf</tt>
   *  else if {@link #display} supports <tt>setEnabled(boolean)<tt> then
   *    invoke this method with <tt>tf</tt>
   *  
   *  <p>change the appearence of {@link #display} 
   *    according to <tt>tf</tt> (without affecting {@link #editable})
   *    
   * @version 5.1c
   */
  public void setEditableView(boolean tf) throws NotPossibleException {
    // invoke setEditable on display
    if (display instanceof JTextComponent) {
      ((JTextComponent)display).setEditable(tf);
      // color the text of non-editable fields differently
      setEditableColor(display, tf);    
    } else {
      // Sub-classes need to override this method if needed.
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
//          new Object[] {display.getClass(), "setEditable", tf+""});
      boolean currState = display.isEnabled();
      
      if (tf != currState) {
        display.setEnabled(tf);
        
        // color the text of non-editable fields differently
        setEditableColor(display, tf);    
      }
    }
  }
  
  /**
   * @effects 
   *  change the appearance colors of <tt>display</tt> depending on the value of <tt>editable</tt>
   * @version 5.1c
   */
  protected void setEditableColor(JComponent display, boolean editable) {
    if (!editable) {
      display.setForeground(GUIToolkit.COLOUR_FOREGROUND_DISABLED);
    } else {
      Color fg = GUIToolkit.COLOUR_FOREGROUND;
      if (display.getForeground() != fg)
        display.setForeground(fg);
    }    
  }

  @Override
  public void setPreferredSize(Dimension size) {
    // set the display component's preferred size to size - indicator's label's size
    
    /* v2.7.4:
   int w = size.width-labelIndi.getWidth();
     */
    
    int w;
    if (labelIndi != null) // v2.7.4: added this check
      w = size.width-labelIndi.getWidth();
    else
      w = size.width;
    
    display.setPreferredSize(new Dimension(w,size.height));
    
    // set the display size of this field
    super.setPreferredSize(size);
  }
  
  /**
   * @requires 
   *  style != null
   * @effects 
   *  update the display style of the GUI component of this to use the specification defined in <tt>style</tt>
   *  and, if this contains other components, 
   *    also updates the display style of the child components (recursively). 
   */
  public void setStyle(Style style) {
    this.style = style;
    setStyle(getGUIComponent(), style);
  }
  
  /**
   * This method is used by {@link #setStyle(Style)}.
   * 
   * @requires 
   *  style != null
   * @effects 
   *  update the display style of <tt>comp</tt> to use the specification defined in <tt>style</tt>
   *  and, if <tt>comp</tt> contains other components, 
   *    also updates the display style of the child components (recursively). 
   */
  protected void setStyle(JComponent comp, Style style) {
    Font font = null;
    Color fg = null;
    Color bg = null;
    
    font = GUIToolkit.getFontValue(style.getFont()); 
    fg = GUIToolkit.getColorValue(style.getFgColor());
    bg = GUIToolkit.getColorValue(style.getBgColor());

    /*v3.1: move to method for overriding
    if (font != null) {
      comp.setFont(font);
    }
    */
    setStyleFont(comp, font);
    
    if (fg != null) {
      comp.setForeground(fg);
    }

    if (bg != null) {
      comp.setOpaque(true);
      comp.setBackground(bg);
    }
    
    if (comp instanceof JPanel) {
      Component[] children = comp.getComponents();
      /* v2.7.4: moved to method to enable overriding
      for (Component child : children) {
        if (child instanceof JComponent) {
          setStyle((JComponent) child, style);
        }
      }
      */
      setStyle(children, style);
    }
  }
  
  /**
   * @requires 
   *  font != null
   * @effects
   *  set comp.font = font
   *  
   * @version 3.1
   */
  protected void setStyleFont(JComponent comp, Font font) {
    comp.setFont(font);
  }

  /**
   * @effects 
   *  apply style <tt>style</tt> to all components in <tt>comps</tt>
   * @version 2.7.4
   */
  protected void setStyle(Component[] comps, Style style) {
    for (Component comp : comps) {
      if (comp instanceof JComponent) {
        setStyle((JComponent) comp, style);
      }
    }
  }

  /**
   * @effects 
   *  return the display style of this or <tt>null</tt> if no style was specified
   */
  public Style getStyle() {
    return style;
  }
  
  /**
   * @effects  
   *  if <tt>display</tt> is initialised
   *    return the foreground color of <tt>display</tt>
   *  else
   *    return the foreground color of the super class
   */
  public Color getForegroundColor() {
    return (display != null) ? display.getForeground() : super.getForeground();
  }
  
  /**
   * @effects  
   *  if <tt>display</tt> is initialised
   *    return the background color of <tt>display</tt>
   *  else
   *    return the background color of the super class
   */
  public Color getBackgroundColor() {
    return (display != null) ? display.getBackground() : super.getBackground();
  }
  
  /**
   * @effects 
   *  return the actual Font used for the display component of this
   */
  public Font getTextFont() {
    return (display != null) ? display.getFont() : super.getFont();
  }
  
  /**
   * @requires 
   *  f != null
   * @effects 
   *  sets the font of the GUI component of this to <tt>f</tt>
   */
  public void setTextFont(Font f) {
    getGUIComponent().setFont(f);
  }
  
  public void setParentContainer(JDataContainer container) {
    this.parent = container;
  }
  
  public JDataContainer getParentContainer() {
    return this.parent;
  }
    
  /**
   * @effects 
   *  return the application-wise <tt>Configuration</tt> object.
   */
  public Configuration getConfiguration() {
    return config;
  }
  
  /**
   * @effects
   * if field configuration is specified  
   *  return it
   * else
   *  return <tt>null</tt>
   *  
   * @version
   *  v2.7 
   */
  public RegionDataField getDataFieldConfiguration() {
    return dataFieldConfig;
  }

  /**
   * @effects 
   *  sets the data field config of this to <tt>fieldCfg</tt>
   * @version
   *  v2.7 
   */
  public void setDataFieldConfig(RegionDataField fieldCfg) {
    this.dataFieldConfig = fieldCfg;
  }

  /**
   * @effects 
   *  if dataFieldConfig is not null 
   *    return the <tt>SwingConstant</tt>'s equivalent of <tt>dataFieldConfig.alignX</tt>
   *  else
   *    return the default <tt>SwingConstant</tt> alignment X
   */
  public int getAlignX() {
    if (dataFieldConfig != null) {
      return GUIToolkit.toSwingAlignmentX(dataFieldConfig.getAlignX());
    } else {
      return SwingConstants.LEFT;
    }
  }
  
  /**
   * @effects 
   *  if dataFieldConfig is not null 
   *    return the <tt>SwingConstant</tt>'s equivalent of <tt>dataFieldConfig.alignY</tt>
   *  else
   *    return the default <tt>SwingConstant</tt> alignment Y
   */
  public int getAlignY() {
    if (dataFieldConfig != null) {
      return GUIToolkit.toSwingAlignmentY(dataFieldConfig.getAlignY());
    } else {
      return SwingConstants.TOP;
    }
  }

  /**
   * @requires {@link #display} has been initialised
   * 
   * @effects 
   *  If this supports size update for {@link #display}
   *    change the size of {@link #display} to <tt>(width, height)</tt>
   *  else 
   *    do nothing
   * @version 4.0
   */
  public void setCustomSize(int width, int height) {
    if (display == null) return;
    
    Dimension newSize = new Dimension(width, height);
    
    // needs to set both preferred size and current size for it to take effect
    display.setPreferredSize(newSize);
    display.setSize(newSize);
  }  
  
  /**
   * @effects 
   *  log error associated with <tt>e</tt> to the standard output
   *  
   * @version 3.2c
   * @see {@link #displayError(InfoCode, Throwable, boolean, boolean, boolean)}
   */
  protected void logError(Throwable e) {
    displayError(null, e, false, false, true);
  }
  
  // TODO: improve this to display mesg as a tool tip on the error label
  protected void displayError(
      // v3.2: String mesg,
      InfoCode errorCode, Throwable t, 
      boolean updateGUI, boolean withDialog, boolean console) {
    // JOptionPane.showMessageDialog(this, mesg, "Data error",
    // JOptionPane.ERROR_MESSAGE);
    // display.requestFocus();
    // show error icon
    if (parent != null && withDialog) {
      //v3.2: parent.getController().getUser().displayError(mesg, null);
      parent.getController().getUser().displayError(errorCode, t, null);
    } else if (console) {
      System.err.println(this.getClass().getSimpleName()+" - Error: " + 
          //v3.2: mesg
          t.getMessage());
    }
    
    if (updateGUI)
      updateGUI(true);
  }

  /***
   * @requires <tt>ERROR_ICON != null && BLANK_ICON != null</tt>
   * @effects if <tt>error</tt> then set the icon of <tt>this.labelIndi</tt> to 
   *          <tt>ERROR_ICON</tt> else sets it to <tt>BLANK_ICON</tt>
   */
  protected // TODO: improve this
  void updateGUI(boolean error) {
    // show error icon

    if (labelIndi != null) {  // v2.7.4: added this check
      if (error && ERROR_ICON != null) {
        if (labelIndi.getIcon() != ERROR_ICON) {
          labelIndi.setIcon(ERROR_ICON);
        }
      } else if (BLANK_ICON != null) {
        if (labelIndi.getIcon() != BLANK_ICON) {
          labelIndi.setIcon(BLANK_ICON);
        }
      }
    }
  }
  
  /**
   * @effects 
   *  if the cursor of ancestor window of this is not <tt>cursor</tt>
   *    change it to <tt>cursor</tt>
   *  else
   *    do nothing 
   * @version 3.2
   */
  protected void setAncestorWindowCursor(Cursor cursor) {
    if (ancestorWindow == null)
      ancestorWindow = GUIToolkit.getWindowAncestor(this);
    
    if (ancestorWindow != null && ancestorWindow.getCursor() != cursor) {
      ancestorWindow.setCursor(cursor);
    }
  }
  
  /**
   * This method is used to reset this field ready to take input from the user. 
   * 
   * @effects re-initialises <code>this</code> to the initial state (AND set <tt>value = initVal</tt>)
   */
  public abstract void reset();  

  /**
   * Unlike {@link #reset()} this method is used when the data field is not being used to capture 
   * input from the user.
   * 
   * @effects 
   *  clear the state of <code>this</code> AND set <tt>value=null</tt> 
   */
  public abstract void clear();
  
  /**
   * @effects 
   *  invoke requestFocusInWindow on the display component of this
   */
  @Override
  public boolean requestFocusInWindow() {
    return display.requestFocusInWindow();
  }
  
  /**
   * @effects 
   *  if the display component of this or this is a focus owner
   *    return true
   *  else
   *    return false
   */
  @Override
  public boolean isFocusOwner() {
    return display.isFocusOwner() || super.isFocusOwner();
  }
  
  /**
   * Overrides the parent's class method.
   * 
   * <p>
   * Note: this does not register the mouse listener to the
   * <code>JDataField</code>, only to its actual display component.
   * 
   * @effects registers the {@see MouseListener} <code>ml</code> to the
   *          component {#link display} of <code>this</code>.
   */
  @Override
  public void addMouseListener(MouseListener ml) {
    display.addMouseListener(ml);
  }

  /**
   * @effects registers <code>KeyListener</code> <code>kl</code> to the actual <code>display</code>
   *          component of <code>this</code>.
   */
  @Override
  public void addKeyListener(KeyListener kl) {
    display.addKeyListener(kl);
  }  

  /**
   * @effects registers <code>FocusListener</code> <code>fl</code> to the actual <code>display</code>
   *          component of <code>this</code>.
   */
  @Override
  public void addFocusListener(FocusListener fl) {
    display.addFocusListener(fl);
  }  
  
  /**
   * @effects registers <code>ComponentListener cl</code> to the actual <code>display</code>
   *          component of <code>this</code>.
   */
  @Override
  public void addComponentListener(ComponentListener cl) {
    display.addComponentListener(cl);
  }  
  
  // //// interface methods ////
  /**
   * This method is used together with {@link #fireStateChanged()} method.
   * 
   * @effects registers <code>l</code> to receive state change events when the
   *          value of <code>this</code> is changed.
   */
  public void addChangeListener(ChangeListener l) {
    listeners.add(l);
  }
  
  /**
   * This method is invoked when the <b>user</b> has edited the value on this data field (by 
   * entering some text via the keyboard or by using the mouse to manipulate some data-related 
   * controls). Thus, this method should only be invoked from within an event handling method. 
   * For example, it is invoked by those of the {@link DataFieldInputHelper} class after <tt>value</tt> is changed
   * as the result of user editing the value.
   * 
   * <p>Do not invoke this method from other methods (such as {@link #setValue(Object)}), even though it seems
   * logical to do so. The reason is because those methods may be invoked directly by the application code and 
   * the firing of the state change event would cause undesirable side-effects to those code. 
   * 
   * @effects notifies all listeners registered to <code>this</code> about the
   *          value change
   */
  protected void fireStateChanged() {
    if (!listeners.isEmpty()) {
      ChangeEvent e = new ChangeEvent(this);
      for (ChangeListener l : listeners) {
        l.stateChanged(e);
      }
    }
  }
  
  /**
   * <b>Note</b> This method is coupled with {@link #fireValueChanged()}. It should only be invoked once for each application. 
   * 
   * @effects   
   *  set this.valueListener = valueListener
   */
  public void addValueChangeListener(ValueChangeListener valueListener) {
    this.valueListener = valueListener;
  }

  /**
   * This method has been added to provide an alternative to {@link #fireStateChanged()} in the case that 
   * the application needs to handle
   * more specific state event in which the value has been changed. The reason that this is separated 
   * from that other method is b/c some composite-typed data fields (e.g. combo of date field) contain 
   * many different parts altogether and thus need a higher-level event to inform the application of 
   * the fact that the value has been changed. For these fields, 
   * mouse and key events are associated to the parts and are considered lower-level. These events are typically 
   * handled by the fields themselves.
   * 
   * <p><b>NOTE</b>: this method also invokes {@link #fireStateChanged()} so there is no need to call this method. 
   *  
   * @effects <pre>
   *  if this.valueListener is not null 
   *    inform listener of the specific event of this.value has been changed.
   *  call {@link #fireStateChanged()}   
   *  
   *   </pre>
   * @version 2.7.4  
   */
  protected void fireValueChanged() {
    if (valueChangeEvent == null) {
      valueChangeEvent = new ChangeEvent(this);
    }
    
    if (valueListener != null) {
      valueListener.fieldValueChanged(valueChangeEvent);
    }
    
    // debug
    //System.out.println(this.getClass().getSimpleName()+".Field value changed: " + this);

    fireStateChanged();
  }

  
  /**
   * This method is used by <tt>JDataTable</tt> to create a cell editor from a data field.  
   * 
   * @effects returns the <code>DataCellEditor</code> whose editing component is
   *          this data field itself.
   */
  public TableCellEditor toCellEditor() {
    if (dataCellEditor == null) {
      dataCellEditor = new DataCellEditor();
    }

    return dataCellEditor;
  }

  /**
   * @effects 
   *  sets {@link #dataCellEditor} = editor
   *  
   * @version 5.1c
   */
  protected final void setDataCellEditor(DataCellEditor editor) {
    this.dataCellEditor = editor;
  }

  /**
   * @effects 
   *  return {@link #dataCellEditor}
   *  
   * @version 5.1c
   */
  protected final DataCellEditor getDataCellEditor() {
    return dataCellEditor;
  }
  
  @Override
  public String toString() {
    return this.getClass().getSimpleName()+"(" + getName() + ")";
  }

  /*
   *  helper methods invoked by TextFieldHandler and to be overriden by sub-classes
   */
  protected void handleKeyTyped(KeyEvent e) {
    // empty
  }
  
  protected void handleFocusLost() {
    // empty
  }
  
  protected void handleFocusGained() {
    // empty
  }
  
  /**
   * @effects 
   *  call {@link Thread#sleep(long)}<tt>(millies)</tt>
   * @version 3.1
   */
  protected void sleep(long millis) {
    // wait
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }    
  }
  
  /**
   * @overview
   *  A helper class used to handle user's mouse and keyboard actions 
   *  on the display component. 
   *    
   *  <p>An object of this class is created by the constructor of <tt>JDataField</tt> and is passed 
   *  as input to the {@link JDataField#createDisplayComponent(DataFieldInputHelper)} method to initialise 
   *  the display component of the sub-class.
   *  
   *  <p>Not all sub-classes of <tt>JDataField</tt> need to use this helper.
   *  Those that do should override the <tt>handleX()</tt> methods, including:
   *  <ul>
   *    <li>{@link JDataField#handleKeyTyped()}  
   *    <li>{@link JDataField#handleFocusLost()}
   *    <li>{@link JDataField#handleFocusGained()}
   *   </ul> 
   *  
   *  <p>For example, the two decendants <tt>JSpinnerField</tt>, <tt>JTextField</tt> 
   *    currently use this helper to handle user actions on their text field components. 
   *  
   * @author dmle
   */
  protected class DataFieldInputHelper extends KeyAdapter implements 
  FocusListener {
    
    @Override
    public void keyTyped(KeyEvent e) {
      if (editable) {
        handleKeyTyped(e);
      }
    }

    @Override
    public void focusLost(FocusEvent e) {
      if (// v5.1c: //editable
          getEditable()
          ) {
        handleFocusLost();
      }
    }

    @Override
    public void focusGained(FocusEvent e) {
      handleFocusGained();
    }
  } // end TextField handler

  /**
   * @overview A helper class that represents a <tt>CellEditor</tt> that wraps around this class.
   *   An object of this class is created by the method {@link JDataField#toCellEditor()}
   *   
   * @author dmle
   */
  public class DataCellEditor extends AbstractCellEditor implements
      UpdatableCellEditor {
    protected DAttr[] constraints;

    @Override
    public Object getCellEditorValue() {
      // use validated value
      // TODO: should we use getRawValue() instead?
      Object val = JDataField.this.getValue();
      return val;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {
      JDataField df = JDataField.this;
      return df;
    }

    @Override
    public void setCellEditorValue(Object val) throws IllegalArgumentException {
      JDataField.this.setValue((C) val);
    }

    @Override
    public DAttr[] getDomainConstraints() {
      if (constraints == null) {
        constraints = new DAttr[] { // 
            JDataField.this.dconstraint, //
            null, 
            };
      }

      return constraints;
    }

    /**
     * @effects 
     *  return the <tt>JDataField</tt> object that owns this
     */
    public JDataField getDataField() {
      return JDataField.this;
    }
    
    public void reset() {
      JDataField.this.reset();
    }
  } // end DataCellEditor
} // end JDataField
