package jda.modules.mccl.conceptmodel.module.containment;

import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.Child;
import jda.modules.mccl.syntax.view.AttributeDesc;

/**
 * @overview
 *  Extended scope definition for a {@link Child} in the {@link CTree} of a composite module.
 *  
 * @author dmle
 * 
 * @version 
 * - 3.2: created<br>
 * - 5.1: becomes wrapper for {@link RegionLinking} and is moved package to ...model.config.module
 */
public class ScopeDef {

  /**
   * mapped to {@link Child#cname()}
   */
  private Class cname;
  
  /**
   * mapped to {@link Child#scope()}
   */
  private String[] scope;

  /**mapped to {@link AttributeDesc#editable()}*/
  private Boolean modelEditable;

//  /**mapped to {@link AttributeDesc#controllerDesc()}*/
//  private ControllerDesc controllerDesc;

  /**mapped to {@link AttributeDesc#type()*/
  private Class displayClass;

  /** the configuration for this containment scope */
  private RegionLinking containCfg;

  /**
   * @effects 
   *  initialises this with <tt>cname, scope</tt>
   */
  public ScopeDef(Class cname, String[] scope) {
    this(cname, scope, null, null);
  }

  /**
   * @effects 
   *  extends {@link #ScopeDef(Class, String[])} with <tt>modelEditable</tt>.
   */
  public ScopeDef(Class cname, String[] scope, Boolean modelEditable) {
    //this(cname, scope, modelEditable, null);
    this(cname, scope, modelEditable, null);
  }
  
  /**
   * @effects 
   *  extends {@link #ScopeDef(Class, String[], Boolean, Class)} with <tt>displayClass</tt>.
   */
  public ScopeDef(Class cname, String[] scope, Boolean modelEditable, Class displayClass) {
    this.cname = cname;
    this.scope = scope;
    this.modelEditable = modelEditable;
    this.displayClass = displayClass;
  }

  /**
   * @effects 
   *  extends {@link #ScopeDef(Class, String[])} with <tt>containCfg</tt>.
   * @version 5.1
   */
  public ScopeDef(Class cname, String[] scope, RegionLinking containCfg) {
    this.cname = cname;
    this.scope = scope;
    this.containCfg = containCfg;
  }
  
  /**
   * @effects 
   *  return {@link Child#cname()}
   */
  public final Class cname() {
    return cname;
  }

  /**@Documented

   * @effects 
   *  return {@link Child#scope()}
   */
  public final String[] scope() {
    return scope;
  }

  /**
   * @effects <pre> 
   *  if no editability setting is specified
   *    return <tt>null</tt>
   *  else 
   *    if child module specified by {@link #cname()} is editable in this containment 
   *    (i.e. its objects are editable by the user via the containment)
   *      return <tt>Boolean.TRUE</tt>
   *    else
   *      return <tt>Boolean.FALSE</tt>
   * </pre>
   * 
   * @note the default is <tt>null</tt>
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public Boolean isEditable() {
    if (containCfg != null) {
      return containCfg.getEditable();
    } else {
      return modelEditable;
    }
  }

  /**
   * @effects <pre> 
   *  if display class is specified for {@link #cname()}
   *    return it 
   *  else 
   *    return <tt>null</tt>
   * </pre>
   * 
   * @note the default is <tt>null</tt>
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public Class getDisplayClass() {
    if (containCfg != null) {
      return containCfg.getDisplayClassType();
    } else {
      return displayClass;
    }
  }
  
  /**
   * This is equivalent to {@link AttributeDesc#editable()()}
   * 
   * @requires 
   *  attribName is in {@link #scope()}
   *  
   * @effects 
   *  return the <tt>editable</tt> setting for <tt>attribName</tt>, or <tt>null</tt> 
   *  if no such setting is specified
   *  
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public Boolean isEditable(final String attribName) {
    if (containCfg != null) {
      if (containCfg.hasChildren()) {
        Region attribReg = containCfg.getChildRegion(attribName);
        if (attribReg != null)
          return attribReg.getEditable();
        else
          return null;      
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  /**
   * This is equivalent to {@link AttributeDesc#type()}
   * 
   * @requires 
   *  attribName is in {@link #scope()}
   *  
   * @effects 
   *  return the display class setting for <tt>attribName</tt>, or <tt>null</tt> 
   *  if no such setting is specified
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public Class getDisplayClass(final String attribName) {
    if (containCfg != null) {
      if (containCfg.hasChildren()) {
        //return containCfg.getChildRegion(attribName).getDisplayClassType();
        Region attribReg = containCfg.getChildRegion(attribName);
        if (attribReg != null)
          return attribReg.getDisplayClassType();
        else
          return null;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

//  /**
//   * This is equivalent to {@link AttributeDesc#ref()}
//   * 
//   * @requires 
//   *  attribName is in {@link #scope()}
//   *  
//   * @effects 
//   *  return the <tt>ref</tt> setting for <b>bounded</b> attribute <tt>attribName</tt>, or <tt>null</tt> 
//   *  if no such setting is specified
//   */
//  public Select getRef(final String attribName) {
////    if (containCfg != null) {
////      if (containCfg.hasChildren()) {
////        return ((RegionDataField) containCfg.getChildRegion(attribName)).getRef();
////      }
////    } else {
////      return null;
////    }
//    return null;
//  }
  
  /**
   * This is equivalent to {@link AttributeDesc#width()}
   * 
   * @requires 
   *  attribName is in {@link #scope()}
   *  
   * @effects 
   *  return the <tt>width</tt> setting for <b>bounded</b> attribute <tt>attribName</tt>, or <tt>null</tt> 
   *  if no such setting is specified
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public Integer getWidth(final String attribName) {
    if (containCfg != null) {
      if (containCfg.hasChildren()) {
        //return containCfg.getChildRegion(attribName).getWidth();
        Region attribReg = containCfg.getChildRegion(attribName);
        if (attribReg != null)
          return attribReg.getWidth();
        else
          return null;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * This is equivalent to {@link AttributeDesc#height()}
   * 
   * @requires 
   *  attribName is in {@link #scope()}
   *  
   * @effects 
   *  return the <tt>height</tt> setting for <b>bounded</b> attribute <tt>attribName</tt>, or <tt>null</tt> 
   *  if no such setting is specified
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public Integer getHeight(final String attribName) {
    if (containCfg != null) {
      if (containCfg.hasChildren()) {
        Region attribReg = containCfg.getChildRegion(attribName);
        if (attribReg != null)
          return attribReg.getHeight();
        else
          return null;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  return {@link ControllerConfig} (if specified) for {@link #cname} in {@link #containCfg}; 
   *  or return <tt>null</tt> if not specified 
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public ControllerConfig getControllerConfig() {
    if (containCfg != null) {
      return containCfg.getControllerCfg();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  return {@link ModelConfig} (if specified) for {@link #cname} in {@link #containCfg}; 
   *  or return <tt>null</tt> if not specified 
   * @version 
   * - 5.1: support {@link #containCfg} 
   */
  public ModelConfig getModelConfig() {
    if (containCfg != null) {
      return containCfg.getModelCfg();
    } else {
      return null;
    }
  }
}
