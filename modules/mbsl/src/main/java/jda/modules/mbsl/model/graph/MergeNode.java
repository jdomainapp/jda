package jda.modules.mbsl.model.graph;

import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.module.ModuleService;

/**
 * @overview 
 *  Represents merge nodes.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class MergeNode extends ControlNode {

  /**
   * @effects 
   *
   * @version 
   */
  public MergeNode(String label, Class refCls, Class serviceCls) {
    super(label, refCls, serviceCls);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see domainapp.modules.activity.model.graph.Node#exec(domainapp.basics.core.ControllerBasic, java.lang.Object[])
   */
  /**
   * @effects 
   *  invoke super.{@link #exec(Node, ModuleService, Object...)} /\ 
   *  (> out[0].exec())
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
    
    activateRefModuleService(actMService);
    
    setStopped(true);

    // execOffer: do out[0].exec()
    getOut().get(0).exec(actMService, args);
  }  
}
