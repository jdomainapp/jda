package jda.modules.dcsl.syntax;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @overview 
 *  Container annotation for {@link DOpt}
 *  
 * @author dmle
 *
 * @version 3.3 
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DOpts {
  DOpt[] value();
}
