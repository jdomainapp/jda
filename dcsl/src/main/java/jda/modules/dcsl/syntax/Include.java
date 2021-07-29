package jda.modules.dcsl.syntax;

import java.lang.annotation.Documented;

/**
 * @overview
 *    Attached to a class to define the members that are to be included for consideration.
 *    
 *    <p>For example, it is used in an <tt>enum</tt> to specify the constants of that enum that 
 *    are to be considered as domain objects. 
 *    
 *    <p>Attribute {@link #members()} specify the exact name of the members that are defined.  
 *  
 * @author dmle
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
@Documented
public @interface Include {
  String[] members();
  // empty
}