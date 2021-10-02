package jda.modules.mbsl.model.graph.meta;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.mbsl.model.graph.ActivityGraph;

/**
 * @overview 
 *  Configures an {@link ActivityGraph} as being consisted of nodes.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value=java.lang.annotation.ElementType.TYPE)
@Documented
public @interface AGraph {
  /**
   * @effects
   *  The set of {@link ANode}s that make up this graph 
   */
  ANode[] nodes();
}
