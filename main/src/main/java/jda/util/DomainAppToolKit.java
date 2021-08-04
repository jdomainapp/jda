package jda.util;

import java.util.ArrayList;
import java.util.List;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.expression.Op;
import jda.modules.common.types.tree.Tree;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.security.def.Role;
import jda.mosa.controller.ControllerBasic;
import jda.util.properties.PropertySet;

@Deprecated
public class DomainAppToolKit {
  private DomainAppToolKit() {}

  /**
   * @effects 
   *  create the main module, defined by <tt>moduleMainCls</tt>, and the functional modules defined by 
   *  <tt>moduleClasses</tt> whose domain schema is <tt>schema</tt>.
   *  return a <tt>List</tt> of <tt>Controller</tt>s of the modules (the main module's controller is the first element)
   *  in the same order as they were specified in the argument.
   */
  public static List<ControllerBasic> createModules(DODMBasic schema, String dbName, Class moduleMainCls, Class...moduleClasses) {
    System.out.printf("%ncreateModules()%n");

    List<ControllerBasic> moduleCtls = new ArrayList<ControllerBasic>();
    
    // the main module
    Configuration config = SwTk.createSimpleConfigurationInstance(schema, dbName, moduleMainCls);
    
    ApplicationModule mainModule = createApplicationModule(moduleMainCls, config);
    ControllerBasic mainCtl = ControllerBasic.createController(schema, mainModule, null, null, config);

    moduleCtls.add(mainCtl);

    System.out.printf("Created main module: %n  %s%n", mainCtl);
    
    // create functional modules
    ControllerBasic ctl;
    for (Class moduleCls : moduleClasses) {
      ApplicationModule module = createApplicationModule(moduleCls, config);
      
      ctl = ControllerBasic.createController(schema, module, null, mainCtl, config);
      
      System.out.printf("Created functional module: %n  %s%n", ctl);
      
      moduleCtls.add(ctl);
    }
    
    return moduleCtls;
  }
  
  public static ControllerBasic createMainModule(DODMBasic schema, 
      Configuration config, Class moduleMainCls) {
    System.out.printf("%ncreateMainModule(%s)%n", moduleMainCls);

    // the main module
    ApplicationModule mainModule = createApplicationModule(moduleMainCls, config);
    ControllerBasic mainCtl = ControllerBasic.createController(schema, mainModule, null, null, config);

    System.out.printf("Created main module: %n  %s%n", mainCtl);
    
    return mainCtl;
  }
  
  public static List<ControllerBasic> createModules(DODMBasic schema, 
      String dataSourceName, 
      ControllerBasic mainCtl, Configuration config, Class...moduleClasses) {
    System.out.printf("%ncreateModules()%n");

    List<ControllerBasic> moduleCtls = new ArrayList<ControllerBasic>();
    
    // create functional modules
    ControllerBasic ctl;
    for (Class moduleCls : moduleClasses) {
      ApplicationModule module = createApplicationModule(moduleCls, config);
      
      ctl = ControllerBasic.createController(schema, module, null, mainCtl, config);
      
      System.out.printf("Created functional module: %n  %s%n", ctl);
      
      moduleCtls.add(ctl);
    }
    
    return moduleCtls;
  }
  
  /**
   * @effects
   *  return an <tt>ApplicationModule</tt> defined by <tt>moduleDescClass</tt> using the specified 
   *  configuration <tt>config</tt>
   */
  public static ApplicationModule createApplicationModule(Class moduleDescClass, Configuration config) {
    ModuleDescriptor moduleDesc = (ModuleDescriptor) moduleDescClass.getAnnotation(ModuleDescriptor.class);
    
    Class model = moduleDesc.modelDesc().model();

    if (model == CommonConstants.NullType){ //ModuleDescriptor.NullType) {
      model = null;
    }
    
    ModelConfig modelCfg = null; 
    if (model != null) {
      PropertySet modelProps = null;
      modelCfg = ModelConfig.createInstance(moduleDesc.modelDesc(), modelProps);
    }
    
    RegionGui viewCfg = null;
    //Class viewCls = moduleDesc.viewDesc().view();
    //if (viewCls != MetaConstants.NullType) //ModuleDescriptor.NullType)
    PropertySet guiProps = null;  // ignore
    viewCfg = RegionGui.createInstance(moduleDesc.name(), 
      null, // ignore label
      null, // ignore class label
      guiProps, 
      moduleDesc.viewDesc());
    
    // ignore menu item
    RegionToolMenuItem menuItemCfg = null;  
    
    // ignore properties
    PropertySet ctlProps = null;
    
    ControllerConfig ctlCfg = ControllerConfig.createInstance(moduleDesc.controllerDesc(), ctlProps);
    
    Class[] childModuleDescClasses = moduleDesc.childModules();
    ApplicationModule[] childModules = null;
    if (childModuleDescClasses.length > 0)
      //TODO: if necesary, look up the child modules for composite module,
      childModules = null;
    
    // v2.7.2: support print config
    PropertySet printConfig = null;
    //TODO: uncomment the following to use print config
    // printConfig = PropertySetFactory.createPrintConfigPropertySet(schema, moduleDescClass);
    
    Tree contTreeObj = null;  // v3.0
    
    ApplicationModule module = ApplicationModule.createInstance(
        moduleDesc, config, modelCfg, viewCfg, menuItemCfg, ctlCfg, printConfig, childModules,
        contTreeObj);
    
    if (modelCfg != null)
      modelCfg.setApplicationModule(module);

    if (viewCfg != null)
      viewCfg.setApplicationModule(module);
    
    ctlCfg.setApplicationModule(module);
    
    if (printConfig != null)
      printConfig.setAppModule(module);
    
    return module;
  }

  /**
   * @effects 
   *  if exists in <tt>dodm</tt> {@link Role} whose name <b>equals</b> <tt>roleName</tt>
   *    return the object
   *  else
   *    return <tt>null</tt> 
   *  
   *  <p>throws NotFoundException if no suitable attribute is found for the value, 
   *  DataSourceException if failed to retrieve object from <tt>dodm</tt>
   *   
   * @version 3.2
   */
  public static Role retrieveRole(DODMBasic dodm, String roleName) throws NotFoundException, DataSourceException {
      DOMBasic dom = dodm.getDom();
      
      Role obj = dom.retrieveObject(Role.class, 
          Role.Attribute_name, 
          Op.EQ, 
          roleName);
      
      return obj;
  }
}
