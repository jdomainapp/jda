package jda.modules.mbsl.model.graph;

import java.util.List;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mbsl.model.util.Decision;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents decision nodes.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class DecisionNode extends ControlNode {

  /**a singleton {@link Decision} of super.#refCls*/
  private static Decision decision;

  /**
   * @effects 
   *  initialise this from arguments.
   *  
   *  <p>Throws {@link ConstraintViolationException} if an error occured.
   */
  public DecisionNode(String label, Class refCls, Class serviceCls) throws ConstraintViolationException {
    super(label, refCls, serviceCls);
    
    if (!Decision.class.isAssignableFrom(refCls)) {
      throw new ConstraintViolationException(ConstraintViolationException.Code.INVALID_DATA_TYPE, new Object[] {refCls.getSimpleName(), Decision.class.getSimpleName()});
    }
  }

  /**
   * @effects 
   *  invoke super.{@link #exec(Node, ModuleService, Object...)} /\ 
   *    [e ∈ out] (e.isSat() → e.exec())
   */
  @Override
  public void exec(Node src, ModuleService actMService, Object... args)
      throws NotPossibleException {
    setStopped(false);

    //(1) 
    validate();
    
    // (2)
    execReceive(src, actMService, args);
    
    // (3) & (4): execSelf, execOffer
    
    // uncomment this if DecisionNode has a view
    //  activateRefModuleService(actMService);

    // execSelf: requests refCls to evaluate the decision
    Decision decision = getDecisionInstance(getDecisionClass());
    
    Edge offeredEdge = decision.evaluate(this, args);
    /* alternatively: use this when Edge.guard is used instead
    for (Edge e : out) {
      if (e.isSat(args)) {
        e.exec(actMService, args);  
        break;  // at most one guard is checked to be true
      }
    }
    */

    setStopped(true);

    // execOffer
    if (offeredEdge != null) {
      offeredEdge.exec(actMService, args);
    }
  }

  /**
   * @effects 
   *  return super.{@link #getRefCls()} casted to <tt>Class&lt;Decision&gt;</tt>
   */
  public Class<Decision> getDecisionClass() {
    return (Class<Decision>) getRefCls();
  }

  /**
   * @effects <pre>
   *  if {@link #decision} has not been initialised
   *    initialise it
   *    throws {@link NotPossibleException} if failed
   *  
   *  return {@link #decision}
   *  </pre>
   */
  private static Decision getDecisionInstance(final Class<Decision> decisionCls) throws NotPossibleException {
    if (decision == null) {
      try {
        decision = decisionCls.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, new Object[] {decisionCls.getSimpleName(), ""});
      }
    }
    
    return decision;
  }
}
