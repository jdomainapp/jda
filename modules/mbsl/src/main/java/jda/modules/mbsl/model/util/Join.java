package jda.modules.mbsl.model.util;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.graph.Node;

/**
 * @overview 
 *  Represents domain classes that serve as the join class. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public interface Join {

  /**
   * @effects 
   *  transform the input <tt>args</tt> 
   *  to return an {@link Object}[] to be used as the token to be offered to the out-going edges.
   *  
   *  <p>If no transformation is found suitable for <tt>args</tt> then return <tt>args</tt> itself as the result.
   *  
   * @version 4.0
   */
  Object[] transform(Node decisionNode, Object[] args) throws NotPossibleException;

}
