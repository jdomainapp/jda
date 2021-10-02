package jda.modules.mbsl.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.Null;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mbsl.exceptions.DomainMessage;
import jda.modules.mbsl.model.appmodules.ModuleAct;
import jda.modules.mbsl.model.appmodules.meta.MAct;
import jda.modules.mbsl.model.graph.ActivityGraph;
import jda.modules.mbsl.model.graph.Edge;
import jda.modules.mbsl.model.graph.JoinNode;
import jda.modules.mbsl.model.graph.Node;
import jda.modules.mbsl.model.graph.NodeFactory;
import jda.modules.mbsl.model.graph.NodeType;
import jda.modules.mbsl.model.graph.meta.AGraph;
import jda.modules.mbsl.model.graph.meta.ANode;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents an activity model.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ActivityModel {
  /**name of the activity model*/
  private String name;
  /**activity domain class*/
  private Class activityCls;
  /**referenced domain classes*/
  private List<Class> refClses;

  /**initial classes */
  private List<Class> initClses;
  
  /**the domain schema to which all the domain classes contained in this are registered*/
  private DSMBasic dsm;

  ///// helper data structures 
  /** keys = {@link #refClses} /\ values = {a | exists k in keys. a = assign(ANode,k)} */
  private Map<Class,ANode> graphNodeCfgMap;

  /** keys = {@link #refClses} /\ values = {o | exists k in keys. o = assign(OutCls,k)} */
  private Map<Class,Class[]> graphOutClsCfgMap;
  
  private ActivityGraph graph;

  /**
   * @effects 
   *  initialise this with <tt>name</tt> and <tt>dsm</tt>
   */
  public ActivityModel(String name, DSMBasic dsm) {
    this.name = name;
    this.dsm = dsm;
    refClses = new ArrayList<>();
    initClses = new ArrayList<>();
  }


  /**
   * @modifies {@link #activityCls}, {@link #graphNodeCfgMap}, {@link #graphOutClsCfgMap}
   * @effects 
   *  set the activity class to <tt>actCls</tt>, 
   *  updates {@link #graphNodeCfgMap}, {@link #graphOutClsCfgMap} with activity graph configuration from <tt>actCls</tt>
   */
  public void setActivityCls(Class actCls) throws NotFoundException, ConstraintViolationException {
    this.activityCls = actCls;
    
    AGraph[] agraphs = (AGraph[]) actCls.getAnnotationsByType(AGraph.class);

    if (agraphs == null || agraphs.length == 0) {
      // error: no graph config
      throw new NotFoundException(DomainMessage.ERR_GRAPH_CONFIGURATION_NOT_FOUND_WHEN_REQUIRED, 
          new Object[] {actCls});
    }

    AGraph agraph = agraphs[0];
    ANode[] graphCfgs = agraph.nodes();

    if (graphCfgs.length == 0) {
      // error: no graph config
      throw new NotFoundException(DomainMessage.ERR_GRAPH_CONFIGURATION_NOT_FOUND_WHEN_REQUIRED, 
          new Object[] {actCls});
    }
    
    Class c; Class[] outClses;
    for (ANode ncfg : graphCfgs) {
      c = ncfg.refCls();
      
      if (ncfg.init()) initClses.add(c);

      if (graphNodeCfgMap == null) graphNodeCfgMap = new LinkedHashMap<>();
      graphNodeCfgMap.put(c, ncfg);
      
      outClses = ncfg.outClses();
      if (graphOutClsCfgMap == null) graphOutClsCfgMap = new LinkedHashMap<>();
      if (outClses.length > 0)
        graphOutClsCfgMap.put(c, outClses);
    }
    
    // if initClses is empty: error
    if (initClses.isEmpty())
      throw new ConstraintViolationException(DomainMessage.ERR_GRAPH_HAS_NO_INITIAL_NODES, new Object[] {actCls});
  }
  
  /**
   * @effects 
   *  return the activity class
   */
  public Class getActivityCls() {
    return activityCls;    
  }
  
//  /**
//   * @modifies {@link #refClses}, {@link #graphCfgMap}
//   * @effects 
//   *  add <tt>c</tt> to this as a referenced domain class <br>
//   *  if exists {@link ANode} assignment <tt>a</tt> of <tt>c</tt>
//   *     add <tt>(c,a))</tt> to {@link #graphNodeCfgMap}
//   *  <br>
//   *  if exists {@link OutCls} assignment <tt>o</tt> of <tt>c</tt>
//   *     add <tt>(c,o))</tt> to {@link #graphOutClsCfgMap}  
//   */
//  public void addRefCls(Class c) {
//    refClses.add(c);        
//    
//    ANode a = (ANode)c.getAnnotation(ANode.class);
//    if (a != null) {
//      if (graphNodeCfgMap == null) graphNodeCfgMap = new HashMap<>();
//      graphNodeCfgMap.put(c, a);
//    }
//    
//    OutCls o = (OutCls) c.getAnnotation(ANode.class);
//    if (o != null) {
//      if (graphOutClsCfgMap == null) graphOutClsCfgMap = new HashMap<>();
//      graphOutClsCfgMap.put(c, o);
//    }
//  }
  
  /**
   * @effects 
   *  return all the referenced domain classes
   */
  public List<Class> getRefClses() {
    return refClses;
  }
  
//  /**
//   * @effects 
//   *  set this.{@link #initClses} = <tt>initClses</tt> 
//   */
//  public void setInitClses(Class[] initClses) {
//    this.initClses = initClses;
//  }
  
  /**
   * @effects 
   *  return the initial classes (in the activity flow order)
   */
  public List<Class> getInitClses() {
    return initClses;
  }
  
  /**
   * @effects 
   *  return the sequence of the domain classes C1,...,Cn that are at the receiving ends of 
   *  <tt>n</tt> activity edges from class <tt>source</tt>,
   *  or return <tt>null</tt> if no such class exists
   *  
   */  
  public List<Class> getNSeqClses(Class source) {
    // TODO
    return null;
  }

  /**
   * @effects 
   *  return all the referenced domain classes, whose corresponding nodes have nodeTypes ≠ t
   */    
  public List<Class> getRefClsesExcl(NodeType t) {
    // TODO
    return null;
  }
    
  /**
   * @effects 
   *  return the control class Ck that is at the receiving end of an activity edge from class <tt>source</tt>,
   *  or return <tt>null</tt> if no such class exists
   */ 
  public Class getCkCls(Class source) {
    //TODO
    return null;
  }

  /**
   * @effects 
   *  return the sequence of classes that are the out classes of a given class <tt>c</tt>
   */ 
  public List<Class> getOutClses(Class c) {
    //TODO
    return null;
  }

  /**
   * @effects 
   *  return the first referenced domain class, whose corresponding node has nodeType = t
   */ 
  public Class getRefCls(NodeType t) {
    //TODO
    return null;
  }
  
  /**
   * @effects 
   *  return the last referenced domain class (in the activity flow order)
   */ 
  public Class getLastRefCls() {
    //TODO
    return null;
  }
  
  /**
   * @effects
   *  if {@link #graph} is null
   *    generate {@link #graph}
   *    
   *  return {@link #graph}
   */
  public ActivityGraph getGraph() throws ConstraintViolationException {
    if (graph == null) {
      // generate graph (once)
      genGraph();
    }
    
    return graph;
  }

  /**
   * @modifies this.{@link #graph}
   * @effects 
   *  create {@link #graph} as a new {@link ActivityGraph}, from the graph configurations defined in {@link #activityCls}
   */
  private void genGraph() throws ConstraintViolationException {
    graph = new ActivityGraph();
    
    Class c, s; ANode a;
    String l; NodeType t; MAct[] P; Node n; String[] attribNames;
    Map<Class,Node> nodeMap = new LinkedHashMap<>();
    List<ModuleAct> optSeq = null;
    
    // create nodes without edges
    for (Entry<Class,ANode> e : graphNodeCfgMap.entrySet()) {
      c = e.getKey();
      a = e.getValue();
      l = "M"+c.getSimpleName();
      t = a.nodeType();
      P = a.actSeq();
      s = a.serviceCls();
      if (s == Null.class) s = null;
      
      // create node based on t
      n = NodeFactory.createNode(t, l, c, s);
      //n.setTransformResult(a.transformResult());
      
      // create ModuleOpt sequence of n
      if (P.length > 0) {
        if (optSeq == null) optSeq = new ArrayList<>(); else optSeq.clear();
        
        for (MAct p : P) {
          optSeq.add(new ModuleAct(p.actName(), 
              p.endStates().length == 0 ? null : p.endStates(),
              p.attribNames().length == 0 ? null : p.attribNames(),    
              n));
        }
        
        n.setActSeq(optSeq);
      }
      
      // add n to graph
      graph.addNode(n);
      nodeMap.put(c, n);
      
      // if c is initial class then n is the initial node
      if (initClses.contains(c)) {
        graph.addInitNode(n);
      }
    }
    
    // create out-edges of each node, and edges of g
    Class[] outClses; // o
    Node no; Edge edge;
    for (Entry<Class,Node> e : nodeMap.entrySet()) {
      c = e.getKey();
      n = e.getValue();
      outClses = graphOutClsCfgMap.get(c);
      
      if (outClses != null) {
        for (Class co : outClses) {
          no = nodeMap.get(co);
          edge = new Edge(n, no);
          n.addOutEdge(edge);
          
          // if no is a join node the update its pre
          if (no instanceof JoinNode) {
            ((JoinNode) no).addPreNode(n);
          }
          
          graph.addEdge(edge);
        }
      }
    }
  }

  /**
   * @effects 
   *  executes this {@link #graph} using <tt>actMService</tt> as module service of the activity module of {@link #activityCls}.  
   *  
   *  <p>throws NotPossibleException if failed  
   */
  public void exec(ModuleService actMService, Object...args) throws NotPossibleException {
    getGraph(); // make sure that activity graph is generated
    
    graph.exec(actMService, args);
  }
  
  /**
   * @effects 
   *  if this is empty (i.e. containing no referenced domain classes)
   *    return true
   *  else
   *    return false 
   */
  public boolean isEmpty() {
    return refClses.isEmpty();
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
    return "ActivityModel (" + name + ", " + activityCls + ")";
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
    result = prime * result + ((activityCls == null) ? 0 : activityCls.getName().hashCode());
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
    ActivityModel other = (ActivityModel) obj;
    if (activityCls == null) {
      if (other.activityCls != null)
        return false;
    } else if (!activityCls.equals(other.activityCls))
      return false;
    return true;
  }
}
