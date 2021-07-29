package jda.modules.mccl.syntax.containment;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;

/**
 * @overview
 *  Meta-attribute for specifying the containment tree of a composite module
 * @author dmle
 * 
 * @version 
 * - 5.1c: upgraded to become CTree
 */
@Documented
public @interface CTree {

  /**
   * The domain class of the root module 
   * <p>Default: {@link CommonConstants#NullType}
   */
  Class root() default Null.class;

  /**
   * State scope of the root module ({@link #root()}
   * <p>Default: <tt>{}</tt> (empty array)
   */
  String[] stateScope() default {};

  /**
   * The configured CEdges of this {@link CTree}
   * <br>Default: <tt>{}</tt> (empty array)
   * @version 5.1c
   */
  CEdge[] edges() default {};
  
  /**
   * The sub-trees of the containment tree
   * <p>Default: <tt>{}</tt> (empty array)
   * @deprecated (v5.1c) use {@link #edges} instead
   */
  @Deprecated
  SubTree1L[] subtrees() default {};
}
