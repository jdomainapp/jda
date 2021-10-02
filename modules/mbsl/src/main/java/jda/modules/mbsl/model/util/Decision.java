package jda.modules.mbsl.model.util;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.graph.Edge;
import jda.modules.mbsl.model.graph.Node;

/**
 * @overview 
 *  Represents domain classes that serve as the decision class (of the decision node). 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public interface Decision {

  /**
   * @effects 
   *  evaluate each {@link Edge} in <tt>decisionNode.out</tt> w.r.t the decision input <tt>args</tt> 
   *  to return at most one {@link Edge} to be offered the token(s) of <tt>decisionNode</tt>.
   *  <p>If no edges satisfy then return <tt>null</tt>
   *  
   * @version 4.0
   */
  Edge evaluate(Node decisionNode, Object[] args) throws NotPossibleException;

}
