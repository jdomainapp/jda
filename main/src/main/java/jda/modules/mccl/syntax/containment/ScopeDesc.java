package jda.modules.mccl.syntax.containment;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;

/**
 * @overview 
 *   Represents a custom configuration for a descendant module in a containment tree. 
 *   
 *   <p>It is used by {@link Child#scopeDesc()} to specify for a descendant module. 
 *   
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.1
 */
@Documented
public @interface ScopeDesc {

  /**
   * State scope of the associated {@link CEdge}. This is an array of attribute names of the child node of the associated
   * {@link CEdge}, that will be included in the subview. 
   * 
   * <br>Default: <tt>{}</tt> (empty array)
   * 
   * @version 5.1c
   */
  String[] stateScope() default {} ;
  
  /**
   * This is equivalent to {@link AttributeDesc#editable()}, which is set for the associative 
   * field of the parent node that is associated to the descendant node for which this {@link ScopeDesc} is defined.
   * 
   * <p>Default: <tt>true</tt>
   */
  boolean editable() default true;

  /**
   * This is equivalent to {@link AttributeDesc#type()}, which is set for the associative 
   * field of the parent node that is associated to the descendant node for which this {@link ScopeDesc} is defined.
   * 
   * <p>Default: {@link CommonConstants#NullType}
   */
  Class displayType() default Null.class;

  /**
   * This is equivalent to {@link AttributeDesc#modelDesc()}, which is set for the associative 
   * field of the parent node that is associated to the descendant node for which this {@link ScopeDesc} is defined.
   * 
   * <p>Default: {@link ModelDesc()}
   */
  ModelDesc modelDesc() default @ModelDesc();

// NOT NEEDED!
//  /**
//   * This is equivalent to {@link ModuleDesc#viewDesc()}, which is set for the the descendant module for which this {@link ScopeDesc} is defined.
//   * 
//   * <p>Default: {@link ViewDesc()}
//   */
//  ViewDesc viewDesc() default @ViewDesc();
  
  /**
   * This is equivalent to {@link AttributeDesc#controllerDesc()}, which is set for the associative 
   * field of the parent node that is associated to the descendant node for which this {@link ScopeDesc} is defined.
   * 
   * <p>Default: {@link ControllerDesc()}
   */
  ControllerDesc controllerDesc() default @ControllerDesc();  

  /**
   * Any additional properties that are associated with this (now or in the future).
   * 
   * <br>Default: <tt>{}</tt>
   * 
   */
  PropertyDesc[] props() default {};  
  
  /**
   * This is equivalent to {@link AttributeDesc}[] of the subview of the descendant node 
   * for which this {@link ScopeDesc} is defined. Any {@link AttributeDesc}s 
   * that are defined here override those of the corresponding view fields 
   * defined by the descendant module's configuration.
   * 
   * <p>Thus, all {@link AttributeDesc}s in this must have their ids found in {@link #stateScope()}.
   * 
   * <p>Default: <tt>{}</tt> (empty array)
   */
  AttributeDesc[] attribDescs() default {};
}
