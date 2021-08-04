/**
 * @overview
 *
 * @author dmle
 */
package jda.modules.dodm;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Format;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.osm.OSMFactory;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmClientServerConfig;
import jda.mosa.view.assets.GUIToolkit;
import jda.util.SwTk;

/**
 * @overview
 *  Encapsulate shared utility functionality concerning {@link DODMBasic} and its components.
 *  
 * @author dmle
 * 
 * @version 3.2
 */
public class DODMToolkit {
  private DODMToolkit() {}

  /**
   * @effects 
   *  create and return a {@link DODM} that only stores objects in memory. 
   */
  public static DODM createMemoryBasedDODM(String appName,
      String dataSourceName) {
    Configuration config = SwTk.createMemoryBasedConfiguration(appName);
    
    DODM dodm = DODM.getInstance(DODM.class, config);
    return dodm;
  }
  
  /**
   * @effects
   *  create and return a new {@link DODM} that uses <b>embedded JavaDb</b> database
   */
  public static DODM createJavaDbEmbeddedDODM(String appName, String dataSourceName) {
    Configuration config = SwTk.createSimpleConfigurationInstance(appName, dataSourceName);
    
    DODM dodm = DODM.getInstance(DODM.class, config);
    return dodm;
  }
  
  /**
   * @effects
   *  create a new {@link DODM} that uses <b>client/server</b> database 
   */
  public static DODM createJavaDbClientServerDODM(String appName, 
      String dataSourceName) {
    //create a new Configuration that uses <b>client/server JavaDb</b> database running at <tt>localhost:1527</tt>
    
    String clientUrl = "//localhost/"+dataSourceName;
    String serverUrl = "//localhost";
    
    OsmClientServerConfig osmConfig = OSMFactory.getStandardOsmClientServerConfig("derby", clientUrl, serverUrl);

    Configuration config = SwTk.createInitApplicationConfiguration(appName, osmConfig);
    
    DODM dodm = DODM.getInstance(DODM.class, config);
    return dodm;
  }
  
  /**
   * The reverse of this method is {@link DODMToolkit#parseDomainValue(Type, Format, Object)}
   * 
   * @requires 
   *  domainVal != null /\ domainVal.class matches format (if specified)
   * @effects 
   *  if <tt>format</tt> is specified AND <tt>domainVal</tt> is convertible to <tt>format</tt>
   *    return a value of <tt>domainVal</tt> after being converted to match <tt>format</tt>
   *  else
   *    return <tt>domainVal</tt> (unchanged)
   *  @version 2.7.4
   */
  public static Object formatDomainValue(DSMBasic dsm, Type type, DAttr.Format format,
      Object domainVal) {
    if (domainVal == null)
      return null;
    
    Object formatted = domainVal;
    if (format != DAttr.Format.Nil) {
      if (type.isDate() && domainVal instanceof Date) {
        formatted = DODMToolkit.dateToString((Date)domainVal, format);
      } 
      //TODO: support other formats here
    } 
    
    return formatted;
  }

  /**
   * This method is the reverse of {@link formatDomainValue}.
   * It is similar to the method <tt>validateDomainValue</tt> of the DODM.
   * 
   * @requires 
   *  value != null /\
   *  attrib.type = Type.Domain -> valueType != null /\ valueType must be compatible to <tt>value</tt>
   *  
   * @effects 
   *  convert <tt>value</tt> into a valid domain value as it would be used to assign to the domain attribute <tt>attrib</tt>  
   *    return result 
   *  else
   *    return <tt>value</tt> (unchanged)
   *    
   *  <p>throws ConstraintViolationException if failed to parse; IllegalArgumentException
   *  if valueType is not one of the supported types.
   *  
   *  @version 
   *  - 3.1: if <tt>valueType</tt> is specified then it must be an <tt>enum</tt>
   */
  public static Object parseDomainValue(
      //Type type, DomainConstraint.Format format,
      DAttr attrib, 
      Class valueType, 
      Object value) throws ConstraintViolationException {
    /* v3.1: support other types
    if (value == null)
      return null;
      
    Object domainVal = val;
    if (format != DomainConstraint.Format.Nil) {
      if (type.isDate()) {
        if (val instanceof String)
          domainVal = dateFromString((String)val, format);
      } 
      //TODO: support other formats here
    } 
    
    return domainVal;
  
    */
    
    if (value == null)
      return null;
    
    Object val = value;
  
    String attribName = attrib.name();
    
    // validate length constraint
    Type type = attrib.type();
    Format format = attrib.format();
    
    // if (type.equals(Type.String)) {
    if (type.isString()) {
      String valStr = value.toString();
      
      if (attrib.length() > 0) {
        if (valStr.length() > attrib.length())
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_ATTRIBUTE_LENGTH, 
              //"Dữ liệu nhập không đúng: {0}", valStr
              new Object[] {attribName, valStr, valStr.length(), attrib.length()});
      }
      
      // v2.7.3: support char type
      if (type.isChar()) {
        val = valStr.charAt(0); 
      } else {
        val = valStr;
      }
    } // end string check
    else if (type.isNumeric()) {
      // parse into number if necessary
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
          // v2.7.2
          else if (type.equals(Type.BigInteger)) {
            val = new BigInteger(val.toString());
          }
        } catch (NumberFormatException e) {
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_NUMERIC_VALUE, e, 
              //"Dữ liệu nhập không đúng: {0}", value
              new Object[] {attribName, value}
              );
        }
      } else {
        // already a number, see if it matches the specified type. If not then
        // convert
        if (type.equals(Type.Integer) && !(value instanceof Integer)) {
          val = ((Number) value).intValue();
        } else if (type.equals(Type.Long) && !(value instanceof Long)) {
          val = ((Number) value).longValue();
        } else if (type.equals(Type.Float) && !(value instanceof Float)) {
          val = ((Number) value).floatValue();
        } else if (type.equals(Type.Double) && !(value instanceof Double)) {
          val = ((Number) value).doubleValue();
        } // v2.7.2
        else if (type.equals(Type.BigInteger) && !(value instanceof BigInteger)) {
          val = new BigInteger(value.toString());
        }
      }
  
      // validate min and max constraints
      if (attrib.min() != Double.NEGATIVE_INFINITY) {
        if (((Number) val).doubleValue() < attrib.min())
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_VALUE_LOWER_THAN_MIN,
              //"Dữ liệu nhập không đúng: {0}", val
              new Object[] {attribName, val, attrib.min()}
              );
      }
  
      if (attrib.max() != Double.POSITIVE_INFINITY) {
        if (((Number) val).doubleValue() > attrib.max())
          throw new ConstraintViolationException(
              ConstraintViolationException.Code.INVALID_VALUE_HIGHER_THAN_MAX,
              //"Dữ liệu nhập không đúng: {0}", val
              new Object[] {attribName, val, attrib.max()}
              );
      }
    } // end numeric check
    else if (type.isBoolean() && !(value instanceof Boolean)) {
      try {
        val = Boolean.parseBoolean(value.toString());
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_BOOLEAN_VALUE, e, 
            //"Dữ liệu nhập không đúng: {0}", value
            new Object[] {attribName, value}
            );
      }
    } // end boolean check
    else if (type.isColor() && !(value.getClass().equals("java.awt.Color"))) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_COLOR_VALUE,
          //"Dữ liệu nhập không đúng: {0}", value
          new Object[] {attribName, value}
          );
    } // end color check
    else if (type.isFont() && !(value.getClass().equals("java.awt.Font"))) {
      throw new ConstraintViolationException(
          ConstraintViolationException.Code.INVALID_FONT_VALUE,
          //"Dữ liệu nhập không đúng: {0}", value
          new Object[] {attribName, value}
          );
    } // end font check
    else if (type.isDate() && !(value instanceof Date)) {
      // v2.7.3: support date type where value is NOT already a date
      // try to convert it to date, throws exeception if failed
      //DateFormat format = (DateFormat) dsm.getAttributeFormat(attrib);
      try {
        //val = (Date) format.parseObject(value.toString());
        if (format != DAttr.Format.Nil) {
          val = DODMToolkit.dateFromString(value.toString(), format);
        }
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_DATE_VALUE, e, 
            //value
            new Object[] {attribName, value}
            );
      }
    } // end date check
    else if (type.isDomainType()) {
      // v3.1: only support enum-typed
      if (valueType == null || !valueType.isEnum()) {
        throw new IllegalArgumentException(
            String.format("%s.parseDomainValue: invalid value type(%s) for attribute(%s) value(%s)", Toolkit.class.getSimpleName(), valueType, attrib.name(), value));
      }
      
      if (value == null) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE_NOT_SPECIFIED_WHEN_REQUIRED, 
            new Object[] {attribName, value}
            );
      }
      
      try {
        val = Enum.valueOf(valueType, value.toString());
      } catch (Exception e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_ENUM_VALUE, e, 
            new Object[] {attribName, value}
        );
      }
    }
    return val;
  }

  /**
   * A generic <tt>parseValue</tt> method, compared to Java's built-in methods of the wrapper types.
   * It supports the majority of the common Java's wrapper types, 
   * including {@link Number}s, {@link Boolean}, {@link Color}, {@link Font}, {@link String}[], and {@link Enum}.
   * 
   * @requires valueStr != null /\ type != null
   * @effects 
   *  parse <tt>valueStr</tt> into an object of the type expected by <tt>type</tt>. 
   *  If succeeded return the object, else throws ConstraintViolationException.
   * @version 2.8
   */
  public static <T> T parseValue(String valueStr, Class<T> type) throws ConstraintViolationException {
    Object val = null;
    
    if (type.isPrimitive()) {
      // parse into number if necessary
      try {
        if (type == Integer.class) {
          val = Integer.parseInt(valueStr);
        } else if (type == Long.class) {
          val = Long.parseLong(valueStr);
        } else if (type == Float.class) {
          val = Float.parseFloat(valueStr);
        } else if (type == Double.class) {
          val = Double.parseDouble(valueStr);
        }
        else if (type == BigInteger.class) {
          val = new BigInteger(valueStr);
        }
      } catch (NumberFormatException e) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE, e, 
            "Dữ liệu nhập không đúng: {0}", valueStr);
      }
    } // end numeric check
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
    } else if (Enum.class.isAssignableFrom(type)) {
      // enum: look up the right enum
      Class enumType = type;
      val = Enum.valueOf(enumType, valueStr);
    }
    // add other checks here
    else {
      val = valueStr;
    }
    
    return (T) val;
  }

  /**
   * @requires d != null /\ isNumeric(type)
   * @effects 
   *  if <tt>d</tt> is suitable for conversion into the data type specified by <tt>type</tt>
   *    return an <tt>Object</tt> of type <tt>type</tt> from <tt>d</tt> (preserving as much of d's original representation as possible)
   *  else
   *    throws IllegalArgumentException
   */
  public static Object fromDecimal(DAttr.Type type, BigDecimal d) throws IllegalArgumentException {
    if (type.isDecimal()) {
      // suitable for conversion
      if (type.isBigInteger()) {
        return d.toBigInteger();
      } else {
        // keeps d
        return d;
      }
    } else {
      throw new IllegalArgumentException("Toolkit.fromDecimal: not a Decimal type: " + type);
    }
  }

  /**
   * @requires 
   *  d != null
   * @effects 
   *  if d is an instance of Number
   *    return a BigDecimal representation of d (preserving as much of d's original representation as possible)
   *  else
   *    throws IllegalArgumentException
   */
  public static BigDecimal toDecimal(DAttr.Type type, Object d) throws IllegalArgumentException {
    if (type.isBigInteger()) {
      if (d instanceof BigInteger) {
        return new BigDecimal((BigInteger) d);
      } else {
        throw new IllegalArgumentException("Toolkit.toDecimal: not a BigInteger: " + d);
      }
    } else if (d instanceof Number) {
      return new BigDecimal(((Number)d).doubleValue());
    } else {
      throw new IllegalArgumentException("Toolkit.toDecimal: not a number: " + d);
    }
  }

  /**
   * The reverse of this is {@link DODMToolkit#dateFromString(String, Format)}.
   * 
   * @requires 
   *  date is not null 
   * @effects 
   *  return a user-friendly string of <tt>date</tt> based on <tt>dateFormat</tt> (if specified)
   */
  //TODO: cache date format to reduce memory 
  public static String dateToString(Date date, Format dateFormat) {
    DateFormat format; 
    Locale currentLocale = Locale.getDefault();
  
    if (dateFormat != null) {
      String formatString = dateFormat.getFormatString();
      
      
      if (formatString != null)
        format = new SimpleDateFormat(formatString, currentLocale);
      else
        format = new SimpleDateFormat();
    } else {
      format = new SimpleDateFormat();
    }
    
    String str = format.format(date);
    
    return str;
  }

  /**
   * This method is the reverse of {@link dateToString}.
   * 
   * @requires 
   *  dateStr is not null 
   * @effects 
   *  return a {@link Date} object based on <tt>dateFormat</tt> (if specified) that is 
   *  whose formated value is <tt>dateStr</tt>;
   *  
   *  <p>throws ParseException if failed to parse;
   * @version 3.1
   */
  //TODO: cache date format to reduce memory 
  public static Date dateFromString(String dateStr, Format dateFormat) throws ParseException {
    DateFormat format; 
    Locale currentLocale = Locale.getDefault();
  
    if (dateFormat != null) {
      String formatString = dateFormat.getFormatString();
      
      
      if (formatString != null)
        format = new SimpleDateFormat(formatString, currentLocale);
      else
        format = new SimpleDateFormat();
    } else {
      format = new SimpleDateFormat();
    }
    
    Date date = format.parse(dateStr);
    
    return date;
  }

  /**
   * @effects 
   *  return a user-friendly string of <tt>monthOfYear</tt> based on <tt>monthFormat</tt>
   * @example
   *  <pre>monthOfYear = Date("11/06/2015") /\ monthFormat = "MM/yyyy" 
   *  -> dateToMonthString(monthOfYear,monthFormat) = "06/2015"
   *  
   */
  public static Object dateToMonthString(Date monthOfYear, Format monthFormat) {
    DateFormat format; 
    Locale currentLocale = Locale.getDefault();
  
    String formatString; 
    if (monthFormat != null) {
      formatString = monthFormat.getFormatString();
    } else {
      formatString = "MM/yyyy"; // default
    }
      
    format = new SimpleDateFormat(formatString, currentLocale);
    
    String str = format.format(monthOfYear);
    
    return str;
  }

  /**
   * @requires <tt>domainCls != null</tt>
   * @effects 
   *  return a <tt>Class</tt> object of the view class of <tt>domainCls</tt>
   *  or <tt>null</tt> if no such class exists.
   *  
   *  <p>A view class (if exists) must have the same name as <tt>domainCls</tt>, 
   *     and must be located in the <tt>view.config</tt> sub-package 
   *     of the package <tt>view</tt> of the project. For example, 
   *     if the domain class is <tt>vn.com.vendmachine.model.Product</tt> then 
   *     the corresponding view class is expected to be <tt>vn.com.vendmachine.<b>view.config</b>.Product</tt> 
   */
  public static Class getViewClass(Class domainCls) throws NotFoundException {
    String clsName = domainCls.getSimpleName();
    String packageName = domainCls.getPackage().getName();
    
    //System.out.println("packageName: " + packageName);
    
    int firstDot = packageName.indexOf(".model");
    int lastDot = packageName.lastIndexOf(".");
    
    String viewClsPackage1 = 
        packageName.substring(0, firstDot);
    String viewClsPackage2 = null;
    // the part of package name between the two dots 
    if (lastDot > firstDot)
      viewClsPackage2 = packageName.substring(firstDot+7);
    
    String viewClsName = viewClsPackage1+".view.config."+
    ((viewClsPackage2!=null) ? viewClsPackage2+"." : "")+clsName;
    
    try {
      return Class.forName(viewClsName);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
          e, "Không tìm thấy lớp {0}", viewClsName);
    }
  }
  
  /**
   * @effects 
   *  create (if not already) the data source schema(s) that are specified in <tt>classes</tt> 
   *  
   * @throws DataSourceException
   */
  public static void registerDataSourceSchemas(DODM schema, Class[] classes) throws DataSourceException {
    // if there are domain classes defined in the sub-packages of the model package then 
    // we must create the database schemas for these sub-packages
    Stack<String> schemas = new Stack();
    //String[] clsNames;
    String schemaName;
    
    boolean created;
    DSMBasic dsm = schema.getDsm();
    for (Class c : classes) {
      schemaName = dsm.getDomainSchema(c);
      if (schemaName != null && !schemas.contains(schemaName)) {
        schemas.push(schemaName);
        created = schema.getDom().addSchemaIfNotExist(schemaName);
        System.out.println(((created) ? "Created schema: " : "Registered schema: ") + schemaName);
      }
    }
  }

  /**
   * @effects 
   *  create and return a memory-based {@link DODMConfig}.
   *  
   * @version 5.4
   */
  public static DODMConfig createMemoryBasedDODMConfig(String appName) {
    Configuration config = SwTk.createMemoryBasedConfiguration(appName);
    return config.getDodmConfig();
  }
}
