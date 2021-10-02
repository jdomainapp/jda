package jda.modules.mbsl.model.graph;

import java.util.ArrayList;
import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.appmodules.ModuleAct;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents activity nodes.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class Node {
  private String label;
  private Class refCls;
  private Class serviceCls;
  private List<ModuleAct> actSeq;
  private List<Edge> out;
  //private boolean transformResult;
  
  ///// derived attributes
  /**derived from {@link #refCls}*/
  private ModuleService refModuleService;
  
  /** derived from the complete status of {@link #exec(Node, ControllerBasic, Object...)}*/
  private boolean stopped;
  
  public Node(String label, Class refCls, Class serviceCls) {
    this.label = label;
    this.refCls = refCls;
    this.serviceCls = serviceCls;
  }

  /**
   * @modifies this.{@link #actSeq}
   * @effects 
   *   set this.{@link #actSeq} to contain those in <tt>actSeq</tt> (removing any existing 
   *   entries of {@link #actSeq}) 
   */
  public void setActSeq(List<ModuleAct> actSeq) {
    if (actSeq != null) {
      this.actSeq = new ArrayList<>();
      for (ModuleAct opt : actSeq) {
        this.actSeq.add(opt);
      }
    } else {
      this.actSeq = null;
    }
  }

  /**
   * @effects return out
   */
  public List<Edge> getOut() {
    return out;
  }

  /**
   * @modifies this.{@link #out}
   * @effects 
   *  if this.{@link #out} does not contain e
   *    add e to this.{@link #out} (initialise it to be empty first if needed)
   *  else
   *    do nothing
   */
  public void addOutEdge(Edge e) {
    if (out == null) out = new ArrayList<>();
    
    if (!out.contains(e)) {
      out.add(e);
    }
  }
  
  /**
   * @effects 
   *  return {@link #refCls}
   */
  public Class getRefCls() {
    return refCls;
  }
  
//  /**
//   * @effects 
//   *  set this.{@link #transformResult} = transformResult 
//   */
//  public void setTransformResult(boolean transformResult) {
//    this.transformResult = transformResult;
//  }
//
//  /**
//   * @effects 
//   *  if this is configured to transform result on behalf of the outgoing edge
//   *    return true
//   *  else
//   *    return false 
//   */
//  public boolean isTransformResult() {
//    return transformResult;
//  }
//
//  /**
//   * @requires 
//   *  {@link #isTransformResult()} = true
//   * @effects 
//   *  if this supports transformation (i.e. {@link #isJoinRefClass()} = true)
//   *    transform input into an {@link Object} that is to be used as the output token
//   *  else
//   *    return <tt>input</tt>
//   */
//  protected Object transformResult(Object[] input) {
//    return input;
//  }
  
  /**
   * @effects 
   *  executes this using <tt>actMService</tt> as the application handle.  
   *  
   *  <p>throws NotPossibleException if failed  
   */
  public void exec(Node src, ModuleService actMService, Object...args) throws NotPossibleException {
    stopped = false;
    
    // (1)
    validate();

    // (2)
    execReceive(src, actMService, args);
    
    // (3) 
    Object[] results = execSelf(src, actMService, args);
    
    // (4)
    execOffer(src, actMService, results);
  }
  
  /**
   * @effects 
   *  receive tokens offered by <tt>src</tt>
   */
  protected void execReceive(Node src, ModuleService actMService, Object...args) {
    // nothing to do here
  }
  
  /**
   * <b>Note:</b> certain GUI-related tasks that are performed by this operation 
   * need to be synchronised so that they would not create conflicts
   * when multiple {@link Node}s are running at the same time. 
   * 
   * @effects 
   *  execute the logic encapsulated by this and return the result as {@link Object}[].
   *  
   *  <p>If no result is produced then an empty array is returned.
   */
  protected Object[] execSelf(Node src, ModuleService actMService, Object...args) throws NotPossibleException {
    
    List<Object> results = new ArrayList<>();
    
    /* This part involves activating the view and performing module operations on it.
     * We therefore need to synchronise this code to avoid potential conflicts 
     *  when multiple nodes are executing at the same time. 
     *  Why? because technically speaking only one view can be activated at any given time!
     */
    synchronized(this) {
      activateRefModuleService(actMService);
  
      if (actSeq != null) { // execute operations     
        //results = new ArrayList<>();//new Object[optSeq.size()];
        //int i = 0;
        
        // execute the operations sequence...
        for (ModuleAct opt : actSeq) {
          opt.exec(refModuleService, args);
        }
        
        // wait for all operations to complete
        for (ModuleAct opt : actSeq) {
          while (!opt.isStopped()) {
            Toolkit.sleep(100);
          }
          
          // finished...obtain result
          //results[i++] = opt.getResult();
          Object result = opt.getResult();
          if (result != null) {
            results.add(result);
          }
        }
      }
    } // end synchronized
    
    // flag stopped to true
    if (!stopped)
      stopped = true;
    
    return results.toArray(new Object[results.size()]);
  }

  /**
   * @effects 
   *  offer tokens to outgoing edges 
   */
  protected void execOffer(Node src, ModuleService actMService, Object...results) throws NotPossibleException {
    // execute outgoing edges
    // TODO: support multiple outgoing edges for action nodes?
    // (control nodes are handled separately by each such type of node)
    if (out != null) {
//      Object transfResult;
//      if (isTransformResult()) {
//        transfResult = transformResult(results);
//      } else {
//        transfResult = results;
//      }
      
      out.get(0).exec(actMService, results);
    }    
  }

  /**
   * @modifies this.{@link #refModuleService}
   * @effects <pre> 
   *  if {@link #refModuleService} has not been initialised
   *    initialise it to be a module service based on {@link #serviceCls} or {@link #refCls}
   *  
   *  if {@link #refModuleService} has view
   *    activite its view
   *  </pre>
   */
  protected void activateRefModuleService(final ModuleService actMService) {
    if (refModuleService == null) { 
      // refModuleService has not been initialised, initialise it
      if (serviceCls != null) {
        if (actMService.isDataService(serviceCls)) {
          // data controller service
          // look up the descendant data service of refCls in the containment hierarchy of actMService's module
          refModuleService = actMService.getModule().getDescendantDataService(refCls);
        } else {
          // controller service
          refModuleService = actMService.getContext().lookUpPrimaryService(refCls);
        }
      }
    }
    
    if (refModuleService != null) {
      if (refModuleService.hasView()) refModuleService.activateView();
    }
  }
  
  /**
   * @effects 
   *  if this is not valid w.r.t rules below
   *    throws {@link ConstraintViolationException}
   *  else
   *    do nothing
   * @rules <pre>
   *  (1) {@link #refCls} != null /\ this is not a control node => {@link #actSeq} != null
   *  (2) {@link #refCls} != null /\ this is not a control node => {@link #serviceCls} != null
   * </pre>
   *  
   */
  protected void validate() throws ConstraintViolationException {
    if (refCls != null && !isControlType()) {
      if (actSeq == null)
        throw new ConstraintViolationException(ConstraintViolationException.Code.OBJECT_STATE_VIOLATES_RULE, new Object[] {this, "(1)"}); // rule 1
      
      if (serviceCls == null)
        throw new ConstraintViolationException(ConstraintViolationException.Code.OBJECT_STATE_VIOLATES_RULE, new Object[] {this, "(2)"}); 
    }
    
  }

  /**
   * @effects 
   *  if this is a {@link ControlNode}
   *    return true
   *  else
   *    return false
   */
  private boolean isControlType() {
    return (this instanceof ControlNode);
  }

  
  /**
   * @effects set stopped = stopped
   */
  public void setStopped(boolean stopped) {
    this.stopped = stopped;
  }

  /**
   * @effects
   *  if {@link #exec(Node, ControllerBasic, Object...)} has completed
   *    return true
   *  else
   *    return false 
   */
  public boolean isStopped() {
    return stopped;
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
    return getClass().getSimpleName()+" (" + label + ")";
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
    result = prime * result + ((label == null) ? 0 : label.hashCode());
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
    Node other = (Node) obj;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    return true;
  }
}
