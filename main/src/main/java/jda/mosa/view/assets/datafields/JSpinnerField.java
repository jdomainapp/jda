package jda.mosa.view.assets.datafields;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.JDataField.DataCellEditor;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ObjectUpdateData;

/**
 * @overview
 *   A <tt>JSpinner</tt> field that can be bound to the domain field of a domain class. 
 *   
 * @author dmle
 */
public class JSpinnerField<C> extends JBindableField {

  //private List<C> values;

  /**a flag to indicate whether or not 
   * data validation has been performed after the item value was changed */
  private boolean validatedOnItemChanged; 
  
  /** a shared Runnable that is used to highlight the text field component 
   * of a JSpinner when it is on focus*/
  private TextFieldAndButtonHandler textFieldAndButtonHandler;
  
  /**
   * Creates a spinner-type data field constrained by the domain constraint
   * <code>domainConstraint</code>, whose list of values is <code>values</code> may be bounded to 
   * a domain field specified by <tt>boundConstraint</tt>, the 
   * initially selected value is <code>val</code>, and whose editability is
   * determined by <code>editable</code>.
   * 
   * <p>
   * If <code>val = null</code> then the first element of the list (if any) is
   * selected.
   * 
   * <p>
   * If <code>values</code> is empty then the list is initialised to contain an
   * empty string (
   * 
   * <pre>
   * &quot;&quot;
   * </pre>
   * 
   * ) element.
   * 
   * @requires <code>values != null</code>
   * 
   */
  public JSpinnerField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint,
      Boolean editable) {
    this(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, 
        false /* auto-validation: generally there is no need to auto-validate since values are selected from a
               * list of allowed values. An exception to this is for bounded fields whose bounded domain objects 
               * are specified with cardinality constraints. For performance reason such constraints
               * should be validated by the application rather by this field.
               * If this feature is important for an application, however, then auto-validation can still be 
               * turned-on by using the second constructor (below)
               */
        );
  }
  
  public JSpinnerField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint,
      Boolean editable, Boolean autoValidation) {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);

    validatedOnItemChanged = true;
    
    /*v2.7: moved to createDisplayComponent  
    JExtensibleSpinner spinner = (JExtensibleSpinner) display;
    
    // adjust the text field length based on the bound constraint
    if (boundConstraint != null && boundConstraint.length() > 0) {
      spinner.setTextFieldLength(boundConstraint.length());
    }
    */
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {

    int length = getDomainFieldWidth();

    //v.2.6.4.a: change to not use editable here and that extensible=false by default 
    //JSpinner spinner = new JExtensibleSpinner(this, null, length, editable);
    boolean extensible = false;
    JSpinner spinner = new JExtensibleSpinner(this, null, length, extensible);
    
    /*
     * Set up the spinner so that its text field has focus on mouse click 
     *  need to do so separately for both the value buttons and the text field 
     */
    // spinner buttons: a special handler used to handle the mouse and focus events
    int timeOut = //25;
        getConfiguration().getListSelectionTimeOut();
    textFieldAndButtonHandler = new TextFieldAndButtonHandler(timeOut);

    Component[] comps = spinner.getComponents();
    for (Component c : comps) {
      if (c instanceof AbstractButton) {
        c.addMouseListener(textFieldAndButtonHandler);
      }
    }
    
    // spinner text field: use the provided input handler
    // for focus event
    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
        .getEditor();
    JTextField tf = editor.getTextField();
    tf.addFocusListener(tfh);
    
    // v2.7.3: for key-typed event
    tf.addKeyListener(tfh);

    // v2.6.4.b: left-justified
    tf.setHorizontalAlignment(SwingConstants.LEFT);
    
    // disabled text color for text field
    tf.setDisabledTextColor(GUIToolkit.COLOUR_FOREGROUND_DISABLED);
    
    // v5.1c: display = spinner;
    setGUIComponent(spinner);
    
    setEditable(// v5.1c: //editable
        getEditable());
    
    return spinner; //v5.1c: display;
  }
  
  @Override
  protected void loadBoundedData() throws NotPossibleException {
  //v2.7.4: moved up 
    //dataSource.connect();
    
    loadData();
  }

  @Override
  protected C getRawValue() throws NotFoundException {
    JComponent display = getGUIComponent(); // v5.1c:
    Object displayVal = ((JSpinner) display).getValue();
    if (displayVal.equals(Nil)) {
      return null;
    } else {
      DAttr boundConstraint = getBoundConstraint(); // v5.1c:
      
      if (boundConstraint != null) {
        JDataSource dataSource = getDataSource(); // v5.1c
        
        return (C) dataSource.reverseLookUp(boundConstraint, displayVal);
      } else
        return (C) displayVal;
    }  
  }
  
  
  @Override
  public C getValue() throws ConstraintViolationException {
    //Object v;

    C rawValue;
    
    // get the raw value first
    try {
      rawValue = getRawValue();
    } catch (NotFoundException e) {
      // raw value is not one of the allowed values
      displayError(e.getCode(), e// null
          , true, false, false);
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_VALUE, e, 
          //"{0}: Giá trị dữ liệu không hợp lệ {1}",
          //this,
          new Object[] {""} // cannot determine exactly the value currently displayed on the textfield of the spinner
          );
    }
    
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    Object value = getValueDirectly(); // v5.1c
    
    if (//!autoValidation &&           // auto-validation is OFF
        !validatedOnItemChanged  // new value item but has not been validated
        && (rawValue != value)   // v2.7.3: validate only when changed value is different from the previous value (
                                            //two values may be the same if user entered the same value on the keyboard)
        ) {
      
      // 2.6.4.a: value has been changed but has not be validated
      if (isBounded()) { //bounded field
        if (getValidator() != null && // a validator used to validate is specified
            rawValue != null) {
          validateBoundedValue(rawValue);
        }
      } else {        
        validateValue(rawValue, dconstraint);
      }
      validatedOnItemChanged = true;
    } else { // validatedOnItemChanged = true
      // either value has not been changed or it has been changed and has been validated
      // check the first case
      if (rawValue == null && requireValues()) {
        updateGUI(true);  // display error icon
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED,
            //"{0}: Giá trị dữ liệu không hợp lệ {1}",
            new Object[] {dconstraint.name(), null});   
      }
    } 

    if (!//v5.1c: validated
        isValidated()
        ) //validated = true;
      setIsValidated(true);
    
    // v2.6.4.a: commented out (should not happen)
//    if (!validated) {
//      // value has not passed any of the validation mechanisms of this
//      
//        // definitely error
//      updateGUI(true);  // display error icon
//      
//      throw new ConstraintViolationException(
//          ConstraintViolationException.Code.INVALID_VALUE,
//          "{0}: Giá trị dữ liệu không hợp lệ {1}",
//          this,
//          "" // cannot determine exactly the value currently displayed on the textfield of the spinner
//          );        
//    } 

    // validated ok, update value
    // updateValue();
    setValueDirectly(rawValue) ; // v5.1c: value = rawValue;

    return (C) getValueDirectly(); //v5.1c: value;
  }

  /**
   * @effects 
   *  get display value of this.display
   *  update this.value from the display value
   */
  /* v2.6.4.a: replaced by getRawValue()
  private void updateValue() {
    Object v = ((JSpinner) display).getValue();
    if (v.equals(Nil)) {
      value = null;
    } else {
      if (boundConstraint != null)
        value = (C) dataSource.reverseLookUp(boundConstraint, v);
      else
        value = (C) v;
    }  
  }
  */
  
  /**
   * @effects sets the current value list to a new value list <code>vals</code>.
   *          <p>
   *          This method actually stores the reference to the new list.
   */
  // v2.6.2 commented out to use data source
//  public void setValues(List<C> vals) {
//    values = vals;
//    List displayValues = getDisplayValues(vals, dconstraint, boundConstraint);
//    ((JExtensibleSpinner) display).setValues(displayValues);
//    reset();
//  }

  @Override // this is the same as JListDataField's implementation
  protected List getDisplayValues(DAttr domainConstraint) {
    List values = new ArrayList();
    Iterator sit;
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (boundConstraint != null)
      sit = dataSource.getBoundedValues(boundConstraint);
    else
      sit = dataSource.iterator();
    
    /* this is where this differs from the super-class method
     * v2.7.2: make Nil the first value of all fields 
    */
    values.add(Nil);
    
    // add rest of values
    if (sit != null) {
      while (sit.hasNext())
        values.add(sit.next());
    }
    
    return values;
  }
  
  @Override
  protected void setDisplayValue(Object dispVal) {
    try {
      JComponent display = getGUIComponent(); // v5.1c:
      ((JSpinner) display).setValue(dispVal);
      
      // reset status
      validatedOnItemChanged = true;
      
      // v2.7.4: uncomment these if this field is bounded and editable
      if (!//v5.1c: validated
          isValidated()
          ) //validated=true;
        setIsValidated(true);
        
      updateGUI(false);
      
    } catch (IllegalArgumentException e) {
      // should not happen
      /**
       * this exception is thrown in some cases when the user is logging out of
       * the current session...
       */
//      System.err.println(getParentContainer()+"."+this + ".setDisplayValue: failed to set display value: "
//          + dispVal);
//      e.printStackTrace();
    }
  }  

  @Override
  public void setEditable(boolean state) {
    super.setEditable(state);
    
    // it is not enough to disable the text field of the spinner
    // because the value selector buttons are still enabled
    // thus the only possible way is to disable the whole spinner field! 
    //((JSpinner.DefaultEditor) ((JSpinner)display).getEditor()).getTextField().setEditable(state);
    JComponent display = getGUIComponent(); // v5.1c:
    ((JSpinner)display).setEnabled(state);
  }

//  /**
//   * @effects if the domain constraint states that a value must be specified for
//   *          this field (i.e. <code>optional = false</code>) then returns
//   *          <code>true</code>, otherwise returns <code>false</code>.
//   */
//  private boolean requireValues() {
//    return (dconstraint != null && (dconstraint.optional() == false));
//  }

  @Override
  public void addMouseListener(MouseListener ml) {
    // register the mouse to both the spinner buttons and its text field
    JComponent display = getGUIComponent(); // v5.1c:
    JSpinner spinner = (JSpinner) display;
    JSpinner.DefaultEditor editor = ((JSpinner.DefaultEditor) ((JSpinner) display).getEditor());

    // text field
    editor.getTextField().addMouseListener(ml);
    
    // buttons
    // debug
    Component[] comps = spinner.getComponents();
    for (Component c : comps) {
      if (c instanceof AbstractButton) {
        c.addMouseListener(ml);
      }
    }
  }
  
  @Override
  public void addKeyListener(KeyListener kl) {
    JComponent display = getGUIComponent(); // v5.1c:
    ((JSpinner.DefaultEditor) ((JSpinner) display).getEditor())
          .getTextField().addKeyListener(kl);
  }
  
  /**
   * Implements the <code>ChangeListener</code> interface.
   * <p>
   * This method is invoked when a state change event is fired by a data source
   * (typically the {@see DomainSchema} object of the application).
   */
  public void stateChanged(ChangeEvent e) {
    // received when the values list to which this data field is bound
    // is changed. This simply fires state change on the JSpinnerModel
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    
    if (boundConstraint != null) {
      JComponent display = getGUIComponent(); // v5.1c:
      
      JExtensibleSpinner spinner = (JExtensibleSpinner) display;
      JSpinnerField.JExtensibleSpinner.ExtensibleSpinnerListModel model = (JSpinnerField.JExtensibleSpinner.ExtensibleSpinnerListModel) spinner
          .getModel();

      // update the spinner model:
      // needs 2 things:
      // the affected value object(s)
      // the operation that was performed on the object (create or delete)

      ChangeEventSource ds = (ChangeEventSource) e.getSource();
      /**
       * get the changed objects and use them to update the list model. Note:
       * there is no need to update {@link #values} because the changed objects
       * are taken from this list.
       */
      
      List objects = ds.getObjects();
      if (objects == null) // something was wrong
        throw new IllegalArgumentException("");

      if (objects.isEmpty()) // no objects
        return;

      List bvals = new ArrayList();
      for (Object obj : objects) {
        bvals.add(getDisplayValue((C) obj));
      }
      
      /**
       * v2.5.4
       * Support 2 object actions: add (new), delete
       */
      if (ds.isAddNew()) {
        // add new
        model.addValues(bvals);
      } else if (ds.isDelete()) {
        // delete
        // v2.6.4.b: use a separate method for delete
        //model.deleteValues(bvals);
        deleteValues(objects, bvals);
      } else if (ds.isUpdate()) {
        // v2.6.4.a: support update (reload the bounded values - because there is no mapping from display values to objects)
        // only update if this is bounded and that the bounded attribute was changed
        if (isBounded()) {
          /*v2.7.2
          Collection<DomainConstraint> affectedAttributes = (Collection<DomainConstraint>) ds.getEventData();
          */
          ObjectUpdateData data = (ObjectUpdateData) ds.getEventData();
          Collection<DAttr> affectedAttributes = data.getUpdatedAttribs();
          
          if (affectedAttributes != null) {
            for (DAttr attrib : affectedAttributes) {
              if (attrib.equals(boundConstraint)) {
                // bounded attribute was changed
                refresh();
                break;
              }
            }
          }
        }
      }
    }
  }  

  /**
   * @effects 
   *  remove the display objects contained in <tt>displayObjects</tt> from 
   *  this. 
   *  
   *  <p>If this results in an empty state 
   *      refresh the data
   *      set this.value to null
   *     Else if this.value is contained in objects
   *      set this.value to the object matching the new display value in this
   */
  private void deleteValues(List objects, List displayObjects) {
    JComponent display = getGUIComponent(); // v5.1c:
    
    JExtensibleSpinner spinner = (JExtensibleSpinner) display;
    JSpinnerField.JExtensibleSpinner.ExtensibleSpinnerListModel model = (JSpinnerField.JExtensibleSpinner.ExtensibleSpinnerListModel) spinner
        .getModel();
    
    // remove from model
    model.deleteValues(displayObjects);
    
    if (model.isEmpty()) {
      setValueDirectly(null); // v5.1c: value = null;      
      refresh();
    } else if (objects.contains(getValueDirectly()//v5.1c: value
        )) {
      // set this.value to the object matching the new display value in this
      //TODO: should we wait for state change update (fired by the deletion on the model) to finish?
      // value = getRawValue();
      setValueDirectly(null); // v5.1c: value = null;      
      model.fireUpdate();
    }
  }
  
  @Override
  protected void handleKeyTyped(KeyEvent e) {
    // user is entering values via keyboard
    // set edit flag to true but not validating the value here. It will be validated on next read
    // by getValue
    if (validatedOnItemChanged)
      validatedOnItemChanged=false;
  }

  @Override
  protected void handleFocusLost() throws ConstraintViolationException {
    // debug
    // System.out.printf("%s.focusLost%n",((JTextComponent)src).getParent());
//    if (autoValidation &&           // auto-validation is ON
//        !validatedOnItemChanged &&  // new value item but has not been validated 
//        isBounded() && // bounded field
//        validated && // previously-validated (i.e. skip if not already validated) 
//        validator != null // a validator used to validate must be specified
//        ) {
    if (//v5.1c: autoValidation
        isAutoValidation() &&           // auto-validation is ON
        !validatedOnItemChanged  && // new value item but has not been validated 
        //v5.1c: validated
        isValidated()               // previously-validated (i.e. skip if not already validated)
        ) {
      // 2.6.4.a: value has been changed but has not be validated
      C rawValue;
      
      // get the raw value first
      try {
        rawValue = getRawValue();
      } catch (NotFoundException e) {
        // raw value is not one of the allowed values
        displayError(e.getCode(), e// null
            , true, false, false);
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "{0}: Giá trị dữ liệu không hợp lệ {1}",
            this,
            "" // cannot determine exactly the value currently displayed on the textfield of the spinner
            );
      }
      
      if (isBounded()) { //bounded field
        if (getValidator() != null && // a validator used to validate is specified
            rawValue != null) {
          validateBoundedValue(rawValue);
        }
      } else {
        DAttr dconstraint = getDomainConstraint();  // v5.1c:
        
        validateValue(rawValue, dconstraint);
      }
      validatedOnItemChanged = true;
    } else { 
      //TODO: should we also handle the case where user has not changed the value, i.e. validatedOnItemChanged=true?
      // (see getValue())
      // do nothing
    } 

      
//      if (rawValue != null) {
//        validateBoundedValue(rawValue);
//      }
//      
//      validatedOnItemChanged = true;
//    }
  }

  /**
   * @requires 
   *  isBounded() = true /\ validator != null /\
   *  domainClass != null
   * 
   * @modifies validated
   *  
   * @effects <pre>
   *  if domainClass != null
   *    validate cardinality constraint on value
   *    if ok
   *      validated = true
   *      updateGUI
   *    else
   *      display error
   *      throws ConstraintViolationException
   *   </pre>
   */
  private void validateBoundedValue(Object value) throws ConstraintViolationException {
    //if (autoValidation && !validatedOnItemChanged) {
      /*
       * if bounded and the association is 1:M check the cardinality constraint
       * of the association to the new
       * value object
       * (Note: there is no need to validate the association to the old object)
       */
      // validate new object constraint (action=create)
        //updateValue();
      try {
        /* v2.6.4.a: it is not possible to determine the current link count from here
         * because objects may not have been loaded
         */
        getValidator().validateBoundedValueOnCreate(this, value);
        updateGUI(false);
        //v5.1c: validated = true;
        setIsValidated(true);
      } catch (ConstraintViolationException e) {
        //v5.1c: validated = false;
        setIsValidated(false);
        // v2.6.1: to display dialog message because error icon is not displayed
        // for spinner field on table when this occurs
        if (//v5.1c: autoValidation
            isAutoValidation()
            ) // v2.7.3: added this check
          displayError(e.getCode(), e// e.getMessage()
              , true, true, false);
        else 
          displayError(e.getCode(), e// e.getMessage()
              , true, false, false);
          
        throw e;
      } catch (NotFoundException e) {
        // ignore this
        updateGUI(false);
        //v5.1c: validated = true;
        setIsValidated(true);
      }
//      validatedOnItemChanged = true;
    //}
  }
  
  @Override
  protected void handleFocusGained() {
    // highlight text
    JComponent display = getGUIComponent(); // v5.1c:
    JSpinner spinner = (JSpinner) display;
    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
    JTextField tf = editor.getTextField();
    
    // debug
    //System.out.printf("%s.focusGained%n",JDataField.this.getClass());
    textFieldAndButtonHandler.run(tf);
  }

  @Override
  public void reset() {
    // resets both the value and the display value
    Object displayValue;

    //TODO: should we do value = initVal first??

    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (dataSource != null && requireValues() && !dataSource.isEmpty()) {
      // get the first object from data source
      // error -> value = dataSource.iterator().next();
      JComponent display = getGUIComponent(); // v5.1c:
      
      JExtensibleSpinner spinner = (JExtensibleSpinner) display;
      //v2.7.2: Object dispVal = spinner.getFirstValue();
      displayValue = spinner.getFirstValue();
      
      // reset value based on display value
      if (isBounded() && displayValue != null) {
        DAttr boundConstraint = getBoundConstraint(); // v5.1c:
        
        try {
          // v5.1c: value = dataSource.reverseLookUp(boundConstraint,displayValue);
          setValueDirectly(dataSource.reverseLookUp(boundConstraint,displayValue));
        } catch (Exception e) {
          // should not happen
          if (debug) { 
            System.out.printf(getParentContainer()+"."+this+"reset(): %n");
            e.printStackTrace();
          }
          // v5.1c: value = getInitValue(); //initVal;
          setValueDirectly(getInitValue());
        }
      } else {
        // v5.1c: value = displayValue;
        setValueDirectly(displayValue);
        if (displayValue == null) // v2.7.4
          displayValue = Nil;
      }
    } else {
      // reset to initial value (whatever that is)
      // v5.1c: value = getInitValue(); //initVal;
      setValueDirectly(getInitValue());
      Object value = getValueDirectly(); // v5.1c
      if (value != null) {
        displayValue = getDisplayValue((C) value);
      } else {
        displayValue = Nil;
      }
    }
    
    /*v2.7.2: moved to the else branch (above)
    // the actual display value
    if (value != null) {
      displayValue = getDisplayValue((C) value);
    } else {
      displayValue = Nil;
    }
    */
    
    /*v2.7.4: moved to below setDisplayValue (below)
    // validation is false
    validated = false;
    */

    setDisplayValue(displayValue);
    
    // validation is false
    //validated = false;
    setIsValidated(false);
  }
  
  /**
   * @effects 
   *  refresh this to show the most up-to-date data
   */
  public void refresh() throws NotPossibleException {
    loadData();
  }

  @Override 
  public void deleteBoundedData() {

    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (dataSource != null) {
//      //TODO: implement this if data source is used
//      throw new NotImplementedException(NotImplementedException.Code.METHOD_NOT_IMPLEMENTED,
//          new Object[] {this.getClass().getSimpleName(), "deleteBoundedData()"});
      JComponent display = getGUIComponent(); // v5.1c:
      JExtensibleSpinner spinner = (JExtensibleSpinner) display;
      spinner.clear();
      
      // validation is false
      //v5.1c; validated = false;
      setIsValidated(false);
    }
    /*
    if (!isBounded()) {
      return;
    }
    
    if (dataSource != null) {
      // get the first object from data source
      JExtensibleSpinner spinner = (JExtensibleSpinner) display;
      spinner.clear();
      
      // validation is false
      validated = false;
    } */
  }
  
  /**
   * @effects <pre>
   *  get the display values for this (requires reading from the data source if this is bounded)
   *  update this with the display values
   *  set the initially selected value either to this.value (if specified) or the first display value</pre>
   */
  private void loadData() throws NotPossibleException {
    JComponent display = getGUIComponent(); // v5.1c:
    JExtensibleSpinner spinner = (JExtensibleSpinner) display;

    Object dispVal;
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    
    List displayValues = getDisplayValues(dconstraint); 
    Object value = getValueDirectly(); // v5.1c
    if (value == null && !displayValues.isEmpty()) {
      dispVal = displayValues.get(0);
    } else if (value != null) {
      dispVal = getDisplayValue(value);
    } else {
      // no display values
      if (requireValues()) {
        throw new NotPossibleException (NotPossibleException.Code.DATA_SOURCE_IS_EMPTY, 
            "Không có dữ liệu nào từ nguồn cho {0}", this);
      } else {
        // accept
        dispVal = Nil;
      }
    }
    
    // v2.6.4b: debug
    if (debug) {
      if (!displayValues.contains(dispVal))
        System.err.println(this+".loadData(): display value not in list: " + dispVal.getClass().getSimpleName() + 
            "('"+dispVal+"')");
    }
      
    spinner.setValues(displayValues);
    //v2.7.3: spinner.setValue(dispVal);
    setDisplayValue(dispVal);
  }
  
  @Override
  public TableCellEditor toCellEditor() {
    // v5.1c: if (dataCellEditor == null) {
    DataCellEditor dataCellEditor = getDataCellEditor(); 
    if (dataCellEditor == null) {
      dataCellEditor = new SpinnerDataCellEditor();
      
      setDataCellEditor(dataCellEditor);
    }

    return dataCellEditor;
  }
  
  /**
   * A sub-class of <code>JSpinner</code>, which can add new values and cycle
   * through the values when browsing.
   * 
   * @author dmle
   * 
   */
  private final class JExtensibleSpinner extends JSpinner {
    private ExtensibleSpinnerListModel model;
    private JTextField textField;

    // v2.6.4.a: changed editable -> extensible
    private boolean extensible;

    // /// constructor methods /////
    public JExtensibleSpinner(final JDataField owner, final List vals,
        final int length, final boolean extensible) {
      super();

      if (vals != null)
        model = new ExtensibleSpinnerListModel(owner, vals);
      else
        model = new ExtensibleSpinnerListModel(owner);

      setModel(model);
      this.extensible = extensible;

//      JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) getEditor();
//
//      textField = editor.getTextField();
//      textField.setColumns(length);
      setTextFieldLength(length);
      
      textField.setHorizontalAlignment(JTextField.RIGHT);
    }

    /**
     * @effects 
     *  if model is initialised 
     *    return the first value of this.model
     *  else
     *    return null
     */
    public Object getFirstValue() {
      return model.firstValue;
    }

    public void setTextFieldLength(int length) {
      JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) getEditor();

      textField = editor.getTextField();
      
      // v2.5.4: set length to min (MAX_LENGTH,length)
      //textField.setColumns(length);      
      int displayLength = Math.min(MAX_DISPLAYABLE_TEXT_WIDTH, length);
      textField.setColumns(displayLength);      
    }
    
    public void setValues(List vals) {
      model.setList(vals);
    }

    /**
     * @effects
     *  clear {@link #model} 
     *
     * @version 3.2c
     */
    public void clear() {
      model.deleteValues(model.valList);
    }
    
    @Override
    public void setValue(Object v) {
      // update current value
      model._setValue(v);
    }
    
    /**
     * @overview 
     * A sub-type of <code>SpinnerListModel</code> which maintains the same value list as 
     * the super-class. 
     * 
     * <p>This design breaks the encapsulation rule, but appears to be the only 
     * way to enable the modification of the model (i.e. addition/removal 
     * of values). The <tt>SpinnerListModel</tt> class does not allow modification
     * of the model.
     * 
     * @author dmle
     */
    private final class ExtensibleSpinnerListModel extends SpinnerListModel {
      private List valList;
      private JDataField owner;
      private Object firstValue, lastValue;
      
      // v2.6.4.b: needs to maintain currentValue (b/c cannot update
      // the index of the super class when values are removed)
      private Object currentValue;
      
      public ExtensibleSpinnerListModel(JDataField owner) {
        super();
        this.owner = owner;
      }

      public ExtensibleSpinnerListModel(JDataField owner, List v) {
        // use the same value list here and in the super-class
        super(v);
        this.owner = owner;
        valList = v;

        // cyclable
        firstValue = v.get(0);
        lastValue = v.get(v.size() - 1);
        
        currentValue = firstValue;
      }

      /**
       * This is an alternative to {@link #addValue(Object)}.
       *  
       * @requires 
       *  vals != null /\ vals.size() > 0
       * @effects add a <code>List</code> of objects to <code>this</code>.
       */
      void addValues(List vals) {
        if (valList == null) {
          // not yet initialised
          valList = new ArrayList();
          valList.addAll(vals);
          currentValue = vals.get(0);
          refresh();
          // pass the same list to the super class
          super.setList(valList);
        } else {
          // just add new values 
          valList.addAll(vals);
          
          /*v2.7.2: support Nil as the first value of all fields 
          if (requireValues() && firstValue != null && firstValue.equals(Nil)) {
            // valList temporarily contains only Nil before addition (a sign
            // if it being 'pseudo' empty)
            // update current value
            currentValue = vals.get(0);
            // remove the Nil object from the list
            valList.remove(0);
            refresh();
            fireUpdate(); //fireStateChanged();
          } else {
          */
            // valList is not empty before addition
            // update the last value only
            lastValue = vals.get(vals.size() - 1);
          //}
        }
      }

      /**
       * @effects 
       *  remove elements in <tt>vals</tt> from the value list of this
       *  and update the first and last values (if required) 
       */
      void deleteValues(List vals) {
        if (valList != null) {
          // remove from valList (also remove from the same list in the super-class)
          valList.removeAll(vals);

          /* v2.6.4.b: removed this block because already performed by addValues()
          if (requireValues() && firstValue != null && firstValue.equals(Nil)) {
            // remove the Nil object from the list
            valList.remove(0);
            refresh();
            //fireStateChanged();
            fireUpdate();
          } 
          */
          
          if (!isEmpty()) {
            /*v2.6.4b: update both first and last values (if needed)
            // update the last value only 
            lastValue = valList.get(valList.size() - 1);
            */
            if (vals.contains(firstValue)) {
              // update to the new first value
              firstValue = valList.get(0);
              //super.setValue(firstValue);
              //fireUpdate(); //fireStateChanged();
            } 
            
            if (vals.contains(lastValue)) {
              // update to the new last value
              lastValue = valList.get(valList.size()-1);
              //super.setValue(lastValue);
            }
            
            if (vals.contains(currentValue)) {
              // move away from this value
              currentValue = getPreviousValue();
              setValue(currentValue);
            }
          } else {
            // empty
            // reset first and last value
            if (firstValue != null && !firstValue.equals(Nil)) {
              firstValue = null;
            }
            
            if (lastValue != null) {
              lastValue = null;
            }
            
            // reset current value
            currentValue = null;
          }
        }
      }
      
      /**
       * @effects adds object <code>v</code> to <code>this</code> (hence
       *          extending the values list.) and 
       *          update the first and last values (if required)
       */
      void addValue(Object v) {
        boolean updateState = false;
        if (valList == null) {
          // not yet initialised
          valList = new ArrayList();
          valList.add(v);
          currentValue = v;
          refresh();
          // pass the same list to the super class
          super.setList(valList);
        } else {
          valList.add(v);

          /*v2.7.2: support Nil as the first value of all fields 
          if (requireValues() && firstValue != null && firstValue.equals(Nil)) {
            // valList temporarily contains only Nil before addition (a sign
            // if it being 'pseudo' empty)
            // update current value
            currentValue = v;
            // remove the Nil object from the list
            valList.remove(0);
            refresh();
            fireUpdate(); //fireStateChanged();
          } else {
          */
            // valList is not empty before addition
            // update the last value only
            lastValue = v;
          //}
        }
      }

      /**
       * @effects if current value is not the last then returns the next value,
       *          else returns the first object (cycle back to the beginning)
       */
      public Object getNextValue() {
        Object value = super.getNextValue();
        if (value == null) {
          value = firstValue;
        }
        
        currentValue = value;
        
        return value;
      }

      /**
       * @effects if current value is not the first then returns the previous
       *          value, else returns the last object (cycle to the end)
       */
      public Object getPreviousValue() {
        Object value = super.getPreviousValue();
        if (value == null) {
          value = lastValue;
        }
        
        currentValue = value;
        
        return value;
      }


      /**
       * @requires 
       *  vals != null /\ vals not empty
       * @effects replaces the existing value list in <code>this</code> by
       *          <code>vals</code>.
       */
      public void setList(List vals) {
        valList = vals;
        currentValue = valList.get(0);
        super.setList(vals);
        refresh();
      }

      @Override
      public Object getValue() {
        return currentValue;
      }
      
      /**
       * This method eventually leads to {@link #setValue(Object)} to be invoked. 
       * However, it must be used instead of the other method for setting value by the application. 
       * The other method 
       * is also used internally when user browse through the value list. 
       * 
       * @effects 
       *  update currentValue and invoke super method to change the value
       */
      void _setValue(Object v) {
        // change current value
        currentValue = v;
        // invoke super
        //v2.7.3: super.setValue(v);
        setValue(v);
      }
      
      /**
       * @effects <pre>
       * if boundConstraint is specified (i.e. valList contains domain
       *          objects, e.g. students)
       *    a- elt has the same type as the bounded attribute
       *       i- elt is a valid value of the bounded attribute (--> retrieve object, invoke
       *            super.setValue(object)...)
       *       ii- elt is not a valid of the bounded attribute (--> throws
       *            IllegalArgumentException)
       *    b- else (i.e. elt is a domain object)
       *       --> invoke super.setValue(elt),...            
       * else (i.e. boundConstraint is not specified)
       *    a- elt is not of the correct type as specified in dconstraint
       *            --> convert elt to the correct type
       *                + successful: --> invoke super.setValue(elt)...
       *                + unsuccessful: throws IllegalArgumentException
       *    b- elt is of the correct type --> invoke super.setValue(elt)...
       * </pre>
       */
      public void setValue(Object elt) throws IllegalArgumentException {
        // try setting the value, if not found add it
        /* v2.6.4.b: 
         * this first block is used to convert the input (which is 
         * read as String from the text field of the spinner) into the 
         * correct form (expected by the model)
         * 
         * Update: if bounded then after the normal conversion, determine
         * if we need to perform an extra conversion into a specific form 
         * of display value (e.g. DisplayValueTuple) that is required by the model  
         **/
        Object v;
        String eltType = elt.getClass().getSimpleName();
        DAttr dconstraint = getDomainConstraint();  // v5.1c:
        DAttr boundConstraint = getBoundConstraint(); // v5.1c:
        
        if (!elt.equals(Nil)) {
          /* v2.6.4b: determine if extra conversion required
          if (boundConstraint != null
              && !eltType.equals(boundConstraint.type().name())) {
            v = parseValue(elt, boundConstraint);
             */          
          if (boundConstraint != null) {
            JDataSource dataSource = getDataSource(); // v5.1c
            
            //TODO: this parsing seems a bit awkward and slow. It would have been avoided
            // if a specialised text field is used for the spinner 
            if (dataSource != null && dataSource.useDisplayValueTypeFor(boundConstraint)) {
              v = dataSource.parseDisplayValue(elt, boundConstraint);
            } else if (!eltType.equals(boundConstraint.type().name())) {
              // parse value based on the bound constraint
              v = parseDisplayValue(elt, boundConstraint);
            } else {
              // already in the correct format
              v = elt;
            }
          } else if (dconstraint != null
              && !eltType.equals(dconstraint.type().name())) {
            v = parseDisplayValue(elt, dconstraint);
          } else {
            v = elt;
          }
        } else {
          v = Nil;
        }

        // v is the expected value object, but v may still not be one of the
        // existing objects. If not then IllegalArgumentException is thrown by
        // super.setValue
        // we catch it and process one more case when JDataField is editable
        try {
          super.setValue(v);
          
          // update validated
          if (!v.equals(Nil)) {
            if (!//v5.1c: validated
                isValidated()
                ) //validated = true;
              setIsValidated(true);
              
            updateGUI(false);
          } else if (!requireValues()) { // v is Nil & optional
            if (!//v5.1c: validated
                isValidated()
                ) //validated = true;
              setIsValidated(true);
          }
          // fire a state change event source by the data field
          JSpinnerField.this.fireStateChanged();
        } catch (IllegalArgumentException e) {
          if (extensible && !v.equals(Nil)) {
            // value not found in the list add it
            // validate it first
            try {
              addValue(validateValue(v, dconstraint));
              // fire a state change event source by the data field
              JSpinnerField.this.fireStateChanged();

              if (debug) {
                System.out.println("added: " + v);
              }
            } catch (ConstraintViolationException ex) {
              // ignore
            }
          } else {
            // re-throw exception
            // debug
            if (debug)
              System.err.println(JSpinnerField.this+".model.setValue(): invalid value: " + v.getClass().getSimpleName() + "('" +v+"')");
            
            throw e;
          }
        }
      }

      void refresh() {
        // List l = super.getList();
        firstValue = valList.get(0);
        lastValue = valList.get(valList.size() - 1);
      }

      /**
       * @effects returns the <code>JDataField</code> that uses this to manage
       *          its values
       */
      JDataField getDataField() {
        return owner;
      }

      /**
       * @effects returns <code>true</code> if the value list is empty,
       *          otherwise returns <code>false</code>
       */
      boolean isEmpty() {
        if (valList == null)
          return true;
        
        return valList.isEmpty();
      }
      
      /**
       * @effects 
       *  fire state change event to update this
       */
      void fireUpdate() {
        super.fireStateChanged();
      }
    } // end ExtensibleListModel
  } // end JExtensibleSpinner
  
  /**
   * @overview 
   *  A sub-class of {@link JDataField.DataCellEditor} to provide both domain constraint 
   *  and bound constraint. 
   * @author dmle
   */
  protected class SpinnerDataCellEditor extends JDataField.DataCellEditor {
    @Override
    public Object getCellEditorValue() {
      // use raw value
      Object val = JSpinnerField.this.getValue(true);
      return val;
    }
    
    @Override
    public DAttr[] getDomainConstraints() {
      if (constraints == null) {
        constraints = new DAttr[] { // 
            JSpinnerField.this.getDomainConstraint(), // v5.1c: dconstraint, //
            JSpinnerField.this.getBoundConstraint() // v5.1c: boundConstraint, // 
            };
      }

      return constraints;
    }
  } // end SpinnerDataCellEditor
  
  /**
   * @overview
   *  A helper sub-class of Runnable that is specifically used to highlight a text field 
   *  component of a JSpinner after a given time-out.
   *  
   *  <p>This is a hack to overcome the limitation of the JSpinner whose text field is not highlighted 
   *  when on-focus.  
   *     
   * @author dmle
   */
  private class TextFieldAndButtonHandler extends MouseAdapter implements Runnable {
    private JTextComponent tf;
    private int timeOut;
    public TextFieldAndButtonHandler(int timeOut) {
      this.timeOut = timeOut;
    }

    /**
     * @effects 
     *  run this with the specified text component  
     */
    void run(JTextComponent tf) {
      this.tf = tf;
      SwingUtilities.invokeLater(this);
    }

    public void run() {
      try {
        Thread.sleep(timeOut);
      } catch (InterruptedException e) {
      }
      tf.selectAll();
    }
    
    // Mouse click event
    public void mouseClicked(MouseEvent e) {
      // only interested in mouse click on abstract button
      // i.e. when item changed caused by using clicking on the value button
      Object src = e.getSource();
      if (src instanceof AbstractButton) {
        // request focus for text field
        JComponent display = getGUIComponent(); // v5.1c:
        JSpinner spinner = (JSpinner) display; //((AbstractButton)src).getParent();
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
            .getEditor();
        JTextField tf = editor.getTextField();
        tf.requestFocusInWindow();
        // also highlight text field (?)
        // tf.selectAll();
        
        // flag validate on item changed
        validatedOnItemChanged = false;
      }
    }
  } // end ListSelectionRunnable
}
