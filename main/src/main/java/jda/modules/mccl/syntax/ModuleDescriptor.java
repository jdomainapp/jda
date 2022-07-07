package jda.modules.mccl.syntax;

//import javax.lang.model.type.NullType;

import java.lang.annotation.Documented;

import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.ViewDesc;

/**
 * A <b>class</b>  annotation that is used to specify information about a program module
 * . These are the parameters that will be used to create
 * the GUI <tt>Region</tt> object for the functional GUI of the class.
 *  
 * @author dmle
 */
@java.lang.annotation.Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target(value={java.lang.annotation.ElementType.TYPE})
//@java.lang.annotation.Inherited
@Documented
public @interface ModuleDescriptor {
  public String name();
  
  /*v2.7 moved to ModelDesc 
  public Class model() default Null.class;
  **
   * this overrides the mutable() value fields of the <tt>DomainConstraint</tt> 
   * of the domain attribute thats of the domain class associated to this module.
   * <tt>true</tt> if the associated view components are editable on the GUI that uses this view 
   * </tt>false</tt> if otherwise.
   * <br>Default: <tt>true</tt>
   *
  public boolean editable() default true;
  */
  /**
   * Model-specific configuration for this module.
   * See {@link ModelDesc} for more details.
   */
  public ModelDesc modelDesc() default @ModelDesc();
  
  /*v2.7: moved to ViewConfig
  public String label();
  
  public String imageIcon();
  
  public Class view() default Null.class; //NullType.class;

  public Type viewType() default Type.Null;
  
  public RegionName parent() default RegionName.Null;
  
  public RegionName[] children() default {}; 
  
  public RegionName[] excludeComponents() default {}; 

  public StyleName style() default StyleName.Null;
  */

  /**
   * View-specific configuration for this module. 
   * See {@link ViewDesc} for more details.
   */
  public ViewDesc viewDesc() default @ViewDesc();
  
  /*v2.6.4.b: moved to ControllerConfig
  public Class controller() default Controller.class;
  public Class dataController() default Controller.DataPanelController.class; 
  ** the default command to run when the controller of this module is started 
   * Default: {@link LogicalAction.LAName#Null} 
  **
   * true if the controller associated to this module is a state listener, 
   * false if otherwise.
   * <br>Default: false
  public boolean isStateListener() default false;
   **
  public LogicalAction.LAName defaultCommand() default LAName.Null;
  */
  /**
   * Controller-specific configuration for this module.
   * See {@link ControllerDesc} for more details.
   */
  public ControllerDesc controllerDesc() default @ControllerDesc();
  
  /**
   * The type of module (user, system, report, etc.). This is used to 
   * manage the modules in the control panel.
   * 
   * @version v2.7
   */
  public ModuleType type() default ModuleType.DomainData;
  
  /**
   * Whether or not the module described by this descriptor is 
   * provides a GUI that gives details about the objects of the 
   * specified model class.
   * <br>Default: <tt>true</tt> 
   **/
  public boolean isViewer() default true;

  /**
   * Whether or not the module described by this descriptor is 
   * the primary module for its model class 
   * <br>Default: <tt>false</tt> 
   **/
  public boolean isPrimary() default false;

  /**
   * Applies to composite module ONLY.
   * <p>Lists the {@link ModuleDescriptor}s of the child modules (excluding the current module if it 
   * includes itself as a child)
   * <p>Default: emtpy array
   */
  public Class[] childModules() default {};

  /**
   * Specify the commannds that can customise set up of this module.  
   * 
   * <br>Default: <tt>{@link SetUpDesc}()</tt>
   */
  public SetUpDesc setUpDesc() default @SetUpDesc();

  /**
   * Whether or not this module is to be created only in memory.
   *  
   * <br> Default: <tt>false</tt> (i.e. all modules are created in the data source)
   * @version 2.8
   */
  boolean isMemoryBased() default false;

  /**
   * Specify the containment tree of composite modules whose <b>module scope 
   * does not cover all domain attributes</b>. 
   * 
   * <p>Note, for performance reasons do not use this property for composite modules
   * whose module scope cover all domain attributes. 
   * 
   * <br>Default: {@link CTree}()
   * @version 3.0
   */
  public CTree containmentTree() default @CTree();
  
  /**
   * The additional properties that are associated with this.
   * 
   * <br>Default: <tt>{}</tt>
   * 
   *  @version 5.2
   */
  PropertyDesc[] props() default {};

  /**
   * @author Linh Quang tran
   * Use for declare sub-modules that extended this
   * @return
   */
  Class<?>[] subtypes() default {};

//  /**
//   * List of service-typed MCCs whose services are used by this module.<br> 
//   *  
//   * Default: <tt>{}</tt> (i.e. no services)
//   * @version 5.2
//   */
//  public Class[] useServices() default {};
}
