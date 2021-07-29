package jda.modules.mccl.syntax;

import jda.modules.common.CommonConstants;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.syntax.containment.ScopeDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.mosa.controller.ControllerBasic;

/**
 * @overview 
 *  Defines shared, common operations concerning meta-attributes. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.1
 */
public class ModuleToolkit {
  private ModuleToolkit() {} 
  
  /**
   * @effects 
   *  if <tt>scopeDesc</tt> is the default {@link ScopeDesc} object (i.e. values of all properties
   *    are the default values)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public static boolean isDefaultScopeDef(ScopeDesc scopeDesc) {
    ModelDesc modelDesc = scopeDesc.modelDesc();
    ControllerDesc ctlDesc = scopeDesc.controllerDesc();
    AttributeDesc[] attribDescs = scopeDesc.attribDescs();
    
    return (scopeDesc.editable() == true && 
        scopeDesc.displayType() == CommonConstants.NullType &&
        modelDesc.model() == CommonConstants.NullType &&
          modelDesc.editable() == true && 
          modelDesc.indexable() == false && 
          modelDesc.dataSourceType() == CommonConstants.NullType && 
          modelDesc.props().length == 0 && 
        ctlDesc.isDataFieldStateListener() == false && 
          ctlDesc.isStateListener() == false && 
          ctlDesc.controller() == ControllerBasic.class && 
          ctlDesc.dataController() == CommonConstants.NullType &&
          ctlDesc.defaultCommand() == LAName.Null && 
          ctlDesc.objectBrowser() == CommonConstants.NullType && 
          ctlDesc.openPolicy() == OpenPolicy.I && 
          ctlDesc.props().length == 0 && 
          ctlDesc.startAfter() == 0 && 
          ctlDesc.runTime() == -1 &&
        attribDescs.length == 0
        );
  }

  /**
   * @effects 
   *  if cls is the default display type specified in {@link MCCLConstants#DEFAULT_DISPLAY_CLASS}
   *    return true
   *  else
   *    return false
   * @version 5.2b
   */
  public static boolean isDefaultDisplayType(Class cls) {
    return (cls != null && cls.equals(MCCLConstants.DEFAULT_DISPLAY_CLASS));
  }

}
