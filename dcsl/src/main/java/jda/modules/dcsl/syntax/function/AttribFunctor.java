package jda.modules.dcsl.syntax.function;

import java.lang.annotation.Documented;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.function.Function;

/**
 * @overview Function-based definition of an attribute being referred to by {@link Selectx}. The attribute  
 * must correspond to a valid attribute in the last domain class specified in  
 * {@link Selectx#classJoin()}. The function name must be one of the valid names defined in {@link Function}.
 * 
 * <p>If function is {@link Function#nil} then this simply refers to an attribute.  
 * 
 * <p>Example:<br>
 * Given the class join example between <tt>Customer</tt> and <tt>CustomerOrder</tt> 
 * in {@link Selectx#classJoin()}, the following definition simply refers to the attribute 
 * <tt>CustomerOrder.orderDate</tt>
 *  <pre>@AttribDef(function=FunctionName.nil,attrib="orderDate")</pre> 
 * 
 * The following definition 
 * defines the function <tt>month(orderDate)</tt> over the attribute named  
 * <tt>orderDate</tt> of the class <tt>CustomerOrder</tt>
 * <pre>@AttribDef(function=FunctionName.month,attrib="orderDate")</pre>
 *   
 * The following definition 
 * defines the function <tt>month(orderDate)</tt> over the attribute named  
 * <tt>orderDate</tt> of the class <tt>CustomerOrder</tt> and applies the operator {@link Op#GT}
 * to the range of the function (the operand is provided at run-time by the user):
 * <pre>@AttribDef(function=FunctionName.month,attrib="orderDate",operator=Op.GT)</pre>
 *  
 * @author dmle
 */
@Documented
public @interface AttribFunctor {
  /**
   * Name of a valid function (specified in {@link Function})
   */
  Function function();
  
  /**
   * Valid name of a domain attribute
   */
  String attrib();

  /**
   * (Optional) The operator that is applied to the range of {@link #function}({@link #attrib()})  
   * Default: {@link Op#EQ}
   */
  Op operator() default Op.EQ;
}
