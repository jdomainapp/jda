package jda.modules.dcsl.syntax.report;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;

/**
 * @overview
 *  Annotates a domain attribute of a report class as the output.
 * @author dmle
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Output {

  /**
   * The class whose object implements a filter for the value of this attribute.
   * @version 2.7.4
   * 
   * <br>Default: {@link CommonConstants#NullType} (i.e. not specified)
   */
  Class filter() default Null.class;

  /**
   * The class whose objects will be displayed on the output attribute represented by this.
   * Use this <b>ONLY</b> if the output class differs from that of the output attribute.
   * 
   * <br>Default: {@link CommonConstants#NullType} (i.e. same as that of the attribute.
   */
  Class outputClass() default Null.class;
}
