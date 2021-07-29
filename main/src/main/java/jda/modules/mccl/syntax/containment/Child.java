package jda.modules.mccl.syntax.containment;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;


/**
 * @overview
 *  A containment edge of the {@link CTree}
 * @author dmle
 * @version 
 * - 5.1c: make deprecated
 * 
 * @deprecated by {@link CEdge}
 */
@Documented
@Deprecated
public @interface Child {

  /**
   * The domain class of the child module of this containment edge 
   */
  Class cname();

  /**
   * of this contaiment edge
   */
  String[] scope();

  /**
   * Extended scope definition which covers additional properties. 
   * 
   * <p>It <b>must</b> be set to a <tt>String</tt> that has this format:
   * <b><tt>.X</tt></b>,
   * where <tt>X = </tt> <b>name of a ScopeDef-typed constant object defined in the domain class of the module</b>. 
   * 
   * <p>For example, given the module <tt>ModuleStudent</tt> whose domain class is <tt>Student</tt>, 
   * then each value of this property must be set to <tt>.X</tt> where <tt>X</tt> is name of a ScopeDef constant object defined in the class <tt>Student</tt>.  
   * 
   * <p>Default: {@link CommonConstants#NullString}
   *  
   * @version 3.2
   * @deprecated uses {@link #scopeDesc()} instead.
   */
  @Deprecated
  String scopeDef() default CommonConstants.NullString;

  /**
   * A custom configuration for a containment scope. This is more complete and easier to use
   * than {@link #scopeDef()}.  
   * 
   * <p>This property is used with {@link #scope()} as follows: the custom configuration 
   * refers to attributes whose names are included in {@link #scope()}.
   * 
   * <p>Default: {@link ScopeDesc}(). 
   * 
   * @version 5.1 
   */
  ScopeDesc scopeDesc() default @ScopeDesc();
}
