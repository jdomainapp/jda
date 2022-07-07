package jda.modules.mccl.util;

import com.github.javaparser.ast.PackageDeclaration;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mccl.syntax.ModuleDescriptor;

/**
 * @overview A toolkit class for everying related to {@link MCC}.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2c
 */
public class MCCTk {
  
  private static final Class<ModuleDescriptor> MD = ModuleDescriptor.class;

  private MCCTk() {}

//  /**
//   * @effects 
//   *  if c is a registered domain class AND is configured with a module-descriptor class m
//   *    return m
//   *  else
//   *    return null
//   */
//  public static Class getModuleDescriptor(Class c) {
//    DClass dm = (DClass) c.getAnnotation(CC);
//    if (dm != null) {
//      Class moduleDescrCls = dm.moduleDescriptor();
//      if (moduleDescrCls != MetaConstants.NullType) {
//        return moduleDescrCls;
//      }
//    }
//    
//    // not specified
//    return null;
//  }
  
  /**
   * @effects 
   *  if mcc is configured with a module-descriptor annotation dm
   *    return dm
   *  else
   *    return null
   */
  public static ModuleDescriptor getModuleDescriptorObject(Class mcc) {
    ModuleDescriptor dm = (ModuleDescriptor) mcc.getAnnotation(MD);
    
    return dm;
  }
  
  /**
   * @requires 
   *   <tt>mcc</tt> is a valid MCC (i.e. a {@link Class} attached with a valid {@link ModuleDescriptor})
   *    
   * @effects 
   *   return <tt>ModuleDesc(mcc).modelDesc.model</tt> or 
   *   throws NotPossibleException if <tt>mcc</tt> is not a valid MCC or is not configured with a domain class 
   * @version 5.1
   */
  public static Class getDomainClass(Class mcc) throws NotPossibleException {
    ModuleDescriptor md = getModuleDescriptorObject(mcc);
    
    Class c = null;
    if (md != null) {
      c = md.modelDesc().model();
      
      if (c.equals(CommonConstants.NullType)) c = null;
    } 
    
    if (c == null) {
      throw new NotPossibleException(NotPossibleException.Code.MODULE_NOT_WELL_FORMED, new Object[] {mcc.getSimpleName(), "domain class not found"}) ;
    } else {
      return c;
    }
  }

  /**
   * @effects 
   *  if <tt>c</tt> is a module descriptor class (i.e. annotated with {@link ModuleDescriptor})
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public static boolean isModuleDescrClass(Class c) {
    ModuleDescriptor moduleCfg = (ModuleDescriptor) c.getAnnotation(MD);
    
    return moduleCfg != null;
  }

  /**
   * @effects 
   *  return the FQN of the package, relative to <code>dclsPkg</code> of a domain class, which 
   *  is used to locate the MCC of the domain class.
   *  
   *  <code>result = dclsPkg.parent + pkgLastName</code>
   *  
   *  <p>If <code>pkgLastName = null</code> then <code>result = dclsPkg.parent</code>
   *  
   * @version 5.4.1
   */
  public static String getMCCPackage(String dclsPkg, String pkgLastName) {
    
    String parent = dclsPkg.substring(0, dclsPkg.lastIndexOf("."));
    String mccPkg;
    if (pkgLastName != null)
      mccPkg = parent+ "." + pkgLastName;
    else
      mccPkg = parent;
    
    return mccPkg;
  }
  
  public static String getMCCPackage(String dclsPkg) {
    
    String parent = dclsPkg.substring(0, dclsPkg.lastIndexOf("."));
    
    return parent;
  }
}
