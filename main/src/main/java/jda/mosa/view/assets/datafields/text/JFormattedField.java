package jda.mosa.view.assets.datafields.text;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.datafields.JSimpleFormattedField;
import jda.mosa.view.assets.datafields.JTextField;

/**
 * @overview
 *  A sub-type of {@link JTextField} that supports additional formatting (e.g. mask formatter for string-typed values)
 *  compared to {@link JSimpleFormattedField}.
 *  
 * @author dmle
 */
public class JFormattedField<C> extends JTextField {
  
  /** the display format used to display the value */
  private Format format;  // for other types
  
  /** the display formatter used for string-typed values*/
  private MaskFormatter formatter;  // for string-typed
  
  // the format string
  private String formatString;

  private Runnable runSelectLaterObj;
  
  // the swing formatted text field object used 
  private javax.swing.JFormattedTextField tf;

  // bounded
  public JFormattedField(DataValidator validator, 
      Configuration config,
      C val,
      JDataSource dataSource, // the data source to which this field is bound 
      DAttr domainConstraint, DAttr boundConstraint, Boolean editable, Boolean autoValidation) {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);
  }
  
  // unbounded
  public JFormattedField(DataValidator validator, Configuration config,
      C val, DAttr domainConstraint, Boolean editable,
      Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, domainConstraint, editable, 
        autoValidation
        );
  }

  /**
   * @effects 
   *    initialise <tt>this.display</tt> as a formatted text field 
   *    
   *  <br>Return <tt>this.display</tt>
   */
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    
    //////////////////// INIT format/formatter
    /** initialise the format based on the data type and 
     *  the format string (if any) and create the display component */
    
    // format string is determined by the bound constraint (if specified) or the domain constraint
    DAttr.Format formatSpec;
    //String formatString;
    Type type;
    String attribName;
    
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    
    if (boundConstraint != null) {
      formatSpec = boundConstraint.format();
      type = boundConstraint.type();
      attribName = boundConstraint.name();
    } else {
      formatSpec = dconstraint.format();
      type = dconstraint.type();
      attribName = dconstraint.name();
    }
    
    if (formatSpec.isNull()) {
      formatString = null;
    } else {
      formatString = formatSpec.getFormatString();
    }
    
//    if (formatString == MetaConstants.NullString) {
//      // no format string
//      formatString = null;
//    }
    
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
        NumberFormat nformat = NumberFormat.getNumberInstance();
        format = nformat;
        // determine the integer and fraction digits (if any)
        int numFractions = 0, numIntegers = 1;
        if (formatString != null) {
          String[] digits = formatString.split("\\.");
          String intDigitStr = digits[0];
          numIntegers = intDigitStr.length() > 0 ? intDigitStr.length() : 1;

          if (digits.length>1) { 
            String fracDigitStr = digits[1];
            numFractions = fracDigitStr.length();
          }
        } 
        
        nformat.setMinimumIntegerDigits(numIntegers);
        nformat.setMaximumFractionDigits(numFractions);
        nformat.setMinimumFractionDigits(numFractions);
//      }
    } else if (type.isString()) {
      if (formatString == null)
        throw new NotPossibleException(NotPossibleException.Code.NO_FORMAT_STRING, 
            new Object[] {attribName});
      
      try {
        /*v3.2c: use variable length mask formatter 
        formatter = new MaskFormatter(formatString);
        */
        formatter = new VariableLengthMaskFormatter(formatString);
        // remove place holder chars in the returned value
        formatter.setValueContainsLiteralCharacters(false); 
        // end v3.2c
        formatter.setPlaceholderCharacter('_');
        formatter.setAllowsInvalid(false);
        // NOTE: this does not work!
        //formatter.setOverwriteMode(false); // add new char without overriding
      } catch (java.text.ParseException exc) {
        throw new NotPossibleException(NotPossibleException.Code.INVALID_FORMAT_STRING, 
            new Object[] {attribName, formatString});
      }
      
      // v3.0: fixed to avoid JFormattedTextField displays this "javax.swing.text.MaskFormatter@" when first display on form
      // -> use empty string if no initial value is set
//      if (value==null)
//        setValueDirectly(Nil);
    }
    // add other cases here
    else {
      // not supported
      throw new NotImplementedException(NotImplementedException.Code.DATA_TYPE_NOT_SUPPORTED, 
          "Không hỗ trợ kiểu dữ liệu {0}", type);
    }

    ////////////////////// CREATE the display component as Swing formatted text field
    //return super.createDisplayComponent(tfh);
    
    // default length if no length is specified
    int length = getDomainFieldWidth();
    
    if (format != null) { // use format
      tf = new javax.swing.JFormattedTextField(format);
    } else {  // use formatter
      tf = new javax.swing.JFormattedTextField(formatter);
    }
    
    tf.setColumns(length);
    tf.setFocusLostBehavior(javax.swing.JFormattedTextField.COMMIT_OR_REVERT);
    
    int align = getAlignX();
    tf.setHorizontalAlignment(align);
    
    // v5.1c: display = tf;
    setGUIComponent(tf);
    Object value = getValueDirectly(); // v5.1c
    if (value != null)
      displayFormattedValue();

    //    // on first display on form
//    if (value==null)
//      setValueDirectly(Nil);
//    
//    displayFormattedValue();

    
    setUpTextField();
    setUpListener(tfh);
    
    return tf; // v5.1c: display;
  }

  @Override
  protected void handleFocusGained() {
    // select later
    SwingUtilities.invokeLater(runSelectLater());
  }
  
  @Override
  protected void handleFocusLost() {
    // force commit
    boolean commited;
    try {
      tf.commitEdit();
      commited = true;
    } catch (ParseException e1) {
      //ignore: e1.printStackTrace();
      commited = false;
    }
    
    // validate text field if needed
    if (commited && //v5.1c: autoValidation
        isAutoValidation()
        ) {
      try {
        Object val = getRawValue();
        boolean changed = validate(val);
        if (changed) {
          // value was changed
          fireValueChanged();

          /* no need to do this for this field
          // display the value (with format if any)
          displayFormattedValue();
          */
        }
      } catch (Exception e) {
        // error: display it
        setIsValidated(false);
      }
    }
  }

  /**
   * @effects 
   *  initialise (if not already) and return a Runnable object used to select the text in the formatted text field
   *  of this
   */
  private Runnable runSelectLater() {
    if (runSelectLaterObj == null) {
      //final javax.swing.JFormattedTextField tf = (javax.swing.JFormattedTextField) display;

      runSelectLaterObj = new Runnable() {
        public void run() {
          tf.selectAll();
        }
      };
    }
    
    return runSelectLaterObj;
  }

  @Override
  public String getFormatString() {
    return formatString;
  }
  
  @Override
  protected void displayFormattedValue(Object val) {
//    String formatted = getFormattedValue(val);
//    ((javax.swing.JFormattedTextField) display).setValue(formatted);
    if (val != null && val.equals(Nil))
      val = null;
    
    tf.setValue(val);
  }

  @Override
  protected Object getRawTextValue() 
      throws ConstraintViolationException // v3.0 
  {
    Object v = tf.getValue();
    
    // v3.2c: empty string is treated as null (this is ONLY needed b/c of the use of 
    // variable length mask formatter)
    if (v != null && v.equals(Nil))
      v = null;
    
    return v;
  }
  
  @Override
  public boolean isSupportValueFormatting() {
    return true;
  }

  @Override
  public String getFormattedValue(Object val) throws NotPossibleException {
    String formatted = null;
    if (format != null && !val.equals(Nil)) {
      formatted = format.format(val);
    } else if (formatter != null && !val.equals(Nil)) { 
      try {
        formatted = formatter.valueToString(val);
      } catch (ParseException e) {
        throw new NotPossibleException(NotPossibleException.Code.CANNOT_FORMAT_VALUE, e,  
            new Object[] {this, val, formatter.getMask()}); 
      }
    } else {
      // value equals Nil
      formatted = (val != null) ? val.toString() : null;
    }
    
    return formatted;
  }
  

  @Override
  protected Object parseFormattedValue(String val) throws ParseException {
    // convert formatted value to actual value object and return
    Object v;
    
    if (format != null)
      v = format.parseObject(val);
    else 
      v = formatter.stringToValue(val);
      
    return v;
  }
}
