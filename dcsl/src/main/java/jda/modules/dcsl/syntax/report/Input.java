package jda.modules.dcsl.syntax.report;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jda.modules.common.CommonConstants;
import jda.modules.common.expression.Op;
import jda.modules.common.types.Null;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;

/**
 * @overview
 *  Annotates a domain attribute of a report class as the input 
 *  
 * @author dmle
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Input {
  
  /**
   * <b>Note</b>: Used ONLY for <b>simple</b>input expressions that can be written without
   * using {@link #refFuncClass()}.
   * 
   * <p>Specify which domain attribute of a domain class the attribute that this 
   * attribute (the attribute upon which this annotation is being applied to) references. 
   * It has the same purpose as the SQL SELECT statement. 
   * 
   * <p>A more detailed explanation is found in {@link Selectx}.
   * 
   * <p>Default: a <tt>Selectx</tt> constant whose <tt>join = NullType.class</tt>
   */
  public Selectx reference() default @Selectx(
      classJoin={Null.class}, //NullType.class},
      attribFunc=@AttribFunctor(function=Function.nil,attrib="",operator=Op.EQ));

  /**
   * <b>Note</b>: Used ONLY for <b>complex</b> input expression that is not expressible by {@link #reference()}.
   * 
   * <br>Specifies the function class, whose implementation provides input value for this attribute.
   * 
   * <p>This function class must specify a <b>getter</b> method for the specified attribute whose 
   * return type matches that of the attribute.
   * 
   * <br>Default: {@link CommonConstants#NullType}
   */
  public Class refFuncClass() default Null.class;
}
