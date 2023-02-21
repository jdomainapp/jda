package jda.modules.setup.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Edge;
import jda.modules.common.types.tree.Node;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Company;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.SplashInfo;
import jda.modules.mccl.conceptmodel.Configuration.Organisation;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ApplicationModuleMap;
import jda.modules.mccl.conceptmodel.module.DomainApplicationModule;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.modules.mccl.conceptmodel.view.ExclusionMap;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.conceptmodel.view.RegionMap;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.ModuleToolkit;
import jda.modules.mccl.syntax.containment.CEdge;
import jda.modules.mccl.syntax.containment.CTree;
import jda.modules.mccl.syntax.containment.Child;
import jda.modules.mccl.syntax.containment.ScopeDesc;
import jda.modules.mccl.syntax.containment.SubTree1L;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.Query;
import jda.modules.setup.init.RegionConstants;
import jda.modules.setup.init.StyleConstants;
import jda.modules.setup.model.SetUpBasic.MessageCode;
import jda.mosa.view.assets.datafields.JDataField;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySetFactory;

/**
 * @overview
 *  A setup helper class responsible for setting up the configuration-related resources.
 *  
 *  <p>It specifies a set of {@link #cfgClasses} that together defines the data schema for all 
 *  {@link Configuration}-related resources, and {@link #defaultModuleDescriptors} that lists the 
 *  default system modules that need to be included into every application. 
 * 
 * @author dmle
 * 
 */
public class SetUpConfigBasic {
  private DODMBasic dodm;
  private Configuration config;

  // v2.7
  private boolean registeredSchema = false;
  
  // constrants
  //
  /*v2.7.4: change to normal fields
  private static final Style DEFAULT_LINKED_LABEL_STYLE; = StyleConstants.Heading4;
  private static final Style DEFAULT_TITLE_LABEL_STYLE = StyleConstants.Heading1;
  private static final Style DEFAULT_DOMAIN_TYPE_LABEL_STYLE = StyleConstants.DefaultBlue;
  */      
  protected Style DEFAULT_LINKED_LABEL_STYLE;
  protected Style DEFAULT_TITLE_LABEL_STYLE;
  protected Style DEFAULT_DOMAIN_TYPE_LABEL_STYLE;
  
  protected static final Class<ModuleDescriptor> MD = ModuleDescriptor.class;

  /** the configuration domain classes */
  /*v2.7.3: replaced by a better schema registeration code
  public static Class[] enumClasses = new Class[] {
    ModuleType.class,
    Region.Type.class,
    LogicalAction.LAName.class,
    Configuration.Language.class,
    //v2.7.3: moved to chart module: 
    // ChartWrapper.ChartType.class,
    OpenPolicy.class, // v2.6.4.b
    AlignmentX.class, // v2.7.2
    AlignmentY.class,
    PropertySetType.class,
    // v2.7.3 congnv 
    DomainConstraint.Type.class
  };
  */
  
  private static Class[] cfgClasses = new Class[] { //
      Label.class, 
      jda.modules.setup.init.lang.vi.Label.class,
      jda.modules.setup.init.lang.en.Label.class, //
      Style.class, //
      // v2.7.2:
      Property.class,
      PropertySet.class,
      /* v2.7.2: re-arranged the classes to support a new association between RegionLink and ApplicationModule
      Region.class, RegionGui.class, RegionToolMenuItem.class,
      RegionDataField.class, //
      RegionLinking.class, // v2.6.4.b
      RegionMap.class, //
      ExclusionMap.class, //
      LoginUser.class, 
      ApplicationModule.class,
      DomainApplicationModule.class, // v2.7
      ControllerConfig.class, // v2.6.4.b
      ModelConfig.class,  // v2.7
      Configuration.class,
      Organisation.class,
      ApplicationModuleMap.class, // v2.7.2
      */
      //
      Region.class, RegionGui.class, RegionToolMenuItem.class,
      RegionDataField.class, //
      //
      ApplicationModule.class,
      DomainApplicationModule.class, // v2.7
      //
      RegionLinking.class, // v2.6.4.b
      RegionMap.class, //
      ExclusionMap.class, //
      //v2.7.4: removed 
      // LoginUser.class,   
      //
      ControllerConfig.class, // v2.6.4.b
      ModelConfig.class,  // v2.7
      DODMConfig.class, // v2.8
      Configuration.class,
      Organisation.class,
      //
      Company.class,
      SplashInfo.class,
      //
      ApplicationModuleMap.class, // v2.7.2
  };

  /** general modules*/
  protected static final Class[][] defaultModuleDescriptors =
      null; 
  /* v2.7.3: no default modules (to be individually imported by applications)
    {
      // moved to application config: getConfigurationModules(),
      {
        ModuleExportDocument.class
        // moved to application config: ModuleChart.class,
      }
    };
   */
  
  // v2.8: moved out of basics
//  /**
//   * @effects 
//   *  return module configution classes needed to operate on <tt>Configuration</tt> 
//   */
//  public static Class[] getConfigurationModules() {
//    return new Class[] {
//        ModuleConfiguration.class,
//        ModuleApplicationModule.class, 
//        ModuleOrganisation.class, 
//        ModuleControllerConfig.class, // v2.6.4.b
//    };
//  }
  
  // constants
  protected static boolean debug = Toolkit.getDebug(SetUpConfigBasic.class);
  protected static boolean debugRegion = Toolkit.getDebug(Region.class);    // v2.7.4
  
  protected static boolean loggingOn = Toolkit.getLoggingOn(SetUpConfigBasic.class);

  // v2.7.2: maps ModuleDescriptor class to module 
  private Map<Class,ApplicationModule> moduleMap;
  
  // v2.8
  private Region regionComponents;
  
  /**
   * Use this constructor to load configuration to run the application
   */
  public SetUpConfigBasic(DODMBasic dodm) {
    //this.schema = schema;
    this(dodm, null);
  }
  
  /**
   * Use this constructor to create configuration at set-up
   */
  public SetUpConfigBasic(DODMBasic dodm, Configuration config) {
    this.dodm = dodm;
    this.config = config;
    moduleMap = new HashMap<Class,ApplicationModule>();
  }

  /**
   * @effects 
   *  create a return a sub-type of this whose actual type is <tt>suCfgType</tt> and using 
   *  other arguments as input.
   *  
   *  <p>Throws NotPossibleException if failed to create the instance.
   *  
   *  @version 2.7.3
   */
  public static <T extends SetUpConfigBasic> SetUpConfigBasic createInstance(
      Class<T> suCfgType, DODMBasic dodm, Configuration config) throws NotPossibleException {
    try {
      // invoke constructor to create object 
      T instance = suCfgType.getConstructor(DODMBasic.class, Configuration.class).newInstance(dodm, config);
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          "Không thể tạo đối tượng lớp: {0}.{1}({2})", suCfgType.getSimpleName(), "init", config);
    }
  }

  /**
   * @version 2.8
   */
  public static <T extends SetUpConfigBasic> SetUpConfigBasic createInstance(
      Class<T> suCfgType, DODMBasic dodm) throws NotPossibleException {
    try {
      // invoke constructor to create object 
      T instance = suCfgType.getConstructor(DODMBasic.class).newInstance(dodm);
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          "Không thể tạo đối tượng lớp: {0}.{1}({2})", suCfgType.getSimpleName(), "init", "");
    }
  }
  
  // private void initRegions() throws NotFoundException {
  // if (Components == null) {
  // Components = lookUpRegion(RegionNames.Components.name());
  // LoginActions =
  // lookUpRegion(RegionNames.Actions.name(),
  // getGUIName(getModuleName(null, LoginUser.class, Type.Data)));
  //
  // Heading1 = lookUpStyle("Heading1");
  // Heading3 = lookUpStyle("Heading3");
  // }
  // }

//  void run() throws DBException {
//    deleteConfigurationSchema();
//
//    // register the domain classes
//    registerConfigurationSchema(true);
//
//    createConfiguration();
//  }

  
  /**
   * @effects 
   *  delete from the data source the schema containing the class stores of the domain classes in <tt>classes</tt>
   *  
   *  <p>Throws DataSourceException if failed.
   * @version 2.7.4
   */
  public void deleteDataSourceSchema(List<Class> classes) throws DataSourceException {
    /* 
    if (debug) log(MessageCode.UNDEFINED,
        //"  Xóa các ràng buộc"
        "Deleting class store constraints"
        );
    deleteDataSourceConstraints(classes);
    
    if (debug) log(MessageCode.UNDEFINED,
        "Deleting class stores"
        );
    schema.getDom().deleteClasses(classes, true);
    */
    dodm.deleteClasses(classes);
  }
  
  // v2.6.4.b: updated to delete constraints first
  /**
   * @effects 
   *   delete all the system-typed resources used by the configuration schema
   */
  protected void deleteConfigurationSchema() throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Xóa mô hình hiện tại"
        "Deleting configuration schema"
        );

    List<Class> classes = new ArrayList<>();
    /* v2.7.4: moved to method
    if (debug) log(MessageCode.UNDEFINED,
        //"  Xóa các ràng buộc"
        "Deleting constraints"
        );
    deleteDataSourceConstraints(cfgClasses);
    
    if (debug) log(MessageCode.UNDEFINED,
        //"  Xóa các lớp"
        "Deleting configuration classes"
        );
    schema.getDom().deleteClasses(cfgClasses, true);
    */
    Collections.addAll(classes, 
        //cfgClasses
        getConfigurationSchema()
        );
    
    if (defaultModuleDescriptors != null) {
      Class[] modelClasses = SetUpBasic.getModelClasses(
          // v2.8: dodm.getDsm(), 
          defaultModuleDescriptors);          
      /* v2.7.4: moved to method

      if (debug) log(MessageCode.UNDEFINED,
          //"  Xóa các ràng buộc mô-đun"
          "Deleting the model constraints"
          );
      deleteDataSourceConstraints(modelClasses);
      
      if (debug) log(MessageCode.UNDEFINED,
          //"  Xóa các lớp mô-đun"
          "Deleting the models"
          );
      schema.getDom().deleteClasses(modelClasses, true);
      */
      Collections.addAll(classes, modelClasses);
    }
    
    deleteDataSourceSchema(classes);
  }

  // v2.6.4.b
  /*v2.7.4: not used
  protected void deleteDataSourceConstraints(List<Class> classes) throws DataSourceException {
    List<String> consNames;
    for (Class c : classes) {
      consNames = schema.getDom().loadDataSourceConstraints(c);
      if (consNames != null) {
        for (String cons : consNames) {
          if (debug) log(MessageCode.UNDEFINED,
              //"  ràng buộc {0}"
              "   constraint {0}"
              , cons);
          schema.getDom().deleteDataSourceConstraint(c, cons);
        }
      }
    }
  }
  */
  
  /**
   * @effects 
   *  delete all <tt>Configuration</tt>-related data (i.e. objects of {@link #getConfigurationSchema()}) from the data source.
   */
  public void clearConfigurationSchema() throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Xóa dữ liệu mô hình hiện tại"
        "Deleting configuration data"
        );
    boolean strict = false; // v3.0
    List<Class> classList = new ArrayList();
    Collections.addAll(classList, getConfigurationSchema());
    
//v3.0:    dodm.getDom().deleteObjects(
//        getConfigurationSchema(), strict);
    dodm.getDom().deleteObjects(classList, strict);
        
    if (defaultModuleDescriptors != null) {
      Class[] defaultClasses = SetUpBasic.getModelClasses(
          //v2.8: dodm.getDsm(), 
          defaultModuleDescriptors);
      classList.clear();
      Collections.addAll(classList, defaultClasses);
      //v3.0: dodm.getDom().deleteObjects(defaultClasses, strict);
      dodm.getDom().deleteObjects(classList, strict);
    }
  }

  /**
   * @effects 
   * <pre> 
   *  if configuration schema is not yet registered
   *    register the <tt>Configuration</tt> related classes to the domain schema of the application.
   *      if <tt>serialised = true /\ createIfNotExist = true </tt>
   *        create the class store for each class that does not yet exist
   *   </pre>
   *   
   * @version 2.8.
   */
  public void registerConfigurationSchema(SetUpBasic su, 
      boolean serialised, 
      boolean createIfNotExist) throws DataSourceException {
    // not supporting the serialised option
    registerConfigurationSchema(su, createIfNotExist);
  }
  
  /**
   * @effects <pre> 
   *  if configuration schema is not yet registered
   *    register the <tt>Configuration</tt> related classes to the domain schema of the application.
   *      if <tt>createIfNotExist = true </tt>
   *        create the class store for each class that does not yet exist
   *   </pre>
   */
  public void registerConfigurationSchema(SetUpBasic su, boolean createIfNotExist) throws DataSourceException {
    if (registeredSchema)
      return;
    
    log(MessageCode.UNDEFINED,
        //"Tạo mô hình"
        "Registering configuration schema"
        );

    boolean read = false;
    boolean serialised = true;    // v2.8
    
    if (debug) log(MessageCode.UNDEFINED,"  Base configuration classes...");

    su.registerClasses(
        //cfgClasses
        getConfigurationSchema()
        , serialised, createIfNotExist, read);
    
    if (debug) log(MessageCode.UNDEFINED,"  Default module classes...");
    if (defaultModuleDescriptors != null) {
      Class[] defaultClasses = SetUpBasic.getModelClasses(
          //v2.8: dodm.getDsm(), 
          defaultModuleDescriptors);
      su.registerClasses(defaultClasses, serialised, createIfNotExist, read);
    }
    
    registeredSchema = true;
  }

  public boolean isRegisteredConfigurationSchema() {
    return registeredSchema;
  }
  
  protected void setIsRegisteredConfigurationSchema(boolean tf) {
    registeredSchema = tf;
  }

  /**
   * @version 2.7.4
   */
  protected DODMBasic getDodm() {
    return dodm;
  }

  /**
   * @version 2.7.4
   */
  protected Configuration getConfiguration() {
    return config;
  }

  /**
   * @requires 
   *  config != null
   */
  public String getLanguageCode() {
    return config.getLanguage().getLanguageCode();
  }
  
  /**
   * @effects<pre> 
   *  create all <tt>Configuration</tt>-related data (i.e. objects of {@link #getConfigurationSchema()})
   *  if serialised = true 
   *    serialise the objects in the data source.
   *  </pre>
   */
  public void createConfiguration(SetUpBasic su,
      boolean serialised  // v2.8
      ) throws DataSourceException, 
  NotFoundException {
    final String dots = "...";

    final String langCode = config.getLanguage().getLanguageCode();
    DOMBasic dom = dodm.getDom();
    
    if (debug)
      log(MessageCode.UNDEFINED,
          //"Tạo các kiểu (style)"
          "Creating view styles"
          );

    /**v2.7.4: moved to method
    List<Style> styles = Toolkit.getConstantObjects(StyleConstants.class,
        Style.class);
        */
    List<Style> styles = getStyleDefs();
    
    // v2.7.4: added
    initDefaultStyles();
    
    if (debug)
      log(MessageCode.UNDEFINED,
          //"Tổng cộng: {0} (kiểu)", 
          "Total: {0} (styles)",
          styles.size());

    Style styleDefault = null;

    for (Style style : styles) {
      dom.addObject(style, serialised);
      if (style.getName().equals(StyleName.Default.name())) {
        styleDefault = style;
      }
    }

    if (debug)
      log(MessageCode.UNDEFINED,
          //"Tạo các nhãn dữ liệu loại: {0}"
          "Creating labels typed: {0}"
          , langCode);
    
    java.util.Map<String,Label> labels = getSystemLabelConstants(langCode); //getSystemLabels(lang);
    
    if (debug)
      log(MessageCode.UNDEFINED,
          //"Tổng cộng: {0} (nhãn)", 
          "Total: {0} (labels}",
          labels.size());
    
    if (debug)
      log(MessageCode.UNDEFINED,
          //"Khởi tạo vùng cấu hình"
          "Initialising regional configuration"
          );

    /*v2.8: moved to method 
    java.util.Map<String,Region> regions = Toolkit.getConstantObjectsAsMap(
        RegionConstants.class, Region.class);
    */
    java.util.Map<String,Region> regions = getRegionConstants();
    
    if (debug)
      log(MessageCode.UNDEFINED, 
          //"Tổng cộng: {0} (vùng)"
          "Total: {0} (regions)"
          , regions.size());
    
    List<RegionMap> parents;
    Region r;
    String name;
    Label label;
    for (Entry<String,Region> e : regions.entrySet()) {
      name = e.getKey();
      r = (Region) e.getValue();
      if (debugRegion)
        log(MessageCode.UNDEFINED,dots + r.getName());

      // set up style for regions
      if (r.isType(RegionType.Root)) {
        r.setStyle(styleDefault);
      }

      // look up label for region and set it
      // the label constant name is the same as the region constant name
      label = (Label) labels.get(name); 
      if (label != null) {
        dom.addObject(label, serialised);
        
        //System.out.println("saved label " + label);
        
        r.setLabel(label);
      } else {
        if (debug)
          System.err.println("No label for region " + r);
      }
      
      dom.addObject(r, serialised);

      // add all region maps to parents (if any)
      parents = r.getParents();
      if (parents != null) {
        if (debugRegion)
          log(MessageCode.UNDEFINED,dots + " with parents:");
        for (RegionMap pm : parents) {
          if (debugRegion)
            log(MessageCode.UNDEFINED,dots + dots + pm.getParent().getName());

          dom.addObject(pm, serialised);
        }
      }
    }   
    
    if (serialised)
      saveApplicationConfiguration();

    // set up login if security is used
    if (config.getUseSecurity()) {
      // set up login modules
      createLoginModules(su, 
          //v3.0: null,
          serialised
          );
    }
    
    if (defaultModuleDescriptors != null) {
      log(MessageCode.UNDEFINED,
          //"Khởi tạo mô-đun"
          "Creating the default modules"
          );
      //createModulesByView(defaultViewClasses, labels);
      // v3.0: load module labels from file
      java.util.Map<String,Label> defModuleLabels;  
      for (Class[] mds : defaultModuleDescriptors) {
        defModuleLabels = su.getModuleLabels(mds);
        createModules(su, mds, defModuleLabels, serialised);
      }
    }
  }

  /**
   * @effects 
   *  read and return the pre-configured <tt>Region</tt> constant objects ad <tt>Map</tt>, whose keys are 
   *  constant variable names.<br>
   *  <br>Return <tt>null</tt> if no objects are found
   *  
   * @version 2.8
   */
  protected Map<String, Region> getRegionConstants() {
    // return the pre-configured objects
    return Toolkit.getConstantObjectsAsMap(
        RegionConstants.class, Region.class);
  }

//  /**
//   * @effects 
//   *  read the Label constant objects of the system-typed modules from a label constant class of the pre-defined <tt>lang</tt> and 
//   *  return them as a Map 
//   * @version 2.7.3: 
//   *  if no label constants are defined for a system module, read its labels from the module descriptor (if available) 
//   */
//  public java.util.Map<String,Label> getSystemLabelsTODO(String lang) {
//    /*(1) Read the label constants from the label constant class of the specified language */
//    String pkgPrefix = RegionConstants.class.getPackage().getName()+".lang."+lang+".";
//    Class<Label> labelClass = Label.class;
//    Class labelConstantsClass;
//    try {
//      String lblConstantsClassName = pkgPrefix+"LabelConstants";
//      labelConstantsClass = Class.forName(lblConstantsClassName);
//
//    } catch (ClassNotFoundException e) {
//      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e,  
//          "Không tìm thấy lớp nhãn của ngôn ngữ " + lang);      
//    }
//    
//    java.util.Map<String,Label> labels = Toolkit.getConstantObjectsAsMap(
//        labelConstantsClass, labelClass);
//    
//    /*(2) for each module whose labels are not found in (1), use the default labels defined in their module descriptors */
//    if (defaultModuleDescriptors != null) {
//      String modName, labelName;
//      for (Class[] modDescClasses : defaultModuleDescriptors) {
//        for (Class modDescCls : modDescClasses) {
//          modName = getModuleName(modDescCls);
//          if (!containsModuleLabels(labels, modName)) {
//            // module's labels are NOT found -> read from the descriptor
//            addModuleLabels(modDescCls, labels);
//          }
//        }
//      }
//    }
//    
//    return labels;
//  }
//
//  /**
//   * @effects 
//   *  create in <tt>labelMap</tt> entries <tt>(s,l)</tt> where <tt>s = modDescCls.name+"_"+field.name</tt> (<tt>field</tt> is a 
//   *  field of <tt>modDescCls</tt>) and 
//   *  <tt>l</tt> is a <tt>Label</tt> object created from <tt>field.label</tt>
//   */
//  private void addModuleLabels(Class modDescCls, Map<String, Label> labelMap) {
//    
//  }
//
//  /**
//   * @effects 
//   *  if exists in <tt>labelMap</tt> labels for the module named <tt>modName>
//   *    return true
//   *  else
//   *    return false 
//   */
//  private boolean containsModuleLabels(Map<String, Label> labelMap, String modName) {
//    for (String labelName : labelMap.keySet()) {
//      if (labelName.startsWith(modName)) {
//        // found module labels
//        return true;
//      }
//    }
//    
//    // not found
//    return false;
//  }
//
//  /**
//   * @effects 
//   *  if exists <tt>ModuleDescriptor</tt> for <tt>modDescCls</tt>
//   *    return its name
//   *  else
//   *    return null 
//   */
//  private String getModuleName(Class modDescCls) {
//    ModuleDescriptor moduleCfg = (ModuleDescriptor) modDescCls.getAnnotation(MD);
//    
//    if (moduleCfg != null)
//      return moduleCfg.name();
//    else
//      return null;
//  }

  /**
   * @effects 
   *  return the <tt>Style</tt>s that are used by this application
   * @version 2.7.4
   */
  protected List<Style> getStyleDefs() {
    return Toolkit.getConstantObjects(StyleConstants.class,
        Style.class);
  }

  /**
   * @requires 
   *  {@link #getStyleDefs()} has been invoked
   * @effects 
   *  initialise the default Styles that are used by set up
   * @version 2.7.4
   */
  protected void initDefaultStyles() {
    DEFAULT_TITLE_LABEL_STYLE = StyleConstants.Heading1;         
    DEFAULT_LINKED_LABEL_STYLE = StyleConstants.Heading4;       
    DEFAULT_DOMAIN_TYPE_LABEL_STYLE = StyleConstants.Link;
  }

//  /**
//   * @effects 
//   *  if exist in this a <tt>Region</tt> named <tt>regionName</tt>
//   *    return the region
//   *  else
//   *    throw NotFoundException
//   */
//  protected Region lookUpRegion(RegionName regionName) throws NotFoundException {
//    Region region = regions.get(regionName.name());
//    
//    if (region == null) {
//      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, new Object[] {Region.class.getSimpleName(), regionName.name()});
//    }
//    
//    return region;
//  }
  
  /**
   * @effects 
   *  read the Label constant objects of the system-typed modules from a label constant class of the pre-defined <tt>lang</tt> and 
   *  return them as a Map 
   */  
  private java.util.Map<String,Label> getSystemLabelConstants(String lang) {
    String pkgPrefix = RegionConstants.class.getPackage().getName()+".lang."+lang+".";
    Class<Label> labelClass = Label.class;
    Class labelConstantsClass;
    try {
      String lblConstantsClassName = pkgPrefix+"LabelConstants";
      labelConstantsClass = Class.forName(lblConstantsClassName);

    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e,  
          "Không tìm thấy lớp nhãn của ngôn ngữ " + lang);      
    }
    
    java.util.Map<String,Label> labels = Toolkit.getConstantObjectsAsMap(
        labelConstantsClass, labelClass);
    
    return labels;
  }
  
  /**
   * @effects 
   *  store <tt>Configuration</tt> object to data source
   */
  void saveApplicationConfiguration() throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    // save application configuration to db      
    Organisation org = config.getOrganisation();
    
    if (org != null)
      dom.addObject(org);
    
    // v2.7.4: support splashinfo
    saveSplashInfo(config);

    // v2.8:  
    dom.addObject(config.getDodmConfig());
    
    dom.addObject(config);  
  }
  
  /**
   * @effects 
   *  save splash screen info to data source
   */
  private void saveSplashInfo(Configuration config) throws DataSourceException {
    SplashInfo splashInfo = config.getSplashInfo();
    if (splashInfo != null) {
      DOMBasic dom = dodm.getDom();
      
      Company comp = config.getCompany();
      
      dom.addObject(comp);
      dom.addObject(splashInfo);
    }
  }

  /**
   * @effects 
   *  create the login modules and if <tt>serialised=false</tt> then only create them in memory
   */
  protected void createLoginModules(SetUpBasic su, 
      // v3.0: Map<String,Label> labelMap, 
      boolean serialised  // v2.8
      ) throws DataSourceException, NotFoundException {
    // for sub-types to implement
  }

  private String toString(Object[] arr) {
    if (arr.length > 0) {
      StringBuffer sb = new StringBuffer();
      for (Object o : arr) {
        sb.append(o).append(",\n");
      }
      
      sb.delete(sb.length()-2,sb.length()-1);
      
      return sb.toString();
    } else {
      return "";
    }
  }
  
//  /**
//   * This method is used (together with {@link #createDefaultModules(Class[])} when no module descriptors are provided.
//   * If module descriptors are given then use {@link #createModules(Class[], Map)} instead. 
//   * 
//   * @effects 
//   *  create a default main module and add it to {@link #schema}
//   * @version 2.7.3
//   */
//  public void createDefaultMainModule() throws DataSourceException,
//    NotFoundException, NotPossibleException, NotImplementedException {
//    
//    DSM dsm = schema.getDsm();
//    
//    
//    
//  }

// v3.2: moved to SetUpConfigTool
//  /**
//   * This method is used when no module descriptors are provided.
//   * If module descriptors are given then use {@link #createModules(Class[], Map)} instead.
//   *  
//   * @requires 
//   *  length(modelClasses) > 0 /\ for all c in modelClasses. c is a domain class  
//   * @effects <pre>
//   *  create a default main application module
//   *  add main module to {@link #dodm}
//   *  
//   *  for each domain class c in modelClasses
//   *    create an application module m from the attribute specification of c
//   *    if c is abstract the m has no GUI and no tool menu item 
//   *    add m to {@link #dodm}</pre>
//   */
//  public void createModulesFromClasses(Class[] modelClasses,
//      boolean serialisedConfig  // v2.8
//      ) throws DataSourceException,
//  NotFoundException, NotPossibleException, NotImplementedException {
//    // create the module descriptors for the model classes and use them
//    
//    DSMBasic dsm = dodm.getDsm();
//    ModuleDescriptorGenerator mgen;
//
//    Collection<ModuleDescriptorGenerator> moduleDescGens = new ArrayList<>();
//    
//    // the main module
//    ModuleDescriptor mainModuleDesc  = dsm.getModuleDescriptorObject(Main.class);
//    mgen = new ModuleDescriptorGenerator(dsm,
//        "Module: main", 
//        mainModuleDesc.type(), 
//        null  // no domain class
//        );
//    moduleDescGens.add(mgen);
//    
//    // the functional modules
//    ModuleDescriptor funcModuleDesc = dsm.getModuleDescriptorObject(FunctionalModule.class);
//    Class domainCls;
//    for (int i = 0; i < modelClasses.length; i++) {
//      domainCls =  modelClasses[i];
//       mgen = new ModuleDescriptorGenerator(dsm,
//           "Module: " + domainCls.getSimpleName(), 
//           funcModuleDesc.type(),
//           domainCls);
//       moduleDescGens.add(mgen); 
//    }
//
//    // TODO: sort the module descriptors
//    /**
//    if (debug) {
//      log(MessageCode.UNDEFINED,"Modules:\n{0}", toString(moduleDescrs));
//    }
//    
//    Class[] sorted = sortModulesByReverseDependency(moduleDescrs);
//
//    if (debug) {
//      log(MessageCode.UNDEFINED,"Sorted:\n {0}", toString(sorted));
//    }
//    */
//    
//    /**
//     * <pre>
//     *  for each domain class c in domainClasses
//     *    creates a module object for c
//     *    creates GUI regions for c
//     *    create component regions for c
//     * 
//     * </pre>
//     */
//
//    String moduleName;
//    ModuleDescriptor moduleCfg;
//    
//    Class controller;
//    Type type;
//    RegionName parentName;
//    ApplicationModule module;
//    ControllerConfig ctlCfg;  // v2.6.4.b
//    ModelConfig modelCfg;   // v2.7
//    ApplicationModule[] childModules; // v2.7.2
//
//    int displayOrder = 1;
//    boolean isAbstract = false;
//
//    boolean serialised; // v2.8
//    //Map<String,Label> labelMap = null;
//    
//    DOMBasic dom = dodm.getDom();
//    
//    for (ModuleDescriptorGenerator moduleDescGen: moduleDescGens) {
//      moduleCfg = moduleDescGen.isType(ModuleType.DomainMain) ? 
//          mainModuleDesc : funcModuleDesc;
//      
//      module = null;
//      
//      if (moduleCfg != null) {
//        moduleName = moduleDescGen.getModuleName(); //moduleCfg.name();
//        domainCls = moduleDescGen.getDomainClass(); //moduleCfg.modelDesc().model();
//        
//        isAbstract = (domainCls != null) ? DSMBasic.isAbstract(domainCls) : false;
//        
//        controller = moduleCfg.controllerDesc().controller();
//        
//        if (!serialisedConfig)  // use config-wise setting
//          serialised = false;
//        else  // use module-specific setting (if config-wise setting is set to true)
//          serialised = !moduleCfg.isMemoryBased();
//        
//        type = moduleCfg.viewDesc().viewType();
//        parentName = moduleCfg.viewDesc().parent();
//        
//        if (debug) 
//          log(MessageCode.UNDEFINED,"\nProcessing configuration:\n controller({0})\n moduleName({1})\n guiType({2})\n domain class({3})", controller, moduleName, type, domainCls);
//        
//        /** MODULE **/
//        if (debug)
//          System.out.format("Creating module configuration for %s(%s)%n",
//              moduleName, (domainCls != null) ? domainCls.getName() : "");
//
//        // the module object
//        if (controller == MetaConstants.NullType){ //ModuleDescriptor.NullType) {
//          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_MODULE,
//              "Could not create module {0}", moduleName);
//        }
//        
//        /** MENU ITEM REGION **/
//        RegionToolMenuItem menuItemRegion = null;
//        if (!isAbstract && parentName != RegionName.Null) {
//          // REQUIRES: region name is unique
//          if (debug)
//            System.out.format("Creating menu item for %s(parent: %s)%n", moduleName,
//              parentName);
//
//          menuItemRegion = createToolMenuGUIRegion(moduleCfg, moduleDescGen, displayOrder, serialised);
//        }
//
//        /** GUI REGION **/
//        /*v2.7.2: create GUI region even if there is no gui class
//         * The fact that a gui region that has no GUI class will not have an AppGUI created 
//         * will be determined by the ApplicationLauncher 
//         */
//        // create gui region if domain class is non-abstract
//        RegionGui regionGui = null;
//        boolean hasMenuItem = (menuItemRegion != null);
//        if (!isAbstract ) {
//          regionGui = createGUIConfig(config,moduleCfg, moduleDescGen, hasMenuItem, serialised);
//        }
//        
//        /** APPLICATION MODULE */
//        // create model config if domain class is specified
//        modelCfg = null;
//        if (domainCls != null) {
//          PropertySet modelProps = null;
//          modelCfg = ModelConfig.createInstance(moduleCfg.modelDesc(), modelProps);
//          modelCfg.setDomainClassCls(domainCls);
//          dom.addObject(modelCfg, serialised);
//        }
//        
//        // v2.7.2: support module without controller
//        if (moduleCfg.controllerDesc().on()) {
//          /*v2.7.4: moved to method 
//          ctlCfg = ControllerConfig.createInstance(moduleCfg.controllerDesc());
//          dodm.getDom().addObject(ctlCfg);
//          */
//          ctlCfg = createControllerConfig(dodm, moduleCfg.controllerDesc(), serialised);
//        } else {  // no controller config
//          ctlCfg = null;
//        }
//        
//        // v2.7.2: if this has child modules then look them up 
//        childModules = null;
//
//        // v2.7.2: support print config
//        PropertySet printConfig = null;
//        
//        // v3.0: containment tree not supported by this method
//        Tree contTreeObj = null;
//        
//        module = ApplicationModule.createInstance(moduleCfg, config, modelCfg, regionGui, menuItemRegion, ctlCfg, printConfig, childModules, contTreeObj);
//        module.setName(moduleName);
//        dom.addObject(module, serialised);
//
//        if (modelCfg != null) 
//          modelCfg.setApplicationModule(module);
//        
//        if (regionGui != null)
//          regionGui.setApplicationModule(module);
//        if (menuItemRegion != null)
//          menuItemRegion.setApplicationModule(module);
//        
//        // update ctlCfg to point to module (must do this afterwards b/c of the two-way associations)
//        if (ctlCfg != null) 
//          ctlCfg.setApplicationModule(module);
//
////        if (printConfig != null)
////          printConfig.setAppModule(module);
//        
//        // add module to configuration
//        config.addApplicationModule(module);
//        
//        ///// v3.0: moved out to be performed later
//        /** components region
//         * Note: a module may not have a GUI but still has a components region.
//         * Such region is used to create linking (nested) data containers. 
//         *  */
//        if (domainCls != null) {
//          // the components region
//          String regionName = moduleName; 
//
//          Style style = (regionGui != null) ? regionGui.getStyle() : 
//            getModuleStyle(moduleCfg);
//          
//          Region comRegion = createComponentRegions(dodm, 
//              config, 
//              regionGui,  // v2.7.4
//              regionName, domainCls,
//              moduleDescGen, type, style, serialised);
//
//          addRegion(dodm, comRegion, true, serialised);
//        }
//        
//        displayOrder++;
//      } 
//      
//      // v2.7.2: add module to map
////      if (module != null)
////        moduleMap.put(moduleDescrCls, module);
//      
//    } // end for loop
//  }

  /**
   * @effects create a program module configuration for each of the module in
   *          <tt>moduleCfgSettings</tt>
   * @version
   * - 4.0: change return type to {@link ApplicationModule}<br>
   * - 5.2: support ApplicationModule.props 
   */
  public Collection<ApplicationModule> createModules(SetUpBasic su, Class[] moduleDescriptors, 
      Map<String,Label> labelMap
      ,boolean serialisedConfig  // v2.8
      ) throws DataSourceException,
      NotFoundException, NotPossibleException, NotImplementedException {

    if (debug) {
      log(MessageCode.UNDEFINED,"Modules:\n{0}", toString(moduleDescriptors));
    }
    
    /**
     * first: sort the moduleCfgs based on the domain class dependency: the
     * independent modules first, then the modules that depend on them.
     * 
     * class A depends on class B if A contains a collection-type attribute,
     * whose element type is B. For example: StudentClass depends on Student,
     * because it contains a collection-type attribute StudentClass.students
     * (which records all the students belonging to that class), each element of
     * which is of type Student.
     */
    /*v3.0: remove sorting 
    Class[] sorted = sortModulesByReverseDependency(moduleDescriptors);

    if (debug) {
      log(MessageCode.UNDEFINED,"Sorted:\n {0}", toString(sorted));
    }
    */
    
    /**
     * <pre>
     *  for each domain class c in domainClasses
     *    creates a module object for c
     *    creates GUI regions for c
     *    create component regions for c
     * 
     * </pre>
     */

    String moduleName;
    LAName action;
    Class domainCls;
    //Class viewCls;
    ModuleDescriptor moduleCfg;
    Class controller;
    Class dataController;
    // String labelStr;
    String imageIcon;
    RegionType type;
    Class guiClass;
    RegionName parentName;
    Region parent;
    RegionName[] children;
    ApplicationModule module;
    ControllerConfig ctlCfg;  // v2.6.4.b
    ModelConfig modelCfg;   // v2.7
    RegionGui viewCfg;    // v2.7
    LAName defCommand;
    boolean isStateListener;
    boolean viewer;
    boolean primary;
    Class[] childModuleDescClasses; // v2.7.2
    ApplicationModule[] childModules; // v2.7.2

    boolean serialised;  // v2.7.4
    RegionGui regionGui;
    
    DOMBasic dom =  dodm.getDom();

    // v3.0: used to keep track of modules created so that they can be processed later to 
    // create components Region
    Map<Class,ApplicationModule> myModuleMap = new LinkedHashMap<>();
    
    // v5.2: support other regions
    Map<Region, Map<ModuleDescriptor, Region>> otherRegionsMap = getOtherRegionsMap(); // v5.2

    int displayOrder = 1;
    for (Class moduleDescrCls : 
      //v3.0: sorted
        moduleDescriptors
        ) {
      moduleCfg = (ModuleDescriptor) moduleDescrCls.getAnnotation(MD);
      
      module = null;
      
      if (moduleCfg != null) {
        moduleName = moduleCfg.name();
        domainCls = moduleCfg.modelDesc().model();
        if (domainCls == CommonConstants.NullType) //ModuleDescriptor.NullType)
          domainCls = null;
        
        controller = moduleCfg.controllerDesc().controller();
        dataController = moduleCfg.controllerDesc().dataController();
        
        // labelStr = moduleCfg.label();
        imageIcon = moduleCfg.viewDesc().imageIcon();
        type = moduleCfg.viewDesc().viewType();
        guiClass = moduleCfg.viewDesc().view();
        parentName = moduleCfg.viewDesc().parent();
        children = moduleCfg.viewDesc().children();
        defCommand = moduleCfg.controllerDesc().defaultCommand();
        isStateListener = moduleCfg.controllerDesc().isStateListener();
        viewer = moduleCfg.isViewer();
        primary = moduleCfg.isPrimary();
        
        if (!serialisedConfig)  // use config-wise setting
          serialised = false;
        else  // use module-specific setting (if config-wise setting is set to true)
          serialised = !moduleCfg.isMemoryBased();
        
        if (debug) 
          log(MessageCode.UNDEFINED,"\nProcessing configuration:\n controller({0})\n moduleName({1})\n guiType({2})\n domain class({3})", controller, moduleName, type, domainCls);
        
        /** MODULE **/
        if (debug)
          System.out.format("Creating module configuration for %s(%s)%n",
              moduleName, (domainCls != null) ? domainCls.getName() : "");

        regionGui = null;
        if (controller == CommonConstants.NullType){ //ModuleDescriptor.NullType) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_MODULE,
              "Could not create module {0}", moduleName);
        }
        
        /** MENU ITEM REGION **/
        /**
         * <pre>
         * if module has not been created
         *  Create a gui region representing the AppGui of the module
         *  if nameToLabels != null
         *    create a child region of Components and make it the child of the GUI region
         * Create a tool menu item representing the menu item of the gui object
         * </pre>
         */
        RegionToolMenuItem menuItemRegion = null;
        if (parentName != RegionName.Null) {
          // REQUIRES: region name is unique
          if (debug)
            System.out.format("Creating menu item for %s(parent: %s)%n", moduleName,
              parentName);

          menuItemRegion = createToolMenuGUIRegion(
               //v2.7: module, 
               moduleCfg, labelMap, displayOrder,
               serialised
              );
        }

        /** GUI REGION **/
        /*v2.7.2: create GUI region even if there is no gui class
         * The fact that a gui region that has no GUI class will not have an AppGUI created 
         * will be determined by the ApplicationLauncher 
         *
         */
        // create gui region
        boolean hasMenuItem = (menuItemRegion != null);
        /*v3.1: updated to create regionGUI always: because some settings (e.g. domain class label)
         * requires that it is created
         */
        regionGui = createGUIConfig(
            config, 
            moduleCfg, 
            // v2.7.3: moduleDescrCls, 
            labelMap, hasMenuItem,
            serialised
            );
        
        /** APPLICATION MODULE */
        // v2.7
        // create model config if domain class is specified
        modelCfg = null;
        if (domainCls != null) {
          modelCfg = createModelConfig(dodm, moduleCfg.modelDesc(), serialised);
        }
        
        // v2.7.2: support module without controller
        if (moduleCfg.controllerDesc().on()) {
          ctlCfg = createControllerConfig(dodm, moduleCfg.controllerDesc(), serialised);
        } else {  // no controller config
          ctlCfg = null;
        }
        
        // v2.7.2: if this has child modules then look them up 
        childModuleDescClasses = moduleCfg.childModules();
        // validate module: composite module must have at least one child module
        if (ctlCfg != null && ctlCfg.isComposite()) {
          if (childModuleDescClasses.length == 0) {
            /*v2.7.3: remove this restriction b/c some modules (e.g. ModuleImport) does not 
             * require any module to be its child (it is composite w.r.t to its own operations)
            throw new NotPossibleException(NotPossibleException.Code.MODULE_HAS_NO_CHILDREN,
                "Mô-đun không được cấu hình với các mô-đun con: {0}", moduleName); 
             */
            childModules = null;
          }
          else
            childModules = lookUpModules(childModuleDescClasses);
        } else {
          childModules = null;
        }
        
        /*v5.2: to allow any module with @ModuleDesc.childModules specified to have the specified child modules 
        if (childModuleDescClasses.length > 0) {
          childModules = lookUpModules(childModuleDescClasses);
        } else {
          childModules = null;
        }
        */
        
        // v2.7.2: support print config
        PropertySet printConfig = PropertySetFactory.createPrintConfigPropertySet(dodm, 
            moduleName, 
            moduleDescrCls);
        
        if (printConfig != null) {
          // v3.0: createPropertySet(dodm, printConfig, serialised, 0);
          PropertySetFactory.createPropertySet(dodm, printConfig, serialised);
        }
        
        //v3.0: if @ContainmentTree is specified then convert it to a Tree object and 
        // set this into the application module
        CTree contTreeSpec = moduleCfg.containmentTree();
        Tree contTreeObj = null;
        if (contTreeSpec.root() != CommonConstants.NullType) {
          // containment tree is specified
          contTreeObj = createContainmentTree(contTreeSpec, config, labelMap, serialisedConfig);
        }
        
        // v5.2: support ApplicationModule.props
//        module = ApplicationModule.createInstance(moduleCfg, config, modelCfg, regionGui, menuItemRegion, 
//            ctlCfg, printConfig, childModules, contTreeObj);

        module = createModuleConfig(moduleCfg, config, modelCfg, regionGui, menuItemRegion, 
          ctlCfg, printConfig, childModules, contTreeObj, serialised);
        // end v5.2
        
        dom.addObject(module, serialised);

        if (modelCfg != null) 
          modelCfg.setApplicationModule(module);
        
        if (regionGui != null)
          regionGui.setApplicationModule(module);
        if (menuItemRegion != null)
          menuItemRegion.setApplicationModule(module);
        
        // update ctlCfg to point to module (must do this afterwards b/c of the two-way associations)
        if (ctlCfg != null) 
          ctlCfg.setApplicationModule(module);
        //schema.getDom().updateObject(ctlCfg, null);

        if (printConfig != null)
          printConfig.setAppModule(module);
        
        // add module to configuration
        config.addApplicationModule(module);
        
        // v2.7.2: if there are child modules then save the mappings
        if (childModules != null) {
          Collection<ApplicationModuleMap> childModulesMap = module.getChildModulesMap();
          for (ApplicationModuleMap modMap : childModulesMap) {
            if (debug)
              System.out.format("...Saving module map %s%n", modMap);
            dom.addObject(modMap, serialised);
          }
        }
        
        displayOrder++;
      } // end if: moduleCfg 
      
      // v2.7.2: add module to map
      if (module != null) {
        moduleMap.put(moduleDescrCls, module);
        
        // v3.0
        myModuleMap.put(moduleDescrCls, module);
      }
    
    } // end for loop: moduleCfg
    
    /*** v3.0: POST-PROCESSING THE MODULES **/ 
    //Class moduleDescrCls;
    
    if (debug)
      System.out.println("\nPost-processing the modules");
    
    // Pass 1: create components region (without the linked regions)
    
    // a properties map: Field (the field) -> Map (the linked region properties of the field)
    /*v3.2: FIXED bug in not able to store mappings for same Field when this Field is used in multiple comps region (e.g. sub-type and super-type modules) 
     * - changed to List of Maps and add all the necessary details (including the Field) in the Map 
    Map<Field,Map> linkedRegionMap = new LinkedHashMap();
    */
    List<Map> linkedRegionMap = new ArrayList();
    
    Map<ModuleDescriptor,Region> compRegions = new HashMap<>();
    
    /** COMPONENTS REGION: Pass 1 - without linked regions 
     * @version 
     * - 5.2: added support for side-panel region
     * */
    if (debug)
      System.out.println("\nCreating components region: Pass #1");

    createComponentRegionsPass1(myModuleMap, labelMap, linkedRegionMap, 
        compRegions,
        otherRegionsMap,
        serialisedConfig);
    /* v5.2: moved to method */
//    for (Entry<Class,ApplicationModule> entry : myModuleMap.entrySet()) {
//      Class mcc = entry.getKey();
//      moduleCfg = (ModuleDescriptor) mcc.getAnnotation(MD);
//      module = entry.getValue();
//      
//      domainCls = module.getDomainClassCls();
//      /* v5.1: moved to method createComponentRegionsWithoutLinkings
//      regionGui = module.getViewCfg();
//      type = moduleCfg.viewDesc().viewType();
//      */
//      
//      /** components region
//       * Note: a module may not have a GUI but still has a components region.
//       * Such region is used to create linking (nested) data containers. 
//       *  */
//      if (domainCls != null) {
//        // the components region
//        /* v5.1: moved to method createComponentRegionsWithoutLinkings
//        String regionName = module.getName();  
//        if (debug)
//          System.out.printf("...%s%n", regionName);
//        
//        Style style = (regionGui != null) ? regionGui.getStyle() :  getModuleStyle(moduleCfg);
//        */
//        
//        if (!serialisedConfig)  // use config-wise setting
//          serialised = false;
//        else  // use module-specific setting (if config-wise setting is set to true)
//          serialised = !moduleCfg.isMemoryBased();
//        
//        Region comRegion = createComponentRegionsWithoutLinkings(dodm, 
//            config, 
//            /* v5.1: narrow the interface
//            regionGui,  // v2.7.4
//            regionName, domainCls,
//            moduleDescrCls, type, 
//            style, 
//            */
//            mcc, module,
//            labelMap
//            , linkedRegionMap // v3.0
//            , serialised);
//
//        compRegions.put(moduleCfg, comRegion);
//      }
//    } // end pass 1
    // end v5.2
    
    /** COMPONENTS REGION: Pass 2 - the linked regions */
    if (debug)
      System.out.println("\nCreating components region: Pass #2");
    
    /*v3.2:
     * - improved to support sub-type links
     * - use List<Map> rather than Map
      for (Entry<Field,Map> entry : linkedRegionMap.entrySet()) {
        createLinkedRegion(entry.getKey(), entry.getValue(), compRegions.values());
      }
     */
    /*v5.2: moved to method */
    createComponentRegionsPass2(compRegions, linkedRegionMap, otherRegionsMap, serialisedConfig);
    
//    // v5.1: create a var for compRegions.values
//    Collection<Region> compRegionObjs = compRegions.values();
//    
//    for (Map linkedRegionProps : linkedRegionMap) {
//      createLinkedRegionWithSubTypeSupport(linkedRegionProps, compRegionObjs //compRegions.values()
//          );
//    }
//    
//    // commit component regions to data source
//    Region comRegion;
//    if (debug)
//      System.out.println("\nSaving components regions:");
//    
//    // first: add each com region (on its own)
//    for (Entry<ModuleDescriptor,Region> entry : compRegions.entrySet()) {
//      moduleCfg = entry.getKey();
//      comRegion = entry.getValue();
//      
//      if (!serialisedConfig)  // use config-wise setting
//        serialised = false;
//      else  // use module-specific setting (if config-wise setting is set to true)
//        serialised = !moduleCfg.isMemoryBased();
//      
//      if (debug)
//        System.out.printf("...%s%n", comRegion.getName());
//      
//      // add just the region first
//      addRegion(dodm, comRegion, serialised);
//    }
//    
//    // second: add children of each component region
//    if (debug) System.out.println();
//    
//    for (Entry<ModuleDescriptor,Region> entry : compRegions.entrySet()) {
//      moduleCfg = entry.getKey();
//      comRegion = entry.getValue();
//      
//      if (!serialisedConfig)  // use config-wise setting
//        serialised = false;
//      else  // use module-specific setting (if config-wise setting is set to true)
//        serialised = !moduleCfg.isMemoryBased();
//      
//      if (debug)
//        System.out.printf("...%s%n", comRegion.getName());
//
//      // add the region's children
//      addRegionChildren(dodm, comRegion, serialised);
//    }
//    
//    // third: add parents, exclusion
//    if (debug)
//      System.out.println();
//    for (Entry<ModuleDescriptor,Region> entry : compRegions.entrySet()) {
//      moduleCfg = entry.getKey();
//      comRegion = entry.getValue();
//      
//      if (!serialisedConfig)  // use config-wise setting
//        serialised = false;
//      else  // use module-specific setting (if config-wise setting is set to true)
//        serialised = !moduleCfg.isMemoryBased();
//      
//      if (debug)
//        System.out.printf("...%s%n", comRegion.getName());
//
//      // add the region's parents
//      addRegionParents(dodm, comRegion, serialised);
//      
//      // add the exclusions (if any)
//      addRegionExclusion(dodm, comRegion, serialised);
//    }
    // end v5.2: 
    
    /**POST-SETUP: for each module */
    if (su.isPostSetUpOn()) { /*v3.1: added support for option to turn off post-setup */
      if (debug)
        System.out.printf("\nPost set-up:%n");
      
      for (Entry<Class,ApplicationModule> entry : myModuleMap.entrySet()) {
        Class moduleDescrCls = entry.getKey();
        moduleCfg = (ModuleDescriptor) moduleDescrCls.getAnnotation(MD);
        
        // post setup the module
        if (debug)
          System.out.printf("...%s%n", moduleCfg.name());
  
        postSetUpModule(su, moduleDescrCls, moduleCfg);
      } // end post-setup
    }
    
    // v4.0:
    if (myModuleMap.isEmpty())
      return null;
    else
      return myModuleMap.values();
  }
  

  /**
   * @effects
   *  for each supported Region, initialise it with a <tt>null</tt> Map in result.
   *  
   * @version 5.2
   * @param myModuleMap 
   */
  protected Map<Region, Map<ModuleDescriptor, Region>> getOtherRegionsMap() {
    // for sub-types to support 
    return null;
  }

  /**
   * @effects 
   *  create and return an {@link ApplicationModule} for <tt>moduleCfg</tt> using other specified parameters.
   *   
   * @version 5.2
   */
  protected ApplicationModule createModuleConfig(ModuleDescriptor moduleCfg, 
      Configuration config, ModelConfig modelCfg, RegionGui regionGui, 
      RegionToolMenuItem menuItemRegion, ControllerConfig ctlCfg, 
      PropertySet printConfig, ApplicationModule[] childModules, 
      Tree contTreeObj, final boolean serialised) throws DataSourceException {
    
    ApplicationModule module = ApplicationModule.createInstance(moduleCfg, config, modelCfg, regionGui, menuItemRegion, 
        ctlCfg, printConfig, childModules, contTreeObj);
    
    return module;
  }

  /**
   * @modifies <tt>compRegions</tt>
   * @effects 
   *  for each module in <tt>moduleMap</tt>, create a Components region for it and add
   *  to <tt>compRegions</tt>
   *  
   * @version 5.2
   */
  protected void createComponentRegionsPass1(final Map<Class, ApplicationModule> myModuleMap,
      final Map<String, Label> labelMap, final List<Map> linkedRegionMap, 
      final Map<ModuleDescriptor, Region> compRegions,
      Map<Region, Map<ModuleDescriptor,Region>> otherRegionsMap, // v5.2 
      boolean serialisedConfig) throws NotFoundException, DataSourceException {
    
    for (Entry<Class,ApplicationModule> entry : myModuleMap.entrySet()) {
      Class mcc = entry.getKey();
      ModuleDescriptor moduleCfg = (ModuleDescriptor) mcc.getAnnotation(MD);
      ApplicationModule module = entry.getValue();
      
      Class domainCls = module.getDomainClassCls();
      /* v5.1: moved to method createComponentRegionsWithoutLinkings
      regionGui = module.getViewCfg();
      type = moduleCfg.viewDesc().viewType();
      */
      
      /** components region
       * Note: a module may not have a GUI but still has a components region.
       * Such region is used to create linking (nested) data containers. 
       */
      boolean serialised;
      if (domainCls != null) {
        // the components region
        /* v5.1: moved to method createComponentRegionsWithoutLinkings
        String regionName = module.getName();  
        if (debug)
          System.out.printf("...%s%n", regionName);
        
        Style style = (regionGui != null) ? regionGui.getStyle() :  getModuleStyle(moduleCfg);
        */
        
        if (!serialisedConfig)  // use config-wise setting
          serialised = false;
        else  // use module-specific setting (if config-wise setting is set to true)
          serialised = !moduleCfg.isMemoryBased();
        
        Region comRegion = createComponentRegionsWithoutLinkings(dodm, 
            config, 
            /* v5.1: narrow the interface
            regionGui,  // v2.7.4
            regionName, domainCls,
            moduleDescrCls, type, 
            style, 
            */
            mcc, module,
            labelMap
            , linkedRegionMap // v3.0
            , serialised);

        compRegions.put(moduleCfg, comRegion);
      }
      
      // v5.2: support other related regions (e.g. side panel)
      if (otherRegionsMap != null) {
        createOtherDataRegions(module, moduleCfg, otherRegionsMap, serialisedConfig);
      }
    } // end pass 1
  }

  /**
   * @effects 
   *  update each Components region in <tt>compRegions</tt> and store them to the data source.
   *  
   * @version 5.2
   */
  private void createComponentRegionsPass2(
    final Map<ModuleDescriptor, Region> compRegions, final List<Map> linkedRegionMap,
    final Map<Region, Map<ModuleDescriptor,Region>> otherRegionsMap, // v5.2 
    final boolean serialisedConfig) throws DataSourceException {
    // v5.1: create a var for compRegions.values
    Collection<Region> compRegionObjs = compRegions.values();
    
    for (Map linkedRegionProps : linkedRegionMap) {
      createLinkedRegionWithSubTypeSupport(linkedRegionProps, compRegionObjs //compRegions.values()
          );
    }
    
    // commit component regions to data source
    //Region comRegion;
    if (debug)
      System.out.println("\nSaving components regions:");
    
    // first: add each com region (on its own)
    ModuleDescriptor moduleCfg;
    Region comRegion;
    boolean serialised;
    for (Entry<ModuleDescriptor,Region> entry : compRegions.entrySet()) {
      moduleCfg = entry.getKey();
      comRegion = entry.getValue();
      
      if (!serialisedConfig)  // use config-wise setting
        serialised = false;
      else  // use module-specific setting (if config-wise setting is set to true)
        serialised = !moduleCfg.isMemoryBased();
      
      if (debug)
        System.out.printf("...%s%n", comRegion.getName());
      
      // add just the region first
      addRegion(dodm, comRegion, serialised);
    }
    
    // second: add children of each component region
    if (debug) System.out.println();
    
    for (Entry<ModuleDescriptor,Region> entry : compRegions.entrySet()) {
      moduleCfg = entry.getKey();
      comRegion = entry.getValue();
      
      if (!serialisedConfig)  // use config-wise setting
        serialised = false;
      else  // use module-specific setting (if config-wise setting is set to true)
        serialised = !moduleCfg.isMemoryBased();
      
      if (debug)
        System.out.printf("...%s%n", comRegion.getName());
  
      // add the region's children
      addRegionChildren(dodm, comRegion, serialised);
    }
    
    // third: add parents, exclusion
    if (debug)
      System.out.println();
    for (Entry<ModuleDescriptor,Region> entry : compRegions.entrySet()) {
      moduleCfg = entry.getKey();
      comRegion = entry.getValue();
      
      if (!serialisedConfig)  // use config-wise setting
        serialised = false;
      else  // use module-specific setting (if config-wise setting is set to true)
        serialised = !moduleCfg.isMemoryBased();
      
      if (debug)
        System.out.printf("...%s%n", comRegion.getName());
  
      // add the region's parents
      addRegionParents(dodm, comRegion, serialised);
      
      // add the exclusions (if any)
      addRegionExclusion(dodm, comRegion, serialised);
    }  
    
    // commit other Regions to data source
    // v5.2: support other related regions (e.g. side panel)
    if (otherRegionsMap != null) {
      addOtherRegions(dodm, otherRegionsMap, serialisedConfig);
    }
  } 

  /**
   * @effects 
   *  If there are other regions created then add them to data source
   *  
   * @version 5.2 
   */
  protected void addOtherRegions(DODMBasic dodm, Map<Region, Map<ModuleDescriptor, Region>> otherRegionsMap, boolean serialisedConfig) throws DataSourceException  {
    // TODO for sub-types to implement
  }

  /**
   * @requires <tt>moduleCfg</tt> was used as the base to create <tt>module</tt> 
   * 
   * @effects 
   *  Create other data-related regions (e.g. side-panel) and add them as childen of Region(Data). 
   *    
   * @version 5.2
   */
  protected void createOtherDataRegions(final ApplicationModule module,
      final ModuleDescriptor moduleCfg, final Map<Region, Map<ModuleDescriptor,Region>> otherRegionsMap, final boolean serialisedConfig) throws DataSourceException  {
    // for sub-types to use
  }

  /**
   * @requires 
   *  contTreeSpec != null /\ contTreeSpec is not empty
   *  
   * @effects
   *  if <tt>contTreeSpec</tt> is valid
   *    convert <tt>contTreeSpec</tt> to <tt>Tree</tt> and return it
   *  else
   *    throws NotPossibleException
   *    
   *  <p>Throws DataSourceException if fails to create resources in the underlying data source.
   *   
   * @version 
   * - 3.0: created <br>
   * - 3.2: improved to support {@link ScopeDef} <br>
   * - 5.1: improved to support {@link ScopeDesc}, added 2 parameters: labelMap, config <br>
   * - 5.1c: support the new CTree design
   */
  private Tree createContainmentTree(CTree contTreeSpec, Configuration config, Map<String, Label> labelMap, final boolean serialisedConfig) throws NotPossibleException, DataSourceException {
    // v5.1:
    final String lang = config.getLanguage().getLanguageCode();
    
    Class rootCls = contTreeSpec.root();
    String[] stateScope = contTreeSpec.stateScope();
    String stateScopeStr = toScopeString(stateScope); 
        
    Tree tree = null;
    
    // keep track of the nodes that are created
    Map<Class,Node> nodeMap = new HashMap<>();
    
    // root node
    Node root = new Node(rootCls.getName(), stateScopeStr);
    nodeMap.put(rootCls, root);
    
    // create the tree
    tree = new Tree(root);
    
    if (contTreeSpec.edges().length > 0) {
      // case (1) (preferred, new method): use CEdges
      createCustomContainmentFromEdges(contTreeSpec.edges(), tree, nodeMap, labelMap, lang, serialisedConfig);
    } else {
      // case (2) (old, obsolete method): use SubTree1Ls 
      createCustomContainmentFromSubTrees(contTreeSpec.subtrees(), tree, nodeMap, labelMap, lang, serialisedConfig);
    }
    
    return tree;
  }

  /**
   * @effects 
   *  populate <tt>tree</tt> with {@link Node}s and {@link Edge}s that are created from <tt>edges</tt>.
   *  
   *  <p>Throws NotPossibleException if fails to do so, {@link DataSourceException} if fails to create 
   *  data source object for the linked region corresponding to a ScopeDesc.
   * 
   * @version 5.1c
   */
  private void createCustomContainmentFromEdges(CEdge[] edges, Tree tree,
      Map<Class, Node> nodeMap, Map<String, Label> labelMap, String lang,
      boolean serialisedConfig) throws NotPossibleException, DataSourceException {
    // process the edges
    for (CEdge cedge : edges) {
      Class parentCls = cedge.parent();
      Class childCls = cedge.child();
      
      Node parent = nodeMap.get(parentCls);
      if (parent == null) {
        // parent is not yet created: create it
        //throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"Parent is not one of the existing node: "+ parentCls.getName()});
        parent = new Node(parentCls.getName());
        nodeMap.put(parentCls, parent);
      }
      
      // each child results in a new node and edge
      ScopeDesc scopeDesc = cedge.scopeDesc();
      String[] scope = scopeDesc.stateScope();

      // scope must be specified 
      if (scope.length == 0) {
        // invalid: scope is empty
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"Scope is empty for: (parent,child) = ("+parentCls.getName()+","+childCls.getName()+")"});
      } else if (scope.length==1 && scope[0].trim().equals("")) {
        // invalid: scope contains a single empty string
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"Scope is an empty string ('') for: (parent,child) = ("+parentCls.getName()+","+childCls.getName()+")"}); 
      }
      
      /* support scopeDesc for customisation */
      String scopeStr = toScopeString(scope);
      if (scopeDesc != null && !ModuleToolkit.isDefaultScopeDef(scopeDesc)) { 
        // yes, scopeDesc. Create a RegionLinking object for scopeDesc, 
        // NOTE: pass the current scopeStr value to this object as input (b/c scopeStr will be changed below)
        RegionLinking rl = createLinkedRegionForScopeDesc(childCls, scopeStr, scopeDesc, labelMap, lang, serialisedConfig);
        // change scopeStr to rl.id      
        scopeStr = rl.getFormalObjId();
      }
              
      Node child = nodeMap.get(childCls);
      if (child == null) {
        // child not yet created
        child = new Node(childCls.getName());
      }
      
      boolean nodeAdded = tree.addNodeFlex(child, parent, scopeStr);
      
      if (!nodeAdded) {
        // something wrong: could not add this child node
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {String.format("Failed to add child node (%s) to parent (%s) using scope: %s",childCls.getSimpleName(), parentCls.getSimpleName(), scopeStr)});
      }
      
      nodeMap.put(childCls, child);
    }        
  }

  /**
   * This is an old, obsolete method. 
   * @effects 
   *  populate <tt>tree</tt> with {@link Node}s and {@link Edge}s that are created from <tt>subTrees</tt>.
   *  
   *  <p>Throws NotPossibleException if fails to do so, {@link DataSourceException} if fails to create 
   *  data source object for the linked region corresponding to a ScopeDesc.
   *  
   * @version 5.1c
   * @deprecated by {@link #createCustomContainmentFromEdges(CEdge[], Tree, Map, Map, String, boolean)}
   */
  private void createCustomContainmentFromSubTrees(SubTree1L[] subTrees,
      Tree tree, Map<Class, Node> nodeMap, Map<String, Label> labelMap,
      String lang, boolean serialisedConfig) throws NotPossibleException, DataSourceException {
    // process the sub-trees
    // Class parentCls, childCls;
    //Node parent, child;
    Child[] children;
    //String[] scope;
    //String scopeDef;  // v3.2
    //  boolean nodeAdded;
    for (SubTree1L subTree : subTrees) {
      Class parentCls = subTree.parent();
      children = subTree.children();
      
      if (children.length == 0) {
        // invalid: no children specified
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"No children of parent: " + parentCls.getName()});
      }

      Node parent = nodeMap.get(parentCls);
      if (parent == null) {
        // invalid: parent is not one of the existing nodes
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"Parent is not one of the existing node: "+ parentCls.getName()});
      }
      
      // each child results in a new node and edge
      for (Child childSpec : children) {
        Class childCls = childSpec.cname();
        String scopeDef = childSpec.scopeDef();
        /*v5.1: support scopeDesc() */
        ScopeDesc scopeDesc = null;
        String[] scope;
        if (!scopeDef.equals(CommonConstants.NullString)) {
          // has scopeDef, ignore scope()
          scope = new String[] { scopeDef };
        } else {
          // no scopeDef, use scope()
          scope = childSpec.scope();
          scopeDesc = childSpec.scopeDesc();
        }
        
        // scope must be specified AND valid
        if (scope.length == 0) {
          // invalid: scope is empty
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"Scope is empty for: (parent,child) = ("+parentCls.getName()+","+childCls.getName()+")"});
        } else if (scope.length==1 && scope[0].trim().equals("")) {
          // invalid: scope contains a single empty string
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"Scope is an empty string ('') for: (parent,child) = ("+parentCls.getName()+","+childCls.getName()+")"}); 
        }
        
        /* v5.1: support scopeDesc */
        String scopeStr = toScopeString(scope);
        if (scopeDesc != null && !ModuleToolkit.isDefaultScopeDef(scopeDesc)) { // yes, scopeDesc. Create a RegionLinking object for scopeDesc
          RegionLinking rl = createLinkedRegionForScopeDesc(childCls, scopeStr, scopeDesc, labelMap, lang, serialisedConfig);
          // change scopeStr to rl.id      
          scopeStr = rl.getFormalObjId();
        } 
                
        Node child = new Node(childCls.getName());
        
        boolean nodeAdded = tree.addNode(child, parent, scopeStr);
        
        if (!nodeAdded) {
          // something wrong: could not add this child node
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_CONTAINMENT_TREE_FROM_SPEC, new Object[] {"Failed to add child node for parent: "+parentCls.getName()});
        }
        
        nodeMap.put(childCls, child);
      }
    }    
  }

  /**
   * @requires 
   *  scope != null
   * @effects
   *  if <tt>scope.length > 0</tt> AND <tt>scope neq ["*"]</tt> (i.e. contains specific elements)
   *    return a comma-separated string of the scope elements
   *  ; else
   *    return <tt>null</tt> 
   */
  private String toScopeString(String[] scope) {
    if (scope == null || scope.length == 0 || scope[0].equals("*"))
      return null;
    
    // scope is specific
    StringBuffer scopeStr = new StringBuffer();
    scopeStr = new StringBuffer();
    for (String scopeE : scope) scopeStr.append(scopeE).append(",");
    scopeStr.deleteCharAt(scopeStr.length()-1); // the last comma
    
    return scopeStr.toString();
  }

  /**
   * @effects 
   *  create in data source a <tt>ControllerConfig</tt> from <tt>controllerDesc</tt>
   * @version 2.7.4 
   */
  protected ControllerConfig createControllerConfig(DODMBasic dodm,
      ControllerDesc controllerDesc,
      boolean serialised  // v2.8
      ) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    /* v5.0: support property set 
    ControllerConfig ctlCfg = ControllerConfig.createInstance(controllerDesc, null);
    */
    PropertyDesc[] propDescs = controllerDesc.props();
    
    PropertySet pset = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);

    // create controller config
    ControllerConfig ctlCfg = ControllerConfig.createInstance(controllerDesc, pset);
    
    // end 5.0
    
    dom.addObject(ctlCfg, serialised);
    
    return ctlCfg;
  }
  
  /**
   * @effects 
   *  create in data source a <tt>ModelConfig</tt> from <tt>modelDesc</tt>
   * @version 3.0 
   */
  protected ModelConfig createModelConfig(DODMBasic dodm, ModelDesc modelDesc,
      boolean serialised) throws DataSourceException {
    /* A RESTRICTED IMPLEMENTATION:does not support adding ModelDesc.props
     * to Modelconfig
     */
    DOMBasic dom = dodm.getDom();
    
    ModelConfig modelCfg = ModelConfig.createInstance(modelDesc, null);
    
    dom.addObject(modelCfg, serialised);
    
    return modelCfg;
  }
  
  /**
   * @effects
   *  perform post set-setup for a given module 
   *  
   * @version 2.7.4
   */
  protected void postSetUpModule(SetUpBasic su, Class moduleDescrCls, ModuleDescriptor moduleCfg) throws NotPossibleException {
    // for sub-types to implement
  }

  /**
   * @effects
   *  perform post set-setup for the {@link ModuleDescriptor} of <tt>moduleDescCls</tt> 
   *  
   * @version 2.7.4
   */
  public void postSetUpModule(SetUpBasic su, Class moduleDescCls) throws NotPossibleException {
    ModuleDescriptor md = (ModuleDescriptor) moduleDescCls.getAnnotation(MD);
    if (md != null) {
      postSetUpModule(su, moduleDescCls, md);
    }
  }

// v3.0  
//  // v2.7.2
//  protected void createPropertySet(DODMBasic dodm, PropertySet  propSet, 
//      boolean serialised, // v2.8 
//      int gapDistance) throws DataSourceException {
//    DOMBasic dom = dodm.getDom();
//    
//    StringBuffer indent = new StringBuffer();
//    for (int i = 0; i < gapDistance;i++) indent.append(" ");
//    
//    StringBuffer subIndent = new StringBuffer(indent);
//    subIndent.append("  ");
//    
//    gapDistance = gapDistance + 4;
//
//    // add property set to data source
//    if (debug)
//      out.printf("%sProperty set: %s%n", indent, propSet.getName());
//    
//    dom.addObject(propSet, serialised);
//    
//    if (debug)
//      out.printf("%sProperties:%n", indent);
//    Collection<Property> props = propSet.getProps();
//    for (Property p : props) {
//      if (debug)
//        out.printf("%s%s: \"%s\" (%s<%s>)%n", subIndent, 
//          p.getPkey(), p.getValueAsString(), p.getType(), p.getValue());
//      
//      // add property set to data source
//      dom.addObject(p, serialised);
//    }
//
//    Collection<PropertySet> extents = propSet.getExtensions();
//    if (extents != null && !extents.isEmpty()) {
//      if (debug)
//        out.printf("%sExtension(s):%n", subIndent);
//      for (PropertySet pset : extents) {
//        // recursive
//        createPropertySet(dodm, pset, serialised, gapDistance);
//      }
//    }
//  }
  
//  /**
//   * This method was used in an earlier version where the model rather than view 
//   * classes are specified as input for set up.  
//   * 
//   * @effects create a program module configuration for each of the module in
//   *          <tt>moduleCfgSettings</tt>
//   * @deprecated to be replaced by {@link #createModulesByView(Class[])}
//   */
//  public void createModules(Class[] domainClasses) throws DBException,
//      NotFoundException {
//
//    /**
//     * first: sort the moduleCfgs based on the domain class dependency: the
//     * independent modules first, then the modules that depend on them.
//     * 
//     * class A depends on class B if A contains a collection-type attribute,
//     * whose element type is B. For example: StudentClass depends on Student,
//     * because it contains a collection-type attribute StudentClass.students
//     * (which records all the students belonging to that class), each element of
//     * which is of type Student.
//     */
//    Class[] sorted = sortDomainClassesByReverseDependency(domainClasses);
//
//    /**
//     * <pre>
//     *  for each domain class c in domainClasses
//     *    creates a module object for c
//     *    creates GUI regions for c
//     *    create component regions for c
//     * 
//     * </pre>
//     */
//    // keep track of the modules that have been created
//    Stack<Module> buffer = new Stack();
//    boolean created;
//
//    final Class viewGUIClass = ViewGUI.class;
//
//    String guiName;
//    LogicalAction.Name action;
//    // Class domainCls;
//    Class viewCls;
//    ViewGUI guiCfg;
//    Class controller;
//    String labelStr;
//    String imageIcon;
//    Type type;
//    Class guiClass;
//    RegionName parentName;
//    Region parent;
//    RegionName[] children;
//    Module module;
//    int displayOrder = 1;
//    for (Class domainCls : sorted) {
//      viewCls = Toolkit.getViewClass(domainCls);
//      guiCfg = (ViewGUI) viewCls.getAnnotation(viewGUIClass);
//
//      if (guiCfg != null) {
//        guiName = guiCfg.name();
//        action = guiCfg.menuAction();
//        controller = guiCfg.controller();
//        labelStr = guiCfg.label();
//        imageIcon = guiCfg.imageIcon();
//        type = guiCfg.guiType();
//        guiClass = guiCfg.guiClass();
//        parentName = guiCfg.parent();
//        children = guiCfg.children();
//
//        // skip if module has already been created
//        created = false;
//        module = null;
//        for (Module m : buffer) {
//          if (m.getControllerCls() == controller && domainCls != null
//              && m.getDomainClassCls() == domainCls) {
//            created = true;
//            module = m;
//            break;
//          }
//        }
//
//        if (!created) {
//          String moduleName = getModuleName(controller, domainCls, guiName, type);
//          module = createModuleConfig(controller, moduleName, domainCls, type);
//          buffer.push(module);
//        }
//
//        /**
//         * <pre>
//         * if module has not been created
//         *  Create a gui region representing the AppGui of the module
//         *  if nameToLabels != null
//         *    create a child region of Components and make it the child of the GUI region
//         * Create a tool menu item representing the menu item of the gui object
//         * </pre>
//         */
//        // create tool menu item
//        /**
//         * if type != Type.Main then create a corresponding Tools menu item
//         * region
//         */
//        if (parentName != RegionName.Null) {
//          // REQUIRES: region name is unique
//          parent = lookUpRegion(parentName.name());
//          createToolMenuGUIRegion(config, action, labelStr, imageIcon, parent, module,
//              displayOrder);
//        }
//
//        if (type != Type.Null && !created) {
//          // create gui region
//          createGUIConfig(config, module, domainCls, viewCls, guiName, labelStr,
//              imageIcon, type, guiClass, children);
//        }
//
//        displayOrder++;
//      }
//    }
//  }

  private Class[] sortModulesByReverseDependency(Class[] moduleDescriptors) 
  throws NotPossibleException, NotImplementedException {
    /**
     * construct a reverse map between the domain classes and the modules
     * sort the domain classes by reverse dependency
     * use the sorting result to sort the modules:
     *  modules that refer to the same model are placed next to each other 
     *  in the result array 
     */
    // TODO: this is not the most efficient sorting algorithm, but acceptable 
    // as it is only used during set-up
    
    // initialise a map:
    // one domain class may be mapped to several views
    // v2.6.4.b: changed to use one map
    Map<Class,List<Class>> modulesMapList = 
         new LinkedHashMap<Class, // Domain class (arranged in the same order as the modules)
           List<Class>>();        // Module Descriptor Classes that refer to the domain class
    
    Class dc;
    ModuleDescriptor moduleDesc;
    List<Class> modulesList;
    final Class moduleDescAn = ModuleDescriptor.class;
    // populate the mappings
    /*v2.7: put all modules whose model is NullType aside to be added later    
    for (Class moduleDescCls : moduleDescriptors) {
      moduleDesc = (ModuleDescriptor) moduleDescCls.getAnnotation(moduleDescAn);
      if (moduleDesc == null) {
        throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
            "Lớp không được định nghĩa đúng {0} ({1})", moduleDescCls, "no module descriptor configuration");
      }
      dc = moduleDesc.modelDesc().model(); // domain class
      modulesList = modulesMapList.get(dc);
      if (modulesList == null) {
        modulesList = new ArrayList<Class>();
        modulesMapList.put(dc, modulesList);
      }
      modulesList.add(moduleDescCls);
    }
    */
    List<Class> nullModelModules = new ArrayList<Class>();
    //v2.7.2: exclude main module from sorting 
    Class mainDescCls = null;
    
    for (Class moduleDescCls : moduleDescriptors) {
      moduleDesc = (ModuleDescriptor) moduleDescCls.getAnnotation(moduleDescAn);
      if (moduleDesc == null) {
        throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
            "Lớp không được định nghĩa đúng {0} ({1})", moduleDescCls, "no module descriptor configuration");
      }
      dc = moduleDesc.modelDesc().model(); // domain class
      
      if (moduleDesc.type().isType(ModuleType.DomainMain)) {
        // v2.7.2:
        // main module
        mainDescCls = moduleDescCls;
      } else {
        // other modules
        // v2.7: exclude the null-model modules from sorting
        if (dc == CommonConstants.NullType) {
          nullModelModules.add(moduleDescCls);
        } else {
          modulesList = modulesMapList.get(dc);
          if (modulesList == null) {
            modulesList = new ArrayList<Class>();
            modulesMapList.put(dc, modulesList);
          }
          modulesList.add(moduleDescCls);
        }
      }
    }
    
    // sort the domain classes
    Class[] sortedModel = sortDomainClassesByReverseDependency(
        modulesMapList);
    
    // sort the module descriptors based on the above sort
    List<Class> sortedModules = new ArrayList();
    for (Class dcls : sortedModel) {
      // retrieve the module list classes and append them 
      // to sortedViews
      modulesList = modulesMapList.get(dcls);
      sortedModules.addAll(modulesList);
    }
    
    /*v2.7.2: add null-type modules to the back
    // v2.7: add modules with nul-type model to the front (anywhere is fine but this 
    // will help arranging the modules on display)
    if (!nullModelModules.isEmpty()) {
      for (Class moduleDescCls : nullModelModules) sortedModules.add(0, moduleDescCls);
    }*/
    if (!nullModelModules.isEmpty()) {
      for (Class moduleDescCls : nullModelModules) sortedModules.add(moduleDescCls);
    }
    
    //v2.7.2 add main module to the front
    if (mainDescCls != null)
      sortedModules.add(0, mainDescCls);
    
    return sortedModules.toArray(new Class[sortedModules.size()]);
  }
  
  /**
   * @requires <tt>moduleCfgs != null /\ moduleCfgs.length > 0 /\ </tt> the
   *           dependency graph of the modules in </tt>moduleCfgs</tt> does not
   *           contain any cycles.
   * 
   * @effects return a 2 dimensional-array containing the same module elements
   *          in <tt>moduleCfg</tt>, but sorted in the reversed direction to the
   *          dependency between domain classes of the modules.
   * 
   *          <p>
   *          <b>Dependency: </b> Class A depends on class B if there is an
   *          has-a relationship from A to B, i.e. A contains a collection-type
   *          attribute, whose element type is B. For example: StudentClass
   *          depends on Student, because it contains a collection-type
   *          attribute StudentClass.students (which records all the students
   *          belonging to that class), each element of which is of type
   *          Student.
   * @pseudocode 
   *  <pre>
   *  let L be an empty list 
   *  for each module config m in moduleCfgs
   *   let c = m.class 
   *   if m has dependents 
   *     for each dependency c->d
   *       for d=m'.class for some module config m' != m 
   *         if m' in L // m not in L 
   *           /\ no modules in L depend on m 
   *           place m immediately after m' in L 
   *         else // m' not in L /\ no modules in L depend on m' 
   *           if m in L 
   *             place m' immediately before m in L 
   *           else place m',m in that order at the beginning of L 
   *   else 
   *    // add m anywhere 
   *    add m to the beginining of L 
   * return an array of the elements in L.
   * </pre>
   * @version 2.6.4.b: changed to take modulesMapList as parameter
   */
  private Class[] sortDomainClassesByReverseDependency(
      //Class[] domainClasses,
      Map<Class,List<Class>> modulesMapList) 
  throws NotImplementedException {
    Collection<Class> domainClasses = modulesMapList.keySet();
        
    if (debug)
      log(MessageCode.UNDEFINED,"sortDomainClassesByReverseDependency(\n{0}\n)",domainClasses);
    
    List<Class> sorted = new ArrayList();
    List<Class> moduleDescs1, moduleDescs2;
    
    int idxC=-1;
    int idxD;
    boolean hasDependents = false;
    for (Class c : domainClasses) {
      if (debug)
        log(MessageCode.UNDEFINED,"  Class: {0}", c.getSimpleName());
      
      hasDependents = false;
      idxC=-1;
      
      if (c == CommonConstants.NullType) {  //TODO v2.7: this check is no longer necessary
        // add to end (actually anywhere is fine)
        sorted.add(c);
        if (debug)
          log(MessageCode.UNDEFINED,"  -> added at the end");
      } else {
        //v 2.6.4b: get c's modules
        moduleDescs1 = modulesMapList.get(c);
            
        for (Class d : domainClasses) {
          if (d == c 
              || d == CommonConstants.NullType  //TODO v2.7: this check is no longer necessary
              ) 
            continue;

          //v 2.6.4b: get d's modules
          //moduleDescs2 = modulesMapList.get(d);
              
          if (debug)
            log(MessageCode.UNDEFINED,"  ..Class: {0}", d.getSimpleName());
          
          // check if c is in sorted
          // c can be in or not in sorted
          idxC = sorted.indexOf(c);

          // v2.6.4.b: check dependency based on the modules  
//          if (has(c, d)) { // c depends on d
          if (has(c, moduleDescs1, d)) { // c depends on d
            if (debug)
              log(MessageCode.UNDEFINED,"    {0} depends-on {1}", c.getSimpleName(), d.getSimpleName());
            
            if (!hasDependents) hasDependents = true;

            idxD = sorted.indexOf(d);
            if (idxD > -1) { // d in sorted
              if (debug)
                log(MessageCode.UNDEFINED,"    {0} in sorted", d.getSimpleName());
              
              if (idxC > -1 && idxC < idxD) {
                if (debug)
                  log(MessageCode.UNDEFINED,"    {0} also in sorted, {1} before {2}:\n    sorted: {3}\n", c.getSimpleName(), c.getSimpleName(), d.getSimpleName(), sorted);

                // both c & d are in sorted and c is before d  
                //TODO: it is likely (not certainly) that the dependency graph has a cycle
                // not yet support this case -> throws exception
                throw new NotImplementedException(
                    NotImplementedException.Code.MODULE_DEPENDENCY_GRAPH_NOT_SUPPORTED, 
                    "Chương trình không hỗ trợ mô hình hiện tại: {0}", c.getName() + "->" + d.getName());
              } else if (idxC < 0) {
                // c is not in sorted, add it after d
                sorted.add(idxD + 1, c);                
                if (debug)
                  log(MessageCode.UNDEFINED,"  -> added {0} after {1}", c.getSimpleName(), d.getSimpleName());
              }
            } else {  // d not in sorted
              if (idxC > -1) { 
                // c in sorted, add d before c
                sorted.add(idxC, d);
                if (debug)
                  log(MessageCode.UNDEFINED,"    ({0} in sorted AND {1} not in sorted) -> added {2} before {3}", c.getSimpleName(), d.getSimpleName(), d.getSimpleName(), c.getSimpleName());
                
              } else { 
                // c not in sorted, add c and then d before c
                sorted.add(0, c);
                sorted.add(0, d);
                if (debug)
                  log(MessageCode.UNDEFINED,"    (both not in sorted) -> added {0} before {1}", d.getSimpleName(), c.getSimpleName());
              }
            }
          }
        } // end inner for

        if (!hasDependents && idxC < 0) {
          // c does not depend on any module and not in sorted, 
          // add it anywhere
          // adding to the end is better than added to the front
          // because that preserves the original order of the classes
          // (in case this order means something)
          //sorted.add(0, c);
          sorted.add(c);
          if (debug)
            log(MessageCode.UNDEFINED,"  (has no dependents AND not in sorted) -> added at end");
        }
      }
    } // end outer for

    return sorted.toArray(new Class[sorted.size()]);
  }

  /**
   * @effects if c1 depends on c2, i.e. there is an association from
   *          <tt>c1</tt> to <tt>c2</tt> (e.g. StudentClass depends on Student
   *          because has(StudentClass,Student)) and that the association is used in the object from 
   *          of one of the modules in <tt>moduleDescs1</tt> 
   *            return <tt>true</tt> 
   *         else
   *            return <tt>false</tt>
   * @version 
   * 
   *  2.6.4.b: check the module configs of two classes first to be certain that 
   *  the has-a relationship between the attributes are actually used in the modules  
   */
  protected boolean has(Class c1, Collection<Class> moduleDescs1, Class c2) {
    Collection<DAttr> attributes = dodm.getDsm().getDomainConstraints(c1); //schema.getDom().getAttributeConstraints(c1);

    // if no domain attributes then return immediately
    if (attributes == null) {
      // should not happen
      return false;
    }

    // the actual attributes that are used by a module
    Collection<Field> moduleAttribs;
    for (Class moduleDescCls : moduleDescs1) {
      // determine if this module depends on c2
      
      moduleAttribs = dodm.getDsm().getAnnotatedSerialisableFields(moduleDescCls, AttributeDesc.class);
      
      if (moduleAttribs == null)
          continue; // ignore
      
      for (Field moduleAttrib : moduleAttribs) {
        // find the matching domain attribute in c1
        for (DAttr attrib: attributes) {
          if (attrib.name().equals(moduleAttrib.getName())) {
            // found the attribute
            if (attrib.type().isCollection()) {
              // describes a collection-type attribute,
              // check if it refers to c2, if so return true immediately
              Select filter = attrib.filter();

              // find the class that is used as the type of the elements in the
              // collection
              // String refType = filter.clazz();
              Class col = filter.clazz();// schema.getDom().getDomainClassFor(refType);
              if (col == null) {
                throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
                    new Object[] { col });
              }

              // check if same class or a sub-class
              if (col != c2) {
                try {
                  col.asSubclass(c2);
                  // depends
                  return true;
                } catch (ClassCastException e) {
                  // not a sub-class
                }
              } else {
                // depends
                return true;
              }
            }
            
            // stop searching
            break;
          } // end if
        } // end for
      } // end while
      
      // not depends
      return false;
    }
    return false;
  }

//  /**
//   * @effects creates in <code>schema</code> a <code>Module</code> object from
//   *          <code>(c,controller)</code>; throws DBException if an error
//   *          occured
//   */
//  private Module createModuleConfig(Class controller, String name,
//      LogicalAction.LAName defCommand, 
//      Class c, Type type, Configuration config)
//      throws DBException {
//    
//    if (debug)
//      System.out.format("Creating module configuration for %s(%s)%n", name,
//        (c != null) ? c.getName() : "");
//
//    // the module object
//    Module module = new Module(name, controller, c, defCommand, config);
//    schema.getDom().addObject(module);
//
//    // add module to configuration
//    config.addModule(module);
//    
//    return module;
//  }

//  private String getModuleName(Class controller, Class c, String guiName, Type type) 
//  throws NotPossibleException {
//    String name;
//    if (c != null)
//      name = "Module"+schema.getDom().getDomainClassName(c);
//    else if (guiName != null && !guiName.equals(""))
//      name = "Module"+guiName;
//    else
//      throw new NotPossibleException(NotPossibleException.Code.CONFIGURATION_NOT_WELL_FORMED, 
//          "Cấu hình không đúng {0} ({1})", "", "Cannot find module name for " + type);
//    
//    return name;
//  }

  /**
   * @param serialised 
   * @effects 
   *   create and return <code>RegionGui</code>
   * @version 
   * -3.1: updated to ignore viewDesc.on and to always create RegionGui <br>
   * -5.2: fixed editable setting to not use ModelDesc.editable() 
   */
  private RegionGui createGUIConfig(
      final Configuration config,
      //final ApplicationModule module, 
      final ModuleDescriptor moduleCfg,
      //v2.7.3: final Class viewClass,
      final Map<String,Label> labelMap,
      boolean hasMenuItem, 
      boolean serialised  // v2.8
      ) throws DataSourceException, NotPossibleException {

    final ViewDesc viewDesc = moduleCfg.viewDesc();
    DOMBasic dom = dodm.getDom();
    
    /*v3.1: updated to create regionGUI always: because some settings (e.g. domain class label)
         * requires that it is created
    */
    
    String moduleName = moduleCfg.name();
    //TODO: use menuItem if supported
    String formTitleStr = viewDesc.formTitle();
    RegionName[] children = viewDesc.children();
    /* v5.2: fixed
    boolean editable = moduleCfg.modelDesc().editable();
    */
    boolean editable = moduleCfg.viewDesc().editable();
    
    // get GUI style (if available)
    Style style = getModuleStyle(moduleCfg);
    
    if (debug)
      System.out.format("Creating GUI configuration for %s%n", moduleName);

    // the GUI regions
    /**
     * <pre>
     * Create a gui region representing the AppGui of the module
     * if nameToLabels != null
     *  create a child region of Components and make it the child of the GUI region
     * </pre>
     */

    // create the GUI region, all GUI regions are child of Region.Root
    Label titleLabel = null;
    String lang = config.getLanguage().getLanguageCode();     

    if (labelMap != null) {
      // GUI label is the label object in the label map whose key  
      // is the module name
      String labelId = moduleName + "_formTitle";
      titleLabel = (Label) labelMap.get(labelId);
      
      /*v3.0: changed to support the use of formTitle separately from menuItem
      // only create label in database if this GUI is not associated to a menu item
      */
      if (titleLabel != null && titleLabel.getStyle() == null) {
        // already updated with style
        dom.addObject(titleLabel, serialised);
      }
    } 
    
    if (titleLabel == null) {
      // use label from the module config
      if (formTitleStr.equals(CommonConstants.EmptyString)) {
        throw new NotFoundException(NotFoundException.Code.MODULE_LABEL_NOT_FOUND, new Object[] {moduleName});
      }
      
      if (hasMenuItem) {
        // retrieve label
        //TODO: assume that label values are unique
        titleLabel = lookUpLabelByValue(formTitleStr);
      } else {
        // create label
        titleLabel = createLabel(lang, formTitleStr, style, serialised);
      }
    }
    
    /*v3.0: support class label from labelMap 
    */
    Label domainClassLabel = null;
    
    if (labelMap != null) {
      domainClassLabel = labelMap.get(moduleName + "_domainClassLabel");
      if (domainClassLabel != null && domainClassLabel.getStyle() == null) {
        // already updated with style
        dom.addObject(domainClassLabel, serialised);
      }
    }
    
    if (domainClassLabel == null) {
      // not specified in label map: use default class label in the module configuration
      String clsLabelStr = viewDesc.domainClassLabel();
      if (!clsLabelStr.equals(CommonConstants.NullString))
        domainClassLabel = createLabel(lang, clsLabelStr, style, serialised);
    }
      
    // the root region (to be the parent of GUI region)
    final Region root = lookUpRegion(RegionName.Root.name());
    
    // v3.0: gui properties
    PropertyDesc[] propDescs = viewDesc.props(); 
    PropertySet props = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
        
    // create the GUI region
    RegionGui regionGui =  
        RegionGui.createInstance(moduleName, titleLabel, 
            domainClassLabel, // v2.7.3 
            props,
            viewDesc
            );
    
    // set other properties
    regionGui.setEditable(editable);
    
    if (style != null) {
      regionGui.setStyle(style);
    }
        
    // make gui region a child of root
    regionGui.addParent(root, null);
    
    if (debug)
      log(MessageCode.UNDEFINED,"...GUI: {0}", moduleName);

    // if there are child regions then add them
    if (children.length > 0) {
      int order = 1;
      Region child;
      for (RegionName childName : children) {
        child = lookUpRegion(childName.name());
        regionGui.addChild(child, order);
        order++;
      }
    }

    // if there are excluded regions then add them
    RegionName[] exclusionNames = viewDesc.excludeComponents(); // guiAn.excludeComponents();
    if (exclusionNames.length > 0) {
      Region target;
      for (RegionName excludedName : exclusionNames) {
        /**v2.7.2: ignore regions that are not found (perhaps not configured to use)
        target = lookUpRegion(excludedName.name());
        regionGui.addExclusion(target);
        */
        try {
          target = lookUpRegion(excludedName.name());
          regionGui.addExclusion(target);
        } catch (NotFoundException e) {
          // ignore
        }
      }
    }

    // add the region to database
    addRegion(dodm, regionGui, true, serialised);

    return regionGui;
  }

//v3.2: moved to SetUpConfigTool
//  /**
//   * @effects 
//   *  create and return <code>RegionGui</code>
//   * @version 
//   * -3.1: updated to ignore viewDesc.on and to always create RegionGui  
//   */
//  private RegionGui createGUIConfig(
//      final Configuration config,
//      final ModuleDescriptor moduleCfg,
//      final ModuleDescriptorGenerator moduleDescGen,
//      boolean hasMenuItem, 
//      boolean serialised  // v2.8
//      ) throws DataSourceException, NotPossibleException {
//
//    final ViewDesc viewDesc = moduleCfg.viewDesc();
//    
//    /*v3.1: updated to create regionGUI always: because some settings (e.g. domain class label)
//     * requires that it is created
//    // v2.7.2: only create RegionGUI if viewDescriptor.on = true
//    if (!viewDesc.on()) {
//      return null;
//    }
//    */
//    
//    String moduleName = moduleDescGen.getModuleName();//moduleCfg.name();
//    String labelStr = moduleName; //viewDesc.label();
//    RegionName[] children = viewDesc.children();
//    boolean editable = moduleCfg.modelDesc().editable();
//
//    // get GUI style (if available)
//    Style style = getModuleStyle(moduleCfg);
//    
//    if (debug)
//      System.out.format("Creating GUI configuration for %s%n", moduleName);
//
//    // the GUI regions
//    /**
//     * <pre>
//     * Create a gui region representing the AppGui of the module
//     * if nameToLabels != null
//     *  create a child region of Components and make it the child of the GUI region
//     * </pre>
//     */
//
//    // create the GUI region, all GUI regions are child of Region.Root
//    Label label = null;
//    String lang = config.getLanguage().getLanguageCode();     
//
//    if (hasMenuItem) {
//      // retrieve label
//      //TODO: assume that label values are unique
//      label = lookUpLabelByValue(labelStr);
//    } else {
//      // create label
//      label = createLabel(lang, labelStr, style, serialised);
//    }
//
//    // v2.7.3: class label
//    String clsLabelStr = viewDesc.domainClassLabel();
//    Label classLabel = null;
//    if (!clsLabelStr.equals(MetaConstants.NullString))
//      classLabel = createLabel(lang, clsLabelStr, style, serialised);
//      
//    // the root region (to be the parent of GUI region)
//    final Region root = lookUpRegion(RegionName.Root.name());
//    
//    // v3.0: gui properties
//    PropertyDesc[] propDescs = viewDesc.props(); 
//    PropertySet props = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
//    
//    // create the GUI region
//    RegionGui regionGui =  
//        RegionGui.createInstance(moduleName, label, classLabel, 
//            props,  // v3.0
//            viewDesc);
//    
//    // set other properties
//    regionGui.setEditable(editable);
//    
//    if (style != null) {
//      regionGui.setStyle(style);
//    }
//        
//    // make gui region a child of root
//    regionGui.addParent(root, null);
//    
//    if (debug)
//      log(MessageCode.UNDEFINED,"...GUI: {0}", moduleName);
//
//    // if there are child regions then add them
//    if (children.length > 0) {
//      int order = 1;
//      Region child;
//      for (RegionName childName : children) {
//        child = lookUpRegion(childName.name());
//        regionGui.addChild(child, order);
//        order++;
//      }
//    }
//
//    // if there are excluded regions then add them
//    RegionName[] exclusionNames = viewDesc.excludeComponents(); // guiAn.excludeComponents();
//    if (exclusionNames.length > 0) {
//      Region target;
//      for (RegionName excludedName : exclusionNames) {
//        /**v2.7.2: ignore regions that are not found (perhaps not configured to use)
//        target = lookUpRegion(excludedName.name());
//        regionGui.addExclusion(target);
//        */
//        try {
//          target = lookUpRegion(excludedName.name());
//          regionGui.addExclusion(target);
//        } catch (NotFoundException e) {
//          // ignore
//        }
//      }
//    }
//
//    // add the region to database
//    addRegion(dodm, regionGui, true, serialised);
//
//    return regionGui;
//  }
  
  /**
   * @effects 
   *  retrieve and return <tt>Style</tt> object associated to the style name specified 
   *  in <tt>moduleCfg</tt>, or return <tt>null</tt> if no style specified. 
   */
  protected Style getModuleStyle(ModuleDescriptor moduleCfg) throws NotFoundException {
    StyleName styleName = moduleCfg.viewDesc().style();
    Style style = null;
    if (styleName != StyleName.Null) {
      style = lookUpStyle(styleName.name());
    }
    
    return style;
  }
  
  /**
   * @effects 
   *  retrieve and return <tt>Style</tt> object associated to the style name specified 
   *  for the <b>data label</b> in <tt>attributeDesc</tt>, or return <tt>null</tt> if no style specified. 
   */
  protected Style getLabelStyle(AttributeDesc attribDesc) throws NotFoundException {
    StyleName styleName = attribDesc.styleLabel();
    Style style = null;
    if (styleName != StyleName.Null) {
      style = lookUpStyle(styleName.name());
    }
    
    return style;
  }
  
  /**
   * @effects 
   *  retrieve and return <tt>Style</tt> object associated to the style name specified 
   *  for the <b>data field</b> in <tt>attributeDesc</tt>, or return <tt>null</tt> if no style specified. 
   */
  private Style getFieldStyle(AttributeDesc attribDesc) throws NotFoundException {
    StyleName styleName = attribDesc.styleField();
    Style style = null;
    if (styleName != StyleName.Null) {
      style = lookUpStyle(styleName.name());
      
      // debug
//      if (debug) {
//        if (styleName.name().equals("DefaultArial")) {
//          System.out.printf("Attribute: %s %n   style (default arial): %s%n", 
//              attribDesc.label(), style);
//        }
//      }
    }
    
    return style;
  }
  
  /**
   * @effects returns a 2-element array <code>Region[]</code>, the first element
   *          is a <code>RegionGui</code> object and the second is the
   *          corresponding <code>RegionToolMenuItem</code> object.
   */
  private RegionToolMenuItem createToolMenuGUIRegion(
      //v2.7: final ApplicationModule module,
      final ModuleDescriptor moduleCfg, 
      final Map<String,Label> labelMap,
      Integer displayOrder,
      boolean serialised  // v2.8
      ) throws DataSourceException {
    String imageIcon = moduleCfg.viewDesc().imageIcon();
    RegionName parentName = moduleCfg.viewDesc().parent();
    Region parent = lookUpRegion(parentName.name());
    //v2.7: String moduleName = module.getName();
    String moduleName = moduleCfg.name();
    
    DOMBasic dom = dodm.getDom();
    
    /*v2.7: support style 
     * tool menu item style is the same as the module style 
     */
    Style style = getModuleStyle(moduleCfg);

    // label is the label object with the same name as the module name 
    // in the label map
    Label label = null;
    // v5.1: improved to support the case that labelMap != null but module's menuItem is 
    // of the module is not specified in labelMap
    String moduleTitleKey = moduleName + "_menuItem";
    if (labelMap != null 
        && labelMap.containsKey(moduleTitleKey) // v5.1
        ) {
      // retrieve label from label map
      label = (Label) labelMap.get(moduleTitleKey);

      // v5.1: unlikely to happen now
      if (label == null)
        throw new NotFoundException(NotFoundException.Code.MODULE_LABEL_NOT_FOUND, 
            new Object[] {moduleName});
      
      label.setStyle(style);  // v2.7
      
      dom.addObject(label, serialised);
    } else {
      // create label from label string of the module config
      String lang = config.getLanguage().getLanguageCode();
      //TODO: use menuItem if supported
      String labelStr = moduleCfg.viewDesc().formTitle();
      
      if (labelStr.equals(CommonConstants.EmptyString))
        throw new NotFoundException(NotFoundException.Code.LABEL_NOT_FOUND, 
            "Không tìm thấy nhãn {0}", moduleName);

      label = createLabel(lang, labelStr, style, serialised);
    }

    String toolMenuItemName = moduleName;
    
    // TODO: get these from the config (?)
    boolean enabled = true;
    boolean visible = true;
    RegionToolMenuItem rt = new RegionToolMenuItem(toolMenuItemName, label, imageIcon, null,
        enabled, 
        visible,
        //v2.7: module, 
        parent, displayOrder);

    addRegion(dodm, rt, true, serialised);

    return rt;
  }

//v3.2: moved to SetUpConfigTool
//  /**
//   * @effects returns a 2-element array <code>Region[]</code>, the first element
//   *          is a <code>RegionGui</code> object and the second is the
//   *          corresponding <code>RegionToolMenuItem</code> object.
//   */
//  private RegionToolMenuItem createToolMenuGUIRegion(
//      final ModuleDescriptor moduleCfg, 
//      //final Map<String,Label> labelMap,
//      ModuleDescriptorGenerator moduleDescGen, 
//      Integer displayOrder, 
//      boolean serialised  // v2.8
//      ) throws DataSourceException {
//    String imageIcon = moduleCfg.viewDesc().imageIcon();
//    RegionName parentName = moduleCfg.viewDesc().parent();
//    Region parent = lookUpRegion(parentName.name());
//    String moduleName = moduleDescGen.getModuleName();//moduleCfg.name();
//    
//    /*v2.7: support style 
//     * tool menu item style is the same as the module style 
//     */
//    Style style = getModuleStyle(moduleCfg);
//
//    // label is the label object with the same name as the module name 
//    // in the label map
//    Label label = null;
////    if (labelMap != null) {
////      // retrieve label from label map
////      label = (Label) labelMap.get(moduleName);
////      
////      if (label == null)
////        throw new NotFoundException(NotFoundException.Code.LABEL_NOT_FOUND, 
////            "Không tìm thấy nhãn {0}", moduleName);
////      
////      label.setStyle(style);  // v2.7
////      
////      schema.getDom().addObject(label);
////    } else {
//      // create label from label string of the module config
//      String lang = config.getLanguage().getLanguageCode();
//      String labelStr = moduleName;//moduleCfg.viewDesc().label();
//      
//      if (labelStr.equals(MetaConstants.EmptyString))
//        throw new NotFoundException(NotFoundException.Code.LABEL_NOT_FOUND, 
//            "Không tìm thấy nhãn {0}", moduleName);
//
//      label = createLabel(lang, labelStr, style, serialised);
////    }
//
//    String toolMenuItemName = moduleName;
//    
//    // TODO: get these from the config (?)
//    boolean enabled = true;
//    boolean visible = true;
//    RegionToolMenuItem rt = new RegionToolMenuItem(toolMenuItemName, label, imageIcon, null,
//        enabled, 
//        visible,
//        //v2.7: module, 
//        parent, displayOrder);
//
//    addRegion(dodm, rt, true, serialised);
//
//    return rt;
//  }
  
  /**
   * @modifies linkedRegionProps
   * @effects create and returns a <code>Region</code> object representing the
   *          components-type region for the specified <code>domainClass</code>, 
   *          <b>but</b> without creating the linked regions. The configuration of those regions
   *          are recorded into <tt>linkedRegionProps</tt>.
   *          
   *          <p>The region is the child of a special {@link Region} named <tt>Components</tt>
   * @version 
   * - 3.3c: set editability to regionGUI's editable b/c the result region will be the top-level region of the GUI <br>
   * - 5.1: narrow the interface /\ use containment tree in module to support custom configuration of descendant module
   */
  private Region createComponentRegionsWithoutLinkings(final DODMBasic dodm,
      final Configuration config,
      /* v5.1: narrow the interface
      RegionGui regionGui,  // v2.7.4
      String regionName, Class domainClass, 
      Class moduleDescrCls, Type type, 
      Style style, 
      */
      Class moduleDescrCls, ApplicationModule module, 
      final Map<String,Label> labelMap,
      /*v3.2: changed to List of Map
      final Map<Field, Map> linkedRegionProps,
      */
      List<Map> linkedRegionProps,
      boolean serialised // v2.8
      )
      throws DataSourceException, NotFoundException {
    final String lang = config.getLanguage().getLanguageCode();
    DOMBasic dom = dodm.getDom();

    /* v5.1: added this block */ 
    ModuleDescriptor moduleCfg = (ModuleDescriptor) moduleDescrCls.getAnnotation(MD);
    Class domainClass = module.getDomainClassCls();
    RegionGui regionGui = module.getViewCfg();
    RegionType type = moduleCfg.viewDesc().viewType();
    
    // the components region name
    String regionName = module.getName();  

    if (debug)
      System.out.printf("...%s%n", regionName);
    
    Style style = (regionGui != null) ? regionGui.getStyle() :  getModuleStyle(moduleCfg);
    // end 5.1
    
    // the components region (r): to be the child of Region(name="Components")
    Region r = new Region(regionName);
    r.addParent(lookUpComponentsRegion(), 
        null);

    // use the specified style for this region
    r.setStyle(style);

    // v2.7.4: use GUI's layout builder type 
    r.setLayoutBuilderClass(
        (regionGui != null) ? regionGui.getLayoutBuilderClass() : null);
    
    // v3.3c: use regionGui's editability (b/c this will be the top-level region of the GUI)
    r.setEditable(regionGui.getEditable());
    
    // get the view configuration from the view class
    final Class<AttributeDesc> attribDescCls = AttributeDesc.class;
    //v3.3: not used: final Class<PrintFieldDesc> printfDescCls = PrintFieldDesc.class;
    
    /*v3.2: FIXED: read attributes with preference given to those from moduleDescrCls if it is 
     * a sub-type
     List<Field> attributes = schema.getDsm().getAttributes(moduleDescrCls, null);
     */
    Collection<Field> attributes = dodm.getDsm().getFieldsNoDuplicates(moduleDescrCls);

    // title and data field regions if specified
    Region rt;
    if (attributes != null) {
      String clsName = domainClass.getSimpleName();
      String name;
      String labelStr;
      Class displayClass;
      //v5.1: String labelId;
      //v5.1: Label label;
      AttributeDesc attribDesc;
      Style titleStyle;
      int index = 1;
      // v2.7.3: use module class name to identify title label
      Class moduleClass;
      String moduleClsName;
      
      for (Field field : attributes) {
        name = field.getName();
        attribDesc = field.getAnnotation(attribDescCls);

        moduleClass = field.getDeclaringClass();
        moduleClsName = moduleClass.getSimpleName();
        
        if (name.equals("title")) {
          displayClass = attribDesc.type();
          
          // v2.7
          titleStyle = getLabelStyle(attribDesc); 
          
          if (titleStyle == null) {
            // use default title style
            titleStyle = DEFAULT_TITLE_LABEL_STYLE;
          }
          
          // title field
          // get label from the label map or from the field config
          // v5.1: improved to support the case that labelMap != null but the labelId is not specified in labelMap
          // (possibly because now language config file was defined)
          String labelId = moduleClsName+"_title";
          Label label = null;
          if (labelMap != null) {
            label = (Label) labelMap.get(labelId);
            if (label == null) {
              // try the other labelId
              labelId = module.getName()+"_title";
              label = (Label) labelMap.get(labelId);
            }
          }
          
          if (//v5.1: labelMap != null
              label != null
              ) {
            /*v5.1: moved to above
            // label is the label constant of the same name as the domain class's title
            label = (Label) labelMap.get(labelId);
            
            if (label == null)
              throw new NotFoundException(NotFoundException.Code.LABEL_NOT_FOUND, new Object[] {labelId});    
            */
            
            // v5.1: only add label if not already added. Why? b/c label may come from an ancester class
            // which may have been processed before
            if (dom.lookUpObjectByID(Label.class, label.getId()) == null) {
              label.setStyle(titleStyle);            
              dom.addObject(label, serialised);
            }
          } else {
            labelStr = attribDesc.label();
            label = createLabel(lang, labelStr, titleStyle, serialised);
          }
          
          rt = new Region(name, label, null, 
              ((displayClass != CommonConstants.NullType) ? displayClass.getName() : null), 
              r, index);
          
          //v2.7: rt.setStyle(StyleConstants.Heading1);
          
          index++;
        } else {
          // domain fields
          // create child regions for each field
          createRegionField(module, // v5.1
              domainClass,field, index, attribDesc, 
              r, lang, labelMap, 
              linkedRegionProps // v3.0 
              , serialised  // v2.8
              );
          index++;
        } // end domain fields
      }
    }

    return r;
  }
  
  /**
   * Another version of {@link #createRegionField(Class, Field, int, AttributeDesc, Region, String, Map, List, boolean)} 
   * which is specifically used by {@link #createLinkedRegionForScopeDesc(Class, ScopeDesc, Map, String)}. 
   * In this, the {@link Field} argument is not known. What is known is an {@link AttributeDesc}.
   * 
   * @effects 
   *   create and return a {@link Region} (either a {@link RegionDataField} or a {@link RegionLinking}) representing the configuration for <tt>attribDesc</tt> of the 
   *   domain field of <tt>domainCls</tt> whose name is <tt>fieldName</tt>. The result region is the child of <tt>parent</tt>.
   *   
   *   <p>The result object is <b>NOT</b> yet stored in the object store.
   *    
   * @version 5.1
   */
  private Region createCustomRegionField(Class domainCls, int index,
      String fieldName, AttributeDesc attribDesc, 
      Region parent, String lang,
      Map<String, Label> labelMap, boolean serialised) throws NotPossibleException, DataSourceException {
    String labelStr;
    
    DOMBasic dom = dodm.getDom();
    
    //String labelId;
    //Label label;

    Class viewType; // view component class used for this field
    String viewTypeName;
    Select selectAn;
    DAttr dcAn;
    boolean hasRef = false;
    Style labelStyle = null;
    
    ModelDesc modelDesc;    // v2.7.2
    boolean visible;
    
    selectAn = attribDesc.ref(); 
    hasRef = (selectAn.clazz() != CommonConstants.NullType); //Select.NullClass);
    labelStr = attribDesc.label();
    viewType = attribDesc.type();
   
    modelDesc = attribDesc.modelDesc(); // v2.7.2
    visible = attribDesc.isVisible();
    
    // check if this field's display type is a container type
    boolean isContainerType=false;

    if (viewType != CommonConstants.NullType) { //AttributeDesc.Null_Type) {
      viewTypeName = viewType.getName();
      isContainerType = !JDataField.class.isAssignableFrom(viewType);
    } else {
      viewTypeName = null;
    }

    labelStyle = getLabelStyle(attribDesc);
    
    // get label from the label map or from the field config
    String clsName =  domainCls.getSimpleName();
    
    // v5.1: use labelId constructed from both the owner MCC and the current module
    // in case the owner MCC is an ancestor but the labelId was constructed from the language resource file
    // bares the module's name
    String labelId = clsName+"_"+fieldName;
    Label label = null;
    if (labelMap != null) {
      label = (Label) labelMap.get(labelId);
      if (label == null) {
        // try the other labelId
        // look up the module of domainCls
        ApplicationModule module = lookUpModule(domainCls);
        labelId = module.getName()+"_"+fieldName;
        label = (Label) labelMap.get(labelId);
      }
    }
    
    if (// v5.1: labelMap != null
        label != null
        ) {
      // label is the label object in labelMap that is mapped to 
      // the combination of class name and field name. The class name 
      // is the name of the class in which the field is declared.
      //Class ownerClass = domainCls; //field.getDeclaringClass();
      /* v5.1: moved to above
      String clsName = domainCls.getSimpleName();
      
      labelId = clsName+"_"+fieldName;
      label = (Label) labelMap.get(labelId);
      if (label == null)
        throw new NotFoundException(NotFoundException.Code.LABEL_NOT_FOUND, new Object[] {labelId});
      */
      
      // only add label to db if owner class of the label is different from this class 
      // and label look-up return null
      if (dom.lookUpObjectByID(Label.class, label.getId()) == null) {
        // v2.7
        label.setStyle(labelStyle);
        dom.addObject(label, serialised);
      }
    } else {
      label = createLabel(lang, labelStr, labelStyle, serialised);
    }
    
//    Region cr = null;
    dcAn = dodm.getDsm().getDomainConstraint(domainCls, fieldName);
    jda.modules.dcsl.syntax.DAttr.Type dataType = dcAn.type();
    
      // v2.7
      if (dataType.isDomainType()) {
        label.setStyle(DEFAULT_DOMAIN_TYPE_LABEL_STYLE);
        dom.updateObject(label, null, serialised);
      }
      
      // v2.7.2: support model config
      // create the model cfg if specified
      ModelConfig modelCfg = null;
      if (modelDesc.model() != CommonConstants.NullType) {
        modelCfg = createModelConfig(dodm, modelDesc, serialised);
      }
      
      // v3.0: support additional properties
      PropertyDesc[] propDescs = attribDesc.props(); 
      PropertySet props = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
      
      RegionDataField crd = new RegionDataField(fieldName, label, null,
          viewTypeName, parent, index, 
          visible 
          , props // v3.0
          ,modelCfg   // v2.7.2
          );

      // if data type is domain type then use a special style for it
      if (hasRef) {
        // turn bound attributes into a string and put into the region
        // config
        // of this field
        String[] boundAttributesArr = selectAn.attributes();
        StringBuffer boundAttributes = new StringBuffer();
        for (int j = 0; j < boundAttributesArr.length; j++) {
          boundAttributes.append(boundAttributesArr[j]);
          if (j < boundAttributesArr.length - 1)
            boundAttributes.append(",");
        }
        crd.setBoundAttributes(boundAttributes.toString());
      }

      // v2.7.4: support other properties
      crd.setProperties(attribDesc);
      
//      cr = crd;
//    }

    // if field region was created then set up other properties 
    if (crd != null) {
      setUpRegionFieldProperties(crd, attribDesc);
    }
    
    return crd;
  }
  
  /**
   * @requires
   *  <tt>module</tt> is the current module of <tt>domainClass</tt> 
   *  
   * @modifies linkedRegionProps 
   * @effects 
   *  if the specified arg is a normal field
   *    create and return a <tt>Region</tt> for the field from the arguments. 
   *    <p>The parent region of the created region is <tt>parent</tt>, 
   *    which is the components region of the functional gui of <tt>domainClass<tt>.
   *  else
   *    record the field configuration in linkedRegionProps
   *    return null
   *    
   *  @version 
   *  - 3.0 <br>
   *  - 5.1: added parameter module and improved label look up 
   */
  protected Region createRegionField(ApplicationModule module, // v5.1 
      Class domainClass, 
      //String name,
      Field field,
      int index, 
      AttributeDesc attribDesc,
      Region parent, String lang, 
      Map<String,Label> labelMap,
      /*v3.2: changed to List of Map
      final Map<Field, Map> linkedRegionProps,
      */
      List<Map> linkedRegionProps, 
      boolean serialised
      ) throws NotPossibleException, DataSourceException {
    String labelStr;
    String fieldName = field.getName();
    
    DOMBasic dom = dodm.getDom();
    
    //v5.1: String labelId;
    //v5.1: Label label;

    Class viewType; // view component class used for this field
    String viewTypeName;
    Select selectAn;
    DAttr dcAn;
    boolean hasRef = false;
    Style labelStyle = null;
    
    ModelDesc modelDesc;    // v2.7.2
    boolean visible;
    
    selectAn = attribDesc.ref(); 
    hasRef = (selectAn.clazz() != CommonConstants.NullType); //Select.NullClass);
    labelStr = attribDesc.label();
    viewType = attribDesc.type();
   
    modelDesc = attribDesc.modelDesc(); // v2.7.2
    visible = attribDesc.isVisible();
    
    // check if this field's display type is a container type
    boolean isContainerType=false;

    if (viewType != CommonConstants.NullType) { //AttributeDesc.Null_Type) {
      viewTypeName = viewType.getName();
      isContainerType = !JDataField.class.isAssignableFrom(viewType);
    } else {
      viewTypeName = null;
    }

    labelStyle = getLabelStyle(attribDesc);
    
    // get label from the label map or from the field config
    // v5.1: improved to support the case that labelMap != null but the labelId is not specified in labelMap
    // (possibly because now language config file was defined)
    // the owner MCC of the field, which may be an ancester MCC 
    Class ownerClass = field.getDeclaringClass(); 
    String clsName = ownerClass.getSimpleName();
    
    // v5.1: use labelId constructed from both the owner MCC and the current module
    // in case the owner MCC is an ancestor but the labelId was constructed from the language resource file
    // bares the module's name
    String labelId = clsName+"_"+fieldName;
    Label label = null;
    if (labelMap != null) {
      label = (Label) labelMap.get(labelId);
      if (label == null) {
        // try the other labelId
        labelId = module.getName()+"_"+fieldName;
        label = (Label) labelMap.get(labelId);
      }
    }
    
    if (// v5.1: labelMap != null
        label != null
        ) {
      // label is the label object in labelMap that is mapped to 
      // the combination of class name and field name. The class name 
      // is the name of the class in which the field is declared.
      
      /* v5.1: moved to above
      Class ownerClass = field.getDeclaringClass();
      String clsName = ownerClass.getSimpleName();
      
      labelId = clsName+"_"+fieldName;
      
      label = (Label) labelMap.get(labelId);
      if (label == null)
        throw new NotFoundException(NotFoundException.Code.LABEL_NOT_FOUND, new Object[] {labelId});
      */
      
      // only add label to db if owner class of the label is different from this class 
      // and label look-up return null
      if (dom.lookUpObjectByID(Label.class, label.getId()) == null) {
        // v2.7
        label.setStyle(labelStyle);
        dom.addObject(label, serialised);
      }
    } else {
      label = createLabel(lang, labelStr, labelStyle, serialised);
    }
    
    Region cr = null;
    dcAn = dodm.getDsm().getDomainConstraint(domainClass, fieldName);
    jda.modules.dcsl.syntax.DAttr.Type dataType = dcAn.type();
    
    Map linkedRegionPropsMap;
    
    if (dataType.isCollection()) {
      // collection-type field
      Select filter = dcAn.filter();
      Class domainType = filter.clazz();
      
      if (domainType == CommonConstants.NullType) {
        throw new NotPossibleException(
            NotPossibleException.Code.INVALID_REFERENCED_DOMAIN_TYPE, new Object[] {domainClass.getSimpleName(), fieldName, domainType});
      }
      
      // v2.7
      if (label.getStyle() == null) {
        // use default style
        label.setStyle(DEFAULT_LINKED_LABEL_STYLE);
        dom.updateObject(label, null, serialised);
      }
      
      linkedRegionPropsMap = new HashMap();
      // v3.2: added Field to map
      // linkedRegionPropsMap.put("field", field);
      linkedRegionPropsMap.put("fieldName", fieldName);
      linkedRegionPropsMap.put("label", label);
      linkedRegionPropsMap.put("parent", parent);
      linkedRegionPropsMap.put("index", index);
      linkedRegionPropsMap.put("domainType", domainType);
      linkedRegionPropsMap.put("attribDesc", attribDesc);
      linkedRegionPropsMap.put("serialised", serialised);
      /*v3.2: 
      linkedRegionProps.put(field, linkedRegionPropsMap);
       */
      linkedRegionProps.add(linkedRegionPropsMap);
    } else if (dataType.isDomainType() && isContainerType) {
      Class domainType = dodm.getDsm().getDomainClassFor(domainClass, fieldName);

      // v2.7
      if (label.getStyle() == null) {
        // use default style
        label.setStyle(DEFAULT_LINKED_LABEL_STYLE);
        dom.updateObject(label, null, serialised);
      }
      
      linkedRegionPropsMap = new HashMap();
      // v3.2: added Field to map
      // linkedRegionPropsMap.put("field", field);
      linkedRegionPropsMap.put("fieldName", fieldName);
      linkedRegionPropsMap.put("label", label);
      linkedRegionPropsMap.put("parent", parent);
      linkedRegionPropsMap.put("index", index);
      linkedRegionPropsMap.put("domainType", domainType);
      linkedRegionPropsMap.put("attribDesc", attribDesc);
      linkedRegionPropsMap.put("serialised", serialised);
      /*v3.2: 
      linkedRegionProps.put(field, linkedRegionPropsMap);
       */
      linkedRegionProps.add(linkedRegionPropsMap);
    } else {
      // normal field
      
      // v2.7
      if (dataType.isDomainType()) {
        label.setStyle(DEFAULT_DOMAIN_TYPE_LABEL_STYLE);
        dom.updateObject(label, null, serialised);
      }
      
      // v2.7.2: support model config
      // create the model cfg if specified
      ModelConfig modelCfg = null;
      if (modelDesc.model() != CommonConstants.NullType) {
        /* v3.0: 
        modelCfg = ModelConfig.createInstance(modelDesc);
        dom.addObject(modelCfg, serialised);
        */
        modelCfg = createModelConfig(dodm, modelDesc, serialised);
      }
      
      // v3.0: support additional properties
      PropertyDesc[] propDescs = attribDesc.props(); 
      PropertySet props = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
      
      RegionDataField crd = new RegionDataField(fieldName, label, null,
          viewTypeName, parent, index, 
          visible 
          , props // v3.0
          ,modelCfg   // v2.7.2
          );

      // if data type is domain type then use a special style for it
      if (hasRef) {
        // turn bound attributes into a string and put into the region
        // config
        // of this field
        String[] boundAttributesArr = selectAn.attributes();
        StringBuffer boundAttributes = new StringBuffer();
        for (int j = 0; j < boundAttributesArr.length; j++) {
          boundAttributes.append(boundAttributesArr[j]);
          if (j < boundAttributesArr.length - 1)
            boundAttributes.append(",");
        }
        crd.setBoundAttributes(boundAttributes.toString());
      }

      // v2.7.4: support other properties
      crd.setProperties(attribDesc);
      
      cr = crd;
    }

    // if field region was created then set up other properties 
    if (cr != null) {
      setUpRegionFieldProperties(cr, attribDesc);
    }
    
    return cr;
  }

  /**
   * @version 3.0
   */
  protected void setUpRegionFieldProperties(Region cr, AttributeDesc attribDesc) {
    int width;
    int height;
    Style fieldStyle = null;
    boolean isStateListener;
    boolean isStateEventSource; // v2.7.2
    boolean editable; 
    
    width = attribDesc.width();
    height = attribDesc.height();
    fieldStyle = getFieldStyle(attribDesc);

    isStateListener = attribDesc.isStateListener();
    isStateEventSource = attribDesc.isStateEventSource();
    editable = attribDesc.editable();
    
    
    if (width > -1)
      cr.setWidth(width);

    if (height > -1)
      cr.setHeight(height);    
    
    // set style
    if (fieldStyle != null)
      cr.setStyle(fieldStyle);
    
    cr.setIsStateListener(isStateListener);
    cr.setIsStateEventSource(isStateEventSource); // v2.7.2
    cr.setEditable(editable);
  }

  
  /**
   * @effects 
   *  if exists the the child region of the <tt>Components</tt> region, 
   *  whose name is <tt>sharedRegionName</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   *    
   *  <p> throws NotFoundException if <tt>Components</tt> region has no child regions.
   * @version 
   * - 3.0: created <br>
   * - 3.1: remove NotFoundException
   * 
   */
  protected Region retrieveSharedRegion(String sharedRegionName) 
      //v3.1: throws NotFoundException 
  {
    // find the shared region created for this type, it is the child region of
    // Components region, whose name is linkedRegionName
    DOMBasic dom = dodm.getDom();

    Query q = new Query();
    Op eq = Op.EQ;
    final Class RM = RegionMap.class;
    final Region linkedParent = lookUpComponentsRegion();
    
    q.add(new Expression("parent", eq, linkedParent));
    Collection<RegionMap> rmaps = dom.getObjects(RM, q);
    if (rmaps == null || rmaps.isEmpty()) {
      /* v3.1: 
      throw new NotFoundException(
          NotFoundException.Code.CHILD_REGION_NOT_FOUND,
          "Không tìm thấy vùng con {0} của vùng {1}", "", linkedParent);
          */
      // no child regions
      return null;
    }
    
    Region sharedRegion = null;
    for (RegionMap rmap : rmaps) {
      Region ccr = rmap.getChild();
      if (ccr.getName().equals(sharedRegionName)) {
        // found the region
        sharedRegion = ccr;
        break;
      }
    }
    
    return sharedRegion;
  }

  /**
   * @effects 
   *  create and return a <tt>RegionLinking r</tt> representing the containment configuration for <tt>scopeDesc</tt>.
   *  
   *  <p>Throws DataSourceException if fails to create object in the object store, NotPossibleException if fails for other reasons.
   *  
   * @version 5.1
   */
  private RegionLinking createLinkedRegionForScopeDesc(final Class childCls, final String scopeStr, final ScopeDesc scopeDesc, 
      final Map<String, Label> labelMap, 
      final String lang, final boolean serialised) throws DataSourceException, NotPossibleException {
    // region name equates the child class name
    String rname = (String) childCls.getSimpleName();
    
    // no label 
    Label label = null;
    
    // no parent
    Region nullParent = null; //(Region) linkedRegionPropsMap.get("parent");
    
    // no index 
    int index = -1; // (Integer) linkedRegionPropsMap.get("index");
    
    // the domain class that is the data type of field
    //Class domainType = (Class) linkedRegionPropsMap.get("domainType");
    
    //AttributeDesc attribDesc = (AttributeDesc) linkedRegionPropsMap.get("attribDesc");
    
    //boolean serialised = true; //(Boolean) linkedRegionPropsMap.get("serialised");
    
    boolean visible = false; //attribDesc.isVisible();
        
    // create the region
    ControllerDesc controllerDesc = scopeDesc.controllerDesc();
    ModelDesc modelDesc = scopeDesc.modelDesc();
    
    // no display class
    String displayClass = null;
    
    DOMBasic dom = dodm.getDom();
    
    // the model config
    ModelConfig modelCfg = null;
    if (modelDesc.model() != CommonConstants.NullType) {
      modelCfg = createModelConfig(dodm, modelDesc, serialised);
    }

    ControllerConfig controllerCfg  = createControllerConfig(dodm, controllerDesc, serialised);
    
    // v3.0: additional properties
    PropertyDesc[] propDescs = scopeDesc.props(); 
    boolean displayVisible = false; //getPropertyValue(propDescs, 
        //PropertyName.view_objectForm_dataField_visible, Boolean.class, Boolean.TRUE); 

    PropertySet props = null;
    if (propDescs.length > 0) {
      props = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
      if (scopeStr != null) {
        // add scope as a property of rl
        props.setProperty(PropertyName.module_containment_scope, scopeStr);
      }
    } else if (scopeStr != null) {
      // create a property set containing one single element
      props = PropertySetFactory.createAnoPropertySet(dodm, serialised, 
          PropertyName.module_containment_scope, scopeStr);
    }

    // v5.2b: support other scopeDesc's attributes
    boolean editable = scopeDesc.editable();  // TODO: support editable!
    Class displayType = scopeDesc.displayType();
    if (!ModuleToolkit.isDefaultDisplayType(displayType)) {
      displayClass = displayType.getName();
    }
    
    // create the linked region
    RegionLinking linkedRegion = new RegionLinking(
        rname, null, null, displayClass, nullParent, index, controllerCfg, modelCfg, visible
        , props // v3.0
        );

    // v3.0: needed for modules that need to process labels of all the domain attributes
    //linkedRegion.setLabel(label);
    
    // no title child region
    // Region rt = new Region("title", label, null, linkedRegion, 1);
    
    ////////
    // Unlike other RegionLinking, this object is not used to link to the target modules.
    ///////
    // look up the module of the refering region (if available) and use it to 
    // set up other properties and to link to the region as a child
    // NOTE: if the attribute is not visible then the module may not be specified 
//    ApplicationModule referringModule = addLinkedRegionChildLink(linkedRegion, domainType, compRegions, displayVisible);
    
    // TODO: consider removing referringModule.getViewCfg() from the properties setting below 
    // because this does not work with the use of subTypes (further below). 
    // The actual referringModule to use is only known at run-time by the user module. The viewConfig
    // of that module can be merged into linkedRegion there.
//    if (referringModule != null) {
//      // v2.7.4: support other configuration properties
//      linkedRegion.setProperties(attribDesc, referringModule.getViewCfg());
//    }
    
    // v3.2: support sub-types
//    addLinkedRegionChildLinks(domainType, linkedRegion, compRegions, displayVisible);
    
    // set up properties 
    //setUpRegionFieldProperties(linkedRegion, attribDesc);
    
    // the attribute descriptors that will become the children of rl
    // this is where rl differs from the other use of RegionLinking
    AttributeDesc[] attribDescs = scopeDesc.attribDescs();
    if (attribDescs.length > 0) {
      index = 1;
      for (AttributeDesc attribDesc : attribDescs) {
        String fieldName = attribDesc.id();
        createCustomRegionField(childCls, index, fieldName, attribDesc, linkedRegion, lang, labelMap, serialised);
        index++;
      }
    }
    
    // store region linking and all of its child regions (if any) to object store(s)
    // add the region to object store
    boolean withParents = false; // nullParents
    addRegion(dodm, linkedRegion, withParents, serialised);

    return linkedRegion;
  }
  
  /**
   * This works similar to {@link #createLinkedRegion(Field, Map, Collection)} except that it additionally 
   * links the regions of the sub-type modules of the field's domain type.  
   * 
   * @effects 
   *  create and return a <tt>RegionLinking r</tt> for <tt>field</tt> (whose domain type is <tt>D</tt>) from the properties in 
   *  <tt>linkedRegionProps</tt>, such that:
   *  <pre>
   *    r is the parent of the component region of Module(D) AND
   *    if D has sub-types d1,...,dn then 
   *      r is also the parent of the component regions of Module(d1),..., Module(dn) 
   *   </pre>
   * @version 
   * - 3.2: created<br>
   * - 3.3: improved to support descendant types
   */
  private Region createLinkedRegionWithSubTypeSupport( 
      Map linkedRegionPropsMap, 
      Collection<Region> compRegions) throws NotFoundException, NotPossibleException, DataSourceException {
    // retrieve the linked region properties 
    String fieldName = (String)
        linkedRegionPropsMap.get("fieldName");
    
    Label label = (Label)
        linkedRegionPropsMap.get("label");
    
    Region parent = (Region) linkedRegionPropsMap.get("parent");
    
    int index = (Integer) linkedRegionPropsMap.get("index");
    
    // the domain class that is the data type of field
    Class domainType = (Class) linkedRegionPropsMap.get("domainType");
    
    AttributeDesc attribDesc = (AttributeDesc) linkedRegionPropsMap.get("attribDesc");
    
    boolean serialised = (Boolean) linkedRegionPropsMap.get("serialised");
    
    boolean visible = attribDesc.isVisible();
        
    // create the region
    /*v2.7.2: process attribute desc*/
    Class viewType = attribDesc.type();
    ControllerDesc controllerDesc = attribDesc.controllerDesc();
    ModelDesc modelDesc = attribDesc.modelDesc();
    
    // the display class
    String displayClass;
    if (viewType != CommonConstants.NullType) { //AttributeDesc.Null_Type) {
      displayClass = viewType.getName();
    } else {
      displayClass = null;
    }
    
    DOMBasic dom = dodm.getDom();
    
    // the model config
    ModelConfig modelCfg = null;
    if (modelDesc.model() != CommonConstants.NullType) {
      /*v3.0
      modelCfg = ModelConfig.createInstance(modelDesc);
      dom.addObject(modelCfg, serialised);
      */
      modelCfg = createModelConfig(dodm, modelDesc, serialised);
    }

    ControllerConfig controllerCfg  = createControllerConfig(dodm, controllerDesc, serialised);
    
    // v3.0: additional properties
    PropertyDesc[] propDescs = attribDesc.props(); 
    boolean displayVisible = getPropertyValue(propDescs, 
        PropertyName.view_objectForm_dataField_visible, Boolean.class, Boolean.TRUE); 

    PropertySet props = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
    
    // create the linked region
    RegionLinking linkedRegion = new RegionLinking(
        fieldName, null, null, displayClass, parent, index, controllerCfg, modelCfg, visible
        , props // v3.0
        );

    // v3.0: needed for modules that need to process labels of all the domain attributes
    linkedRegion.setLabel(label);
    
    // create the title child region
    Region rt = new Region("title", label, null, linkedRegion, 1);
    
    // look up the module of the refering region (if available) and use it to 
    // set up other properties and to link to the region as a child
    // NOTE: if the attribute is not visible then the module may not be specified 
    ApplicationModule referringModule = addLinkedRegionChildLink(linkedRegion, domainType, compRegions, displayVisible);
    
    // TODO: consider removing referringModule.getViewCfg() from the properties setting below 
    // because this does not work with the use of subTypes (further below). 
    // The actual referringModule to use is only known at run-time by the user module. The viewConfig
    // of that module can be merged into linkedRegion there.
    if (referringModule != null) {
      // v2.7.4: support other configuration properties
      linkedRegion.setProperties(attribDesc, referringModule.getViewCfg());
    }
    
    // v3.2: support sub-types
    // if domainType has sub-types then also make linkedRegion the parent of the respective comp regions
    // of those sub-types' modules
    /*v3.3: improved to support type hierarchy (i.e descendant sub-types as well as direct sub-types)
    DSMBasic dsm = dodm.getDsm();
    Class[] subTypes = dsm.getSubClasses(domainType);
    if (subTypes != null) {
      // has sub-types
      for (Class sub : subTypes) {
        try {
          addLinkedRegionChildLink(linkedRegion, sub, compRegions, displayVisible);
        } catch (NotFoundException e) {
          // sub-type module may not be included: ignore 
        }
      }
    }
    */
    addLinkedRegionChildLinks(domainType, linkedRegion, compRegions, displayVisible);
    
    // set up properties 
    setUpRegionFieldProperties(linkedRegion, attribDesc);
    
    return linkedRegion;
  }

  /**
   * @modifies linkedRegion
   * @effects 
   *  if <tt>cls</tt> has direct domain sub-types
   *    call {@link #addLinkedRegionChildLink(RegionLinking, Class, Collection, boolean)} on each sub-type
   *  else
   *    do nothing
   *
   * @version 3.3
   */
  private void addLinkedRegionChildLinks(
      final Class cls,
      final RegionLinking linkedRegion,
      Collection<Region> compRegions, final boolean displayVisible) {
    DSMBasic dsm = dodm.getDsm();
    Class[] subTypes = dsm.getSubClasses(cls);
    if (subTypes != null) {
      // has sub-types
      for (Class sub : subTypes) {
        try {
          addLinkedRegionChildLink(linkedRegion, sub, compRegions, displayVisible);
          
          // (recursive): do the same for sub
          addLinkedRegionChildLinks(sub, linkedRegion, compRegions, displayVisible);
        } catch (NotFoundException e) {
          // sub-type module may not be included: ignore 
        }
      }
    }    
  }

  /**
   * @modifies linkedRegion
   * 
   * @effects <pre>
   *  let m = Module(domainType)
   *  if m is not null
   *    let r = m.compRegion
   *    make <tt>linkedRegion</tt> = parent of r
   *  
   *  return m or null of m is not found
   *  </pre>
   */
  private ApplicationModule addLinkedRegionChildLink(final RegionLinking linkedRegion, 
      final Class domainType, 
      Collection<Region> compRegions,
      final boolean displayVisible) throws NotFoundException {
    ApplicationModule referringModule = null; 
    
    try {
      referringModule = lookUpModule(domainType);
    } catch (NotFoundException e) {
      //if (visible) {
      if (displayVisible) {
        // attribute is visible: -> definitely a problem
        throw e;
      }
    }
    
    if (referringModule != null) {
//      // v2.7.4: support other configuration properties
//      linkedRegion.setProperties(attribDesc, referringModule.getViewCfg());
  
      // make linked region the parent of the target region associated to domainType
      String referedRegionName = referringModule.getName();
  
      // find the referred region (of the referred module) created for this type among the input compsRegion
      Region referedRegion = null;
      for (Region ccr : compRegions) {
        if (ccr.getName().equals(referedRegionName)) {
          // found the region
          referedRegion = ccr;
          break;
        }
      }
  
      if (referedRegion == null) {
        // not found in among the input compsRegion, try to retrieve from the data source 
        // (possible because modules are created in different groups and a module in one group 
        // , e.g. reports, may link to a shared region in another group, e.g. data)
        referedRegion = retrieveSharedRegion(referedRegionName);
        
        if (referedRegion == null) {
          // something wrong
          throw new NotFoundException(
              NotFoundException.Code.CHILD_REGION_NOT_FOUND,
              new Object[] {referedRegionName, "Components"});
        }
      }
  
      // make linked region (cr) the parent of the actual region found above
      // this will be used as a "bridge" to the child regions of this region
      referedRegion.addParent(linkedRegion, 2);
    }
    
    return referringModule;
  }

  /**
   * @effects 
   *  create and return a <tt>RegionLinking r</tt> for <tt>field</tt> (whose domain type is <tt>D</tt>) from the properties in 
   *  <tt>linkedRegionProps</tt>, such that:
   *    r is the parent of the component region of Module(D) 
   * @version 3.0
   * 
   * @deprecated as of v3.2; use {@link #createLinkedRegionWithSubTypeSupport(Field, Map, Collection)} instead
   */
  private Region createLinkedRegion(Field field, 
      Map linkedRegionPropsMap, 
      Collection<Region> compRegions) throws NotFoundException, NotPossibleException, DataSourceException {
    // retrieve the linked region properties 
    String fieldName = (String)
        linkedRegionPropsMap.get("fieldName");
    
    Label label = (Label)
        linkedRegionPropsMap.get("label");
    
    Region parent = (Region) linkedRegionPropsMap.get("parent");
    
    int index = (Integer) linkedRegionPropsMap.get("index");
    
    // the domain class that is the data type of field
    Class domainType = (Class) linkedRegionPropsMap.get("domainType");
    
    AttributeDesc attribDesc = (AttributeDesc) linkedRegionPropsMap.get("attribDesc");
    
    boolean serialised = (Boolean) linkedRegionPropsMap.get("serialised");
    
    boolean visible = attribDesc.isVisible();
    
        
    // create the region
    /*v2.7.2: process attribute desc*/
    Class viewType = attribDesc.type();
    ControllerDesc controllerDesc = attribDesc.controllerDesc();
    ModelDesc modelDesc = attribDesc.modelDesc();
    
    // the display class
    String displayClass;
    if (viewType != CommonConstants.NullType) { //AttributeDesc.Null_Type) {
      displayClass = viewType.getName();
    } else {
      displayClass = null;
    }
    
    DOMBasic dom = dodm.getDom();
    
    // the model config
    ModelConfig modelCfg = null;
    if (modelDesc.model() != CommonConstants.NullType) {
      /*v3.0
      modelCfg = ModelConfig.createInstance(modelDesc);
      dom.addObject(modelCfg, serialised);
      */
      modelCfg = createModelConfig(dodm, modelDesc, serialised);
    }

    ControllerConfig controllerCfg  = createControllerConfig(dodm, controllerDesc, serialised);
    
    // v3.0: additional properties
    PropertyDesc[] propDescs = attribDesc.props(); 
    boolean displayVisible = getPropertyValue(propDescs, 
        PropertyName.view_objectForm_dataField_visible, Boolean.class, Boolean.TRUE); 

    PropertySet props = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
    
    // create the linked region
    RegionLinking linkedRegion = new RegionLinking(
        fieldName, null, null, displayClass, parent, index, controllerCfg, modelCfg, visible
        , props // v3.0
        );

    // v3.0: needed for modules that need to process labels of all the domain attributes
    linkedRegion.setLabel(label);
    
    // create the title child region
    Region rt = new Region("title", label, null, linkedRegion, 1);
    
    // look up the module of the refering region (if available) and use it to 
    // set up other properties and to link to the region as a child
    // NOTE: if the attribute is not visible then the module may not be specified 
    
    ApplicationModule referringModule = null; 
    
    try {
      referringModule = lookUpModule(domainType);
    } catch (NotFoundException e) {
      //if (visible) {
      if (displayVisible) {
        // attribute is visible: -> definitely a problem
        throw e;
      }
    }
    
    if (referringModule != null) {
      // v2.7.4: support other configuration properties
      linkedRegion.setProperties(attribDesc, referringModule.getViewCfg());
  
      // make linked region the parent of the target region associated to domainType
      String referedRegionName = referringModule.getName();
  
      // find the referred region (of the referred module) created for this type among the input compsRegion
      Region referedRegion = null;
      for (Region ccr : compRegions) {
        if (ccr.getName().equals(referedRegionName)) {
          // found the region
          referedRegion = ccr;
          break;
        }
      }
  
      if (referedRegion == null) {
        // not found in among the input compsRegion, try to retrieve from the data source 
        // (possible because modules are created in different groups and a module in one group 
        // , e.g. reports, may link to a shared region in another group, e.g. data)
        referedRegion = retrieveSharedRegion(referedRegionName);
        
        if (referedRegion == null) {
          // something wrong
          throw new NotFoundException(
              NotFoundException.Code.CHILD_REGION_NOT_FOUND,
              new Object[] {referedRegionName, "Components"});
        }
      }
  
      // make linked region (cr) the parent of the actual region found above
      // this will be used as a "bridge" to the child regions of this region
      referedRegion.addParent(linkedRegion, 2);
    }
    
    // set up properties 
    setUpRegionFieldProperties(linkedRegion, attribDesc);
    
    return linkedRegion;
  }
  
  /**
   * @effects 
   *  if exists a {@link PropertyDesc} in <tt>propDescs</tt> whose name is <tt>propName</tt>
   *  and whose type is <tt>valueType</tt>
   *    return its value
   *  else
   *    return defaultVal
   * @version 3.0
   */
  protected <T> T getPropertyValue(PropertyDesc[] propDescs,
      PropertyName propName, Class<T> valueType, T defaultVal) {
    if (propDescs!= null && propDescs.length > 0) {
      for (PropertyDesc propDesc : propDescs) {
        if (propDesc.name() == propName && valueType.isAssignableFrom(propDesc.valueType())) {
          return (T) Property.parseValue(propDesc.valueAsString(), propDesc.valueType());
        }
      }
    }
    
    return defaultVal;
  }

  /**
   * @effects 
   *  look up and return the <tt>Components</tt> region
   */
  protected Region lookUpComponentsRegion() {
    if (regionComponents == null) {
      regionComponents = lookUpRegion(RegionName.Components.name());
    }
    
    return regionComponents;
  }
  
  /**
   * @effects return <tt>Region</tt> object whose name is <tt>name</tt>
   */
  //TODO: (performance) cache look-up queries or regions 
  public Region lookUpRegion(String name) throws NotFoundException {
    Query q = new Query(new Expression("name", Op.EQ, name));

    Collection<Region> list = dodm.getDom().getObjects(Region.class, q);

    if (list == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND,
          new Object[] {Region.class, q.toString()});
    }

    return list.iterator().next();
  }

  public Region lookUpRegion(String name, String parent)
      throws NotFoundException {
    Query q = new Query(new Expression("name", Op.EQ, name));

    Collection<Region> list = dodm.getDom().getObjects(Region.class, q);

    if (list == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND,
          new Object[] {Region.class, q.toString()});
    }

    return list.iterator().next();
  }

  /**
   * @requires 
   *  moduleMap != null
   * @effects 
   *  if exists modules whose {@link ModuleDescriptor} classes are <tt>moduleDescClasses</tt>
   *    return them as an array
   *  
   *  <p>throws NotFoundException if any one of the modules is not found.
   */
  protected ApplicationModule[] lookUpModules(Class[] moduleDescClasses)
      throws NotFoundException {
    ApplicationModule[] modules = new ApplicationModule[moduleDescClasses.length];
    
    Class modCls;
    ApplicationModule mod;
    for (int i = 0; i < modules.length; i++) {
      modCls = moduleDescClasses[i];
      mod = moduleMap.get(modCls);
      if (mod == null) {
        throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND,
            new Object[] { ApplicationModule.class, modCls.getName()});        
      }
      
      modules[i] = mod;
    }

    return modules;
  }
  
  // v2.8: removed
//  /**
//   * @effects 
//   *  retrieve (load, if necessary, from the data source) 
//   *  and return the module that corresponds to the domain class <tt>domainCls</tt>, 
//   *  or throws NotFoundException if that module does not exist.
//   */
//  public ApplicationModule retrieveModule(Class domainCls)
//      throws NotFoundException, NotPossibleException, DataSourceException {
//    // v2.7.4: return ApplicationToolKit.loadModule(dodm, domainCls);
//    
//    // create a join between ApplicationModule, ModelConfig with ModelConfig.domainClass = domainCls
//    Class[] joinClasses = {
//        ApplicationModule.class,
//        ModelConfig.class
//    };
//    String[] assocNames = {
//        "module-has-modelConfig"
//    };
//
//    String clsName = domainCls.getName();
//    
//    ObjectJoinExpression jexp = QueryToolKit.createJoinExpression(dodm, joinClasses, assocNames, 
//        ModelConfig.AttributeName_domainClass,
//        Op.EQ,
//        clsName);
//    
//    Query q = new Query(jexp);
//
//    DOMBasic dom = dodm.getDom();
//    Class<ApplicationModule> c = ApplicationModule.class;
//    
//    Map<Oid,ApplicationModule> modules = dom.retrieveObjects(c, q);
//    
//    if (modules == null) {
//      throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND,
//          "Không tìm thấy mô-đun của lớp dữ liệu {0}", domainCls);
//    }
//
//    return modules.values().iterator().next();
//  }
  
  /**
   * @effects 
   *  return the module that corresponds to the domain class <tt>domainCls</tt>, 
   *  or throws NotFoundException if that module does not exist.
   */
  public ApplicationModule lookUpModule(Class domainCls)
      throws NotFoundException {
    Query q = new Query(new Expression("domainClass", Op.EQ, domainCls.getName()));

    Collection<ApplicationModule> list = dodm.getDom().getObjects(ApplicationModule.class, q);

    if (list == null) {
      throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND,
          new Object[] { domainCls});
    }

    return list.iterator().next();
  }
  
  private Style lookUpStyle(String styleName) throws NotFoundException {
    Query q = new Query(new Expression("name", Op.EQ, styleName));

    Collection<Style> list = dodm.getDom().getObjects(Style.class, q);

    if (list == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND,
          "Không tìm thấy đối tương: {0}", q.toString());
    }

    return list.iterator().next();
  }

  protected Label lookUpLabelByValue(String labelVal) throws NotFoundException {
    Query q = new Query(new Expression("value", Op.EQ, labelVal));

    Class cls = Label.class;
    Collection<Label> list = dodm.getDom().getObjects(cls, q);

    if (list == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND,
          "Không tìm thấy đối tương: {0}({1})", cls, q.toString());
    }

    return list.iterator().next();    
  }
  
  /**
   * @effects 
   *  adds to Region's object store <tt>reg</tt>, its children (if any) and its excluded regions (if any).
   *   
   */
  protected void addRegion(DODMBasic schema, Region reg,
      boolean withParentMap,
      boolean serialised  // v2.8
      ) throws DataSourceException {
    // first add the region
    DOMBasic dom = schema.getDom();
    
    dom.addObject(reg, serialised);

    // if withParents and there are parents then add them
    if (withParentMap) {
      List<RegionMap> parents = reg.getParents();
      if (parents != null) {
        for (RegionMap parentMap : parents) {
          // only add the map, no need to add the parent
          dom.addObject(parentMap, serialised);
        }
      }
    }

    // if there are children then also add them
    // but because child regions can be shared so we will ignore those
    // whose ids are lower than this region's id
    List<RegionMap> children = reg.getChildren();
    if (children != null) {
      Region child;
      for (RegionMap childMap : children) {
        child = childMap.getChild();
        if (child.getId() > reg.getId()) {
          // add the child region first (without their parents)
          addRegion(schema, child, false, serialised);
        }
        // then the map
        dom.addObject(childMap, serialised);
      }
    }
    
    // if there are exclusion then also add them
    // add each target region only if it is not already added
    List<ExclusionMap> exclusion = reg.getExclusion();
    if (exclusion != null) {
      Region target;
      for (ExclusionMap targetMap : exclusion) {
        target = targetMap.getTarget();
        if (target.getId() > reg.getId()) {
          // add the child region first (without their parents)
          addRegion(schema, target, false, serialised);
        }
        // then the map
        dom.addObject(targetMap, serialised);
      }
    }
  }

  /**
   * This method differs from {@link #addRegion(DODMBasic, Region, boolean, boolean)} in that 
   * it does not adds the associated children, exclusion regions. 
   * 
   * @effects
   *  adds <tt>reg</tt> to <tt>dodm.getDom()</tt> with the <tt>serialised</tt> setting.
   *  
   * @version 3.0
   */
  protected void addRegion(DODMBasic dodm, Region reg, 
      boolean serialised  // v2.8
      ) throws DataSourceException {
    // first add the region
    DOMBasic dom = dodm.getDom();
    
    dom.addObject(reg, serialised);
  }
  
  /**
   * <b>IMPORTANT</b>: This method MUST NOT be used together with {@link #addRegion(DODMBasic, Region, boolean, boolean)}, 
   * because that other method includes the behaviour of this method. 
   * 
   * @version 3.0
   */
  protected void addRegionParents(DODMBasic schema, Region reg,
      boolean serialised  // v2.8
      ) throws DataSourceException {
    // first add the region
    DOMBasic dom = schema.getDom();
    
    List<RegionMap> parents = reg.getParents();
    Class<RegionMap> RM = RegionMap.class;
    if (parents != null) {
      for (RegionMap parentMap : parents) {
        if (!dom.existObject(RM, parentMap)) {
          // parent Map not yet added 
          // only add the map, no need to add the parent
          dom.addObject(parentMap, serialised);
        }
      }
    }
  }
  
  /**
   * <b>IMPORTANT</b>: This method MUST NOT be used together with {@link #addRegion(DODMBasic, Region, boolean, boolean)}, 
   * because that other method includes the behaviour of this method. 
   * 
   * @version 3.0
   */
  protected void addRegionChildren(DODMBasic dodm, Region reg,
      boolean serialised  // v2.8
      ) throws DataSourceException {
    // first add the region
    DOMBasic dom = dodm.getDom();
    
    // if there are children then also add them
    // but because child regions can be shared so we will ignore those
    // whose ids are lower than this region's id
    List<RegionMap> children = reg.getChildren();
    if (children != null) {
      //Region child;
      for (RegionMap childMap : children) {
        Region child = childMap.getChild();
        if (child.getId() > reg.getId()) {
          // add the child region first (without their parents)
          addRegion(dodm, child, false, serialised);
        }
        // then the map
        dom.addObject(childMap, serialised);
      }
    }
  }
  
  /**
   * <b>IMPORTANT</b>: This method MUST NOT be used together with {@link #addRegion(DODMBasic, Region, boolean, boolean)}, 
   * because that other method includes the behaviour of this method. 
   * 
   * @effects 
   *  Add a specific child <tt>child</tt> of <tt>parent</tt> to data source, together with its {@link RegionMap}.
   * @version 5.2
   */
  protected void addRegionChild(DODMBasic dodm, Region parent, Region child,
      boolean serialised) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    // because child regions can be shared so we will ignore those
    // whose ids are lower than this region's id
    if (child.getId() > parent.getId()) {
      // add the child region first (without their parents)
      addRegion(dodm, child, false, serialised);
    }
    
    // then the region-map
    RegionMap childMap = parent.getChildRegionMap(child);
    dom.addObject(childMap, serialised);
  }

  /**
   * <b>IMPORTANT</b>: This method MUST NOT be used together with {@link #addRegion(DODMBasic, Region, boolean, boolean)}, 
   * because that other method includes the behaviour of this method. 
   * 
   * @version 3.0
   */
  protected void addRegionExclusion(DODMBasic schema, Region reg, 
      boolean serialised  // v2.8
      ) throws DataSourceException {
    // first add the region
    DOMBasic dom = schema.getDom();
    
    // if there are exclusion then also add them
    // add each target region only if it is not already added
    List<ExclusionMap> exclusion = reg.getExclusion();
    if (exclusion != null) {
      Region target;
      for (ExclusionMap targetMap : exclusion) {
        target = targetMap.getTarget();
        if (target.getId() > reg.getId()) {
          // add the child region first (without their parents)
          addRegion(schema, target, false, serialised);
        }
        // then the map
        dom.addObject(targetMap, serialised);
      }
    }
  }
  
  /**
   * @effects 
   *  create a <tt>Label</tt> object of the type suitable for 
   *    the language <tt>lang</tt> whose value is <tt>val</tt> and 
   *    store the object to the database
   *    return the label object 
   */
  /*v2.7: support style 
  public Label createLabel(String lang, String val)
  */
  public Label createLabel(String lang, String val, Style style,
      boolean serialised  // v2.8
      )
      throws DataSourceException, NotPossibleException {

    String pkgPrefix = RegionConstants.class.getPackage().getName()+".lang."+lang+".";
    String lblClassName = pkgPrefix+"Label";
    try {
      // create label object
      Class labelClass = Class.forName(lblClassName);
      Constructor<Label> cons = labelClass.getDeclaredConstructor(String.class);
      Label label = cons.newInstance(val);
      
      label.setStyle(style); // v2.7
      
      // store label into to database
      dodm.getDom().addObject(label, serialised);
      
      return label;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, 
          e, e.getMessage());
    }
  }

  /**
   * This is the opposite to {@link #createModulesByView(Object[][])}
   * 
   * @effects remove module, region and menu bar configurations of the modules
   *          in <tt>moduleCfgs</tt> from the configuration database.
   */
  public void clearModules(Class[] moduleCfgs) throws DataSourceException,
      NotFoundException {

    String moduleName;
    LAName action;
    Class controller;
    RegionType type;
    RegionName parentName;
    Region parent = null;
    Query q;
    Class moduleClass = ApplicationModule.class;
    Class regionToolMenuClass = RegionToolMenuItem.class;
    Class regionGUIClass = RegionGui.class;
    Class regionMapClass = RegionMap.class;
    Class regionClass = Region.class;
    Class viewGUIClass = ModuleDescriptor.class;

    Class viewClass;

    Op opEq = Op.EQ;
    Op opCon = Op.CONTAINS;
    ModuleDescriptor moduleCfg;
    Class domainCls;
//    for (Class domainCls : moduleCfgs) {
//      viewClass = Toolkit.getViewClass(domainCls);
//      guiCfg = (ViewGUI) viewClass.getAnnotation(viewGUIClass);
    for (Class viewCls : moduleCfgs) {
      moduleCfg = (ModuleDescriptor) viewCls.getAnnotation(viewGUIClass);
      if (moduleCfg != null) {
        moduleName = moduleCfg.name();
        //action = moduleCfg.menuAction();
        domainCls = moduleCfg.modelDesc().model();
        if (domainCls == CommonConstants.NullType) //ModuleDescriptor.NullType)
          domainCls = null;
        controller = moduleCfg.controllerDesc().controller();
        type = moduleCfg.viewDesc().viewType();
        parentName = moduleCfg.viewDesc().parent();

        if (parentName != RegionName.Null)
          parent = lookUpRegion(parentName.name());

        // remove module configuration
        //String moduleName = getModuleName(controller, domainCls, moduleName, type);
        q = new Query(new Expression("name", opEq, moduleName));
        dodm.getDom().deleteObjects(moduleClass, q);

        // remove tool menu item
        if (parent != null) {
          // remove tool menu region
          String toolName = moduleName; //getToolMenuItemName(action, moduleName);
          q = new Query(new Expression("parent", opEq, parent,
              Expression.Type.Object));
          Collection<RegionMap> regionMaps = dodm.getDom().getObjects(regionMapClass, q);
          if (regionMaps != null) {
            for (RegionMap regionMap : regionMaps) {
              Region toolRegion = regionMap.getChild();
              if (toolRegion.getName().equals(toolName)) {
                // delete both the region and its map
                dodm.getDom().deleteObject(regionMap, regionMapClass);
                // schema.getDom().deleteObject(toolRegion,regionToolMenuClass);
                deleteRegion(toolRegion, regionToolMenuClass, false);
                break;
              }
            }
          }
        }

        if (type != RegionType.Null) {
          // remove gui region
          q = new Query(new Expression("name", opEq, moduleName));
          Collection<Region> regions = dodm.getDom().getObjects(regionGUIClass, q);
          if (regions != null) {
            // one GUI region matching the name
            for (Region r : regions)
              // recursively remove this region
              // but keep the child regions (because these are shared regions)
              deleteRegion(r, regionGUIClass, false);
          }

          // remove components region (if any)
          if (domainCls != null) {
            // v2.5.4: use module name
            // String regionName = schema.getDom().getDomainClassName(domainCls);
            String regionName = moduleName;
            q = new Query(new Expression("name", opEq, regionName));
            regions = dodm.getDom().getObjects(regionClass, q);
            if (regions != null) {
              // one region matching the name
              for (Region r : regions) {
                // reg = (Region) regions.get(0);
                // recursively remove this region and all of its children and
                // grandchildren, etc.
                deleteRegion(r, regionClass, true);
              }
            }
          }
        }
      }
    }
  }

  private void deleteRegion(Region region, Class regionClass, boolean withChild)
      throws DataSourceException {
    final Class regionMapClass = RegionMap.class;

    // delete region
    dodm.getDom().deleteObject(region, regionClass);

    // delete children
    if (withChild) {
      final int regId = region.getId();
      List<RegionMap> childMap = region.getChildren();
      Region child;
      if (childMap != null) {
        for (RegionMap cm : childMap) {
          // recursive: delete own child region (id > the region's id) and so on
          child = cm.getChild();
          if (child.getId() > regId)
            deleteRegion(child, child.getClass(), withChild);

          dodm.getDom().deleteObject(cm, regionMapClass);
        }
      }
    }
  }

//  /**
//   * @effects 
//   *  load all security and non-security 
//   *  configuration defined by <tt>schema</tt> from 
//   *  the database.  
//   */
//  public ApplicationModule loadMainModule() throws DataSourceException, NotFoundException {
//    Class<ApplicationModule> c = ApplicationModule.class;
//    String attribName = ApplicationModule.AttributeName_type;
//    Op op = Op.EQ;
//    ModuleType val = ModuleType.DomainMain;
//    ApplicationModule module = dodm.getDom().retrieveObject(c, attribName, op, val);
//    
//    return module;
//  }
//  
//  /**
//   * @requires 
//   *  configuration schema has been registered
//   * @effects 
//   *  load the <b>functional</b> modules and all the application resources needed for them.
//   *  return the modules or <tt>null</tt> if no such modules are found.
//   * @version 2.7.1 
//   */
//  public Collection<ApplicationModule> loadFunctionalModules() throws NotPossibleException, DataSourceException {
//    Collection<ApplicationModule> modules = new ArrayList();
//    
//    Class<ApplicationModule> c = ApplicationModule.class;
//    String attribName = ApplicationModule.AttributeName_type;
//    Op op = Op.EQ;
//    ModuleType val = ModuleType.DomainData;
//    
//    Collection<ApplicationModule> dataModules = dodm.getDom().retrieveObjects(c, attribName, op, val);
//    
//    if (dataModules != null)
//      modules.addAll(dataModules);
//    
//    val = ModuleType.DomainReport;
//    Collection<ApplicationModule> reportModules = dodm.getDom().retrieveObjects(c, attribName, op, val);
//    if (reportModules != null) {
//      modules.addAll(reportModules);
//    }
//    
//    return modules.isEmpty() ? null : modules;
//  }
  
  /**
   * @effects loads the configuration objects of <code>cfgClasses</code> into
   *          the <code>schema</code>; throws <code>DBException</code> if an
   *          error occured
   */
  public void loadConfiguration() throws DataSourceException {
    // load the main classes
    DSMBasic dsm = dodm.getDsm();
    DOMBasic dom = dodm.getDom();
    
    Class[] cfgClasses = getConfigurationSchema();
    
    for (Class cfgCls : cfgClasses) {
      if (!dsm.isTransient(cfgCls)) {
        dom.retrieveObjectsWithAssociations(cfgCls);
        //v2.7 schema.getDom().loadObjectHierarchyWithAssociations(cfgCls);
      }
    }

    // for each class that has collection-type attributes, load the referenced
    // objects
    for (Class cfgCls : cfgClasses) {
      if (!dsm.isTransient(cfgCls)) {
        dom.retrieveAssociatedObjects(cfgCls);
      }
    }
  }
  
  /**
   * @effects 
   *  return the <tt>Configuration</tt>-related domain classes (i.e. {@link #cfgClasses})
   */
  protected Class[] getConfigurationSchema() {
    return cfgClasses;
  }
  
  /**
   * @effects 
   *  return the <tt>Configuration</tt>-related domain classes (i.e. {@link #getConfigurationSchema()})
   */
  public static Class[] getConfigurationClasses() {
    return cfgClasses;
  }
  
  /**
   * @effects 
   *    print log message for <tt>code</tt>
   */
  protected void log(MessageCode code, String message, Object...data) {
    if (debug || loggingOn) {
      //TODO: convert message based on language
      if (data != null && data.length > 0) {
        Format fmt = new MessageFormat(message);
        message = fmt.format(data);
        //message = String.format(message, data);
      }
      
      System.out.println(message);
    }
  }
}
