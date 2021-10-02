package jda.modules.mbsl.model.graph;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.graph.util.RunnableEdge;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents fork nodes.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class ForkNode extends ControlNode {

  /** the thread pool manager for executing forked tasks defined by this*/
  private ExecutorService threadPool;
  private RunnableEdge[] runnableEdges;
  
  /**
   * @effects 
   *
   * @version 
   */
  public ForkNode(String label, Class refCls, Class serviceCls) {
    super(label, refCls, serviceCls);
    
    // thread pool manager
    threadPool = Executors.newCachedThreadPool();
  }

  /* (non-Javadoc)
   * @see domainapp.modules.activity.model.graph.Node#exec(domainapp.basics.core.ControllerBasic, java.lang.Object[])
   */
  /**
   * @effects 
   *  invoke super.{@link #exec(Node, ModuleService, Object...)} /\ 
   *  || [e ∈ out] e.exec() (i.e. execute all the {@link #runnableEdges})  
   */
  @Override
  public void exec(Node src, ModuleService actMService, Object... args)
      throws NotPossibleException {
    setStopped(false);

    // (1)
    validate();
    
    // (2) 
    execReceive(src, actMService, args);

    // (3) & (4): execSelf, execOffer

    activateRefModuleService(actMService);
    
    // execute the out edges concurrently
    RunnableEdge[] runnableEdges = getRunnableEdges(actMService, args);
    
    for (RunnableEdge r : runnableEdges) {
      threadPool.execute(r);  // execute immediately, without waiting for result...
    }
    
    setStopped(true);

  }

  /**
   * @effects 
   *  if {@link #runnableEdges} is not initialised
   *    initialise it to contain {@link RunnableEdge}s for {@link Edge}s in this.{@link #getOut()}, whose
   *    states are <tt>actMService, args</tt>
   *  else
   *    update the states of {@link RunnableEdge}s in {@link #runnableEdges} to <tt>actMService, args</tt>
   */
  private RunnableEdge[] getRunnableEdges(final ModuleService actMService, final Object[] args) {
    if (runnableEdges == null) {
      List<Edge> out = getOut();
      runnableEdges = new RunnableEdge[out.size()];
      
      int i = 0;
      for (final Edge e : out) {
        RunnableEdge r = new RunnableEdge(e);
        r.setState(actMService, args);
        runnableEdges[i++] = r;
      }
    } else {
      for (RunnableEdge r : runnableEdges) {
        r.setState(actMService, args);
      }
    }
    
    return runnableEdges;
  }
}
