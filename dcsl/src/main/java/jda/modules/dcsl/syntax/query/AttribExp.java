package jda.modules.dcsl.syntax.query;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;
import jda.modules.common.expression.Op;
import jda.modules.common.types.Null;
import jda.modules.dcsl.syntax.function.Function;

/**
 * @overview 
 *  Represents a constraint expression on a domain attribute, as shown in the following example.
 *  
 * @example
 * 
 * (1) Natural language: Student.name contains "Le"
 * 
 * <p>attribute constraint expression: <pre>
 *  function={@link Function#nil}
 *  attrib="name"
 *  op = {@link Op#MATCH}
 *  value = "%Le%" </pre> 
 *  
 *  <p>
 *  (2) Natural language: year(Student.dob) = 1980
 * <p>attribute constraint expression: <pre>
 *  function={@link Function#year}
 *  attrib="dob"
 *  op = {@link Op#EQ}
 *  value = "1980" </pre> 
 *  
 * @author dmle
 * 
 * @version 3.1
 * 
 */
@Documented
public @interface AttribExp {

  /**
   * (Optional) the function over {@link #attrib()} (see example above for more details) 
   */
  Function function() default Function.nil;
  
  /**
   * the domain attribute name
   */
  String attrib();

  /**
   * the operator that applies to {@link #attrib()} and {@link #value()}
   * (see example above for more details) 
   */
  Op op();

  /**
   * the string-format value of this expression (see example above for more details), which must be convertible 
   * to a domain value suitable for {@link #attrib()}
   */
  String value();

  /**
   * the {@link Class} (built-in or domain) that is the data type of {@link #value()}.
   * 
   * This needs not be specified (and thus takes the default value) for built-in data type of {@link #attrib()}.
   * 
   *  <p>Default: {@link CommonConstants#NullType}
   */
  Class valueType() default Null.class;
}
