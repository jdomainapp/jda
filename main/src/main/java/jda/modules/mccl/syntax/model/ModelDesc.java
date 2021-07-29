package jda.modules.mccl.syntax.model;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyDesc;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
/**
 * @overview
 *  Represents the model-specific configuration of a module
 *  
 * @author dmle
 */
@Documented
public @interface ModelDesc {
  /** 
   * The model class
   * <p>Default: {@link CommonConstants#NullType}
   **/
  public Class model() default Null.class;
  
  /**
   * this overrides the mutable() value fields of the <tt>DomainConstraint</tt> 
   * of the domain attribute thats of the domain class associated to this module.
   * <tt>true</tt> if the associated view components are editable on the GUI that uses this view 
   * </tt>false</tt> if otherwise.
   * <br>Default: <tt>true</tt>
   */
  public boolean editable() default true;
  
  /**
   * indicates whether or not the objects of the domain class specified by {@link #model()}
   * are indexable by the application. Certain applications (e.g. reporting) need to use this feature 
   * to present the objects to the user.
   * 
   * <p>Note: for this property to be effective {@link #model()} must be specified to something other 
   * than its default value. 
   * 
   * <p>Default: <tt>false</tt>
   * @version 2.7.2
   */
  public boolean indexable() default false;
  
  /**
   * The <tt>Class</tt> object that represents the type of data source bounded to the domain objects of 
   * the domain class (specified by {@link #model()}).
   * 
   * <p>Default: {@link CommonConstants#NullType}
   */
  public Class dataSourceType() default Null.class;

  /**
   * The additional properties that are associated with this
   * 
   * <br>Default: <tt>{}</tt>
   * 
   *  @version 3.0
   */
  public PropertyDesc[] props() default {};
}
