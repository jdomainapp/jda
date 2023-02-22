package jda.mosa.module;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.view.View;

/**
 * @overview 
 *  Represents an MVC module objects of an application. The configuration of each {@link Module} is recorded in
 *  an {@link ApplicationModule} object, and is realised in terms of three objects: model, view, 
 *  and controller. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public interface Module {

  /**
   * @effects 
   *  return the {@link ModuleService} that acts as the data service of the descendant {@link Module} 
   *  , which is contained in the containment hierarchy of this module; 
   *  or return <tt>null</tt> if such descendant module is not found
   *  
   *  <p>(Note: the containment hierarchy is configured in the {@link ApplicationModule} object of this module) 
   */
  ModuleService getDescendantDataService(Class refCls);

  /**
   * @effects 
   *  return the {@link ModuleService} that acts as the data service of the <b>child</b> {@link Module} 
   *  , of <tt>parent</tt> module service in the containment hierarchy of this module; 
   *  or return <tt>null</tt> if such child module is not found
   *  
   *  <p>(Note: the containment hierarchy is configured in the {@link ApplicationModule} object of this module)
   * @version 5.6 
   */
  ModuleService getChildDataService(ModuleService parent, Class refCls) throws NotPossibleException ;

  /**
   * @effects 
   *  return the {@link ModuleService} that is used as the default service interface for this module
   */
  ModuleService getDefaultService();

  /**
   * @effects 
   *  return the Controller of this
   */
  ControllerBasic getController();

  /**
   * @effects 
   *  returns the {@link View} of this 
   *  
   * @version 5.2
   */
  View getView();
  
  /**
   * @effects 
   *  if this has a model (i.e. its domain class) 
   *    return it
   *  else
   *    return null
   */
  Class getModel();
}
