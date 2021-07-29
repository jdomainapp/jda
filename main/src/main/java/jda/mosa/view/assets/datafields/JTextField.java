package jda.mosa.view.assets.datafields;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.GUIToolkit;

/**
 * @overview 
 *  Represents a <b>bindable</b> text field.
 *  
 *  <p>A <b>bounded</b> text field must be non-editable and is created with a bound constraint. This constraint 
 *  is used to look up the bounded value to be displayed on the field. 
 *  
 * @author dmle
 * 
 * @version 
 * - 3.2: fixed {@link #loadBoundedData()} to do nothing
 */
public class JTextField<C> extends JBindableField {

  // bounded
  public JTextField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);
    
    // bounded text field must be non-editable
    if (boundConstraint != null && editable==true) {
      // not supported
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_TEXT_FIELD_BOUNDED_AND_EDITABLE, 
          new Object[] {domainConstraint.name(), boundConstraint.name()});
    }
  }

  // bounded
  public JTextField(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound 
      DAttr domainConstraint, DAttr boundConstraint, Boolean editable) throws ConstraintViolationException {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable);
    
    // bounded text field must be non-editable
    if (boundConstraint != null && editable==true) {
      // not supported
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_TEXT_FIELD_BOUNDED_AND_EDITABLE, 
          new Object[] {domainConstraint.name(), boundConstraint.name()});
    }
  }
  
  // unbounded
  public JTextField(DataValidator validator, Configuration config, C val, 
      DAttr domainConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException  {
    super(validator, config, val, null, domainConstraint, null, editable, autoValidation);
  }

  // unbounded
  public JTextField(DataValidator validator, Configuration config, C val, 
      DAttr domainConstraint, Boolean editable) throws ConstraintViolationException {
    // auto-validation=true
    super(validator, config, val, null, domainConstraint, null, editable, true);
  }

  // unbounded
  public JTextField(DataValidator validator, Configuration config, 
      DAttr domainConstraint) throws ConstraintViolationException {
    super(validator, config, null, null, domainConstraint, null, true);
  }
  
  /**
   * This method is only used if this is a text field.
   * 
   * @effects 
   *  if masked = true
   *    initialise <tt>this.display</tt> as a <tt>JPasswordField</tt> with length <tt>length</tt> and 
   *    initial value <tt>val</tt>
   *  else 
   *    initialise <tt>this.display</tt> as a <tt>JTextField</tt> with length <tt>length</tt> and 
   *    initial value <tt>val</tt>
   *    
   *  <br>Return <tt>this.display</tt>
   */
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    JComponent actualDisplay = createTextFieldComponent(tfh);
    
    setUpTextField();
    setUpListener(tfh);
    
    return actualDisplay;
  }
  
  /**
   * @effects 
   *  create and return a <tt>JTextField</tt> that satisfies the configuration of this 
   */
  protected JComponent createTextFieldComponent(DataFieldInputHelper tfh) {
    // default length if no length is specified
    // v3.2: moved to below to use width if specified
    // int length = getDomainFieldWidth();
    
    // v3.0: support configured dimension (override length above)
    Integer width = null, height = null;
    Dimension configDim = getConfiguredDimension();
    
    if (configDim != null) {
      // configured width 
      width = (int) configDim.getWidth(); 

      // configured height
      height = (int) configDim.getHeight();
    }
    
    int length;
    if (width != null)
      length = width;
    else
      length = getDomainFieldWidth();
    
    JComponent actualDisplay = null;
    if (length > MAX_DISPLAYABLE_TEXT_WIDTH) {
      // text area
      if (width == null) width = DEFAULT_TEXT_WIDTH;
      if (height == null) height = MAX_DISPLAYABLE_TEXT_HEIGHT;

      JTextArea ta = new JTextArea(height, width);

      ta.setLineWrap(true);
      
      // v4.0: wrap at word boundary
      ta.setWrapStyleWord(true);
      
      /*v5.1c: 
      display = ta;
      actualDisplay = new JScrollPane(display);
      */
      setGUIComponent(ta);
      actualDisplay = new JScrollPane(ta);
    } else {
      // text field
      javax.swing.JTextField tf = new javax.swing.JTextField(length);
      
      /*v3.2: FIXED use width as text length, not pixels (above)
      // v3.0: support configured dimension
      if (width != null)
        tf.setPreferredSize(new Dimension(width, tf.getPreferredSize().height));
      */
      
      int align = getAlignX();
      tf.setHorizontalAlignment(align);
      
      /* v5.1c: 
      display = tf;
      actualDisplay = display;
      */
      setGUIComponent(tf);
      actualDisplay = tf;      
    }

    // display value
    Object value = getValueDirectly(); // v5.1c
    if (value != null)
      displayFormattedValue();

    return actualDisplay;
  }

  @Override
  protected void loadBoundedData() throws NotPossibleException {
    /*v3.2: moved the following to JTextFieldAuto, do nothing here
     loadBoundedDataSingle();
     */
  }

  @Override
  protected void displayFormattedValue() {
    // 
    //((JTextComponent) display).setText(value.toString());
    Object value = getValueDirectly(); // v5.1c
    if (value != null)
      displayFormattedValue(value);
  }

  //@Override
  /**
   * @requires 
   *  val != null
   * 
   * @effects
   *  convert <tt>val</tt> to formatted string suitable for this and displays it 
   */
  protected void displayFormattedValue(Object val) {
    // 
    JComponent display = getGUIComponent(); // v5.1c:
    
    String formatted = getFormattedValue(val);
    ((JTextComponent) getGUIComponent()).setText(formatted);
    
    // v3.0: support auto-scroll
    //TODO: add to field config
    boolean autoScroll = true;
    if (autoScroll && display instanceof JTextArea) {
      ((JTextArea)display).setCaretPosition(formatted.length());
    }
  }
  
  @Override
  public String getFormattedValue(Object val) throws NotPossibleException {
    return val.toString();
  }
  
  protected void setUpTextField() {
    
    setEditable(// v5.1c: //editable
        getEditable());

    // border color the id field 
    if (isId()) {
      Color BORDER_COLOR = new Color(0, 100, 255);
      JComponent display = getGUIComponent(); // v5.1c:
      // display.setBackground(new Color(120,120,120));
      display.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createLineBorder(BORDER_COLOR, 2),
          BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }
  }

  protected void setUpListener(DataFieldInputHelper tfh) {
    JComponent display = getGUIComponent(); // v5.1c:
    
    display.addKeyListener(tfh);
    display.addFocusListener(tfh);      
  }

  /**
   * @effects 
   *  if this is the text field
   *    sets the horizontal alignment of the text of this to <tt>alignX</tt>
   */
  public void setAlignX(AlignmentX alignX) {
    JComponent display = getGUIComponent(); // v5.1c:
    if (display instanceof javax.swing.JTextField) {
      int align = GUIToolkit.toSwingAlignmentX(alignX);
      ((javax.swing.JTextField) display).setHorizontalAlignment(align);
    }
  }

  /**
   * @effects
   *  if this is a text field
   *    set its length (in number of characters) to <tt>lengthInChars</tt>
   *  else
   *    do nothing 
   */
  public void setTextLength(int lengthInChars) {
    JComponent display = getGUIComponent(); // v5.1c:
    
    if (display instanceof javax.swing.JTextField)
      ((javax.swing.JTextField) display).setColumns(lengthInChars);
  }
  
  @Override
  public Object getValue() throws ConstraintViolationException {
    // ASSUME: 
    // - bounded text fields are non-editable
    
    Object v;
    // if bounded then simply return value
    // otherwise if the text on the text field is not yet validated
    // validate it and use the validated value if succeeds 
    
    if (!isBounded() && !//v5.1c: validated
        isValidated()
        ) {
      // v2.6.2
      v = getRawTextValue(); 
      
      /*v3.0: moved to getRawTextValue
      // empty strings are treated as null
      if (v != null && v.equals(Nil))
        v = null;
       */
      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      
      // v5.1c: value = validateValue(v, dconstraint);
      setValueDirectly(validateValue(v, dconstraint));
    }

    return getValueDirectly(); // v5.1c: value;
  }
  
  @Override
  protected C getRawValue() throws NotFoundException {
    /*v3.0: support exception 
    Object v;
    if (!isBounded()) {
      v = getRawTextValue();
      
      //v3.0: moved to getRawTextValue
//      // empty strings are treated as null
//      if (v != null && v.equals(Nil))
//        v = null;
    } else {
      v = // v3.0: getTextObjectValue();
          getRawTextValue();
      
      // v2.7.4: look up bounded object
      if (v != null) {
        return (C) dataSource.reverseLookUp(boundConstraint, v);
      } 
    }
    */

    C v = null;
    try {
      Object raw  = getRawTextValue();
      
      if (isBounded()) {
        // v2.7.4: look up bounded object
        if (raw != null) {
          DAttr boundConstraint = getBoundConstraint(); // v5.1c:
          JDataSource dataSource = getDataSource(); // v5.1c
          
          v = (C) dataSource.reverseLookUp(boundConstraint, raw);
        } 
      } else {
        v = (C) raw;
      }
    } catch (ConstraintViolationException | NotPossibleException e) {
      v = null;
      displayError(e.getCode(), e// e.getMessage()
          , false, false, false);
    }

    return v;
  }

  @Override
  protected void setDisplayValue(Object dispVal) {
    JComponent display = getGUIComponent(); // v5.1c:
    
    if (display != null) {
      if (dispVal != null)
        displayFormattedValue(dispVal);

      // v2.7.4
      if (!//v5.1c: validated
          isValidated()
          ) //validated = true;
        setIsValidated(true);
      
      /** need to reset the indicator here */
      updateGUI(false);
      //debug
      //System.out.printf("%s.text = %s%n", this, ((JTextComponent) display).getText());
    }
  }
  
  @Override
  public void setEditable(boolean state) {
    super.setEditable(state);
    
    JComponent display = getGUIComponent(); // v5.1c:
    
    ((JTextComponent)display).setEditable(state);
  }
  
  
  /**
   * @effects 
   *  return the raw text object contained in this 
   *  
   *  <p>throws ConstraintViolationException if the raw text is not valid (e.g. 
   *  violate the text format)
   */  
  protected Object getRawTextValue() 
      throws ConstraintViolationException // v3.0 
  {
    Object v;
    
    JComponent display = getGUIComponent(); // v5.1c:
    
    String vs = ((JTextComponent) display).getText();
    if (vs != null && !vs.equals(Nil)) {
      // parse value (if needed)
      try {
        v = parseFormattedValue(vs);
      } catch (ParseException e) {
        /*v3.0
        // reset to empty on error 
        v = Nil;
        */
        throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_FORMAT_RAW_TEXT_VALUE, 
            new Object[] {getAttributeName(), vs, getFormatString()});
      }
    } else {
      v = vs;
    }
    
    // v3.0: empty string is treated as null
    if (v != null && v.equals(Nil))
      v = null;
    
    return v;
  }
  
  /**
   * @requires 
   *  {@link #isSupportValueFormatting()} = true
   * 
   * @effects 
   *  return the the format string that is used by this to format the value
   */
  protected String getFormatString() {
    // for sub-types to implement
    return null;
  }

  /**
   * @effects 
   *  return the text component of this
   * @version 2.7.4
   */
  protected JTextComponent getTextComponent() {
    JComponent display = getGUIComponent(); // v5.1c:
    return (JTextComponent) display;
  }

  // v3.0: removed
//  /**
//   * @effects Returns the raw value object of the text display component
//   */
//  protected Object getTextObjectValue() throws ConstraintViolationException {
//
//    Object v = getRawTextValue(); 
//      
//    // empty strings are treated as null
//    if (v != null && v.equals(Nil))
//      v = null;
//
//    return v;
//  }
  
  @Override
  protected void handleKeyTyped(KeyEvent e) {
    // if enter key was pressed then update value
    if (e.getKeyChar() == e.VK_ENTER) {
      // validate if needed
      if (//v5.1c: autoValidation
          isAutoValidation()
          ) {
        /*v3.0: support exception 
        Object val = // v3.0: getTextObjectValue();
            getRawTextValue();
        
        boolean changed = validate(val);
        if (changed) {
          // value was changed
          fireValueChanged();
          
          // format value
          displayFormattedValue();
        }
         */
        try {
          Object val = getRawValue();
          boolean changed = validate(val);
          if (changed) {
            // value was changed
            fireValueChanged();
            
            // display the value (with format if any)
            displayFormattedValue();
//            Object valTxt = getRawTextValue();
//            displayFormattedValue(valTxt);
          }
        } catch (Exception ex) {
          // error: display it
          setIsValidated(false);
        }
      }
    } else {
      setIsValidated(false);
//      if (validated)
//        validated = false;
    }
  }
  
  @Override
  protected void handleFocusLost() {
    // validate text field if needed
    if (//v5.1c: autoValidation
        isAutoValidation()
        ) {
      /*v3.0: support exception 
      Object val = // v3.0: getTextObjectValue();
          getRawTextValue(); 

      boolean changed = validate(val);
      if (changed) {
        // value was changed
        fireValueChanged();
        
        // set format value
        displayFormattedValue();
      }
      */
      try {
        Object val = getRawValue();
        boolean changed = validate(val);
        if (changed) {
          // value was changed
          fireValueChanged();
          
          // display the value (with format if any)
          displayFormattedValue();
//          Object valTxt = getRawTextValue();
//          displayFormattedValue(valTxt);
        }
      } catch (Exception e) {
        // error: display it
        setIsValidated(false);
      }
    }
  }
  
  @Override
  protected void handleFocusGained() {
    // highlight text
    // debug
    //System.out.printf("%s.focusGained%n",JDataField.this.getClass());
    JComponent display = getGUIComponent(); // v5.1c:
    
    final JTextComponent tf = (JTextComponent) display;
    tf.selectAll();
  }
  
  /**
   * @modifies this
   * @effects 
   *  if autoValidation=true and validated=false
   *    validate the text value of <tt>comp</tt> and assign 
   *    it to <tt>value</tt>. 
   *    If succeeds (i.e. value was changed)
   *      return true
   *    else
   *      return false
   *  else
   *    return false
   */
  protected boolean validate(Object val) {
    if (// -- redundant -> autoValidation && 
        !//v5.1c: validated
        isValidated()
        ) {
      try {
        DAttr dconstraint = getDomainConstraint();  // v5.1c:
        
        // 5.1c: value = validateValue(val, dconstraint);
        setValueDirectly(validateValue(val, dconstraint));
        
        return true;
      } catch (ConstraintViolationException ex) {
      }
    }
    
    // either no validation was performed or the value entered 
    // was invalid 
    return false;
  }
  
  @Override 
  public void deleteBoundedData() {
    /*v2.7.4: moved to super
    if (dataSource != null) {
      value = null;
      validated = false;
    }
     */
    deleteBoundedDataSingle();
  }

  /**
   * @effects 
   *  return a {@link TableCellEditor} that has both domain and bound constraints (if specified)
   *  
   * @version 
   * - 3.2: added this method to allow text field be used as proper bounded field
   * 
   */
  @Override
  public TableCellEditor toCellEditor() {
    // v5.1c: if (dataCellEditor == null) {
    DataCellEditor dataCellEditor = getDataCellEditor(); 
    if (dataCellEditor == null) {
      dataCellEditor = new DataCellEditor() {
        @Override
        public DAttr[] getDomainConstraints() {
          if (constraints == null) {
            constraints = new DAttr[] { // 
                JTextField.this.getDomainConstraint(), //
                JTextField.this.getBoundConstraint(), 
                };
          }

          return constraints;
        }
      };
      
      setDataCellEditor(dataCellEditor);
    }

    return dataCellEditor;
  }
}
