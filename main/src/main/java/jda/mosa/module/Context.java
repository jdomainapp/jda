package jda.mosa.module;

import jda.modules.mccl.conceptmodel.module.ApplicationModule;


/**
 * @overview 
 *  Represents a context around an application's module. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 3.4c 
 */
public interface Context {

  /**
   * @effects 
   *  return the primary {@link ModuleService} of the module whose domain class is <tt>domainCls</tt>, 
   *  or return <tt>null</tt> if no such service is found.
   */
  public ModuleService lookUpPrimaryService(Class domainCls);

  /**
   * @effects if module whose configuration is <tt>mcfg</tt> has been created in this return <tt>true</tt>
   *          else return <tt>false</tt>
   */
  public boolean lookUpModule(ApplicationModule mcfg);

}
