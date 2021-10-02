package jda.modules.mbsl.model.graph.util;

import jda.modules.mbsl.model.graph.Edge;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  A {@link Runnable} wrapper around an {@link Edge} in order to run it in a thread pool.
 *  
 * @author Duc Minh Le (ducmle)
 */
public class RunnableEdge implements Runnable {

  private Edge edge;
  private ModuleService actMService;
  private Object[] args;

  /**
   * @effects 
   *  initialise this with <tt>edge</tt>
   */
  public RunnableEdge(Edge edge) {
    this.edge = edge;
  }

  /**
   * @effects 
   *  set running state of {@link #edge} (needed by {@link #run()}) to <tt>actMService, args</tt> 
   */
  public void setState(ModuleService actMService, Object...args) {
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
    edge.exec(actMService, args);
  }
} /** end {@link RunnableEdge} */ 