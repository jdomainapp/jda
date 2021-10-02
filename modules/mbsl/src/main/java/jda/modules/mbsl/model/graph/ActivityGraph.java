package jda.modules.mbsl.model.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.graph.util.RunnableNode;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents an activity graph, whose nodes are {@link Node} and whose edges are {@link Edge}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ActivityGraph {

  private int id;
  private static int idCounter = 0;
  
  private List<Node> nodes;
  private List<Edge> edges;
  private List<Node> n0;
  
  /** the thread pool manager for executing forked tasks defined by this*/
  private ExecutorService threadPool;
  private RunnableNode[] runnableNodes;

  
  /**
   * @effects 
   *  initialise this to be empty
   */
  public ActivityGraph() {
    nodes = new ArrayList<>();
    edges = new ArrayList<>();
    n0 = new ArrayList<>();
    
    // thread pool manager
    threadPool = Executors.newCachedThreadPool();
    
    id = ++idCounter;
  }

  /**
   * @modifies this.{@link #nodes}
   * @effects 
   *  if this.{@link #nodes} does not contain <tt>n</tt>
   *    add <tt>n</tt> to this.{@link #nodes}
   *  else
   *    do nothing
   */
  public void addNode(Node n) {
    if (!nodes.contains(n))
      nodes.add(n);
  }

  /**
   * @modifies this.{@link #edges}
   * @effects 
   *  if this.{@link #edges} does not contain <tt>e</tt>
   *    add <tt>e</tt> to this.{@link #edges}
   *  else
   *    do nothing
   */
  public void addEdge(Edge e) {
    if (!edges.contains(e)) {
      edges.add(e);
    }
  }
  
  /**
   * @modifies this.{@link #n0}
   * @effects 
   *  if this.{@link #n0} does not contain <tt>n</tt>
   *    add <tt>n</tt> to this.{@link #n0}
   *  else
   *    do nothing
   */
  public void addInitNode(Node n) {
    if (!n0.contains(n))
      n0.add(n);
  }


  /**
   * @effects 
   *  executes this using <tt>actMService</tt> as module service of the activity module.  
   *  
   *  <p>throws NotPossibleException if failed  
   */
  public void exec(ModuleService actMService, Object...args) throws NotPossibleException {
    Node src = null;
//    for (Node n : n0) {
//      n.exec(src, actMService, args);
//    }
    
    // execute the initial nodes concurrently
    RunnableNode[] runnableNodes = getRunnableNodes(src, actMService, args);
    
    // TODO: should hand the case where nodes are rendering GUIs on the tabs of the same GUI 
    for (RunnableNode r : runnableNodes) {
      threadPool.execute(r);  // execute immediately, without waiting for result...
    }
  }

  /**
   * @effects 
   *  if {@link #runnableEdges} is not initialised
   *    initialise it to contain {@link RunnableNode}s for {@link Edge}s in this.{@link #getOut()}, whose
   *    states are <tt>actMService, args</tt>
   *  else
   *    update the states of {@link RunnableNode}s in {@link #runnableEdges} to <tt>actMService, args</tt>
   */
  private RunnableNode[] getRunnableNodes(final Node src, final ModuleService actMService, final Object[] args) {
    if (runnableNodes == null) {
      
      runnableNodes = new RunnableNode[n0.size()];
      
      int i = 0;
      for (final Node e : n0) {
        RunnableNode r = new RunnableNode(e);
        r.setState(src, actMService, args);
        runnableNodes[i++] = r;
      }
    } else {
      for (RunnableNode r : runnableNodes) {
        r.setState(src, actMService, args);
      }
    }
    
    return runnableNodes;
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
    return "ActivityGraph (" + id + ",\n  Nodes::" + nodes + ",\n  Edges::" + edges + ")";
  }
  
}
