package jda.modules.security.def;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.syntax.ModuleDescriptor;

/**
 * @overview
 *  Represents a permission definition for a module descriptor class 
 *  
 * @author dmle
 */
public class ModuleClassPerm {

  private static final Class MD = ModuleDescriptor.class;
  
  private Class moduleDescrClass;
  private ModuleDescriptor moduleDescriptor;
  private PermType type;
  private DomainClassPerm domainClsPerm;

  public ModuleClassPerm(Class moduleDescrClass, PermType type) throws NotPossibleException {
    this.moduleDescrClass = moduleDescrClass;
    this.type = type;
    
    // extract a domain class permission from this
    initDomainClassPerm(moduleDescrClass, type);
  }

  private void initDomainClassPerm(Class moduleDescrClass, PermType type) throws NotPossibleException {
    moduleDescriptor = (ModuleDescriptor) moduleDescrClass.getAnnotation(MD);
    
    if (moduleDescriptor == null) {
      throw new NotPossibleException(NotPossibleException.Code.MODULE_DESCRIPTOR_NOT_DEFINED, 
          new Object[] {moduleDescrClass.getSimpleName()});
    }
    
    Class domainCls = moduleDescriptor.modelDesc().model();
    if (domainCls == CommonConstants.NullType) {
      throw new NotPossibleException(NotPossibleException.Code.MODULE_NOT_WELL_FORMED, 
          new Object[] {moduleDescrClass.getSimpleName(), "domain class not specified"});      
    }
    
    this.domainClsPerm = new DomainClassPerm(domainCls, type);
  }

  public Class getModuleDescrClass() {
    return moduleDescrClass;
  }

  public PermType getType() {
    return type;
  }

  public DomainClassPerm getDomainClsPerm() {
    return domainClsPerm;
  }

  public String getModuleName() {
    return moduleDescriptor.name();
  }

  public ModuleDescriptor getModuleDescriptor() {
    return moduleDescriptor;
  }

  /**
   * @effects 
   *
   * @version 3.3
   */
  public void setType(PermType permType) {
    this.type = permType;
  }

}
