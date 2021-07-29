package jda.mosa.view.assets.datafields.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.table.TableCellEditor;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JDataField.DataCellEditor;
import jda.util.events.ChangeEvent;
import jda.util.events.ChangeEventSource;
import jda.util.events.ObjectUpdateData;

public abstract class JAbstractListDataField<C> extends JBindableField { 
  //implements JMultiValuedDataField {

  /**a flag to indicate whether or not 
   * data validation has been performed after the item value was changed */
  private boolean validatedOnItemChanged; 
  
  public JAbstractListDataField(DataValidator validator, Configuration config,  
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
  
  public JAbstractListDataField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint,
      Boolean editable, Boolean autoValidation) {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);

    validatedOnItemChanged = true;
  }
  

  @Override
  protected void loadBoundedData() throws NotPossibleException {
    //v2.7.4: moved up 
    // dataSource.connect();
    
    loadData();
  }

  @Override
  protected void deleteBoundedData() {
    deleteValues();
  }
  
  @Override
  protected List getDisplayValues(DAttr domainConstraint) {
    List values = new ArrayList();
    Iterator sit = getBoundValues();
/*v3.2    Iterator sit;
    
    if (isBounded()) //boundConstraint != null)
      sit = dataSource.getBoundedValues(boundConstraint);
    else
      sit = dataSource.iterator();
*/    
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

  /**
   * @effects
   *   return the currently selected bounded object of this
   *     
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JDataField#getRawValue()
   */
  @Override
  protected Object getRawValue() throws NotFoundException {
    Object displayVal = getSelectedValue();
    
    if (displayVal == null || displayVal.equals(Nil)) {
      return null;
    } else {
      if (isBounded()) //boundConstraint != null)
        return reverseLookUp(displayVal); //(C) dataSource.reverseLookUp(boundConstraint, displayVal);
      else
        return (C) displayVal;
    }
  }

  /**
   * @requires 
   *  {@link #isMultiValued()} = true
   *  
   * @effects
   *    return all currently selected bounded objects as {@link Collection}
   * @version 3.2
   */
  protected final Collection getRawValues() throws NotFoundException {
    Collection displayVals = getSelectedValues();
    
    if (displayVals == null) {
      return null;
    } else {
      if (isBounded()){ //boundConstraint != null) {
        // look up the bounded objects
        Collection boundedObjs = GUIToolkit.newEmptyCollection(displayVals.getClass());
        Object boundedObj;
        for (Object displayVal : displayVals) {
          if (!displayVal.equals(Nil)) {
            boundedObj = reverseLookUp(displayVal); //(C) dataSource.reverseLookUp(boundConstraint, displayVal);
            boundedObjs.add(boundedObj);
          }
        }
        
        if (!boundedObjs.isEmpty())
          return boundedObjs;
        else
          return null;
      } else {
        return displayVals;
      }
    }
  }

  /**v3.2: changed to support multi-valued
  @Override
  public C getValue() throws ConstraintViolationException {
    //Object v;

    C rawValue;
    
    // get the raw value first
    try {
      rawValue = getRawValue();
    } catch (NotFoundException e) {
      // raw value is not one of the allowed values
      displayError(null, true, false, false);
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_VALUE, e, 
          "{0}: Giá trị dữ liệu không hợp lệ {1}",
          this,
          "" // cannot determine exactly the value currently displayed on the textfield of the spinner
          );
    }
    
    // v2.6.1/2.6.4.a: if bounded then validate the value (if needed)
    if (//!autoValidation &&           // auto-validation is OFF
        !validatedOnItemChanged  // new value item but has not been validated
        //&& (rawValue != value)   // v2.7.3: validate only when changed value is different from the previous value (
        //two values may be the same if user entered the same value on the keyboard)
        ) {
      // 2.6.4.a: value has been changed but has not be validated
      if (isBounded()) { //bounded field
        if (validator != null && // a validator used to validate is specified
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
            ConstraintViolationException.Code.INVALID_VALUE,
            "{0}: Giá trị dữ liệu không hợp lệ {1}",
            this,
            null);   
      }
    } 

    if (!validated) validated = true;

    // v2.7.2: make sure that status icon is reset
    updateGUI(false);
    
    // validated ok, update value
    value = rawValue;

    return (C) value;
  }
  */
  
  /**
   * 
   * @effects
   *  if {@link #isMultiValued()}
   *    return all currently selected objects of this
   *  else
   *    return the currently selected object of this
   */
  /* (non-Javadoc)
   * @see domainapp.basics.view.datafields.JBindableField#getValue()
   */
  @Override
  public Object getValue() throws ConstraintViolationException {
    if (isMultiValued()) {
      return getValues();
    } else {
      return getSingleValue();
    }
  }
  
  /**
   * This is the original implementation of {@link #getValue()}.
   * 
   * @effects 
   *  return the first selected object of this
   *  
   * @version 3.2
   */
  protected final Object getSingleValue() throws ConstraintViolationException {
    //Object v;

    Object rawValue;
    
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
    
    // v2.6.1/2.6.4.a: if bounded then validate the value (if needed)
    if (//!autoValidation &&           // auto-validation is OFF
        !validatedOnItemChanged  // new value item but has not been validated
        //&& (rawValue != value)   // v2.7.3: validate only when changed value is different from the previous value (
        //two values may be the same if user entered the same value on the keyboard)
        ) {
      // 2.6.4.a: value has been changed but has not be validated
      if (isBounded()) { //bounded field
        if (isValueValidable() && //validator != null && // a validator used to validate is specified
            rawValue != null) {
          validateBoundedValue(rawValue);
        }
      } else {
        validateValue(rawValue, getDomainConstraint());// dconstraint);
      }
      validatedOnItemChanged = true;
    } else { // validatedOnItemChanged = true
      // either value has not been changed or it has been changed and has been validated
      // check the first case
      if (rawValue == null && requireValues()) {
        updateGUI(true);  // display error icon
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE,
            new Object[] {this + " = " + null});   
      }
    } 

    setIsValidated(true); //if (!validated) validated = true;

    // v2.7.2: make sure that status icon is reset
    updateGUI(false);
    
    // validated ok, update value
    setValueDirectly(rawValue); //value = rawValue;

    return getValueDirectly(); //(C) value;
  }

  //@Override
  protected final Collection getValues() {
    Collection rawValues;
    
    // get the raw value first
    try {
      rawValues = getRawValues();
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
    
    // v2.6.1/2.6.4.a: if bounded then validate the value (if needed)
    if (!validatedOnItemChanged  // new value item but has not been validated
        ) {
      // 2.6.4.a: value has been changed but has not be validated
      if (isBounded()) { //bounded field
        if (isValueValidable() && // validator != null && // a validator used to validate is specified
            rawValues != null) {
          for (Object rawValue : rawValues)
            validateBoundedValue(rawValue);
        }
      } else {
        DAttr dconstraint = getDomainConstraint();
        for (Object rawValue : rawValues)
          validateValue(rawValue, dconstraint);
      }
      validatedOnItemChanged = true;
    } else { // validatedOnItemChanged = true
      // either value has not been changed or it has been changed and has been validated
      // check the first case
      if (rawValues == null && requireValues()) {
        updateGUI(true);  // display error icon
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE,
            "{0}: Giá trị dữ liệu không hợp lệ {1}",
            this,
            null);   
      }
    } 

    setIsValidated(true); //if (!validated) validated = true;

    // v2.7.2: make sure that status icon is reset
    updateGUI(false);
    
    // validated ok, update value
    //value = rawValue;

    return rawValues;
  }

  /**
   * 
   * @effects
   *  if {@link #isMultiValued()}
   *    set all objects in <tt>val</tt> as selected objects of this
   *  else
   *    set <tt>val</tt> to be the selected object of this
   */
  @Override
  public void setValue(Object val) {
    if (isMultiValued()) {
      // multi valued
      setValues((Collection)val);
    } else {
      // single value
      setSingleValue(val);
    }
  }

  /**
   * @effects
   *  call <tt>super</tt>{@link #setValue(Object)}<tt>(val)</tt>  
   *
   * @version 3.2
   */
  protected final void setSingleValue(Object val) {
    super.setValue(val);
  }

  /**
   * @requires 
   *  vals contain domain values to be displayed on this 
   *  
   * @effects 
   *  set {@link #value} to first value in <tt>vals</tt>,
   *  update selected values of this to <tt>vals</tt> 
   *  
   * @version 3.2
   */
  protected final void setValues(Collection vals) {
    if (vals != null) {
      // indices of the display values of vals
      List<Integer> displayIndicesToSelect = new ArrayList(); 
          
      boolean firstVal = true;
      Object dispVal;
      int index;
      for (Object val : vals) {
        if (firstVal) { // use first value
          setValueDirectly(val);//value = val;
          firstVal = false;
        }
        
        dispVal = getDisplayValue((C) val);
        
        index = getValueIndex(dispVal);
        displayIndicesToSelect.add(index);
      }
      
      // update selected values to displayIndices
      setSelectedValues(displayIndicesToSelect);
    } else {
      setNullValue();
    }
  }

  /**
   * Implements the <code>ChangeListener</code> interface.
   * <p>
   * This method is invoked when a state change event is fired by a data source
   * (typically the {@see DomainSchema} object of the application).
   */
  public void stateChanged(ChangeEvent e) {
    /* v2.7.2: 
     * if data source is not null 
     *  only update state if this data field is connected to the data source
     */
    if (hasDataSource() // dataSource != null 
        && !isConnectedDataSource())
      // not yet connected
      return;
    
    // received when the values list to which this data field is bound
    // is changed.
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    
    if (boundConstraint != null) {
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
        addValues(bvals);
      } else if (ds.isDelete()) {
        // delete
        // v2.6.4.b: use a separate method for delete
        //model.deleteValues(bvals);
        deleteValues(objects, bvals);
      } else if (ds.isUpdate()) {
        if (isBounded()) {
          /*v2.7.2: suport ObjectUpdateData 
          */
          ObjectUpdateData data = (ObjectUpdateData) ds.getEventData();
          if (data != null) {
            Collection<DAttr> affectedAttributes = data.getUpdatedAttribs();
            Object o = objects.get(0);
            for (DAttr attrib : affectedAttributes) {
              if (attrib.equals(boundConstraint)) {
                // found the item
                Object oldVal = data.getOldVal(attrib);
                Object newVal = data.getNewVal(attrib);
                
                // update value 
                updateValue(o, oldVal, newVal);
                break;
              }
            }
          }
        }
      }
    }
  }  

  @Override
  protected void handleFocusLost() throws ConstraintViolationException {
    // debug
    // System.out.printf("%s.focusLost%n",((JTextComponent)src).getParent());
    if (isAutoValidation() && //autoValidation &&           // auto-validation is ON
        !validatedOnItemChanged  && // new value item but has not been validated 
        isValidated() // validated    // previously-validated (i.e. skip if not already validated)
        ) {
      // 2.6.4.a: value has been changed but has not be validated
      Object rawValue; //3.2: C rawValue
      
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
        if (isValueValidable() && //validator != null && // a validator used to validate is specified
            rawValue != null) {
          validateBoundedValue(rawValue);
        }
      } else {
        validateValue(rawValue, getDomainConstraint()); //dconstraint);
      }
      validatedOnItemChanged = true;
    } else { 
      //TODO: should we also handle the case where user has not changed the value, i.e. validatedOnItemChanged=true?
      // (see getValue())
      // do nothing
    } 
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
        setIsValidated(true);//validated = true;
      } catch (ConstraintViolationException e) {
        setIsValidated(false);//validated = false;
        // v2.6.1: to display dialog message because error icon is not displayed
        // for spinner field on table when this occurs
        //displayError(e.getMessage(), true, true, false);
        if (isAutoValidation()) //autoValidation) // v2.7.3: added this check
          displayError(e.getCode(), e// e.getMessage()
              , true, true, false);
        else 
          displayError(e.getCode(), e// e.getMessage()
              , true, false, false);
        
        throw e;
      } catch (NotFoundException e) {
        // ignore this
        updateGUI(false);
        setIsValidated(true);//validated = true;
      }
  }

  @Override
  public void reset() {
    // resets both the value and the display value
    Object displayValue;

// v3.2: FIXED to display init value first (if it is specified)    
//    // TODO: should we do value = initVal first??
//    // the only reason that we have the following check is because historically list-typed
//    // data fields donot display Nil if requireValues()=true, it displays the first value 
//    // of the list
//    if (hasDataSource() // dataSource != null 
//        && requireValues() && !isDataSourceEmpty()){// !dataSource.isEmpty()) {
//      // get the first object from data source
//      displayValue = getFirstValue();
//      
//      // reset value based on display value
//      if (isBounded()) {
//        if (!displayValue.equals(Nil))  { // v3.1
//          try {
//            setValueDirectly(reverseLookUp(displayValue)); //value = dataSource.reverseLookUp(boundConstraint,displayValue);
//          } catch (Exception e) {
//            // should not happen
//            if (debug) { 
//              System.out.printf(getParentContainer()+"."+this+"reset(): %n");
//              e.printStackTrace();
//            }
//            setValueDirectly(getInitValue()); //value = getInitValue(); 
//
//            /*v3.2 
//            if (value != null) {
//              displayValue = getDisplayValue((C) value);
//            } else {
//              displayValue = Nil;
//            }*/
//            displayValue = getDisplayValueWithNilSupport(getInitValue());
//          }
//        } else { // displayValue is Nil
//          setValueDirectly(getInitValue()); //value = getInitValue();
//
//          /*if (value != null) {
//            displayValue = getDisplayValue((C) value);
//          } else {
//            displayValue = Nil;
//          }*/
//          displayValue = getDisplayValueWithNilSupport(getInitValue());
//        }
//      } else {
//        setValueDirectly(displayValue); //value = displayValue;
//      }
//    } else {
//      // reset to initial value (whatever that is)
//      setValueDirectly(getInitValue()); //value = getInitValue(); 
//
//      /*if (value != null) {
//        displayValue = getDisplayValue((C) value);
//      } else {
//        displayValue = Nil;
//      }*/
//      displayValue = getDisplayValueWithNilSupport(getInitValue());
//    }

    Object initValue = getInitValue();
    Object resetValue = null;
    if (initValue != null) {
      // use init value
      resetValue = initValue;  
      displayValue = getDisplayValueWithNilSupport(initValue);
    } else {
      // no init value: use the first available display value or Nil if list is empty
      if (hasDataSource() && requireValues() && !isDataSourceEmpty()) {
        // data source is specified /\ value is required /\ data source is not empty
        displayValue = getFirstValue();
        
        // reset value based on display value
        if (isBounded()) {
          if (!displayValue.equals(Nil))  { // v3.1
            try {
              resetValue = reverseLookUp(displayValue); 
            } catch (Exception e) {
              // should not happen
              if (debug) { 
                System.out.printf(getParentContainer()+"."+this+"reset(): %n");
                e.printStackTrace();
              }
              resetValue = null;  
              displayValue = Nil;
            }
          } else { // displayValue is Nil
            resetValue = null;
            displayValue = Nil;
          }
        } else { // not bounded, use value directly
          resetValue = displayValue;
        }
      } else {
        // EITHER data source is NOT specified \/ value is NOT required \/ data source IS empty
        // i.e no value available to use for display: use Nil
        resetValue = null;
        displayValue = Nil;
      }
    }
    
    setValueDirectly(resetValue);
    
    setDisplayValue(displayValue);
    
    // validation is false
    //validated = false;
    setIsValidated(false);
  }
  
  /**
   * @effects 
   *  if value != null
   *    return {@link #getDisplayValue(Object)}<tt>(value)</tt>
   *  else
   *    return {@link #Nil}
   *    
   * @version 3.2
   */
  private Object getDisplayValueWithNilSupport(Object value) {
    Object displayValue;
    if (value != null) {
      displayValue = getDisplayValue((C) value);
    } else {
      displayValue = Nil;
    }
    
    return displayValue;
  }

  /**
   * @effects 
   *  set {@link #validatedOnItemChanged} to <tt>tf</tt> if it differs
   *  
   * @version 3.2
   */
  protected void setIsValidatedOnItemChanged(boolean tf) {
    if (validatedOnItemChanged != tf)
      validatedOnItemChanged = tf;    
  }

  /**
   * @effects 
   *  refresh this to show the most up-to-date data
   */
  public void refresh() throws NotPossibleException {
    loadData();
  }
  
  /**
   * @effects <pre>
   *  get the display values for this (requires reading from the data source if this is bounded)
   *  update this with the display values
   *  set the initially selected value either to this.value (if specified) or the first display value</pre>
   */
  protected void loadData() throws NotPossibleException {
    Object dispVal;
    List displayValues = getDisplayValues(getDomainConstraint()); //dconstraint); 
    
    Object value = getValueDirectly();
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
      
    //debug: System.out.println("loadData()...");
    
    setValues(displayValues);
    setDisplayValue(dispVal);
  }
  
  /**
   * @effects 
   *  if  <tt>selectedObj</tt> is not Nil
   *    set <tt>this.value</tt> to the actual value that is mapped to <tt>selectedObj</tt>
   *  else
   *    set <tt>this.value = null</tt>
   */
  protected void setValueOnSelection(Object selectedObj) {
    if (selectedObj.equals(Nil)) {
      setValueDirectly(null);// value = null;
    } else {
      if (isBounded()) //boundConstraint != null)
        setValueDirectly(reverseLookUp(selectedObj)); //value = (C) dataSource.reverseLookUp(boundConstraint, selectedObj);
      else
        setValueDirectly(selectedObj); //value = (C) selectedObj;
    }  
  }
  
  protected abstract void setValues(List displayValues);

  protected abstract void addValues(List displayValues);
  
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
  protected abstract void deleteValues(List objects, List displayObjects);

  /**
   * @effects <pre>
   *  delete all values contained in this and  
   *  if this is bounded 
   *    keep this.value (the currently bounded value object) unchanged
   *  else
   *    set this.value = initVal (the initial value)</pre>
   */
  protected abstract void deleteValues();
  
  /**
   * @effects 
   *  change the item <tt>oldVal</tt> in this to <tt>newVal</tt>  
   */
  protected abstract void updateValue(Object o, Object oldVal, Object newVal);
  
  protected abstract Object getFirstValue();

  /**
   * @effects 
   *  if there is a selected display value in this
   *    return it
   *  else
   *    return null
   */
  protected abstract Object getSelectedValue();
  

  /**
   * @effects 
   *  if there are selected display values in this
   *    return them as {@link Collection}
   *  else
   *    return <tt>null</tt>
   * @version 3.2
   */
  protected abstract Collection getSelectedValues();
  

  /**
   * @effects 
   *  update the display values of this such that those found in <tt>selectedIndices</tt> are selected 
   *  
   * @version 3.2
   */
  protected abstract void setSelectedValues(List<Integer> selectedIndices);

  /**
   * @requires 
   *  dispVal is in the current display values of this
   *  
   * @effects 
   *  Return the index of <tt>dispVal</tt> (starting from 0) that is found in the current display values 
   *  of this 
   *  
   * @version 3.2
   */
  protected abstract int getValueIndex(Object dispVal);
  
  @Override
  public TableCellEditor toCellEditor() {
    // v5.1c: if (dataCellEditor == null) {
    DataCellEditor dataCellEditor = getDataCellEditor(); 
    if (dataCellEditor == null) {
      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      DAttr boundConstraint = getBoundConstraint(); // v5.1c:
      
      dataCellEditor = new ListDataCellEditor(this, 
          new DAttr[] {
              dconstraint,
              boundConstraint
          });
      
      setDataCellEditor(dataCellEditor);
    }

    return dataCellEditor;
  }
  
  @Override
  protected void setDisplayValue(Object dispVal) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // TODO Auto-generated method stub
    return null;
  }


  /**
   * @overview 
   *  A sub-class of {@link JDataField.DataCellEditor} to provide both domain constraint 
   *  and bound constraint. 
   * @author dmle
   */
  protected class ListDataCellEditor extends JDataField.DataCellEditor {
    
    private JDataField owner;
    private DAttr[] constraints;
    
    public ListDataCellEditor(JDataField owner, DAttr[] constraints) {
      this.owner = owner;
    }
    
    @Override
    public Object getCellEditorValue() {
      // use raw value
      Object val = owner.getValue(true); //JComboField.this.getValue(true);
      return val;
    }
    
    @Override
    public DAttr[] getDomainConstraints() {
      if (constraints == null) {
        constraints = new DAttr[] { // 
            JAbstractListDataField.this.getDomainConstraint(), //dconstraint, //
            JAbstractListDataField.this.getBoundConstraint(), //boundConstraint, // 
            };
      }

      return constraints;
    }
  }
}
