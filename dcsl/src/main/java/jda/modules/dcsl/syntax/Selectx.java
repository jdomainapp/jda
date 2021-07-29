package jda.modules.dcsl.syntax;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;

/**
 * @overview 
 * An extended field annotation (compared to {@link Select}) that is used to annotate derived attributes 
 *  
 * @author dmle
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.FIELD})
//@java.lang.annotation.Inherited
//TODO: support join operator
@Documented
public @interface Selectx {
  
  /** 
   * An array of one or more domain classes that define an  
   * attribute being referenced in {@link #attribFunc()}.
   *  
   * <p>The first class in the array is the class that requires access to the value of the 
   * referenced attribute. The last class in the array (which may be the same as the first)  
   * is the one in which the referenced attribute is defined.
   * 
   * <p>If {@link #classJoin()}<tt>.length > 1</tt> then the classes in the array must form a chain of class associations (similar to  
   * an SQL join}.  
   *  
   * <p>If {@link #classJoin()}<tt>.length = 1</tt> then the class in the array must own the attribute.
   *   
   * <p>Example:<br>
   * This join defines an attribute in the class <tt>CustomerOrder</tt>
   * which is being referred to by an object of the class <tt>Customer</tt> 
   * <pre>
   * {Customer,CustomerOrder}
   * </pre>
   **/
  public Class[] classJoin();

  /**
   * Names of the {@link DAssoc}s between the domain classes specified in {@link #classJoin()}. These names must match 
   * the {@link DAssoc}'s names as defined in these classes.
   * 
   * <p>It must be specified if {@link #classJoin()}<tt>.length > 1</tt>, and in which case the number of names must be exactly 
   * equal to the number of classes less 1. It takes the default value if otherwise. 
   * 
   * <p>Default value: {{@link CommonConstants#NullString}}
   */
  public String[] joinAssocs() default {CommonConstants.NullString};
  
  /**
   * Function-based definition of the attribute being referred to by this. The attribute  
   * must correspond to a valid attribute in the last domain class specified in  
   * {@link #classJoin()}. The function name must be one of the valid names defined in {@link Function}.
   * 
   * <p>If function is {@link Function#nil} then this  
   * simply refers to an attribute. 
   * 
   * <p>Example:<br>
   * Given the class join example between <tt>Customer</tt> and <tt>CustomerOrder</tt> 
   * in {@link #classJoin()}, the following definition 
   * defines the function <tt>month(orderDate)</tt> over the attribute named  
   * <tt>orderDate</tt> of the class <tt>CustomerOrder</tt>
   * <pre>
   * @AttribDef(function=Function.month,attrib="orderDate")
   *  </pre>
   *  
   *  <p>The following definition simply refers to the attribute <tt>CustomerOrder.orderDate</tt>
   *  <pre>
   * @AttribDef(function=Function.nil,attrib="orderDate")
   *  </pre>
   **/
  public AttribFunctor attribFunc() ;
}
