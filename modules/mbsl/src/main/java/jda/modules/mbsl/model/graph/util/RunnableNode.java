package jda.modules.mbsl.model.graph.util;

import jda.modules.mbsl.model.graph.Node;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  A {@link Runnable} wrapper around an {@link Node} in order to run it in a thread pool.
 *  
 * @author Duc Minh Le (ducmle)
 */
public class RunnableNode implements Runnable {

  private Node node;
  private ModuleService actMService;
  private Object[] args;
  private Node src;

  /**
   * @effects 
   *  initialise this with <tt>node</tt>
   */
  public RunnableNode(Node node) {
    this.node= node;
  }

  /**
   * @effects 
   *  set running state of {@link #node} (needed by {@link #run()}) to <tt>actMService, args</tt> 
   */
  public void setState(Node src, ModuleService actMService, Object...args) {
    this.src = src;
    this.actMService = actMService;
    this.args = args;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public void run() {
    node.exec(src, actMService, args);
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
    return "RunnableNode (" + node + ")";
  }
  
  
} /** end {@link RunnableNode} */ 