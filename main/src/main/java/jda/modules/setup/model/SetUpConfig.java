package jda.modules.setup.model;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.DAssoc;
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
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.ApplicationModuleMap;
import jda.modules.mccl.conceptmodel.module.DomainApplicationModule;
import jda.modules.mccl.conceptmodel.view.ExclusionMap;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.conceptmodel.view.RegionMap;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.conceptmodel.view.UserRegion;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.SetUpDesc;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.modules.mccl.syntax.controller.ControllerDesc;
import jda.modules.mccl.syntax.model.ModelDesc;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.security.authentication.ModuleSecurity;
import jda.modules.security.authentication.login.ModuleLogin;
import jda.modules.security.authentication.logout.ModuleLogout;
import jda.modules.security.def.DomainUser;
import jda.modules.setup.commands.SetUpCommand;
import jda.modules.setup.init.RegionConstants;
import jda.modules.setup.init.StyleConstants;
import jda.modules.setup.model.SetUpBasic.MessageCode;
import jda.modules.setup.modules.ModuleConfiguration;
import jda.modules.setup.modules.applicationmodule.ModuleApplicationModule;
import jda.modules.setup.modules.controllerconfig.ModuleControllerConfig;
import jda.modules.setup.modules.dodmconfig.ModuleDodmConfig;
import jda.modules.setup.modules.organisation.ModuleOrganisation;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JDataField;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySetFactory;

/**
 * A setup helper class responsible for setting up the security resources.
 * 
 * @author dmle
 * 
 */
public class SetUpConfig extends SetUpConfigBasic {

  // v2.8 
  private static Class[] cfgClasses = new Class[] { //
    Label.class, 
    jda.modules.setup.init.lang.vi.Label.class,
    jda.modules.setup.init.lang.en.Label.class, //
    Style.class, //
    // v2.7.2:
    Property.class,
    PropertySet.class,
    // v2.8: added DomainUser 
    DomainUser.class,
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
    // v2.8: added for DomainUser
    UserRegion.class,
//    // v3.1: added for Role
//    RoleModule.class,
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
  
  /**
   * Use this constructor to load configuration to run the application
   */
  public SetUpConfig(DODMBasic schema) {
    super(schema);
  }
  
  /**
   * Use this constructor to create configuration at set-up
   */
  public SetUpConfig(DODMBasic schema, Configuration config) {
    super(schema, config);
  }

  @Override
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
   *  return module configution classes needed to operate on <tt>Configuration</tt>
   * @version 2.8 
   */
  public static Class[] getConfigurationModules() {
    return new Class[] {
        ModuleConfiguration.class,
        ModuleDodmConfig.class, // v2.8
        ModuleApplicationModule.class, 
        ModuleOrganisation.class, 
        ModuleControllerConfig.class, // v2.6.4.b
    };
  }
  
  /**
   * @effects 
   *  register the <tt>Configuration</tt> related classes to the domain schema of the application.
   *  <br>if <tt>serialised = true /\ createIfNotExist = true </tt>
   *    create the class store for each class if it does not yet exist
   *   
   * @version 2.8.
   */
  @Override
  public void registerConfigurationSchema(SetUpBasic su, 
      final boolean serialised, 
      final boolean createIfNotExist) throws DataSourceException {
    if (isRegisteredConfigurationSchema())
      return;
    
    log(MessageCode.UNDEFINED,
        //"Tạo mô hình"
        "Registering configuration schema"
        );

    boolean read = false;
    Class[] cfgClasses = getConfigurationSchema(); //getConfigurationClasses();
    
    if (debug) log(MessageCode.UNDEFINED,"  Base configuration classes...");
    su.registerClasses(cfgClasses, serialised, createIfNotExist, read);
    
    if (debug) log(MessageCode.UNDEFINED,"  Default module classes...");
    if (defaultModuleDescriptors != null) {
      Class[] defaultClasses = SetUpBasic.getModelClasses(
          // v2.8: getDodm().getDsm(), 
          defaultModuleDescriptors);
      su.registerClasses(defaultClasses, serialised, createIfNotExist, read);
    }
    
    setIsRegisteredConfigurationSchema(true);
  }

  /**
   * @effects 
   *  if style definition class is provided
   *    read styles from that class
   *  else
   *    read default styles
   * @version 2.7.4
   */
  @Override
  protected List<Style> getStyleDefs() {
    Configuration config = getConfiguration();
    Class styleDefCls = config.getStyleDefClassObject();
    
    if (styleDefCls != null) {
      // use defined styles
      return Toolkit.getConstantObjects(styleDefCls,
          Style.class);
    } else {
      // use default
      return Toolkit.getConstantObjects(StyleConstants.class,
          Style.class);
    }
  }

  /**
   * @effects if c1 depends on c2, i.e. there is an association from
   *          <tt>c1</tt> to <tt>c2</tt> AND that <tt>c2</tt>'s object form is nested
   *          in <tt>c1</tt>'s (e.g. StudentClass depends on Student
   *          because Student form is nested inside StudentClass's) return <tt>true</tt> else
   *          return <tt>false</tt>
   * @version 
   *  <ul>
   *    <li>2.6.4.b: check the module configs of two classes first to be certain that 
   *  the has-a relationship between the attributes are actually used in the modules
   *    <li>2.7.4: support 1:1 associations 
   *    </ul>
   */
  protected boolean has(Class c1, Collection<Class> cModuleDescs, Class c2) {
    /*v2.7.4: use association instead of isCollection to check dependency
    List<DomainConstraint> c1Attributes = dodm.getDsm().getDomainConstraints(c1); 
    */
    DODMBasic dodm = getDodm();
    
    DSMBasic dsm = dodm.getDsm();
    Map<DAttr,DAssoc> assocs = dsm.getAssociations(c1);
    
    // if no associations then return immediately
    if (assocs == null) {
      // should not happen
      return false;
    }

    // find the module that actually uses one of c's attributes
    Collection<Field> modAttribs;
    DAttr attrib1; 
    DAssoc assoc;
    Class assocCls;
    AttributeDesc modAttribDesc;
    
    MOD: for (Class mod : cModuleDescs) {
      modAttribs = dodm.getDsm().getAnnotatedSerialisableFields(mod, AttributeDesc.class);
      
      if (modAttribs == null)
          continue; // ignore
      
      MODFIELD: for (Field modAttrib : modAttribs) {
        
        // if display class of modAttrib's config is not a container
        // then ignore
        modAttribDesc = modAttrib.getAnnotation(AttributeDesc.class);
        if (!JDataContainer.class.isAssignableFrom(modAttribDesc.type()))
          continue MODFIELD;
        
        ASSOC: for (Entry<DAttr,DAssoc> e: assocs.entrySet()) {
          attrib1 = e.getKey();
          assoc = e.getValue();
          
          // if c1 is depended on the association then ignore
          if (!dsm.isDependedOn(c1, assoc)) {
            if (attrib1.name().equals(modAttrib.getName())) {
              // found the attribute
              // check if association refers to c2, if so return true immediately
              assocCls = assoc.associate().type();
              
              // check if same class or a sub-class
              if (assocCls != c2) {
                try {
                  assocCls.asSubclass(c2);
                  // depends
                  return true;
                } catch (ClassCastException ex) {
                  // not a sub-class
                }
              } else {
                // depends
                return true;
              }
              
              // c1 does not depend on c2 w.r.t mod
              // stop searching
              break ASSOC;
            }
          }            
        } // end ASSOC
      } // end FIELD
      
      // TODO: should we continue to search in other modules 
      return false;
    } // end MOD
    return false;
  }

  @Override
  protected void createLoginModules(SetUpBasic su, 
      // v3.0: Map<String, Label> labelMap,
      boolean serialised) throws DataSourceException, NotFoundException {
    log(MessageCode.UNDEFINED,
        //"Khởi tạo login mô-đun"
        "Creating the login module"
        );

    Class[] loginModules = { 
        ModuleSecurity.class, 
        ModuleLogin.class,
        ModuleLogout.class,
        };
    
    //v2.7.4: register domain classes of the modules
    su.registerClasses(loginModules);
    
    // v3.0: load labels from file 
    Map<String, Label> labelMap = 
        su.getModuleLabels(loginModules); 
        
    // create the modules
    createModules(su, loginModules, labelMap, serialised);
  }

  @Override
  protected void postSetUpModule(SetUpBasic su, Class moduleDescrCls, ModuleDescriptor moduleCfg) throws NotPossibleException {
    SetUpDesc moduleSetUpDesc = moduleCfg.setUpDesc();
    
    Class postSetUpCmdCls = moduleSetUpDesc.postSetUp();
    
    if (postSetUpCmdCls != CommonConstants.NullType) {
      // run it
      SetUpCommand postSetUpCmd = SetUpCommand.createInstance(postSetUpCmdCls, su, moduleDescrCls);
      
      postSetUpCmd.run();
    }
  }
  
  /**
   * @effects 
   *  create in data source a <tt>ControllerConfig</tt> from <tt>controllerDesc</tt>
   * @version 2.7.4 
   */
  // TODO: this implementation looks very similar to the super-type's method!!!!
  @Override
  protected ControllerConfig createControllerConfig(DODMBasic dodm,
      ControllerDesc controllerDesc, boolean serialised) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    //TODO: v5.0 this implementation is now moved to the super class
    // so it can be removed
    
    // support property set
    PropertyDesc[] propDescs = controllerDesc.props();
    
    PropertySet pset = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);

    // create controller config
    ControllerConfig ctlCfg = ControllerConfig.createInstance(controllerDesc, pset);
    
    dom.addObject(ctlCfg, serialised);
    
    return ctlCfg;
  }
  
  /**
   * @effects 
   *  create in data source a <tt>ModelConfig</tt> from <tt>modelDesc</tt>
   * @version 3.0 
   */
  @Override
  protected ModelConfig createModelConfig(DODMBasic dodm,
      ModelDesc modelDesc, boolean serialised) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    // support property set
    PropertyDesc[] propDescs = modelDesc.props();
    /*v3.0: moved to method  
    PropertySet pset = null;
    
    if (propDescs.length > 0) {
      pset = new PropertySet(null, PropertySetType.Annotation);
      
      Property prop;
      for (PropertyDesc pd : propDescs) {
        prop = Property.createInstance(pd, pset);
        
        pset.addProperty(prop);
      }
      
      createPropertySet(dodm, pset, serialised, 0);
    } 
     */
    PropertySet pset = PropertySetFactory.createPropertySet(dodm, serialised, propDescs);
    
    // create controller config
    ModelConfig modelCfg = ModelConfig.createInstance(modelDesc, pset);
    
    dom.addObject(modelCfg, serialised);
    
    return modelCfg;
  }

  /**
   * @requires
   *  <tt>module</tt> is the current module of <tt>domainClass</tt> 
   *  
   * @effects
   *  similar to super type's method except that it also support multi-valued data fields.
   *   
   * @version 
   * - 3.2<br>
   * - 5.1: added parameter module and improve label look up 
   */
  @Override
  protected Region createRegionField(ApplicationModule module, // v5.1
      Class domainClass, Field field, int index,
      AttributeDesc attribDesc, Region parent, String lang,
      Map<String, Label> labelMap, List<Map> linkedRegionProps,
      boolean serialised) throws NotPossibleException, DataSourceException {
    String labelStr;
    String fieldName = field.getName();
    
    DODMBasic dodm = getDodm();
    
    DOMBasic dom = dodm.getDom();
    
    // v5.1: Label label;

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
    boolean isExplicitContainer=false;  // v3.2: renamed from isContainerType
    boolean isExplicitDataField=false;
    
    if (viewType != CommonConstants.NullType) { //AttributeDesc.Null_Type) {
      viewTypeName = viewType.getName();
      //isExplicitContainer = !JDataField.class.isAssignableFrom(viewType);
      isExplicitDataField = JDataField.class.isAssignableFrom(viewType);
      isExplicitContainer = !isExplicitDataField;
    } else {
      viewTypeName = null;
    }

    labelStyle = getLabelStyle(attribDesc);
    
    // get label from the label map or from the field config
    // v5.1: improved to support the case that labelMap != null but the labelId is not specified in labelMap
    // (possibly because now language config file was defined)
    // the owner MCC, which may be an ancestor MCC
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
    
    if (//v5.1: labelMap != null
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
      
      // v5.1: unlikely to happen now
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
    
    if (dataType.isCollection()
        && !isExplicitDataField // v3.2: added this case to support multi-valued field
        ) {
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
    } else if (dataType.isDomainType() && isExplicitContainer) {
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
      
      // v2.7: emphasise the linked label 
      if (dataType.isDomainType()
          || dataType.isCollection() // v3.2: support multi-valued fields 
          ) {
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

  @Override
  protected Map<Region, Map<ModuleDescriptor, Region>> getOtherRegionsMap() {
    // support other regions
    
    Map<Region, Map<ModuleDescriptor, Region>> otherRegMap = new HashMap<>();

    // SidePane:
    Region sidePaneReg = lookUpRegion(RegionName.SidePane.name());
    
    otherRegMap.put(sidePaneReg, null);
    
    //TODO ? support other regions here when needed
    
    return otherRegMap;
  }

  /**
   * @modifies each Region in myOtherRegionMap.keys()
   *  
   * @effects 
   *  for each Region r in myOtherRegionMap.keys()
   *    create a child Region c of r for <tt>module</tt> and puts (r,c) back into myOtherRegionMap
   *  
   * @version 5.2
   */
  @Override
  protected void createOtherDataRegions(final ApplicationModule module,
      final ModuleDescriptor moduleCfg, 
      final Map<Region, Map<ModuleDescriptor, Region>> otherRegionsMap,
      final boolean serialisedConfig) throws DataSourceException {

    // SidePane:
    Region regSidePane = lookUpRegion(RegionName.SidePane.name());
    Map<ModuleDescriptor, Region> regMap = null;
    
    if (otherRegionsMap.containsKey(regSidePane)) {
      RegionGui viewCfg = module.getViewCfg();
      // side panel region:
      AlignmentX sidePaneLoc = viewCfg.getProperty(PropertyName.view_region_sidePane, AlignmentX.class, null);
      
      if (sidePaneLoc != null) {
        regMap = otherRegionsMap.get(regSidePane);
        if (regMap == null) { 
          regMap = new HashMap<>();
          otherRegionsMap.put(regSidePane, regMap);
        }
        
        // this module uses a side pane: create its region
        DODMBasic dodm = getDodm();
        String moduleName = module.getName();
        String regName = RegionConstants.genSidePaneRegionNameForModule(moduleName);
        Region sidePaneReg = new Region(regName, null, null, regSidePane, 1);
        sidePaneReg.setAlignX(sidePaneLoc);
        
        regMap.put(moduleCfg, sidePaneReg);
        /* dont serialise sidePaneReg yet: serialise them altogether later
        boolean serialised;
        if (!serialisedConfig)  // use config-wise setting
          serialised = false;
        else  // use module-specific setting (if config-wise setting is set to true)
          serialised = !moduleCfg.isMemoryBased();
        
        addRegion(dodm, sidePaneReg, serialised);
        */
      }
    }
    
    //TODO ? support other regions here when needed
  }

  @Override
  protected void addOtherRegions(DODMBasic dodm, Map<Region, Map<ModuleDescriptor, Region>> otherRegionsMap, boolean serialisedConfig) throws DataSourceException {
    boolean serialised;
    for (Entry<Region,Map<ModuleDescriptor, Region>> e : otherRegionsMap.entrySet()) {
      Region parentReg = e.getKey();
      Map<ModuleDescriptor, Region> moduleRegs = e.getValue();
      
      if (moduleRegs != null) {
        for (Entry<ModuleDescriptor, Region> me : moduleRegs.entrySet()) {
          ModuleDescriptor moduleCfg = me.getKey();
          Region mreg = me.getValue();
          
          // add mreg to data source
          if (!serialisedConfig)  // use config-wise setting
            serialised = false;
          else  
            serialised = !moduleCfg.isMemoryBased();
          
          addRegionChild(dodm, parentReg, mreg, serialised);
        }
      }
    }
  }

  @Override
  protected ApplicationModule createModuleConfig(ModuleDescriptor moduleCfg,
      Configuration config, ModelConfig modelCfg, RegionGui regionGui,
      RegionToolMenuItem menuItemRegion, ControllerConfig ctlCfg,
      PropertySet printConfig, ApplicationModule[] childModules,
      Tree contTreeObj, boolean serialised) throws DataSourceException {
    DODMBasic dodm = getDodm();
    
    // properties set
    PropertySet modulePset = PropertySetFactory.createPropertySet(dodm, serialised, moduleCfg.props());
    
    ApplicationModule module = ApplicationModule.createInstance(moduleCfg, config, modelCfg, regionGui, menuItemRegion, 
        ctlCfg, printConfig, childModules, contTreeObj, 
        modulePset // v5.2
        );
      
    return module;
  }
  
  
}
