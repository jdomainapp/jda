package jda.modules.dcsl.syntax;

import java.lang.annotation.Documented;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.dcsl.syntax.DCSLConstants;


/**
 * A <b>class</b> annotation that is used to annotate a domain class.
 * @author dmle
 *
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
//@java.lang.annotation.Inherited
@Documented
public @interface DClass {
  
  //public static final String DEFAULT_SCHEMA = "APP";
  
  //public static final String Null = "";
  
  //public static final Class[] EMPTY_CLASS_ARRAY = {}; 
  
  /**
   * the name of the domain schema to which this class belongs. The name 
   * must have this format: <tt>name1_name2_..._namek</tt>, where the <tt>name1</tt>
   * is typically equal to {@link DCSLConstants#DEFAULT_SCHEMA}. For example: <tt>app_config</tt>. 
   * 
   * <p>This name is used to determine the database schema where objects of this class
   * will be stored.  
   * 
   * Default: {@link DCSLConstants#DEFAULT_SCHEMA}
   */
  public String schema() default DCSLConstants.DEFAULT_SCHEMA;
  
  /** whether or not the objects of this class are serialisable (e.g. to the database) <br>
   * Default: true 
   * */
  public boolean serialisable() default true;
  
  /** whether or not the objects of this class are mutable <br>
   * Default: true*/
  public boolean mutable() default true;

  /**
   * true if the associated class has a single object, false if otherwise.
   * This property has effect on how the life cycle of the singleton. 
   * Most importantly, the singleton object is not unloaded from memory when the application is 
   * temporarily closed (e.g. by the user logging out).
   * <br>Default: false 
   */
  public boolean singleton() default false;

  /**
   * A meta-attribute used to mark a domain class as a wrapper class of another (base) class.
   * 
   * Default: {@link CommonConstants#NullType} (i.e. not a wrapper class) 
   */
  public Class wrapperOf() default Null.class;

  /**
   * (Optional) The class that specifies the application module whose model is this domain class
   * 
   * <p>Default: {@link CommonConstants#NullType} (i.e. not specified)
   */
  Class moduleDescriptor() default Null.class;

  /**
   * Whether or not a domain class is language-aware, i.e. it has sub-types defined for the languages supported
   * by the application. 
   * 
   * <p>Default: <tt>false</tt>
   */
  boolean languageAware() default false;

  /**
   * @effects 
   *  Whether or not this class's object pool is constant (i.e. its objects are not to be changed by the user, 
   *  and thus would not be removed from memory between user sessions) 
   *  
   *  <br>Default: <tt>false</tt>
   * @version 3.3
   */
  boolean objectPoolIsConstant() default false;

//  /**
//   * An array of classes on which this class depend.<br> 
//   * Class A depends on class B,C,... if every object a of A is valid only 
//   * with regards to some objects b of B, c of C, ...; more specifically a
//   * can only be created from b,c,... and a is deleted if one of b,c,... is deleted.
//   * 
//   * <br>In practice, every <b>associative class</b> (or <i>associative entity</i> 
//   * in the ER modelling language terminology) depends on the classes to which it associates.   
//   * For example, Enrolment depends on Student and Module; while  
//   * OrderLine depends on Order and Product.
//   * 
//   * <br> Default: <tt>{@link #EMPTY_CLASS_ARRAY}</tt> 
//   * 
//   * @deprecated this property is obsolete, do not use it
//   */
//  public Class[] dependsOn() default {};
}
