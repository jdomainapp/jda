package jda.modules.common.types.properties;

import java.util.ArrayList;
import java.util.Collection;

// v2.7.4
  public enum PropertyName {
    docTemplate("domainapp.exportdoc.docTemplate"), 
    Debug("debug"), // v4.0: changed Debug -> debug
    Logging("logging"), // v4.0: changed Logging -> logging
    /////// SetUp
    /** whether or not to store application configuration into a data store. If false
     * then application configuration is created entirely in memory. */
    setup_SerialiseConfiguration("domainapp.setup.SerialiseConfiguration"),
    /** whether or not to run post-setup of each module. If false the post-setup is not run
     * for any application modules when the set-up routine is run for that application. 
     * 
     * <p>This property is particularly useful to disable post setup when application is run in 
     * memory-only mode (i.e. when {@link #setup_SerialiseConfiguration} = false)   
     * */
    setup_PostSetUpOn("domainapp.setup.postSetUpOn"),
    /** the FQN of the set up class that is used to start the application. This is normally the same 
     * class that is used to set up the application.
     *  */
    setup_class("domainapp.setup.class"),
    /**
     * the FQN of the application's class that contains the application configuration needed to set up 
     * an application
     * @version 3.3
     */
    setup_systemClass("domainapp.setup.systemClass"), 
      // DODM, etc.
    ///// Specific set up properties (that can be set using System properties)
    /**must match osmType*/
    setup_dataSource_type("domainapp.setup.dataSource.type"),
    setup_dataSource_userName("domainapp.setup.dataSource.userName"), 
    setup_dataSource_password("domainapp.setup.dataSource.password"), 
    setup_dodm_type("domainapp.setup.dataSource.dodmType"),
    setup_dodm_clientUrl("domainapp.setup.dataSource.clientUrl"), 
    setup_dodm_dsmType("domainapp.setup.dataSource.dsmType"), 
    setup_dodm_domType("domainapp.setup.dataSource.domType"), 
    setup_dodm_osmType("domainapp.setup.dataSource.osmType"), 
    setup_config_appName("domainapp.setup.config.appName"), 
    setup_config_language("domainapp.setup.config.language"), 
    setup_config_configType("domainapp.setup.config.configType"), 
    ////// CONTROLLER
    /***/
    controller_ObjectScrollUpdate("domainapp.controller.ObjectScrollUpdate"), 
    /**a controller command used by TaskController to customise the operation that performs the main task*/
    controller_command("domainapp.controller.cmd"),
    /**a controller command (which must be a sub-type of ControllerCommand) used by Controller to perform a start-up operation (an example is an operation 
     * that loads pre-defined domain objects from the data source)
     * @version 3.2
     * */
    controller_startup_command("domainapp.controller.cmd.StartUp"),
    /**default data controller command*/
    controller_dataControllerCommand("domainapp.controller.dataControllerCmd")
    /**
     * The PREFIX for all data controller property names.
     * <p><b>Note:</b> NOT TO BE USED as individual property names
     * @version 3.1
     */
    ,prefix_controller_dataController("domainapp.controller.dataController")

    /** 
     * whether or not to check action performed of data controller 
     * 
     * <p><b>IMPORTANT</b>: set this property to <tt>false</tt> <b>ONLY</b> if the data controller <b>always</b> perform its actions
     * Modules that fit this category include help, print, etc.
     * 
     * <p>If not set then this property is assumed to be <tt>true</tt>.
     *  
     * @version 3.2c     
     */
    ,controller_dataController_isCheckActionPerformed(
        //"domainapp.controller.dataController.isCheckActionPerformed"
        prefix_controller_dataController.getSysPropName()+".isCheckActionPerformed"
        )
    
    /** 
     * limit the LAName actions performable by a data controller. 
     * <p>If the restricted actions are <b>always</b> performed, then 
     * need to set property {@link #controller_dataController_isCheckActionPerformed} to <tt>false</tt>
     *  
     * @version 3.2c     
     */
    ,controller_dataController_actions(
        //"domainapp.controller.dataController.actions"
        prefix_controller_dataController.getSysPropName()+".actions"
        )
    
    /** A DataControllerCommand that is used for performing operation New (which is   
     * defined in LAName} 
     * @version 3.2     
     */
    ,controller_dataController_new(prefix_controller_dataController.getSysPropName()+".New")
    /** A DataControllerCommand that is used for performing operation Update (which is  
     * defined in LAName} 
     * @version 3.2     
     */
    ,controller_dataController_update(prefix_controller_dataController.getSysPropName()+".Update")
    /** A DataControllerCommand that is used for performing operation OnUpdateObject (which is  
     * defined in LAName} 
     * @version 3.3     
     */
    ,controller_dataController_onUpdateObject(prefix_controller_dataController.getSysPropName()+".OnUpdateObject")
    /** A DataControllerCommand that is used for performing Create (which is 
     * defined in LAName} 
     */
    , controller_dataController_create(prefix_controller_dataController.getSysPropName()+".Create")
    /** A DataControllerCommand that is used for performing operation OnCreateObject (which is  
     * defined in LAName} 
     * @version 3.3     
     */
    ,controller_dataController_onCreateObject(prefix_controller_dataController.getSysPropName()+".OnCreateObject")
    /** A DataControllerCommand that is used for performing Open (which is 
     * defined in LAName} */
    , controller_dataController_open(prefix_controller_dataController.getSysPropName()+".Open")
    /** A DataControllerCommand that is used for performing OpenOnNew (which is 
     * defined in LAName} */
    , controller_dataController_openOnNew(prefix_controller_dataController.getSysPropName()+".OpenOnNew")
    /** A DataControllerCommand that is used for performing copyObject (which is 
     * defined in LAName} */
    , controller_dataController_copyObject(prefix_controller_dataController.getSysPropName()+".CopyObject")
    /** A DataControllerCommand that is used for handling copyObject */
    ,controller_dataController_handleCopyObject(prefix_controller_dataController.getSysPropName()+".HandleCopyObject")
    /** A DataControllerCommand that is used for performing Delete (which is 
     * defined in LAName} */
    ,controller_dataController_delete(prefix_controller_dataController.getSysPropName()+".Delete")
    /** A DataControllerCommand that is used for performing operation OnDeleteObject (which is  
     * defined in LAName} 
     * @version 3.3     
     */
    ,controller_dataController_onDeleteObject(prefix_controller_dataController.getSysPropName()+".OnDeleteObject")
    /** A DataControllerCommand that is used for performing action OnSetCurrentObject (which is 
     * defined in LAName} 
     * */
    /** A DataControllerCommand that is used for performing HelpButton (which is 
     * defined in LAName} 
     * @version 3.2c
     * */
    , controller_dataController_help(prefix_controller_dataController.getSysPropName()+".HelpButton")
    /** A DataControllerCommand that is used for performing onSetCurrentObject (which is 
     * defined in LAName} 
     * */
    ,controller_dataController_onSetCurrentObject(prefix_controller_dataController.getSysPropName()+".OnSetCurrentObject")
    /**
     * A DataControllerCommand which is also a StateChangeListener that
     * handles the application state change event raised by the data controller.
     * If {@link #controller_dataController_appStateListenerSource} is specified
     * then it also uses this to check the source of certain events (e.g.
     * Update).
     */
    ,controller_dataController_appStateEventHandler(prefix_controller_dataController.getSysPropName()+".AppStateEventHandler")
    /**
     * listens to application state change event whose source is the data field of the attribute specified by the property's name
     */
    ,controller_dataController_appStateListenerSource(prefix_controller_dataController.getSysPropName()+".appStateListenerSource")
    /**custom input helper command to handle mouse-click actions on the referenced objects of a module and of the descendant modules (if any)
     * @version 3.2
     */
    ,controller_dataController_helperMouseClickOnReferencedObject(prefix_controller_dataController.getSysPropName()+".HelperMouseClickOnReferencedObject")
    //,controller_forked("domainapp.controller.forked"),
    /**
     * custom handler to handle value changed event fired by the data fields of a data controller.
     * @version 3.2
     */
    ,controller_dataController_dataFieldValueChangedHandler(prefix_controller_dataController.getSysPropName()+".DataFieldValueChangedHandler")
    ////// MODEL
    ,model_sort_objects("domainapp.model.sortObjects"), 
    model_sort_attribute("domainapp.model.sortAttribute"),
    ////// VIEW
    view_toolBar_buttonIconDisplay("domainapp.view.toolbar.buttonIconDisplay"),
    view_toolBar_buttonTextDisplay("domainapp.view.toolbar.buttonTextDisplay"), 
    view_searchToolBar_buttonIconDisplay("domainapp.view.searchToolbar.buttonIconDisplay"), 
    view_searchToolBar_buttonTextDisplay("domainapp.view.searchToolbar.buttonTextDisplay"),
    
    view_objectForm("domainapp.view.objectForm"),
    view_objectForm_actions_buttonIconDisplay(view_objectForm.getSysPropName()+".actions.buttonIconDisplay"), 
    view_objectForm_actions_buttonTextDisplay(view_objectForm.getSysPropName()+".actions.buttonTextDisplay"),
    /**whether or not to automatically activate this object form (i.e. making it visible) */
    view_objectForm_autoActivate(view_objectForm.getSysPropName()+".autoActivate"), 
    /**auto-export: to automatically display the exported report of this object form to user when it is opened*/
    view_objectForm_autoExport(view_objectForm.getSysPropName()+".autoExport"),
//    /**
//     * Applies ONLY to child object forms.
//     * 
//     * <br>
//     * Whether or not to automatically open this object form (i.e. executing its Open command) when 
//     * a new object of the parent form is created
//     * 
//     * @version 3.2 */
    // not yet used: view_objectForm_autoOpenOnCreateNewParent(view_objectForm.getSysPropName()+".autoOpenOnCreateNewParent"), 
    /**
     * the name of the data field that links to another object form of the same view that will 
     * be the target of the changes that occur to this data field 
     */
    view_objectForm_targetForm(view_objectForm.getSysPropName()+".targetForm"), 
    /**whether or not to include a data field in an object form*/
    view_objectForm_dataField_visible(view_objectForm.getSysPropName()+".dataField.visible"),
    /**
     * This property applies ONLY to bounded data field, whose data source contains objects whose data are sensitive to changes at the data source. 
     * <p>Specifies whether or not the associated data source should be reloaded every time the object form of the data
     * field is refreshed. Sets this to <tt>true</tt> if the bounded objects are sensitive to changes at the source.
     *  
     * @version 3.1
     */
    view_objectForm_dataField_reloadBoundedDataOnRefresh(view_objectForm.getSysPropName()+".dataField.reloadBoundedDataOnRefresh"),
    /**
     * This property specifies the group id of the data components that belong to the same display group.
     * @version 3.1
     */
    view_objectForm_dataField_groupId(view_objectForm.getSysPropName()+".dataField.groupId"),
    /**
     * This property specifies whether or not a data field, when its value has been changed, can automatically cause an update of the value of the corresponding 
     *  domain attribute of the domain object that it is rendering
     * @version 3.1
     */
    view_objectForm_dataField_autoUpdate(view_objectForm.getSysPropName()+".dataField.autoUpdate"),
    /**
     * This property specifies the target data field of the same form that will receive an update event 
     * when value of this data field is changed. 
     * 
     * @version 3.1
     */
    view_objectForm_dataField_target(view_objectForm.getSysPropName()+".dataField.target"),
    /**
     * Specifies the group-by condition (typically the name of a domain attribute) that is used to 
     * group the objects that are displayed on the object form.
     * 
     * <p>It is <b>ONLY applicable to tabular object form</b> (e.g. JObjectTable or Html table). 
     * 
     * @version 3.3
     */
    view_objectForm_groupBy(view_objectForm.getSysPropName()+".groupBy"),
    /**whether or not to support international languages other than English in view interface (eg. Vietnamese)*/
    view_lang_international("domainapp.view.international")
    /**whether or not to create this view on module's start-up (default is not to do so)
     * @version 3.2
     */
    ,view_createOnStartUp("domainapp.view.createOnStartUp")
    /////////////////////  KEY BOARD SHOT-CUTS
    /**
     * The PREFIX for all keyboard shotcut property names.
     * <p><b>Note:</b> NOT TO BE USED as individual property names
     * @version 3.1
     */
    ,view_shotcuts("domainapp.view.shotcuts"),
    view_shotcuts_desktop_Logout(view_shotcuts.getSysPropName()+".desktop.ModuleLogout"), 
    view_shotcuts_desktop_Exit(view_shotcuts.getSysPropName()+".desktop.Exit"), 
    /**
     * the keyboard shot-cut key for the tool bar button <tt>Open</tt>
     * @version 3.1
     */
    view_shotcuts_tool_Open(view_shotcuts.getSysPropName()+".toolbar.Open"), 
    view_shotcuts_tool_New(view_shotcuts.getSysPropName()+".toolbar.New"), 
    view_shotcuts_tool_Delete(view_shotcuts.getSysPropName()+".toolbar.Delete"), 
    view_shotcuts_tool_Update(view_shotcuts.getSysPropName()+".toolbar.Update"), 
    view_shotcuts_tool_CopyObject(view_shotcuts.getSysPropName()+".toolbar.CopyObject"), 
    view_shotcuts_tool_First(view_shotcuts.getSysPropName()+".toolbar.First"), 
    view_shotcuts_tool_Previous(view_shotcuts.getSysPropName()+".toolbar.Previous"), 
    view_shotcuts_tool_Next(view_shotcuts.getSysPropName()+".toolbar.Next"), 
    view_shotcuts_tool_Last(view_shotcuts.getSysPropName()+".toolbar.Last"), 
    view_shotcuts_tool_Refresh(view_shotcuts.getSysPropName()+".toolbar.Refresh"), 
    view_shotcuts_tool_Reload(view_shotcuts.getSysPropName()+".toolbar.Reload"), 
    view_shotcuts_tool_Export(view_shotcuts.getSysPropName()+".toolbar.Export"), 
    view_shotcuts_tool_Print(view_shotcuts.getSysPropName()+".toolbar.Print"), 
    view_shotcuts_tool_Chart(view_shotcuts.getSysPropName()+".toolbar.Chart"), 
    view_shotcuts_tool_HelpButton(view_shotcuts.getSysPropName()+".toolbar.HelpButton")
    
//    /**
//     * The view region name (see RegionName) provided by a client module that is used for displaying information that
//     * it obtains from the service modules that it uses.  
//     * 
//     * @version 5.2
//     */
//    view_client_serviceRegion("view.client.serviceRegion"),
    /**
     * The additional view region (see RegionName#SidePane) that a view must contain 
     * for displaying information in a side-panel.  
     * 
     * @version 5.2
     */
    ,view_region_sidePane("view.region.sidePane")
    
    /**
     * The view.builder prefix for properties concerning this view part. 
     * 
     * @version 5.2
     */
    ,prefix_view_builder("view.builder"),

    /**
     * A view-builder class for building the side panel. 
     *  
     * @version 5.2
     */
    view_builder_sidePane(prefix_view_builder.getSysPropName()+".sidePane"),
    /**
     * the keyboard shot-cut key for the form action button <tt>Create</tt>
     * @version 3.1
     */
    view_shotcuts_action_Create(view_shotcuts.getSysPropName()+".action.Create"), 
    view_shotcuts_action_Reset(view_shotcuts.getSysPropName()+".action.Reset"), 
    view_shotcuts_action_Cancel(view_shotcuts.getSysPropName()+".action.Cancel"),
    /**mapped to property of the same name of <tt>PrintDesc</tt> */
    pageFormat("domainapp.view.print.pageOrientation."), 
    /**mapped to property of the same name of <tt>PrintDesc</tt> */
    paperSize("domainapp.view.print.mediaSize"),
    /**the name of the help file of an application module
     * @version 3.2 
     */
    module_help_fileName("domainapp.module.help.fileName"),
    /**
     * the module containment scope value (usually a comma-separated string of attribute names) 
     * 
     * @version 5.1
     */
    module_containment_scope("domainapp.module.containment.scope"), 
    /**
     * the module auto-start (automatically start a module when the software has been executed and before it is used by the user) 
     * 
     * @version 5.2
     */
    module_autoStart("domainapp.module.autoStart"),
    /**
     * one of the other module types (specified in ModuleType} that is not explicitly set in the MCC.
     * 
     * @version 5.2
     */
    module_type("domainapp.module.type"),
    /**
     * a generic tag
     * @version 5.6
     */
    tag("domainapp.tag")
    ;

    // the formal property name that is used as the system property name
    // (i.e. specified as VM argument, using -D option)
    private String formalName;
    
    private PropertyName(String formalName) {
      this.formalName = formalName;
    }
    
    /**
     * @effects 
     *  return the formal property name that is used as the system property name 
     *  (i.e. specified as VM argument, using -D option)  
     */
    public String getSysPropName() {
      return formalName;
    }
    
    /**
     * @effects 
     *  if exists PropertName whose system property name is <tt>sysName</tt>
     *    return it
     *  else
     *    return null
     */
    public static PropertyName lookUpBySysPropName(String sysName) {
      for (PropertyName propName : values()) {
        if (propName.getSysPropName().equals(sysName)) {
          return propName;
        }
      }
      
      return null;
    }

    /**
     * @effects 
     *  if exists PropertNames whose system property names start with <tt>prefix</tt>
     *    return them
     *  else
     *    return null
     */
    public static Collection<PropertyName> lookUpBySysPropNamePrefix(
        PropertyName prefix) {
      String prefixName = prefix.getSysPropName();
      int prefixNameLen = prefixName.length();
      
      Collection<PropertyName> names = new ArrayList();
      String propSysName;
      for (PropertyName propName : values()) {
        propSysName = propName.getSysPropName();
        if (propSysName.length() > prefixNameLen && propSysName.startsWith(prefixName)) {
          names.add(propName);
        }
      }
      
      return names.isEmpty() ? null : names;
    }

    /**
     * @effects 
     *  return the last name element of this.formalName
     * @example <pre>
     *  this = "domainapp.controller.dataController.Open"
     *  ->
     *  this.getLastName() = "Open"
     *  </pre>
     */
    public String getLastName() {
      return formalName.substring(formalName.lastIndexOf(".")+1);
    }

    /**
     * @effects 
     *  Normalise propLastName and look up in this for {@link PropertyName} whose name 
     *  is constructed from <tt>prefix.propLastName</tt>.
     *  
     *  <p>If found then return it else return null
     * @version 5.2
     */
    public static PropertyName lookUpByName(PropertyName prefix,
        String propLastName) {
      String first = propLastName.charAt(0)+"";
      propLastName = first.toLowerCase() + propLastName.substring(1);
      
      String propName = prefix.getSysPropName() + "." + propLastName;

      for (PropertyName prop : values()) {
        if (prop.getSysPropName().equals(propName)) {
          return prop;
        }
      }
      
      return null;
    }
  } // end property name