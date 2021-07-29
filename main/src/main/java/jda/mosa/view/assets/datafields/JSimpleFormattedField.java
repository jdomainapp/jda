package jda.mosa.view.assets.datafields;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.JComponent;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;

public class JSimpleFormattedField<C> extends JTextField {
  /** the display format used to display the value */
  private Format format;

  // bounded
  public JSimpleFormattedField(DataValidator validator, 
      Configuration config,
      C val,
      JDataSource dataSource, // the data source to which this field is bound 
      DAttr domainConstraint, DAttr boundConstraint, Boolean editable, Boolean autoValidation) {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);
  }

  // unbounded
  public JSimpleFormattedField(DataValidator validator, Configuration config, C val, 
      DAttr domainConstraint, Boolean editable, Boolean autoValidation ) {
    this(validator, config, val, null, domainConstraint, null, editable, autoValidation);
  }

  /**
   * @effects 
   *    initialise <tt>this.display</tt> as a formatted text field 
   *    
   *  <br>Return <tt>this.display</tt>
   */
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    /** initialise the format based on the data type and 
     *  the format string (if any) and create the display component */
    
    // format string is determined by the bound constraint (if specified) or the domain constraint
    DAttr.Format formatSpec;
    String formatString;
    Type type;
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    
    if (boundConstraint != null) {
      formatSpec = boundConstraint.format();
      type = boundConstraint.type();
    } else {
      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      
      formatSpec = dconstraint.format();
      type = dconstraint.type();
    }
    
    if (formatSpec.isNull()) {
      formatString = null;
    } else {
      formatString = formatSpec.getFormatString();
    }
    
    if (formatString == CommonConstants.NullString) {
      // no format string
      formatString = null;
    }
    
    // use the data type to determine the value format
    Locale currentLocale = Locale.getDefault();
    if (type.isDate()) {
      if (formatString != null)
        format = new SimpleDateFormat(formatString, currentLocale);
      else
        format = new SimpleDateFormat();
    } 
    else if (type.isInteger()) {
      // need to separate this for integer type
      //TODO test currency format
//      if (formatSpec.isCurrency()) {
//        format = NumberFormat.getCurrencyInstance();
//      } else {
        format = NumberFormat.getIntegerInstance();
//      }
    } 
    else if (type.isNumeric()) {
      // other numeric type use number format
      //TODO: test currency format
//      if (formatSpec.isCurrency()) {
//        format = NumberFormat.getCurrencyInstance();
//      } else {
        format = NumberFormat.getNumberInstance();
//      }
    }
    // add other cases here
    else {
      // not supported
      throw new NotImplementedException(NotImplementedException.Code.DATA_TYPE_NOT_SUPPORTED, 
          "Không hỗ trợ kiểu dữ liệu {0}", type);
    }

    // use JTextField as the display component
    return super.createDisplayComponent(tfh);
  }

  @Override
  public boolean isSupportValueFormatting() {
    return true;
  }

  @Override
  public String getFormattedValue(Object val) {
    if (format != null && !val.equals(Nil)) {
      String formatted = format.format(val);
      return formatted;
    } else {
      return (val != null) ? val.toString() : null;
    }
  }
  

  @Override
  protected Object parseFormattedValue(String val) throws ParseException {
    // convert formatted value to actual value object and return
    Object v = format.parseObject(val); 
    return v;
  }
  
  /** The code below was used for JFormattedTextField but it does not yet work properly **************/
//  /**
//   * @effects 
//   *    initialise <tt>this.display</tt> as a <tt>JFormattedTextField</tt> 
//   *    
//   *  <br>Return <tt>this.display</tt>
//   */
//  @Override
//  protected JComponent createDisplayComponent(TextFieldHandler tfh) {
//    int length = -1;
//    if (dconstraint != null) {
//      length = dconstraint.length();
//    }
//    
//    if (length == -1)
//      length = 10;
//
//    length = Math.min(MAX_DISPLAYABLE_TEXT_WIDTH, length);
//    
//    JComponent actualDisplay = null;
//    
//    // format string is determined by the bound constraint (if specified) or the domain constraint
//    String formatString;
//    Type type;
//    Format format = null;
//    JFormattedTextField tf;
//    
//    if (boundConstraint != null) {
//      formatString = boundConstraint.format();
//      type = boundConstraint.type();
//    } else {
//      formatString = dconstraint.format();
//      type = dconstraint.type();
//    }
//    
//    if (formatString == DomainConstraint.Null) {
//      // no format string
//      formatString = null;
//    }
//    
//    // use the data type to determine the value format
//    Locale currentLocale = Locale.getDefault();
//    if (type.isDate()) {
//      if (formatString != null)
//        format = new SimpleDateFormat(formatString, currentLocale);
//      else
//        format = new SimpleDateFormat();
//    } else if (type.isInteger()) {
//      // cannot use this because the returned value is of type Long (not Integer as expected)
//      // format = NumberFormat.getIntegerInstance();
//      // just let the text field determines a suitable format for the input value 
//    } else if (type.isNumeric()) {
//      // other numeric type use number format
//      //TODO: support more specific number format
//      format = NumberFormat.getNumberInstance();
//    }
//    // add other cases here
//    else {
//      // default: let the field determine suitable format based on the value passed to it
//    }
//    
//    if (format != null) {
//      tf = new JFormattedTextField(format);
//    } else {
//      // let the field determine suitable format based on the value passed to it
//      tf = new JFormattedTextField();
//    }
//
//    tf.setColumns(length);
//    tf.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
//
//    // set initial value (if any)
//    if (value != null)
//      tf.setValue(value);
//    
//    display = tf;
//    actualDisplay = display;
//
//    setUpTextField();
//    setUpListener(tfh);
//
//    return actualDisplay;
//  }

//  @Override
//  protected void setUpListener(TextFieldHandler tfh) {
//    //super.setUpListener(tfh);
//
//    // need to set up property change listener to get the value
//    display.addPropertyChangeListener("value", tfh);
//  }

  // TODO: does not yet work!
//  @Override
//  public Object getValue() throws ConstraintViolationException {
//    JFormattedTextField ff = (JFormattedTextField) display;
//    
//    // value has not be obtained or validation did not succeed before (need to retry)
//    if (value == null && !validated) {
//      try {
//        ff.commitEdit();
//      } catch (ParseException e) {
//        // ignore
//      }
//      
//      Object v = ff.getValue();
//
//      // empty strings are treated as null
//      if (v!= null && v.equals(Nil))
//        v = null;
//
//      value = validateValue(v, dconstraint);
//    } 
//
//    return value;
//  }
//  
//  @Override
//  protected void setDisplayValue(Object dispVal) {
//    if (display != null) {
//      JFormattedTextField ffield = (JFormattedTextField) display;
//      try {
//        if (dispVal == Nil)
//          ffield.setValue(null);
//        else
//          ffield.setValue(dispVal);  
//      } catch (IllegalArgumentException e) {
//        // ignore
//      }
//      
//      /** need to reset the indicator here */
//      updateGUI(false);
//    }
//  }
}
