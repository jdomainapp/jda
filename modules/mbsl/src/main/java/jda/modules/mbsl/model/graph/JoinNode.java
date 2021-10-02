package jda.modules.mbsl.model.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.util.Join;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents join nodes.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class JoinNode extends ControlNode {

  /**a singleton {@link Join} of super.#refCls*/
  private static Join join;

  /**
   * Nodes that connect to this join node.<br>
   * ∀n ∈ pre. ∃e ∈ Edge s.t e = (n,this) 
   */
  private List<Node> pre;
  
  /**
   * buffer of input from nodes
   */
  private List<Object[]> inputBuffer; 
  
  /**
   * @effects 
   */
  public JoinNode(String label, Class refCls, Class serviceCls) {
    super(label, refCls, serviceCls);
    
    pre = new ArrayList<>();
    inputBuffer=new ArrayList<>();
  }

  /**
   * @effects 
   *  if {@link #pre} does not contain n
   *    add n to {@link #pre}
   *  else
   *    do nothing 
   */
  public void addPreNode(Node n) {
    if (!pre.contains(n)) pre.add(n);
  }
  
  /**
   * @effects 
   *  if super.#refCls is a {@link Join}
   *    return true
   *  else
   *    return false
   */
  private boolean isJoinRefClass() {
    return (Join.class.isAssignableFrom(getRefCls()));
  }
  
  /**
   * @requires 
   *  {@link #isJoinRefClass()} = true
   *  
   * @effects 
   *  return super.{@link #getRefCls()} casted to <tt>Class&lt;Fork&gt;</tt>
   */
  private Class<Join> getJoinClass() {
    return (Class<Join>) getRefCls();
  }

  /**
   * @effects <pre>
   *  if {@link #join} has not been initialised
   *    initialise it
   *    throws {@link NotPossibleException} if failed
   *  
   *  return {@link #join}
   *  </pre>
   */
  private static Join getJoinInstance(final Class<Join> joinCls) throws NotPossibleException {
    if (join == null) {
      try {
        join = joinCls.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, new Object[] {joinCls.getSimpleName(), ""});
      }
    }
    
    return join;
  }

  /**
   * @effects 
   *  if this supports transformation (i.e. {@link #isJoinRefClass()} = true)
   *    transform input into an {@link Object[]} that is to be used as the output token(s)
   *    throws NotPossibleException if failed to transform
   *  else
   *    return <tt>input</tt>
   */
  protected Object[] transformResult(Object[] input) throws NotPossibleException {
    if (isJoinRefClass()) {
      Join join = getJoinInstance(getJoinClass());
      
      return join.transform(this, input);
    } else {
      return input;
    }
  }
  
  /* (non-Javadoc)
   * @see domainapp.modules.activity.model.graph.Node#exec(domainapp.basics.core.ControllerBasic, java.lang.Object[])
   */
  /**
   * A <b>synchronized</b> method needed to synchronise the concurrent flows. 
   * 
   * @effects   
   *  invoke super.{@link #exec(Node, ModuleService, Object...)} /\ 
   *  (ψ[n ∈ pre] n.exec()) > out[0].exec())
   */
  @Override
  public synchronized void exec(Node src, ModuleService actMService, Object... args)
      throws NotPossibleException {
    setStopped(false);

    // (1)
    validate();
    
    activateRefModuleService(actMService);

    // (2) execReceive:
    //execReceive(src, actMService, args);
    // buffer the input
    inputBuffer.add(args);

    // ψ[n ∈ pre] n.exec()
    if (inputBuffer.size() < pre.size()) {
      // not yet received input from all nodes: return immediately
      return;
    }
    
    // (3) & (4): execSelf, execOffer

    // execSelf: 
    // received input from all nodes: start processing...
    List resultList = new ArrayList();
    for (Object[] input : inputBuffer) {
      Collections.addAll(resultList, input);
    }
    
    // convert to array
    Object[] resultArr = resultList.toArray(new Object[resultList.size()]);

    /* (optionally) determine the token(s) to be offered to the outgoing edge
       - this mimics the transformation behaviour of ObjectFlow in the UML specification
     */
    Object[] results = null;
    boolean toOffer = true;
    try {
      results = transformResult(resultArr);
    } catch (Exception e) {
      // error occured -> cannot offer to outgoing edges
      toOffer = false;
    } finally {
      // clear input buffer and stop
      inputBuffer.clear();
      
      setStopped(true);
    }
    
    if (toOffer) {
      // execOffer: do out[0].exec()
      getOut().get(0).exec(actMService, results);
    }
  }
}
