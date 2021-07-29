package jda.modules.dcsl.syntax;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  Define <b>complex</b> value expression for a domain attribute, which consists of a simple expression that 
 *  is bound to the domain attribute of another class. It takes this form <tt>({@link Op} {@link #clazz()}.{@link #attribute()})</tt>. 
 *  
 *  <p>The value of the target (bounded) domain attribute will be retrieved at run-time and evaluated w.r.t the operator
 *  and the result is then used as the default value for this attribute.  
 *  
 *  <p>This is typically used for complex default value specifications that are not currently supported by {@link DAttr#defaultValue()} and 
 *  {@link DAttr#defaultValueFunction()}.  
 *  
 * @example
 *  Natural language: the value of this attribute is 
 *  'equal to the value of the domain attribute of <tt>Enrolment.startDate</tt>'
 *  
 *  </pre>
 *  @DefValueDesc(op=Op.EQ,clazz=Enrolment.class,attribute=Enrolment.Attribute_startDate
 *  </pre>
 *  
 * @author dmle
 * 
 * @version 3.2c
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainValueDesc {

  /**
   * The operator of the expression 
   * 
   * <p>Default: {@link Op#EQ}
   */
  Op op() default Op.EQ;
  
  /**
   * The domain class (e.g. <tt>Enrolment</tt>) in which the target attribute is defined
   */
  Class clazz();

  /**
   * specifies name of the target domain attribute of {@link #clazz()} whose value will be 
   * evaluated against {@link #op()} to give the result
   */
  String attribute();  
}
