package jda.modules.common.types.properties;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;

/**
 * @overview
 *  Specify a property, which will be converted into a Property object. 
 * 
 * @example
 * <tt>Property("sunnyOrNot",Boolean.TRUE,Boolean.class)</tt> is specified as 
 * <tt>PropertyDesc(name="sunnyOrNot",valueAsString=Boolean.TRUE.toString(),valueType=Boolean.class)</tt>
 * 
 * @author dmle
 */
public @interface PropertyDesc {
  /**Property name */
  PropertyName name();
  
  /**
   * Property value as <tt>String</tt> (i.e. its <tt>toString</tt>) whose type is specified by {@link #valueType()}. 
   * Use this for properties that are not of type Java class. 
   * 
   * For Java-class properties, use {@link #valueIsClass()} instead and 
   * set {@link #valueAsString()} to {@link CommonConstants#NullValue}.
   * */
  String valueAsString();


  /**
   * A <b>special</b> type of property value that is a Java class. When this is used, 
   * set {@link #valueType()} = <tt>Class.class</tt>. 
   * 
   * Note that {@link #valueAsString()} will be ignored.
   * 
   * <p>Default: {@link CommonConstants#NullType}
   * */
  Class valueIsClass() default Null.class; 

  /**
   * Property value type (i.e. the declared type of the value). This is used to convert 
   * {@link #valueAsString()} to the actual value type.
   * 
   * <p>The supported type values are as defined in <tt>Property.parseValue</tt>  
   * 
   * <p>Example: <tt>Property("sunnyOrNot",Boolean.TRUE,Boolean.class)</tt> is specified as 
   * <tt>PropertyDesc(name="sunnyOrNot",valueAsString=Boolean.TRUE.toString(),valueType=Boolean.class)</tt>
   */
  Class valueType();
}
