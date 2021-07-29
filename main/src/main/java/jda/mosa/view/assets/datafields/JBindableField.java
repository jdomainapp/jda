package jda.mosa.view.assets.datafields;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.util.events.ChangeEvent;

/**
 * @overview 
 *  A general data field that can be bounded to the domain field of a domain class.
 *   
 * @author dmle
 */

public abstract class JBindableField<C> extends JDataField 
//v2.7.2: implements ChangeListener 
implements JBoundedComponent
{
  // v5.1c: changed to private 
  private DAttr boundConstraint;
  
  // v5.1c: changed to private
  private JDataSource dataSource;
  
  // v2.7.2: added to know whether this data field is connected
  private boolean isConnectedToDataSource;

  // v2.7.4
  private boolean isLoaded;
  
  // a partial constructor: auto-validated
  public JBindableField(DataValidator validator, 
      Configuration config, 
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, 
      DAttr boundConstraint,
      Boolean editable) throws ConstraintViolationException {
    this(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, true);
  }
  
  // the complete constructor
  public JBindableField(DataValidator validator, 
      Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, 
      DAttr boundConstraint,
      Boolean editable, 
      Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, domainConstraint, editable, autoValidation);
    
    if (boundConstraint != null && dataSource == null)
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_VALUE, 
          "{0}<{1}>.init(): a data source must be specified for bound attribute {2}", this.getClass().getSimpleName(),
          domainConstraint.name(),
          boundConstraint.name());

    this.dataSource = dataSource;
    this.boundConstraint = boundConstraint;
    this.isLoaded = false;
  }

  @Override
  protected int getDomainFieldWidth() {
    int length = DEFAULT_FIELD_WIDTH;  // default length
    
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    
    if (isBounded() && boundConstraint.length() > 0) {
      // adjust the text field length based on the bound constraint
      length = boundConstraint.length();
    } else if (dconstraint != null && dconstraint.length() > 0) {
      // if not, use the field length (if specified)
      length = dconstraint.length();
    }
    
    return length;
  }

  /**
   * @effects 
   *  if boundConstraint is defined
   *    return it
   *  else
   *    return null
   */
  @Override // v2.7.4
  public DAttr getBoundConstraint() {
    return boundConstraint;
  }

  /**
   * @requires 
   *  {@link #hasDataSource()}
   *  
   * @effects 
   *  if {@link #dataSource}.isEmpty
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2
   */
  protected boolean isDataSourceEmpty() {
    return dataSource.isEmpty();
  }
  
  /**
   * @effects 
   *  if {@link #dataSource} != null
   *    return <tt>true</tt>
   *  else 
   *    return <tt>false</tt>
   * @version 3.2
   */
  protected boolean hasDataSource() {
    return dataSource != null;
  }

  /**
   * @effects 
   *  return {@link #dataSource}
   * @version 5.1c
   */
  protected JDataSource getDataSource() {
    return dataSource;
  }
  
  /**
   * @requires 
   *  {@link #hasDataSource()} /\ {@link #isBounded()}
   * @effects 
   *  return the domain object in {@link #dataSource} whose bounded value of {@link #getBoundConstraint()}
   *  is <tt>boundVal</tt>
   *  
   * @version 3.2
   */
  protected Object reverseLookUp(Object boundVal) {
    return dataSource.reverseLookUp(boundConstraint, boundVal);
  }
  
  /**
   * @effects 
   *  if this is bounded
   *    return an {@link Iterator} of the bounded values in {@link #dataSource}
   *  else
   *    return an {@link Iterator} of all values in {@link #dataSource}
   * @version
   */
  protected Iterator getBoundValues() {
    Iterator sit;
    
    if (isBounded()) //boundConstraint != null)
      sit = dataSource.getBoundedValues(boundConstraint);
    else
      sit = dataSource.iterator();
    
    return sit;
  }

  /**
   *  Default implementation for unbounded fields is to do nothing. Bounded fields must provide their 
   *  actual code as specified below.
   * 
   * @effects <pre>
   *  if dataSource != null
   *    connect to dataSource and if data has not been loaded then load data into this
   *    set flag this.isConnectedToDataSource = true
   *    throws NotPossibleException if fails to do this
   *  else
   *    do nothing
   *    </pre>
   */
  // v2.7.2: broken down into two steps, the first of which is abstract 
  //public abstract void connectDataSource() throws NotPossibleException;
  public void connectDataSource() throws NotPossibleException {
    if (dataSource != null) {
      try {
        // v2.7.4: added this
        dataSource.connect();
      
        /* v2.7.4: renamed
        doConnectDataSource()
        */
        
        /*v2.7.4: added check to avoid duplicate loading of data
         * between application's login and logout  
         */
        if (!isLoaded()) {
          loadBoundedData();
          isLoaded = true;
        }
        
        // set flag to true
        isConnectedToDataSource = true;
      } catch (NotPossibleException e) {
        // set flag to false: need to do this for the reloadData operation
        isConnectedToDataSource = false;
        throw e;
      }
    } else {
      isConnectedToDataSource = false;
    }
  }

  /**
   * @requires 
   *  isBounded /\ dataSource != null
   * @effects 
   *  if bounded data has been loaded from dataSource
   *    return true
   *  else
   *    return false
   * @version 2.7.4
   */
  private boolean isLoaded() {
    return isLoaded;
  }

  /**
   * @effects 
   *  set this.isLoaded = tf 
   * @version 2.7.4
   */
  protected void setIsLoaded(boolean tf) {
    this.isLoaded = tf;
  }
  
  /**
   * Bounded fields must provide their actual code as specified below.
   * 
   * @effects <pre>
   *  if dataSource != null
   *    connect to dataSource and load values into this
   *    throws NotPossibleException if fails to do this
   *  else
   *    do nothing
   *    </pre>
   */
  protected abstract void loadBoundedData() throws NotPossibleException; 
  
  /**
   * This method is used to be invoked by single-valued sub-types (e.g. text and date field) 
   * as part of their implementation of {@link JBindableField#loadBoundedData()}.
   *    
   * @requires 
   *  this is a single-valued data field
   *      
   * @effects 
   *  load the (only) bounded data object and display it on this.display (component) 
   */
  protected void loadBoundedDataSingle() {
    // if there are data then get it to use
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    
    List displayVals = getDisplayValues(dconstraint);
    
    Object displayVal;
    Object val = null;
    if (displayVals != null) {
      // only one 
      displayVal = displayVals.get(0);
      
      if (!displayVal.equals(Nil))
        val = dataSource.reverseLookUp(boundConstraint, displayVal);
      
      if (val != null)
        setValidatedValue(val);
      
      // display the value
      setDisplayValue(displayVal);
    }
  }

  /**
   * @effects 
   *  if this.dataSource != null AND this has successfully connected to the data source
   *    return true
   *  else
   *    return false
   */
  public boolean isConnectedDataSource() {
    return isConnectedToDataSource;
  }
  
  /**
   * @requires
   *  isBounded
   *  
   * @effects
   *  if data source != null
   *    delete existing bounded data in this 
   *    reload the data bounded to this and update this to show them
   *    
   * @version 2.7.2
   */
  public void reloadBoundedData() {
    /*v2.7.4
    //TODO: would be better if we make the sub-types implement this method
    connectDataSource();
    */
    // debug: System.out.println("reloadBoundedData()...");

    deleteBoundedData();
    
    loadBoundedData();
  }

  /**
   * @effects 
   *  if data source != null
   *    delete existing bounded data in this 
   * @version 2.7.4
   */
  protected abstract void deleteBoundedData();

  /**
   * This method is used to be invoked by single-valued sub-types (e.g. text and date field) 
   * as part of their implementation of {@link JBindableField#deleteBoundedData()}.
   * 
   * @requires 
   *  this is a single-valued data field
   *  
   * @effects 
   *  implement {@link #deleteBoundedData()} but for single-valued data field
   * @version 2.7.4 
   */
  protected void deleteBoundedDataSingle() {
    if (dataSource != null) {
      /* 
       * clear the data objects that have been loaded via the binding (without removing the binding)
       */
      setNullValueDirectly(); //v5.1c: value = null;
      //validated = false;
      setIsValidated(false);
      
      // reset
      updateGUI(false);
    }    
  }

  @Override // JBoundeComponent
  public void clearBinding() {
    if (!isBounded()) {
      return;
    }

    if (dataSource != null) {
      // v2.7.4
      deleteBoundedData();
      
      setIsLoaded(false);
    }
  }

  @Override // JBoundedComponent
  public void refreshBinding() {
    if (dataSource != null) {
      // before reloading data, record the current value to determine if it was changed by the reload
      C beforeVal = (C) getValueDirectly();
      
      boolean isEmptyBefore = dataSource.isEmpty();
      
      // clear attached data source buffer
      clearDataSource();
      
      // reload data
      reloadBoundedData();
      
      C afterVal = (C) getValueDirectly();
      
      boolean isEmptyAfter = dataSource.isEmpty();
      boolean valueChanged = (isEmptyBefore == true) ? (isEmptyAfter == false): (isEmptyAfter == true);
      
      if (!valueChanged) {
        valueChanged = (beforeVal == null && 
                          (afterVal != null || !isEmptyAfter)) ||
                       (beforeVal != null && !beforeVal.equals(afterVal)); 
      }
      
      if (valueChanged) {
        // value is changed
        // debug: 
        //System.out.println(this+ ".refreshBinding()");
        
        fireValueChanged();
      }
    }
  }
  
  @Override
  public C getValue() throws ConstraintViolationException {
    Object value = getValueDirectly(); // v5.1c
    
    if (!//v5.1c: validated
        isValidated()
        )
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_VALUE, 
          "{0}: Giá trị dữ liệu không hợp lệ {1}",
          this, 
          value);
    
    return (C) value;
  }

  @Override
  public void setValue(Object val) {
    if (val != null) {
      setValueDirectly((C) val); // v5.1c: value = val;
      Object dispVal = getDisplayValue((C) val);

      setDisplayValue(dispVal);
    } else {
      /*v3.1: this causes a bug when initVal is used instead of setting value to null
      reset();
      */
      setNullValue();
    }
  }

  /**
   * This method is invoked when null is a valid value to be set for this.
   * 
   * @effects
   *  set this.value = null and update state accordingly (if needed)
   * @version 3.1
   */
  protected void setNullValue() {
    setValueDirectly(null);
    setDisplayValue(Nil);
  }

  /**
   * @effects 
   *  update <tt>display</tt> to show <tt>dispVal</tt>
   */
  protected abstract void setDisplayValue(Object dispVal);
  
  /**
   * @effects 
   *  if <code>boundConstraint != null</code> 
   *    return the value of the bounded domain attribute in the <code>val</code> object
   *  else
   *    return <code>val</code>
   */
  protected Object getDisplayValue(C val) {
    if (boundConstraint != null) {
      return dataSource.getBoundAttributeValue(val, boundConstraint);
    } else {
      return val;
    }
  }
  
  /**
   * @requires 
   *  dataSource != null
   * 
   * @effects 
   *  if boundConstraint != null
   *    return from dataSource an Iterator of the data value Objects of the bounded attribute represented by 
   *    boundConstraint 
   *  else
   *    return from dataSource an Iterator of the objects
   *    
   *  <p>Return <tt>null</tt> in both cases if no objects were found.
   */
  protected List getDisplayValues(DAttr domainConstraint) {
    List values = null; // new ArrayList();
    Iterator sit;
    
    if (boundConstraint != null)
      sit = dataSource.getBoundedValues(boundConstraint);
    else
      sit = dataSource.iterator();

    // add rest of values
    if (sit != null && sit.hasNext()) {
      values = new ArrayList();
      while (sit.hasNext())
        values.add(sit.next());
    }
    
    return values;
  }

  /**
   * @requires displayVals != null /\ size(displayVals) > 0
   *    /\ displayVals were created by {@link #getDisplayValues(DomainConstraint)}
   *    
   * @effects
   *  if displayVals contains non-empty values
   *    return the first of such value
   *  else
   *    return Nil ("")
   */
  /*v2.7.3: removed
  protected Object getFirstDisplayValue(List displayVals) {
    Object first = displayVals.get(0);
    if (first.equals(Nil) && displayVals.size() > 1) {       // the first value is Nil
      return displayVals.get(1);
    } else
      return first;
  }
  */
  
  /**
   * @effects 
   *  if <tt>boundConstraint != null</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean isBounded() {
    return (boundConstraint != null);
  }

  /**
   * @effects if the domain constraint states that a value must be specified for
   *          this field (i.e. <code>optional = false</code>) then returns
   *          <code>true</code>, otherwise returns <code>false</code>.
   */
  protected boolean requireValues() {
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    
    return (dconstraint != null && (dconstraint.optional() == false));
  }
  
  /**
   * @effects if <code>boundeConstraint != null</code> returns the bounded
   *          (display) value of <code>value</code>, else returns
   *          <code>value</code>.
   */
  public Object getBoundValue() throws ConstraintViolationException {
    C val = getValue();
    if (val != null)
      return getDisplayValue(val);
    else
      return null;
  }


  /**
   * @effects returns the object of the type <code>C</code>, which is specified
   *          in the domain constraint <code>constraint</code>; throws
   *          <code>IllegalArgumentException</code> if <code>value</code>'s type
   *          is not compatible with the type specified in the domain
   *          constraint.
   */
  protected C parseDisplayValue(Object value, DAttr d)
      throws IllegalArgumentException {
    Object val = value;

    if (d != null && value != null) {
      Type type = d.type();

      try {
        if (type.isString()) {
          val = value.toString();
        } else if (type.isNumeric()) {
          // create a number
          if (!(value instanceof Number)) {
            try {
              if (type.equals(Type.Integer)) {
                val = Integer.parseInt(value.toString());
              } else if (type.equals(Type.Long)) {
                val = Long.parseLong(value.toString());
              } else if (type.equals(Type.Float)) {
                val = Float.parseFloat(value.toString());
              } else if (type.equals(Type.Double)) {
                val = Double.parseDouble(value.toString());
              }
            } catch (NumberFormatException e) {
              // System.err
              // .println("DomainSchema.validateDomainValue: invalid value "
              // + value);
              throw new IllegalArgumentException("invalid value " + value);
            }
          }
        } // end is-numeric
        else if (type.isBoolean() && !(value instanceof Boolean)) {
          val = Boolean.parseBoolean(value.toString());
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("invalid value " + value);
      }
    }

    // if we get here then ok
    return (C) val;
  }

  /**
   * Implements the <code>ChangeListener</code> interface.
   * <p>
   * This method is invoked when a state change event is fired by a data source
   * (typically the {@see DomainSchema} object of the application).
   */
  public void stateChanged(ChangeEvent e) {
    // sub-classes should override 
  }

  @Override
  public void reset() {
    // resets both value and the actual value that is displayed
    // on the display component
    setValueDirectly(getInitValue()); // v5.1c: value = getInitValue(); 

    // the actual display value
    Object displayValue = null;
    Object value = getValueDirectly(); // v5.1c

    if (value != null) {
      displayValue = getDisplayValue((C) value);
    } 
    
    if (displayValue == null) {
      displayValue = Nil;
    }

    /*v2.7.4: moved to below setDisplayValue (below)
    // validation is false
    validated = false;
    */

    setDisplayValue(displayValue);
    
    setIsValidated(false);
  }
  
  @Override
  public void clear() {
    setNullValueDirectly(); //v5.1c: value = null;

    // the actual display value
    Object displayValue = Nil;

    setDisplayValue(displayValue);
    
    setIsValidated(true);
  }

  /**
   * @effects 
   *  if data source is specified
   *    clear any data objects in its buffer
   *  else
   *    do nothing
   *  @version 3.0
   */
  public void clearDataSource() {
    if (dataSource != null) {
      dataSource.clearBuffer();
    }
  }
}
