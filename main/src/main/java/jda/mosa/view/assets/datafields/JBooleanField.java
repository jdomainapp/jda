package jda.mosa.view.assets.datafields;

import java.awt.Font;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.datafields.list.JComboField;

/**
 * @overview
 *  A sub-class of <tt>JDataField</tt> used for displaying <tt>Boolean</tt>-type data.
 *  
 * @author dmle
 */
public class JBooleanField<C> extends JComboField //v3.1: JSpinnerField 
{

  /**
   * language-specific boolean value mapping that is used for all {@link JBooleanDataField} objects:
   * maps <tt>Boolean</tt> value to a language-specific <tt>String</tt> 
   * name of that value  
   */
  private static Map<Boolean,String> boolValMap;
  
  /**
   * boolean data source based on {@link #boolValMap} that is used for all {@link JBooleanDataField} objects  
   */
  private static JDataSource booleanDataSourceInstance;
  
  private static final char TrueSymbol = '\u221a';
  private static final char FalseSymbol = '\u00d7';
  private static final Font MyDefaultFont = new Font(
      //"Arial"
      "Tahoma"
      , Font.PLAIN, 14);
  
  public JBooleanField(DataValidator validator, Configuration config, C val,
      JDataSource dataSource,       // ignore
      DAttr dconstraint,   
      DAttr bconstraint, // ignore
      Boolean editable) throws ConstraintViolationException {
    super(validator, config, val, 
        getBooleanDataSourceInstance(config), // use a special data source 
        dconstraint, 
        null,    // no bound constraint  
        editable);
    
    // only supports Boolean-type
    if (!dconstraint.type().isBoolean()) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_DATA_TYPE, 
          "Kiểu dữ liệu không đúng {0} (cần kiểu {1})", dconstraint.type(), "Boolean");
    }
    
    Object value = getValueDirectly(); // v5.1c
    
    if (value != null && !(value instanceof Boolean))
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_VALUE, new Object[] {value});
    
//    if (value == null) {          
//      value = Boolean.FALSE;
//    }
  }

  @Override
  public void setStyleFont(JComponent comp, Font font) {
    // if font does not support the gender symbols, use the default font
    if (font.canDisplay(TrueSymbol) && font.canDisplay(FalseSymbol)) {
      // font support: use it 
      comp.setFont(font);
    } else {
      // font does not support: use default
      Font myFont = MyDefaultFont;
      comp.setFont(myFont);
    }
  }

  /**
   * @effects 
   *  if <tt>this.value</tt> is not null
   *    look up the <tt>Boolean</tt> value of <tt>this.value</tt> and return it
   *  else
   *    return null  
   */
  @Override
  public Object getValue() {
    Object val = super.getSingleValue(); //v3.2: super.getValue();
    
    if (val != null) {
      return reverseLookUp((String)val);
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if <tt>val</tt> is not null
   *    look up the language-specific display value for <tt>val</tt> and 
   *    sets <tt>this.value</tt> to it
   *  else
   *    sets <tt>this.value = null</tt>
   */
  @Override
  public void setValue(Object val) {
    Object value;
    if (val != null) {
      if (val instanceof Boolean) {
        value = lookUp((Boolean)val);
      } else {
        value = val;
      }
    } else {
      value = null;
    }
    
    // v3.2: super.setValue(value);
    super.setSingleValue(value);
  }

  /**
   * @effects 
   *  return the <tt>Boolean</tt> value of <tt>val</tt>, as specified in the language-specific boolean 
   *  mapping of this, or return <tt>null</tt> if no such mapping exists.
   */
  private Boolean reverseLookUp(String val) {
    for (Entry<Boolean,String> e : boolValMap.entrySet()) {
      if (e.getValue().equals(val)) {
        return e.getKey();
      }
    }
    
    return null;
  }
  
  /**
   * @effects 
   *  return the language-specific display value of the <tt>Boolean</tt> value <tt>val</tt>, as specified in the language-specific boolean 
   *  mapping of this; or return <tt>null</tt> if no such mapping exists.
   */
  private String lookUp(Boolean val) {
    return boolValMap.get(val);
  }

  @Override
  protected Object parseDisplayValue(Object value, DAttr d)
      throws IllegalArgumentException {
    // JBoolean field displays boolean values as text so there is no need to 
    // parse them
    return value;
  }
  
  /**
   * @effects 
   *  convert <tt>val</tt> to the display value suitable for the language locale of this (if needed) and return it
   *  (if <tt>val</tt> is <tt>Boolean</tt> then return the language-specific names for <tt>val</tt>)  
   */
  @Override
  protected Object getDisplayValue(Object val) {
    Object value;
    if (val != null) {
      if (val instanceof Boolean) {
        value = lookUp((Boolean)val);
      } else {
        value = val;
      }
    } else {
      value = null;
    }    
    
    return value;
  }
  
  /**
   * @effects 
   *  see {@link #getFormattedValue(Object)}
   */
  @Override
  public boolean isSupportValueFormatting() {
    return true;
  }

  /**
   * @requires 
   *  val != null /\ val instanceof Boolean
   *  
   * @effects 
   *  return the boolean symbols used for <tt>val</tt>
   *  
   * @version 3.2
   */
  @Override
  public String getFormattedValue(Object val) throws NotPossibleException {
    if (val == null || !(val instanceof Boolean)) {
      throw new NotPossibleException(NotPossibleException.Code.CANNOT_FORMAT_VALUE, new Object[] {this, val, null});
    }
    
    return lookUp((Boolean) val);
  }

  /**
   * @effects 
   *  return a pseudo data source that provides two boolean values for display on this field
   */
  private static JDataSource getBooleanDataSourceInstance(Configuration config) {
    if (booleanDataSourceInstance == null) {
      /*v3.1: use universal unicode symbols to avoid the need to support language
      // read the language-specific boolean mappings from the resource file
      // e.g. if language = en then resource file is domainapp/view/datafields/JBooleanField_en.properties
      
      Locale locale;
      if (config != null) {
        locale = config.getLanguageLocale(DEFAULT_LOCALE);
      } else {
        locale = DEFAULT_LOCALE;
      }
      
      ResourceBundle valuesRes = ResourceBundle.getBundle(
          JBooleanField.class.getName(), locale);

      
      // populate boolean-value mappings with data from the resource file
      boolValMap = new LinkedHashMap<>();
      
      // use values resource to create list
      Iterator kit = valuesRes.keySet().iterator();
      Boolean tf;
      String key, valStr;
      while (kit.hasNext()) {
        key = (String)kit.next();
        tf = Boolean.parseBoolean(key);
        valStr = valuesRes.getString(key);
        boolValMap.put(tf, valStr);
      }
      */
      // populate boolean-value mappings with data 
      boolValMap = new LinkedHashMap<>();
      boolValMap.put(Boolean.TRUE, TrueSymbol+"");
      boolValMap.put(Boolean.FALSE, FalseSymbol+"");
      
      // initialise a data source using the boolean value mappings
      
      booleanDataSourceInstance = new JDataSource() {

        @Override
        public Iterator iterator() {
          return boolValMap.values().iterator();
        }

        @Override
        public boolean isEmpty() {
          return false;
        }
      };
    }
    
    return booleanDataSourceInstance;
  }
  
  @Override
  protected int getDomainFieldWidth() {
    int length = DEFAULT_FIELD_WIDTH;
    
    if (//dconstraint != null && dconstraint.length() > 0
        isConstrained()
        ) {
      DAttr dconstraint = getDomainConstraint();
      if (dconstraint.length() > 0)
        length = dconstraint.length();
    }
    
    return length;
  }
}
