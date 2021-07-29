package jda.util.properties;

import java.awt.Color;
import java.awt.Font;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.types.Tuple;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.view.assets.GUIToolkit;

@DClass(schema="app_config")
public class Property {
  
  private static final Class<String[]> StringArrCls = String[].class;
  
  public static final String Association_WithPropertySet = "propSet-has-prop";
  
  @DAttr(name="id",id=true,auto=true,type=DAttr.Type.Integer,mutable=false,optional=false)
  private int id;
  private static int idCounter;
  
  @DAttr(name="key",type=DAttr.Type.String,mutable=false,optional=false,length=150)
  private String pkey;
  
  // not serialisable (see valueAsString)
  private Object value;

  // derived from value
  @DAttr(name="valueAsString",type=DAttr.Type.String,optional=false,length=255)
  private String valueAsString;

  @DAttr(name="typeName",type=DAttr.Type.String,optional=false)
  private String typeName;

  private Class type;

  @DAttr(name="container",type=DAttr.Type.Domain,optional=false)  
  @DAssoc(ascName=Association_WithPropertySet,role="prop",
      ascType=AssocType.One2Many,endType=AssocEndType.Many,
      associate=@Associate(type=PropertySet.class,cardMin=1,cardMax=1))
  private PropertySet container;
  
  // Java's standard wrapper types for the primitive types
  private static final Map<Class,Class> WrapperTypes;
  static {
    WrapperTypes = new HashMap<>();
    WrapperTypes.put(boolean.class, Boolean.class);
    WrapperTypes.put(byte.class, Byte.class);
    WrapperTypes.put(short.class, Short.class);
    WrapperTypes.put(char.class, Character.class);
    WrapperTypes.put(int.class, Integer.class);
    WrapperTypes.put(long.class, Long.class);
    WrapperTypes.put(float.class, Float.class);
    WrapperTypes.put(double.class, Double.class);
    WrapperTypes.put(void.class, Void.class);
  }
  
  /**
   *  constructor to create Property from data source
   */
  public Property(Integer id, String key, String valueAsString, String typeName,
      //PropertySet refSet, 
      PropertySet container) throws ConstraintViolationException, NotFoundException {
    if (id == null) {
      idCounter++;
      this.id=idCounter;
    } else {
      this.id = id;
    }
    this.pkey = key;
    this.valueAsString = valueAsString;
    this.typeName = typeName;
    try {
      this.type=Class.forName(typeName);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, 
          "Không tìm thấy lớp");
    }
    
    if (valueAsString != null) {
      // parse into value
      value = parseValue(valueAsString, type);
    }
    
    //this.refSet = refSet;
    this.container = container;
  }

  /**
   * Constructor to create a raw property
   * 
   *  @requires 
   *    value.getClass = type
   */
  public Property(String key, Object value, Class type, PropertySet container) {
    idCounter++;
    this.id=idCounter;
    
    this.pkey = key;
    if (type.isPrimitive()) {
      // use the wrapper type
      type=WrapperTypes.get(type);
    } 
    /*v2.8
    else {
      this.type = type;
    }
    */
    this.type = type;
    
    setValue(value, type);
    this.typeName = type.getName();
    
    //this.refSet = null;
    this.container = container;
  }

  /**
   * @effects 
   *  return <tt>{@link #Property}(propName.name(), valueAsString, type, container)</tt>
   * @version 2.7.4
   */
  private Property(PropertyName propName, String valueAsString, Class type, PropertySet container) 
  throws ConstraintViolationException {
    idCounter++;
    this.id=idCounter;
    
    String key = propName.name();
    
    this.pkey = key;
    if (type.isPrimitive()) {
      // use the wrapper type
      type=WrapperTypes.get(type);
    } 
    /*v2.8
    else {
      this.type = type;
    }
     */
    this.type = type;
    
    this.valueAsString = valueAsString;
    
    if (valueAsString == null) {
      this.value = null;
    } else {
      this.value = parseValue(valueAsString, type);
    }
    
    this.typeName = type.getName();
    
    this.container = container;
  }

  /**
   * @version 
   *  2.7.4: created
   *  <br>
   *  3.0: updated to support valueIsClass
   */
  public static Property createInstance(PropertyDesc pd, PropertySet container) 
      throws ConstraintViolationException {
    /*v3.0: 
    Property prop = new Property(pd.name(), pd.valueAsString(), pd.valueType(), container);
    */
    Class valueIsClass = pd.valueIsClass();
    
    Property prop;
    String valueAsString;
    
    if (valueIsClass != CommonConstants.NullType) {
      // use valueIsClass = FQN of the class
      valueAsString = pd.valueIsClass().getName(); 
    } else {
      // use valueAsString
      valueAsString = pd.valueAsString();
    }
    
    prop = new Property(pd.name(), valueAsString, pd.valueType(), container);
    
    return prop;
  }


  /**
   * @effects 
   *  return <tt>{@link #Property}(propName, value, value.class, container)</tt>
   * @version 2.8
   */
  public static Property createInstance(String propName, Object value,
      PropertySet container) {
    Property prop = new Property(propName, value, value.getClass(), container);
    
    return prop;
  }
  
  private void setValue(Object value, Class type) {
    this.valueAsString = toValueString(value, type);
    
    if (valueAsString == null) {
      this.value = null;
    } else {
      this.value = value;
    }
  }
  
  /**
   * @requires 
   *  value != null
   *  
   * @effects 
   *  sets this.type = value.class
   *  <br>set this.valueAsString = {@link #toValueString(Object, Class)} for <tt>(value, value.class)</tt>
   *  <br>return old value of this
   * @version 2.8
   */
  public Object setValue(Object value) {
    if (value == null)
      return null;
    
    Object oldVal = this.value;
    
    Class type = value.getClass();
    if (type.isPrimitive()) {
      // use the wrapper type
      type=WrapperTypes.get(type);
    }
    
    setValue(value, type);
    
    this.type = type;
    
    return oldVal;
  }
  
//  // constructor to create a set-based property
//  public Property(String key, PropertySet refSet, PropertySet container) {
//    this(null, key, null, null, 
//        refSet, 
//        container);
//  }

  public int getId() {
    return id;
  }

  public String getPkey() {
    return pkey;
  }

  public Object getValue() {
    return value;
  }

  /**
   * This method is used to retrieve correct typed value of this property.
   *  
   * @effects 
   *  if expectedType is not null AND expectedType is either the same as or a super-type of this.type
   *    return this.value as expectedType
   *  else
   *    return null
   */
  public <T> T getValuez(Class<T> expectedType) {
    if (expectedType != null && expectedType.isAssignableFrom(this.type)) {
      return (T) value;
    } else {
      return null;
    }
  }
  
  public String getValueAsString() {
    return valueAsString;
  }

//  public PropertySet getRefSet() {
//    return refSet;
//  }

//  public Type getType() {
//    return type;
//  }

  public PropertySet getContainer() {
    return container;
  }


  public String getTypeName() {
    return typeName;
  }

  public Class getType() {
    return type;
  }
  
  /**
   * @requires 
   *  minVal != null /\ maxVal != null
   * @effects 
   *  update the auto-generated value of attribute <tt>attrib</tt>, specified for <tt>derivingValue</tt>, using <tt>minVal, maxVal</tt>
   */
  @DOpt(type=DOpt.Type.AutoAttributeValueSynchroniser)
  public static void updateAutoGeneratedValue(
      DAttr attrib,
      Tuple derivingValue, 
      Object minVal, 
      Object maxVal) throws ConstraintViolationException {    
    if (minVal != null && maxVal != null) {
      // check the right attribute
      if (attrib.name().equals("id")) {
        int maxIdVal = (Integer) maxVal;
        if (maxIdVal > idCounter)  
          idCounter = maxIdVal;
      } 
      // TODO add support for other attributes here 
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Property other = (Property) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Property (" + id + ": " + pkey + "=" + valueAsString +")";
  }
  
  private String toValueString(Object val, Class type) {
    String valStr = null;
    
    if (val != null) {
      if (type == Color.class) {
        valStr = GUIToolkit.toColorString((Color) val);
      } else if (type == Font.class) {
        valStr = GUIToolkit.toFontString((Font) val);
      } else if (type == Class.class) {
        valStr = ((Class) val).getName();
      } else if (
          type == StringArrCls
          ) {  // string array
        String[] strArr = (String[]) val;
        if (strArr.length > 0) {  // non-empty array
          StringBuffer sb = new StringBuffer();
          for (int i = 0; i < strArr.length; i++) {
            sb.append(strArr[i]);
            if (i < strArr.length-1)
              sb.append(",");
          }
          valStr = sb.toString();
        }
      }
      // add other types here
      else {
        valStr = val.toString();
      }
    }
    
    return valStr;
    
  }
  
  /**
   * @requires valueStr != null /\ type != null /\ type is a proper class type
   *  <p><b>NOTE:</b> <tt>type</tt> must NOT be primitve type (e.g. <tt>int</tt>, etc.);
   *  these must be replaced by the corresponding Java's wrapper types (e.g. <tt>Integer</tt>, etc.) 
   */
  public static Object parseValue(String valueStr, Class type) throws ConstraintViolationException {
    Object val = null;
    
    /* v3.1: BUG: removed this check 
    if (type.isPrimitive()) {
    */
      // parse into number if necessary
        if (type == Integer.class) {
          try {
            val = Integer.parseInt(valueStr);
          } catch (NumberFormatException e) {
            throw new ConstraintViolationException(
                ConstraintViolationException.Code.INVALID_VALUE, e, 
                "Dữ liệu nhập không đúng: {0}", valueStr);
          }
        } else if (type == Long.class) {
          try {
            val = Long.parseLong(valueStr);
          } catch (NumberFormatException e) {
            throw new ConstraintViolationException(
                ConstraintViolationException.Code.INVALID_VALUE, e, 
                "Dữ liệu nhập không đúng: {0}", valueStr);
          }
        } else if (type == Float.class) {
          val = Float.parseFloat(valueStr);
          try {
          } catch (NumberFormatException e) {
            throw new ConstraintViolationException(
                ConstraintViolationException.Code.INVALID_VALUE, e, 
                "Dữ liệu nhập không đúng: {0}", valueStr);
          }
        } else if (type == Double.class) {
          try {
            val = Double.parseDouble(valueStr);
          } catch (NumberFormatException e) {
            throw new ConstraintViolationException(
                ConstraintViolationException.Code.INVALID_VALUE, e, 
                "Dữ liệu nhập không đúng: {0}", valueStr);
          }
        }
        else if (type == BigInteger.class) {
          try {
            val = new BigInteger(valueStr);
          } catch (NumberFormatException e) {
            throw new ConstraintViolationException(
                ConstraintViolationException.Code.INVALID_VALUE, e, 
                "Dữ liệu nhập không đúng: {0}", valueStr);
          }
        }
    //} // end numeric check
    else if (type == Boolean.class) {
      try {
        val = Boolean.parseBoolean(valueStr.toString());
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Dữ liệu nhập không đúng: {0}", valueStr);
      }
    } // end boolean check
    else if (type == Color.class) {
      try {
        val = GUIToolkit.getColorValue(valueStr);
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Dữ liệu nhập không đúng: {0}", valueStr);
      }
    } // end color check
    else if (type == Font.class) {
      try {
        val = GUIToolkit.getFontValue(valueStr);
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Dữ liệu nhập không đúng: {0}", valueStr);
      }
    } // end font check
    else if (type == Class.class) {
      try {
        val = Class.forName(valueStr);
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Dữ liệu nhập không đúng: {0}", valueStr);        
      }
    }
    else if (type == String[].class) {
      val = valueStr.split(",");
    } else if (type == LAName[].class) {  // v3.2c
      String[] elements = valueStr.split(",");
      LAName[] names = new LAName[elements.length];
      for (int i = 0; i < elements.length; i++) {
        names[i] = LAName.valueOf(elements[i]);
      }
      val = names;
    } else if (Enum.class.isAssignableFrom(type)) {
      // enum: look up the right enum
      val = Enum.valueOf(type, valueStr);
    } 
    // add other checks here
    else {
      val = valueStr;
    }
    
    return val;
  }

}
