package jda.modules.mccl.conceptmodel.module;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.mosa.model.DomainIdable;
import jda.mosa.model.Oid;
import jda.util.properties.PropertySet;

@DClass(schema="app_config")
public class ApplicationModule implements DomainIdable {
  /*** 
   * Implements DomainIdable
   */
  // not a domain attribute
  private Oid oid;
  
  @Override
  public void setOid(Oid id) {
    this.oid = id;
  }

  @Override
  public Oid getOid() {
    return oid;
  }
  // end implement DomainIdable

  /**end {@link ModuleType} */

  public static final String AttributeName_id = "id";
  public static final String AttributeName_type = "type";
  public static final String AttributeName_childModules = "childModules";
  public static final String AttributeName_parentModules = "parentModules";
  public static final String AttributeName_name = "name";
  public static final String Association_WithModelConfig = "module-has-modelConfig";
  
  @DAttr(name="id",type=Type.Long,id=true,auto=true,mutable=false,optional=false)
  private long id;
  
  @DAttr(name=AttributeName_name,
      //v2.7: id=true, 
      type=Type.String,length=100,optional=false)
  private String name;

  @DAttr(name="config",type=Type.Domain,optional=false)
  @DAssoc(ascName="config-has-modules",role="module",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Configuration.class,cardMin=1,cardMax=1))
  private Configuration config; 

  @DAttr(name="modelCfg",type=Type.Domain)
  @DAssoc(ascName=Association_WithModelConfig,role="module",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ModelConfig.class,cardMin=1,cardMax=1))
  private ModelConfig modelCfg;
  
  @DAttr(name="viewCfg",type=Type.Domain)
  @DAssoc(ascName="module-has-viewConfig",role="module",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=RegionGui.class,cardMin=1,cardMax=1))
  private RegionGui viewCfg;
  
  @DAttr(name="toolMenuItemCfg",type=Type.Domain)
  @DAssoc(ascName="module-has-menuItem",role="module",
  ascType=AssocType.One2One,endType=AssocEndType.One,
  associate=@Associate(type=RegionToolMenuItem.class,cardMin=1,cardMax=1))  // v2.7  
  private RegionToolMenuItem toolMenuItemCfg;

  @DAttr(name="controllerCfg",type=Type.Domain,optional=false)
  @DAssoc(ascName="module-has-controllerConfig",role="module",
    ascType=AssocType.One2One,endType=AssocEndType.One,
    associate=@Associate(type=ControllerConfig.class,cardMin=1,cardMax=1))
  private ControllerConfig controllerCfg;
  
  @DAttr(name=AttributeName_type,type=DAttr.Type.Domain,optional=false)  
  private ModuleType type;

  @DAttr(name=AttributeName_childModules,type=DAttr.Type.Collection,
      serialisable=false, filter=@Select(clazz=ApplicationModuleMap.class))
  @DAssoc(ascName="parent-has",role="parent",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=ApplicationModuleMap.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))
  private Collection<ApplicationModuleMap> childModules;
  
  /**
   * derived from {@link #childModules}, which contains the child {@link ApplicationModule}s. 
   * extracted from {@link #childModules}.
   *  
   * @version v5.2:
   */
  private Collection<ApplicationModule> children;
  
  // v5.2
  /** derived from {@link #childModules}: a subset of service modules */
  private Collection<ApplicationModule> serviceModules;
  
//  @DomainConstraint(name=AttributeName_parentModules,type=DomainConstraint.Type.Collection,
//      serialisable=false, filter=@Select(clazz=ApplicationModuleMap.class))
//  @Association(name="parent-has",role="children",
//      type=AssocType.One2Many,endType=AssocEndType.One,
//      associate=@AssocEnd(type=ApplicationModuleMap.class,cardMin=0,cardMax=Association.CARD_MORE))
//  private List<ApplicationModuleMap> parentModules;
  
  @DAttr(name="isViewer",type=DAttr.Type.Boolean)  
  private boolean isViewer;

  @DAttr(name="isPrimary",type=DAttr.Type.Boolean)  
  private boolean isPrimary;

  // v2.7.2: 
  @DAttr(name="printConfig",type=DAttr.Type.Domain)
  @DAssoc(ascName="module-has-printCfg",role="module",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=PropertySet.class,cardMin=0,cardMax=1))
  private PropertySet printConfig; 
  
  // containment tree: not serialisable (serialise its XML representation instead)
  private Tree contTreeObj;
  
  /** derived from {@link #contTreeObj} */ 
  @DAttr(name="contTree",auto=true,mutable=false,type=DAttr.Type.String,length=5000)
  private String contTree;
  
  // v5.2
  @DAttr(name="properties",type=DAttr.Type.Domain)
  @DAssoc(ascName="module-has-props",role="module",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=PropertySet.class,cardMin=0,cardMax=1))
  private PropertySet properties; 
  // end v5.2
  
  /** derived from {@link #getDomainClass()} */
  @DAttr(name="domainClass",type=Type.String,length=100,mutable=false,auto=true,serialisable=false)
  private String domainClass;
  
  /**
   * This constructor is used to create object from the data source
   */
  @DOpt(type=DOpt.Type.DataSourceConstructor)
  public ApplicationModule(
      Long id,
      String name, 
      Configuration config,
      ModelConfig modelCfg,
      RegionGui viewCfg,
      RegionToolMenuItem toolMenuItemCfg,
      ControllerConfig controllerCfg,
      ModuleType type,
      Boolean viewer, 
      Boolean primary,
      PropertySet printConfig,
      String contTree // v3.0
      ,PropertySet props // v5.2
      ) throws NotPossibleException // v3.0 
  { 
    this.id = nextId(id);
    
    this.name = name;
    
    this.config = config;
    
    this.modelCfg = modelCfg;
    
    this.viewCfg = viewCfg; 
    this.toolMenuItemCfg = toolMenuItemCfg;
    
    this.controllerCfg = controllerCfg;
    
    // v2.7
    this.type = type;
    
    if (viewer != null)
      this.isViewer = viewer;
    else
      this.isViewer = true;

    if (primary != null)
      this.isPrimary = primary;
    else
      this.isPrimary = true;
    
    this.printConfig = printConfig;
    
    if (contTree != null) {
      contTreeObj = Tree.fromXMLString(contTree);
      this.contTree = contTree;
    }
    
    this.properties = props;
  }
  
  /**
   * This constructor is used to create a <b>basic</tt> module configuration.
   * It does NOT take any properties.
   */
  public ApplicationModule(String name, 
      Configuration config,
      ModelConfig modelCfg,
      RegionGui viewCfg,
      RegionToolMenuItem toolMenuItemCfg,
      ControllerConfig controllerCfg,
      ModuleType type,      
      Boolean viewer, 
      Boolean primary,
      PropertySet printConfig,
      ApplicationModule[] childModules   // v2.7.2: used for composite controllers
      , Tree contTreeObj  // v3.0
      ) {
    /*v5.2: redirect
    this(null, name, config, modelCfg, viewCfg, toolMenuItemCfg, controllerCfg, type, 
        viewer, primary, printConfig, 
        null  // v3.0: process containment tree separately below 
        );
    // v2.7.2: add child modules
    if (childModules != null) {
      this.childModules = new ArrayList<ApplicationModuleMap>();
      for (ApplicationModule c : childModules) {
        ApplicationModuleMap amm = new ApplicationModuleMap(this, c);
        
        // add this mapping as parent mapping
        this.childModules.add(amm);
        
        // also add this mapping as a child mapping
        //c.addParentModuleMap(amm);
      }
    }
    
    // v3.0: support containment tree
    if (contTreeObj != null) { 
      this.contTreeObj = contTreeObj;
      this.contTree = contTreeObj.toXMLString();
    }
    */
    this(name, config, modelCfg, viewCfg, toolMenuItemCfg, controllerCfg, type, 
        viewer, primary, printConfig, childModules, contTreeObj, null);
  }

  /**
   * This constructor is used to create a <b>standard</b> module configuration.
   * It DOES take extra properties.
   * 
   * @version 5.2:
   * - support {@link PropertySet} and improve <tt>childModules</tt> to support service modules
   */
  public ApplicationModule(String name, 
      Configuration config,
      ModelConfig modelCfg,
      RegionGui viewCfg,
      RegionToolMenuItem toolMenuItemCfg,
      ControllerConfig controllerCfg,
      ModuleType type,      
      Boolean viewer, 
      Boolean primary,
      PropertySet printConfig,
      ApplicationModule[] childModules   // v2.7.2: used for composite controllers
      , Tree contTreeObj  // v3.0
      , PropertySet props // v5.2
      ) {
    this(null, name, config, modelCfg, viewCfg, toolMenuItemCfg, controllerCfg, type, 
        viewer, primary, printConfig, 
        null  // v3.0: process containment tree separately below 
        ,props
        );
    // v2.7.2: add child modules
    if (childModules != null) {
      this.childModules = new ArrayList<ApplicationModuleMap>();
      for (ApplicationModule c : childModules) {
        ApplicationModuleMap amm = new ApplicationModuleMap(this, c);
        
        // add this mapping as parent mapping
        this.childModules.add(amm);
        
        // also add this mapping as a child mapping
        //c.addParentModuleMap(amm);
      }
    }
    
    // v3.0: support containment tree
    if (contTreeObj != null) { 
      this.contTreeObj = contTreeObj;
      this.contTree = contTreeObj.toXMLString();
    }
  }
  
  /**
   * Use this method to create module configuration for a <b>basic</tt> module.
   * 
   * @effects 
   *  create and return an instance of this whose details are defined  in <tt>moduleDesc</tt>,
   *  whose controller configuration is specified in <tt>controllerCfg</tt>,  
   *  and using <tt>config</tt>.
   *  
   *  <p>Throws NotPossibleException if fails to create the instance.
   */
  public static ApplicationModule createInstance(ModuleDescriptor moduleDesc, 
      Configuration config, 
      ModelConfig modelCfg, RegionGui viewCfg, 
      RegionToolMenuItem toolMenuItemCfg, ControllerConfig controllerCfg,
      PropertySet printConfig, 
      ApplicationModule[] childModules  // v2.7.2
      ,Tree contTreeObj   // v3.0
      ) throws NotPossibleException {
    /* v5.2: redirect
    ModuleType type = moduleDesc.type();
    
    Class<? extends ApplicationModule> baseCls;
    if (type.isDomain() 
        && !type.isType(ModuleType.DomainMain) // exclude the main module from the list
        ) {
      // use domain application module class
      baseCls = DomainApplicationModule.class;
    } else {
      // use this class
      baseCls = ApplicationModule.class;
    }

    Constructor<? extends ApplicationModule> cons = null;
    ApplicationModule module;
    try {
      cons = (Constructor<? extends ApplicationModule>) 
          baseCls.getConstructor(new Class[] {
              String.class, 
              Configuration.class,
              ModelConfig.class,
              RegionGui.class,
              RegionToolMenuItem.class,
              ControllerConfig.class,
              ModuleType.class,
              Boolean.class, 
              Boolean.class,
              PropertySet.class,
              ApplicationModule[].class // v2.7.2
              , Tree.class  // v3.0
          });

      module = cons.newInstance(
              moduleDesc.name(), 
              config, 
              modelCfg,
              viewCfg,
              toolMenuItemCfg,
              controllerCfg, 
              type,
              moduleDesc.isViewer(), 
              moduleDesc.isPrimary(),
              printConfig,
              childModules
              , contTreeObj
          );
    } catch (Exception e) {
      // something wrong
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          "Không thể tạo đối tượng lớp: {0}.{1}({2})", baseCls.getSimpleName(), cons, moduleDesc.name()+",...");
    }

    return module;
    */
    return createInstance(moduleDesc, config, modelCfg, viewCfg, toolMenuItemCfg, 
        controllerCfg, printConfig, childModules, contTreeObj
        , null);
  }
  
  /**
   * Use this method to create module configuration for a <b>standard</tt> module.
   * 
   * @effects 
   *  create and return an instance of this whose details are defined  in <tt>moduleDesc</tt>,
   *  whose controller configuration is specified in <tt>controllerCfg</tt>,  
   *  and using <tt>config</tt>.
   *  
   *  <p>Throws NotPossibleException if fails to create the instance.
   *  @verson 5.2
   */
  public static ApplicationModule createInstance(ModuleDescriptor moduleDesc, 
      Configuration config, 
      ModelConfig modelCfg, RegionGui viewCfg, 
      RegionToolMenuItem toolMenuItemCfg, ControllerConfig controllerCfg,
      PropertySet printConfig, 
      ApplicationModule[] childModules  // v2.7.2
      ,Tree contTreeObj   // v3.0
      , PropertySet props
      ) throws NotPossibleException {
    ModuleType type = moduleDesc.type();
    
    Class<? extends ApplicationModule> baseCls;
    if (type.isDomain() 
        && !type.isType(ModuleType.DomainMain) // exclude the main module from the list
        ) {
      // use domain application module class
      baseCls = DomainApplicationModule.class;
    } else {
      // use this class
      baseCls = ApplicationModule.class;
    }

    Constructor<? extends ApplicationModule> cons = null;
    ApplicationModule module;
    try {
      cons = (Constructor<? extends ApplicationModule>) 
          baseCls.getConstructor(new Class[] {
              String.class, 
              Configuration.class,
              ModelConfig.class,
              RegionGui.class,
              RegionToolMenuItem.class,
              ControllerConfig.class,
              ModuleType.class,
              Boolean.class, 
              Boolean.class,
              PropertySet.class,
              ApplicationModule[].class // v2.7.2
              , Tree.class  // v3.0
              , PropertySet.class
          });

      module = cons.newInstance(
              moduleDesc.name(), 
              config, 
              modelCfg,
              viewCfg,
              toolMenuItemCfg,
              controllerCfg, 
              type,
              moduleDesc.isViewer(), 
              moduleDesc.isPrimary(),
              printConfig,
              childModules
              , contTreeObj
              , props
          );
    } catch (Exception e) {
      // something wrong
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {baseCls.getSimpleName(), cons, moduleDesc.name()+ ",..."} );
    }

    return module;
  }
  
  private long nextId(Long currId) {
    // use current time as id
    if (currId == null)
      return System.nanoTime();
    else
      return currId;
  }

  
  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
//  public String getController() {
//    return controller;
//  }

  public Class getControllerCls() {
    return (controllerCfg != null) ? controllerCfg.getControllerCls() : null;
  }

  
//  public String getDataController() {
//    return dataController;
//  }

  public Class getDataControllerCls() {
    return (controllerCfg != null) ? controllerCfg.getDataControllerCls() : null;
  }

  public String getDomainClass() {
    if (modelCfg != null)
      return modelCfg.getDomainClass();
    else
      return null;
  }
  
  public Class getDomainClassCls() {
    if (modelCfg != null)
      return modelCfg.getDomainClassCls();//domainClassCls;
    else
      return null;
  }

  public LAName getDefaultCommand() {
    return (controllerCfg != null) ? controllerCfg.getDefaultCommand() : null;
  }


  public Configuration getConfig() {
    return config;
  }

  public void setConfig(Configuration config) {
    this.config = config;
  }

  public ModelConfig getModelCfg() {
    return modelCfg;
  }

  public void setModelCfg(ModelConfig modelCfg) {
    this.modelCfg = modelCfg;
  }

  public RegionGui getViewCfg() {
    return viewCfg;
  }

  public void setViewCfg(RegionGui viewCfg) {
    this.viewCfg = viewCfg;
  }

  /**
   * @effects 
   *  (derived from {@link #viewCfg})
   *  if viewCfg != null /\ viewCfg.label != null
   *    return the label string
   *  else
   *    return null
   */
  public String getLabelAsString() {
    if (viewCfg != null) {
      return viewCfg.getLabelAsString();
    } else {
      return null;
    }
  }
  
  // v2.7.3
  public String getDomainClassLabelAsString() {
    if (viewCfg != null) {
      return viewCfg.getDomainClassLabelAsString();
    } else {
      return null;
    }
  }
  
  public RegionToolMenuItem getToolMenuItemCfg() {
    return toolMenuItemCfg;
  }

  public void setToolMenuItemCfg(RegionToolMenuItem toolMenuItemCfg) {
    this.toolMenuItemCfg = toolMenuItemCfg;
  }

  public ControllerConfig getControllerCfg() {
    return controllerCfg;
  }

  public void setControllerCfg(ControllerConfig cfg) {
    this.controllerCfg = cfg;
  }

  public boolean hasController() {
    return controllerCfg != null;
  }

  public boolean getIsStateListener() {
    return controllerCfg != null && controllerCfg.getIsStateListener();
  }

  public ModuleType getType() {
    return type;
  }

  public void setType(ModuleType type) {
    this.type = type;
  }

  public boolean getIsViewer() {
    return isViewer;
  }

  public void setIsViewer(boolean viewer) {
    this.isViewer = viewer;
  }

  public boolean getIsPrimary() {
    return isPrimary;
  }

  public void setIsPrimary(boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  public PropertySet getPrintConfig() {
    return printConfig;
  }

  public void setPrintConfig(PropertySet printConfig) {
    this.printConfig = printConfig;
  }

  public String getContTree() {
    return contTree;
  }

  public void setContTreeObj(Tree contTreeObj) {
    if (contTreeObj != null) {
      this.contTreeObj = contTreeObj;
      this.contTree = contTreeObj.toXMLString();
    }
  }

  public Tree getContTreeObj() {
    return contTreeObj;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+"("+name+
        ((controllerCfg != null) ? ","+controllerCfg.getController() : "")+")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ApplicationModule other = (ApplicationModule) obj;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

  /**
   * @effects 
   *  return true if <tt>controllerCls</tt> of this is a composite controller
   *  else return false  
   */
  public boolean isComposite() {
    try {
      Class ctlCls = getControllerCls(); 
      if (ctlCls != null) {
        ctlCls.asSubclass(CompositeController.class);
        return true;
      } else {
        return false;
      }
    } catch (ClassCastException e) {
      // not composite
      return false;
    }
  }

  /**
   * @effects 
   *  if this module is configured with auto-start=true
   *    return true
   *  else  
   *    return false 
   * @version 5.2
   */
  public boolean isAutoStart() {
    Boolean autoStart = getProperty(PropertyName.module_autoStart, Boolean.class, Boolean.FALSE);
    
    return autoStart;
  }
  
  /**
   * @requires 
   *  <tt>this.isComposite() = true</tt>
   * @effects 
   * if <tt>this.isComposite() = true</tt>
   *  return <tt>Iterator</tt> of the component modules
   * else 
   *  return <tt>null</tt>
   */
  public Iterator<ApplicationModule> getChildModulesIterator() {
    // v5.2: Collection<ApplicationModule> children;
    
    if (childModules != null) {
      /* v5.2: improved using cache
      children = new ArrayList<ApplicationModule>();
      for (ApplicationModuleMap m : childModules) {
        children.add(m.getChildModule());
      }
      */
      if (children == null) {
        children = new ArrayList<ApplicationModule>();
        for (ApplicationModuleMap m : childModules) {
          children.add(m.getChildModule());
        }
      }
      
      return children.iterator();
    } else {
      return null;
    }
  }
  

  /**
   * @effects 
   *  if exists child modules in {@link #childModules} that are service modules
   *    return an {@link Iterator} of them
   *  else
   *    return null 
   *  
   * @version 5.2
   */
  public Iterator<ApplicationModule> getDependentServiceModulesIterator() {
    if (childModules == null) return null;
    
    if (serviceModules == null) {
      serviceModules = new ArrayList<>();
      for (ApplicationModuleMap m : childModules) {
        ApplicationModule cm = m.getChildModule();
        if (cm.isType(ModuleType.Composite.Service)) {
          serviceModules.add(cm);
        }
      }
    }
    
    if (serviceModules != null && !serviceModules.isEmpty()) {
      return serviceModules.iterator();
    } else {
      return null;
    }
  }
  
  /**
   * @effects 
   *  if {@link #type} is the specified type
   *    return true
   *  else
   *    return false
   *    
   * @version 5.2
   */
  public boolean isType(ModType modType) {
    return type.isType(modType);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=AttributeName_childModules)
  public void addChildModuleMap(ApplicationModuleMap m) {
    if (childModules == null) {
      childModules = new ArrayList<ApplicationModuleMap>();
    }
    
    childModules.add(m);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=AttributeName_childModules)
  public void addChildModuleMap(List<ApplicationModuleMap> m) {
    if (childModules == null) {
      childModules = new ArrayList<ApplicationModuleMap>();
    }
    
    childModules.addAll(m);
  }
  
  @DOpt(type=DOpt.Type.LinkRemover)
  @AttrRef(value=AttributeName_childModules)
  public void removeChildModuleMap(ApplicationModuleMap m) {
    childModules.remove(m);
  }

  public Integer getChildModulesCount() {
    return (childModules != null) ? childModules.size() : 0;
  }

  public Collection<ApplicationModuleMap> getChildModulesMap() {
    return childModules;
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
   * @version 5.2
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
   * @version 5.2
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
   * @version 5.2
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
  
//  public RegionLinking getLinkedRegion() {
//    return linkedRegion;
//  }
//
//  public void setLinkedRegion(RegionLinking linkedRegion) {
//    this.linkedRegion = linkedRegion;
//  }

//  @Metadata(type=Metadata.Type.MethodValueAdder)
//  @MemberRef(name=AttributeName_parentModules)
//  public void addParentModuleMap(ApplicationModuleMap m) {
//    if (parentModules == null) {
//      parentModules = new ArrayList<ApplicationModuleMap>();
//    }
//    
//    parentModules.add(m);
//  }
//
//  @Metadata(type=Metadata.Type.MethodValueRemover)
//  @MemberRef(name=AttributeName_parentModules)
//  public void removeParentModuleMap(ApplicationModuleMap m) {
//    parentModules.remove(m);
//  }
//
//  public Integer getParentModulesCount() {
//    return (parentModules != null) ? parentModules.size() : 0;
//  }
}
