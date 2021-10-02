package jda.modules.mbsl.model.graph.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @overview 
 *  Container annotation for {@link ANode}
 *  
 * @author Duc Minh Le (ducmle)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=java.lang.annotation.ElementType.FIELD)
@Documented
public @interface ANodes {
  ANode[] value();
}
