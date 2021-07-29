package jda.modules.mccl.syntax.containment;

import java.lang.annotation.Documented;


/**
 * @overview
 *  A sub-tree of {@link CTree}
 * @author dmle
 *
 * @version 
 * - 5.1c: make deprecated
 * 
 * @deprecated by {@link CEdge}
 */
@Documented
@Deprecated
public @interface SubTree1L {

  /**
   * The domain class of the root module of the sub-tree
   */
  Class parent();

  /**
   * The set of edges that connect {@link #parent()} to the child nodes of this sub-tree 
   */
  Child[] children();
}
