package jda.modules.mbsl.model.appmodules.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.mbsl.model.appmodules.ModuleAct;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;

/**
 * @overview 
 *  Configures the state of a {@link ModuleAct}
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=java.lang.annotation.ElementType.FIELD)
@Documented
public @interface MAct {

  /**
   * The operation name
   */
  MethodName actName();

  /**
   * (Optional) The mutually exclusive sequence of ending execution states
   *  <br>Default: []
   */
  AppState[] endStates() default {};

  /**
   * (Optional) the names of the domain attributes of the domain class of the referenced module service of this
   * whose values are to be set by invocation of this.
   * <p>This is ONLY applicable for certain type of {@link #actName()} (e.g. <tt>setDataFieldValue</tt>). 
   *
   * <br>Default: []
   */
  String[] attribNames() default {};
}
