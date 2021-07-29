package jda.mosa.model;

import java.lang.annotation.Documented;

import jda.mosa.model.assets.DataValidatorFunction;

/**
 * @overview
 *  An annotation used to define data-validator-function for a domain class.
 *  
 * @author dmle
 *
 * @version 3.3
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
@Documented
public @interface DDataValidator {
  /**
   * The domain-specific class of data-validator-function, which must 
   * be a sub-type of {@link DataValidatorFunction}
   */
  Class type();
}
