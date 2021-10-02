package jda.modules.mbsl.model.graph;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.graph.guards.Guard;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents an activity edge. An activity edge is a binary, directed edge.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class Edge {

  private int id;
  private static int idCounter = 0;
  
  private Node n1;
  private Node n2;
  
//  private Guard guard;

  /**
   * @effects
   *  initialise this as <tt>(n1,n2)</tt> 
   */
  public Edge(Node n1, Node n2) {
    id = ++idCounter;
    this.n1 = n1;
    this.n2 = n2;    
  }

  /**
   * 
   * @effects
   *  return {@link #n1} 
   *
   */
  public Node getSource() {
    return n1;
  }
  
  /**
   * @effects 
   *  return {@link #n2} 
   */
  public Node getTarget() {
    return n2;
  }
  
//  /**
//   * @effects 
//   *  if this.{@link #guard} is not null /\ <tt>args</tt> satisfy this.{@link #guard}
//   *    return true
//   *  else
//   *    return false
//   */
//  public boolean isSat(Object[] args) {
//    return guard == null || guard.isSat(args);
//  }
  
  /**
   * @effects 
   *  executes this using <tt>actMService</tt> as the application handle.  
   *  
   *  <p>throws NotPossibleException if failed
   */
  public void exec(ModuleService actMService, Object...args) throws NotPossibleException {
    n2.exec(n1, actMService, args);
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "Edge (" + id + ", " + n1 + ", " + n2 + ")";
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Edge other = (Edge) obj;
    if (id != other.id)
      return false;
    return true;
  }
}
