package jda.modules.mccl.syntax.controller;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.controller.ControllerBasic;

/**
 * @overview
 *  Represents the controller-specific configuration that is associated to an attribute or to a module
 *  
 * @author dmle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD, TYPE})
@Documented
public @interface ControllerDesc {
  /**
   * Whether or not controller descriptor is being used
   * 
   * <b>Default: <tt>true</tt>
   */
  boolean on() default true;
  
  /**
   * the controller type
   * <p>Default: {@link ControllerBasic}.class
   */
  Class controller() default ControllerBasic.class;
  
  /**
   * the data controller type
   * <p>Default: {@link CommonConstants#NullType}.class
   */
  Class dataController() default Null.class; //v3.0: DataPanelController.class; 

  /**
   * the object browser type used for browsing the domain objects of this.
   * 
   * <p>Default: {@link CommonConstants#NullType} (i.e. use the default browser type)
   */
  Class objectBrowser() default Null.class;
  
  /** the default command to run when the controller of this module is started 
   * Default: {@link LAName#Null} 
   **/
 LAName defaultCommand() default LAName.Null;

 /**
  * true if the controller associated to this module is a listener for <b>application state</b> events 
  * false if otherwise.
  * <br>Default: false
  */
 boolean isStateListener() default false;

 /**
  * true if the controller associated to this module is a listener for <b>data field state</b> events, 
  * false if otherwise.
  * <br>Default: false
  * @version 2.7.2
  */
 boolean isDataFieldStateListener() default false;
 
  /**
   * the policy ({@link OpenPolicy}) for the <tt>open</tt> operation that is performed by the controller
   * <b>Default: {@link OpenPolicy#I}
   */
  OpenPolicy openPolicy() default OpenPolicy.I;
  
  /**
   * @overview
   *  open policy constants that are used by {@link ControllerDesc#openPolicy()}
   *
   */
  //Serialisable
  public static enum OpenPolicy {
    /**Open <b><u>P</u></b>arent
     */
    P(1),
    /**Open <b><u>C</u></b>hildren
     **/
    C(2),
    /**
     * Open <b><u>A</u></b>utomatically (i.e. when first run)
     **/
    A(4),
    /**
     * Load O<b><u>I</u></b>ds only
     */
    I(8),
    /**Load <b><u>O</u></b>bjects <b>on demand</b> (i.e. when retrieved via an Oid)
     **/
    O(16),
    /**Load Al<b><u>L</u></b>bjects (as opposed on loading them on-demand as with {@link #O})
     **/
    L(32),
//    /**{@link #P} & {@link #I}: open parent with Oids, donot open children*/
//    P_I(P,I),
//    /**{@link #P} & {@link #O}: open parent with objects, donot open children*/    
//    P_O(P,O),
    /**{@link #I} & {@link #C}: open only Oids and open children*/    
    I_C(I,C),
    /**{@link #I} & {@link #A}: open Oids only and automatically*/    
    I_A(I, A), 
    /**{@link #O} & {@link #C}: open objects on demand and open children*/    
    O_C(O,C),
    /**{@link #L} & {@link #C}: open with all objects and open children*/    
    L_C(L,C),
    /**{@link #L} & {@link #A}: open with all objects and open automatically*/    
    L_A(L,A),
    /**{@link #O} & {@link #A}: open automatically and with objects*/    
    O_A(O,A),
    ;
    
    private int code;
    
    private OpenPolicy(int code) {
      this.code = code;
    }
    
    private OpenPolicy(OpenPolicy...policies) {
      this.code = policies[0].code;
      for (int i = 1; i < policies.length; i++) {
        this.code ^= policies[i].code;
      }
    }
    
    @DAttr(name="name",id = true,type=Type.String,length=10,mutable=false)
    public String getName() {
      return name();
    }
    
    public int getCode() {
      return code;
    }
    
    /**
     * @effects 
     *  if this = pol
     *    return true
     *  else
     *    return false
     */
    public boolean equals(OpenPolicy pol) {
      return this == pol;
    }
    
    /**
     * @effects 
     *  if this contains pol
     *    return true
     *  else
     *    return false
     */    
    public boolean contains(OpenPolicy pol) {
      return (this.code & pol.code) == pol.code;
    }
    
    public boolean isWithObject() {
      return contains(OpenPolicy.O);
    }

    public boolean isWithAllObjects() {
      return contains(OpenPolicy.L);
    }
    
    public boolean isWithChildren() {
      return contains(OpenPolicy.C);
    }

    public boolean isWithAutomatic() {
      return contains(OpenPolicy.A);
    }

    public boolean isWithObjectIdOnly() {
      return contains(OpenPolicy.I);
    }

    /**
     * @effects 
     *  return the OpenPolicy whose code is <tt>codeVal</tt> or <tt>null</tt> if no such policy exists
     */
    public static OpenPolicy valueOf(int codeVal) {
      OpenPolicy[] policies = OpenPolicy.values();
      for (OpenPolicy pol : policies) {
        if (pol.code == codeVal)
          return pol;
      }
      
      return null;
    }
  } // end OpenPolicy

  /**
   * the number of <b>milli-seconds</b> to wait before the controller is run
   * <br>Default: 0 (i.e. run immediately without delay)
   */
  long startAfter() default 0;

  /**
   * the number of <b>milli-seconds</b> to wait before the controller is stopped
   * <br>Default: -1 (i.e. run until application is terminated)
   */
  long runTime() default -1;

  /**
   * The additional properties that are associated with this
   * 
   * <br>Default: <tt>{}</tt>
   * 
   *  @version 2.7.4
   */
  PropertyDesc[] props() default {};
}
