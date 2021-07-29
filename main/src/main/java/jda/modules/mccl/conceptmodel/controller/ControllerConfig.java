package jda.modules.mccl.conceptmodel.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.util.properties.PropertySet;

@DClass(schema="app_config")
public class ControllerConfig {
  
  @DAttr(name="id",type=Type.Long,id=true,mutable=false,optional=false)
  private long id;
  
  @DAttr(name="controller",type=Type.String,length=200,optional=false)
  private String controller;

  @DAttr(name="dataController",type=Type.String,length=200,optional=false)
  private String dataController;

  @DAttr(name="objectBrowser",type=Type.String,length=200)
  private String objectBrowser;

  // derived attribute
  private Class objectBrowserCls;
  
  @DAttr(name="openPolicy",type=Type.Domain,optional=false)
  private OpenPolicy openPolicy;

  @DAttr(name="defaultCommand",type=Type.Domain,optional=false)
  private LAName defaultCommand;
  
  @DAttr(name="isStateListener",type=DAttr.Type.Boolean)  
  private boolean isStateListener;

  @DAttr(name="isDataFieldStateListener",type=DAttr.Type.Boolean)  
  private boolean isDataFieldStateListener; // v2.7.2
  
  // v2.7.4
  @DAttr(name="startAfter",type=DAttr.Type.Long,optional=false)
  private long startAfter;

  @DAttr(name="runTime",type=DAttr.Type.Long,optional=false)
  private long runTime;

  @DAttr(name="applicationModule",type=Type.Domain,serialisable=false)
  @DAssoc(ascName="module-has-controllerConfig",role="controllerConfig",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ApplicationModule.class,cardMin=1,cardMax=1,determinant=true))
  private ApplicationModule applicationModule;

  @DAttr(name="linkedRegion",type=Type.Domain,serialisable=false)
  @DAssoc(ascName="regionLinking-has-controllerConfig",role="controllerConfig",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=RegionLinking.class,cardMin=1,cardMax=1,determinant=true))
  private RegionLinking linkedRegion;

  // v5.1
//  @DAttr(name="containRegion",type=Type.Domain,serialisable=false)
//  @DAssoc(ascName="regionContainment-has-controllerConfig",role="controllerConfig",
//      ascType=AssocType.One2One,endType=AssocEndType.One,
//      associate=@Associate(type=RegionContainment.class,cardMin=1,cardMax=1,determinant=true))
//  private RegionContainment containRegion;
  
  // derived attributes to retrieve the controller and domain class objects
  @DAttr(name="controllerClass",auto=true,serialisable=false)
  private Class controllerCls;

  @DAttr(name="dataControllerClass",auto=true,serialisable=false)
  private Class dataControllerCls;

  // v2.7.4
  @DAttr(name="properties",type=DAttr.Type.Domain)
  @DAssoc(ascName="controllerCfg-has-properties",role="controllerCfg",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=PropertySet.class,cardMin=0,cardMax=1))  
  private PropertySet properties;

  // constructor used by SetUp to create objects
  public ControllerConfig(Class controller, 
      Class dataController,
      Class objectBrowser,  // v2.7.2
      OpenPolicy openPolicy,
      LAName defaultCommand, 
      Boolean isStateListener,
      Boolean isDataFieldStateListener  // v2.7.2
      , Long startAfter, Long runTime    // v2.7.4
      , PropertySet properties
      ) {
    this.id = nextId(null);
    this.controller = controller.getName();
    this.controllerCls=controller;
    this.dataControllerCls = dataController;
    if (dataController != null)
      this.dataController=dataController.getName();

    this.objectBrowserCls = objectBrowser;
    if (objectBrowserCls != null) {
      this.objectBrowser = objectBrowserCls.getName();
    }
    
    this.openPolicy = openPolicy;

    this.defaultCommand=defaultCommand;
    
    if (isStateListener != null)
      this.isStateListener=isStateListener;
    else
      this.isStateListener=false;
    
    if (isDataFieldStateListener != null)
      this.isDataFieldStateListener=isDataFieldStateListener;
    else
      this.isDataFieldStateListener=false;
    
    this.startAfter = startAfter;
    this.runTime = runTime;
    
    this.properties = properties;
  }

  // constructor used to create objects from data source
  public ControllerConfig(Long id, String controller, 
      String dataController,
      String objectBrowser, 
      OpenPolicy openPolicy,
      LAName defaultCommand,
      Boolean isStateListener,
      Boolean isDataFieldStateListener  // v2.7.2
      , Long startAfter, Long runTime    // v2.7.4
      , PropertySet properties
      ) throws NotFoundException {
    this.id = nextId(id);
    this.controller = controller;
    this.dataController=dataController;
    this.objectBrowser = objectBrowser;
    
    this.openPolicy = openPolicy;
    
    this.defaultCommand=defaultCommand;
    
    try {
      this.controllerCls=Class.forName(controller);
      if (dataController != null)
        this.dataControllerCls=Class.forName(dataController);
      
      if (objectBrowser != null) {
        this.objectBrowserCls=Class.forName(objectBrowser);
      }
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {controller});
    }
    
    if (isStateListener != null)
      this.isStateListener=isStateListener;
    else
      this.isStateListener=false; 

    
    if (isDataFieldStateListener != null)
      this.isDataFieldStateListener=isDataFieldStateListener;
    else
      this.isDataFieldStateListener=false;
    
    
    this.startAfter = startAfter;
    this.runTime = runTime;
    
    this.properties = properties;
  }
  
//  public ControllerConfig(String controller, String dataController, 
//      LogicalAction.LAName defaultCommand,
//      Boolean isStateListener 
//      ) throws NotFoundException {
//    this(null, controller, dataController, defaultCommand, isStateListener);
//  }
  
  private long nextId(Long currId) {
    // use current time as id
    if (currId == null)
      return System.nanoTime();
    else
      return currId;
  }
  
  /**
   * @effects
   *  creates and return an instance of this whose details are specified in <tt>controllerDesc</tt> 
   */
  public static ControllerConfig createInstance(ControllerDesc controllerDesc, 
      PropertySet properties  // v2.7.4
      ) {
    Class objectBrowser = controllerDesc.objectBrowser();
    
    if (objectBrowser == CommonConstants.NullType) objectBrowser = null;
    
    // v3.0: support null data controller
    Class dataController = controllerDesc.dataController();
    if (dataController == CommonConstants.NullType)
      dataController = null;
    
    return new ControllerConfig(controllerDesc.controller(), 
        dataController, 
        objectBrowser, // v2.7.2
        controllerDesc.openPolicy(),
        controllerDesc.defaultCommand(),
        controllerDesc.isStateListener(),
        controllerDesc.isDataFieldStateListener(), // v2.7.2
        controllerDesc.startAfter(),  // v2.7.4
        controllerDesc.runTime(),
        properties
        );
  }
  
  public long getId() {
    return id;
  }

  public void setApplicationModule(ApplicationModule applicationModule) {
    this.applicationModule = applicationModule;
  }

  public ApplicationModule getApplicationModule() {
    return applicationModule;
  }

  public RegionLinking getLinkedRegion() {
    return linkedRegion;
  }

  public void setLinkedRegion(RegionLinking linkedRegion) {
    this.linkedRegion = linkedRegion;
  }

//  /**
//   * @effects return containRegion
//   */
//  public RegionContainment getContainRegion() {
//    return containRegion;
//  }
//
//  /**
//   * @effects set containRegion = containRegion
//   */
//  public void setContainRegion(RegionContainment containRegion) {
//    this.containRegion = containRegion;
//  }

  public String getController() {
    return controller;
  }

  /**
   * @requires
   *  ctlName is the name of a valid controller class
   */
  public void setController(String controller) {
    this.controller = controller;
  }
  
  public Class getControllerCls() {
    return controllerCls;
  }

  public void setControllerCls(Class controller) {
    this.controllerCls = controller;
    this.controller=controller.getName();
  }
  
  public String getDataController() {
    return dataController;
  }

  public void setDataController(String dataController) {
    this.dataController = dataController;
  }

  public Class getDataControllerCls() {
    return dataControllerCls;
  }

  public void setDataControllerCls(Class dataControllerCls) {
    this.dataControllerCls = dataControllerCls;
    this.dataController=dataControllerCls.getName();
  }
  
  
  public String getObjectBrowser() {
    return objectBrowser;
  }

  public void setObjectBrowser(String objectBrowser) throws NotFoundException {
    this.objectBrowser = objectBrowser;
    if (objectBrowser != null) {
      try {
          this.objectBrowserCls=Class.forName(objectBrowser);
      } catch (ClassNotFoundException e) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, 
            "Không tìm thấy lớp");
      }
    }
  }

  public Class getObjectBrowserCls() {
    return objectBrowserCls;
  }

  public OpenPolicy getOpenPolicy() {
    return openPolicy;
  }

  public void setOpenPolicy(OpenPolicy openPolicy) {
    this.openPolicy = openPolicy;
  }

  public LAName getDefaultCommand() {
    return defaultCommand;
  }

  public void setDefaultCommand(LAName defaultCommand) {
    this.defaultCommand = defaultCommand;
  }

  public boolean getIsStateListener() {
    return isStateListener;
  }

  public void setIsStateListener(boolean isStateListener) {
    this.isStateListener = isStateListener;
  }

  public boolean getIsDataFieldStateListener() {
    return isDataFieldStateListener;
  }

  public void setIsDataFieldStateListener(boolean isDataFieldStateListener) {
    this.isDataFieldStateListener = isDataFieldStateListener;
  }

  public long getStartAfter() {
    return startAfter;
  }

  public void setStartAfter(long startAfter) {
    this.startAfter = startAfter;
  }

  public long getRunTime() {
    return runTime;
  }

  public void setRunTime(long runTime) {
    this.runTime = runTime;
  }


  public void setProperties(PropertySet pset) {
    this.properties = pset;
  }
  
  public PropertySet getProperties() {
    return this.properties;
  }

  /**
   * 
   * @requires 
   *  propName != null /\ val != null
   * @effects 
   *  if property <tt>propName</tt> of <tt>this</tt> has value equal to <tt>val</tt>
   *    return true
   *  else
   *    return false
   *     
   * @version 2.7.4
   */
  public boolean hasProperty(PropertyName propName, Object val) {
    boolean hasIt = false;
    if (properties != null) {
      hasIt = properties.containsProperty(propName, val);
    }
    
    return hasIt;
  }


  /**
   * 
   * @requires 
   *  propName != null
   * @effects 
   *  if exists property <tt>propName</tt> of <tt>this</tt> 
   *    return its value
   *  else
   *    return <tt>defaultVal</tt>
   *     
   * @version 2.7.4
   */  
  public Object getProperty(PropertyName propName, Object defaultVal) {
    Object val = null;
    if (properties != null) {
      val = properties.getPropertyValue(propName, defaultVal);
    } else {
      val = defaultVal;
    }
    
    return val;
  }
  
  /**
   * 
   * @requires 
   *  propName != null
   * @effects 
   *  if exists property <tt>propName</tt> of <tt>this</tt> whose value type is assignable to <tt>valueType</tt> 
   *    return its value
   *  else
   *    return <tt>defaultVal</tt>
   *     
   * @version 3.0
   */  
  public <T> T getProperty(PropertyName propName,
      Class<T> valueType, T defaultVal) {
    T val = null;
    if (properties != null) {
      val = properties.getPropertyValue(propName, valueType, defaultVal);
    } else {
      val = defaultVal;
    }
    
    return val;
  }

  public String toString() {
    return this.getClass().getSimpleName()+"("+id+","+
          ((applicationModule != null) ? applicationModule.getName() : 
            ((linkedRegion != null) ? linkedRegion.getName() : 
//              ((containRegion != null) ? containRegion.getName() : 
                ""))
            +
          ","+controller+")";
  }

  /**
   * @effects 
   *  return true if <tt>controllerCls</tt> of this is a composite controller
   *  else return false  
   */
  public boolean isComposite() {
    try {
      controllerCls.asSubclass(CompositeController.class);
      return true;
    } catch (ClassCastException e) {
      // not composite
      return false;
    }
  }

  /**
   * @effects 
   *  if exists controller command specification whose name is specified by <tt>commandName</tt>
   *    return it (as <tt>Object</tt>)
   *  else
   *    return <tt>null</tt>
   *    
   *  @version 3.0 
   *    
   */
  public Object getControllerCommand(PropertyName commandName) {
    if (properties != null) {
      Object val = properties.getPropertyValue(commandName, null);
      
      return val;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if exists data controller command specification whose name is specified by <tt>"domainapp.controller.dataController."cmd</tt>
   *    return it (as <tt>Object</tt>)
   *  else
   *    return <tt>null</tt>
   *    
   *  @version 3.0 
   *    
   */
  public Object getDataControllerCommand(String cmd) {
    if (properties != null) {
      PropertyName commandName = PropertyName.lookUpBySysPropName("domainapp.controller.dataController."+cmd);
      
      if (commandName == null) 
        return null;
      else {
        Object val = properties.getPropertyValue(commandName, null);
        return val;
      }
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if exists data controller command specifications
   *    return <tt>Map(PropertyName,Object)</tt> where keys are command names, values are command classes
   *  else
   *    return <tt>null</tt>
   * @version 3.0
   */
  public Map<PropertyName, Object> getDataControllerCommands() {
    if (properties != null) {
      // v3.1: use prefix property name
      // String cmdPrefix = "domainapp.controller.dataController";
      PropertyName cmdPrefix = PropertyName.prefix_controller_dataController;
      
      Collection<PropertyName> propNames = PropertyName.lookUpBySysPropNamePrefix(cmdPrefix);
      
      if (propNames != null) {
        Map<PropertyName,Object> cmdMap = new HashMap();
        Object val;
        for (PropertyName commandName : propNames) {
          val = properties.getPropertyValue(commandName, null);
          if (val != null) {
            cmdMap.put(commandName, val);
          }
        }
        
        return (cmdMap.isEmpty()) ? null : cmdMap;
      }
    }
    
    return null;
  }

  /**
   * @effects 
   *  merge values of <tt>otherCfg</tt> in and taking precendence over this 
   * @version 5.2b
   * TODO: complete this merge for all attributes!
   */
  public void merge(ControllerConfig otherCfg) {
    if (otherCfg == null) return;
    
    if (otherCfg.openPolicy != null)
      this.openPolicy = otherCfg.openPolicy;
    
  }

}
