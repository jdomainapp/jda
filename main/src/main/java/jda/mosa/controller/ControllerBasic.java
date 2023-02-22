package jda.mosa.controller;

import static jda.modules.mccl.conceptmodel.controller.LAName.Add;
import static jda.modules.mccl.conceptmodel.controller.LAName.Cancel;
import static jda.modules.mccl.conceptmodel.controller.LAName.Chart;
import static jda.modules.mccl.conceptmodel.controller.LAName.ClearSearch;
import static jda.modules.mccl.conceptmodel.controller.LAName.CloseSearch;
import static jda.modules.mccl.conceptmodel.controller.LAName.CopyObject;
import static jda.modules.mccl.conceptmodel.controller.LAName.Create;
import static jda.modules.mccl.conceptmodel.controller.LAName.Delete;
import static jda.modules.mccl.conceptmodel.controller.LAName.Export;
import static jda.modules.mccl.conceptmodel.controller.LAName.First;
import static jda.modules.mccl.conceptmodel.controller.LAName.HelpButton;
import static jda.modules.mccl.conceptmodel.controller.LAName.Last;
import static jda.modules.mccl.conceptmodel.controller.LAName.New;
import static jda.modules.mccl.conceptmodel.controller.LAName.Next;
import static jda.modules.mccl.conceptmodel.controller.LAName.Open;
import static jda.modules.mccl.conceptmodel.controller.LAName.Previous;
import static jda.modules.mccl.conceptmodel.controller.LAName.Print;
import static jda.modules.mccl.conceptmodel.controller.LAName.Refresh;
import static jda.modules.mccl.conceptmodel.controller.LAName.Reload;
import static jda.modules.mccl.conceptmodel.controller.LAName.Reset;
import static jda.modules.mccl.conceptmodel.controller.LAName.Search;
import static jda.modules.mccl.conceptmodel.controller.LAName.Update;
import static jda.modules.mccl.conceptmodel.controller.LAName.ViewCompact;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.Format;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jda.modules.common.Toolkit;
import jda.modules.common.collection.ProtectedMap;
import jda.modules.common.concurrency.Task;
import jda.modules.common.concurrency.Task.TaskName;
import jda.modules.common.concurrency.TaskManager;
import jda.modules.common.concurrency.TaskManager.RunnableQueue;
import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.InfoCode;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotImplementedException.Code;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.QueryException;
import jda.modules.common.exceptions.SecurityException;
import jda.modules.common.exceptions.signal.ObsoleteStateSignal;
import jda.modules.common.exceptions.warning.DomainWarning;
import jda.modules.common.expression.Op;
import jda.modules.common.filter.Filter;
import jda.modules.common.types.Tuple2;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.common.types.tree.Tree;
import jda.modules.dcsl.syntax.Associate;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.ds.viewable.JAdaptiveDataSource;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.ds.viewable.JDataSourceFactory;
import jda.modules.ds.viewable.JSimpleDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.model.ModelConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.containment.ScopeDef;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.RegionGui;
import jda.modules.mccl.conceptmodel.view.RegionLinking;
import jda.modules.mccl.conceptmodel.view.RegionMap;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.mccl.conceptmodel.view.RegionToolMenuItem;
import jda.modules.mccl.conceptmodel.view.RegionType;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.syntax.controller.ControllerDesc.OpenPolicy;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.modules.security.authentication.controller.SecurityController;
import jda.modules.security.def.LogicalAction;
import jda.modules.security.def.Security;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.command.ControllerCommand;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.mosa.controller.assets.composite.MethodEvent;
import jda.mosa.controller.assets.composite.MethodListener;
import jda.mosa.controller.assets.datacontroller.DataPanelController;
import jda.mosa.controller.assets.datacontroller.ObjectTableController;
import jda.mosa.controller.assets.datacontroller.SimpleDataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;
import jda.mosa.controller.assets.eventhandler.InputHelper;
import jda.mosa.controller.assets.eventhandler.WindowHelper;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.controller.assets.helper.DefaultDataValidator;
import jda.mosa.controller.assets.helper.SecurityStateListener;
import jda.mosa.controller.assets.helper.indexer.IndexConsumer;
import jda.mosa.controller.assets.helper.indexer.IndexManager;
import jda.mosa.controller.assets.helper.indexer.Indexable;
import jda.mosa.controller.assets.helper.objectbrowser.IdPooledObjectBrowser;
import jda.mosa.controller.assets.helper.objectbrowser.ObjectBrowser;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.ControllerLookUpPolicy;
import jda.mosa.controller.assets.util.MessageCode;
import jda.mosa.controller.assets.util.MethodName;
import jda.mosa.model.Oid;
import jda.mosa.module.Context;
import jda.mosa.module.Module;
import jda.mosa.module.ModuleService;
import jda.mosa.view.View;
import jda.mosa.view.assets.DataContainerToolkit;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.dialog.JMessageDialog;
import jda.mosa.view.assets.panels.DefaultPanel;
import jda.mosa.view.assets.swing.JHtmlLabel;
import jda.mosa.view.assets.tables.JDataTable;
import jda.util.ObjectComparator;
import jda.util.ObjectComparator.SortBy;
import jda.util.ObjectMapSorter;
import jda.util.SwTk;
import jda.util.SysConstants;
import jda.util.events.InputHandler;
import jda.util.events.StateChangeListener;
import jda.util.properties.Property;

//import domainapp.util.Map;

/**
 * Represents a controller class for a given domain class <code>C</code>
 * 
 * @author dmle (Duc Minh Le <le.m.duc@gmail.com>)
 * 
 * @param <C>
 *          a domain class (e.g. Student)
 *          
 * @version 
 * - 4.0: implements ModuleService, Module, Context
 */
public class ControllerBasic<C> implements ModuleService, Module, Context {

  /** (Optional) a {@see Module} to identify this controller */
  private ApplicationModule module;

  /**
   * the domain class for the operations on the objects of whom this controller
   * is responsible for coordinating
   */
  private Class<C> cls;

  /**
   * the default top-level data controller used for manipulating the domain
   * objects. It is the data controller that has access to the entire object
   * pool containing the domain objects that are managed by this controller.
   * 
   * <p>
   * This controller is also the key of the first entry that is added to
   * {@link #dataControllerMap} if this controller is associated to a view
   * (gui).
   */
  private ControllerBasic.DataController rootDctl;

  /**
   * The GUI object that is controlled by this.
   */
  protected View gui;

  /**
   * maps a {@see JDataContainer} to the data controller that manages the domain
   * objects displayed by that GUI.
   * 
   * <p>
   * All the data controller objects in this map operate on the same object pool
   * of the domain class of this. However, each controller maintains its own
   * access pointer to the pool (via their <code>currentObj</code> attribute)
   * and, hence, can (quite) independently update and iterate over the objects
   * in the pool without affecting one another.
   **/
  private Map<JDataContainer, DataController> dataControllerMap;

  /** The main controller instance */
  private static ControllerBasic mainCtl;

  /** the parent controller of this (used only for child controller) 
   * @version 3.0
   * */
  private ControllerBasic parent;

  /**
   * <b>Global application properties</b><br>
   * This object encapsulates application-wide properties. Unlike
   * {@link #appProperties} which only captures controller properties, this
   * attribute captures properties of the application as a whole. For example,
   * it specifies the start-up controller and the default user that is used to
   * login to the system.<br>
   * 
   * This attribute is initialised once when the <tt>main</tt> controller is
   * initialised, and is available for all the child controllers. The
   * <tt>Configuration</tt> object is created by the application launcher and is
   * used as an input argument to create the <tt>main</tt> controller.
   */
  private static Configuration appConfig;

  /**
   * An application-wide {@see WindowHelper} object to handle window-related
   * events
   */
  private static WindowHelper wHelper;

  /**
   * an {@see InputHelper} object that is responsible for handling user input
   * actions on all parts of the GUI
   */
  private static InputHelper ihelper;

  /**
   * The index manager responsible for managing the indices of certain domain objects.
   * 
   * <br>Only one instance of this is used for each application.
   * 
   * @version 2.7.4
   */
  private IndexManager indexManager;
  
  /** the domain schema object */
  private static DODMBasic dodm;

  // static fields
  /** a map between the domain class and its controller */
  private static Map<ApplicationModule, ControllerBasic> funcControllerMap;

  /**
   * maps domain class to the data source that provides bounded components the
   * access to its object.
   * <p>
   * Note: This maps is managed by <b>main controller</b> to <b>only contain those data sources whose domain classes
   * have not got an associated Controller object</b>. For those that do, the
   * associated data source is kept in the
   * {@link ControllerBasic.DataController#dataSource} attribute.
   * */
  private static Map<Class, JDataSource> dataSourceMap;

  /**
   * a security cache that caches the permission states of the application
   * resources by names
   */
  /*
   * v2.7.2: support Object-typed key private static Map<String, Boolean>
   * securityCache;
   */
  private static Map<Object, Boolean> securityCache;

  /**
   * Listeners of changes to the application state
   */
  private static java.util.Map<AppState, List<StateChangeListener>> stateChangeListeners;

  /**
   * v2.7.2: listens to security-related events (fired by methods of
   * {@link SecurityController} and the like)
   */
  private static SecurityStateListener secStateListener;

  /**
   * Defines attribute (name, label) mappings for all queries created on the domain objects managed by 
   * all data controllers of this.
   * 
   *  <p>A mapping is of the form <tt>(s1,s2)</tt> 
   *  where <tt>s1</tt> is a domain attribute name and <tt>s2</tt> is a 
   *  data field label of the attribute named <tt>s1</tt> 
   * 
   * <p> The data field label is language-aware in that it is changed based on the language
   * configuration of the application. 
   * 
   * <p><b>Example</b>: if this is <tt>View(Student)</tt> then this 
   * would contain these mappings (in Vietnamese):
   * <pre>
   *  (id, "Mã sinh viên")
   *  (name, "Họ và tên")
   *  (dob, "Ngày sinh")
   *  ...
   * </pre>
   * 
   * @version 3.1
   */
  private Map<String,String> attribNameLabelMap;
  
  /**
   * Caches mappings between each domain class and its language-aware domain class label.
   * Since this label is frequently used by {@link DataController}'s operations but 
   * is rarely changed, it helps improve the performance to cache them.
   * 
   * <p>The labels are only changed when the application language is updated. In which case
   * the labels are updated via method {@link #updateDomainClassLabelCache(Collection)}.
   *  
   * @version 3.1
   */
  private static Map<Class,String> domainClassLabelCache;
  
  // constants
  /** debug for each parameterised <tt>Controller</tt> class */
  protected static final boolean debug = Toolkit
      .getDebug(ControllerBasic.class);

  protected static final boolean loggingOn = Toolkit
      .getLoggingOn(ControllerBasic.class);

  /**
   * Number of millisecs to wait in each cycle for {@link View} to be ready
   * @version 3.1
   */
  private static final long VIEW_READY_WAIT_CYCLE = 100;

  /***
   * whether or not the application support different languages.
   * 
   * @deprecated as of version 2.7.3
   */
  private static Boolean isSupportLanguage;

  /***
   * whether or not the application support chart.
   */
  private static Boolean isSupportChart;

// v3.2: removed
//  /**
//   * for performance reason: this holds the result of the check as to whether or
//   * not this is a controller for report panel<br>
//   * 
//   * @see #isReport()
//   * @deprecated as of version 2.7.3
//   */
//  private Boolean isReport;

  private Boolean isSerialisable;

  private Boolean isEditable;

  private Boolean isSingleton;
  
  // v3.0: whether or not the data container of this will be automatically exported and displayed to user 
  private Boolean isAutoExport;
  
  /**
   * <b>Controller-specific</b> properties: These are properties that control
   * various run-time aspects of how the methods of <b>this controller
   * object</b> are executed. This is different from {@link #appProperties},
   * which affect all controller objects.
   * 
   * <br>
   * An example property is whether or not to display a pop-up message. <br>
   * Using code should use {@link #setProperty} and {@link #getProperty} method
   * to set and get the properties that they desired.
   * 
   * <p>
   * Temporary properties (those that only affect one invocation of a method)
   * should be set using the invocation sequence
   * <tt>getProperty-setProperty-setProperty</tt>. The second is used to set a
   * new property value while the first and last are used to change the property
   * value back to the old one.
   * 
   * <p>
   * The property name consists of string items separated by dots. For example,
   * <tt>show.message.popup</tt> is the name of a property concerning the
   * display of pop-up messages during a method execution. The value of this
   * property is <tt>Boolean.TRUE</tt> or <tt>Boolean.FALSE</tt>.
   **/
  private jda.modules.common.collection.Map<String, Object> properties;

  /**
   * whether or not this has finished all initialisation tasks (incl. creating
   * GUI) <br>
   * For functional modules: initialised = true when its GUI has been
   * initialised and completed populated with data fields <br>
   * For main module: initialised = true when all functional modules have been
   * initialised and other resources (e.g. menu bar) have been initialised
   * */
  private boolean initialised;

  /**
   * A shared stack that is used internally to process region graphs.
   * 
   * @version 5.1
   */
  private static Stack<Region> processedRegionBuffer;

  
  /**
   * The shared error icon used to display error messages 
   * 
   * @version 3.1
   */
  private static ImageIcon errorIcon;
  
  /**
   * (Performance)<br>
   * Caches the information dialogs that are displayed so that they are not re-created each time for each gui.
   * @version 3.2c
   */
  private static DialogCache infoDialogCache;

  public static Region Desktop;
  public static Region MenuBar;
  public static Region ToolBar;
  public static Region StatusBar;
  // static Region File;
  public static Region Tools;
  // static Region Options;
  public static Region Actions;
  public static Region LoginActions;
  public static Region SearchToolBar;
  public static Region Components;
  public static Region SidePane; // v5.2

  /**
   * @effects initialises <code>this</code> with a parent controller
   *          <code>parent</code>. If <code>autoCreateGUI = true</code> then
   *          also creates the <code>AppGUI</code> object.
   * 
   */
  public ControllerBasic(DODMBasic dodm, ApplicationModule module,
      Region guiConfig, ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    this.module = module;
    if (module != null) {
      this.cls = module.getDomainClassCls();
    }

    properties = new jda.modules.common.collection.Map();

    initialised = false; // v2.7.4

    View parentGUI = null;
    if (parent != null) { // child controller
      // places this into the map
      if (module != null)
        funcControllerMap.put(module, this);

      // this.parent = parent;

      dataControllerMap = new LinkedHashMap<JDataContainer, DataController>();

      parentGUI = parent.getGUI();

      // v2.7.3: add init for functional module
      initModule();
      
      this.parent = parent;
    } else {
      // top-level controller
      this.dodm = dodm;
      initMainModule(config, guiConfig);
    }
    // v2.6.1: create a top-level data controller for every controller,
    // regardless of
    // whether or not it has a GUI
    /* v3.0: add check for null data controller
    if (module != null && module.getDataControllerCls() != null
    */
    if (module != null && module.hasController()) {
      Class dataCtlCls = module.getDataControllerCls();
      if (dataCtlCls == null) dataCtlCls = SysConstants.DEFAULT_PANEL_DATA_CONTROLLER;
      rootDctl = createRootDataController(
          dataCtlCls, this);
    }

    // initialise the GUI object
    if (guiConfig != null // view config is on
        && guiConfig.getDisplayClass() != null // display class is specified
    ) {
      gui = initGUI(this, guiConfig, parentGUI);
    }

    // register the domain class
    // v2.7.3: IMPORTANT this must be performed after adding this to
    // funcControllerMap (above)
    // b/c some system modules (e.g. ImportData) requires this.
    if (cls != null) {
      /*
       * v2.7.3: register class hierarchy dodm.registerClass(cls);
       */
      /* v2.7.3: register class hierarchy using a separate method */
      // dodm.registerClassHierarchy(cls);
      registerDomainClass(cls);
    }

    // if this is a state listener then register it to listen to application
    // state events
    if (module != null) {
      StateChangeListener listener;
      if (module.getIsStateListener()) {
        listener = (StateChangeListener) this;
        addApplicationStateChangedListener(listener, listener.getStates());
      }
    }
  }

  /**
   * @requires 
   *  appConfig != null /\ user is logged in
   * @effects 
   *  if user configuration settings are defined
   *    load them
   *  else
   *    do nothing
   *  @version 2.8: 
   *    stub-methods only (for sub-types to implement)
   */
  protected void loadUserConfiguration() {
    // stub method 
  }
  
  /**
   * @requires dodm != null /\ cls != null
   * @effects register this.cls in dodm
   * @version 2.7.3
   */
  protected void registerDomainClass(Class<C> cls) throws NotPossibleException {
    dodm.registerClassHierarchy(cls);
  }

  /**
   * @effects 
   *  if this.gui has been created (i.e. its GUI components have been created)
   *    return true
   *  else
   *    return false
   *  @version 2.7.4
   */
  private boolean isGuiCreated() {
    if (gui != null) {
      return gui.isCreated();
    } else {
      return false;
    }
  }
  
  /**
   * @effects 
   *  if this.gui has not been created
   *    create it
   *  else 
   *    do nothing
   * @version 2.7.4
   */
  public void createGUIIfNotAlready() throws NotFoundException, NotPossibleException {
    if (hasGUI() && !isGuiCreated()) {
      createGUI();
      postCreateGUI();
    }
  }

  /**
   * @requires 
   *  {@link #hasGUI()} = true
   *  
   * @effects initialises <code>this.gui</code> as an <code>AppGUI</code> for
   *          <code>this.domainClass</code>
   */
  public void createGUI() throws NotFoundException, NotPossibleException {
    gui.createGUI();

    /*
     * v2.7.2: moved to postCreateGUI // post processing gui.postCreateGUI();
     */
  }

  /**
   * @effects finalise <tt>this.gui</tt>
   */
  public void postCreateGUI() {
    // post processing
    gui.postCreateGUI();

    // v2.7.4: if security is enabled then update data field permissions
    if (!gui.isTopLevel() && 
        mainCtl.isSecurityEnabled() 
        // v2.8: added this check to correctly preserve the original semantics
        // of this method, which is invoked by ApplicationLauncher.postLogin() -- i.e. after login.
        // It helps avoid the error of prematurally setting read-only permissions  
        // on the Login form's fields.
        && mainCtl.isLoggedIn()     
        ) {
      updateDataPermissions();
    }
    
    initialised = true; // v2.7.4
  }

  /**
   * This method must be invoked by the main controller in order to initialise
   * the application security.
   * 
   * @requires this is the main controller
   * @effects 
   *  <pre>
   *    if this is the main controller 
   *      if there is a default user in appConfig 
   *        login with the default user 
   *          if fails try logging in as guest 
   *      else logging in as guest 
   *    else 
   *      do nothing
   *      
   *    throws NotFoundException if required security resources are not found
   *  </pre>
   */
  public void initSecurity() throws NotFoundException {
    // initialise security
    if (isSecurityEnabled()) {
      if (gui != null && gui.isTopLevel()) {
        SecurityController secCtl = (SecurityController) lookUp(Security.class);
        if (secCtl != null) {
          String defUser = appConfig.getUserName();
          String pwd = appConfig.getPassword();
          if (defUser != null) {
            try {
              secCtl.init(defUser, pwd);
            } catch (SecurityException e) {
              // something wrong, could not login the default user
              e.printStackTrace();
              // display login dialog
              secCtl.init();
            }
          } else {
            // display login dialog
            secCtl.init();
          }
        }
      }
    }
  }

  /**
   * @requires this is the main controller /\ security is enabled /\ user has
   *           logged in
   * @effects perform tasks after a successful login
   * 
   * @version 
   *  - 2.8: load user configuration settings<br>
   *  - 3.3: start up the modules (using their start-up commands, if any)
   */
  public void postLogin() {
    // v2.8: added support for user configuration settings
    loadUserConfiguration();
    
    // v3.3: run start-up commands of each module (if specified)
    startUpModules();
    
    updateMenuBarPermissions();
    updateDataPermissionsOfCreatedForms();
  }

  /**
   * @requires this is the main controller /\ all 
   *  functional controllers of this have been set up.
   * 
   * @effects <pre>
   *  for each module in this
   *    if controller's start-up command is specified
   *      run it
   *      </pre>
   *      
   * @version 
   * - 3.3 <br>
   * - 5.2: support modules configured with property autoStart=true  
   */
  public void startUpModules() {
    Iterator<ControllerBasic> childControllers = getFunctionalControllers();

    if (childControllers != null) {
      ControllerBasic c;
      while (childControllers.hasNext()) {
        c = childControllers.next();
        
        // if c has start-up command
        c.doStartUpCommand();
        
        // if c is an auto-start module
        ApplicationModule cmodule = c.getApplicationModule();
        if (cmodule.isAutoStart()) {
          try {
            c.runWithDefaultCommand();
          } catch (Exception e) {
            // ignore error
            displayErrorFromCode(MessageCode.ERROR_RUN_MODULE, e, new Object[] {cmodule.getName()});
          }
        }
      }
    }
  }

  /**
   * @requires this is the main controller /\ security is enabled /\ user has
   *           NOT yet completed logging out
   * 
   * @effects perform tasks before loggout out
   */
  public void preLogout() {
    clearAllDataSourceBindings();
  }

  /**
   * @requires this is the main controller /\ security is enabled /\ user has
   *           logged out
   * 
   * @effects perform tasks after a log out
   */
  public void postLogout() {
    updateMenuBarPermissions();
  }

  /**
   * @requires this is the main controller /\ {@link #postCreateGUI()} has been
   *           performed /\ {@link #funcControllerMap} is not empty
   * 
   * @effects perform tasks after the functional modules have been loaded
   * @version 
   * - 2.7.3<br>
   * - 5.2: add support for configuring service modules for each client module
   */
  public void postCreateFunctionalModules() {
    if (!gui.isTopLevel())
      return;

    // hide the menu items whose functional modules are not found in
    // funcControllerMap
    Collection<ApplicationModule> functionalModules = funcControllerMap.keySet();

    gui.setVisibleModuleMenuItems(functionalModules);

    initialised = true; // v2.7.4
  }

  /**
   * This method works for functional controller the same way as
   * {@link #initMainModule(Configuration, Region)} does for the main
   * controller.
   * 
   * <p>
   * Overriding methods of a sub-type <b>must</b> first invoke
   * <tt>super.initModule</tt>.
   * 
   * @requires this is a functional controller
   * @effects initialise the state of this
   * 
   * @version 
   * - 2.7.3: created <br>
   * - 3.2: added support for start-up command
   */
  protected void initModule() throws ApplicationRuntimeException {
    if (this == mainCtl)
      return;

    /* v3.3: use a shared method AND add a check condition to run this method only if security is not enabled
     * (when security is enabled, this method is be invoked by postLogin instead)
    */
    if (!isSecurityEnabled())
      doStartUpCommand();
    
    // for sub-types to implement (must invoke super.initFunctionalModule first)
  }

  /**
   * @requires this is a functional controller
   * 
   * @effects 
   *  if exists start-up-command
   *    run it
   *  else 
   *    do nothing
   * @version 3.3
   */
  private void doStartUpCommand() {
    if (this == mainCtl)
      return;
    
    ControllerCommand startUp = lookUpCommand(PropertyName.controller_startup_command);
    
    if (startUp != null) {
      startUp.doTask();
    }
  }

  /**
   * @requires this is a top-level controller
   * @effects initialises application-wide properties.
   * @version v2.6.4b: added moduleGUI parameter and only initialise GUI-related
   *          tasks if moduleGui is specified
   */
  // private void initCommon(Configuration config) {
  private void initMainModule(Configuration config, Region moduleGui) {
    /*
     * v2.6.4b: only initialise GUI-related tasks if moduleGui is specified
     * GUIToolkit.initInstance(config);
     * 
     * initRegions();
     */

    GUIToolkit.initInstance(config);

    if (moduleGui != null) {
      initRegions();
    }

    funcControllerMap = new LinkedHashMap();
    mainCtl = this;

    appConfig = config;

    // initialise state rules
    // initStates();

    ihelper = InputHelper.getInstance(this);
    wHelper = WindowHelper.getInstance(this);

    if (isSecurityEnabled())
      securityCache = new LinkedHashMap();

    // state change listeners
    stateChangeListeners = new LinkedHashMap();

    // v2.7.2
    secStateListener = new SecurityStateListener(this);
    addApplicationStateChangedListener(secStateListener,
        secStateListener.getStates());

    // v2.7.2
    dataSourceMap = new HashMap<Class, JDataSource>();

    // register input helper as state change listener of this controller
    addApplicationStateChangedListener(ihelper, ihelper.getStates());
    
    // v2.7.4: index manager
    indexManager = IndexManager.getInstance();
    addApplicationStateChangedListener(indexManager, indexManager.getStates());
  }

  /**
   * @effects load some pre-configured <tt>Region</tt>s from the database.
   *          Throws NotFoundException if could not find a region.
   * @requires this is a top-level controller
   * 
   * @version 
   * - 5.2: support side panel
   */
  private void initRegions() throws NotFoundException {
    // init the regions once for all objects of this class
    if (Desktop == null) {
      Desktop = lookUpRegion(RegionName.Desktop.name());
      MenuBar = lookUpRegion(RegionName.MenuBar.name());
      ToolBar = lookUpRegion(RegionName.ToolBar.name());
      StatusBar = lookUpRegion(RegionName.StatusBar.name());
      // File = lookUpRegion(RegionName.File.name());
      Tools = lookUpRegion(RegionName.Tools.name());
      // Options = lookUpRegion(RegionName.Options.name());
      Actions = lookUpRegion(RegionName.Actions.name());
      LoginActions = lookUpRegion(RegionName.LoginActions.name());
      SearchToolBar = lookUpRegion(RegionName.SearchToolBar.name());
      Components = lookUpRegion(RegionName.Components.name());
      
      // v5.2
      SidePane = lookUpRegion(RegionName.SidePane.name());
    }

    /*
     * v2.7 if (Desktop == null) { Desktop =
     * loadRegion(RegionName.Desktop.name()); MenuBar =
     * loadRegion(RegionName.MenuBar.name()); ToolBar =
     * loadRegion(RegionName.ToolBar.name()); StatusBar =
     * loadRegion(RegionName.StatusBar.name()); // File =
     * loadRegion(RegionName.File.name()); Tools =
     * loadRegion(RegionName.Tools.name()); // Options =
     * loadRegion(RegionName.Options.name()); Actions =
     * loadRegion(RegionName.Actions.name()); LoginActions =
     * loadRegion(RegionName.LoginActions.name()); SearchToolBar =
     * loadRegion(RegionName.SearchToolBar.name()); Components =
     * loadRegion(RegionName.Components.name()); }
     */
  }

  /**
   * @effects returns a new functional <code>AppGUI</code> object for the
   *          controller <code>ctl</code> whose parent GUI is
   *          <code>parent</code> and whose GUI config is <tt>moduleGui</tt>
   */
  protected View initGUI(ControllerBasic ctl, Region guiConfig,
      View parent) throws NotPossibleException {
    Class guiClass = null;
    String guiClassName = guiConfig.getDisplayClass();
    try {
      guiClass = Class.forName(guiClassName);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND,
          "Không tìm thấy lớp giao diện cho {0}", guiClassName);
    }

    try {
      Constructor<View> c = guiClass.getConstructor(
          ControllerBasic.class, Region.class, View.class);
      return c.newInstance(ctl, guiConfig, parent);
    } catch (Exception e) {
      e.printStackTrace();
      throw new NotPossibleException(
          NotPossibleException.Code.CLASS_NOT_WELL_FORMED, e,
          "Không thể tạo giao diện");
    }
  }
  
  /**
   * @effects performs any last-minute configuration of the GUI of this
   */
  public void preRunConfigureGUI() {
    gui.preRunConfigure();
  }

  /**
   * @effects performs any pre-run configuration for the data objects managed by
   *          this
   * @version 2.7.2
   */
  public void preRunConfigure() throws DataSourceException {
    if (rootDctl != null) {
      if (rootDctl.getOpenPolicy().isWithAutomatic()// contains(OpenPolicy.A)
      ) {
        // auto-openning
        rootDctl.open();
      }
    }
  }

  /**
   * @requires module != null
   * @effects create and return a new Controller object from the specified
   *          arguments
   * 
   * @deprecated as of v2.7
   */
  public static <T extends ControllerBasic> T createController(
      DODMBasic schema, ApplicationModule module, Region moduleGui,
      ControllerBasic parent, Configuration config//
  ) throws NotPossibleException {
    Class<T> controllerClass = null;
    try {
      controllerClass = module.getControllerCls();
      Constructor<T> c = controllerClass.getConstructor(DODMBasic.class,
          ApplicationModule.class, Region.class, ControllerBasic.class,
          Configuration.class);

      return c.newInstance(schema, module, moduleGui, parent, config);
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_CREATE_CONTROLLER, e,
          "Không thể tạo trình điểu khiển {0}", controllerClass);
    }
  }

  /**
   * @requires module != null
   * @effects create and return a new Controller object from the specified
   *          arguments
   */
  public static <T extends ControllerBasic> T createController(
      DODMBasic schema, ApplicationModule module, ControllerBasic parent,
      Configuration config//
  ) throws NotPossibleException {
    Class<T> controllerClass = null;
    try {
      controllerClass = module.getControllerCls();
      Constructor<T> c = controllerClass.getConstructor(DODMBasic.class,
          ApplicationModule.class, Region.class, ControllerBasic.class,
          Configuration.class);

      RegionGui moduleGui = module.getViewCfg();

      return c.newInstance(schema, module, moduleGui, parent, config);
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_CREATE_CONTROLLER, e,
          "Không thể tạo trình điểu khiển {0}", controllerClass);
    }
  }

  // Not USED
//  /**
//   * @effects 
//   *  create a <b>data-only</b> ControllerBasic of type <tt>controllerClass</tt> with the 
//   *  specified arguments.
//   *  
//   *  <p>A <i>data-only</i> controller is a controller object that is created only to serve
//   *  its data-related operations (e.g. as requested by a <tt>DataController</tt> of its kind), 
//   * 
//   *  <p>throws NotPossibleException if failed
//   */
//  private static <T extends ControllerBasic> T createDataOnlyController(
//      Class<T> controllerClass,
//      ControllerBasic parent, 
//      ApplicationModule module,
//      DODMBasic dodm,
//      Configuration config)  throws NotPossibleException {
//    try {
//      Constructor<T> c = controllerClass.getConstructor(DODMBasic.class,
//          ApplicationModule.class, Region.class, ControllerBasic.class,
//          Configuration.class);
//
//      //ApplicationModule module = null;
//      RegionGui moduleGui = null; //module.getViewCfg();
//      //ControllerBasic parent = null;
//      
//      return c.newInstance(dodm, module, moduleGui, parent, config);
//    } catch (Exception e) {
//      throw new NotPossibleException(
//          NotPossibleException.Code.FAIL_TO_CREATE_CONTROLLER, e,
//          "Không thể tạo trình điểu khiển {0}", controllerClass);
//    }
//  }
  
  
  /**
   * @requires 
   *  containerCfg != null /\ parent != null /\ ...
   *  
   * @effects 
   *  <pre>
   *  if controllerConfig.forked = true
   *    call {@link #createForkedDataController(DODMBasic, Configuration, Class, Class, ControllerBasic, DataController)}
   *  else
   *    call creator.{@link #createDataController(Class, ControllerBasic, DataController)}
   *  </pre>
   * 
   * <p>throws NotPossibleException if failed.
   * @version 3.0
   * @param defaultTableDataController 
   */
  public static DataController createChildDataController(
      DODMBasic dodm,
      Configuration appConfig,
      RegionLinking containerCfg, 
      Class<? extends DataController> defaultDataControllerType, 
      ControllerBasic creator,
      ControllerBasic user, 
      DataController parent
      ) throws NotPossibleException {
    ControllerConfig controllerConfig = containerCfg.getControllerCfg();
    Class<? extends DataController> dctlCls = controllerConfig.getDataControllerCls();
    if (dctlCls == null) {
      // use default
      dctlCls = defaultDataControllerType;
    }

    DataController dctl;

    /* not used
    */
    dctl = creator.createDataController(dctlCls, user, parent, containerCfg);
    //}
    
    return dctl;
  }

  /**
   * @effects create and return a <b>top-level</b> <tt>DataController</tt> instance of
   *          <tt>type</tt>, using <tt>this,user</tt> as arguments.
   *          Throws NotPossibleException if fails to do so.
   * @version 3.0
   *  - make private
   */
  private DataController createRootDataController(Class type, ControllerBasic user) throws NotPossibleException {
    return createDataController(type, user, null, null);
  }
  
  /**
   * @requires 
   *  parent != null <-> containerCfg != null
   *  
   * @effects create and return <tt>DataController</tt> instance of
   *          <tt>type</tt>, using <tt>this,user,parent</tt> as arguments.
   *          Throws NotPossibleException if fails to do so.
   * @version 3.0
   *  - make private
   */
  private DataController createDataController(Class type, ControllerBasic user,
      DataController parent, RegionLinking containerCfg) throws NotPossibleException {
    try {
      Constructor<DataController> cons = type.getConstructor(
          ControllerBasic.class, ControllerBasic.class, DataController.class);
      DataController dctl = cons.newInstance(this, user, parent);
      
      // v3.0
      if (parent == null)
        dctl.initRootCfg();
      else
        dctl.initChildCfg(containerCfg);
      
      return dctl;
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,
          new Object[] {type.getName(), user + "," + parent});
    }
  }
  
  // Not used
//  /**
//   * <b>This method MUST ONLY be used for special configurations.</b>
//   * Normal configurations must 
//   * instead use {@link #createDataController(Class, ControllerBasic, DataController)}.
//   * 
//   * <p>This method creates a DataController that is forked from an independent Controller instance.
//   * That is it is not added to the same data controller pool of the primary Controller object.
//   * This means that it also does not receive the benefits of being updated with state changes.
//   * 
//   * @effects 
//   * create a new (forked) instance <tt>creator</tt> of <tt>creatorType</tt>
//   * use <tt>creator</tt> to create and return a <tt>DataController</tt> instance of
//   * <tt>type</tt>, using <tt>creator,user,parent</tt> as arguments.
//   *          Throws NotPossibleException if fails to do so.
//   * @version 3.0
//   */
//  private static <C extends ControllerBasic, D extends DataController> D createForkedChildDataController(
//      DODMBasic dodm,
//      Configuration config,
//      ControllerBasic parentCtl, 
//      ApplicationModule creatorModule,
//      Class<C> creatorType,
//      Class<D> type, 
//      ControllerBasic user,
//      DataController parent,
//      RegionLinking containerCfg
//      ) throws NotPossibleException {
//    C creator = ControllerBasic.createDataOnlyController(creatorType, parentCtl, 
//        creatorModule, 
//        dodm, config);
//
//    try {
//      Constructor<D> cons = type.getConstructor(
//          ControllerBasic.class, ControllerBasic.class, DataController.class);
//      D dctl = cons.newInstance(creator, user, parent);
//      
//      // make the data controller the root of its creator
//      creator.setRootDataController(dctl);
//      
//      // initialise config 
//      if (parent == null)
//        dctl.initRootCfg();
//      else
//        dctl.initChildCfg(containerCfg);
//      
//      return dctl;
//    } catch (Exception e) {
//      throw new NotPossibleException(
//          NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e,
//          new Object[] {type.getName(), user + "," + parent});
//    }
//  }
  
  /**
   * @effects places the pair
   * 
   *          <pre>
   * <dcont,dctl></code> into the data controller map of
   * <code>this</code>
   */
  public void putDataController(JDataContainer dcont, DataController dctl) {
    dataControllerMap.put(dcont, dctl);
  }

  /**
   * Return the top-level data controller of this. It is also the data
   * controller that has access to the entire object pool containing the domain
   * objects that are managed by this controller.
   * 
   * @effects return this.topDctl
   */
  public DataController<C> getRootDataController() {
    // it is the first controller added to the map
    // v2.6.1: use rootDctl
    /*
     * if (!dataControllerMap.isEmpty()) return
     * dataControllerMap.values().iterator().next(); else return null;
     */
    return rootDctl;
  }

  /**
   * <b>IMPORTANT</b>: this method MUST NOT be <tt>public</tt>
   * 
   * @effects
   *  set this.rootDctl = dctl  
   * @version 3.0
   */
  void setRootDataController(DataController dctl) {
    this.rootDctl = dctl;
  }

  /**
   * @effects 
   *  if this.rootDctl is not null
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public boolean hasRootDataController() {
    return rootDctl != null;
  }

  /**
   * @effects 
   *  if <tt>dctl == this.rootDctl</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   *    
   * @version 3.1
   */
  public boolean isRootDataController(DataController dctl) {
    return dctl == rootDctl;
  }

  /**
   * @requires cls is a domain class registered in the system /\ there exists a
   *           data controller for it
   * 
   * @effects if exists top data controller for domain class <tt>cls</tt> return
   *          it else return <tt>null</tt>
   */
  private DataController getDataController(Class cls) {
    ControllerBasic ctl = lookUp(cls);
    ControllerBasic.DataController dctl;
    if (ctl != null) {
      dctl = ctl.getRootDataController();

      if (dctl != null) {
        return dctl;
      }

    }

    return null;
  }

  /**
   * @effects <pre>
   *  if exists view g of ApplicationModule(cls)
   *    return g.rootContainer
   *  else if exists m = ApplicationModule(c), t = JDataContainer(cls) s.t. DomainClass(c) /\ m.view uses t
   *    return t
   *  else
   *    return null
   * </pre>
   */
  public JDataContainer getDataContainerWithPreference(Class cls) {
    ControllerBasic ctl = lookUpPrimary(cls);
    if (ctl != null) {
      View g = ctl.getGUI();
      if (g != null) {
        // v2.7.4: create GUI on demand
        ctl.createGUIIfNotAlready();
        
        return g.getRootContainer();
      } else {
        Iterator<DataController> dctls = ctl.getDataControllers();
        /*v2.7.4: create GUI on demand */
        if (dctls != null) {
          DataController dctl = dctls.next();
          ControllerBasic userCtl = dctl.getUser();
          userCtl.createGUIIfNotAlready();
          
          return dctl.getDataContainer();
        }
      }
    }

    // not found
    return null;
  }

  /**
   * @effects return the data controller object in this that is used by
   *          controller <tt>user</tt>, or <tt>null</tt> if no such controller
   *          exists.
   */
  public ControllerBasic.DataController getDatacontroller(ControllerBasic user) {
    if (dataControllerMap.isEmpty())
      return null;

    Iterator<DataController> it = dataControllerMap.values().iterator();
    DataController dctl;
    ControllerBasic usedBy;
    while (it.hasNext()) {
      dctl = it.next();
      usedBy = dctl.getUser();
      if (user == usedBy)
        return dctl;
    }

    // not found
    return null;
  }

  /**
   * @effects if there are <tt>DataController</tt>s of this return an
   *          <tt>Iterator</tt> of them else return null
   */
  public Iterator<DataController> getDataControllers() {
    if (!dataControllerMap.isEmpty()) {
      return dataControllerMap.values().iterator();
    } else {
      return null;
    }
  }

  /**
   * @effects if there are functional controllers registered in this return an
   *          Iterator object of them else return null
   * @deprecated same as {@link #getFunctionalControllers()}
   */
  public Iterator<ControllerBasic> getControllers() {
    if (funcControllerMap != null) {
      return funcControllerMap.values().iterator();
    } else {
      return null;
    }
  }

  /**
   * @effects if l already registered to <tt>state</tt> in this do nothing else
   *          register l to listen to <tt>state</tt>
   */
  @Override // ModuleService
  public void setMethodListener(AppState state, MethodListener l) {
    DataController dctl = getRootDataController();
    if (dctl != null) {
      dctl.setMethodListener(state, l);
    }
  }

  /**
   * This method is used by {@link CompositeController} to wait for the data
   * controller of this to finish executing a pre-defined task. The other
   * related method is {@link #removeMethodListener(AppState, MethodListener)}.
   * 
   * @effects registers l to listen to the state change event that causes the
   *          state <tt>state</tt> in the data controller of this.
   */
  @Override
  public void addMethodListener(AppState state, MethodListener l) {
    DataController dctl = getRootDataController();
    if (dctl != null) {
      dctl.addMethodListener(state, l);
    }
  }

  /**
   * @effects unregister <tt>l</tt> from listening to the state change event
   *          that causes the state <tt>state</tt> in the data controller of
   *          this.
   */
  @Override
  public void removeMethodListener(AppState state, MethodListener l) {
    DataController dctl = getRootDataController();
    if (dctl != null) {
      dctl.removeMethodListener(state, l);
    }
  }

  /**
   * @effects if states != null register <tt>listener</tt> to listen to state
   *          changes to the specified states else register <tt>listener</tt> to
   *          listen to changes to any state
   */
  public void addApplicationStateChangedListener(StateChangeListener listener,
      AppState[] states) {
    if (states != null) {
      for (AppState state : states) {
        List<StateChangeListener> listeners = stateChangeListeners.get(state);

        if (listeners == null) {
          listeners = new ArrayList();
          stateChangeListeners.put(state, listeners);
        }

        listeners.add(listener);
      }
    } else {
      List<StateChangeListener> listeners = stateChangeListeners
          .get(AppState.AnyState);

      if (listeners == null) {
        listeners = new ArrayList();
        stateChangeListeners.put(AppState.AnyState, listeners);
      }

      listeners.add(listener);
    }
  }

  /**
   * @effect inform state change listeners of this about changes to
   *         <tt>state</tt> that are described in <tt>message</tt>
   */
  public void fireApplicationStateChanged(Object src, AppState state,
      String message, Object... data) {
    List<StateChangeListener> listeners = stateChangeListeners.get(state);

    List<StateChangeListener> all = stateChangeListeners.get(AppState.AnyState);

    if (listeners != null) {
      for (StateChangeListener listener : listeners) {
        listener.stateChanged(src, state, message, data);
      }
    }

    if (all != null) {
      for (StateChangeListener listener : all) {
        listener.stateChanged(src, state, message, data);
      }
    }
  }

  /**
   * @effects returns the viewer-type (
   *          {@link ControllerLookUpPolicy#PrimaryOnly})
   *          <code>Controller</code> bound to the specified domain class, or
   *          <tt>null</tt> if no such controller exists
   */
  public static ControllerBasic lookUpPrimary(Class cls) {
    return lookUp(cls, ControllerLookUpPolicy.PrimaryOnly);
  }

  /**
   * @effects returns the viewer-type ({@link ControllerLookUpPolicy#ViewerOnly}
   *          ) <code>Controller</code> bound to the specified domain class, or
   *          <tt>null</tt> if no such controller exists
   */
  public static ControllerBasic lookUp(final Class domainClass) {
    List<ControllerBasic> ctls = lookUpExtended(domainClass,
        ControllerLookUpPolicy.ViewerOnly);
    if (ctls != null) {
      return ctls.get(0);
    } else {
      return null;
    }
  }

  /**
   * @effects return the <b>first</b> <tt>Controller</tt> bound to
   *          <tt>domainClass</tt> and matching the specified policy; or return
   *          <tt>null</tt> if no such controller exists.
   */
  public static ControllerBasic lookUp(final Class domainClass,
      ControllerLookUpPolicy policy) {
    List<ControllerBasic> ctls = lookUpExtended(domainClass, policy);
    if (ctls != null) {
      return ctls.get(0);
    } else {
      return null;
    }
  }

  // /**
  // * @effects
  // * return the <tt>Controller</tt> object that is the viewer of the specified
  // * domain class <tt>domainClass</tt>, or <tt>null</tt> if no such controller
  // * is found.
  // */
  // private static Controller lookUpViewer(final Class domainClass) {
  // Controller viewer = null;
  //
  // Module module;
  // Controller ctl;
  // for (Entry<Module, Controller> e : funcControllerMap.entrySet()) {
  // module = e.getKey();
  // if (module.getIsViewer()) {
  // ctl = e.getValue();
  // if (ctl.getDomainClass() == domainClass) {
  // // found it
  // viewer = ctl;
  // break;
  // }
  // }
  // }
  //
  // return viewer;
  // }

  /**
   * @effects if security is enabled if user has permission on the specified
   *          domain class return the controller responsible for this class or
   *          throws NotFoundException if the controller cannot be found else
   *          throws SecurityException else return the controller responsible
   *          for this class or throws NotFoundException if the controller
   *          cannot be found
   * 
   */
  public ControllerBasic lookUpViewerWithPermission(Class domainClass)
      throws SecurityException, NotFoundException {
    // check security permission
    boolean allowed = true;
    if (mainCtl.isSecurityEnabled()) {
      if (domainClass != Security.class) {
        allowed = getResourceState(null,
            dodm.getDsm().getResourceNameFor(domainClass));// schema.getDomainClassName(domainClass));
      }
    }

    if (allowed) {
      // return lookUpViewer(domainClass);
      List<ControllerBasic> found = lookUpExtended(domainClass,
          ControllerLookUpPolicy.ViewerOnly);
      if (found != null) {
        return found.get(0);
      } else {
        return null;
      }
    } else {
      throw new SecurityException(
          SecurityException.Code.INSUFFICIENT_PERMISSION, new Object[] {domainClass});
    }
  }

  /**
   * @effects if security is enabled if user has permission on the specified
   *          domain class return the controller responsible for this class or
   *          throws NotFoundException if the controller cannot be found else
   *          throws SecurityException else return the controller responsible
   *          for this class or throws NotFoundException if the controller
   *          cannot be found
   * 
   */
  ControllerBasic lookUpWithPermission(Class domainClass)
      throws SecurityException, NotFoundException {
    // check security permission
    boolean allowed = true;
    if (mainCtl.isSecurityEnabled()) {
      if (domainClass != Security.class) {
        allowed = getResourceState(null,
            dodm.getDsm().getResourceNameFor(domainClass));
        // schema.getDomainClassName(domainClass));
      }
    }

    if (allowed) {
      return lookUp(domainClass);
    } else {
      throw new SecurityException(
          SecurityException.Code.INSUFFICIENT_PERMISSION, new Object[] {domainClass});
    }
  }

  /**
   * @effects returns the controller responsible for the module named
   *          <code>moduleName</code>.
   * 
   *          <p>
   *          Throws SecurityException if user does not have permission to use
   *          the controller; NotFoundException if the controller is not found.
   */
  public static ControllerBasic lookUpByModuleWithPermission(
      final String moduleName) throws NotFoundException, SecurityException {
    boolean allowed;

    ApplicationModule module;
    ControllerBasic ctl;
    for (Entry<ApplicationModule, ControllerBasic> e : funcControllerMap
        .entrySet()) {
      module = e.getKey();
      if (module.getName().equals(moduleName)) {
        // check permission
        allowed = true;
        ctl = e.getValue();
        if (mainCtl.isSecurityEnabled()) {
          allowed = getResourceStateOfController(ctl);
        }

        if (allowed) {
          return ctl;
        } else {
          throw new SecurityException(
              SecurityException.Code.INSUFFICIENT_PERMISSION, new Object[] {moduleName});
        }
      }
    }

    // none found
    throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
        new Object[] {moduleName});
  }

  /**
   * @requires this is the main controller
   * @effects 
   *  if exists a module <tt>m</tt> whose tool menu item OR whose {@link RegionGui} has the configuration <tt>region</tt>
   *    return <tt>m</tt>
   *  else
   *    return <tt>null</tt>
   * @version 3.1
   */
  public ApplicationModule lookUpModuleByViewRelatedRegion(Region region) {
    // look up controller then use it to retrieve the module
    if (region instanceof RegionToolMenuItem) {
      return ((RegionToolMenuItem) region).getApplicationModule();
    } else if (region instanceof RegionGui) {
      return ((RegionGui) region).getApplicationModule();
    } else {
      // should not happen
      return null;
    }
  }
  
  /**
   * @requires this is the main controller
   * @effects if module <tt>m</tt> has been created in this return <tt>true</tt>
   *          else return <tt>false</tt>
   * @version 2.7.2
   */
  public boolean lookUpModule(ApplicationModule m) {
    if (funcControllerMap.containsKey(m)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @requires this is the main controller
   * @effects if exists module <tt>m</tt> which is the primary module of domain class is
   *          <tt>domainCls</tt> return <tt>m</tt> else return <tt>null</tt>
   * @version 2.7.3
   */
  public static ApplicationModule lookUpModule(Class domainCls) {
    ControllerLookUpPolicy policy = ControllerLookUpPolicy.PrimaryOnly;

    for (ApplicationModule module : funcControllerMap.keySet()) {

      // consider policy here
      if (policy == ControllerLookUpPolicy.ViewerOnly
          && module.getIsViewer() == false) {
        continue;
      } else if (policy == ControllerLookUpPolicy.PrimaryOnly
          && module.getIsPrimary() == false) {
        continue;
      }

      if (module.getDomainClassCls() == domainCls) {
        // found one
        return module;
      }
    }

    // not found
    return null;
  }

  /**
   * @effects returns the controller responsible for the specified module
   * 
   *          <p>
   *          Throws SecurityException if user does not have permission to use
   *          the controller; NotFoundException if the controller is not found.
   */
  public static ControllerBasic lookUpByModuleWithPermission(
      final ApplicationModule theModule) throws NotFoundException,
      SecurityException {
    boolean allowed;

    ApplicationModule module;
    ControllerBasic ctl;
    for (Entry<ApplicationModule, ControllerBasic> e : funcControllerMap
        .entrySet()) {
      module = e.getKey();
      if (module == theModule) {
        // check permission
        allowed = true;
        ctl = e.getValue();
        if (mainCtl.isSecurityEnabled()) {
          allowed = getResourceStateOfController(ctl);
        }

        if (allowed) {
          return ctl;
        } else {
          throw new SecurityException(
              SecurityException.Code.INSUFFICIENT_PERMISSION, new Object[] {module});
        }
      }
    }

    // none found
    throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
        "Không tìm thấy mô-đun điều khiển {0}", theModule);
  }

  /**
   * @effects return the <tt>Controller</tt>s responsible for the specified
   *          domain class (and (recursively) those responsible for the
   *          superclasses and sub-classes of this class if any) that match the specified
   *          <tt>ControllerLookUpPollicy</tt>; or return <tt>null</tt> if no
   *          such controllers exist.
   * 
   * @version 
   * - 2.6.1 add support for primary type controller<br>
   * - v5.2: improved to support looking up controllers of the super-class of <tt>domainClass</tt>
   * - <br>v5.4: added support for inclSubCls option
   */
  protected static List<ControllerBasic> lookUpExtended(
      final Class domainClass, final ControllerLookUpPolicy policy) {
    
    return lookUpExtended(domainClass, policy, true, true);
  }

  /**
   * Improved from {@link #lookUpExtended(Class, ControllerLookUpPolicy)} to support looking up controllers of the super-class of <tt>domainClass</tt>.
   * 
   * @effects return the <tt>Controller</tt>s responsible for the specified
   *          domain class (and (recursively) those responsible for the
   *          sub-classes of this class (if any), and, if <tt>inclSuperCls = true</tt> then also those of the super-class (if any), 
   *          that match the specified
   *          <tt>ControllerLookUpPollicy</tt>; or return <tt>null</tt> if no
   *          such controllers exist.
   * 
   * @version 
   * - v5.2: created<br>
   * - v5.4: added an option (inclSubCls) to configure whether or not to look up the subclasses
   */
  protected static List<ControllerBasic> lookUpExtended(
      final Class domainClass, final ControllerLookUpPolicy policy, 
      final boolean inclSuperCls, 
      final boolean inclSubCls) {
    List<ControllerBasic> ctls = new ArrayList<ControllerBasic>();

    ControllerBasic c = null;
    ApplicationModule module;
    ControllerBasic ctl;
    for (Entry<ApplicationModule, ControllerBasic> e : funcControllerMap
        .entrySet()) {
      module = e.getKey();
      ctl = e.getValue();

      // consider policy here
      if (policy == ControllerLookUpPolicy.ViewerOnly
          && module.getIsViewer() == false) {
        continue;
      } else if (policy == ControllerLookUpPolicy.PrimaryOnly
          && module.getIsPrimary() == false) {
        continue;
      }

      if (ctl.getDomainClass() == domainClass) {
        // found one
        c = ctl;
        ctls.add(c);
        // look up the controllers of the sub-classes (if any)
        DSMBasic dsm = dodm.getDsm();
        // v5.4
        if (inclSubCls) {
          Class[] subs = dsm.getSubClasses(domainClass);
          if (subs != null) {
            boolean _inclSuperCls = false;  // do not include super when looking at the sub-classes
            boolean _inclSubCls = true; // v5.4
            for (Class sub : subs) {
              List<ControllerBasic> subCtls = lookUpExtended(sub, policy, _inclSuperCls, _inclSubCls);
              if (subCtls != null) {
                ctls.addAll(subCtls);
              }
            }
          }
        }
        
        if (inclSuperCls) {
          // look up the controllers of the super-classes (if any)
          Class sup = dsm.getSuperClass(domainClass);
          if (sup != null) {
            boolean _inclSuperCls = true;  // v5.4
            boolean _inclSubCls = false; // v5.4
            List<ControllerBasic> supCtls = 
                lookUpExtended(sup, policy, _inclSuperCls, _inclSubCls);  // v5.4
            if (supCtls != null) {
              ctls.addAll(supCtls);
            }
          }
        }

        // consider policy here
        if (policy == ControllerLookUpPolicy.First
            || policy == ControllerLookUpPolicy.ViewerOnly
            || policy == ControllerLookUpPolicy.PrimaryOnly) {
          break;
        }
      }
    }

    if (!ctls.isEmpty()) {
      return ctls;
    } else {
      return null;
    }
  }
  
  /**
   * @effects returns the first child <code>Controller</code> in
   *          <code>this.funcControllerMap</code> whose associated GUI type is
   *          <code>type</code> or <code>null</code> if no such controller
   *          exists.
   * 
   *          <p>
   *          Note: if there are multiple controllers having the same type, only
   *          the first one is returned.
   */
  static ControllerBasic lookUpByGuiType(RegionType type) {
    if (funcControllerMap != null) {
      View gui;
      Region guiCfg;
      for (ControllerBasic c : funcControllerMap.values()) {
        gui = c.getGUI();
        if (gui != null) {
          guiCfg = gui.getGUIConfig();
          if (guiCfg.getType().equals(type)) {
            // returns the first found
            return c;
          }
        }
      }
      // return funcControllers.get(name);
    }
    return null;
  }

  /**
   * @effects returns the <code>Controller</code> object in <code>this</code>
   *          whose <code>Module</code> is referenced by the specified
   *          <code>region</code> object.
   */
  protected static ControllerBasic lookUpByRegion(Region region) {
    if (funcControllerMap != null) {
      ApplicationModule m;
      ControllerBasic c;
      for (Entry<ApplicationModule, ControllerBasic> e : funcControllerMap
          .entrySet()) {
        m = e.getKey();
        c = e.getValue();
        // if the module is linked to the associated region then return c
        if (region instanceof RegionToolMenuItem) {
          if (m.equals(((RegionToolMenuItem) region).getApplicationModule())) {
            return c;
          }
        } else if (region instanceof RegionGui) {
          if (m.equals(((RegionGui) region).getApplicationModule())) {
            return c;
          }
        }
      }
    }
    return null;
  }

  /**
   * Use this method <b>only</b> to search for controllers who are sub-classes
   * of <tt>Controller</tt> (their types are other than
   * <tt>Controller.class</tt>).
   * 
   * 
   * @effects return the controller responsible has the specified type or throws
   *          NotFoundException if the controller cannot be found
   */
  public static <T> T lookUpByControllerType(Class<T> ctlClass)
      throws NotFoundException {
    Class cls;
    for (ControllerBasic ctl : funcControllerMap.values()) {
      cls = ctl.getClass();
      if (cls == ctlClass) {
        return (T) ctl;
      }
    }

    // none found
    throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
        "Không tìm thấy mô-đun điều khiển của {0}", ctlClass);
  }

  /**
   * @effects 
   *  if exists {@link ControllerCommand} in {@link #getControllerConfig()} whose name is <tt>cmdName</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   *    
   * @version 
   * - 3.2: moved here from TaskController
   */
  protected ControllerCommand lookUpCommand(PropertyName cmdName) {
    ControllerConfig ctlCfg = getControllerConfig();
    
    Object cmdClsObj = ctlCfg.getControllerCommand(cmdName);
    
    ControllerCommand cmd = null;
    if (cmdClsObj != null && cmdClsObj instanceof Class) {
      Class<? extends ControllerCommand> cmdCls = (Class) cmdClsObj; 
      cmd = ControllerCommand.createInstance(cmdCls, this);
    }
    
    return cmd;
  }
  
  // /**
  // * Use this method <b>only</b> to search for controllers who are sub-classes
  // * of <tt>Controller</tt> (their types are other than
  // * <tt>Controller.class</tt>).
  // *
  // *
  // * @effects if security is enabled if user has permission on the specified
  // * controller class return the controller responsible has this type
  // * or throws NotFoundException if the controller cannot be found else
  // * throws SecurityException else return the controller has this type
  // * or throws NotFoundException if the controller cannot be found
  // *
  // */
  // Controller lookUpByControllerTypeWithPermission(Class ctlClass)
  // throws SecurityException, NotFoundException {
  // // check security permission
  // boolean allowed = true;
  // if (mainCtl.isSecurityEnabled()) {
  // if (ctlClass != SecurityController.class) {
  // allowed = getResourceState(null, schema.getDomainClassName(ctlClass));
  // }
  // }
  //
  // if (allowed) {
  // return lookUpByControllerType(ctlClass);
  // } else {
  // throw new SecurityException("Không đủ quyền: " + ctlClass);
  // }
  // }

  /**
   * @effects 
   *  if this is the main controller
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public boolean isMainController() {
    return this == mainCtl;
  }
  
  /**
   * @effects returns the <code>this.mainCtl</code> object
   */
  public ControllerBasic getMainController() {
    return mainCtl;
  }

  /**
   * @requires SearchToolBar is initialised
   * @effects return SearchToolBar region.
   */
  public static Region getSearchToolBar() {
    return SearchToolBar;
  }

  /**
   * @requires this is the main controller
   * 
   * @effects if there are functional controllers of the application return an
   *          <tt>Iterator</tt> of them else return null
   */
  public Iterator<ControllerBasic> getFunctionalControllers() {
    if (funcControllerMap != null) {
      return funcControllerMap.values().iterator();
    }

    return null;
  }

  /**
   * @effects 
   *  return {@link #funcControllerMap}
   *  
   * @version 5.2
   */
  protected Map<ApplicationModule, ControllerBasic> getFuncControllerMap() {
    return funcControllerMap;
  }
  
  // /**
  // * @effects returns the parent controller
  // */
  // public Controller getParent() {
  // return parent;
  // }

  // ////// GUI-related methods /////////////////////
  /**
   * @effects invoke <code>showGUI</code> of this controller and execute the
   *          specified command of the top-level data controller of this.
   */
  public void runWithCommand(LAName command) {
    preRun(); // v2.7.3
    showGUI();
    postRun(); // v2.7.3

    DataController topDctl = getRootDataController();
    if (topDctl != null) {
      topDctl.actionPerformed(command);
    }
  }

/**
   * @effects 
   *  perform tasks before {@link #run())
   *  
   *  <br><b>Note</b>: Sub-types must first invoke this method before doing their own things
   * @version 2.7.3
   */
  // must be public as it is invoked by reflection
  public void preRun() throws ApplicationRuntimeException {
    // v2.7.4: check startAfter time
    ControllerConfig ctlCfg = getControllerConfig();

    if (ctlCfg != null) {
      long startAfter = ctlCfg.getStartAfter();
      if (startAfter > 0) {
        SwTk.sleep(startAfter);
      }
    }
  }

  /**
   * @effects runs the default method <code>showGUI</code> of this controller
   */
  public void run() {
    preRun();
    showGUI();
    postRun();
  }

  /**
   * A variant of {@link #run()} which supports the default command.
   * 
   * @effects 
   *  if exists default-command in {@link #module}
   *    run this with default-command
   *  else
   *    invoke {@link #run()}
   *    
   * @version 5.2 
   *
   */
  public void runWithDefaultCommand() {
    LAName defCommand = module.getDefaultCommand();
    if (defCommand != null && defCommand != SysConstants.NullCommand) {
      runWithCommand(defCommand);
    } else {
      run();
    }
  }
  
  /**
   * @effects perform tasks after {@link #run()}
   * 
   * <br>
   *          <b>Note</b>: Sub-types must first invoke this method before doing
   *          their own things
   * 
   * @version 2.7.3
   */
  protected void postRun() throws ApplicationRuntimeException {
    // for sub-types to implement
  }

  /**
   * @requires this is the main controller
   * 
   * @effects run the controller associated to the module whose GUI region is
   *          <tt>guiCfg</tt>
   * 
   *          <p>
   *          Throws NotFoundException if module not found.
   */
  public void runModule(Region guiCfg) throws NotFoundException {
    /**
     * if this is the top-level controller and the command is the name of a
     * child controller show the GUI of that controller
     */
    if (funcControllerMap != null) {
      ApplicationModule m = null;
      ControllerBasic c = null;
      ControllerBasic found = null;
      for (Entry<ApplicationModule, ControllerBasic> entry : funcControllerMap
          .entrySet()) {
        m = entry.getKey();
        c = entry.getValue();
        // find the module linked to the associated region
        if (guiCfg instanceof RegionToolMenuItem) {
          if (m.equals(((RegionToolMenuItem) guiCfg).getApplicationModule())) {
            found = c;
            break;
          }
        } else if (guiCfg instanceof RegionGui) {
          if (m.equals(((RegionGui) guiCfg).getApplicationModule())) {
            found = c;
            break;
          }
        }
      }

      if (found != null) {
        LAName defCommand = m.getDefaultCommand();
        if (defCommand != null && defCommand != SysConstants.NullCommand) {
          found.runWithCommand(defCommand);
        } else {
          found.run();
        }
      } else {
        // should not happen
        throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, 
            new Object[] { guiCfg.getName()});
      }
    }
  }

  /**
   * @requires this is the main controller
   * 
   * @effects if a default child controller is specified run it else do nothing
   */
  public void runDefaultModule() {
    if (gui != null && gui.isTopLevel()) {
      String moduleName = appConfig.getDefaultModule();
      if (moduleName != null) {
        try {
          ControllerBasic defCtl = lookUpByModuleWithPermission(moduleName);
          
          defCtl.run();
        } catch (SecurityException e) {
          displayErrorFromCode(MessageCode.ERROR_INSUFFICIENT_PERMISSION_TO_RUN, moduleName);
        } catch (NotFoundException e) {
          displayErrorFromCode(MessageCode.ERROR_NO_MODULE_FOUND, e, moduleName);
        } catch (Exception e) {
          // ignore error
          displayErrorFromCode(MessageCode.ERROR_RUN_MODULE, e, moduleName);
        }
      }
    }
  }

  /**
   * @requires this is the main controller /\ module != null
   * 
   * @effects look up the controller of the specified module and if user has
   *          permission for it run it
   */
  public void runModule(ApplicationModule module) {
    if (gui != null && gui.isTopLevel()) {
      try {
        ControllerBasic defCtl = lookUpByModuleWithPermission(module);
        
        defCtl.run();
      } catch (SecurityException e) {
        displayErrorFromCode(MessageCode.ERROR_INSUFFICIENT_PERMISSION_TO_RUN, module);
      } catch (NotFoundException e) {
        displayErrorFromCode(MessageCode.ERROR_NO_MODULE_FOUND, e, module);
      } catch (Exception e) {
        // ignore error
        displayErrorFromCode(MessageCode.ERROR_RUN_MODULE, e, module);
      }
    }
  }

  /**
   * @effects 
   *  if <tt>this.gui != null</tt> AND is active and valid
   *    hide it
   *  else
   *    do nothing
   */
  public void stop() {
    if (gui != null) {
      // v3.2: added this to wait until gui is fully sized and positioned before made hidden
      while (!gui.isActive() || !gui.isValid()) {
        SwTk.sleep(200);
      }
      
      hideGUI(gui);
    }
  }

  /**
   * @effects if compact = true show a compact view of <tt>this.gui</tt> else
   *          show a normal view
   */
  public void showGUI(boolean compact) {
    // debug
    // System.out.printf("Controller.showGUI: compact=%b%n", compact);
    
    // v2.7.4: create GUI if first time
    createGUIIfNotAlready();

    if (gui != null) {
      if (!gui.isTopLevel()) {
        gui.preRunConfigure(true); // v2.7.2
      }

      if (compact) {
        gui.compact(true);
      } else {
        gui.compact(false);
      }

      showGUI();
    }
  }

  /**
   * This method is needed for composite controllers when it is important to wait for the 
   * GUI to completely visible before doing other tasks. 
   * 
   * @effects shows <code>this.gui</code> and wait for it to finish
   * @version 3.1
   * @see {@link MethodName#showGUIAndWait} 
   */
  public void showGUIAndWait() {
    showGUI();
    
    // wait for finish
    if (gui != null) {
      boolean finish;
      do {
        finish = gui.isDataEntryReady();
        
        if (!finish)
          SwTk.sleep(VIEW_READY_WAIT_CYCLE);
      } while (!finish);
    }
  }
  
  /**
   * @effects shows <code>this.gui</code>.
   * @see {@link MethodName#showGUI} 
   */
  public void showGUI() {
    // v2.7.4: create GUI if first time
    createGUIIfNotAlready();

    if (gui != null) {
      if (!gui.isTopLevel()) {
        gui.select();
      }

      // need to pack child frame if it is set to do so
      // this relates to a problem of the frame not being fully packed in the
      // desktop
      // before it is made visible
      /*
       * v2.7.4: support location update if (gui.isVisible() && !gui.isSized())
       * { gui.pack(); }
       */
      if (gui.isVisible()) {
        if (!gui.isSized())
          gui.pack();

        /*
         * v2.7.4: moved to outside this block (below) if (!gui.isLocated()) {
         * gui.updateLocation(); }
         */
      }

      if (!gui.isLocated()) {
        gui.updateLocation();
      }

      if (!gui.isVisible()) {
        gui.setVisible(true);
      }
    }
  }

  /**
   * @effects look up the <tt>AppGUI</tt> of the class <tt>domainClass</tt> and
   *          show it
   */
  public void showGUI(Class domainClass) {
    ControllerBasic ctl = lookUp(domainClass);

    if (ctl == null) {
      System.err.println("No controller exists for " + domainClass);
    }

    // v2.7.4: create GUI if first time
    ctl.createGUIIfNotAlready();

    ctl.getGUI().setVisible(true);
  }

  /**
   * @requires val != null
   * 
   * @effects
   *  if  <tt>this.gui != null AND this.rootDatacontroller</tt> is not null
   *    <br>make <tt>this.gui</tt> visible (if not already then in the compact mode)
   *    <br>display <tt>o</tt> on <tt>this.rootDatacontroller</tt>
   *    <br>display the associated objects of <tt>o</tt> in the child data containers if the open policy allows it
   *   
   * @version 
   * - 2.7.4 <br>
   * - 3.1: only compact GUI if it is currently not visible
   */
  public void showObject(C o) {
    if (!hasGUI())
      return;
    
    DataController rootDctl = getRootDataController();
    if (rootDctl != null) {
      /*v3.1: only compact view if it is not visible (in case user is viewing certain sub-containers)
      // show GUI in the compact mode
      showGUI(true);
      */
      if (!gui.isVisible()) {
        showGUI(true);
      } else {
        showGUI();
      }
        
      rootDctl.showObject(o);
    }
  }

  /**
   * @requires val != null
   * 
   * @effects
   *  if  <tt>this.gui != null AND this.rootDatacontroller</tt> is not null
   *    <br>make <tt>this.gui</tt> visible (if not already then in the best-fit mode)
   *    <br>display <tt>o</tt> on <tt>this.rootDatacontroller</tt>
   *    <br>display the associated objects of <tt>o</tt> in the child data containers if the open policy allows it
   *   
   * @version 
   * - 3.2: created
   */
  public void showObjectBestFit(C o) {
    if (!hasGUI())
      return;
    
    DataController rootDctl = getRootDataController();
    if (rootDctl != null) {
      if (!gui.isVisible()) {
        showGUI();
        // best fit
        gui.compactViewOnly(true);
      } else {
        showGUI();
      }
        
      rootDctl.showObject(o);
    }
  }

  /**
   * This method is a shortcut for {@link #hideGUI(View)} which is
   * invoked by <tt>CompositeController</tt>s
   * 
   * @effects if <tt>this.gui</tt> != null hide <tt>this.gui</tt> else do
   *          nothing
   */
  public void hideGUI() {
    if (gui != null)
      hideGUI(this.gui);
  }

  /**
   * @requires gui is a functional gui
   * 
   * @effects iconify gui and if gui is active then deactivate it; set gui.visible = false
   */
  void hideGUI(View gui) {
    // debug:
    // System.out.println("Hiding GUI: %s " + gui);
    
    if (!gui.isTopLevel()) {
      // System.out.println("Controller.hideGUI: " + gui);
      // gui.setIconified(true);
      gui.iconify();

      // update application state
      if (gui.isActive()) {
        // System.out.println("active: " + true);
        ihelper.deactivateGUI(gui);
      }
    }

    gui.setVisible(false);
  }

  /**
   * This acts similar to {@link #hideGUI(View)} except that it does not
   * iconify the associated internal frame of the gui.
   * 
   * @requires gui is a functional gui
   * 
   * @effects if gui is active deactivate it set gui.visible = false
   */
  public void hideFunctionalGUI(View gui) {
    if (!gui.isTopLevel()) {
      // update application state
      if (gui.isActive()) {
        // System.out.println("active: " + true);
        ihelper.deactivateGUI(gui);
      }
    }

    gui.setVisible(false);
  }

  @Override
  public View getView() {
    return getGUI();
  }
  
  /**
   * @effects return <code>this.gui</code>
   */
  public View getGUI() {
    return gui;
  }

  /**
   * @effects if gui != null return true else return false
   */
  public boolean hasGUI() {
    return gui != null;
  }

  /**
   * @requires this is the main controller
   * @effects returns the child functional <code>AppGUI</code> that contains the
   *          <code>frame</code> or <code>null</code> if no guis are found to
   *          contain it.
   */
  public View getGUI(JInternalFrame frame) {
    for (ControllerBasic c : funcControllerMap.values()) {
      View gui = c.getGUI();
      if (gui != null && gui.getGUI() == frame) {
        return gui;
      }
    }

    return null;
  }

  /**
   * @requires this is the main controller
   * @effects return the AppGUI that is active or null if no AppGUIs are active
   */
  public View getActiveGUI() {
    View activeGUI = null;
    if (funcControllerMap != null) {
      for (ControllerBasic c : funcControllerMap.values()) {
        View gui = c.getGUI();
        if (gui != null && gui.isActive()) {
          activeGUI = gui;
          break;
        }
      }
    }

    // debug
//    if (activeGUI == null)
//      System.out.printf("ControllerBasic.getActiveGUI: activeGUI: %s%n", activeGUI);
    
    return activeGUI;
  }

  /**
   * @requires 
   *  this is the main controller
   * @effects return the data container that currently has focus or null if no
   *          container has a focus
   * @version 
   *  - 3.0: fixed to use {@link #getActiveGUI()}
   */
  public JDataContainer getActiveDataContainer() {
    // v3.0: return ihelper.getCurrentContainer();
    View activeGUI = getActiveGUI();
    
    if (activeGUI != null) {
      // find the currently active data container in this
      JDataContainer dcont = activeGUI.getRootContainer();
      
      return getActiveDataContainer(dcont);
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if <tt>dcont</tt> or one of its descendant data container has a focus
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  private JDataContainer getActiveDataContainer(JDataContainer dcont) {
    if (dcont.hasFocus()) {
      return dcont;
    } else {
      // find the child containers
      Iterator<JDataContainer> children = dcont.getChildContainerIterator();
      
      if (children != null) {
        JDataContainer child;
        while (children.hasNext()) {
          child = children.next();
          return getActiveDataContainer(child);
        }
      }
      
      return null;
    }
  }


  public String getName() {
    return (module != null) ? module.getName() : this.getClass()
        .getSimpleName();
  }
  
  /**
   * @version 3.0
   * @effects 
   *  return this.parent
   */
  public ControllerBasic getParent() {
    return parent;
  }
  
  public jda.modules.common.collection.Map<String, Object> getProperties() {
    return properties;
  }

  public void setProperties(
      jda.modules.common.collection.Map<String, Object> properties) {
    this.properties = properties;
  }

  /**
   * @effects return <tt>this.appConfig</tt>
   */
  public Configuration getConfig() {
    return appConfig;
  }

  /**
   * @effects return the name of the module that is associated to this.
   */
  public String getModuleName() {
    return module.getName();
  }

  /**
   * @effects return the <tt>title</tt> of the module that is associated to this
   * @version 3.1
   */
  public String getModuleTitle() {
    String title = module.getLabelAsString();
    
    return title;
  }
  
  /**
   * @effects <pre>
   * if the property named name exists
   *            returns its value
   *          else
   *            return null
   * </pre>
   */
  public Object getProperty(String name) {
    return properties.get(name);
  }

  /**
   * @effects puts the entry <tt><name,value></tt> into <tt>this.properties</tt>
   *          ; return the value previously associated to <tt>name</tt>
   */
  public Object setProperty(String name, Object value) {
    return properties.put(name, value);
  }
  
  /**
   * @effects clears a specific region of the GUI
   */
  public void clearGUI(Region region
  // String regionName
  ) {
    if (gui != null) {
      gui.clear(region);
      // gui.clear(regionName);
    }
  }

  /**
   * @requires gui != null
   * @effects reset the AppGUI of this to its initial state
   */
  void resetGUI() {
    gui.reset();
  }

  // /**
  // * @effects returns <code>true</code> if this has an associated
  // * <code>AppGUI</code> instance, else returns <code>false</code>
  // * @return
  // */
  // private boolean hasGUI() {
  // return (gui != null);
  // }

  /**
   * A custom window listener used by the main <code>Controller</code> to handle
   * window's related events.
   * 
   * @effects return the singleton window helper
   * 
   */
  public WindowHelper getWindowHelper() {
    return wHelper;
  }

  /**
   * @effect return the singleton input helper
   */
  public InputHelper getInputHelper() {
    return ihelper;
  }

  /**
   * @requires 
   *  this is the main controller 
   *  
   * @effects 
   *  return the index manager
   * @version 2.7.4 
   */
  public IndexManager getIndexManager() {
    return indexManager;
  }

  // /////////// other getter methods
  /**
   * @effects returns the domain schema
   */
  public DSMBasic getDomainSchema() {
    return dodm.getDsm();
  }

  public DODMBasic getDodm() {
    return dodm;
  }

  /**
   * @effects returns the domain class of <code>this</code>
   */
  public Class<C> getDomainClass() {
    return cls;
  }

  /**
   * @requires
   *  this is a functional controller
   *  
   * @effects 
   *  return the language-aware label of this.domainClass
   * @version 
   * - 3.0: created<br>
   * - 3.1: added caching
   */
  public String getDomainClassLabel() {
    /*v3.1: cache this label to speed up
    return ControllerBasic.lookUpDomainClassLabel(getDomainClass());
    */
    Class cls = getDomainClass();
    return ControllerBasic.getDomainClassLabel(cls);
  }
  
  /**
   * Unlike {@link #getDomainClass()}, this method is invoked when there is no specific 
   * {@link ControllerBasic} to use. 
   * 
   * @effects 
   *  if not exists in {@link #domainClassLabelCache} a class label for cls
   *    look up class label and add it to {@link #domainClassLabelCache} 
   *  
   *  return class label
   *  
   * @version 3.1
   */
  public static String getDomainClassLabel(final Class cls) {
    String classLabel = null;
    if (domainClassLabelCache == null) {
      domainClassLabelCache = new HashMap();
    } else {
      classLabel = domainClassLabelCache.get(cls);
    }
    
    if (classLabel == null) {
      // look up and add to cache
      classLabel = lookUpDomainClassLabel(cls);
      domainClassLabelCache.put(cls, classLabel);
    }
    
    return classLabel;
  }
  
  /**
   * @effects return the string literal used as label for <tt>cls</tt> in
   *          <tt>this.</tt>{@link #module}; or return <tt>cls.simpleName</tt>
   *          if module is not specified.
   * 
   * @version 2.7.3
   */
  private static String lookUpDomainClassLabel(Class cls) {
    String clsLabel = null;
    ApplicationModule module = lookUpModule(cls);
    if (module != null) {
      clsLabel = module.getDomainClassLabelAsString();
    }

    if (clsLabel == null) {
      // use default
      return cls.getSimpleName();
    } else {
      return clsLabel;
    }
  }
  
  /**
   * @requires 
   *  this is the main controller
   * @effects 
   *  for each {@link RegionGui} <tt>rg</tt> in regions
   *    replace the domain class label of the domain class managed by the corresponding functional controller in <tt>this</tt>
   *    by <tt>rg.label</tt>
   * @version 3.1
   */
  private void updateDomainClassLabelCache(final Collection<Region> regions) {
    // the implementation is simplified by relooking up the domain class label 
    // because this look-up operation retrieves the (updated) label from the corresponding region GUI
    if (domainClassLabelCache != null) {
      String newLabel;
      for (Class cls : domainClassLabelCache.keySet()) {
        newLabel = lookUpDomainClassLabel(cls);
        domainClassLabelCache.put(cls, newLabel);
      }
    }
  }
  
  /**
   * @effects 
   *  initialise {@link #attribNameLabelMap} if not yet done so;
   *  if mapping  <tt>(attribName,attribLabel)</tt> is not in {@link #attribNameLabelMap} 
   *    add it to {@link #attribNameLabelMap} 
   * 
   * @requires
   *  attribName is a valid attribute name of <tt>this.</tt>{@link #cls}
   *   
   * @version 3.1 
   */
  public void addAttribNameLabelMapping(String attribName, String attribLabel) {
    if (attribNameLabelMap == null) {
      attribNameLabelMap = new HashMap();
      
      attribNameLabelMap.put(attribName, attribLabel);
    } else if (!attribNameLabelMap.containsKey(attribName)){
      attribNameLabelMap.put(attribName, attribLabel);
    }
  }
  
  /**
   * @effects
   *  if this is the main controller
   *    update domain class label cache
   *  else  
   *    update {@link #attribNameLabelMap} of <tt>this</tt> such that <tt>s2</tt> of each mapping <tt>(s1,s2)</tt> is replaced by 
   *    the label string of a region <tt>r</tt> in <tt>regions</tt> whose name is equal-to <tt>s1</tt>
   * @version 3.1 
   */
  public void updateOnLanguageChange(Collection<Region> regions) {
    if (this == getMainController()) {
      // main controller
      updateDomainClassLabelCache(regions);
    } else {
      // functional controller
      updateAttributeNameLabelMap(regions);
    }
  }
  
  /**
   * @effects
   *    update {@link #attribNameLabelMap} of <tt>this</tt> such that <tt>s2</tt> of each mapping <tt>(s1,s2)</tt> is replaced by 
   *    the label string of a region <tt>r</tt> in <tt>regions</tt> whose name is equal-to <tt>s1</tt>
   * @version 3.1 
   */
  private void updateAttributeNameLabelMap(final Collection<Region> regions) {
    if (attribNameLabelMap != null) {
      Collection<String> attribNames = attribNameLabelMap.keySet();
      for (String attribName : attribNames) {
        for (Region r : regions) {
          if (r.getName().equals(attribName)) {
            // found matching region: update label
            attribNameLabelMap.put(attribName, r.getLabelAsString());
            break;
          }
        }
      }
    }    
  }

  /**
   * @effects 
   *  return {@link #attribNameLabelMap}
   * @version 3.1
   */
  public Map<String,String> getAttribNameLabelMap() {
    return attribNameLabelMap;
  }

  /**
   * @effects 
   *  return the <tt>String</tt> label of a domain attribute named <tt>attribName</tt> defined in {@link #attribNameLabelMap}; 
   *  or return <tt>attribName</tt> if such label is not found
   *    
   * @version 3.2
   */
  public String getAttribNameLabel(String attribName) {
    if (attribNameLabelMap != null) {
      return attribNameLabelMap.get(attribName);
    } else {
      return attribName;
    }
  }
  
  /**
   * Some domain classes (e.g. DomainApplicationModuleWrapper) are wrappers over
   * the base classes. In these cases, this method can be used to return the
   * base class.
   * 
   * <p>
   * Note: <b>You should Only use this method</b> if the domain class of this
   * controller is a wrapper class. If it is not then use the regular method
   * {@link #getDomainClass()} instead.
   * 
   * @requires this.cls != null
   * 
   * @effects if userBasisIfExists AND this.cls is the wrapper class return the
   *          base class else return <tt>this.cls</tt>
   */
  public Class getDomainClass(boolean usebasisIfExists) {
    if (cls == null)
      return null;

    if (usebasisIfExists) {
      // try the base class first
      Class baseCls = dodm.getDsm().getWrappedBaseClass(cls);

      if (baseCls != null) {
        // wrapper class: return its base class
        return baseCls;
      } else {
        // use the domain class
        return cls;
      }
    } else {
      // use the domain class
      return cls;
    }
  }

  /**
   * @effects return the ApplicationModule for which this is defined.
   */
  public ApplicationModule getApplicationModule() {
    return module;
  }

  /**
   * @effects if this.module is not null return the <tt>ControllerConfig</tt> of
   *          this.module else return null
   */
  public ControllerConfig getControllerConfig() {
    if (module != null) {
      return module.getControllerCfg();
    } else {
      return null;
    }
  }

  /**
   * @modifies {@link #dataSourceMap}
   * 
   * @effects <pre>
   * if exists a data controller (dctl) for domainCls 
   *  return the shared data source configured for dctl 
   * else 
   *  return a simple data source for the objects of domain type
   *  (add this data source to {@link #dataSourceMap}
   *  </pre>
   */
  public static JDataSource getDataSourceInstance(Class domainCls) {
    JDataSource dataSource;

    ControllerBasic domainCtl = lookUp(domainCls,
        ControllerLookUpPolicy.PrimaryOnly);

    if (domainCtl != null) {
      // use the shared data source managed by the root-data controller of domainCtl
      ControllerBasic.DataController domainDctl = domainCtl
          .getRootDataController();
      dataSource = domainDctl.getDataSourceInstance();
    } else {
      // use a simple datasource
      dataSource = dataSourceMap.get(domainCls);
      if (dataSource == null) {
        
        /*v3.2c: moved to a proper JDataSource class so that we can manage
         *  its state correctly

        DOMBasic dom = dodm.getDom();
        
        //TODO: the following code may be replaced simply by dom.retrieveObjects(domainCls)
        // BUT this may affect situations where data source is not available for use 
        
        // check in object pool first
        Collection vals = dom.getObjects(domainCls);
        if (vals == null || vals.isEmpty()) {
          // may not have been loaded 
          try {
            // retrieve from data source
            Map<Oid,Object> valMap = dom.retrieveObjects(domainCls);
            
            if (valMap == null)
              vals = new ArrayList();
            else
              vals = valMap.values();
          } catch (NotPossibleException | DataSourceException e) {
            vals = new ArrayList();
          }
        }
                 
        final Collection values = vals;
        dataSource = new JDataSource(mainCtl, dodm, domainCls) {
          @Override
          public boolean isEmpty() {
            return values.isEmpty();
          }

          @Override
          public Iterator iterator() {
            return values.iterator();
          }
        };
         */
        dataSource = new JSimpleDataSource(mainCtl, dodm, domainCls);
        
        dataSourceMap.put(domainCls, dataSource);
      }
    }

    return dataSource;
  }

  /**
   * This method differs from {@link #getDataSourceInstance(Class)} in that it enables the use of a customised
   * data source type (<tt>dsType</tt>) and that the resulted data source is always added to {@link #dataSourceMap} 
   * (the other method tries to use a shared data source which is managed by the root data controller
   * of the domain class's module) 
   * 
   * @modifies {@link #dataSourceMap}
   * 
   * @effects <pre> 
   *  if exists in {@link #dataSourceMap} a {@link JDataSource} mapped to <tt>domainCls</tt> and 
   *  whose type is <tt>dsType</tt>
   *    return it
   *  else 
   *    create a {@link JDataSource} whose type is <tt>dsType</tt>.
   *    Add the created data source to {@link #dataSourceMap} and return it
   *  </pre>
   * @version 3.3
   */
  public static <T extends JDataSource> T getDataSourceInstance(Class domainCls, Class<T> dsType) {
    
    T dataSource = lookUpDataSource(domainCls, dsType);
    
    if (dataSource == null) {
      // create new 
      dataSource = JDataSourceFactory.createInstance(dsType, 
          mainCtl, 
          dodm, domainCls);

      dataSourceMap.put(domainCls, dataSource);
    }

    return dataSource;
  }

  /**
   * @effects 
   *  if exists in {@link #dataSourceMap} a {@link JDataSource} mapped to <tt>domainCls</tt> and 
   *  whose type is <tt>dsType</tt>
   *    return it
   *  else
   *    return null
   * @version 3.3
   */
  private static <T extends JDataSource> T lookUpDataSource(Class domainCls,
      Class<T> dsType) {
    if (dataSourceMap != null) {
      Class c; 
      JDataSource ds;
      for (Entry<Class, JDataSource> e : dataSourceMap.entrySet()) {
        c = e.getKey();
        ds = e.getValue();
        
        if (c.equals(domainCls) && dsType.isInstance(ds)) {
          return (T) ds;
        }
      }
    }
    
    // not found
    return null;
  }

  /**
   * @requires this is the main controller /\ security is enabled /\ user has
   *           logged in
   * @effects clear the states of all the data fields bounded to the data
   *          sources managed by the functional controllers of this.
   * 
   *          <pre>
   *  for each controller whose domain class the user has object-group permission on
   *    clear the state of the data source instance of the controller (so that it can be loaded 
   *    again)
   * </pre>
   * 
   * @version 2.7.2
   */
  private void clearAllDataSourceBindings() {
    // debug
    if (debug) System.out.println(this+".clearAllDataSourceBindings...");
    
    Iterator<ControllerBasic> childControllers = getFunctionalControllers();

    // 1. clear the data source bindings managed by the root data controller of each child controller
    if (childControllers != null) {
      ControllerBasic c;
      Class domainCls;
      while (childControllers.hasNext()) {
        c = childControllers.next();
        domainCls = c.getDomainClass();
        if (domainCls != null // v3.2c: && hasObjectGroupPermission(domainCls)
            ) {
          // debug
          if (debug) System.out.printf("   %s: %n", c);
          c.clearDataSourceBindings();
        }
      }
    }

    // 2. clear the data source bindings managed by this.dataSourceMap
    Class domainCls;
    JDataSource ds;
    
    for (Entry<Class, JDataSource> e : dataSourceMap.entrySet()) {
      domainCls = e.getKey();
      // debug
      if (debug) System.out.printf("   data source %s: %n", e.getValue());
      //v3.2c: if (hasObjectGroupPermission(domainCls)) {
        ds = e.getValue();
        ds.clearBindings();
        
        // v3.2c: needs to call this because ds is managed independently from DataController and so its buffer 
        // is not cleared as part of DataController.close() during log-out
        ds.clearBuffer();
      // }
    }
  }

  /**
   * @requires this is a functional controller
   * @effects clear the state of the components bounded to the data source
   *          managed by {@link #rootDctl}
   */
  private void clearDataSourceBindings() {
    getRootDataController().clearDataSourceBindings();
  }

  /**
   * @requires domainCls != null /\ assocCls != null
   * @effects if this has GUI return the child <tt>JDataContainer</tt> of the
   *          GUI of the module of the domain class <tt>domainCls</tt> that is
   *          responsible for displaying the domain objects of the
   *          <tt>assocCls</tt> else return null
   * 
   *          <p>
   *          Throws NotFoundException if no controller for <tt>domainCls</tt>
   *          is found or no data container is found satisfying the arguments
   */
  protected <T> JDataContainer<T> getChildDataContainer(Class domainClass,
      Class<T> assocCls) throws NotFoundException {
    ControllerBasic ctl = lookUp(domainClass,
        ControllerLookUpPolicy.PrimaryOnly);

    if (ctl == null) {
      throw new NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
          new Object[] { domainClass.getSimpleName() });
    }

    // v2.6.4b: add support for Gui check
    if (ctl.hasGUI()) {
      DataController dctl = ctl.getRootDataController();

      Iterator<JDataContainer> childContainers = dctl.getDataContainer()
          .getChildContainerIterator();
      if (childContainers != null) {
        JDataContainer child;
        while (childContainers.hasNext()) {
          child = childContainers.next();
          if (child.getController().getCreator().getDomainClass() == assocCls) {
            // found it
            return (JDataContainer<T>) child;
          }
        }
      }
    }

    // not found
    throw new NotFoundException(
        NotFoundException.Code.CHILD_DATA_CONTAINER_NOT_FOUND, new Object[] {
            assocCls.getSimpleName(), domainClass.getSimpleName() });
  }

  /**
   * @requires this is the main controller
   * 
   * @effects if there are child controllers of this.rootDataController that are
   *          configured with an auto-open policy (i.e. containing
   *          {@link OpenPolicy#A)) return them as Iterator else return null
   */
  protected Iterator<DataController> getAutoChildDataControllers(Class domainCls) {
    ControllerBasic ctl = lookUp(domainCls, ControllerLookUpPolicy.PrimaryOnly);

    // v2.6.4b: add support for Gui check
    if (ctl.hasGUI()) {
      DataController dctl = ctl.getRootDataController();

      Iterator<DataController> children = dctl
          .getChildAutoControllersIterator();
      return children;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if dataContainer is not visible 
   *    make it visible 
   *  else 
   *    do nothing
   * @version 
   * - 4.0: improved to support the case where dataContainer is contained within another container (e.g. a tab in a tab group)<br>
   * - 5.2: fixed a bug
   */
  public void showDataContainer(JDataContainer dataContainer) {
    if (!dataContainer.isVisible()) {
      //v5.2: moved to bottom (see below)
      // keeping this here causes SeqLayoutBuilder to improperly display the next view
      // dataContainer.setVisible(true);

      /*v4.0: if dataContainer is contained within a tab-group then activate the tab */
      DataContainerToolkit.updateContainerOnVisibilityChange(dataContainer);
      /*end: 4.0*/

      getMainController().getGUI().updateContainerLabelOnVisibilityUpdate(
          dataContainer, true);
      
      //v5.2: 
      dataContainer.setVisible(true);

    }
  }

  /**
   * @requires this is the main controller
   * 
   * @effects if there are child controllers of
   *          <tt>Controller< domainCls >.rootDataController</tt> that are
   *          configured with an auto-open policy (i.e. containing
   *          {@link OpenPolicy#A)) show them and invoke <tt>open</tt> to open
   *          the data else do nothing
   */
  protected void showAutoChildDataContainer(Class domainCls,
      JDataContainer excludeThis) {
    Iterator<DataController> autoChildren = getAutoChildDataControllers(domainCls);
    if (autoChildren != null) {
      DataController child;
      JDataContainer childContainer;
      while (autoChildren.hasNext()) {
        child = autoChildren.next();
        childContainer = child.getDataContainer();
        if (childContainer == excludeThis)
          continue; // skip

        // show the container
        showDataContainer(childContainer);

        // open data
        try {
          child.open();
        } catch (DataSourceException e) {
          logError("Failed to open auto child controller", e);
        }
      }
    }
  }

  /**
   * @effects 
   *  if dataContainer is not activated 
   *    activate it
   *  else 
   *    do nothing
   * @version 
   * - 2.7.3<br>
   */
  protected void activateDataContainer(JDataContainer dataContainer) {
    if (!dataContainer.hasFocus()) {
      InputHelper ihelper = getInputHelper();
      ihelper.activateDataContainer(dataContainer);
      ihelper.updateToolBarButtons();
    }
  }

  /**
   * @effects if there are child data controllers of this.rootDataController
   *          clear them
   * @version 2.7.3
   */
  protected void clearChildDataControllers() {
    DataController dctl = getRootDataController();
    dctl.clearChildren();
  }

  /**
   * @effects clear the data controller as specified in
   *          {@link ControllerBasic.DataController#clearAll()}
   * 
   * @version 2.7.3
   */
  protected void clearChildDataController(DataController dctl) {
    dctl.clearAll();
  }

  /**
   * @effects if exists a data controller (dctl) for domainCls return the
   *          DataValidator configured for dctl else return null
   */
  public static DataValidator getDataValidatorInstance(Class domainCls) {
    DataValidator validator = null;

    ControllerBasic domainCtl = lookUp(domainCls,
        ControllerLookUpPolicy.PrimaryOnly);

    if (domainCtl != null) {
      validator = domainCtl.getDataValidatorInstance();
    }

    return validator;
  }

  /**
   * @effects return the DataValidator configured for the top data controller of
   *          this
   */
  public DataValidator getDataValidatorInstance() {
    DataValidator validator = null;

    ControllerBasic.DataController domainDctl = getRootDataController();
    validator = domainDctl.getDataValidatorInstance();

    return validator;
  }

  // /////////////// GUI management methods /////////////////////////
  /**
   * @requires region names are unique
   * @effects return <tt>Region</tt> object whose name is <tt>name</tt>, throws
   *          NotFoundException if could not find such a region
   */
  public Region lookUpRegion(String name) throws NotFoundException {
    Query q = new Query(new Expression("name", Op.EQ, name));

    Collection<Region> list = dodm.getDom().getObjects(Region.class, q);

    if (list == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND,
          "Không tìm thấy đối tượng: {0}", q.toString());
    }

    return list.iterator().next();
  }

  /**
   * @requires region names are unique
   * @effects if <tt>Region</tt> object whose name is <tt>name</tt> has not been
   *          loaded from data source load it; return the <tt>Region</tt>
   * 
   *          <p>
   *          throws NotFoundException if could not find such a region
   */
  public Region loadRegion(String name) throws NotFoundException {
    Region r = null;
    try {
      r = lookUpRegion(name);
    } catch (NotFoundException e) {
      //
    }

    // load if not already exists
    if (r == null) {
      Class<Region> c = Region.class;
      String attribName = Region.AttributeName_name;
      Op op = Op.EQ;
      try {
        r = dodm.getDom().retrieveObject(c, attribName, op, name);
      } catch (DataSourceException e) {
        // ignore
      }

      if (r == null)
        throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND,
            "Không tìm thấy đối tượng: {0}", "Region(" + name + ")");
    }

    return r;
  }

  /**
   * @effects Returns <code>List<Region></code> of the child regions of the
   *          region <code>regionGui</code>.
   *          
   *          <p>In general, the GUI regions that apply to a GUI are the children of the
   * region that corresponds to the GUI's type. However, if the GUI has child
   * regions specified then use these over the type's regions
   */
  public List<Region> getGUIRegions(final Region regionGui)
      throws NotFoundException, NotPossibleException {

    /**
     * In general, the GUI regions that apply to a GUI are the children of the
     * region that corresponds to the GUI's type. However, if the GUI has child
     * regions specified then use these over the type's regions
     **/
    RegionType type = regionGui.getType();

    if (type == null) {
      throw new NotPossibleException(
          NotPossibleException.Code.CONFIGURATION_NOT_WELL_FORMED,
          new Object[] { regionGui.toString() });
    }

    List<Region> refRegions = new ArrayList();
    if (type == RegionType.Main) {
      // for main type, use the specified child regions
      List<Region> children = regionGui.getChildRegions();

      if (children == null) {
        throw new NotFoundException(
            NotFoundException.Code.CHILD_REGION_NOT_FOUND,
            "Không tìm thấy vùng con nào của {0}", regionGui);
      }

      // TODO: FIX this - this loop should not be needed
      // for some reasons, child regions contain duplicates (?)
      for (Region child : children) {
        if (!refRegions.contains(child))
          refRegions.add(child);
      }
    } else {
      // for non-main type, look up child regions of the type
      // to use
      Query q = new Query();
      Op eq = Op.EQ;
      q.add(new Expression("name", eq, type.getName()));
      Collection<Region> regions = dodm.getDom().getObjects(Region.class, q);
      if (regions == null || regions.isEmpty()) {
        throw new NotFoundException(NotFoundException.Code.SETTINGS_NOT_FOUND,
            "Không tìm thấy cấu hình vùng {0}", q.toString());
      }

      Region refRegion = regions.iterator().next();

      refRegions = refRegion.getChildRegions();
      if (refRegions == null) {
        throw new NotFoundException(
            NotFoundException.Code.CHILD_REGION_NOT_FOUND,
            "Không tìm thấy vùng con nào của {0}", refRegion);
      }
    }

    // if there are excluded regions then exclude from the above list
    // those found in the exclusion list
    List<Region> exclusion = regionGui.getExcludedRegions();
    if (exclusion != null) {
      Region region;
      for (int i = 0; i < refRegions.size(); i++) {
        region = refRegions.get(i);
        if (exclusion.contains(region)) {
          refRegions.remove(region);
          i--;
        } else {
          // process children and descendants of region (if any)
          // use region maps
          List<RegionMap> children = region.getChildren();
          if (children != null) {
            excludeRegion(children, exclusion);
          }
        }
      }
    }

    // final check
    if (refRegions.isEmpty()) {
      throw new NotFoundException(
          NotFoundException.Code.CHILD_REGION_NOT_FOUND,
          "Không tìm thấy vùng con nào của {0}", regionGui);
    }

    return refRegions;
  }

  /**
   * @modifies <tt>regions</tt>, {@link #processedRegionBuffer}
   * @effects 
   *  remove from <tt>regions</tt> and their descedants (if necessary) all <tt>region</tt>s 
   *  that are specified in <tt>exclusion<tt> 
   *  
   * @version
   * - 5.1: added this spec and improved to avoid endless-loop error when 
   * there is a cycle in the region graph
   */
  /*
  private void excludeRegion(final List<RegionMap> regions,
      final List<Region> exclusion) {
    RegionMap rmap;
    Region region;
    List<RegionMap> children;
    for (int i = 0; i < regions.size(); i++) {
      rmap = regions.get(i);
      region = rmap.getChild();
      if (exclusion.contains(region)) {
        regions.remove(rmap);
        i--;
      } else {
        // recursive call
        children = region.getChildren();
        if (children != null) {
          excludeRegion(children, exclusion);
        }
      }
    }
  }
  */
  private void excludeRegion(final List<RegionMap> regions,
      final List<Region> exclusion) {
    // TODO ? put processedBuffer on the stack of this method if concurrent invocation is used
    if (processedRegionBuffer != null) {
      processedRegionBuffer.clear();
    } else {
      processedRegionBuffer = new Stack<>();
    }
    
    excludeRegion(regions, exclusion, processedRegionBuffer);
  }
  
  /**
   * @modifies <tt>regions</tt>
   * @effects 
   *  remove from <tt>regions</tt> and their descedants (if necessary) all <tt>region</tt>s 
   *  that are specified in <tt>exclusion<tt>.
   *  
   *  <p>Overcome any cycles in the region graph. 
   *  
   * @version
   * - 5.1: created to avoid endless-loop error when 
   * there is a cycle in the region graph
   */
  private void excludeRegion(final List<RegionMap> regions,
      final List<Region> exclusion, final Stack<Region> processedBuffer) {
    RegionMap rmap;
    Region region;
    List<RegionMap> children;
    for (int i = 0; i < regions.size(); i++) {
      rmap = regions.get(i);
      region = rmap.getChild();
      if (processedBuffer.contains(region)) {
        // cycle detected, i.e. region already processed: 
        continue;
      } else {  // region not yet processed
        processedBuffer.push(region);
        if (exclusion.contains(region)) {
          regions.remove(rmap);
          i--;
        } else {
          // recursive call
          children = region.getChildren();
          if (children != null) {
            excludeRegion(children, exclusion, processedBuffer);
          }
        }
      }
    }
  }
  
  /**
   * @effects if exists a child region named <code>childName</code> of the
   *          region <code>region</code> then returns it, else returns
   *          <code>null</code>
   */
  public static Region getSettingsForChild(Region region, final String childName)
      throws NotFoundException, NotPossibleException {

    List<Region> children = region.getChildRegions();
    if (children == null) {
      throw new NotFoundException(
          NotFoundException.Code.CHILD_REGION_NOT_FOUND, new Object[] {childName, region });
    }

    for (Region child : children) {
      if (child.getName().equals(childName)) {
        return child;
      }
    }

    return null;
  }

  /**
   * @effects returns a <code>List<Region></code> of the child regions of
   *          <code>region</code>
   */
  public List<Region> getSettings(final Region region)
      throws NotFoundException, NotPossibleException {
    // final String dbSchema =
    // props.getStringValue(PropertyName.Language.name());

    /* v2.7.2 */
    // List<RegionMap> children = region.getChildren();
    //
    // if (children == null) {
    // throw new NotFoundException(
    // NotFoundException.Code.CHILD_REGION_NOT_FOUND,
    // "Không tìm thấy cấu hình con nào của vùng: {0}", region);
    // }
    //
    // Region child;
    // List<Region> regions = new ArrayList();
    //
    // for (RegionMap rm : children) {
    // child = rm.getChild();
    // regions.add(child);
    // }
    //
    // return regions;
    return getSettings(region, null);
  }

  /**
   * @effects returns a <code>List<Region></code> of the child regions of
   *          <code>region</code> filtered by <tt>filter</tt> (if specified)
   * 
   * @version 2.7.2
   */
  public List<Region> getSettings(final Region region, Filter<Region> filter)
      throws NotFoundException, NotPossibleException {

    List<RegionMap> children = region.getChildren();

    if (children == null) {
      throw new NotFoundException(
          NotFoundException.Code.CHILD_REGION_NOT_FOUND,
          "Không tìm thấy cấu hình con nào của vùng: {0}", region);
    }

    Region child;
    List<Region> regions = new ArrayList();

    for (RegionMap rm : children) {
      child = rm.getChild();
      if (filter == null || filter.check(child))
        regions.add(child);
    }

    return regions;
  }

  /**
   * @effects returns a <code>List<Region></code> of the configuration settings
   *          of a group of regions whose parent is the child of the region
   *          identified by <code>region</code>. Throws
   *          <code>NotPossibleException</code> if an error occured.
   * 
   *          <p>
   *          This type of 'referral setting' acts like a pointer to the
   *          settings of a shared region. It is often used in the configuration
   *          of a nested data component (e.g. the enrolments of a student).
   * @version 
   * - 3.0: support filter
   */
  public List<Region> getReferralSettings(Region region, Filter<Region> regionFilter)
      throws NotPossibleException {

    /**
     * the referral regions of a given region are the child regions of a child
     * region of the current region whose ID is smaller the the region's id
     */
    List<RegionMap> childMap = region.getChildren();

    if (childMap == null) {
      throw new NotPossibleException(
          NotPossibleException.Code.CONFIGURATION_NOT_WELL_FORMED,
          new Object[]{ region });
    }

    // get the referenced child
    Region refChild = null;
    Region child;
    int regionID = region.getId();
    for (RegionMap rm : childMap) {
      child = rm.getChild();
      if (child.getId() < regionID) {
        refChild = child;
        break; // stop at the first one
      }
    }

    if (refChild == null)
      throw new NotFoundException(
          NotFoundException.Code.REFERENCE_SETTINGS_NOT_FOUND, new Object[] {region});

    // get the child regions of the above child
    List<Region> refRegions = 
        // v3.0: getSettings(refChild);
        getSettings(refChild, regionFilter);

    return refRegions;
  }

  /**
   * @requires 
   *  <tt>linkedRegion</tt>.name = name of a domain attribute whose data type is <tt>domainCls</tt>
   *  
   * @effects returns a <code>List<Region></code> of the configuration settings
   *          of a group of regions whose parent is the child of the region
   *          identified by <code>linkedRegion</code>. Throws
   *          <code>NotPossibleException</code> if an error occured.
   * 
   *          <p>
   *          This type of 'referral setting' acts like a pointer to the
   *          settings of a shared region. It is often used in the configuration
   *          of a nested data component (e.g. the enrolments of a student).
   * @version 
   * - 3.2
   */
  public List<Region> getReferralSettings(RegionLinking linkedRegion, Class domainCls,           
      Filter<Region> regionFilter)
      throws NotPossibleException {
    
    List<RegionMap> childMap = linkedRegion.getChildren();

    if (childMap == null) {
      throw new NotPossibleException(
          NotPossibleException.Code.CONFIGURATION_NOT_WELL_FORMED,
          new Object[] {linkedRegion});
    }

    // get the referenced child (using containment tree if specified)
//    Class subCls = null;
//    if (containmentTree != null) {
//      DSMBasic dsm = getDomainSchema();
//      Class[] subTypes = dsm.getSubClasses(domainCls);
//      if (subTypes != null) {
//        // has sub-types
//        for (Class sub : subTypes) {
//          if (ApplicationToolKit.hasContainmentEdge(containmentTree, containerDomainCls, sub)) {
//            // found the first edge to a sub-type: use it and ignore others (if any)
//            subCls = sub;
//            break;
//          }
//        }
//      }
//    }

    // find the refChild
    // refChild = child of linkedRegion that is the comp. region of Module(subCls)
    Region refChild = null;
    Region child;
    int regionID = linkedRegion.getId();
//    if (subCls == null) {
//      // no link to sub-type: refChild = first child of linkedRegion s.t. refChild.id < linkedRegion.id
//      for (RegionMap rm : childMap) {
//        child = rm.getChild();
//        if (child.getId() < regionID) {
//          refChild = child;
//          break; // stop at the first one
//        }
//      }
//    } else {
      // has link to sub-type: refChild = child of linkedRegion that is the comp. region of Module(subCls)
      ApplicationModule moduleSubCls = lookUpModule(domainCls);
      String moduleSubClsName = moduleSubCls.getName();
      
      for (RegionMap rm : childMap) {
        child = rm.getChild();
        //if (child.getId() < regionID) {
          // check that it is the comp. region of Module(subCls)
          // use the module's name to search
          if (child.getName().equals(moduleSubClsName)) {
            // found the comp region of moduleSubClsName
            refChild = child;
            break; // stop at the first one
          }
        //}
      }      
//    }
    

    if (refChild == null)
      throw new NotFoundException(
          NotFoundException.Code.REFERENCE_SETTINGS_NOT_FOUND,
          new Object[] {linkedRegion});

    // get the child regions of the above child
    List<Region> refRegions = 
        // v3.0: getSettings(refChild);
        getSettings(refChild, regionFilter);

    return refRegions;
  }

  /**
   * @effects returns <code>Style</code> object containing the style settings
   *          for the region <code>region</code>.
   * 
   *          <p>
   *          The style settings of a region is a merged style from its own and
   *          those of the parents' and the ancestors'; otherwise throws
   *          <code>NotPossibleException</code>
   */
  public Style getStyleSettings(final Region region) throws NotFoundException,
      NotPossibleException {
    // get all the styles
    List<Style> styles = new ArrayList();
    getStyleSettings(region, styles);

    if (styles.isEmpty()) {
      throw new NotFoundException(
          NotFoundException.Code.STYLE_SETTINGS_NOT_FOUND,
          "Không tìm thấy cấu hình kiểu (style) của vùng: {0}", region);
    }

    // merge styles of the parents (and ancesters) into the current region style
    Style regionStyle = styles.remove(0);

    Style style = regionStyle.copy();
    for (Style pstyle : styles) {
      mergeStyle(style, pstyle);
    }

    return style;
  }

  /**
   * @modifies <tt>styles</tt>, {@link #processedRegionBuffer}
   * 
   * @effects recursively gets the styles from <code>region</code> and its
   *          parents and ancestors (if any) to put into <code>styles</code>
   * @version 
   * - 5.1: improved to overcome cycles in region graph
   */
  private void getStyleSettings(Region region, List<Style> styles) {
    /* v5.1: improved to overcome cycles 
    Style style = region.getStyle();

    if (style != null) {
      styles.add(style);
    }

    // if there are parents then get the parents and ancester styles
    List<RegionMap> parentMap = region.getParents();
    if (parentMap != null) {
      for (RegionMap pm : parentMap) {
        getStyleSettings(pm.getParent(), styles);
      }
    }
    */
    
    // TODO ? put processedBuffer on the stack of this method if concurrent invocation is used
    if (processedRegionBuffer != null) {
      processedRegionBuffer.clear();
    } else {
      processedRegionBuffer = new Stack<>();
    }
    
    getStyleSettings(region, styles, processedRegionBuffer);
  }

  /**
   * @effects recursively gets the styles from <code>region</code> and its
   *          parents and ancestors (if any) to put into <code>styles</code>
   *          
   * <p>Overcome cycles in the parent region map.
   * 
   * @version 5.1 : created
   */
  private void getStyleSettings(Region region, List<Style> styles, Stack<Region> processedBuffer) {
    Style style = region.getStyle();

    if (style != null) {
      styles.add(style);
    }

    // if there are parents then get the parents and ancester styles
    List<RegionMap> parentMap = region.getParents();
    if (parentMap != null) {
      for (RegionMap pm : parentMap) {
        Region parent = pm.getParent();
        
        if (processedBuffer.contains(parent)) {
          // parent already processed
          continue;
        } else {
          // parent not yet processed
          processedBuffer.push(parent);
          getStyleSettings(parent, styles, processedBuffer);
        }
      }
    }
  }
  
  /**
   * @effects Merges the style values in <code>style</code> with those in
   *          <code>pstyle</code>, with the values of former taking precedence
   *          over those of the later
   * @modifies <code>style</code>
   * 
   * @version 
   * - 3.2: exclude background colour from merging
   *  
   */
  private void mergeStyle(Style style, Style pstyle) {
    Object mv, v;

    DSMBasic dsm = dodm.getDsm();
    DOMBasic dom = dodm.getDom();
    Collection<DAttr> attribs = // v5.0: dsm.getAttributeConstraints(
        //Style.class);
        dsm.getDomainConstraints(Style.class);

    String attributeName;
    for (DAttr dc : attribs) {
      attributeName = dc.name();
      mv = dsm.getAttributeValue(style, attributeName);
      v = dsm.getAttributeValue(pstyle, attributeName);
      
      // v3.2: exclude bg color from merging
      if (attributeName.equals(Style.A_bgColor)) {
        continue;
      }
      
      // TODO: improve this
      if (mv == null && v != null) { // my style, if set, takes precedence
        dom.setAttributeValue(style, attributeName, v);
      }
    }
  }

//  /**
//   * This method is a thread-safe abstraction over the corresponding method
//   * {@link DODMBasic#retrieveObjectsWithAssociations(Class, Query)}.
//   * 
//   * @effects Load all objects of a domain class satisfying <tt>query</tt> and
//   *          also load the objects of the associated domain classes and the
//   *          objects referenced by these so on.
//   * 
//   *          <p>
//   *          Throws DBException if fails to load objects.
//   * 
//   * @example if <tt>this.cls = Student</tt> and <tt>Student</tt> is associated
//   *          to <tt>SClass</tt> then this will load all <tt>SClass</tt>
//   *          objects. And if <tt>SClass</tt> is associated to other domain
//   *          classes and the objects of these will be loaded and so on.
//   */
//  public synchronized Collection loadObjectsComplete(Query query)
//      throws DataSourceException {
//    return dodm.getDom().retrieveObjectsWithAssociations(cls, query);
//  }

//  /**
//   * @effects if the domain class of this contains collection-type attribute(s)
//   *          for each object of this populate the collection(s) of the
//   *          attribute value(s) else do nothing
//   * 
//   *          <p>
//   *          Throws DBException if fails to load objects from the database
//   */
//  public void loadReferencedObjects() throws DataSourceException {
//    List<DomainConstraint> attributes = dodm.getDsm().getAttributeConstraints(
//        cls);
//    // Stack colAttributes = new Stack();
//    boolean loadReferenced = false;
//    Select filter;
//    Class refType;
//    ControllerBasic refCtl;
//    for (DomainConstraint dc : attributes) {
//      // TODO: use Association instead
//      if (dc.type().isCollection()) {
//        // make sure that the collection type is loaded
//        filter = dc.filter();
//        if (filter.clazz() == MetaConstants.NullType) // NullType.class)
//          continue;
//        refType = filter.clazz();
//
//        // look up the main data controller of this type and use it to load
//        // objects
//        // to be thread-safe, we invoke this instead of invoking
//        // schema.loadObjects directly
//        if (dodm.getDom().isEmptyExtent(refType)) {
//          refCtl = lookUpExtended(refType, ControllerLookUpPolicy.First).get(0);
//          refCtl.loadObjectsComplete(null);
//        }
//
//        if (!loadReferenced)
//          loadReferenced = true;
//        // colAttributes.push(dc);
//      }
//    }
//
//    // now load all the referenced objects for each object of this controller
//    // if (!colAttributes.isEmpty()) {
//    if (loadReferenced) {
//      // schema.loadReferencedObjects(cls, colAttributes);
//      dodm.getDom().retrieveAssociatedObjects(cls);
//    }
//  }

  // /////////////// END GUI management methods /////////////////////////

  /**
   * Invokes this method only if this is a main controller.
   * 
   * @effects <pre>
   *            if this.mainCtl is null or this is not mainCtl
   *              do nothing
   *            else 
   *              updates the states of the tool bar of 
   *              mainCtl.gui based on the user permissions 
   *              associated to currentGUI
   * </pre>
   */
  // private void updateToolBarPermissions(final AppGUI currentGUI) {
  // if (appConfig.getUseSecurity())
  // mainCtl.updateToolBarPermissions(currentGUI, null, true, null);
  // }


  /**
   * This method is used to save GUI configuration settings to data source
   * 
   * @requires gui != null
   * @effects store <tt>changedSettings</tt> of this.gui.config to data source
   * 
   * <br>
   *          Throws NotPossibleException if failed to do so.
   * @version 
   *  - 2.7.3
   */
  public void saveGuiConfig(Map<DAttr, Object> changedSettings)
      throws NotPossibleException {
    if (gui == null)
      return;

    DOMBasic dom = dodm.getDom();
    
    // save configuration directly to RegionGui
    Region guiCfg = gui.getGUIConfig();
    try {
      dom.updateObject(guiCfg, changedSettings);
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_SAVE_GUI_CONFIG, e,
          new Object[] { gui.getTitle() });
    }
  }

  /**
   * @requires this is the main controller
   * 
   * @effects <pre>
   *              updates the states of the menu components of
   *              mainCtl.gui based on the current user permissions
   * </pre>
   * @version 2.7.2
   */
  private void updateMenuBarPermissions() {
    // Controller mainCtl = getMainController();
    // mainCtl.getGUI().
    gui.updateMenuBarPermissions();
  }
  
  /**
   * @requires this is the main controler
   */
  public void updateToolBarPermissions(final ControllerBasic currentCtl,
      DAttr containerField, boolean editable, final JDataField df) {
    // if (appConfig.getUseSecurity())
    gui.updateToolBarPermissions(currentCtl, containerField, editable, df);
  }
  
  /**
   * This method is invoked after a successful login to update the data permissions of the <b>existing</b>
   * object forms based on the new user's permissions. 
   * 
   * <p><b>Note:</b> these include ONLY the object forms that were created by the previous login (perhaps of another user).
   * They do NOT necessarily include all the object forms that the current user has permissions for. 
   * Some forms are only created when the user opens them (via the menu bar). For these forms, 
   * their permissions are updated separately via the gui-creation routine.  
   * 
   * 
   * @requires this is the main controller
   * @effects <pre>
   *  update the data field components of all the object forms that are <b>already created</b> 
   *  based on the current user permissions.
   * </pre>
   * @version 
   * - 3.1: update design spec and rename
   */
  private void updateDataPermissionsOfCreatedForms() {
    // Controller mainCtl = getMainController();
    Iterator<ControllerBasic> childControllers = getFunctionalControllers(); // mainCtl.getFunctionalControllers();

    if (childControllers != null) {
      ControllerBasic c;
      while (childControllers.hasNext()) {
        c = childControllers.next();
        if (c.isGuiCreated()) // v3.1: added this check
          c.updateDataPermissions();
      }
    }
  }

  /**
   * @requires 
   *  {@link #isGuiCreated()} = true
   *  
   * @effects 
   *  update the permissions on the data field components of the data container 
   *  of each data controller in the containment hierarchy of this.
   * @version 
   * - 3.1: changed to update permission on the data containers in the containment hierarchy of this AND
   *  changed to private
   */
  private void updateDataPermissions() {
    // v3.1: changed to a new implementation: to update permission on the child data containers of this
//    if (!dataControllerMap.isEmpty()) {
//      // System.out.println("Update data permission: " + this);
//      View userGUI;
//      boolean editable;
//      for (JDataContainer dcont : dataControllerMap.keySet()) {
//        /**
//         * if no user GUI that contains the container dcont has editable = false
//         * (note: this includes the parent and ancestor GUIs) update data
//         * permissions on all the data controllers of this controller else do
//         * nothing
//         */
//        userGUI = dcont.getController().getUser().getGUI();
//        editable = userGUI.isEditable(dcont);
//
//        if (editable) {
//          // System.out.println("..."+dcont.toString());
//          dcont.updateDataPermissions();
//        }
//      }
//    }
// end v3.1
    if (rootDctl != null) {
      // update data permissions on root data container and if it is nested then recursively 
      // to all its descendant data containers
      updateDataPermissions(rootDctl);
    }
  }

  /**
   * @requires 
   *  {@link #isGuiCreated()} = true
   *  
   * @effects 
   *  update data permissions on <tt>dctl.dataContainer</tt> and if it is nested then recursively 
   *     update data permissions all its descendants
   * @version 3.1 
   */
  private void updateDataPermissions(DataController dctl) {
    JDataContainer dcont = dctl.getDataContainer();

    //only update permission if dctl.user.gui (incl. the ancester GUIs, if any) has editable = false
    View userGUI = dctl.getUser().getGUI();
    // userGUI has been created
    boolean editable = userGUI.isEditable(dcont);

    if (editable) {
      // System.out.println("..."+dcont.toString());
      dcont.updateDataPermissions();
    }
    
    // update data permissions on the decescendants (if any)
    if (dctl.isNested()) {
      Iterator<DataController> children = dctl.getChildControllersIterator();
      
      DataController childDctl;
      while (children.hasNext()) {
        childDctl = children.next();
        updateDataPermissions(childDctl);
      }
    }
  }

  /**
   * @requires 
   *  {@link #isSecurityEnabled()} = true
   *  
   * @effects 
   *  if security is enabled and user is logged in
   *    return {@link Security} object responsible for managing security-related details
   *  else
   *    return null
   * @version 3.1 
   */
  public Security getSecurity() {
    if (isSecurityEnabled() && isLoggedIn()) {
      SecurityController securityCtl = (SecurityController) lookUp(Security.class);
      return securityCtl.getSecurity();
    } else {
      return null;
    }
  }

  /**
   * @requires security is enabled
   * @effects if current user has a permission on <tt>cmd</tt> on the specified class return
   *          true its value else throws NotFoundException
   * @version 3.4
   */
  public boolean getResourceStateOfDomainClassStrictly(String action, Class domainClass) throws NotFoundException {
    String classResourceName = dodm.getDsm().getResourceNameFor(domainClass);
    boolean strict = true;
    return getResourceState(action, classResourceName, strict);
  }
  
  /**
   * @requires security is enabled
   * @effects if current user has ANY permission on the specified class return
   *          true else return false
   */
  boolean getResourceStateOfDomainClassForAny(Class domainClass) {
    String classResourceName = dodm.getDsm().getResourceNameFor(domainClass);
    return getResourceState(LAName.LogicalAny.name(), classResourceName);
  }

  /**
   * @requires security is enabled
   * @effects if <tt>ctl</tt> has a domain class return true if user has
   *          permission on the domain class, otherwise return false else if
   *          <tt>ctl</tt> is a composite controller return true if user has
   *          permission on <b>all</tt> the domain classes of the component
   *          controllers; otherwise return false else return true
   */
  static boolean getResourceStateOfController(ControllerBasic ctl) {
    Class domainCls = ctl.getDomainClass();
    if (domainCls != null) {
      return getResourceState(null, dodm.getDsm().getResourceNameFor(domainCls));// schema.getDomainClassName(domainCls));
    } else if (ctl instanceof CompositeController) {
      CompositeController cctl = (CompositeController) ctl;
      Iterator<ControllerBasic> components = cctl.getComponentControllers();
      ControllerBasic compCtl;
      while (components.hasNext()) {
        compCtl = components.next();
        if (!getResourceStateOfController(compCtl)) {
          return false;
        }
      }

      // if get here then user has permission on all
      return true;
    } else {
      return true;
    }
  }

  /**
   * @requires security is enabled
   * 
   * @effect if current user has executable permission for <tt>m</tt> return
   *         true else return false
   * 
   * @version 
   * - 2.7.2 <br>
   * - v3.1: renamed to getResourceStateByDomainClass
   */
  public boolean getResourceStateOfModuleByDomainClass(ApplicationModule m) {
    Class domainCls = m.getDomainClassCls();
    if (domainCls != null) {
      // has domain class, return the permission on this class
      return getResourceState(null, dodm.getDsm().getResourceNameFor(domainCls));
      // schema.getDomainClassName(domainCls));
    } else if (m.isComposite()) {
      // composite module: must have permission on all component modules
      Iterator<ApplicationModule> modules = m.getChildModulesIterator();
      ApplicationModule mod;

      // debug
      if (modules == null) {
        throw new InternalError("Module has no children: " + m.getName());
      }

      while (modules.hasNext()) {
        mod = modules.next();
        if (!getResourceStateOfModuleByDomainClass(mod)) {
          return false;
        }
      }

      // if get here then user has permission on all
      return true;
    } else {
      // other cases: grant permission
      return true;
    }
  }

  /**
   * @requires 
   *  security is enabled /\ has object group permission on {@link ApplicationModule} 
   * 
   * 
   * @effect if current user has <tt>view</tt> permission for <tt>m</tt> return
   *         true else return false
   * 
   * @version 
   * - 3.1 : created
   */
  public boolean getViewResourceStateOfModule(ApplicationModule module) {
    String action = LAName.View.getName();
    return getResourceStateOfModule(action, module);
  }

  /**
   * @requires 
   *  security is enabled /\ has object group permission on {@link ApplicationModule} 
   * 
   * 
   * @effect if current user has some permission for <tt>m</tt> return
   *         true else return false
   * 
   * @version 
   * - 3.1 : created
   */
  public boolean getResourceStateOfModule(ApplicationModule m) {
    String action = null;
    return getResourceStateOfModule(action, m);
  }
  
  /**
   * @requires 
   *  security is enabled /\ has object group permission on {@link ApplicationModule} 
   * 
   * 
   * @effect 
   *  if current user has permission <tt>action</tt> for <tt>m</tt> 
   *    return true 
   *  else 
   *    return false
   * 
   * @version 
   * - 3.1 : created
   */
  private boolean getResourceStateOfModule(String action, ApplicationModule m) {
    String key = action + "->" + m.getOid().hashCode();
    Boolean cached = securityCache.get(key);

    // System.out.printf("<%s,%b>%n",key,cached);

    if (cached != null) {
      return cached;
    } else {
      SecurityController secCtl = (SecurityController) lookUp(Security.class);
      Security sec = secCtl.getSecurity();

      boolean state = true;
      if (sec.isLoggedIn()) {
        state = sec.getResourcePermissionOfModule(action, m, m.getOid());
        // place in cache
        securityCache.put(key, state);
      } else {
        // not yet logged in
        state = false;
      }
      
      return state;
    }
  }

  /**
   * A generic method that is used by other more specific method to look up user permission on 
   * a particular resource. 
   * 
   * @param action
   *          the name of the {@link LogicalAction} to be performed on resource
   * @param resourceName
   *          the name of the resource, which is either the command string (if
   *          the resource is a menu item) or the name of the domain class
   *          against which the <tt>action</tt> is to be performed.
   * 
   *          <p>
   *          Note the following rules about the above parameters:
   * 
   *          <pre>
   *      if resourceName is the name of a domain class
   *        both action and resourceName are not null
   *        (action is the command string of a button either on the tool bar or on the action panel) 
   *      else // resourceName is the command string of a menu item   
   *        action = null
   * </pre>
   * @requires <tt>this = mainCtl</tt>
   * @effects <pre>
   *            if Security object sec is not null
   *              return sec.getResourcePermission(action,resourceName)
   *            else 
   *              return true
   * </pre>
   * @deprecated (as of version 3.1) use {@link #getResourceState(String, String, boolean)}
   */
  public static boolean getResourceState(String action, String resourceName) {

    /*v3.1: replaced by new implementation (below)
    String key = action + "->" + resourceName;
    Boolean cached = securityCache.get(key);

    // System.out.printf("<%s,%b>%n",key,cached);

    if (cached != null) {
      return cached;
    } else {
      SecurityController secCtl = (SecurityController) lookUp(Security.class);
      Security sec = secCtl.getSecurity();

      boolean state = true;
      if (sec.isLoggedIn()) {
        state = sec.getResourcePermission(action, resourceName);
        // place in cache
        securityCache.put(key, state);
      } else {
        // not yet logged in
        state = false;
      }
      return state;
    }
    */
    boolean strictChecking = false;
    return getResourceState(action, resourceName, strictChecking);    
  }

  /**
   * A generic method that is used by other more specific method to look up user permission on 
   * a particular resource. 
   * 
   * @param action
   *          the name of the {@link LogicalAction} to be performed on resource
   * @param resourceName
   *          the name of the resource, which is either the command string (if
   *          the resource is a menu item) or the name of the domain class
   *          against which the <tt>action</tt> is to be performed.
   * 
   *          <p>
   *          Note the following rules about the above parameters:
   * 
   *          <pre>
   *      if resourceName is the name of a domain class
   *        both action and resourceName are not null
   *        (action is the command string of a button either on the tool bar or on the action panel) 
   *      else // resourceName is the command string of a menu item   
   *        action = null
   * </pre>
   * @requires this is the main controller
   * 
   * @effects <pre>
   *    if user is logged in
   *      if exists a user's permission for <tt>action, resourceName</tt>
   *        return <tt>true/false</tt> based on the permission
   *      else if strict = true
   *        throws NotFoundException (i.e. no such permission is defined)
   *      else 
   *        return <tt>true/false</tt> based on the default permission set for such pair
   *    else (// user is not logged in)
   *      return false
   * </pre>
   * @version 
   * - 3.1 : created
   */
  private static boolean getResourceState(String action, String resourceName, boolean strict) throws NotFoundException {

    String key = action + "->" + resourceName;
    Boolean cached = securityCache.get(key);

    // System.out.printf("<%s,%b>%n",key,cached);

    if (cached != null) {
      // found in cache: return immediately
      return cached;
    } else {
      SecurityController secCtl = (SecurityController) lookUp(Security.class);
      Security sec = secCtl.getSecurity();

      boolean state = true;
      if (sec.isLoggedIn()) {
        // user is logged in
        if (strict) {
          // strict checking: which throws NotFoundException if no suitable permission is found
          state = sec.getResourcePermissionStrict(action, resourceName);
        } else {
          // non-strict checking: which uses a default permission if no suitable one is found
          state = sec.getResourcePermissionWithDefault(action, resourceName);
        }
        
        // a permission (default or not) was found: place in cache
        securityCache.put(key, state);
      } else {
        // user not yet logged in
        state = false;
      }
      
      return state;
    }
  }
  
  /**
   * @requires
   * this is the main controller /\  
   * <tt>clsName != null /\ attributeName != null</tt>
   * @effects if a suitable resource permission exists for the current user for
   *          the domain attribute <tt>clsName.attributeName</tt> return its value
   *          else throw NotFoundException
   * 
   * @version 
   * - 3.4: added
   */
  public boolean getResourceStateOfDomainAttributeStrictly(String action, Class cls, String attributeName) {
    String attribResourceName = dodm.getDsm().getResourceNameFor(cls, attributeName); 
        
    boolean strictChecking = true;
    boolean state = getResourceState(action, attribResourceName, strictChecking);

    return state;
  }
  
  /**
   * @requires
   * this is the main controller /\  
   * <tt>clsName != null /\ attributeName != null</tt>
   * @effects if a suitable resource permission exists for the current user for
   *          the domain attribute <tt>clsName.attributeName</tt> return true
   *          else return false
   * 
   * @version 
   * - 3.1: changed name 
   */
  //public boolean getResourceState(String action, Class cls, String attributeName) {
  public boolean getResourceStateOfDomainAttribute(String action, Class cls, String attributeName) {
    String attribResourceName = dodm.getDsm().getResourceNameFor(cls, attributeName); 
    
    /* v3.1: improved to clearly distinguish the case where permission is not found 
    boolean state = getResourceState(action, attribResourceName);

    if (!state) {
      // use state of action set for the class (if any)
      String classResourceName = dodm.getDsm().getResourceNameFor(cls);
      state = getResourceState(action, classResourceName);
    }
    */
    boolean state;
    boolean strictChecking;
    try {
      strictChecking = true;
      state = getResourceState(action, attribResourceName, strictChecking);
    } catch (NotFoundException e) {
      // not found: // use state of action set for the class (if any)
      String classResourceName = dodm.getDsm().getResourceNameFor(cls);
      strictChecking = false;
      state = getResourceState(action, classResourceName, strictChecking);
    }

    return state;
  }

  /**
   * @requires <tt>attributeName != null</tt>
   * @effects if there is a current permission for the attribute
   *          <tt>attributeName</tt> return true/false based on the permission
   *          else return true/false based on the current permission on the
   *          domain class <tt>cls</tt>
   */
  public boolean getAttributeEditableState(String attributeName) {
    if (isSecurityEnabled()) {
      String action = Update.name(); // LogicalAction.Update.getName();

      return getResourceStateOfDomainAttribute(action, cls, attributeName);
    } else {
      return true;
    }
  }

  /**
   * @effects return a List<LAName> object containing the names of the logical
   *          action that the user is NOT allowed to operate on a data container
   *          component whose associated domain constraint is <tt>dc</tt>, and
   *          whose GUI region is <tt>region</tt>; i.e. if dc.mutable=false ||
   *          region.editable = false add [New, Update, Delete] to list if
   *          dc.auto=true add [Open, Refresh] to list else return [] (empty
   *          list)
   *  @version
   *  - 5.2: fixed to allow New and Delete actions when editable=false
   */
  public List<LAName> getDisallowedActionsByConfig(DAttr dc,
      boolean editable) {
    List<LAName> disallowedActions = new ArrayList();
    // readonly if mutable=false OR region.editable=false
    if ((dc != null && dc.mutable() == false) || editable == false) {
      /* v5.2: 
      Collections.addAll(disallowedActions, New, Update, Delete);
       */
      Collections.addAll(disallowedActions, Update);
      
      if (dc != null && dc.auto() == true) {
        // derived -> disallow Open and Refresh
        Collections.addAll(disallowedActions, Open, Refresh);
      }
    }

    return disallowedActions;
  }

  /**
   * @requires this is the main controller /\ security is enabled /\ user is
   *           logged in /\ c is a registered domain class
   * @effects if user has objectgroup-typed permission on objects of <tt>c</tt>
   *          (i.e. there exists a user's permission over a logical resource of
   *          <tt>c</tt> whose object group is not null) return true else return
   *          false
   * 
   * @version v2.7.2
   */
  public boolean hasObjectGroupPermission(Class c) {
    if (isSecurityEnabled()) {
      SecurityController security = (SecurityController) lookUp(Security.class);
      if (security.isLoggedIn()) {
        // check in cache first
        Boolean cached = securityCache.get(c);

        if (cached != null) {
          return cached;
        } else {
          boolean hasPerm = security.getSecurity().hasObjectGroupPermission(c);
          // cache the permission
          securityCache.put(c, hasPerm);
          return hasPerm;
        }
      }
    }

    // no such permission
    return false;
  }

  /**
   * @requires this is the main controller /\ user is logged in /\ user has
   *           object-group permission on c /\ oid is a valid object id of c
   * 
   * @effects if <tt>user</tt> has permission to access domain object of the
   *          domain class <tt>c</tt> whose <tt>Oid</tt> is <tt>id</tt> return
   *          true else return false
   * @version 2.7.2
   */
  public boolean getObjectPermission(Class c, Oid id) {
    // TODO: cache this permission if it is accessed frequently
    SecurityController security = (SecurityController) lookUp(Security.class);
    return security.getSecurity().getObjectPermission(c, id);
  }

  /**
   * @effects <pre>
   * If this is top-level controller 
   *  shut down all the functional controllers
   *  shut down gui 
   *  terminate the application
   * else 
   *  close
   *  shutdown gui
   *  
   * Throws Exception if an error occured
   * </pre>
   */
  public void shutDown() throws Exception {
    // if there are child controllers then also shut them down
    if (gui != null) {
      if (gui.isTopLevel()) {
        // main module
        // v2.7.3:
        preShutDown();

        logFromCode(MessageCode.SHUTTING_DOWN_PROGRAM,
        // "{0} đang đóng chương trình..."
            //"{0} is closing program...", 
            new Object[] {getName()});
        if (funcControllerMap != null) {
          for (ControllerBasic c : funcControllerMap.values()) {
            try { // v2.7.3: added this to isolate controller-specific errors in
                  // shutting down
              // v2.7.3: added pre-shutDown
              c.preShutDown();
              c.shutDown();
            } catch (Exception e) {
              if (debug)
                logError(null, e);
            }
          }
        }
        // TODO: stop all the top-level tasks
        gui.shutDown();

        // v2.7.3: IMPORTANT: this must be done after all other closures (above)
        dodm.close();

        System.exit(0);
      } else {
        // functional module
        close();
        if (gui != null) {
          // TODO: stop all the existing tasks of this module
          gui.shutDown();
        }
      }
    }
  }

  /**
   * @effects perform tasks before shut down (e.g. save GUI config)
   * @version 2.7.3
   */
  private void preShutDown() {
    // v2.7.3: store GUI settings
    if (gui != null) {
      try {
        // v2.8: added this check
        if (dodm.isObjectSerialised())
          gui.saveConfig();
      } catch (NotPossibleException e) {
        logError(null, e);
      }
    }
  }

  /**
   * @effects invoke shutdown if an error occured terminate the application with
   *          a non-zero error code
   */
  public void forceShutDown() {
    try {
      shutDown();
    } catch (Exception e) {
      // ignore
      if (debug)
        e.printStackTrace();

      System.exit(1);
    }
  }

  /**
   * @effects closes the module tasks that are being executed and hides all the
   *          module GUIs
   * @version 
   * - 3.1: fixed to close resources (e.g. data controllers) even if gui is not created
   */
  public void close() throws Exception {
    // works similar to shutdown, except that we donot shutdown the modules and
    // application
    
    if (isMainController()) {
      // main controller
      logFromCode(MessageCode.CLOSING_MODULES, new Object[] {getName()});

      // stop all modules
      if (funcControllerMap != null) {
        for (ControllerBasic c : funcControllerMap.values()) {
          c.close();
        }
      }

      // TODO: stop all the top-level tasks

      // clear resources
      securityCache.clear();
    } else {
      // functional controller
      logFromCode(MessageCode.STATUS_LINE, new Object[] {getName()});
      // TODO: stop all the existing tasks of this module
      if (dataControllerMap != null) {
        // v3.3: moved to a method
//        Collection<DataController> dctls = dataControllerMap.values();
//        //System.out.println("   has data controllers: \n   " + dctls);
//        /** must clear All data controllers GUIs before clearing the data objects (below) */
//        for (DataController dctl : dctls) {
//          dctl.clearGUI(false); // without children (b/c those children will be cleared by their respective Controller)
//        }
//        
//        // clear data objects
//        for (DataController dctl : dctls) {
//          //System.out.println("   " + dctl+".clear()...");
//          dctl.clear(true);
//        }
//        
//        // v3.3: added this for the case that root-dctl is not added to dataControllerMap
//        if (!dataControllerMap.containsValue(rootDctl)) {
//          rootDctl.clearGUI(false);
//          rootDctl.clear(true);
//        }
//
//        // v2.7.2: wait for all tasks to finish
//        for (DataController dctl : dctls) {
//          dctl.stopTaskMan();
//        }
        closeDataControllers();
      }

      if (gui != null && gui.isCreated()) {
        resetGUI();
        hideGUI(gui);
      }
    }
  }

  /**
   * This is performed as part of {@link #close()} that is called upon a functional controller.
   * 
   * @requires 
   *  this is a functional controller /\ {@link #dataControllerMap} != null
   * @effects 
   *  Close all the data controllers owned by this (including the root data controller) 
   *  
   * @version 3.3
   */
  private void closeDataControllers() {
    if (dataControllerMap == null)
      return;
    
    Collection<DataController> dctls = dataControllerMap.values();
    //System.out.println("   has data controllers: \n   " + dctls);

    /** must clear All data controllers GUIs before clearing the data objects (below) */
    for (DataController dctl : dctls) {
      // v3.3: cancel any existing operations that are performed
      if (dctl.isActionPerforming()) {
        dctl.cancel(true);
      }
      
      dctl.clearGUI(false); // without children (b/c those children will be cleared by their respective Controller)
    }
    
    // clear data objects
    for (DataController dctl : dctls) {
      //System.out.println("   " + dctl+".clear()...");
      // v3.3: use a separate method: 
      // dctl.clear(true);
      dctl.clearOnClose();
    }
    
    // v3.3: added this for the case that root-dctl is not added to dataControllerMap
    if (!dctls.contains(rootDctl)) {
      if (rootDctl.isActionPerforming()) {
        rootDctl.cancel(true);
      }
      
      rootDctl.clearGUI(false);
      // v3.3: use a separate method: 
      //rootDctl.clear(true);
      rootDctl.clearOnClose();
    }

    // v2.7.2: wait for all tasks to finish
    for (DataController dctl : dctls) {
      dctl.stopTaskMan();
    }
    
    // v3.3: added this for the case that root-dctl is not added to dataControllerMap
    if (!dctls.contains(rootDctl)) {
      rootDctl.stopTaskMan();
    }
  }

  /**
   * An optional method for sub-types to implement.
   * 
   * @effects refresh the state of this
   */
  public void refresh() {
    // for sub-types to implemement
  }

  /**
   * @effects if security is enabled return true else return false
   */
  public boolean isSecurityEnabled() {
    return appConfig.getUseSecurity();
  }

  /**
   * @requires 
   *  this is the main controller
   *  
   * @effects if the application supports different languages (i.e. language
   *          configurator module is loaded) return true else return false
   */
  public boolean isSupportInternalisation() {
    /*v3.0: changed to application module's property
    return appConfig.getIsInternationalisationSupport();
    */
    boolean defaultVal = false;
    return getApplicationModule().getViewCfg().getProperty(PropertyName.view_lang_international, Boolean.class, defaultVal);

    /*
     * v2.7.3: changed to use a configuration option if (isSupportLanguage ==
     * null) { try { lookUpByControllerType(LanguageConfigurator.class);
     * isSupportLanguage = true; } catch (NotFoundException e) {
     * isSupportLanguage = false; } }
     * 
     * return isSupportLanguage;
     */
  }

  /**
   * @effects if the application supports chart return true else return false
   */
  public boolean isSupportChart() {
    if (isSupportChart == null) {
      /*
       * v2.7.3: check the existence of the chart button if
       * (lookUp(ChartWrapper.class) != null) { isSupportChart = true; } else {
       * isSupportChart = false; }
       */
      isSupportChart = gui.isVisibleDesktopComponent(RegionName.Chart);
    }

    return isSupportChart;
  }

  /**
   * @effects returns <code>true</code> if <code>gui</code> is selected, else
   *          returns <code>false</code>.
   */
  private boolean isActive() {
    if (gui != null) {
      return gui.isActive();
    } else
      return false;
  }

  /**
   * @requires cls != null
   * @effects if the domain class of this is not null and the same as cls return
   *          true else return false
   */
  public boolean isDomainClassType(Class cls) {
    return (this.cls != null && this.cls == cls);
  }

  /**
   * @effects if this has finished all initialisation tasks (incl. creating GUI)
   *          return true else return false
   * @version 2.7.4
   */
  public boolean isInitialised() {
    return initialised;
  }

  /**
   * (TODO: is this necessary?) @requires this is the main controller
   * 
   * 
   * @effects if security is configured if authenticated return true else return
   *          false else return true
   */
  public boolean isLoggedIn() {
    if (isSecurityEnabled()) {
      SecurityController security = (SecurityController) lookUp(Security.class);
      return security.isLoggedIn();
    } else {
      return true;
    }
  }

// v3.2: removed  
//  /**
//   * @effects if <code>this.cls</code> is-a <code>Report</code> then returns
//   *          <code>true</code> else returns <code>false</code>
//   * @deprecated as of version 2.7.3
//   */
//  private boolean isReport() {
//    if (isReport == null) {
//      // return this.getClass() == ReportController.class;
//      /*
//       * -- a more expensive check: try { return (cls != null &&
//       * (cls.asSubclass(Report.class) != null)); } catch (ClassCastException e)
//       * { return false; }
//       */
//      boolean tf;
//      try {
//        tf = (cls != null && (cls.asSubclass(Report.class) != null));
//      } catch (ClassCastException e) {
//        return false;
//      }
//      isReport = new Boolean(tf);
//    }
//
//    return isReport.booleanValue();
//  }

  public boolean isSerialisable() {
    if (isSerialisable == null)
      isSerialisable = !dodm.getDsm().isTransient(cls);

    return isSerialisable.booleanValue();
  }

  /**
   * @effects 
   *  return {@link DSMBasic#isEditable(Class)} on ({@link #getDomainSchema()}, {@link #cls}) 
   */
  protected boolean isEditable() {
    if (isEditable == null)
      isEditable = dodm.getDsm().isEditable(cls);

    return isEditable.booleanValue();
  }

  /**
   * @effects if this has gui /\ search tool bar is visible on gui return true
   *          else return false
   */
  protected boolean isSearchOn() {
    if (gui != null) {
      return gui.isVisibleContainer(SearchToolBar);
    } else {
      return false;
    }
  }

  /**
   * @requires cls != null
   * @effects if the domain class <tt>cls</tt> is a singleton class (i.e. its
   *          class constraint's singleton() = true) return true else return
   *          false
   */
  private boolean isSingleton() {
    if (isSingleton == null)
      isSingleton = dodm.getDsm().isSingleton(cls);

    return isSingleton;
  }


  /**
   * @effects 
   *  if this.gui is configured with a {@link Property} that specifies <tt>true</tt> for auto-export
   *    return <tt>true</tt>
   *  else
   *    return <tt>false<tt>
   * @version 3.0
   */
  protected boolean isAutoExport() {
    if (isAutoExport == null) {
      boolean defaultVal = false; // not to auto-export
      isAutoExport = getApplicationModule().getViewCfg().getProperty(PropertyName.view_objectForm_autoExport, Boolean.class, defaultVal);
    }
    
    return isAutoExport;
  }
  
  /**
   * @effects if browsing functions are enabled return true else return false
   * @requires this is a top-level controller
   */
  private boolean isBrowsingEnabled() {
    boolean isBrowsingEnabled;

    try {
      Region browseFirst = lookUpRegion(RegionName.First.name());
      // check visibility of the browse first button
      isBrowsingEnabled = gui.isVisibleComponent(browseFirst);
    } catch (NotFoundException e) {
      // no such function
      isBrowsingEnabled = false;
    }

    return isBrowsingEnabled;
  }

// v3.1
//  /**
//   * @effects display a message out on the terminal console, whose content is
//   *          <tt>m</tt>
//   * 
//   * @deprecated as of version 2.7.2 (do not use)
//   */
//  public void displayConsoleMessage(String m) {
//    log(MessageCode.UNDEFINED, m);
//  }

  /** The maximum length (in number of characters) of a dialog message */
  private static final int MESSAGE_MAX_SIZE = 60;

// v3.2  
//  /**
//   * @effects display an information message dialog attached to
//   *          <tt>this.gui</tt> whose standard content is <tt>m</tt> and
//   *          parameter data is <tt>data</tt>. The message code is <tt>code</tt>
//   *          .
//   */
//  public String displayMessage(InfoCode code, String m, Object... data) {
//    return displayMessage(code, null, null, m, data);
//  }

// v3.2
//  public String displayMessage(InfoCode code, DataController source, String m,
//      Object... data) {
//    return displayMessage(code, null, source, m, data);
//  }

  /**
   * @requires code.getText() != null
   * @effects invoke
   *          {@link #displayMessageFromCode(InfoCode, Throwable, DataController, Object...)}
   *          with <tt>code.getText</tt> is used as message.
   */
  public String displayMessageFromCode(InfoCode code, DataController source,
      Object... data) {
    // v3.2: return displayMessage(code, null, source, code.getText(), data);
    return displayMessageFromCode(code, null, source, data);
  }
  
  /**
   * @requires code.getText() != null
   * @effects 
   *  display a <i>modaless</i> informational message dialog from information provided in <tt>code, e, data</tt>  
   * @version 
   * - 3.2: rewritten to use MessageFormat <br>
   * - 3.2c: changed to modaless
   */
  public String displayMessageFromCode(InfoCode code, Throwable e,
      DataController source, Object... data) {
    // v3.2: return displayMessage(code, e, source, code.getText(), data);
    Component parent = null;
    if (source != null) {
      View userGUI = source.getUserGUI();
      if (userGUI != null)
        parent = userGUI.getGUI();
    }

    if (parent == null) {
      // use this gui (if any)
      parent = (gui != null) ? gui.getGUI() : null;
    }

    /**
     * TODO if application support language use message code to look up title
     * text and message suitable for the current language combine message with
     * data to display on the dialog
     */
    String m = code.getMessageFormat().format(data);
    
    if (e != null) {
      Throwable cause = e.getCause();
      if (cause != null)
        m += "\nCause:\n" + cause.getClass().getSimpleName() + ": "
            + cause.getMessage();
      else
        m += "\nCause:\n" + e.getClass().getSimpleName() + ": "
            + e.getMessage();
    }

//    /**
//     * TODO if application support language use message code to look up title
//     * text and message suitable for the current language combine message with
//     * data to display on the dialog
//     */
//    Format fmt = new MessageFormat(m);
//    m = fmt.format(data);

    Object mObj;
    if (m.length() <= MESSAGE_MAX_SIZE) {
      mObj = m;
    } else {
      // m is too long, break into an array of smaller strings
      mObj = breakMessage(m);
    }

    // show message
    /* v3.2c: use modaless dialog
    JOptionPane.showMessageDialog(parent, mObj, "Thông tin",
        JOptionPane.INFORMATION_MESSAGE);
    */
    if (parent == null)
      parent = getMainController().getGUI().getGUI();
    
    if (infoDialogCache == null)
      infoDialogCache = new DialogCache();

    JDialog dialog = infoDialogCache.getDialog(parent);
    JOptionPane infoDialogPane;
    if (dialog == null) {
      // dialog not yet created for parent: create dialog once for parent and stores in cache
      infoDialogPane = new JOptionPane(mObj, JOptionPane.INFORMATION_MESSAGE);
      dialog = infoDialogPane.createDialog(parent, "Thông tin");
      dialog.setModal(false);
      infoDialogCache.putDialog(parent, infoDialogPane, dialog);
    } else {
      // dialog alread created in cache, update its message
      infoDialogPane = infoDialogCache.getDialogPane(parent);
      infoDialogPane.setMessage(mObj);
    }

    // v3.3: added this to pack dialog to fit message size
    dialog.pack();
    
    dialog.setVisible(true);
    
    return m;
  }

// v3.2  
//  public String displayMessage(InfoCode code, Throwable e,
//      DataController source, String m, Object... data) {
//    Component parent = null;
//    if (source != null) {
//      View userGUI = source.getUserGUI();
//      if (userGUI != null)
//        parent = userGUI.getGUI();
//    }
//
//    if (parent == null) {
//      // use this gui (if any)
//      parent = (gui != null) ? gui.getGUI() : null;
//    }
//
//    if (e != null) {
//      Throwable cause = e.getCause();
//      if (cause != null)
//        m += "\nCause:\n" + cause.getClass().getSimpleName() + ": "
//            + cause.getMessage();
//      else
//        m += "\nCause:\n" + e.getClass().getSimpleName() + ": "
//            + e.getMessage();
//    }
//
//    /**
//     * TODO if application support language use message code to look up title
//     * text and message suitable for the current language combine message with
//     * data to display on the dialog
//     */
//    Format fmt = new MessageFormat(m);
//    m = fmt.format(data);
//
//    Object mObj;
//    if (m.length() <= MESSAGE_MAX_SIZE) {
//      mObj = m;
//    } else {
//      // m is too long, break into an array of smaller strings
//      mObj = breakMessage(m);
//    }
//
//    // show message
//    JOptionPane.showMessageDialog(parent, mObj, "Thông tin",
//        JOptionPane.INFORMATION_MESSAGE);
//
//    return m;
//  }

  /**
   * @requires code.getText() != null
   * @effects 
   *  display an <tt>Yes/No</tt> confirmation message dialog relative to <tt>source.dataContainer</tt> 
   *  from the message pattern 
   *  defined in <tt>code</tt> and message arguments specified in <tt>data</tt>.
   * 
   * if the user answer <tt>Yes</tt> return true else return fals
   * 
   * @version 
   * - 3.2: rewritten to use code directly
   */
  public boolean displayConfirmFromCode(
      //v3.2: MessageCode code,
      InfoCode code,
      DataController source, Object... data) {
    //return displayConfirm(code, source, code.getText(), data);
    Component parent;
    if (source != null) {
      parent = source.getUserGUI().getGUI();
    } else {
      parent = (gui != null) ? gui.getGUI() : null;
    }

    /**
     * TODO if application support language use message code to look up title
     * text and message suitable for the current language combine message with
     * data to display on the dialog
     */
    Format fmt = code.getMessageFormat(); //new MessageFormat(m);
    String m = fmt.format(data);

    Object mObj;
    if (m.length() <= MESSAGE_MAX_SIZE) {
      mObj = m;
    } else {
      // m is too long, break into an array of smaller strings
      mObj = breakMessage(m);
    }

    int user = JOptionPane.showConfirmDialog(parent, mObj, "Xác nhận",
        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

    if (user == JOptionPane.YES_OPTION) {
      return true;
    } else {
      return false;
    }
  }

// v3.2  
//  /**
//   * @effects display an <tt>Yes/No</tt> confirmation message dialog. The
//   *          message content is a combination of <tt>m</tt> (or an alternative
//   *          message suitable for the current application language) and
//   *          <tt>data</tt>
//   * 
//   *          if the user answer <tt>Yes</tt> return true else return false
//   * @deprecated as of v3.2 (instead use {@link #displayConfirmFromCode(InfoCode, DataController, Object...))
//   */
//  public boolean displayConfirm(InfoCode code, DataController source, String m,
//      Object... data) {
//    // Component parent = (gui != null) ? gui.getGUI() : null;
//    Component parent;
//    if (source != null) {
//      parent = source.getUserGUI().getGUI();
//    } else {
//      parent = (gui != null) ? gui.getGUI() : null;
//    }
//
//    /**
//     * TODO if application support language use message code to look up title
//     * text and message suitable for the current language combine message with
//     * data to display on the dialog
//     */
//    Format fmt = new MessageFormat(m);
//    m = fmt.format(data);
//
//    Object mObj;
//    if (m.length() <= MESSAGE_MAX_SIZE) {
//      mObj = m;
//    } else {
//      // m is too long, break into an array of smaller strings
//      mObj = breakMessage(m);
//    }
//
//    int user = JOptionPane.showConfirmDialog(parent, mObj, "Xác nhận",
//        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
//
//    if (user == JOptionPane.YES_OPTION) {
//      return true;
//    } else {
//      return false;
//    }
//  }

  /**
   * @effects display an <tt>Yes/No</tt> confirmation message dialog with
   *          warning sign. The message content is a combination of <tt>code</tt>
   *          (or an alternative message suitable for the current application
   *          language) and <tt>data</tt>
   * 
   *          if the user answer <tt>Yes</tt> return true else return false
   *          
   * @version 
   * - 3.0 <br>
   * - 3.2: rewritten to use MessageFormat embedded in code
   */
  public boolean displayWarningFromCode(InfoCode code, DataController source,
      boolean withConfirm, Object... data) {
    //v3.2: return displayWarning(code, source, code.getText(), withConfirm, data);
    Component parent;
    if (source != null) {
      parent = source.getUserGUI().getGUI();
    } else {
      parent = (gui != null) ? gui.getGUI() : null;
    }

    /**
     * TODO if application support language use message code to look up title
     * text and message suitable for the current language combine message with
     * data to display on the dialog
     */
    //Format fmt = new MessageFormat(m);
    //m = fmt.format(data);
    String m = code.getMessageFormat().format(data);

    Object mObj;
    if (m.length() <= MESSAGE_MAX_SIZE) {
      mObj = m;
    } else {
      // m is too long, break into an array of smaller strings
      mObj = breakMessage(m);
    }

    /* v3.4: only use YES-NO if withConfirm = true
    int user = JOptionPane.showConfirmDialog(parent, mObj, "Cảnh báo",
        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

    if (user == JOptionPane.YES_OPTION) {
      return true;
    } else {
      return false;
    }    
    */
    if (withConfirm) {
      // with confirmation
      int user = JOptionPane.showConfirmDialog(parent, mObj, "Cảnh báo",
          JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
      if (user == JOptionPane.YES_OPTION) {
        return true;
      } else {
        return false;
      }          
      
    } else {
      // no confirm, return anything
      JOptionPane.showMessageDialog(parent, mObj, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
      
      return true;
    }    
  }
 
// v3.2  
//  /**
//   * @effects display an <tt>Yes/No</tt> confirmation message dialog with
//   *          warning sign. The message content is a combination of <tt>m</tt>
//   *          (or an alternative message suitable for the current application
//   *          language) and <tt>data</tt>
//   * 
//   *          if the user answer <tt>Yes</tt> return true else return false
//   */
//  public boolean displayWarning(InfoCode code, DataController source, String m,
//      boolean withConfirm, Object... data) {
//    Component parent;
//    if (source != null) {
//      parent = source.getUserGUI().getGUI();
//    } else {
//      parent = (gui != null) ? gui.getGUI() : null;
//    }
//
//    /**
//     * TODO if application support language use message code to look up title
//     * text and message suitable for the current language combine message with
//     * data to display on the dialog
//     */
//    Format fmt = new MessageFormat(m);
//    m = fmt.format(data);
//
//    Object mObj;
//    if (m.length() <= MESSAGE_MAX_SIZE) {
//      mObj = m;
//    } else {
//      // m is too long, break into an array of smaller strings
//      mObj = breakMessage(m);
//    }
//
//    int user = JOptionPane.showConfirmDialog(parent, mObj, "Cảnh báo",
//        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//
//    if (user == JOptionPane.YES_OPTION) {
//      return true;
//    } else {
//      return false;
//    }
//  }

// v3.2
//  /**
//   * @effects display an error message dialog attached to this.gui whose content
//   *          is the combination of <tt>mesg</tt> end the cause of <tt>e</tt>
//   *          (if specified)
//   */
//  public void displayError(InfoCode code, String mesg, Throwable e,
//      Object... data) {
//    displayError(gui, code, mesg, e, false, data);
//  }

//v3.2
//  /**
//   * @requires confirm != null
//   * @effects display an error message dialog attached to this.gui whose content
//   *          is the combination of <tt>mesg</tt> end the cause of <tt>e</tt>
//   *          (if specified); The dialog contains Yes/No button to ask user's
//   *          confirmation.
//   * 
//   *          <pre>
//   *  If confirm = true
//   *    add to the dialog Yes/No button to ask user's confirmation
//   *    If user choose Yes
//   *      return true
//   *    else
//   *      return false
//   *  else 
//   *   return false
//   * </pre>
//   */
//  public boolean displayError(InfoCode code, String mesg, Throwable e,
//      Boolean confirm, Object... data) {
//    if (confirm != null)
//      return displayError(gui, code, mesg, e, confirm, data);
//    else
//      return displayError(gui, code, mesg, e, false, data);
//  }

//v3.2
//  /**
//   * @effects display an error message dialog attached to user gui of
//   *          <tt>source</tt> whose content is the combination of <tt>mesg</tt>
//   *          end the cause of <tt>e</tt> (if specified).
//   */
//  public void displayError(InfoCode code, DataController source, String mesg,
//      Throwable e, Object... data) {
//    displayError((source != null) ? source.getUserGUI() : gui, code, mesg, e, false,
//        data);
//  }

  /**
   * Use this method <b>ONLY</b> for display error messages from tasks that are
   * running outside the application main window.
   * 
   * @effects display an <b>independent</b> error message dialog whose content
   *          is the cause of <tt>e</tt>.
   * 
   * @version 2.7.4
   */
  public static void displayIndependentError(Throwable ex) {
    boolean modality = true;  // v3.2c: added modality 
    
    if (ex instanceof ApplicationException) {
      ApplicationException e = (ApplicationException) ex;
      displayIndependentError(e, modality);
    } else if (ex instanceof ApplicationRuntimeException) {
      ApplicationRuntimeException e = (ApplicationRuntimeException) ex;
      displayIndependentError(e, modality);
    } else {
      // other types of exception
      // v3.2: displayError(null, MessageCode.ERROR_UNDEFINED, null, ex, false, null);
      displayError(null, modality, MessageCode.ERROR_UNDEFINED, ex, false, null);
    }
  }

  /**
   * Use this method <b>ONLY</b> for display error messages from tasks that are
   * running outside the application main window.
   * 
   * @effects display an <b>independent</b> error message dialog whose content
   *          is the cause of <tt>e</tt>.
   * 
   * @version 
   *  - 2.7.4: created<br>
   *  - 3.2c: added modality & made private
   *  
   */
  private static void displayIndependentError(ApplicationException e, boolean modality) {
    /*v3.2: 
    displayError(null, e.getCode(), 
        // v3.1: e.getMessage(),
        null,
        // v3.1: null,
        e,
        false, null);*/
    
    displayError(null, modality, null, e.getMessage(), e, false, null);
  }

  /**
   * Use this method <b>ONLY</b> for display error messages from tasks that are
   * running outside the application main window.
   * 
   * @effects display an <b>independent</b> error message dialog whose content
   *          is the cause of <tt>e</tt>.
   * 
   * @version 
   * - 2.7.4: created<br>
   * - 3.2c: added modality & made private
   */
  private static void displayIndependentError(ApplicationRuntimeException e, boolean modality) {
    /*v3.2: 
    displayError(null, e.getCode(), 
        // v3.1: e.getMessage(),
        null, 
        // v3.1: null,
        e,
        false, null);
        */
    displayError(null, modality, null, e.getMessage(), e, false, null);
  }

  /**
   * @effects display an error message dialog attached to <tt>this.gui</tt>
   *          whose content is the cause of <tt>e</tt>.
   */
  public void displayError(InfoCode code, Throwable e, Object... data) {
    /*v3.2: 
    displayError(gui, code,
    // v2.7.3: null,
        code.getText(), e, false, data);*/
    displayError(gui, code, e, false, data);
    
  }

  /**
   * @effects display an error message dialog attached to user gui of
   *          <tt>source</tt> whose content is the cause of <tt>e</tt>.
   */
  public void displayError(InfoCode code, DataController source, Throwable e,
      Object... data) {
    /* v3.2: 
    displayError((source != null) ? source.getUserGUI() : gui, code,
    // v2.7.3: null,
        code.getText(), e, false, data);
    */
    displayError((source != null) ? source.getUserGUI() : gui, code, e, false, data);
  }

// v3.2
//  /**
//   * @effects display an error message dialog attached to the user gui of
//   *          <tt>source</tt> whose content is <tt>mesg</tt>.
//   */
//  public void displayError(InfoCode code, DataController source, String mesg,
//      Object... data) {
//    displayError((source != null) ? source.getUserGUI() : gui, code, mesg,
//        null, false, data);
//  }

  /**
   * @effects
   *  invoke {@link #displayError(View, InfoCode, Throwable, boolean, Object...)} using 
   *  <tt>(code, null, code.getText(), t, data)</tt>
   * @version 3.0
   */
  public void displayErrorFromCode(InfoCode code, Throwable t, Object... data) {
    //v3.2: displayError(code, null, code.getText(), t, data);
    displayError(gui, code, t, false, data);
  }
  
  /**
   * @effects
   *  invoke {@link #displayError(InfoCode, DataController, String, Object...) using 
   *  <tt>(code, null, code.getText(), data)</tt>
   * @version 3.0
   */
  public void displayErrorFromCode(InfoCode code, Object... data) {
    // v3.2: displayError(code, null, code.getText(), data);
    displayError(gui, code, null, false, data);
  }

  /**
   * @effects
   *  invoke {@link #displayError(InfoCode, DataController, String, Object...) using 
   *  <tt>(code, source, code.getText(), data)</tt>
   * @version 2.7.3 
   */
  public void displayErrorFromCode(InfoCode code, DataController source, Object... data) {
    // v3.2: displayError(code, source, code.getText(), data);
    displayError(source.getUserGUI(), code, null, false, data);
  }

  /**
   * @requires code != null
   * 
   * @effects 
   *  if confirm is not null
   *    call {@link #displayError(View, InfoCode, Throwable, boolean, Object...)} with 
   *    <tt>gui</tt> and <tt>code.getText</tt> as the message
   *  else 
   *    call {@link #displayError(View, InfoCode, Throwable, boolean, Object...)} with 
   *    <tt>gui</tt>, <tt>confirm=false</tt>, and </tt>code.getText</tt> as the message
   *  @version 3.0
   */
  public boolean displayErrorFromCode(InfoCode code, Throwable t, Boolean confirm, Object... data) {
    /* v3.2:
    if (confirm != null)
      return displayError(gui, code, code.getText(), t, confirm, data);
    else
      return displayError(gui, code, code.getText(), t, false, data);
    */
    if (confirm != null)
      return displayError(gui, code, t, confirm, data);
    else
      return displayError(gui, code, t, false, data);
  }
  
  /**
   * @effects
   *  invoke {@link #displayError(InfoCode, DataController, Throwable, Object...) using 
   *  <tt>(code, source, code.getText(), t, data)</tt>
   * @version 2.7.3 
   */
  public void displayErrorFromCode(InfoCode code, DataController source, Throwable t, Object... data) {
    // v3.2: displayError(code, source, code.getText(), t, data);
    displayError(source.getUserGUI(), code, t, false, data);
  }

//  /**
//   * @effects display an error message dialog attached to the specified
//   *          <tt>gui</tt> whose content is the combination of <tt>mesg</tt> (or
//   *          an alternative message suitable for the current application
//   *          language) and the cause of <tt>e</tt> (if specified) and
//   *          <tt>data</tt>.
//   * @deprecated as of v3.2 (to be removed)
//   */
//  private void displayError(View gui, InfoCode code, String mesg,
//      Throwable e, Object... data) {
//    displayError(gui, code, mesg, e, false, data);
//  }

  /**
   * @requires 
   * code != null
   * 
   * @effects display an error message dialog attached to the specified
   *          <tt>gui</tt> whose content is formatted from <tt>code</tt>, 
   *          the cause of <tt>e</tt> (if specified) and
   *          <tt>data</tt>.
   * 
   *          <pre>
   *  If confirm = true
   *    add to the dialog Yes/No button to ask user's confirmation
   *    If user choose Yes
   *      return true
   *    else
   *      return false
   *  else 
   *   return false
   * </pre>
   * 
   * @version 3.2: support MessageFormat embedded in InfoCode 
   */
  private static boolean displayError(View gui, InfoCode code, Throwable e, boolean confirm, Object... data) {
    return displayError(gui, code, 
        null, // message 
        e, confirm, data);
  }
  
  /**
   * @requires 
   * code != null
   * 
   * @effects display an error message dialog attached to the specified
   *          <tt>gui</tt> whose content is formatted from <tt>code</tt>, 
   *          the cause of <tt>e</tt> (if specified) and
   *          <tt>data</tt>.
   * 
   *          <pre>
   *  If confirm = true
   *    add to the dialog Yes/No button to ask user's confirmation
   *    If user choose Yes
   *      return true
   *    else
   *      return false
   *  else 
   *   return false
   * </pre>
   * 
   * @version 3.2c  
   */
  private static boolean displayError(View gui, boolean modality, InfoCode code, Throwable e, boolean confirm, Object... data) {
    return displayError(gui, 
        modality,
        code, 
        null, // message 
        e, confirm, data);
  }
  
  /**
   * @requires 
   * code != null
   * 
   * @effects display a <b>modal</b> error message dialog attached to 
   *          <tt>this.gui</tt> whose content is formatted from <tt>code</tt>, 
   *          the cause of <tt>e</tt> (if specified) and
   *          <tt>data</tt>.
   * 
   *          <pre>
   *  If confirm = true
   *    add to the dialog Yes/No button to ask user's confirmation
   *    If user choose Yes
   *      return true
   *    else
   *      return false
   *  else 
   *   return false
   * </pre>
   * @version 3.3
   */
  public boolean displayErrorModal(InfoCode code, Throwable e, boolean confirm, Object...data) {
    boolean modality = true;
    return displayError(gui, modality, code, e, confirm, data);
  }

  /**
   * This is the <b>base</b> method used by other display error methods.
   * 
   * @requires 
   *  code = null -> mesg != null 
   * 
   * @effects display an error message dialog attached to the specified
   *          <tt>gui</tt> whose content is formatted <b>either</b> from:<br>
   *          (1) <tt>code</tt>, the cause of <tt>e</tt> (if specified) and <tt>data</tt>
   *          <br>OR
   *          <br>
   *          (2) <tt>mesg</tt> and the cause of <tt>e</tt> (if specified)
   * 
   *          <pre>
   *  If confirm = true
   *    add to the dialog Yes/No button to ask user's confirmation
   *    If user choose Yes
   *      return true
   *    else
   *      return false
   *  else 
   *   return false
   * </pre>
   * 
   * @version 3.2: support MessageFormat embedded in InfoCode 
   */
  private static boolean displayError(View gui, InfoCode code, String mesg, Throwable e, boolean confirm, Object... data) {
    boolean modality = false;
    return displayError(gui, modality, code, mesg, e, confirm, data);
// v3.2c: redirect
//    /**
//     * TODO if application support language use message code to look up title
//     * text and message suitable for the current language combine message with
//     * data to display on the dialog
//     */
//    //String m = (mesg != null) ? mesg : "";
//
//    String m;
//    if (mesg == null) {
//      Format fmt = code.getMessageFormat(); // new MessageFormat(m);
//      m = fmt.format(data);
//    } else {
//      m = mesg;
//    }
//    
//    Component parent = (gui != null) ? gui.getGUI() : null;
//
//    if (errorIcon == null) {
//      String ecFile = "exclamation.png";
//      try {
//        errorIcon =  GUIToolkit.getImageIcon(ecFile, "error");
//      } catch (Exception nfe) {
//        // not found error icon: use default 
//      }
//    }
//    
//    // show message dialog (with confirmation (if needed))
//    boolean userConfirm = JMessageDialog.showDialog(parent, parent, 
//          "Lỗi chương trình", errorIcon, m, e, confirm);
//    
//    if (e != null)
//      e.printStackTrace();
//    
//    return userConfirm;
  }
 
  /**
   * This is the <b>base</b> method used by other display error methods.
   * 
   * @requires 
   *  code = null -> mesg != null 
   * 
   * @effects display an error message dialog attached to the specified
   *          <tt>gui</tt> whose content is formatted <b>either</b> from:<br>
   *          (1) <tt>code</tt>, the cause of <tt>e</tt> (if specified) and <tt>data</tt>
   *          <br>OR
   *          <br>
   *          (2) <tt>mesg</tt> and the cause of <tt>e</tt> (if specified)
   * 
   *          <pre>
   *  If confirm = true
   *    add to the dialog Yes/No button to ask user's confirmation
   *    If user choose Yes
   *      return true
   *    else
   *      return false
   *  else 
   *   return false
   *   
   *   If modality = true
   *     display a modal dialg
   *   else 
   *    display a modaless dialog
   * </pre>
   * 
   * @version 3.2c 
   */
  private static boolean displayError(View gui, boolean modality, InfoCode code, String mesg, Throwable e, boolean confirm, Object... data) {
    /**
     * TODO if application support language use message code to look up title
     * text and message suitable for the current language combine message with
     * data to display on the dialog
     */
    //String m = (mesg != null) ? mesg : "";

    String m;
    if (mesg == null) {
      Format fmt = code.getMessageFormat(); // new MessageFormat(m);
      m = fmt.format(data);
    } else {
      m = mesg;
    }
    
    Component parent = (gui != null) ? gui.getGUI() : null;

    if (errorIcon == null) {
      String ecFile = "exclamation.png";
      try {
        errorIcon =  GUIToolkit.getImageIcon(ecFile, "error");
      } catch (Exception nfe) {
        // not found error icon: use default 
      }
    }
    
    // show message dialog (with confirmation (if needed))
    boolean userConfirm = JMessageDialog.showDialog(parent, parent, 
          "Lỗi chương trình", errorIcon, m, e, confirm, modality);
    
    if (e != null)
      e.printStackTrace();
    
    return userConfirm;
  }
  
// v3.2  
//  /**
//   * @effects display an error message dialog attached to <tt>this.gui</tt>
//   *          whose content is <tt>mesg</tt>.
//   * @deprecated as of v3.2 (instead use {@link #displayErrorFromCode(InfoCode, Object...)})
//   */
//  public void displayError(String mesg, Object... data) {
//    displayError(gui, 
//        null, // code 
//        mesg, null, false, data);
//  }

// v3.2  
//  /**
//   * @effects display an error message dialog attached to the specified
//   *          <tt>gui</tt> whose content is the combination of <tt>mesg</tt> (or
//   *          an alternative message suitable for the current application
//   *          language) and the cause of <tt>e</tt> (if specified) and
//   *          <tt>data</tt>.
//   * 
//   *          <pre>
//   *  If confirm = true
//   *    add to the dialog Yes/No button to ask user's confirmation
//   *    If user choose Yes
//   *      return true
//   *    else
//   *      return false
//   *  else 
//   *   return false
//   * </pre>
//   * 
//   * @deprecated as of v3.2 (instead use {@link #displayError(View, InfoCode, Throwable, boolean, Object...)})
//   */
//  private static// v2.7.4
//  boolean displayError(View gui, InfoCode code, String mesg,
//      Throwable e, boolean confirm, Object... data) {
//    /**
//     * TODO if application support language use message code to look up title
//     * text and message suitable for the current language combine message with
//     * data to display on the dialog
//     */
//    String m = (mesg != null) ? mesg : "";
//
//    Format fmt = new MessageFormat(m);
//    m = fmt.format(data);
//    
//    Component parent = (gui != null) ? gui.getGUI() : null;
//
//    if (errorIcon == null) {
//      String ecFile = "exclamation.png";
//      try {
//        errorIcon =  GUIToolkit.getImageIcon(ecFile, "error");
//      } catch (Exception nfe) {
//        // not found error icon: use default 
//      }
//    }
//    
//    // show message dialog (with confirmation (if needed))
//    boolean userConfirm = JMessageDialog.showDialog(parent, parent, 
//          "Lỗi chương trình", errorIcon, m, e, confirm);
//    
//    if (e != null)
//      e.printStackTrace();
//    
//    return userConfirm;
//  }

  /**
   * @requires m != null
   * @effects divide m into an array of consecutive sub-strings based on
   *          {@link #MESSAGE_MAX_SIZE}
   */
  private static// v2.7.4
  String[] breakMessage(String m) {
    /*
     * TODO: to break message more intelligently: - break at word boundaries
     * (option) - take into account next line character
     */
    double len = m.length();

    double ratio = len / MESSAGE_MAX_SIZE;
    int numParts = (int) ratio;
    if (numParts < ratio) {
      numParts = (int) (ratio + 1);
    }

    List<String> array = new ArrayList<String>();
    int charsProcessed = 0, first, last = 0, nextWordBound;
    for (int i = 0; i < numParts && charsProcessed < len; i++) {
      first = charsProcessed; // Math.min(i*MESSAGE_MAX_SIZE, (int)len);
      last = Math.min(charsProcessed + MESSAGE_MAX_SIZE, (int) len); // Math.min((i+1)*MESSAGE_MAX_SIZE,
                                                                     // (int)len);

      // adjust last if it is in the middle of a word
      if (last < len) {
        nextWordBound = m.indexOf(" ", last);
        if (nextWordBound < 0 || nextWordBound > last) {
          // middle of a word
          if (nextWordBound >= 0)
            // to next word bound
            last = nextWordBound;
          else
            // to end of string
            last = (int) len;
        }
      }

      // actual number of chars processed
      charsProcessed += last - first;

      array.add(m.substring(first, last).trim());
    }

    return array.toArray(new String[array.size()]);
  }

  /**
   * @effects write error message to the standard logging device of the system.
   *          The content includes a combination of <tt>err</tt> and the stack
   *          trace of <tt>e</tt> (if specified).
   */
  public void logError(String err, Throwable e) {
    if (err != null)
      System.err.println(err);

    if (e != null) {
      if (debug)
        e.printStackTrace();
      else
        System.err.println(e.getMessage());
    }
  }

  /**
   * @requires 
   *  code != null
   *  
   * @effects 
   *  display a log message formatted from <tt>code</tt> and <tt>data</tt> 
   *  
   * @version 3.1
   */
  public void logFromCode(MessageCode code, Object... data) {
    // v3.2: log(code, code.getText(), data);
    String message = "";
    if (debug || loggingOn) {
      // TODO: convert message based on language
      if (data != null && data.length > 0) {
        Format fmt = code.getMessageFormat(); //new MessageFormat(message);
        message = fmt.format(data);
      }

      System.out.println(message);
    }
  }

  /**
   * This method is used for logging free-form messages (with no pre-defined message codes defined). 
   * 
   * @requires
   *  message != null /\ 
   *  message conforms to the format specified in {@link String#format(String, Object...)} 
   * 
   * @effects 
   *  display a log message formatted from <tt>message</tt> and <tt>data</tt>
   *  
   * @version 
   * - 3.2: remove code parameter
   */
  public void log(String message, Object... data) {
    if (debug || loggingOn) {
      String mesg = "";
      // TODO: convert message based on language
      if (data != null && data.length > 0) {
        //Format fmt = new MessageFormat(message);
        //message = fmt.format(data);
        mesg = String.format(message, data);
      }

      System.out.println(mesg);
    }
  }

  public String toString() {
    return "Controller(" + getName() + ":<"
        + ((cls != null) ? cls.getSimpleName() : "") + ">)";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((module == null) ? 0 : module.hashCode());
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
    ControllerBasic other = (ControllerBasic) obj;
    if (module == null) {
      if (other.module != null)
        return false;
    } else if (!module.equals(other.module))
      return false;
    return true;
  }

  /**
   * @effects informs all <code>DataController</code> registered in
   *          <code>this.dataControllerMap</code> about the deletion of
   *          <code>obj</code> by the controller <code>source</code>.
   * @version 2.6.4b: add support for top-level data controllers managed by
   *          other Controller classes that are bound to the same domain class
   *          as this.
   */
  private void fireObjectDelete(final DataController source, final Oid id,
      final C obj, boolean propagate) {
    // notify other data controllers that are managed by this
    for (DataController c : dataControllerMap.values()) {
      if (c != source) { // ignore the source controller
        try {
          c.causeDelete(id, obj);
        } catch (DataSourceException e) {
          logError("Failed to update-delete on " + c.toString(), e);
        }
      }
    } // end for

    // v2.6.4b: if there are other Controllers that are bound to the same domain
    // class as this
    // perform fireObjectDelete on them as well
    if (propagate) {
      Collection<ControllerBasic> controllers = ControllerBasic.lookUpExtended(
          cls, ControllerLookUpPolicy.All, 
          true, false // v5.4: make precise the options
          );
      for (ControllerBasic ctl : controllers) {
        if (ctl != this) {
          // exclude this controller
          // only inform ctl, dont propage further
          ctl.fireObjectDelete(source, id, obj, false);
        }
      }
    }
  }

  /**
   * This method is invoked after a domain object has been created in the object
   * pool (by a top-level controller) to notify other controllers who manipulate
   * the same sort of objects about the change.
   * 
   * @effects for every <code>DataController</code> in
   *          <code>this.dataControllerMap</code> that is different from the
   *          source controller <code>source</code>, if the
   *          <code>linkAttribute</code> of whose data container has not been
   *          processed, invoke {@link #DataController.causeAdd()} method
   *          passing in <code>obj</code> as the argument, else invoke
   *          {@link #DataController.refreshBuffer()}
   * @see DataController
   * 
   * @version 2.6.4b: add support for top-level data controllers managed by
   *          other Controller classes that are bound to the same domain class
   *          as this.
   */
  private void fireObjectAdd(final DataController source, Oid id, final C obj,
      boolean propagate) {
    for (DataController c : dataControllerMap.values()) {
      if (c != source) {
        try {
          // v3.1:  c.causeAdd(id, obj);
          c.causeAdd(source, id, obj);
        } catch (DataSourceException e) {
          logError("Failed to update-add on " + c.toString(), e);
        }
      }
    } // end for

    if (propagate) {
      // v2.6.4b: if there are other Controllers that are bound to the same
      // domain class as this
      // perform fireObjectAdd on them as well
      Collection<ControllerBasic> controllers = ControllerBasic.lookUpExtended(
          cls, ControllerLookUpPolicy.All, 
          true, false // v5.4: make precise the options
          );
      for (ControllerBasic ctl : controllers) {
        if (ctl != this) {
          // exclude this controller
          // only inform ctl (dont propage further)
          ctl.fireObjectAdd(source, id, obj, false);
        }
      }
    }
  }

  // /**
  // * @effects informs all <code>DataController</code> registered in
  // * <code>this.dataControllerMap</code> about the update of
  // * <code>obj</code> by the controller <code>source</code>
  // */
  // private void fireObjectUpdate(final DataController source, final C obj) {
  // for (DataController c : dataControllerMap.values()) {
  // if (c != source) {
  // c.causeUpdate(obj);
  // }
  // } // end for
  // }

  // /**
  // * This method is used when object is added by a child controller. It
  // * does not cause any changes to the data containers. It merely
  // * requests them to refresh their GUIs.
  // *
  // * @effects
  // * for each data controller registered in
  // * <code>this.dataControllerMap</code> that is not the same as
  // <tt>source</tt>
  // * refresh object buffer
  // *
  // * @deprecated
  // *
  // */
  // private void fireObjectAddFromChild(final DataController source, final C
  // obj) {
  // for (DataController c : dataControllerMap.values()) {
  // if (c != source) {
  // try {
  // c.refreshBuffer(obj);
  // } catch (DBException e) {
  // logError("Failed to refresh " + c.toString(), e);
  // }
  // }
  // } // end for
  // }

  /**
   * Represents the operational state (i.e. the effect of the performance of a
   * relevant class method) of a <code>DataController</code>. The encapsulated
   * state values are defined by the enum {@link AppState}.
   * 
   * <p>
   * The <code>State</code> of a <code>DataController</code> is used together
   * with the state of its <code>objectBuffer</code> to determine the GUI states
   * (enabled or disabled) of the command and menu buttons for the controller's
   * <code>JDataContainer</code>. The rules for determining the GUI states are
   * coded inside the method {@link #getGUIState}
   * 
   * @author dmle
   */
  private static class State {
    // state transition rules
    private static Map<AppState, Collection> restrictives;
    private static Collection reflexives;
    private static Map<AppState, Collection> refreshOnly;

    private AppState currentValue;

    State() {
      if (restrictives == null) {
        initStateTransitionRules();
      }

      currentValue = AppState.Init;
    }

    /**
     * @effects if <code>newValue != currentValue</code> and is suitable then
     *          sets <code>currentValue = newValue</code> and returns
     *          <code>true</code>, else returns <code>false</code>
     */
    boolean setValue(AppState newValue) {

      /**
       * a number of special rules to follow when changing states: <br>
       * 
       * <p>
       * (1) some state transitions (e.g. NewObject --> Created || Cancelled)
       * are restricted in that they accept only certain target states <br>
       * 
       * <p>
       * (2) other transitions (e.g. NewObject --> OnFocus) do not cause a
       * change in state as such, only a refresh of the current state (i.e. to
       * refresh the GUI buttons)
       * 
       * <p>
       * (3) some state transitions may occur reflexively upon a given state
       * (i.e. a state may repeated several times), for example OnFocus,
       * Deleted, next, previous. To reduce performance overhead, we only allow
       * this type of transition to occur for states that result in changes to
       * the data. OnFocus and Deleted are examples of such states. Because Next
       * and Previous operate on the same object buffer and also because of the
       * presence of the First and Last states, we ignore the repetitions of
       * these two states.
       * 
       */

      // rule (1)
      if (currentValue != null) {
        if (restrictives.containsKey(currentValue)) {
          Collection allowed = restrictives.get(currentValue);
          if (!allowed.contains(newValue)) {
            // disallow
            return false;
          }
        }

        // rule (2)
        if (refreshOnly.containsKey(currentValue)) {
          Collection states = refreshOnly.get(currentValue);
          if (states.contains(newValue))
            return true;
        }
      }

      // rule (3) && other allowable transitions
      if (reflexives.contains(newValue) || // rule (3)
          newValue != currentValue) // others
      {
        currentValue = newValue;
        return true;
      }

      return false;
    }

    boolean equals(AppState stateVal) {
      return currentValue == stateVal;
    }

    public boolean equals(Object o) {
      if (o == null)
        return false;

      return currentValue == ((State) o).currentValue;
    }

    @Override
    public String toString() {
      return (currentValue != null) ? currentValue.toString() : null;
    }

    /**
     * @effects initialise pre-defined state transition rules. These rules are
     *          used in {@link #setValue(AppState)} to allow/disallow a state
     *          transition to occur.
     */
    private void initStateTransitionRules() {
      restrictives = new LinkedHashMap();
      reflexives = new ArrayList();
      refreshOnly = new LinkedHashMap();

      Collection col;

      /**
       * pre-defined restrictive rules
       */
      col = new ArrayList();
      col.add(AppState.OnFocus); // see refresh-only below
      col.add(AppState.Created);
      // col.add(AppState.Editing);
      col.add(AppState.Cancelled);
      restrictives.put(AppState.NewObject, col);

      // view-compact -> normal view
      col = new ArrayList();
      col.add(AppState.ViewNormal);
      col.add(AppState.OnFocus); // see refresh-only below
      restrictives.put(AppState.ViewCompact, col);

      // view-normal -> onFocus
      col = new ArrayList();
      col.add(AppState.OnFocus);
      restrictives.put(AppState.ViewNormal, col);

      /**
       * pre-defined reflexive rules
       */
      reflexives.add(AppState.OnFocus);
      reflexives.add(AppState.Deleted);
      reflexives.add(AppState.CurrentObjectChanged);
      reflexives.add(AppState.SearchToolBarUpdated);

      /**
       * pre-defined refresh-only rules
       */
      // new-object -> on-focus
      col = new ArrayList();
      col.add(AppState.OnFocus);
      refreshOnly.put(AppState.NewObject, col);

      // view-compact -> on-focus
      col = new ArrayList();
      col.add(AppState.OnFocus);
      refreshOnly.put(AppState.ViewCompact, col);

      // SearchOn -> editing, on-focus
      // SearchCleared -> editing, on-focus
      col = new ArrayList();
      col.add(AppState.Editing);
      col.add(AppState.OnFocus);
      refreshOnly.put(AppState.SearchToolBarUpdated, col);
      refreshOnly.put(AppState.SearchCleared, col);
    }
  } /** end {@link State} */

  /**
   * @overview
   *  Caches ({@link JOptionPane}, {@link JDialog}) pairs that are created for each {@link View} component. 
   * 
   * @version 3.2c
   *
   * @author dmle
   */
  private static class DialogCache {
    private Map<Component,Tuple2<JOptionPane,JDialog>> cache;
    private DialogCache() {
      cache = new HashMap();
    }
    
    /**
     * @effects 
     *  return {@link JDialog} recorded in <tt>this</tt> that was created for <tt>parent</tt>; or 
     *  <tt>null</tt> if no such object is stored in <tt>this</tt>
     */
    public JDialog getDialog(Component parent) {
      Tuple2<JOptionPane,JDialog> pair = cache.get(parent);
      
      if (pair != null) {
        return pair.getSecond();
      } else {
        return null;
      }
    }
    
    /**
     * @effects 
     *  record in <tt>this</tt> the pair ({@link JOptionPane}, {@link JDialog}) for <tt>parent</tt>
     */
    public void putDialog(Component parent, JOptionPane infoDialogPane,
        JDialog dialog) {
      Tuple2<JOptionPane,JDialog> pair = new Tuple2(infoDialogPane, dialog);
      cache.put(parent, pair);
    }
    
    /**
     * @effects 
     *  return {@link JOptionPane} recorded in <tt>this</tt> that is used to create the {@link JDialog} for <tt>parent</tt>; or 
     *  <tt>null</tt> if no such object is stored in <tt>this</tt>
     */
    public JOptionPane getDialogPane(Component parent) {
      Tuple2<JOptionPane,JDialog> pair = cache.get(parent);
      
      if (pair != null) {
        return pair.getFirst();
      } else {
        return null;
      }
    }
  } /**END: {@link DialogCache} */
  
  /**
   * A helper class that takes care of all the actions related to the
   * manipulation of the domain objects of the given type (<code>C</code>).
   * 
   * <p>
   * A <code>DataController</code> object of a type <code>C</code> is created by
   * and is dependent on the <code>Controller</code> object of that type.
   * 
   * <p>
   * It implements the {@see ActionListener} interface to handle the GUI command
   * buttons of the object actions. These buttons are displayed by the
   * corresponding <code>AppGUI</code> object of the same type.
   * 
   * <p>
   * It also implements the {@see ChangeListener} interface to handle the change
   * events raised by the <code>JDataField</code> components that are bounded to
   * the domain class managed by this object. When an event is raised by a
   * bounded data field, the currently selected domain object on the field is
   * retrieved and this object becomes the current object of this. The active
   * data container is subsequently updated to show the details of this object.
   * 
   * @author dmle
   * 
   * @version 
   * - 3.4c: implements ModuleService
   */
  /** list of logical data actions that are performed by data controllers */
  private static final LAName[] DATA_ACTIONS = { Open, CopyObject, Refresh, Reload, New, Add,
      Create, Update, Reset, Cancel, Delete, First, Previous, Next, Last,
      Search, ClearSearch, CloseSearch, 
      //v3.2: Export, Print, Chart, 
      ViewCompact };

  public static abstract class DataController<C> extends InputHandler implements
      ChangeListener, IndexConsumer // v2.7.2
      , ModuleService // 3.4c
  {

    /**
     * @overview
     *  A helper class used to filter {@link DAssoc}s based on the domain attributes 
     *  specified in a given state scope.
     *  
     * @author dmle
     * @version 3.1
     */
    private static class AssociationStateFilter implements Filter<DAssoc> {
      private String[] stateScope;
      
      public AssociationStateFilter(String[] stateScope) {
        this.stateScope = stateScope;
      }
      @Override
      public boolean check(DAssoc o, Object... args) {
        DAttr attrib = (DAttr) args[0];
        for (String attribName : stateScope) {
          if (attribName.equals(attrib.name())) {
            // in state scope
            return true;
          }
        }
        
        // not in state scope
        return false;
      }
    } // end AssociationStateFilter

    /**
     * @overview
     *  A helper class used to record the association state scope and to act also as a {@link Filter}
     *  to filter associations that are not member of this scope. 
     *  
     * @author dmle
     * @version 3.1
     */
    private static class AssociationStateScope implements Filter<DAssoc> {
      private Map<DAttr,DAssoc> assocScope;
      
      public AssociationStateScope(Map<DAttr,DAssoc> assocScope) {
        this.assocScope = assocScope;
      }
      @Override
      public boolean check(DAssoc o, Object... args) {
        return assocScope.containsValue(o);
      }
    } // end AssociationStateScope
    
    /**the creator {@link ControllerBasic} of this (i.e. that of the module that defines the domain objects manipulated of this)*/
    protected ControllerBasic controller; // creator

    /**the user {@link ControllerBasic} of this (i.e. the view of which contains the data container of this as a sub-container)*/
    protected ControllerBasic user;
    
    protected DataController parent;

    /** the currently selected domain object */
    protected C currentObj;

    /**
     * the data container component that is used to display the domain objects
     * managed by this
     */
    protected JDataContainer dataContainer;

    /**
     * to cache the child data controllers of this (if this is nested)
     * 
     * <br>Note: this is derived from {@link #dataContainer}
     * 
     * @version 3.0
     */
    private List<ControllerBasic.DataController> childControllers;
    
    // /**
    // * a cache of the current objects that are manipulated by this.
    // * <p>
    // * Note: {@link #currentObj} is contained in this.
    // */
    // private Collection<C> objectBuffer;

    /**
     * the object browser (to browse objects)
     */
    private ObjectBrowser<C> browser;

    /** the current search query */
    private Query currentQuery;

    // v2.7.2
    private TaskManager taskMan;
    
    /**
     * A gui update queue (needed only for nested controllers) used to perform gui update and child opener 
     * in sequence
     * 
     * @version  v3.1 
     */
    private RunnableQueue<Task> guiUpdateQueue;
    
    /**
     * the {@see State} object that encapsulates the current state of this
     * controller.
     */
    private State currentState;

    /**
     * a state map of the form {@see Map<{@see GUIAction},{@see Boolean}>}
     * which maps a <code>GUIAction</code> to a <code>Boolean</code> value
     * representing its state (enabled or disabled).
     * Updated by {@link #getGUIStateMap()}.
     */
    private Map<LAName, Boolean> guiStateMap;

    /**
     * records the current states of the browsing buttons, needed to update their states while 
     * GUI update is in progress. This variable only exists for performance purpose.
     * @version 3.2c
     */
    private Map<LAName,Boolean> browsingButtonStatesMap;

    /**
     * A <code>Map</code> between {@link #AppState} and a <code>List</code> of
     * {@see MethodListener} objects interested in the change events raised by
     * this state. <br>
     * We use <code>Map</code> instead of <code>List</code> to reduce
     * performance overhead of firing the change event every time a state is
     * changed. Often, only a small number of states changes are listened to.
     */
    protected Map<AppState, List<MethodListener>> methodListenerMap;

    /**
     * A {@see ChangeEvent} object that carries this object as the source and is
     * passed to all the listeners in {@link #methodListenerMap}. <br>
     * Assumption: one method is invoked at a time (i.e. no concurrency)
     */
    private MethodEvent methodEvent;

    /** the data source wrapper for the objects managed by this */
    private JDataSource dataSource;

    /** the data validator helper */
    private DataValidator dataValidator;

    /** derived from {@link #isNested()} */
    private Boolean isNested;

    /**
     * the controller config of the creator controller (if this is top-level) or
     * of the linked region associated to {@link #dataContainer} (if this is a
     * child)
     **/
    private ControllerConfig controllerCfg;

    /**
     * the model config of the creator controller (if this is top-level) or
     * of the linked region associated to {@link #dataContainer} (if this is a
     * child)
     * @version 3.0
     **/
    private ModelConfig modelCfg;

    /** derived from {@link #controllerCfg} */
    private OpenPolicy openPolicy;

    /** (derived) the default run command (configured in {@link #controllerCfg}) */
    private LAName defCommand;

    /** 
     * Maps name of extended data controller command to the command object. These commands 
     * can be configured through properties of {@link #controllerCfg}.
     * 
     * @version 3.0
     **/
    private Map<String,DataControllerCommand> commandMap;

    /**
     * Actions performable by this (default: {@link ControllerBasic#DATA_ACTIONS})
     * 
     * @version 3.2
     */
    private LAName[] performableActions;
    
    /**
     * Whether or not to check condition for performing a given action when called via {@link #actionPerformed(ActionEvent)}. 
     * The default is <tt>true</tt>
     * which allows check to be performed (which is by {@link #actionPerformedPreConditions(ActionEvent)}).
     * 
     * @version 3.2c 
     */
    private boolean isCheckActionPerformed;
    
    /** derived from {@link #modelCfg} */
    private ObjectComparator comparator;

    /** v2.7.2: derived from  */
    private Boolean indexable;

    /** v2.7.2 */
    private boolean notYetOpened;
    
    /**v3.0: derived attribute from {@link #getAssociationOfParent()} */
    private DAssoc associationOfParent;

    /**
     * Record the {@link DAssoc}s that form the association state scope of {@link #getDomainClass()} 
     * when it participates in the containment tree of the parent. This contains all {@link DAssoc}
     * if this is the top-level.
     * 
     * @version 3.1
     */
    private AssociationStateScope associationStateScope;
    
    /**v3.0: derived attribute from {@link #getLinkAttributeOfParent()} */
    private DAttr linkAttributeOfParent;

    /**v3.0: derived attribute from {@link #isDeterminedByParent()} */
    private Boolean isDeterminedByParent;

    // / constructor methods
    /**
     * @effects initialises <code>this</code> with a user
     *          <code>Controller</code> object, a (possibly <code>null</code>)
     *          <code>parent</code>, and with the outer <code>Controller</code>
     *          object as the <code>creator</code>
     * @requires <code>user != null</code>
     * @param creator
     */
    public DataController(ControllerBasic creator, ControllerBasic user,
        DataController parent) {
      if (creator == null)
        throw new InternalError(
            "DataController.init: creator controller must not be null");

      if (user == null)
        throw new InternalError(
            "DataController.init: user controller must not be null");

      currentState = new State();

      this.controller = creator;

      this.user = user;
      this.parent = parent;
      methodListenerMap = new LinkedHashMap();

      taskMan = new TaskManager();
      initTasks();

      notYetOpened = true; // v2.7.2
      
      // v3.2c: default actions
      performableActions = DATA_ACTIONS;
      
      // v3.2c: default
      isCheckActionPerformed = true;
    }

    // v3.0
//    /**
//     * @requires 
//     *  parent != null -> dataContainer != null (see {@link #setDataContainer(JDataContainer)})
//     *  
//     * @effects 
//     *    initialise controller and model configurations associated to this
//     * 
//     * @version 3.0
//     */
//    private void initCfg() {
//      // if this is a top-level controller then use the controller's configurations
//      // otherwise use the configurations of the linked region associated to dataContainer
//      if (parent == null || this == controller.getRootDataController()) {
//        // top-level Dctl
//        /*v3.0: moved to method
//        ApplicationModule module = controller.getApplicationModule();
//        
//        controllerCfg = module.getControllerCfg();
//        modelCfg = module.getModelCfg();
//        */
//        initRootCfg();
//      } else {
//        // a child controller, look up the RegionLinking specified for its data
//        // container (in the parent)
//        initChildCfg(dataContainer);
//      }
//    }
    
    // v3.0
//    /**
//     * @requires 
//     *    parent != null /\ dataContainer != null (see {@link #setDataContainer(JDataContainer)})
//     *  
//     * @effects 
//     *    initialise controller and model configurations associated to this child data controller 
//     *    based on <tt>dataContainer.containerCfg</tt>
//     * 
//     * @version 3.0
//     */
//    private void initChildCfg(JDataContainer dataContainer) {
//      RegionLinking linkedRegion = (RegionLinking) dataContainer
//          .getContainerConfig();
//      
//      if (linkedRegion == null)
//        throw new ApplicationRuntimeException(null,
//            "Controller.initBrowser: no linked region for child container: "
//                + dataContainer);
//
//      controllerCfg = linkedRegion.getControllerCfg();
//      modelCfg = linkedRegion.getModelCfg();      
//    }
    
    /**
     * @effects 
     *  if this is performing an action (i.e. {@link #actionPerformed(LAName)} is being called)
     *    return true
     *  else
     *    return false  
     * @version 3.3
     */
    public boolean isActionPerforming() {
      // two key actions: create-new and update (delete has no transition state)
      // TODO: add support for other actions if needed 
      return isCreating() || isEditing();
    }

    /**
     * <b>Note:</b> This method </b>MUST NOT</b> be made <tt>public</tt>
     * 
     * @requires 
     *    parent == null
     *  
     * @effects 
     *    initialise controller and model configurations associated to this child data controller 
     *    based on <tt>controller.module</tt>
     * 
     * @version 3.0
     */
    void initRootCfg() {
      ApplicationModule module = controller.getApplicationModule();
      
      controllerCfg = module.getControllerCfg();
      // v3.2c: update properties
      setProperties();
      
      modelCfg = module.getModelCfg();    
    }

    /**
     * <b>Note:</b> This method </b>MUST NOT</b> be made <tt>public</tt>
     * 
     * @requires 
     *    parent != null /\ containerCfg != null
     *  
     * @effects 
     *    initialise controller and model configurations associated to this child data controller 
     *    based on <tt>containerCfg</tt>
     * 
     * @version 
     * - 3.0 <br>
     * - 5.2b: improved to support customised controller-cfg and model-cfg in the containment tree
     *   of the user module
     */
    void initChildCfg(RegionLinking containerCfg) throws ApplicationRuntimeException {
      if (containerCfg == null)
        throw new ApplicationRuntimeException(null,
            "Controller.initChildCfg: no container configuration for: " + this);

      // v5.2b: read custom controller and model configurations
      Tree userContTree = user.getApplicationModule().getContTreeObj();
      Class dcls = getDomainClass();      
      ScopeDef scopeDef = null; 
          //v5.2c ApplicationToolKit.getContainmentScopeDefObject(userContTree, user, parent.getDomainClass(), dcls);
      if (userContTree != null) {
        scopeDef = SwTk.getContainmentScopeDefObject(userContTree, user, parent.getDomainClass(), dcls);
      }
      
      ControllerConfig custControllerCfg = null;
      ModelConfig custModelCfg = null;
      if (scopeDef != null) {
        custControllerCfg = scopeDef.getControllerConfig();
        custModelCfg = scopeDef.getModelConfig();
      }
      
      controllerCfg = containerCfg.getControllerCfg();
      if (custControllerCfg != null) {
        controllerCfg.merge(custControllerCfg);
      }
      // end 5.2b
      
      // v3.2c: update properties
      setProperties();

      modelCfg = containerCfg.getModelCfg();  
      
      // v5.2b
      if (custModelCfg != null) {
        modelCfg.merge(custModelCfg);
      }
    }

    /**
     * @requires 
     *  {@link #controllerCfg} != null
     *  
     * @effects 
     *  update this based on properties (if any) defined in {@link #controllerCfg}
     *  
     * @version 3.2c
     */
    private void setProperties() {
      if (controllerCfg == null)
        return;
      
      // performable actions
      LAName[] actions = controllerCfg.getProperty(PropertyName.controller_dataController_actions, LAName[].class, null);
      
      if (actions != null)
        setPerformableActions(actions);
      
      // is-check-action-performed
      Boolean isCheckAction = controllerCfg.getProperty(PropertyName.controller_dataController_isCheckActionPerformed, Boolean.class, null);
      if (isCheckAction != null) {
        setIsCheckActionPerformed(isCheckAction);
      }
    }

    /**
     * @requires 
     *  dataContainer != null /\ {@link #initCfg()} (see {@link #setDataContainer(JDataContainer)})
     */
    private void initBrowser() {
      // v2.7.2: support the specification of the browser type in the
      // configuration of the linked region
      /*v3.0: moved to initConfig
      if (parent == null || this == controller.rootDctl) {
        // top-level Dctl
        controllerCfg = controller.getApplicationModule().getControllerCfg();
      } else {
        // a child controller, look up the RegionLinking specified for its data
        // container (in the parent)
        RegionLinking linkedRegion = (RegionLinking) dataContainer
            .getContainerConfig();
        if (linkedRegion == null)
          throw new ApplicationRuntimeException(null,
              "Controller.initBrowser: no linked region for child container: "
                  + dataContainer);

        controllerCfg = linkedRegion.getControllerCfg();
      }
       */
      
      Class browserType = controllerCfg.getObjectBrowserCls();

      if (browserType == null) {
        // browser type is not specified,
        // use the default browser type depending on whether this is top-level
        // or child
        if (parent != null) {
          // child controller -> use memory-based browser
          browserType = IdPooledObjectBrowser.class;
        } else {
          // top-level -> use normal browser
          browserType = ObjectBrowser.class;
        }
      }

      browser = ObjectBrowser.createInstance(browserType, controller.getDodm(),getDomainClass());
      
    }

    private void initTasks() {
      // gui update task (it is not known if other tasks are needed  
      // because this.dataContainer has not been initialised at this point)
      Task t = new RunUpdateGUI();
      taskMan.registerTask(t);
    }

    /**
     * @effects return this.taskMan
     * @version 2.7.4
     */
    public TaskManager getTaskManager() {
      return taskMan;
    }

    /**
     * @requires 
     *  taskMan != null
     */
    private RunOpenChildren getTaskOpenChildren() {
      RunOpenChildren openChildren = (RunOpenChildren) taskMan
          .getTask(TaskName.OpenChildren);
      if (openChildren == null) {
        // not yet created this task
        
        boolean defSilent = true;
        openChildren = new RunOpenChildren(defSilent);
        taskMan.registerTask(openChildren);
      } 
      
      return openChildren;
    }
    
    /**
     * @effects 
     *  return controller.dodm
     * @version 3.0
     */
    public DODMBasic getDodm() {
      return controller.getDodm();
    }
    
    /**
     * 
     * @effects 
     *  set {@link #performableActions} = <tt>performableActions</tt>
     * @version 3.2c
     */
    public void setPerformableActions(LAName[] performableActions) {
      this.performableActions = performableActions;
    }

    /**
     * @effects 
     *  set {@link #isCheckActionPerformed} = <tt>isCheckActionPerformed</tt>
     *  
     * @version 3.2c
     */
    public void setIsCheckActionPerformed(boolean isCheckActionPerformed) {
      this.isCheckActionPerformed = isCheckActionPerformed;
    }

    /**
     * This method is used by {@link View} when it creates the action components to decide
     * whether or not to register this {@link DataController} to handle the action events of 
     * those components. 
     * 
     * @requires 
     *  <tt>cmd</tt> matches exactly <tt>name</tt> of one of the {@link LAName) constants
     *  
     * @effects if this can perform command <tt>cmd</tt>, i.e. <tt>cmd</tt> is
     *          an action command supported by
     *          {@link #actionPerformed(ActionEvent)} return true else return
     *          false
     * @version 
     * - 3.2c: support specification of {@link #performableActions}
     */
    public boolean actionPerformable(String cmd) {
      /*v3.2c: use performableActions 
      for (LAName la : DATA_ACTIONS) {
        if (la.name().equals(cmd)) {
          return true;
        }
      }

      return false;
      */
      for (LAName la : performableActions) {
        if (la.name().equals(cmd)) {
          return true;
        }
      }

      return false;
    }

    /**
     * This method works similar to {@link #actionPerformed(ActionEvent)} except
     * that it takes a logical action's name as an argument. This method is
     * invoked programmatically, while the other is invoked in response to a
     * user's GUI action.
     * 
     * @requires command != null
     * @effects execute the function of this controller whose name is
     *          <tt>command.name()</tt>
     */
    private void actionPerformed(LAName command) {
      // execute the specified command
      actionPerformed(command.name());
    }

    /**
     * @effects if the action command of <code>e</code> matches the name one of
     *          the approved object {@see AppGUI.GUIAction}s then handles
     *          <code>e</code>, else displays an error message.
     * 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
      /*v3.2: moved to method to allow override
      // only processes action if the user's GUI is active and the data
      // container
      // of this has got focus
      if (!user.isActive() || !dataContainer.hasFocus()) {
        return;
      }*/
      if (isCheckActionPerformed && !actionPerformedPreConditions(e))
        return;
      
      String cmd = e.getActionCommand();

      actionPerformed(cmd);
    }

    /**
     * @effects
     *  check pre-conditions before performing <tt>e</tt>; 
     *  return <tt>true</tt> if conditions are satisfied; otherwise return <tt>false</tt>
     * @version 3.2 
     */
    protected boolean actionPerformedPreConditions(ActionEvent e) {
      boolean ok = true;
      
      // only processes action if the user's GUI is active and the data
      // container
      // of this has got focus
      if (!user.isActive() || !dataContainer.hasFocus()) {
        ok = false;
      }
      
      return ok;
    }

    /**
     * @effects 
     *  If exists in this {@link DataControllerCommand} whose name is <tt>cmdFullName</tt>
     *    execute it
     *  else
     *    throws NotPossibleException
     *    
     * @version 5.2 
     */
    public void actionPerform(PropertyName cmdFullName) throws NotPossibleException {
      String cmdName = cmdFullName.getLastName();
      DataControllerCommand cmd = lookUpCommand(cmdName);
      
      if (cmd == null) {
        throw new NotPossibleException(NotPossibleException.Code.NO_COMMAND, new Object[] {this, cmdName});
      }
      
      try {
        cmd.execute(this);
      } catch (Exception e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_COMMAND, e, new Object[] {cmd});
      }      
    }
    
    /**
     * @effects 
     *  Look up and perform the data-related action whose name matches <tt>cmd</tt>
     *  Display a suitable message concerning the status
     * @version 
     *  - 3.0: improved to support the use of {@link DataControllerCommand} to extend the range of actions supported <br>
     *  - 3.2: changed to protected
     */
    protected void actionPerformed(String cmd) {
      try {
        
        /* v3.0: support extended range of commands 
         */
        DataControllerCommand dctlCmd = lookUpCommand(cmd);
        if (dctlCmd != null) {
          // extended command found, execute it and exit
          dctlCmd.execute(this);
          return;
        }
      
        // not one of the extended commands, then perpaps one of the default commands
        if (cmd.equals(Open.name())) {
          open();
        } else if (cmd.equals(Refresh.name())) {
          refresh();
        } else if (cmd.equals(Reload.name())) { // v3.0
          reload();
        } else if (cmd.equals(New.name())) {
          newObject();
        } else if (cmd.equals(Add.name())) { // v2.7.2
          addObject();
        } else if (cmd.equals(Create.name())) {
          createObject();
        } else if (cmd.equals(Update.name())) {
          updateObject();
        } else if (cmd.equals(Reset.name())) {
          reset();
        } else if (cmd.equals(Cancel.name())) {
          cancel(false);
        } else if (cmd.equals(Delete.name())) {
          deleteObject();
        } else if (cmd.equals(First.name())) {
          // v3.0: first(true, true, false);
          boolean forceToIndex = false;
          first(true, true
              // waitToFinish (same as updateCurrentObject)
              //v3.2c: , false
              , true 
              , forceToIndex);
        } else if (cmd.equals(Previous.name())) {
          previous(true, true
              // waitToFinish (same as updateCurrentObject)
              //v3.2c: , false
              , true
              );
        } else if (cmd.equals(Next.name())) {
          // v3.0: next(true, true, false);
          boolean forceToIndex = false;
          next(true, true
              // waitToFinish (same as updateCurrentObject)
              // v3.2c: ,false
              ,true
              ,forceToIndex);
        } else if (cmd.equals(Last.name())) {
          // v3.0: last(true, true, false);
          boolean forceToIndex = false;
          last(true, true
              // waitToFinish (same as updateCurrentObject)
              //v3.2c: , false
              , true
              , forceToIndex);
        } else if (cmd.equals(Search.name())) {
          search();
        } else if (cmd.equals(ClearSearch.name())) {
          clearSearch();
        } else if (cmd.equals(CloseSearch.name())) {
          closeSearch();
        } 
        /*v3.2: moved out
        else if (cmd.equals(Export.name())) {
          // v2.7.2 TODO: exclude this in the controller config
          if (this.getCreator().getClass() != DocumentExportController.class) {
            // exclude the export module itself!
            export();
          }
        } 
        else if (cmd.equals(Print.name())) { // v2.7.2
          print();
        } 
         else if (cmd.equals(Chart.name())) {
          createChart();
        }*/ 
//        else if (cmd.equals(RunController.name())) {  // v3.0
//          runController();
//        }
        // add other actions
        else { 
          // should not happen
          controller.logError(
              "DataController: action is not an object action: " + cmd, null);
        }
      } catch (Exception ex) {  // errors
        controller.displayErrorFromCode(MessageCode.ERROR_HANDLE_COMMAND, this, ex, cmd);
      }
    }

    // /**
    // * This method is to be used only by other methods within this class. It
    // provides
    // * a generic way of performing well-known data manipulation methods
    // (similar to
    // * that of <tt>CompositeController</tt>).
    // *
    // * <p><b>IMPORTANT</b>: DO NOT OVERUSE THIS METHOD. Only use it when it is
    // not
    // * possible to invoke the specified method directly.
    // *
    // * @effects
    // * invoke method whose name is specified by <tt>methodName</tt> and whose
    // * input parameter types are <tt>parameterTypes</tt>, passing
    // * in <tt>data</tt> as arguments.
    // *
    // * <p>Throws NotFoundException if no such method is found;
    // * NotPossibleException if fails to perform the method on the specified
    // object.
    // *
    // */
    // private Object invoke(
    // MethodName methodName, Class[] parameterTypes,
    // Object data) throws NotFoundException, NotPossibleException {
    //
    // Method m = Toolkit.getMethod(this.getClass(),
    // methodName.name(), parameterTypes);
    //
    // Object output = null;
    //
    // try {
    // if (m.getParameterTypes().length == 0)
    // output = m.invoke(this, new Object[0]);
    // else
    // output = m.invoke(this, data);
    //
    // return output;
    // } catch (Exception e) {
    // throw new
    // NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
    // "Không thể thực thi phương thức {0}.{1}({2})",this,methodName.name(),data);
    // }
    // }

    /**
     * @requires 
     *  controllerCfg != null
     *  
     * @effects 
     *  if exists the data controller command whose <b>last name</b> is <tt>cmd</tt>
     *    return it
     *  else
     *    return <tt>null</tt>
     *  @version 3.0    
     */
    public DataControllerCommand lookUpCommand(String cmd) {
      // get (once) all configured data controller commands (if any) of this data controller
      DataControllerCommand cmdObj = null;
      
      if (commandMap == null) {
        commandMap = new HashMap();
        
        // find all data controller commands 
        Map<PropertyName,Object> cmdClsObjs = controllerCfg.getDataControllerCommands();
        if (cmdClsObjs != null) {
          Object cmdClsObj; String cmdName;
          for (Entry<PropertyName,Object> e : cmdClsObjs.entrySet()) {
            cmdName = e.getKey().getLastName(); // the last name element
            cmdClsObj = e.getValue();
            if (cmdClsObj != null && cmdClsObj instanceof Class) {
              Class<? extends DataControllerCommand> cmdCls = (Class) cmdClsObj; 
              cmdObj = DataControllerCommand.createInstance(cmdCls, this);
              
              commandMap.put(cmdName, cmdObj);
            }            
          }
        }
      }
      
      cmdObj = commandMap.get(cmd);
      return cmdObj;
//        // command object not exist or not yet created -> find it
//        Object cmdClsObj = controllerCfg.getDataControllerCommand(cmd);
//        
//        if (cmdClsObj != null && cmdClsObj instanceof Class) {
//          Class<? extends DataControllerCommand> cmdCls = (Class) cmdClsObj; 
//          cmdObj = DataControllerCommand.createInstance(cmdCls, this);
//          
//          commandMap.put(cmd, cmdObj);
//        }
    }

    /**
     * @effects Handle state change event of checkbox-typed data fields
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (!user.isActive() || !dataContainer.hasFocus()) {
        return;
      }

      // handle state change of a check box
      JCheckBox chk = (JCheckBox) e.getItemSelectable();
      String cmd = chk.getActionCommand();
      int state = e.getStateChange();

      // debug
      // System.out.printf("DataCtl.itemStateChanged: selected = %b%n", (state
      // == ItemEvent.SELECTED));

      try {
        if (cmd.equals(ViewCompact.name())) {
          boolean compact = (state == ItemEvent.SELECTED);
          displayGUI(compact);
        }
        // add other check box items here
        else { // should not happen
          controller.logError("DataController: action is not recognisable: "
              + cmd, null);
        }
      } catch (Exception ex) {
        controller.displayErrorFromCode(MessageCode.ERROR_HANDLE_COMMAND, this, ex, cmd);
      }
    }

    // ChangeListener interface
    /**
     * This method is invoked when a JDataField, that is bounded to an attribute
     * whose type is the domain class of this, is changed
     * 
     * @effects update currentObj of this
     */
    public void stateChanged(ChangeEvent e) {
      /*
       * v2.7.2: process state change fired by data fields that are configured
       * to be the event source. These are special data fields (e.g.
       * non-editable or image) whose editing events cannot be handled in the
       * usual way. The listener registration was performed by AppGUI when it
       * was created.
       */

      // the event handling semantics depend on the data field and the actual
      // action that was performed
      // on it. For now, assume only one event, which is the user changed the
      // data value
      // of the data field...

      // fire the Editing event
      setCurrentState(AppState.Editing);
    }

    /**
     * @effects 
     *  initialise this.dataContainer and the resources (e.g. object browser, controller and model configurations) 
     *  that are associated to this container 
     */
    public void setDataContainer(JDataContainer dcont) {
      this.dataContainer = dcont;

      if (dataContainer != null) {
        // v3.0: invoke this before initBrowser (below)
        // v3.0: initCfg();
        
        initBrowser();
      }
    }

    public JDataContainer getDataContainer() {
      return dataContainer;
    }
    
    /**
     * @effects 
     *  if {@link #dataContainer} is not null
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt>
     *
     * @version 3.2
     */
    @Override // ModuleService
    public boolean hasView() {
      return dataContainer != null;
    }

    /**
     * @requires parent != null
     * @effects <pre>
     *  if {@link #dataContainer} != null
     *    return the attribute of the domain class of this controller that
     *          implements the association between it and the parent
     *  else 
     *    return <tt>null</tt>
     *    </pre>
     * @version 
     *  - 3.2: added dataContainer null check
     */
    private DAttr getLinkAttribute() {
      if (dataContainer != null)  // v3.2
        return dataContainer.getLinkAttribute();
      else
        return null;
    }

    /**
     * @requires parent != null
     * @effects return the attribute of the domain class of the parent
     *          controller that implements the association between it and this
     */
    public DAttr getLinkAttributeOfParent() {
      /*v3.0: add derived attribute to improve performance
      DefaultPanel parentPanel = (DefaultPanel) parent.getDataContainer();
      // the domain constraint of thisContainer in the parent panel
      DomainConstraint parentAttribute = parentPanel
          .getComponentConstraint(dataContainer.getGUIComponent());
      return parentAttribute;
      */
      if (linkAttributeOfParent == null) {
        DefaultPanel parentPanel = (DefaultPanel) parent.getDataContainer();
        // the domain constraint of thisContainer in the parent panel
        linkAttributeOfParent = parentPanel
            .getComponentConstraint(dataContainer.getGUIComponent());
      }
      
      return linkAttributeOfParent;
    }

    /**
     * @effects 
     *  if {@link #parent} is not null
     *    return an {@link AssociationStateScope} containing the {@link DAssoc}s that are contained in the association state scope of {@link #getDomainClass()}
     *    when it is in the containment with {@link #parent}
     *  else 
     *    return an {@link AssociationStateScope} containing all {@link DAssoc}s   
     * 
     * @version 3.1
     */
    public AssociationStateScope getAssociationStateScope() {
      if (associationStateScope == null) {
        // return associations in the containment scope
        // two cases: 
        //    (2) containment scope is defined in the containment tree of the root module 
        Tree containmentTree = controller.getApplicationModule().getContTreeObj();
        DSMBasic dsm = controller.getDomainSchema();
        
        Class parentCls = (parent != null) ? getParentDomainClass() : null;
        Class myCls = getDomainClass();
        
        String[] stateScope = null;
        AssociationStateFilter filter = null;
        
        if (containmentTree != null) {
          // Containment tree IS specified

          if (parent == null) {
            // top-level module
            stateScope = SwTk.getRootStateScope(containmentTree);
          } else {
            // a child module
            stateScope = SwTk.getContainmentScope(containmentTree, controller, parentCls, myCls);
          }
          
          if (stateScope != null) {
            // state scope is defined in containment tree -> filter
            filter = new AssociationStateFilter(stateScope);
            Map<DAttr,DAssoc> assocMap = dsm.getAssociations(myCls, filter);
            if (assocMap != null) {
              associationStateScope = new AssociationStateScope(assocMap);
            }
          }
        }
        
        if (stateScope == null) {
          // Containment tree is NOT specified or stateScope is not specified in the tree
          
          if (parent != null) {
            // a child module: containment scope MAYBE defined directly in the domain constraint of the parent's link attribute
            DAttr parentAttrib = getLinkAttributeOfParent();
            stateScope = parentAttrib.filter().attributes();
            if (stateScope.length > 0) {
              // state scope is specified -> filter
              filter = new AssociationStateFilter(stateScope);
              Map<DAttr,DAssoc> assocMap = dsm.getAssociations(myCls, filter);
              if (assocMap != null) {
                associationStateScope = new AssociationStateScope(assocMap);
              }
            } else {
              stateScope = null;
            }
          }
          
          if (stateScope == null) {
            // top-level module or a child module without a state scope: use all associations 
            Map<DAttr,DAssoc> assocMap = dsm.getAssociations(myCls);
            if (assocMap != null) {
              associationStateScope = new AssociationStateScope(assocMap);
            }
          }
        }
      } 
      
      return associationStateScope;
    }

    /**
     * @requires 
     *  parent != null
     *  
     * @effects 
     *  return the {@link DAssoc} of the parent's attribute that realises the association 
     *  to this. 
     *  
     * @version 3.0
     */
    public DAssoc getAssociationOfParent() {
      if (associationOfParent == null) {
        if (parent == null) {
          associationOfParent = null;
        } else {
          DSMBasic dsm = dodm.getDsm();
          DAttr parentAttrib = getLinkAttributeOfParent();
          
          associationOfParent = dsm.getAssociation(parent.getDomainClass(), parentAttrib).getSecond();
        }
      }
      
      return associationOfParent;
    }
    
    /**
     * @requires 
     *  parent != null
     *  
     * @effects
     *  return the name of the association returned by {@link #getAssociationOfParent()}
     */
    public String getParentAssociationName() {
      DAssoc assoc = getAssociationOfParent();
      
      if (assoc != null) 
        return assoc.ascName(); 
      else
        return null;
    }
    
    /**
     * @effects 
     *  if {@link DAssoc} to {@link #parent} is defined and is of the specified <tt>type</tt>
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt>
     *  @version 3.2
     */
    private boolean isAssociationToParent(AssocType type) {
      DAssoc assoc = getAssociationOfParent();
      
      if (assoc != null) {
        return assoc.ascType().equals(type);
      }
      
      return false;
    }
    
    /**
     * @requires parent != null
     * @effects return the size of the browser's buffer of this (i.e the
     *          <b>total</b> number of objects of this that are participating in
     *          the association with the parent's object (whether or not these
     *          objects have been loaded))
     */
    private int getLinkCount() {
      if (isOpened()) {
        if (controller.isSingleton()) {
          // single object
          return 1;
        } else {
          // no of objects is same as number of oids in the browser
          return browser.size();
        }
      } else {
        // no objects
        return -1;
      }
    }

    /**
     * @effects if this is a child controller then returns the current object of
     *          the parent controller of <code>this</code>, else returns
     *          <code>null</code>
     */
    public Object getParentObject() {
      // Query query = getParentObjectQuery();
      // if (query != null) {
      // Expression exp = query.getTerm(0);
      // return exp.getVal();
      // }
      //
      // return null;
      Object parentObj = null;
      if (parent != null) {
        parentObj = parent.getCurrentObject();
      }

      return parentObj;
    }

    /**
     * @requires parent != null /\ parentObj != null
     * 
     * @effects <pre>
     *  let v be the value of the attribute of <tt>parentObj</tt> that is linked to this
     *  return v
     * </pre>
     */
    private Object getLinkedParentObject() {
      Object parentObj = getParentObject();
      DAttr linkedParentAttrib = this.getLinkAttributeOfParent();
      return dodm.getDsm().getAttributeValue(parentObj,
          linkedParentAttrib.name());
    }

    /**
     * @requires parent != null /\ parentObj != null /\ the association
     *           (parent,this) is 1:M /\ the attribute of <tt>parent</tt> that
     *           is linked to this is of type Collection
     * 
     * @effects <pre>
     *  let v be the value of the attribute of <tt>parentObj</tt> that is linked to this
     *  if v is not null AND size(v) > 0
     *    return v
     *  else
     *    return null
     * </pre>
     */
    private Collection getLinkedParentObjectBuffer() {
      Object v = getLinkedParentObject();

      if (v != null) {
        // ASSUME: v is a Collection
        Collection col = (Collection) v;
        if (!col.isEmpty())
          return col;
      }

      return null;
    }

    /**
     * @requires parent != null
     * @effects if this.cls is determined by parent.cls in the 1:1 association
     *          via its <tt>linkAttribute</tt> return true else return false
     */
    private boolean isDeterminedByParent() {
      /*v3.0: improve performance by adding a derived attribute 
      DomainConstraint linkedAttrib = getLinkAttribute();
      Class parentCls = getParentDomainClass();
      Class cls = getDomainClass();
      if (linkedAttrib != null)
        return dodm.getDsm().isDeterminedByAssociate(cls, linkedAttrib,
            parentCls);
      else
        return false;
        */
      if (isDeterminedByParent == null) {
        DAttr linkedAttrib = getLinkAttribute();
        Class parentCls = getParentDomainClass();
        Class cls = getDomainClass();
        if (linkedAttrib != null)
          isDeterminedByParent = dodm.getDsm().isDeterminedByAssociate(cls, linkedAttrib,
              parentCls);
        else
          isDeterminedByParent = false;
      }
      
      return isDeterminedByParent;
    }


    /**
     * @requires 
     *  parent != null
     *  
     * @effects 
     *  return <tt>true</tt> if this should update association link to the parent, 
     *  otherwise return <tt>false</tt>
     * @version 3.0
     */
    public boolean isUpdateLinkToParent() {
      if (parent == null)
        return false;
      
      DAssoc parentAssoc = getAssociationOfParent();
      return parentAssoc.associate().updateLink();
    }
    
    /**
     * @effects return the domain class of the objects managed by this
     */
    public Class getDomainClass() {
      return controller.getDomainClass();
    }

    /**
     * @requires parent != null
     * @effects return domain class of the parent
     */
    private Class getParentDomainClass() {
      return parent.getCreator().getDomainClass();
    }

    /**
     * @effects returns the current object of <code>this</code>
     */
    public C getCurrentObject() {
      return currentObj;
    }

    /**
     * @effects set the current object of <code>this</code> to
     *          <code>object</code> and if <tt>updateGUI=true</tt> then update
     *          the GUI to show the object; any the child containers of this GUI
     *          will be cleared.
     * 
     *          <p>
     *          return the Oid of object if <tt>object != null</tt> and has been
     *          registered in the object pool, otherwise return <tt>null</tt>
     * @version 
     *   - 3.2: improved to support post-setting object task
     */
    public Oid setCurrentObject(C object, boolean updateGUI) {
      currentObj = object;
      if (updateGUI)
        updateGUI();

      if (object != null) {
        Oid id = dodm.getDom().lookUpObjectId(controller.getDomainClass(),// cls,
            currentObj);
        
        /*v3.2: added extension*/
        try {
          onSetCurrentObject(object);
        } catch (Exception e) {
          controller.logError("Could not perform post-current-object task", e);
        }
        
        return id;
      } else {
        return null;
      }
    }

    /**
     * This method is used to perform tasks needed after {@link #currentObj} is changed 
     * either directly by {@link #setCurrentObject(Object, boolean)} or following one of the browsing operations. 
     * 
     * 
     * @effects 
     *   if exists extension {@link DataControllerCommand} for {@link DataControllerCommand#Name#OnSetCurrentObject} 
     *    execute the command
     *    <br>throws Exception if error occurs
     *   else 
     *    do nothing
     *  
     * @version 3.2
     */
    protected void onSetCurrentObject(C object) throws Exception {
      // look up extension command for this and if found then execute
      // (sub-types can override behaviour if needed)
      DataControllerCommand cmd = lookUpCommand(LAName.OnSetCurrentObject.name());
      
      if (cmd != null) {
        cmd.execute(this, object);
      }
    }
    
//    /**
//     * @effects 
//     *  if <tt>object</tt> is not in this.{@link #selectedObjects}
//     *    add <tt>object</tt> to this.{@link #selectedObjects}
//     *  else
//     *    do nothing
//     * @version 3.0
//     */
//    protected void addSelectedObject(C object) {
//      if (!selectedObjects.contains(object)) {
//        selectedObjects.add(object);
//      }
//    }
//
//    /**
//     * @effects 
//     *  clear this.{@link #selectedObjects}; 
//     *  add <tt>object</tt> to this.{@link #selectedObjects}
//     * @version 3.0
//     */
//    protected void setSelectedObject(C object) {
//      if (!selectedObjects.isEmpty())
//        selectedObjects.clear();
//      
//      selectedObjects.add(object);
//    }
//    
//    /**
//     * @effects 
//     *  if <tt>object</tt> is in this.{@link #selectedObjects}
//     *    remove <tt>object</tt> from this.{@link #selectedObjects}
//     *  else
//     *    do nothing
//     * @version 3.0
//     */
//    protected void removeSelectedObject(C object) {
//      if (selectedObjects.contains(object)) {
//        selectedObjects.remove(object);
//      }
//    }

//    /**
//     * @effects
//     *  return {@link #selectedObjects}
//     */
//    public Collection<C> getSelectedObjects() {
//      return selectedObjects.isEmpty() ? null : selectedObjects;
//    }
    
    /**
     * @effects
     *  return the objects currently being selected on this.dataContainer or 
     *  return <tt>null</tt> if no objects are being selected
     */
    public abstract Collection<C> getSelectedObjects();
    
    /**
     * @effects 
     *    <br>display <tt>o</tt> on <tt>this.dataContainer</tt>
     *    <br>display the associated objects of <tt>o</tt> in the child data containers if the open policy allows it
     * @version 
     * - 2.7.4: created <br>
     * - 5.6: changed visibility to public
     */
    public void showObject(C o) {
      OpenPolicy pol = getOpenPolicy();
      
      boolean openChildren = (pol != null && pol.isWithChildren());
      
      // divide into cases: (1) to open children and (2) not to open children
      // case (1) differs in that we will not update the GUI when setting the object
      // because that would cause an unexpected effect of also clearing the child containers
      // when they are opened. In this case, GUI update will be done manually after opening the children
      
      boolean updateGUIWithSetter = (openChildren) ? false : true;
      
      // display o
      Oid id = setCurrentObject(o, updateGUIWithSetter);
      if (id != null && browser != null && browser.isOpened()) {
        browser.move(id, o);
      }
      
      if (openChildren) {
        // update GUI of the root controller, and opening the children 
        updateGUI(true);
      } else {
        // update GUI of the root controller, *clearing* also the children 
        // (same effect as setCurrentObject if updateGUIWithSetter (above) was set to true)
        updateGUI(false);
      }  
    }
    
    /**
     * @requires 
     *  attrib is a valid domain attribute of this.domainCls
     *  
     * @effects <pre>
     *  if <tt>this.dataContainer</tt> contains a data field for <tt>attrib</tt> 
     *    return the <b>display value</b> of <tt>this.currentObj.attrib</tt> as displayed by the data field 
     *  else if <tt>this.currentObj</tt> neq null 
     *    return <tt>this.currentObj.attrib</tt> 
     *  else
     *    return <tt>null</tt>
     *  </pre>
     * @version 
     * - 3.0 <br>
     * - 3.1: support cases (1) where no current obj is set but the target data field has been set with a value 
     *        and (2) when no data field component renders the attribute
     */
    public Object getDataFieldValue(DAttr attrib) {
      /*v3.1 
      if (currentObj != null) {
        if (dataContainer.containsComponentForAttribute(attrib.name()))
          // a data field component is used for the attribute: get the value displayed by the component
          return DataContainerToolkit.getDataFieldValue(dataContainer, attrib);
        else 
          // no data field component is used: simply return the attribute value
          return controller.getDomainSchema().getAttributeValue(getDomainClass(), currentObj, attrib);
      }
      return null;
      */
      if (dataContainer.containsComponentForAttribute(attrib.name())) {
        // a data field component is used for the attribute: get the value displayed by the component
        return DataContainerToolkit.getDataFieldValue(dataContainer, attrib);
      } else if (currentObj != null) { 
        // no data field component is used: simply return the attribute value
        return controller.getDomainSchema().getAttributeValue(getDomainClass(), currentObj, attrib);
      } else {
        return null;
      }
    }
    
    /**
     * A helper method that uses {@link #getDataFieldValue(DAttr)}
     * 
     * @effects <prre>
     *  let attrib = this.dsm.getDomainConstraint(attribName)
     *  return {@link #getDataFieldValue(DAttr)}(attrib)
     *  </pre>
     * @version 3.2
     */
    public Object getDataFieldValue(String attribName) {
      try {
        DAttr attrib = controller.getDomainSchema().getDomainConstraint(getDomainClass(), attribName);
        return getDataFieldValue(attrib);
      } catch (Exception e) {
        return null;
      }
    }
    
    /**
     * @requires <pre> 
     *  attribNames != null /\ values != null /\ attribNames.length &lt;= values.length /\ 
     *    for all a=attribNames[i]. values[i] is assignable to attribute A whose name is a
     *  </pre>
     *  
     * @modifies data fields of the domain attributes whose names are specified by <tt>attribNames</tt>
     * @effects 
     *  change values of data fields of the domain attributes whose names are specified by <tt>attribNames</tt> to values (respectively).
     * @version 4.0
     *
     * @see {@link MethodName#setDataFieldValues}
     */
    public void setDataFieldValues(
        String[] attribNames, Object[] values
        //Map<String,Object> attribValMap
        ) throws NotPossibleException {
      if (attribNames == null || values == null || attribNames.length > values.length)
        throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] {"attribNames="+Arrays.toString(attribNames)+",values="+Arrays.toString(values)});
      
      String attribName; Object value;
      Class domainCls = getDomainClass();
      DSMBasic dsm = controller.getDomainSchema();
      for (int i = 0; i < attribNames.length; i++) {
        attribName = attribNames[i];
        value = values[i];
        try {
          DAttr attrib = dsm.getDomainConstraint(domainCls, attribName);
          dataContainer.setDataFieldValue(attrib, value);
        } catch (Exception ex) {
          // log
          controller.logError(this+".setDataFieldValues: Failed to set data field value(s)", ex);
        }
      }
      
    }

    /**
     * @requires <pre> 
     *  attribNames != null /\ values != null /\ values.length = 1 /\ 
     *    exists a=attribNames[i]. values[i] is assignable to attribute A whose name is a
     *  </pre>
     *  
     * @modifies data field of a domain attribute whose name is specified in <tt>attribName</tt>
     *  and whose data type matches values[0]
     *  
     * @effects 
     *  change value of the data field of the domain attribute whose name is specified in <tt>attribNames</tt> 
     *  and whose data type matches values[0] to value[0].
     * @version 4.0
     *
     * @see {@link MethodName#setDataFieldValue}
     */
    public void setDataFieldValue(
        String[] attribNames, Object[] values
        ) throws NotPossibleException {
      if (attribNames == null || values == null || values.length > 1)
        throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] {"attribNames="+Arrays.toString(attribNames)+",values="+Arrays.toString(values)});
      
      String attribName; 
      Object value = values[0];
      Class valueCls = value.getClass();
      Class domainCls = getDomainClass();
      DSMBasic dsm = controller.getDomainSchema();
      
      for (int i = 0; i < attribNames.length; i++) {
        attribName = attribNames[i];
        try {
          DAttr attrib = dsm.getDomainConstraintIfCompatibleTo(domainCls, attribName, valueCls);
          if (attrib != null) {
            dataContainer.setDataFieldValue(attrib, value);
            // stop at first found attribute
            break;
          }
        } catch (Exception ex) {
          // log
          controller.logError(this+".setDataFieldValue: Failed to set data field value(s)", ex);
        }
      }
      
    }

    // /**
    // * @effects
    // * set objectBuffer = objects
    // * if browser != null
    // * set browser.source = objects
    // *
    // * @deprecated to be removed
    // */
    // void setObjectBuffer(Collection objects) throws DBException {
    // objectBuffer = objects;
    //
    // //TODO: temporary code for backward compatible
    // Collection<Oid> oids = new ArrayList();
    // Oid minId = null, maxId = null;
    // Oid id = null;
    // for (Object o : objects) {
    // id = schema.lookUpObjectId(cls, o);
    // if (minId == null)
    // minId = id;
    //
    // oids.add(id);
    // }
    //
    // maxId = id;
    //
    // browser.open(oids, minId, maxId);
    // }

    /**
     * @effects 
     *  if browser != null
     *    call <tt>browser.clear()</tt>
     */
    void clearBrowser() {
      if (browser != null)  // v3.3: added this case
        browser.clear();
    }

    // /**
    // * USE THIS METHOD ONLY WHEN NECESSARY!
    // *
    // * <br>Use {@link #getObjectBufferIterator()} instead.
    // *
    // * @effects
    // * return this.objectBuffer
    // *
    // * @deprecated to be removed
    // */
    // protected Collection<C> getObjectBuffer() {
    // return objectBuffer;
    // }

    // /**
    // * @requires
    // * objectBuffer != null
    // * @effects
    // * return an <tt>Iterator</tt> of the elements in this.objectBuffer
    // *
    // * @deprecated to be removed
    // */
    // protected Iterator<C> getObjectBufferIterator() {
    // return objectBuffer.iterator();
    // }

    // /**
    // * @requires
    // * objectBuffer != null /\ index is a valid index
    // */
    // C getObject(int index) {
    // return objectBuffer.get(index);
    // }

    // /**
    // * @requires
    // * objectBuffer != null /\ obj != null
    // */
    // void removeFromBuffer(Oid id, C obj) {
    // //objectBuffer.remove(obj);
    // browser.remove(id, obj);
    // }

    // /**
    // * @requires
    // * objectBuffer != null
    // */
    // boolean isInBuffer(Oid id, Object o) {
    // //return objectBuffer.contains(o);
    // return browser.contains(id);
    // }

    // /**
    // * @effects
    // * if there are objects in buffer
    // * return false
    // * else
    // * return true
    // */
    // public boolean isBufferEmpty() {
    // //return (objectBuffer == null || objectBuffer.isEmpty());
    // return !browser.isOpened();
    // }

    /**
     * This methods differs from {@link #isBrowserOpened()} in that it does not
     * rely on just the browser being used. Some modules (e.g. singleton) donot
     * use the browser to open its objects. For these modules, the only
     * indication that it has been opened is that a valid current object has
     * been initialised.
     * 
     * @effects if this has been opened (i.e. one of the <tt>open</tt>
     *          operations has been executed successfully) return true else
     *          return false
     */
    public boolean isOpened() { //
      if (controller.isSingleton())
        return currentObj != null;
      else
        return browser != null && browser.isOpened();
    }

    /**
     * Object metadata is typically loaded by one of the <tt>open</tt> operations.
     * 
     * @effects 
     *  if object metadata has been loaded from data source
     *    return true
     *  else
     *    return false
     * @version 2.7.4
     */
    public boolean isOpenMetadata() {
      Class cls = getDomainClass();
      DOMBasic schema = controller.getDodm().getDom();
      
      return schema.isIdRangeInitialised(cls);
    }
    
    /**
     * @effects if browser is null OR browser contains no entries return true
     *          else return false
     */
    public boolean isEmpty() {
      return browser != null && !browser.isEmpty();
    }

    /**
     * @effects if this.currentObj = null return true else return false
     */
    public boolean isCurrentObjectNull() {
      return currentObj == null;
    }

    /**
     * @effects 
     *  if {@link #dataContainer} is in editing mode, i.e. user has entered data some data field
     *    return true 
     *  else 
     *    return false
     * @version 
     *  - 2.7.2: created <br>
     *  - 3.3: maded public
     */
    public boolean isEditing() {
      // check if this.state is Editing
      return (isCurrentState(AppState.Editing));
    }

    /**
     * @effects if <code>this.currentState</code> is
     *          <code>AppState.NewObject</code> then returns <code>true</code>,
     *          else returns <code>false</code>
     */
    public boolean isCreating() {
      return isState(AppState.NewObject);
    }

    /**
     * @effects if this currently contains search result return true else return
     *          false
     */
    private boolean isSearchResultOn() {
      // TODO: need to use the following state check instead of isSearchOn()
      // but it currently does not work correctly
      // if the user started browsing the result before this is invoked
      // state check: isState(AppState.Searched)
      if (controller.isSearchOn() && isBrowserOpened()) { // objectBuffer !=
                                                          // null) {
        return true;
      } else {
        return false;
      }
    }
    
    /**
     * @effects if browser is opened (i.e. it is initialised to hold a
     *          <b>non-empty</b> set of objects) return true else return false
     */
    public boolean isBrowserOpened() { //
      return browser != null && browser.isOpened();
    }

    /**
     * @effects 
     *  if this.dataContainer is currently having focus
     *    return true
     *  else
     *    return false
     *  @version 2.7.4
     */
    public boolean isActive() {
      return dataContainer != null && dataContainer.hasFocus() && user.isActive();
    }
    
    /**
     * @effects if this is configured to listen to state change event of the
     *          data fields of this.dataContainer return true else return false
     * @version 2.7.4
     */
    public boolean isDataFieldStateListener() {
      // use the controller config of the creator
      ControllerConfig ctlCfg = getCreator().getControllerConfig();
      if (ctlCfg != null)
        return ctlCfg.getIsDataFieldStateListener();
      else
        return false;
    }

    /**
     * This method is used to set up the data container of this with pre-defined
     * data in order to create a new object.
     * 
     * @effects cause the data container of this to display <tt>vals</tt> on the
     *          <b>mutable</b> data fields
     */
    public void setMutableState(Object[] vals) {
      dataContainer.setMutableState(vals);
    }

    /**
     * This method is used to set up the data container of this with pre-defined
     * data in order to create a new object. This method differs from
     * {@link #setMutableState(Object[])} in that the default fields to be
     * updated include not only mutable but also immutable, non-auto fields.
     * 
     * @effects cause the data container of this to display <tt>vals</tt> on
     *          <b>all</b> data fields
     */
    public void setUserSpecifiedState(Object[] vals) {
      dataContainer.setUserSpecifiedState(vals);
    }

    /**
     * @effects if l already registered to <tt>state</tt> in this do nothing
     *          else register l to listen to <tt>state</tt>
     */
    @Override
    public void setMethodListener(AppState state, MethodListener l) {
      List<MethodListener> list = methodListenerMap.get(state);
      if (list == null) {
        list = new ArrayList();
        methodListenerMap.put(state, list);
      }

      if (!list.contains(l))
        list.add(l);
    }

    /**
     * @effects adds <code>l</code> to the <code>List</code> of
     *          <code>ChangeListener</code>s interested in events concerning the
     *          state <code>state</code>
     */
    @Override
    public void addMethodListener(AppState state, MethodListener l) {
      List<MethodListener> list = methodListenerMap.get(state);
      if (list == null) {
        list = new ArrayList();
        methodListenerMap.put(state, list);
      }

      list.add(l);
    }

    @Override
    public void removeMethodListener(AppState state, MethodListener l) {
      List<MethodListener> list = methodListenerMap.get(state);
      if (list != null) {
        /*
         * v2.7.3: use removeAll list.remove(l);
         */
        int index;
        do {
          index = list.indexOf(l);
          if (index > -1)
            list.remove(index);
        } while (index > -1);
      }
    }

    /**
     * @effects if there are listeners of <tt>state</tt> invoke the
     *          <code>methodPerformed</code> methods of each listener, passing
     *          in an <tt>MethodEvent</tt> object whose data is <tt>args</tt>
     *          else do nothing
     * @version 
     * 3.1: changed to <tt>public</tt> so that it can be invoked by data controller commands
     */
    // v3.1: protected 
    public void fireMethodPerformed(AppState state, Object... args) {
      List<MethodListener> list = methodListenerMap.get(state);

      if (list != null && !list.isEmpty()) {
        if (args != null && args.length > 0) { // v2.6.4.b: added this check
          if (args.length > 1)
            methodEvent = new MethodEvent(this, args);
          else
            methodEvent = new MethodEvent(this, args[0]);
        } else {
          methodEvent = new MethodEvent(this, null);
        }

        // TODO: improve this
        // for now: need to check size after each iteration because the list may
        // be changed by a listener
        // removing itself when methodPerformed is invoked on it
        int size = list.size();
        for (int i = 0; i < size; i++) {
          list.get(i).methodPerformed(methodEvent);
          if (size > list.size()) {// the listener removes itself from the list
            size--;
            i--;
          }
        }
      }
    }

    /**
     * @requires state != null
     * @effects if this.currentState is state return true else return false
     */
    boolean isCurrentState(AppState state) {
      return currentState.equals(state);
    }

    /**
     * Changes the state value of this and inform state listeners.
     * 
     * @effects set the state of this to <tt>newStateValue</tt> and inform
     *          listeners of this state.
     */
    public void setCurrentState(AppState newStateValue) {
      setCurrentState(newStateValue, null);
    }

    /**
     * Changes the state value of this and inform state listeners with
     * additional details about the state.
     * 
     * @effects set the state of this to <tt>newStateValue</tt> and inform
     *          listeners of this state with (optional) <tt>mesg</tt> and
     *          (optional) state data <tt>data</tt>.
     */
    public void setCurrentState(AppState newStateValue, String mesg,
        Object... data) {
      boolean changed = currentState.setValue(newStateValue);

      // if the data container of this controller has focus then update its GUI
      // buttons
      if (changed) {

        // v2.6.4.a: move to a separate method
        handleStateChanged();
        // // v2.6.1: add null check
        // if (dataContainer != null && dataContainer.hasFocus()) {
        // // update the enabled states of buttons
        // updateGUIButtons();
        // }
      }

      // fire app state event for state
      if (controller.getProperties().getBooleanValue("show.message.status",
          true)) {
        controller.fireApplicationStateChanged(this, newStateValue, mesg, data);
      }
    }

    /**
     * @effects does not change this.state, only fire the state change event to
     *          inform interested listeners
     */
    public void setCurrentStateSimple(AppState newStateValue, String mesg,
        Object... data) {
      // fire app state event for state
      if (controller.getProperties().getBooleanValue("show.message.status",
          true)) {
        controller.fireApplicationStateChanged(this, newStateValue, mesg, data);
      }
    }

    /**
     * @effects performs tasks when a state change occured
     */
    private void handleStateChanged() {

      if (dataContainer != null && dataContainer.hasFocus()) {
        /*
         * v2.7.2: moved to bottom // update the enabled states of buttons
         * updateGUIButtons();
         */

        /* v2.7.2: only need to update search state when state is OnFocus */
        if (isState(AppState.OnFocus)) {
          // v2.6.4.a: if search on the user gui, update search query (if
          // needed)
          View userGUI = user.getGUI();

          boolean searchOnUserGui = userGUI.getController().isSearchOn();

          if (currentQuery == null && searchOnUserGui) {
            // clears search tool bar
            userGUI.clear(SearchToolBar);
          } else if (currentQuery != null) {
            /*
             * v2.7.2: make search tool bar visible if it is not this is needed
             * if the user accidentally closed this tool bar when viewing the
             * parent container
             */
            if (!searchOnUserGui) {
              userGUI.setVisibleContainer(SearchToolBar, true);
            }

            // show search query
            userGUI.updateSearchToolBarState(new String[] { 
                currentQuery.//toString(false)
                toUserFriendlyString(controller.getAttribNameLabelMap()) 
                });
          }
        } 
        /*v3.1: not used
        // v3.0: support create new while editing
        else if (isState(AppState.Editing) && !user.isSearchOn()) {
          if (isCurrentObjectNull()) {
            // editing while no search on AND no current object available -> call new Object
            // confirm with user first
            boolean confirmed = controller.displayConfirmFromCode(MessageCode.CONFIRM_CREATE_NEW_OBJECT, this);
            if (confirmed)
              newObject();
          }
        }
        */

        // update the enabled states of buttons
        updateGUIButtons();
      }

      /*
       * v2.7.2: wrong place for this code -> moved to bottom of the if block
       * above // update the enabled states of buttons updateGUIButtons();
       */
    }

    /**
     * @requires search tool bar is visible on user GUI of this
     * 
     * @effects if search-all option is selected on the search tool bar
     *          associated to this container return true else return false
     * @version 2.7.2
     */
    private boolean isSearchAll() {
      View userGUI = user.getGUI();
      Object[] state = userGUI.getSearchToolBarState(false);
      for (Object s : state) {
        if (s instanceof Boolean) {
          // search option
          return (Boolean) s;
        }
      }

      return false;
    }

    /**
     * @effects <pre>
     *  if this is nested AND
     *          parent's current object is not null AND 
     *          link attribute to parent is serialiable and not null
     *    return a Query object containing one query expression that binds objects of this  
     *    to the parent's current object
     *  else
     *    return null
     * </pre>
     * @version 
     * - 2.6.4.b: add check for serialiable of link attribute
     * - 3.0: added parameter strict (needed for certain operations)
     */
    protected Query<ObjectExpression> getParentObjectQuery(boolean strict) {
      Expression exp = null;// object expression
      if (parent != null) {
        //final Class parentClass = parent.getCreator().getDomainClass();
        Object parentObj = getParentObject();

        if (parentObj != null) {
          // prepare an object expression between the bounded attribute of the
          // class cls
          // and the object parentObj
          DAttr attribute = getLinkAttribute();

          if (attribute != null
              /*v3.0: only check serialisable if strict=true
              && attribute.serialisable() // v2.6.4.b: added this check
              */
              && (!strict || attribute.serialisable()) 
          ) {
            exp = new ObjectExpression(controller.getDomainClass(), // cls,
                attribute, Op.EQ, parentObj);
          }
        }
      }

      Query query = null;
      if (exp != null) {
        query = new Query(exp);
      }

      return query;
    }

    /**
     * @requires 
     *  attrib is a domain attribute of this.domainClass
     *  
     * @effects 
     *  if exists a child data controller of this that renders the view of attribute <tt>attrib</tt> of this.domainClass
     *    return it
     *  else
     *    return <tt>null</tt>
     * @version 3.0
     */
    public DataController getChildController(DAttr attrib) {
      Collection<DataController> children = getChildControllers();
      if (children != null) {
        for (DataController child : children) {
          if (child.getLinkAttributeOfParent() == attrib) {
            // found it
            return child;
          }
        }
      }
      
      return null;
    }
    
    /**
     * @requires 
     *  attrib is a domain attribute of this.domainClass
     *  
     * @effects 
     *  if exists a child data controller <tt>child</tt> of this that renders the view of attribute <tt>attrib</tt> of <tt>this.domainClass</tt>
     *  OR of an attribute <tt>a</tt> of a sub-type of <tt>this.domainClass</tt> s.t. <tt>equals(a.name(), attrib.name())</tt>
     *  (<tt>a is called the <i>shadow attribute</i>) 
     *    return <tt>child</tt>
     *  else
     *    return <tt>null</tt>
     * @version 
     * - 3.1: created
     */
    public DataController getChildControllerWithShadowSupport(DAttr attrib) {
      Collection<DataController> children = getChildControllers();
      Class domainCls = getDomainClass();
      DAttr parentLinkAttrib;
      if (children != null) {
        for (DataController child : children) {
          parentLinkAttrib = child.getLinkAttributeOfParent();
          if (parentLinkAttrib == attrib) {
            // found it
            return child;
          } else if (domainCls.isAssignableFrom(child.getParentDomainClass()) && 
              parentLinkAttrib.name().equals(attrib.name())
              ) {
            // child.domainClass is a sub-type of this.domainClass AND child has a shadow attribute a
            return child;
          }
        }
      }
      
      // not found
      return null;
    }
    

    /**
     * @requires 
     *  attrib is a domain attribute of this.domainClass
     *  
     * @effects 
     *  if exists a child data controller of this that renders the view of attribute <tt>attrib</tt> of this.domainClass
     *    return it
     *  else
     *    return <tt>null</tt>
     * @version 3.0
     */
    public DataController getChildController(String attribName) {
      try {
        DAttr attrib = getDodm().getDsm().getDomainConstraint(getDomainClass(), attribName);
        return getChildController(attrib);
      } catch (NotFoundException e) {
        // not found
        return null;
      }
    }

    /**
     * @effects if exists child controllers of this return an <tt>Iterator</tt>
     *          of them else return null
     */
    public Iterator<ControllerBasic.DataController> getChildControllersIterator() {
      List<ControllerBasic.DataController> children = getChildControllers();
      if (children != null)
        return children.iterator();
      else
        return null;
    }

    /**
     * @effects if there are child controllers of this.rootDataController that
     *          are configured with an auto-open policy return them as
     *          Collection else return null
     */
    public Iterator<DataController> getChildAutoControllersIterator() {
      Collection<DataController> autoChildren = null;
      Iterator<ControllerBasic.DataController> children = getChildControllersIterator();
      if (children != null) {
        DataController child;
        while (children.hasNext()) {
          child = children.next();
          if (child.getOpenPolicy().isWithAutomatic()// .contains(OpenPolicy.A)
          ) {
            if (autoChildren == null)
              autoChildren = new ArrayList<>();
            autoChildren.add(child);
          }
        }
      }

      return (autoChildren != null) ? autoChildren.iterator() : null;
    }

    /**
     * @effects 
     *  if this is nested /\ exists a descendant {@link DataController} of this whose 
     *  domain class is <tt>domainCls</tt>
     *    return it
     *  else
     *    return null
     *    
     * @version 3.4c
     */
    public DataController getDescendantDataControllerOf(final Class domainCls) {
      return getDescendantDataControllerOf(this, domainCls);
    }
    
    /**
     * Recursive method used by {@link #getDescendantDataControllerOf(Class)}.
     */
    private DataController getDescendantDataControllerOf(final DataController parent, final Class domainCls) {
      DataController desCtl = null;
      Collection<DataController> children = parent.getChildControllers();
      if (children != null) {
        for (DataController child : children) {
          if (child.getDomainClass().equals(domainCls)) {
            // found it
            desCtl = child;
            break;
          } else {
            // child is not it: recursive call into child
            desCtl = getDescendantDataControllerOf(child, domainCls);
            if (desCtl != null) break;
          }
        }
      }
      
      return desCtl;
    }
    
    /**
     * Recursive method used by {@link #getChildDataControllerOf(Class)}.
     */
    private DataController getChildDataControllerOf(final DataController parent, final Class domainCls) {
      DataController desCtl = null;
      Collection<DataController> children = parent.getChildControllers();
      if (children != null) {
        for (DataController child : children) {
          if (child.getDomainClass().equals(domainCls)) {
            // found it
            desCtl = child;
            break;
          } 
        }
      }
      
      return desCtl;
    }
    
    /**
     * @effects if this has child controllers return true else return false
     */
    public boolean isNested() {
      if (isNested == null)
        isNested = (getChildControllers() != null);

      return isNested;
    }
    
    /**
     * @effects 
     *  if this is nested in another container (i.e. {@link #parent} != null)
     *    return true
     *  else
     *    return false
     * @version 3.3c
     */
    public boolean isNestedIn() {
      return (parent != null);
    }

    /**
     * @effects if exists child controllers of this return a <tt>List</tt> of
     *          them else return null
     * 
     * @version 
     *  - 3.0 improve performance by running this once and record the result in an attribute
     */
    public List<ControllerBasic.DataController> getChildControllers() {
      /*v3.0: improved to cache the controllers after retrieved
      List<ControllerBasic.DataController> children = new ArrayList();
 
      // JDataContainer dcont = getDataContainer(DataController.this);
      if (dataContainer instanceof DefaultPanel) {
        DefaultPanel panel = ((DefaultPanel) dataContainer);
        Component[] comps = panel.getComponents(null);

        if (comps != null) {
          Component comp;
          for (Component c : comps) {
            if (c instanceof JScrollPane) {
              comp = ((JScrollPane) c).getViewport().getView();
              // .getComponent(0);
            } else {
              comp = c;
            }

            if (comp instanceof JDataContainer) {
              JDataContainer childCont = (JDataContainer) comp;
              children.add(childCont.getController());
            }
          }
        }
      }

      if (children.isEmpty())
        return null;
      else
        return children;
        */
      if (childControllers == null) {
        childControllers = new ArrayList();
        
        // JDataContainer dcont = getDataContainer(DataController.this);
        if (dataContainer instanceof DefaultPanel) {
          DefaultPanel panel = ((DefaultPanel) dataContainer);
          Component[] comps = panel.getComponents(null);
  
          if (comps != null) {
            Component comp;
            for (Component c : comps) {
              if (c instanceof JScrollPane) {
                comp = ((JScrollPane) c).getViewport().getView();
                // .getComponent(0);
              } else {
                comp = c;
              }
  
              if (comp instanceof JDataContainer) {
                JDataContainer childCont = (JDataContainer) comp;
                childControllers.add(childCont.getController());
              }
            }
          }
        }
  
        if (childControllers.isEmpty())
          childControllers = null;
      }

      
      return childControllers;
    }

    /**
     * Use this method to gain access to other application resources through the
     * <code>Controller</code> object that creates this object.
     * 
     * @effects returns the <code>Controller</code> object that created
     *          <code>this</code> (it is also the object to which
     *          <code>this</code> is embedded).
     */
    public ControllerBasic<C> getCreator() {
      return controller; // Controller.this;
    }

    /**
     * @effects returns the <code>Controller</code> object <code>user</code> of
     *          this.
     */
    public ControllerBasic getUser() {
      return user;
    }

    /**
     * @effects return the <tt>AppGUI</tt> of the user <tt>Controller</tt> of
     *          this
     */
    public View getUserGUI() {
      return getUser().getGUI();
    }

    /**
     * @effects returns the parent <code>DataController</code> of this (if any).
     */
    public ControllerBasic.DataController getParent() {
      return parent;
    }

    /**
     * @effects returns a <code>List</code> of objects of the domain class
     *          <code>this.cls</code> matching the query <code>query</code>.
     */
    protected Collection<C> getObjects(Query query) throws NotFoundException,
        NotPossibleException {
      return dodm.getDom().getObjects(controller.getDomainClass(), // cls,
          query); // Controller.getObjects(cls, query);
    }

    /**
     * @version 2.7.4
     */
    protected boolean preConditionOnNewObject() {
      boolean pre = preCondition(false, false);
      if (pre) {
        // cannot add new if an ancesstor form is also add new
        DataController ancestor = getParent();
        while (ancestor != null) {
          if (ancestor.isState(AppState.NewObject)) {
            controller.displayMessageFromCode(MessageCode.PARENT_IS_BUSY, this,
                //"Bạn không thể thực thi thao tác này khi form {0} đang bận",
                //ControllerBasic.lookUpDomainClassLabel(ancestor
                //    .getDomainClass())
                ancestor.getCreator().getDomainClassLabel()
                );
            return false;
          } else {
            ancestor = ancestor.getParent();
          }
        }
        
        return true;
      } else {
        return false;
      }
    }

    /**
     * @deprecated as of v2.7.4 (use {@link #preConditionOnNewObject()})
     */
    protected boolean preCondition() {
      return preCondition(false, false);
    }

    protected void preConditionStrict() throws NotPossibleException {
      preCondition(true, false);
    }

    protected boolean preCondition(boolean strict, boolean silent)
        throws NotPossibleException {
      if (parent != null) {
        final Class parentClass = parent.getCreator().getDomainClass();
        Object parentObj = getParentObject();

        if (parentObj == null 
            && isUpdateLinkToParent() //v3.0
            ) {
          if (!silent) {
            if (!strict) {
              controller.displayMessageFromCode(MessageCode.PARENT_OBJECT_REQUIRED,
                  this,
                  // v2.7.3: parentClass.getSimpleName()
                  parent.getCreator().getDomainClassLabel());
            } else {
              throw new NotPossibleException(
                  NotPossibleException.Code.NO_PARENT_OBJECT,
                  new Object[] { parent.getCreator().getDomainClassLabel()});
            }
          } else { // silent
            if (strict) {
              // log error
              controller.logError(
                  "Cần một đối tượng "
                      + parent.getCreator().getDomainClassLabel()
                      + ", nhưng không có", null);
            }
          }

          return false;
        }
      }
      return true;
    }

    /**
     * @effects <pre>
     * if openPolicy has not been initialised
     *    if this is the top-level data controller
     *      return the open policy of its controller
     *    else
     *      return the open policy of <tt>RegionLinking</tt> that is specified for the data container
     * 
     * return openPolicy
     * </pre>
     * @version 2.6.4b
     */
    protected OpenPolicy getOpenPolicy() {
      if (openPolicy == null) {
        // if this is a child controller and there is an open policy configured
        // for
        // the associated linking region then use it; otherwise, use the open
        // policy
        // of the controller (if any)
        
        if (controllerCfg != null) {  // v2.7.4: added this check
          openPolicy = controllerCfg.getOpenPolicy();
        }
      }

      return openPolicy;
    }

    /**
     * @effects <pre>
     * if <tt>indexable</tt> has not been initialised
     *    if this is the root data controller
     *      return the <tt>indexable</tt> of the module's model config
     *    else
     *      return the <tt>indexable</tt> of <tt>RegionLinking</tt> that is specified for the data container
     * 
     * return <tt>indexable</tt>
     * </pre>
     * 
     *          </pre>
     * @version 2.7.2
     */
    protected Boolean getIndexable() {
      if (indexable == null) {
        ModelConfig modelCfg;
        if (this == controller.rootDctl || parent == null) {
          // top-level Dctl
          modelCfg = controller.getApplicationModule().getModelCfg();
          if (modelCfg != null)
            indexable = modelCfg.getIndexable();
        } else {
          // a child controller, look up the RegionLinking specified for its
          // data container (in the parent)
          // JDataContainer parentContainer = getParent().getDataContainer();
          // RegionLinking linkedRegion = (RegionLinking)
          // parentContainer.getComponentConfig(dataContainer.getGUIComponent());
          RegionLinking linkedRegion = (RegionLinking) dataContainer
              .getContainerConfig();
          modelCfg = linkedRegion.getModelCfg();

          if (modelCfg != null)
            indexable = modelCfg.getIndexable();
        }
      }

      return indexable;
    }

    /**
     * @effects if indexable setting is specified and is set to true return true
     *          else return false
     */
    protected boolean isIndexable() {
      Boolean ixable = getIndexable();
      if (ixable != null && ixable)
        return true;
      else
        return false;
    }

    /**
     * @effects if {@link #isIndexable()} = true return
     *          {@link ControllerBasic#getDomainClass()} else return null
     */
    @Override
    // IndexConsumer
    public <T extends Indexable> Class<T> getIndexedClass() {
      if (isIndexable()) {
        return (Class<T>) getDomainClass();
      } else {
        return null;
      }
    }

//    /**
//     * This is a special operation used for special controller configurations (e.g. forked=true), 
//     * where the data controller can invoke its creator <tt>Controller</tt>. 
//     * 
//     * <p>Note: <b>DONOT</b> use this method in the normal configurations.
//     * 
//     * @effects 
//     *  invoke {@link #getCreator().run}
//     * @version 3.0
//     */
//    public void runController() {
//      getCreator().run();
//    }
    
    /**
     * @effects 
     *  if <tt>default-data-controller-command</tt> is specified in the <tt>ControllerConfig</tt> of this
     *    return it
     *  else
     *    return <tt>null</tt> 
     *     
     * @version 3.0
     */
    public LAName getDefaultRunCommand() {
      if (defCommand == null) {
        if (controllerCfg != null) {
          defCommand = controllerCfg.getProperty(PropertyName.controller_dataControllerCommand, LAName.class, null);
        }
      }
      
      if (defCommand != null && defCommand != SysConstants.NullCommand) {
        return defCommand;
      } else {
        return null;
      }
    }
    
//    /**
//     * @requires 
//     *  obj != null /\ this is not nested (i.e. top-level)
//     *  
//     * @effects <pre>
//     *  if obj is not fully loaded according to the open policy of this
//     *    load obj accordingling
//     *  
//     *  update this.dataContainer to show obj </pre>
//     */
//    public void openObject(C obj) {
//      OpenPolicy pol = getOpenPolicy();
//
//      boolean silent = true;
//
//      if (// pol.contains(OpenPolicy.O)
//      pol.isWithObject()) {
//        // with objects
//        openAndLoad(silent);
//      } else {
//        // just display the object
//        
//      }
//    }

    /**
     * @effects 
     *  if this is not opened AND object metadata has not been loaded
     *    load the object metadata
     *  else 
     *    do nothing
     *  
     *  <p>throws NotPossibleException if data source is not connected,
     *     DataSourceException if failed to operate on data source 
     * @version 
     *  2.7.3: created
     *  <br>2.8: improved to support memory-based configuration
     */
    public void openMetadata() throws NotPossibleException, DataSourceException {
      if (!isOpened()) {
        DOMBasic dom = dodm.getDom();// getDomainSchema();
        boolean objectSerialised = dodm.isObjectSerialised(); // v2.8
        
        if (!objectSerialised ||  // v2.8
            dom.isConnectedToDataSource()) {
          // valid connection to data source

          Class cls = getDomainClass();
          // load metadata if not done so
          if (!dom.isIdRangeInitialised(cls))
            // v2.8: schema.loadMetadata(cls);
            dom.retrieveMetadata(cls);
        } else {
          throw new NotPossibleException(
              NotPossibleException.Code.DATA_SOURCE_NOT_CONNECTED);
        }
      }
    }

    /**
     * This method works similar to {@link #open()} except that it waits for all
     * background tasks (e.g GUI update, etc.) to finish before return.
     * 
     * @effects open the objects or their Oids ready for use, wait for all
     *          background tasks to complete before return
     * 
     * @version 2.7.2
     * @see {@link MethodName#openAndWait}
     */
    public void openAndWait() throws DataSourceException {
      open();
      taskMan.waitForAll();
    }

    /**
     * @effects 
     *  clear and open this again
     * @version 3.0
     */
    public void reopen() throws DataSourceException {
      clearAll();
      // call this instead of invoking open() to support extended command
      actionPerformed(Open);
    }
    
    /**
     * @effects open the objects or their Oids ready for use.
     * @see {@link MethodName#open}
     */
    public void open() throws DataSourceException {
      final boolean silent = false;
      open(silent);

      notYetOpened = false;
    }

    /**
     * @effects open the objects and/or their Oids ready for use.
     * 
     * @version 2.6.4.b: improved to take into account different opening
     *          settings
     */
    private void open(boolean silent) throws DataSourceException {
      /*
       * some modules (e.g. Configuration) are allowed to keep their objects in
       * memory throughout the application run-time, so this check is used to
       * avoid loading the object again from the data source if the Open command
       * is issued on this module (possibly by other modules or because that it
       * is the default command of the module, e.g. Configuration), sometime
       * after it has successfully been executed
       */
      if (isOpened()) {
        // already opened, return immediately
        // v2.7.1: this is needed by CompositeController
        // fire method performed if there are listeners
        if (methodListenerMap.containsKey(AppState.Opened)) {
          fireMethodPerformed(AppState.Opened, null);
        }
        return;
      }

      OpenPolicy pol = getOpenPolicy();

      if (debug)
        controller.log("%s.open(%b) with policy: %s",
            this, silent, pol);

      // open this controller first
      if (isOpenWithAllObjects()) { 
        openAll(silent);
      } else if (pol.isWithObject()) {
        // with objects
        openAndLoad(silent);
      }
      else {
        // with Oids
        openOid(silent);
      }

      /**
       * open children (if needed): this would cause a cascade of openings NOTE:
       * it appears that the GUI updates caused by this code do not work well if
       * the code is placed here. Thus, it is temporarily moved to method
       * {@link #setCurrentObjectAfterBrowsing()} instead. if (isNested() &&
       * pol.contains(OpenPolicy.C)) {
       * doTaskAfterGUIUpdate(getOpenChildrenRunnable(silent)); }
       */

      // v2.7.4: added support
      postOpen();
    }

    /**
     * @effects perform tasks needed after {@link #open(boolean)}
     * @version 
     *  - 2.7.4
     *  - v3.0: added check for update link to parent
     */
    private void postOpen() {
      /*
       * if this is a child then set object count of parent object to
       * this.getLinkCount
       */
      if (parent != null  
          && isUpdateLinkToParent() //v3.0
          ) {
        Class parentCls = getParentDomainClass();
        Object parentObj = getParentObject();
        DAttr linkParentAttrib = getLinkAttributeOfParent();
        int linkCount = getLinkCount();

        // v3.1: added this check 
        if (linkCount > -1) {
          // debug
          //System.out.printf(this+".postOpen: link count to parent("+parentObj+") = " + linkCount);
          
          DOMBasic dom = controller.getDodm().getDom();
  
          try {
            /* v3.1: return a flag: whether or not to update data container of parent
            dom.setAssociationLinkCount(parentCls, linkParentAttrib, parentObj,
                linkCount);
            */
            boolean updateView =   
                dom.setAssociationLinkCount(parentCls, linkParentAttrib, parentObj,
                linkCount);
            
            if (updateView) { 
              parent.updateGUI(null);
            }
          } catch (Exception e) {
            // log
            // e.printStackTrace();
            if (debug)
              controller.log("[WARNING] %s.postOpen(): %s", this, e.getMessage());
          }
        } // end if
      }
    }

//    /**
//     * Use this method as a way to conditionally update the data container of this. It is typically used by 
//     * methods other than {@link #updateObject()}, which change the state of some individual attributes 
//     * of a domain object as part of its operation. 
//     * 
//     * @effects
//     *  if this.dataContainer contains a data field for <tt>attrib</tt> then 
//     *    call {@link #updateGUI(Boolean)}(<tt>withChildren</tt>) to update the GUI
//     *  
//     *  <p>Parameter <tt>withChildren</tt> must be set following the same rules that are specified by {@link #updateGUI(Boolean)}  
//     * @version 3.1
//     */
//    private void updateGUIIfContainsDataFieldOf(DomainConstraint attrib, 
//        Boolean withChildren) {
//      if (dataContainer != null && dataContainer.containsComponentForAttribute(attrib.name())) {
//        updateGUI(withChildren);
//      }
//    }

    /**
     * @effects initialise connection to the data source and load the necessary
     *          meta-data about <tt>this.cls</tt> which can be used later to
     *          manipulate domain objects
     * @version 
     *  2.8: improved to support memory-based configuration
     */
    private void openOid(boolean silent) throws DataSourceException {
      // if (isSingleton()) {
      // // handles singleton class differently
      // openSingle(silent);
      // return;
      // }
      DOMBasic dom = dodm.getDom();// getDomainSchema();
      
      boolean objectSerialised = dodm.isObjectSerialised(); // v2.8
      
      if (!objectSerialised ||  // v2.8
          dom.isConnectedToDataSource()) {
        // valid connection to data source

        Class cls = getDomainClass();
        // load metadata if not done so
        if (!dom.isIdRangeInitialised(cls))
          // v2.8: schema.loadMetadata(cls);
          dom.retrieveMetadata(cls);

        if (preCondition(false, silent)) {
          // valid pre-condition for open

          Oid minId, maxId;
          if (parent != null) {
            // a child controller
            // initialise the browser with the id-range of the child
            /*
             * v2.6.4b: added support for 1:1 association in which the parent is
             * the determinant Query query = getParentObjectQuery();
             * Collection<Oid> oids = schema.loadObjectOids(cls, query);
             */
            Collection<Oid> oids = null;
            if (isDeterminedByParent()) {
              Object o = getLinkedParentObject();
              if (o != null) {
                oids = new ArrayList<Oid>();
                oids.add(dom.lookUpObjectId(o.getClass(), o));
              }
            } else {
              if (isUpdateLinkToParent()) { // v3.0: added this check
                Query query = getParentObjectQuery(false  // strict
                    );
                if (query != null         // v2.7.2: added this check
                    //|| parent == null
                    )
                  oids = dom.retrieveObjectOids(cls, query);
              } else {
                // load all object oids without caring about the parent
                oids = dom.retrieveObjectOids(cls, null);
              }
            }

            if (oids != null) {
              // oids found
              Iterator<Oid> it = oids.iterator();
              minId = it.next(); // first item
              maxId = minId; // last item
              while (it.hasNext()) {
                maxId = it.next();
              }

              browser.open(oids, minId, maxId);
              onOpen();
              setCurrentState(AppState.Opened);
              //v2.7.4: setCurrentState(AppState.Opened, null, browser.getBrowserStateAsString());
            } else {
              // no oids found
              // clear GUI
              setCurrentObject(null, false);
              clearBrowser();
              clearGUI();

              if (controller.getProperties().getBooleanValue(
                  "show.message.popup", true)) {
                controller.displayMessageFromCode(MessageCode.NO_CHILD_OBJECTS_FOUND,
                    this, controller.getDomainClassLabel(), 
                    //getParentObject()
                    parent.getCreator().getDomainClassLabel()
                    );
              }

              setCurrentState(AppState.Init);
            }
          } else {
            // top-level controller
            // initialise the browser with min-max Ids only
            try {
              minId = dom.getLowestOid(cls);
              maxId = dom.getHighestOid(cls);
              browser.open(minId, maxId);
              onOpen();
              
              setCurrentState(AppState.Opened);
              //v2.7.4: setCurrentState(AppState.Opened, null, browser.getBrowserStateAsString());
              
            } catch (NotFoundException e) {
              // no oids found
              if (controller.getProperties().getBooleanValue(
                  "show.message.popup", true)) {
                controller.displayMessageFromCode(MessageCode.NO_OBJECTS_FOUND, e,
                    this, controller.getDomainClassLabel());
              }
              setCurrentObject(null, false);
              clearBrowser();
              clearGUI();
              setCurrentState(AppState.Init);
            }
          }
        }
      } else {
        // no data source or no connection
        setCurrentState(AppState.Init);
      }

      // fire method performed if there are listeners
      if (methodListenerMap.containsKey(AppState.Opened)) {
        fireMethodPerformed(AppState.Opened, null);
      }
    }

    /**
     * This method is almost the same as {@link #openAndLoad(boolean)} except
     * for the part where the objects are loaded into the browser for display.
     * 
     * @effects Load all domain objects and their oids and pass them to the
     *          browser for display
     * 
     * @version 
     *  2.7.2: created
     *  <br>2.8: improved to support memory-based configuration
     */
    private void openAll(boolean silent) throws NotPossibleException,
        DataSourceException {
      DOMBasic dom = dodm.getDom();
      
      boolean objectSerialised = dodm.isObjectSerialised(); // v2.8
      
      if (!objectSerialised ||  // v2.8
          dom.isConnectedToDataSource()) {
        // valid connection to data source
        Class cls = getDomainClass();

        // TODO: should update the metadata from the domain objects
        if (!dom.isIdRangeInitialised(cls))
          // v2.8: schema.loadMetadata(cls);
          dom.retrieveMetadata(cls);
        
        if (preCondition(false, silent)) {
          // valid pre-condition for open

          /*v3.0: moved to shared method
          Map<Oid, C> objects = null;
          if (parent != null && isDeterminedByParent()) {
            C o = (C) getLinkedParentObject();
            if (o != null) {
              objects = new LinkedHashMap<Oid, C>();
              objects.put(dom.lookUpObjectId(o.getClass(), o), o);
            }
          } else {
            if (isUpdateLinkToParent()) { // v3.0: added this check
              Query query = getParentObjectQuery(false  // strict
                  );
              if (query != null || parent == null) // v2.7.2: added this check
                objects = dom.retrieveObjects(cls, query);
            } else {
              // load all objects without caring about the parent
              objects = dom.retrieveObjects(cls);
            }
          }
          */
          Map<Oid, C> objects = retrieveObjects();
          
          if (objects != null) {
            // this is where this differs from openAndLoad()
            openObjects(objects, false);

            // post processing
            onOpenAll(objects);

            setCurrentState(AppState.Opened);
            //setCurrentState(AppState.Opened, null, browser.getBrowserStateAsString());
          } else {
            // no objects found
            setCurrentObject(null, false);
            clearBrowser();
            clearGUI();

            if (controller.getProperties().getBooleanValue(
                "show.message.popup", true)) {
              if (parent != null) {
                controller.displayMessageFromCode(MessageCode.NO_CHILD_OBJECTS_FOUND,
                    this, controller.getDomainClassLabel(),
                    //getParentObject()
                    parent.getCreator().getDomainClassLabel()
                    );
              } else {
                controller.displayMessageFromCode(MessageCode.NO_OBJECTS_FOUND, this,
                    controller.getDomainClassLabel());
              }
            }

            setCurrentState(AppState.Init);
          }
        }
      } else {
        // no data source or no connection
        setCurrentState(AppState.Init);
      }

      // fire method performed if there are listeners
      if (methodListenerMap.containsKey(AppState.Opened)) {
        fireMethodPerformed(AppState.Opened, null);
      }
    }

    /**
     * @requires 
     *  object metadata has been loaded /\ 
     *  {@link #preCondition(boolean, boolean)} = true
     *   
     * @effects 
     *  retrieve and return (from data source if necessary) domain objects that
     *  fit for open by this AND that satisfies sorting config (if any); or return <tt>null</tt> if no objects are found 
     *  
     * @version 
     * - 3.0 <br>
     * - 3.1: changed to public
     */
    public Map<Oid, C> retrieveObjects() throws NotPossibleException, DataSourceException {
      DOMBasic dom = dodm.getDom();
      
      Class cls = getDomainClass();
        
      Map<Oid, C> objects = null;
      if (parent != null && isDeterminedByParent()) {
        C o = (C) getLinkedParentObject();
        if (o != null) {
          objects = new LinkedHashMap<Oid, C>();
          objects.put(dom.lookUpObjectId(o.getClass(), o), o);
        }
      } else {
        /*v3.0: support non-serialisable parent's and child's domain class
        if (isUpdateLinkToParent()) { // v3.0: added this check
          Query query = getParentObjectQuery(false  // strict
              );
          if (query != null || parent == null) // v2.7.2: added this check
            objects = dom.retrieveObjects(cls, query);
          
        } else {
          // load all objects without caring about the parent
          objects = dom.retrieveObjects(cls);
        }
        */
        
        // suport sorting
        getObjectSortingConfig();
        
        if ((parent != null && !parent.getCreator().isSerialisable()) // parent's domain class is notserialisable
            || !getCreator().isSerialisable() // this.domainclass is not serialisable
            ) {
          /*v3.2: FIXED: if this domain class is serialisable AND no parent query then retrieve from 
           * data source (if neede)
          // either parent's, child's, or both domain classes are not serialisable
          // -> retrieve from pool only
          if (parent == null || isUpdateLinkToParent()) {
            Query query = getParentObjectQuery(false  // strict
                );
            objects = dom.getObjectsMap(cls, query, comparator);
          } else { // parent != null && isUpdateLinkToParent = false
            // load all objects without caring about the parent
            objects = dom.getObjectsMap(cls, null, comparator);
          }
         */
          // either parent's, child's, or both domain classes are not serialisable
          // -> retrieve from pool only
          if (parent == null || isUpdateLinkToParent()) {
            Query query = getParentObjectQuery(false  // strict
                );
            objects = dom.getObjectsMap(cls, query, comparator);
          } else { // parent != null && isUpdateLinkToParent = false
            // load all objects without caring about the parent
            if (getCreator().isSerialisable()) // v3.2: added this case
              objects = dom.retrieveObjects(cls, comparator);
            else
              objects = dom.getObjectsMap(cls, null, comparator);
          }
        } else {
          // both parent's and child's domain classes are serialisable
          if (parent == null || isUpdateLinkToParent()) { // v3.0: added this check
            Query query = getParentObjectQuery(false  // strict
                );
            
            if (parent != null && query == null)
              // should not happen -> internal error
              throw new NotPossibleException(NotPossibleException.Code.NO_PARENT_QUERY_WHEN_REQUIRED, 
                  new Object[] {this, parent});
              
            //v3.0: removed because of the check above
            // if (query != null || parent == null) // v2.7.2: added this check
            
            objects = dom.retrieveObjects(cls, query, comparator);
            
          } else {  // parent != null && isUpdateLinkToParent = false
            // load all objects without caring about the parent
            objects = dom.retrieveObjects(cls, comparator);
          }
        }
      }
      
      return objects;
    }

    /**
     * @effects initialise connection to the data source and <b>load</b> the
     *          domain objects and their Oids directly from the data source.
     * 
     *          Throws DBException if fails to process objects from the data
     *          source
     * 
     * @version 
     *  2.6.4.b created
     *  <br>2.8: improved to support memory-based configuration
     */
    private void openAndLoad(boolean silent) throws DataSourceException {
      DOMBasic dom = dodm.getDom();
      boolean objectSerialised = dodm.isObjectSerialised(); // v2.8
      
      if (!objectSerialised ||  // v2.8
          dom.isConnectedToDataSource()) {
        // valid connection to data source
        Class cls = getDomainClass();

        // TODO: should update the metadata from the domain objects
        if (!dom.isIdRangeInitialised(cls))
          // v2.8: schema.loadMetadata(cls);
          dom.retrieveMetadata(cls);
        
        if (preCondition(false, silent)) {
          // valid pre-condition for open

          /*
           * v2.6.4b: added support for 1:1 association in which the parent is
           * the determinant Query query = getParentObjectQuery();
           * Map<Oid,Object> objects = schema.loadObjects(cls, query);
           */
          /*v3.0: moved to shared method 
          Map<Oid, C> objects = null;
          if (parent != null && isDeterminedByParent()) {
            C o = (C) getLinkedParentObject();
            if (o != null) {
              objects = new LinkedHashMap<>();
              objects.put(dom.lookUpObjectId(o.getClass(), o), o);
            }
          } else {
            if (isUpdateLinkToParent()) { // v3.0: added this check
              Query query = getParentObjectQuery(false  // strict
                  );
              if (query != null || parent == null) // v2.7.2: added this check
                objects = dom.retrieveObjects(cls, query);
            } else {
              // load all objects without caring about the parent
              objects = dom.retrieveObjects(cls);
            }
          }
          */
          Map<Oid,C> objects = retrieveObjects();
          
          if (objects != null) {
            // found objects, prepare the browser
            Oid minId, maxId;
            Collection<Oid> oids = objects.keySet();
            Iterator<Oid> it = oids.iterator();
            // child controller
            minId = it.next(); // first item
            maxId = minId; // last item
            while (it.hasNext()) {
              maxId = it.next();
            }

            browser.open(oids, minId, maxId);

            // post-processing depending on the type of data container:
            // could either load all objects on to the form or load the first
            // object only
            onOpenAndLoad();

            setCurrentState(AppState.Opened);
            //v2.7.4: setCurrentState(AppState.Opened, null, browser.getBrowserStateAsString());
          } else {
            // no objects found
            setCurrentObject(null, false);
            clearBrowser();
            clearGUI();

            if (controller.getProperties().getBooleanValue(
                "show.message.popup", true)) {
              if (parent != null) {
                controller.displayMessageFromCode(MessageCode.NO_CHILD_OBJECTS_FOUND,
                    this, controller.getDomainClassLabel(),
                    //getParentObject()
                    parent.getCreator().getDomainClassLabel()
                    );
              } else {
                controller.displayMessageFromCode(MessageCode.NO_OBJECTS_FOUND, this,
                    controller.getDomainClassLabel());
              }
            }

            setCurrentState(AppState.Init);
          }
        }
      } else {
        // no data source or no connection
        setCurrentState(AppState.Init);
      }

      // fire method performed if there are listeners
      if (methodListenerMap.containsKey(AppState.Opened)) {
        fireMethodPerformed(AppState.Opened, null);
      }
    }

    /**
     * This method works similar to {@link #openOid(boolean)} except that it
     * uses a given collection of Oids as input instead of loading them from the
     * database.
     * 
     * <p>
     * This method is specifically used by {@link SimpleDataController}.
     * 
     * @effects <pre>
     *  if open policy is to open all objects
     *    open browser with both oids and objects 
     *  else
     * open browser with <tt>oids</tt>
     * 
     *          Throws NotFoundException if object is not found; DBException if
     *          fails to load object(s) from data source.
     * 
     *          </pre>
     * 
     *          <p>
     *          This method is specifically used by <tt>ReportController</tt> and
     *          by the object search function.
     */
    public void open(Collection<Oid> oids) throws NotFoundException,
        DataSourceException {
      /*v3.0: moved check to method for sub-types to change configuration (e.g. without regarding to  
       * the openPolicy)      
      OpenPolicy pol = getOpenPolicy();
      if (pol.isWithAllObjects()) {
       *  
       */
      if (isOpenWithAllObjects()) {
        // load objects as well
        
        /* v3.0: support sorting
        Map<Oid, C> objPool = dodm.getDom().retrieveObjects(getDomainClass(), // cls,
            oids);
        */
        getObjectSortingConfig();
        
        Map<Oid, C> objPool = controller.getDodm().getDom().retrieveObjects(getDomainClass(), oids, 
            comparator // v3.0
            );
        
        openObjects(objPool, true);

        // onOpenAndLoad();

        // perform post-processing
        onOpenAll(objPool);
      } else {
        openOid(oids);
      }
      
      //TODO: do we need postOpen() here?
    }

    /**
     * This method works similar to {@link #openOid(boolean)} except that it
     * uses a given collection of Oids as input instead of loading them
     * one-by-one from the data source as the user browses.
     * 
     * <p>
     * Compared to {@link #openObjects(Map, boolean)}, this method results in a
     * a more visually appealing GUI effect for for
     * {@link ObjectTableController} that uses an {@link #openPolicy} which
     * additionally requires loading the objects from the data source. In
     * particular, the table GUI is more responsive as each object is
     * incrementally loaded and shown on the table (as opposed to having to wait
     * for all the objects to be loaded and then displayed)
     * 
     * <p>
     * Therefore, it is recommended to use this method for
     * {@link ObjectTableController}, while using the
     * {@link #openObjects(Map, boolean)} for {@link DataPanelController}.
     * 
     * @effects open browser with <tt>oids</tt> performs post-openning tasks as
     *          required by {@link #openPolicy}
     */
    private void openOid(Collection<Oid> oids) {
      // initialise browser

      Iterator<Oid> it = oids.iterator();
      Oid minId = it.next(); // first item
      Oid maxId = minId; // last item
      while (it.hasNext()) {
        maxId = it.next();
      }

      browser.open(oids, minId, maxId);

      /*
       * v2.6.4b: takes into account open policy onOpen();
       */
      OpenPolicy pol = getOpenPolicy();
      if (// pol.contains(OpenPolicy.O)
      pol.isWithObject()) {
        // with objects
        onOpenAndLoad();
      } else {
        // with Oids
        onOpen();
      }

      setCurrentState(AppState.Opened);
      //v2.7.4: setCurrentState(AppState.Opened, null, browser.getBrowserStateAsString());

      // v2.7.1: fire method performed if there are listeners
      if (methodListenerMap.containsKey(AppState.Opened)) {
        fireMethodPerformed(AppState.Opened, null);
      }
    }

    // /**
    // * @effects
    // * invoke {@link #openObjects(Map, boolean)} with <tt>updateState =
    // true</tt>
    // */
    // private void openObjects(Map<Oid,C> objPool) {
    // openObjects(objPool, true);
    // }

    /**
     * This method is a short-cut for a case performed by
     * {@link #openObjects(Map, boolean)}
     *
     * @effects <pre>
     *    open browser with both oids and objects (with sorting if <tt>openWithSorting=true</tt> AND 
     *    this is configured to support it)
     *    
     *    throws NotFoundException if the oid of an object is not found
     * </pre>
     * 
     * @version 
     *  - 2.7.3 <br>
     *  - 3.0: support sorting
     */
    public void openObjects(final Collection<C> objects, final boolean openWithSorting) throws NotFoundException {
      LinkedHashMap<Oid, C> objPool = new LinkedHashMap<>();

      Oid oid;
      DOMBasic dom = dodm.getDom();
      Class cls = getDomainClass();

      /* v3.0: support sorting
      for (C o : objects) {
        oid = dom.lookUpObjectId(cls, o);
        if (oid == null) {
          // should not happen
          throw new NotFoundException(
              NotFoundException.Code.OBJECT_ID_NOT_FOUND, o, "");
        }
        objPool.put(oid, o);
      }
      */
      // v3.0
      ObjectMapSorter sorter = null;

      if (openWithSorting) {
        getObjectSortingConfig();
        
        if (comparator != null)
          sorter = new ObjectMapSorter(comparator);
      }
    
      for (C o : objects) {
        oid = dom.lookUpObjectId(cls, o);
        if (oid == null) {
          // should not happen
          throw new NotFoundException(
              NotFoundException.Code.OBJECT_ID_NOT_FOUND, o, "");
        }
        
        if (sorter != null)
          sorter.put(oid, o);
        else
          objPool.put(oid, o);
      }

      if (sorter != null)
        sorter.copyTo(objPool);
      
      openObjects(objPool, true);

      // perform post-processing
      onOpenAll(objPool);
      
      // v3.1: added post-open
      postOpen();
    }

//    /**
//     * @effects 
//     *  call {@link #openObjects(Map, boolean)} with <tt>(objPool,true)</tt>
//     * @version 3.0
//     */
//    public void openObjects(Map<Oid, C> objPool) {
//      boolean updateState = true;
//      openObjects(objPool, updateState);
//    }
    
    /**
     * This method works similar to {@link #openOid(Collection)} except that it
     * also makes available to the browser the domain objects that are
     * associated to the ids. The browser operates entirely in memory (over the
     * specified objects) and, as such, never needs to load any thing from the
     * data source.
     * 
     * <p>This method <b>DOES NOT</b> support sorting.
     * 
     * @requires objPool != null /\ size(objPool) > 0 /\ objPool contains
     *           objects belong to this.cls 
     * 
     * @modifies this
     * 
     * @effects causes this.browser to open <tt>objPool</tt>, ready to be
     *          browsed on the data container of this
     * 
     * @note objPool is used by reference (i.e. its content is not copied)
     * 
     * @version 
     *  3.0: TODO this causes a problem for child module in that the objects 
     *            are NOT added to the parent buffer; because the browser will contain these 
     *            objects BEFORE they are checked for addition (performed by one of the <tt>onOpen</tt> operations)
     *            and so when this check is eventually performed (by {@link #onBrowserStateChanged(ObjectBrowser, AppState, Object, boolean)}), 
     *            the objects are reported as being not fresh and 
     *            thus not added to the parent buffer 
     */
    private void openObjects(Map<Oid, C> objPool, boolean updateState) {
      /*v3.0: improved to use normal browser 
      ((PooledObjectBrowser) browser).open(objPool);
      */
      browser.openPool(objPool);


      // moved out of this method:
      // onOpenAndLoad();

      if (updateState) {
        setCurrentState(AppState.Opened);
        //v2.7.4: setCurrentState(AppState.Opened, null, browser.getBrowserStateAsString());

        if (methodListenerMap.containsKey(AppState.Opened)) {
          fireMethodPerformed(AppState.Opened, null);
        }
      }
    }

    /**
     * The default behaviour of this operation is to use a particular open policy setting.
     * Sub-types (e.g. ObjectDataTable) may override this setting to support a wider range of open policies/
     * 
     * @effects 
     *  if this is configured to open with all the objects loaded into the buffer
     *    return <tt>true</tt>
     *  else  
     *    return <tt>false</tt>
     *  @version 3.0 
     */
    public boolean isOpenWithAllObjects() {
      // the default: use the open policy setting.
      // Sub-types (e.g. ObjectDataTable) may override this setting to support other open policies
      OpenPolicy pol = getOpenPolicy();
      return pol.isWithAllObjects();
    }
    
    /**
     * @effects if there are child controllers of this then invoke
     *          <code>open()</code> on them, or throw <code>DataSourceException</code>
     *          if an error occured.
     */
    protected void openChildren(boolean silent) throws DataSourceException {
      if (debug)
        controller.log("%s.openChildren(%b)", this,silent);

      Iterator<DataController> children = getChildControllersIterator();
      if (children != null) {
        DataController child;

        // v2.7.2: turn off status message of child controllers while running
        // this
        Object messageState;
        boolean messageOn = false;

        LAName runCmd;  // v3.0
        
        while (children.hasNext()) {
          child = children.next();

          /* v3.0: support the use of default run command
          messageState = child.getCreator().getProperty("show.message.popup");
          child.getCreator().setProperty("show.message.popup", messageOn);
          child.open(silent);
           */
          runCmd = child.getDefaultRunCommand();
          if (runCmd != null) {
            // run the configured command
            child.actionPerformed(runCmd); //runWithCommand(runCmd);
          } else {
            messageState = child.getCreator().getProperty("show.message.popup");
            child.getCreator().setProperty("show.message.popup", messageOn);
            child.open(silent);
            // reset property after open
            child.getCreator().setProperty("show.message.popup", messageState);
          }
              
          // reset property after open
          /*v3.0: moved to above
          child.getCreator().setProperty("show.message.popup", messageState);
          */
        }
      }
    }

    /**
     * @effects if there are child controllers of this clear the GUI (and those
     *          of their child controllers if any) clear all associated
     *          resources (except the domain objects in the object pool)
     */
    public void clearChildren() {
      List<ControllerBasic.DataController> children = getChildControllers();
      if (children != null) {

        // System.out.printf("%s.refreshChildren()%n",this);

        for (DataController dc : children) {
          // clear the GUI and the object buffer
          dc.clearGUI(true);
          dc.clear(false);

          // recursive to decendants
          dc.clearChildren();
        }
      }
    }

    public void onOpen() {
      // for sub-classes to implement
    }

    // v2.6.4.b
    protected void onOpenAndLoad() throws NotPossibleException,
        NotFoundException {
      // for sub-classes to implement
    }

    // v2.7.2
    /**
     * @effects perform post-processing tasks after openning all objects
     */
    public abstract void onOpenAll(Map<Oid, C> objects)
        throws NotPossibleException, NotFoundException;

    // v2.6.4.a: removed
    // /**
    // * @effects invalidates <code>currentObj</code> and
    // * <code>objectBuffer</code> and reload objects from the database.
    // */
    // protected void reload() throws DBException {
    // // only applies to non-singleton class
    // if (!isSingleton()) {
    // // v2.6.2.c: deep clear because open is not invoked by default
    // //clear(false);
    // clear(true);
    // // v2.6.4.a: use openBuffer
    // //open();
    // openBuffer(false);
    // }
    // }

// v3.2: moved out    
//    /**
//     * @effects if this.currentObj != null print the content of this object
//     * @version 2.7.2
//     */
//    protected void print() throws 
//    // v3.1: IOException, PrinterException 
//      NotPossibleException
//    {
//      Class<DocumentExportController> exportCls = DocumentExportController.class;
//      DocumentExportController exportCtl = ControllerBasic.lookUpByControllerType(exportCls);
//
//      // v2.7.3: DocumentBuilder doc =
//      // exportCtl.getRootDataController().getCurrentObject();
//      DataDocument doc = (DataDocument) exportCtl.getRootDataController()
//          .getCurrentObject();
//
//      if (doc != null) {
//        // InputStream contentStream = doc.getContentStream();
//
//        JTextComponent textComp = exportCtl.getPrintableComponent();
//
//        /* v3.1: use printing library
//        PrintRequestAttributeSet printAttrib = new HashPrintRequestAttributeSet();
//        printAttrib.add(OrientationRequested.PORTRAIT);
//
//        textComp.print(null, // header
//            null, // footer
//            true, // show dialog
//            null, // service (default)
//            printAttrib, // attributes
//            true // interactive
//            );
//        */
//        try {
//          if (textComp instanceof JEditorPane) {
//            // print using a special form specifically designed for editor pane
//            JEditorPane editor = (JEditorPane) textComp;
//            HtmlTextPanePrintForm printForm = HtmlTextPanePrintForm.getInstance(editor);
//            //printForm.setVisible(true);
//
//            // get page format from doc
//            int orientation = doc.getPageOrientation();
//            MediaSizeName mediaSize = doc.getMediaSizeName();
//            PageFormat pgFormat = new PageFormat(); 
//            HtmlTextPanePrintForm.getPageFormat(pgFormat, mediaSize, orientation);
//            //printForm.printf(pgFormat, PrintDestination.File);
//            printForm.printf(pgFormat, PrintDestination.Printer);
//          } else {
//            // print component directly...
//            PrintRequestAttributeSet printAttrib = new HashPrintRequestAttributeSet();
//            printAttrib.add(OrientationRequested.PORTRAIT);
//  
//            textComp.print(null, // header
//                null, // footer
//                true, // show dialog
//                null, // service (default)
//                printAttrib, // attributes
//                true // interactive
//                );
//          }
//        } catch (Exception e) {
//          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PRINT, 
//              new Object[] {controller.getDomainClassLabel()}, e);
//        }
//      }
//    }

// v3.2: moved out    
//    /**
//     * @effects if <code>objectBuffer != null</code> then export the data contained 
//     *          in the <code>dataContainer</code> to a specified format and display it to  
//     *          the user
//     */
//    public void export() throws IOException, NotPossibleException, ConstraintViolationException, NotFoundException, DataSourceException {
//      // v3.0: the report data container whose data to be exported (must be done before exportCtl.init (below)) 
//      JDataContainer activeDataContainer = controller.getMainController().getActiveDataContainer();
//
//      if (activeDataContainer == null)
//        throw new NotPossibleException(NotPossibleException.Code.NO_ACTIVE_DATA_CONTAINER);
//
//      // the export module
//      Class<DocumentExportController> exportCls = DocumentExportController.class;
//      DocumentExportController<DataDocument> exportCtl = ControllerBasic
//          .lookUpByControllerType(exportCls);
//
//      exportCtl.init(); // to ensure that GUI and other resources are initialised
//      
//      // the doc object
//      DataController<DataDocument> exportDctl = exportCtl.getRootDataController();
//      DataDocument doc = exportDctl.getCurrentObject();
//      
//      if (doc == null) {
//        // not yet created
//        doc = exportDctl.createNewObject(true);
//      }
//      
//      // TODO: pass as arguments in createNewObject (above)
//      // set doc properties 
//      // - only do this if data container is not a container of the export module (itself)
//      // TODO: remove this check if Export action can be excluded for child data container of ModulePage/ModuleHtmlPage
//      //    of ModuleExportDocument
//      if (activeDataContainer.getController().getUser() != exportCtl) {
//        doc.setDataContainer(activeDataContainer);
//        ControllerBasic activeController = activeDataContainer.getController().getCreator();
//        String docName = activeController.getName();
//        String docTitle = null;
//        doc.setName(docName);
//        doc.setDocTitle(docTitle);
//        
//        // v3.1: set doc's print settings based on the print config of the target module
//        PropertySet printCfg = activeController.getApplicationModule().getPrintConfig();
//        doc.setPrintConfig(printCfg);
//      }
//      
//      // run export using doc
//      exportCtl.run();
//    }

    /**
     * @requires label != null
     * @effects return the raw text of the label
     */
    public String getLabelText(JLabel label) {
      String text;
      if (label instanceof JHtmlLabel) {
        text = ((JHtmlLabel) label).getTextRaw();
      } else {
        text = label.getText();
      }

      return text;
    }

    //
    // /**
    // * @effects returns a new <code>Phrase</code> whose text is
    // * <code>label.text</code> and whose font is based on
    // * <code>label.font</code>
    // * @requires <code>label != null</code>
    // */
    // protected Phrase getPdfLabel(JLabel label) throws NotPossibleException {
    // String text = null;
    // Color c = null;
    // Font f = null;
    // // for HTML label, remove HTML tags from label text
    // // if (label instanceof JHtmlLabel) {
    // // text = ((JHtmlLabel) label).getTextRaw();
    // // } else {
    // // text = label.getText();
    // // }
    // text = getLabelText(label);
    //
    // c = label.getForeground();
    // f = label.getFont();
    //
    // return getPdfText(text, f, c);
    // }
    //
    // /**
    // * @effects returns a new <code>Phrase</code> whose text is <code>s</code>
    // * and whose font is based on <code>f</code>
    // * @requires <code>s != null && f != null</code>
    // */
    // protected Phrase getPdfText(String s, Font f, Color c)
    // throws NotPossibleException {
    // com.itextpdf.text.Font font = null;
    //
    // if (f != null && c != null)
    // font = getPdfFont(f, c);
    //
    // Phrase p = new Phrase(s, font);
    //
    // return p;
    // }
    //
    // /**
    // * @effects returns a new {@see com.itextpdf.text.Font} object from the
    // * Java's <code>Font</code> <code>f</code> and <code>Color</code>
    // * object <code>color</code>; throws
    // * <code>NotPossibleException</code> if the required font cannot be
    // * created.
    // */
    // protected com.itextpdf.text.Font getPdfFont(Font f, Color color)
    // throws NotPossibleException {
    // String fname = f.getFamily().toLowerCase();
    // final String fontLoc = appConfig.getFontLocation();
    // // TODO: read default font from the database
    // String defname = "arial";
    // String ffile = fontLoc + fname + ".ttf";
    // String deffile = fontLoc + defname + ".ttf";
    //
    // // load the base font and use it to create font
    // // Note: base fonts are automatically cached
    // final String encoding = BaseFont.IDENTITY_H;
    // BaseFont bf = null;
    //
    // try {
    // bf = BaseFont.createFont(ffile, encoding, BaseFont.EMBEDDED);
    // } catch (Exception e) {
    // // base font not found
    // // use default
    // try {
    // bf = BaseFont.createFont(deffile, encoding, BaseFont.EMBEDDED);
    // } catch (Exception e1) {
    // throw new NotPossibleException(
    // NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e1,
    // "Không thể thực hiện phương thức {0}({1})", "createFont", "");
    // }
    // }
    // BaseColor bc = new BaseColor(color.getRed(), color.getGreen(),
    // color.getBlue());
    //
    // // TODO: cache fonts
    // com.itextpdf.text.Font font = new com.itextpdf.text.Font(bf, f.getSize(),
    // f.getStyle(), bc);
    // return font;
    // }

    /**
     * @effects 
     *  call {@link #newObject()} followed by {@link #createObject()}
     *  ; if <tt>silent = true</tt> then all confirm messages are turned off.
     * @version 3.0
     */
    public C createNewObject(boolean silent) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
      Object messageState = null;
      if (silent) {
        messageState = controller.setProperty("show.message.popup", false);
      }
      
      newObject();
      createObject();
      C currObj = getCurrentObject();
      
      // reset message state
      if (silent) {
        controller.setProperty("show.message.popup", messageState);
      }
      
      return currObj;
    }
    
    /**
     * @effects performs necessary processing for <code>this</code> and informs
     *          the selected child controller (if any).
     *          
     *  <p>This does not set {@link #currentObj} to <tt>null</tt>.
     *  
     * @version 
     * - 3.1: support short-cut to createObject 
     * 
     * @see {@link MethodName#newObject}
     */
    public void newObject() 
      // v3.1: added because of createObject() 
        throws ConstraintViolationException, NotPossibleException, NotFoundException {
      if (preConditionOnNewObject()) {
        if (parent != null) {
          /*
           * v2.6: check to make sure that any constraints imposed on the
           * association with the parent container (if any) are not violated
           */
          DAttr attribute = getLinkAttribute();
          Object parentObj = getParentObject();
          try {
            int currentLinkCount = getLinkCount();
            dodm.getDom().validateCardinalityConstraintOnCreate(
                getDomainClass(), // cls,
                attribute, parentObj, currentLinkCount);
          } catch (ConstraintViolationException e) {
            // constraint violated -> disallow
            controller.displayErrorFromCode(
                MessageCode.ERROR_ASSOCIATION_CONSTRAINT_VIOLATED_ON_NEW,
                this,
                // e, 
                //parentObj
                controller.getDomainClassLabel()
                );
            return;
          }
        }

        // v2.7.4: make sure object metadata and is fully loaded
        if (controller.isSerialisable()) {
          try {
            // v3.3: replaced by a more specific method 
            // openNoPopUp();
            openOnNewObject();
          } catch (DataSourceException e) {
            controller.displayErrorFromCode(MessageCode.ERROR_OPEN_FORM, this, e, controller.getDomainClassLabel());
            return;
          }
        }

        // ok to create new object
        /*v3.1: if user is editing a new object then create new object directly without 
             further prompting for data
             */
        // delete any resources associated to any child containers of this
        clearChildren();

        // prepare for new object
        onNewObject();

        setCurrentState(AppState.NewObject);

        if (methodListenerMap.containsKey(AppState.NewObject)) {
          fireMethodPerformed(AppState.NewObject, null);
        }
        
// v3.1: not used        
//        boolean createObjectDirectly = false;
//        if (isCurrentObjectNull() && 
//            (isState(AppState.Editing) || isState(AppState.Opened)) && 
//            !user.isSearchOn()) {
//          // editing while no search on AND no current object available -> call new Object
//          // confirm with user first
//          // NOTE: this short-cut applies ONLY to DefaultPanel form (table form must still prompt first)
//          boolean confirmed = controller.displayConfirmFromCode(MessageCode.CONFIRM_CREATE_NEW_OBJECT, this);
//          if (confirmed) {
//            // create object directly using form data
//            createObjectDirectly = true;
//            
//            try {
//              createObject();
//            } catch (DataSourceException e) {
//              throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
//                  new Object[] {controller.getDomainClassLabel(), ""});
//            }
//          }
//        }
//        
//        if (!createObjectDirectly) {
//          // prompt user to enter data
//
//          // delete any resources associated to any child containers of this
//          clearChildren();
//          
//          onNewObject();
//
//          setCurrentState(AppState.NewObject);
//
//          if (methodListenerMap.containsKey(AppState.NewObject)) {
//            fireMethodPerformed(AppState.NewObject, null);
//          }
//        }
      }
    }

    /**
     * @effects 
     *  Perform {@link #open()} suitable for {@link #newObject()}
     * @version 3.3
     */
    protected void openOnNewObject() throws DataSourceException {
      // turn-off pop-up
      Object popUpOld = controller.setProperty("show.message.popup", false);
      
      boolean executedCmd = doOpenOnNewCommand();
      
      if (!executedCmd) {
        // command was either not specified or not executed successfully
        // for now: same as openNoPopUp():
        try {
          open();
        } catch (DataSourceException e) {
          throw e;
        } finally {
          // reset pop-up
          controller.setProperty("show.message.popup", popUpOld);  
        }
      } else {
        // reset pop-up
        controller.setProperty("show.message.popup", popUpOld);
      }
    }
    
    /**
     * @effects 
     *  perform open command suitable for on-new-object operation, if it is specified in the configuration. 
     *  If such command was specified and executed successfully
     *    return true
     *  else
     *    return false
     *    
     *  <p>throws NotPossibleException if failed to execute.
     *  
     * @version 3.3
     */
    protected boolean doOpenOnNewCommand() throws NotPossibleException {
      DataControllerCommand cmd = lookUpCommand(LAName.OpenOnNew.name());
      
      boolean executed = false;
      if (cmd != null) {
        try {
          cmd.execute(this);
          executed = true;
        } catch (Exception e) {
          if (e instanceof NotPossibleException)
            throw (NotPossibleException) e;
          else 
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_OPEN_OBJECT_FORM, e);
        }
      }      
      
      return executed;
    }
    
    /**
     * @effects 
     *  call {@link #open()} with message pop-up disabled
     */
    private void openNoPopUp() throws DataSourceException 
    {
      Object popUpOld = controller.setProperty("show.message.popup", false);
      try {
        open();
      } catch (DataSourceException e) {
        throw e;
      } finally {
        controller.setProperty("show.message.popup", popUpOld);  
      }
      // v3.1: actionPerformed(Open);
      // v3.3: duplicate of the statement inside finally {} 
      // controller.setProperty("show.message.popup", popUpOld);  
    }

    public void onNewObject() {
      // for sub-classes to implement
    }

    /**
     * This is invoked when the user wanted to add a link from the current
     * object of a sub-container to that of the parent container.
     * 
     * @requires 
     *  parent != null 
     *  
     * @effects if this is the child container /\ currentObj != null add link
     *          from currentObj to parentObj else do nothing
     * 
     * @version 2.7.2
     */
    public void addObject() {
      // v3.0: use method
      if (parent != null && currentObj != null 
          && isUpdateLinkToParent()
          ) {
        // child controller

        Object parentObj = getParentObject();
        // only proceed if there is a parentObj
        if (parentObj != null) {
          DAttr parentAttribute = getLinkAttributeOfParent(); // .name();
          // add link to parent's object
          parent.addAssociationLink(parentAttribute, currentObj);

          // update current object's end of the link
          DAttr linkAttrib = getLinkAttribute();
          addAssociationLink(linkAttrib, parentObj);

          // do other panel-specific tasks necessary
          onAddObject();

          // requests other controllers of the same type to refresh
          // Controller.this.fireObjectAdd(this, id, currentObj, true);

          /*
           * ask the parent controller of this to inform its parents (if any)
           * why? the current object of an ancestor controller may contain
           * derived attribute whose value depends on the state of currentObj;
           * in such case this request causes the attribute's value to be
           * re-computed
           */
          parent.fireObjectChangeByChild();

          parent.updateGUI(null); // parent.updateGUI(false);

          // update state etc.

          AppState state = AppState.Added;

          String mesg = null;
          if (controller.getProperties().getBooleanValue("show.message.popup",
              true)) {
            mesg = controller.displayMessageFromCode(MessageCode.OBJECT_ADDED, this, 
                //currentObj
                controller.getDomainClassLabel()
                );
          }

          setCurrentState(state, mesg);

          // fire the state change event
          if (methodListenerMap.containsKey(state)) {
            fireMethodPerformed(state, currentObj);
          }
        }
      }
      //addObject(currentObj);
    }

//    /**
//     * @requies 
//     *  parent != null /\ obj is an instance of this.domainClass
//     *  
//     * @version 3.0
//     */
//    public void addObject(Object obj) {
//      if (parent != null && obj != null 
//          && isUpdateLinkToParent()
//          ) {
//        // child controller
//        
//        Object parentObj = getParentObject();
//        // only proceed if there is a parentObj
//        if (parentObj != null) {
//          DomainConstraint parentAttribute = getLinkAttributeOfParent(); // .name();
//          // add link to parent's object
//          parent.addAssociationLink(parentAttribute, obj);
//
//          // update current object's end of the link
//          DomainConstraint linkAttrib = getLinkAttribute();
//          addAssociationLink(linkAttrib, parentObj);
//
//          // do other panel-specific tasks necessary
//          onAddObject();
//
//          // requests other controllers of the same type to refresh
//          // Controller.this.fireObjectAdd(this, id, obj, true);
//
//          /*
//           * ask the parent controller of this to inform its parents (if any)
//           * why? the current object of an ancestor controller may contain
//           * derived attribute whose value depends on the state of currentObj;
//           * in such case this request causes the attribute's value to be
//           * re-computed
//           */
//          parent.fireObjectChangeByChild();
//
//          parent.updateGUI(null); // parent.updateGUI(false);
//
//          // update state etc.
//
//          AppState state = AppState.Added;
//
//          String mesg = null;
//          if (controller.getProperties().getBooleanValue("show.message.popup",
//              true)) {
//            mesg = controller.displayMessage(MessageCode.OBJECT_ADDED, this,
//                "Đã thêm dữ liệu {0}", obj);
//          }
//
//          setCurrentState(state, mesg);
//
//          // fire the state change event
//          if (methodListenerMap.containsKey(state)) {
//            fireMethodPerformed(state, obj);
//          }
//        }
//      }
//    }
    
    /**
     * @version 2.7.2
     */
    protected void onAddObject() {
      // to be implemented by sub-types (if needed)
    }

    /** 
     * This is improved from {@link #createObject(Object[])} to include not only the state values
     * but also the domain attributes associated to those values. These are used to perform an overall 
     * data validation on the values. This validation is needed to support complex domain-specific 
     * value constraint rules.
     * 
     * @effects creates a new object of <tt>C</tt> whose state is specified in
     *          the <tt>valsMap</tt>; throws
     *          <tt>ConstraintViolationException</tt> if some data value is
     *          invalid w.r.t the domain constraint; throws
     *          <tt>NotPossibleException</tt> if the object could not be
     *          created; throws <tt>DataSourceException</tt> if an error occured
     * 
     * @modifies sets <tt>this.currentObj</tt> to the new object
     * 
     * @version 
     * - 3.3 <br>
     * - 3.4: added support for DomainWarning <br>
     */
    @Deprecated
    public C createObjectOLD(Map<DAttr, Object> valsMap) throws DataSourceException,
    ConstraintViolationException, NotPossibleException, NotFoundException  {

      // v3.3: use the data validator to validate vals. Unlike
      // the individual value checks (which were performed as part of getUserSpecifiedState()), 
      // this check is performed against a combination of vals
      DataValidator dataValidator = getDataValidatorInstance();
      
      try {
        dataValidator.validateDomainValues(null, valsMap);
      
        return createObject(valsMap.values().toArray());
      } catch (DomainWarning warn) {
        // display warning and proceed
        if (controller.getProperties().getBooleanValue("show.message.popup",true)) {
          controller.displayWarningFromCode(warn.getCode(), this, false);
        }
        
        return createObject(valsMap.values().toArray());
      }
    }
    
    /** 
     * This is improved from {@link #createObject(Object[])} to include not only the state values
     * but also the domain attributes associated to those values. These are used to perform an overall 
     * data validation on the values. This validation is needed to support complex domain-specific 
     * value constraint rules.
     * 
     * @effects creates a new object of <tt>C</tt> whose state is specified in
     *          the <tt>valsMap</tt>; throws
     *          <tt>ConstraintViolationException</tt> if some data value is
     *          invalid w.r.t the domain constraint; throws
     *          <tt>NotPossibleException</tt> if the object could not be
     *          created; throws <tt>DataSourceException</tt> if an error occured
     * 
     * @modifies sets <tt>this.currentObj</tt> to the new object
     * 
     * @version 
     * - 3.3 <br>
     * - 3.4: added support for DomainWarning <br>
     * - 5.0: improved to make use of the attributes in valsMap
     */
    public C createObject(Map<DAttr, Object> valsMap) throws DataSourceException,
    ConstraintViolationException, NotPossibleException, NotFoundException  {

      // v3.3: use the data validator to validate vals. Unlike
      // the individual value checks (which were performed as part of getUserSpecifiedState()), 
      // this check is performed against a combination of vals
      DataValidator dataValidator = getDataValidatorInstance();
      
      try {
        dataValidator.validateDomainValues(null, valsMap);
      
        return createObjectFromValMap(valsMap);
      } catch (DomainWarning warn) {
        // display warning and proceed
        if (controller.getProperties().getBooleanValue("show.message.popup",true)) {
          controller.displayWarningFromCode(warn.getCode(), this, false);
        }
        
        return createObjectFromValMap(valsMap);
      }
    }
    
    /** 
     * This is improved from {@link #createObject(Object[])} to include not only the state values
     * but also the domain attributes associated to those values. These are used to perform an overall 
     * data validation on the values. This validation is needed to support complex domain-specific 
     * value constraint rules.
     * 
     * @effects creates a new object of <tt>C</tt> whose state is specified in
     *          the <tt>valsMap</tt>; throws
     *          <tt>ConstraintViolationException</tt> if some data value is
     *          invalid w.r.t the domain constraint; throws
     *          <tt>NotPossibleException</tt> if the object could not be
     *          created; throws <tt>DataSourceException</tt> if an error occured
     * 
     * @modifies sets <tt>this.currentObj</tt> to the new object
     * 
     * @version 5.0
     */
    private C createObjectFromValMap(Map<DAttr, Object> valsMap) throws DataSourceException,
    ConstraintViolationException, NotPossibleException, NotFoundException  {
      DODMBasic dodm = controller.getDodm();
	    DOMBasic dom = dodm.getDom();
	    Class cls = getDomainClass();
	    
	    // create object in the object pool
	    // This is the only line that separates this method from the method createObject(Object[])
	    Tuple2<Oid, Object> t = dom.createObject(getDomainClass(), valsMap);
	    
	    currentObj = (C) t.getSecond();
	    Oid id = t.getFirst();
	
	    // v2.5.4: if there are other domain-type attributes of the new object
	    // then we need to add the new object to the values of the corresponding
	    // domain objects
	    // as well
	    DAttr linkAttribute = getLinkAttribute();
	    /*
	     * v2.6.4.b: changed to invoke a DataController method so that we can do
	     * other things schema.updateAssociatesOnCreate(currentObj,
	     * linkAttribute);
	     */
	    // add linkes to associates other than the parent object (this will be
	    // added later below)
	    addLinkToAssociates(currentObj, linkAttribute);
	
	    /**
	     * if parent is present then add object to the object buffer
	     */
	    if (parent != null) {
	      // child controller
	      /*
	       * v2.7.3: replaced by addAssociationLinkToParent String parentAttribute
	       * = getLinkAttributeOfParent().name(); // add object to parent's buffer
	       * parent.updateAssociationLink(parentAttribute, currentObj);
	       */
	      if (isUpdateLinkToParent()) { // v3.0: added this check
	        /* v3.2: added support for update to link end of this
	        addAssociationLinkToParent(id, currentObj);
	        */
	        boolean myLinkEndUpdated = addAssociationLinkToParent(id, currentObj);
	        if (myLinkEndUpdated) {
	          // save change
	          dom.updateObject(currentObj, null);
	        }
	      }
	      // do other panel-specific tasks necessary
	      onCreateObject(currentObj);
	
	      // requests other controllers of the same type to update
	      // Controller.this.
	      controller.fireObjectAdd(this, id, currentObj, true);
	
	    } else { // top-level controller
	      // v2.7.2: added to support JObjectTable as the top-container
	      onCreateObject(currentObj);
	
	      /*
	       * v2.7.2: this seems not needed // do this to clear the child
	       * containers, etc. updateGUI();
	       */
	
	      // inform others
	      // Controller.this.
	      controller.fireObjectAdd(this, id, currentObj, true);
	    }
	
	    // v2.6.4.a:
	    if (controller.getMainController().isBrowsingEnabled()) {
	      if (browser != null) {// v3.2: added this check
	        updateBrowserOnCreate(id, currentObj);
	        boolean forceToIndex = true;  // v3.0
	        last(false, false, false, forceToIndex);
	      }
	    }
	
	    // v2.7.2: if there are child controllers then invoke createObject() on
	    // them
	    // to create associated objects based on the data that the user entered
	    // (if any)
	    createChildAssociatedObjects();
	
	    String mesg = null;
	    if (controller.getProperties()
	        .getBooleanValue("show.message.popup", true)) {
	      mesg = controller.displayMessageFromCode(MessageCode.OBJECT_CREATED, this,
	          //"Tạo dữ liệu mới {0}", 
	          // v3.0: currentObj
	          controller.getDomainClassLabel()
	          );
	    }
	
	    AppState state = AppState.Created;
	
	    setCurrentState(state, mesg);
	
	    // fire the state change event
	    if (methodListenerMap.containsKey(state)) {
	      fireMethodPerformed(state, currentObj);
	    }
	    
	    return currentObj;
    }
    
    /** 
     * <b>IMPORTANT</b>: You should generally use {@link #createObject(Map)} instead of this method, because 
     * that method supports more complex data validation rules. 
     * <br>Only use this method if simple validata rules suffice and that there is no need to map 
     * values to the domain attributes.
     * 
     * @effects creates a new object of <tt>C</tt> whose state is specified in
     *          the <tt>vals</tt>; throws
     *          <tt>ConstraintViolationException</tt> if some data value is
     *          invalid w.r.t the domain constraint; throws
     *          <tt>NotPossibleException</tt> if the object could not be
     *          created; throws <tt>DBException</tt> if an error occured
     * 
     * @modifies sets <tt>this.currentObj</tt> to the new object
     * 
     * @version 
     * - 2.8 <br>
     */
    public C createObject(Object[] vals) throws DataSourceException,
    ConstraintViolationException, NotPossibleException, NotFoundException  {
      
      DODMBasic dodm = controller.getDodm();
      DOMBasic dom = dodm.getDom();
      Class cls = getDomainClass();
      
      // create object in the object pool
      Tuple2<Oid, Object> t = dom.createObject(getDomainClass(), vals);
      currentObj = (C) t.getSecond();
      Oid id = t.getFirst();

      // v2.5.4: if there are other domain-type attributes of the new object
      // then we need to add the new object to the values of the corresponding
      // domain objects
      // as well
      DAttr linkAttribute = getLinkAttribute();
      /*
       * v2.6.4.b: changed to invoke a DataController method so that we can do
       * other things schema.updateAssociatesOnCreate(currentObj,
       * linkAttribute);
       */
      // add linkes to associates other than the parent object (this will be
      // added later below)
      addLinkToAssociates(currentObj, linkAttribute);

      /**
       * if parent is present then add object to the object buffer
       */
      if (parent != null) {
        // child controller
        /*
         * v2.7.3: replaced by addAssociationLinkToParent String parentAttribute
         * = getLinkAttributeOfParent().name(); // add object to parent's buffer
         * parent.updateAssociationLink(parentAttribute, currentObj);
         */
        if (isUpdateLinkToParent()) { // v3.0: added this check
          /* v3.2: added support for update to link end of this
          addAssociationLinkToParent(id, currentObj);
          */
          boolean myLinkEndUpdated = addAssociationLinkToParent(id, currentObj);
          if (myLinkEndUpdated) {
            // save change
            dom.updateObject(currentObj, null);
          }
        }
        // do other panel-specific tasks necessary
        onCreateObject(currentObj);

        // requests other controllers of the same type to update
        // Controller.this.
        controller.fireObjectAdd(this, id, currentObj, true);

      } else { // top-level controller
        // v2.7.2: added to support JObjectTable as the top-container
        onCreateObject(currentObj);

        /*
         * v2.7.2: this seems not needed // do this to clear the child
         * containers, etc. updateGUI();
         */

        // inform others
        // Controller.this.
        controller.fireObjectAdd(this, id, currentObj, true);
      }

      // v2.6.4.a:
      if (controller.getMainController().isBrowsingEnabled()) {
        if (browser != null) {// v3.2: added this check
          updateBrowserOnCreate(id, currentObj);
          boolean forceToIndex = true;  // v3.0
          last(false, false, false, forceToIndex);
        }
      }

      // v2.7.2: if there are child controllers then invoke createObject() on
      // them
      // to create associated objects based on the data that the user entered
      // (if any)
      createChildAssociatedObjects();

      String mesg = null;
      if (controller.getProperties()
          .getBooleanValue("show.message.popup", true)) {
        mesg = controller.displayMessageFromCode(MessageCode.OBJECT_CREATED, this,
            //"Tạo dữ liệu mới {0}", 
            // v3.0: currentObj
            controller.getDomainClassLabel()
            );
      }

      AppState state = AppState.Created;

      setCurrentState(state, mesg);

      // fire the state change event
      if (methodListenerMap.containsKey(state)) {
        fireMethodPerformed(state, currentObj);
      }
      
      return currentObj;
    }
    
    /**
     * @effects creates a new object of <tt>C</tt> whose state is specified in
     *          the <b>input fields of <tt>dataContainer</tt></b>; throws
     *          <tt>ConstraintViolationException</tt> if some data value is
     *          invalid w.r.t the domain constraint; throws
     *          <tt>NotPossibleException</tt> if the object could not be
     *          created; throws <tt>DBException</tt> if an error occured
     * 
     * @modifies sets <tt>this.currentObj</tt> to the new object
     * @version 
     * - 3.3: improved to support complex data validation rules <br>
     * - 3.4: added DomainWarning
     */
    public C  // v3.2: changed from void 
    createObject() throws DataSourceException,
        ConstraintViolationException, NotPossibleException, NotFoundException, DomainWarning {
      LinkedHashMap<DAttr, Object> vals = dataContainer.getUserSpecifiedState();

      if (vals == null) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE,
            "Bạn chưa nhập dữ liệu để tạo bản ghi");
      }
      
      // v3.3: use an improved method
      //return createObject(vals.values().toArray());
      return createObject(vals);
      
    } // end createObject

    /**
     * This method is used to create a sub-type object from the data input for a super-type object.
     * This data is obtained from the object form of the super-type data controller  
     * 
     * @effects 
     *  obtain user-specified-data from the object form of <tt>superDctl</tt>
     *  invoke {@link #createObject(Object[])} passing in this data as input 
     * @version 3.3
     */
    public C createObject(DataController superDctl) throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
      LinkedHashMap<DAttr, Object> valMap = superDctl.getDataContainer().getUserSpecifiedState();
      
      if (valMap == null) {
        throw new ConstraintViolationException(
            ConstraintViolationException.Code.INVALID_VALUE,
            "Bạn chưa nhập dữ liệu để tạo bản ghi");
      }
      
      // v3.3: use an improved method
      //return createObject(valMap.values().toArray());
      return createObject(valMap);
    }
    
    /**
     * @effects 
     *  perform tasks after {@link #createObject()} is invoked.
     * @version 
     * - 3.3: added support for post-create command <br>
     * - 5.1c: added support for {@link JDataContainer#onCreateObject(Object)}
     */
    public void onCreateObject(C obj) {
      // v3.3: perform command (if any)
      doOnCreateCommand(obj);
    }

    /**
     * @effects 
     *  perform post-create command on <tt>obj</tt>, if it is specified in the configuration
     * @version 3.3
     */
    protected void doOnCreateCommand(C obj) {
      // v3.3: added support for post-delete command
      DataControllerCommand cmd = lookUpCommand(LAName.OnCreateObject.name());
      
      if (cmd != null) {
        try {
          cmd.execute(this, obj);
        } catch (Exception e) {
          if (e instanceof NotPossibleException)
            throw (NotPossibleException) e;
          else 
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_POST_CREATE_OBJECT, e);
        }
      }      
    }
    
    /**
     * This method is invoked after {@link #createObject()} to create the
     * objects associated to the new object that are managed by the child
     * controllers of this (if any).
     * 
     * @effects <pre>
     *  let o = this.currentObj
     *  if there are child controllers
     *    for each child controller c
     *      if user had entered new data
     *        set value of c's linked data field to o
     *        invoke c.createObject
     * </pre>
     */
    protected void createChildAssociatedObjects() {
      // for sub-types to implement
    }

    /**
     * This method is invoked by a data controller after it has performed
     * changes that cause a change in the state of the current object of its
     * parent data controller. Its purpose is to inform any parent controllers
     * up in the hierarchy about this state change so that they may update
     * accordingly.
     * 
     * @requires this is a child controller
     * @effects if this.parent != null inform parent (and recursively the
     *          ancestors, if any) about changes to currentObj else do nothing
     */
    private void fireObjectChangeByChild() {
      if (parent != null) {
        // v2.7.2: added support for parent attribute
        DAttr parentAttrib = getLinkAttributeOfParent();
        parent.handleObjectChangeByChild(getDomainClass(), // cls,
            currentObj, parentAttrib);
      }
    }

    /**
     * @effects update currentObj based on state of childObj whose type is
     *          childCls and which is linked to currentObj via parentAttrib;
     * 
     *          if currentObj was changed propagate the update to the ancestor
     *          controller(s) of this (if any)
     */
    private void handleObjectChangeByChild(Class childCls, Object childObj,
        DAttr parentAttrib // v2.7.2
    ) {
      /*
       * v2.7.2: updated to use the parent's link attribute boolean updated =
       * schema.updateAssociateOnUpdate(currentObj, Controller.this.cls,
       * childCls, childObj);
       */

      boolean updated = dodm.getDom().updateAssociateOnUpdate(currentObj,
          getDomainClass(), // Controller.this.cls,
          parentAttrib, childCls, childObj);

      /*
       * if currentObj is changed save to storage inform parent (if any)
       */
      if (updated) {
        try {
          // TODO: update only the changed attribute (got from the above)
          // v2.7.4: moved to method
          // dodm.getDom().updateObject(currentObj, null);
          if (parent != null)
            updateObjectInDataSource(currentObj, null, parentAttrib,
                getLinkAttribute());
          else
            updateObjectInDataSource(currentObj, null, parentAttrib);

          // // v2.7.4
          // if (parent != null)
          // updateAssociatesOnUpdate(currentObj, null, parentAttrib,
          // getLinkAttribute());
          // else
          // updateAssociatesOnUpdate(currentObj, null, parentAttrib);

        } catch (DataSourceException e) {
          controller.logError("Failed to save updated object to storage", e);
        }

        // propagate change up the hierarchy
        fireObjectChangeByChild();
      }
    }

    /**
     * This method is invoked by other <code>DataController</code> objects to
     * force an update of <code>this.currentObjects</code> when their own have
     * been changed by <code>createObject</code>.
     * 
     * @effects if <code>obj</code> is an instance of <code>this.cls</code>,
     *          <code>this.objectBuffer != null</code> and <code>obj</code>
     *          satisfies {@link #getParentObjectQuery()} (if any) then adds it
     *          to <code>this.currentObjects</code>.
     * @requires <code>obj != null</code>
     * 
     * @version 
     *  3.1: added parameter src
     */
    protected void causeAdd(DataController source, Oid id, C obj) throws DataSourceException,
        NotFoundException {
      if (isBrowserOpened()) {
        /*
         * v2.6.4.a: improved to support report data controllers Query
         * parentQuery = getParentObjectQuery(); if (parentQuery == null ||
         * (parentQuery != null && parentQuery.eval(schema, obj))) {
         */
        /*
         * v2.6.4.b: added support for 1:1 association in which parent is the
         * determinant Query parentQuery = getParentObjectQuery(); if (parent !=
         * null && // a child controller (parentQuery == null || // no parent
         * query (e.g report) !parentQuery.eval(schema, obj)) // obj not valid
         * for this buffer ) { // object not suitable for this controller
         * return; }
         */
        if (parent != null) { // a child controller
          /*v3.1: add this check to ensure that objects are only added if they are linked  
          via the same association to the parent as the source controller that fire this update */ 
          if (source.getAssociationOfParent() != this.getAssociationOfParent()) {
            return;
          }
          
          if (isUpdateLinkToParent()) { // v3.0: added this check
            Query parentQuery = getParentObjectQuery(true  // strict
                );
            if (parentQuery != null && !parentQuery.eval(dodm, obj)) {
              // object not suitable for this controller
              return;
            } else if (parentQuery == null && !isDeterminedByParent()) {
              // this is either not determined by parent in a 1:1 association
              // or is so via a different link attribute as obj's to its parent
              return;
            }
          } 
        }

        // update data container
        onCauseAdd(obj);

        // update browser on create
        if (controller.getMainController().isBrowsingEnabled()
            && browser.isOpened()) {
          updateBrowserOnCreate(id, obj);
          boolean forceToIndex = false;
          // v3.2c: NOTE waitToFinish=false here may cause browsing error if the user starts browsing while this method is in progress
          // however, this case rarely happens and so we are happy to take this risk.
          last(true, true, false, forceToIndex);
        }

        // v2.7.3: if there is parent controller then fire an event to inform it
        // this occurs similar to when createObject was invoked
        // if (parent != null) {
        // addAssociationLinkToParent(id, obj);
        // }
      }
    }

    /**
     * This method is <b>ONLY</b> invoked by child controllers other than the
     * source child controller that caused an update to the
     * <tt>child</b> object.
     * 
     * @requires parent != null
     * 
     * @modifies child, {@link #parent#getCurrentObject()}
     * @effects update parent object to add a new association link to <tt>child
     *          </tt>
     *          
     *          <br>if <tt>child</tt> is updated (with the link's end)
     *            return <tt>true</tt>
     *          else
     *          return <tt>false</tt>
     * @version 
     * - 2.7.3 <br>
     * - 3.2: FIXED to update child's end of the link and return boolean if child is updated
     */
    private boolean addAssociationLinkToParent(Oid id, C child) {
      // v3.2: improved to support 1-1 association where link also needs to be set at the child's end
      // added a returned flag
      boolean childChanged = false;
      
      if (isAssociationToParent(AssocType.One2One)) {
        DOMBasic dom = getDodm().getDom();
        DSMBasic dsm = getDodm().getDsm();
        Object parentObj = parent.getCurrentObject();
        DAttr linkAttrib = getLinkAttribute();
        dom.setAttributeValue(child, linkAttrib.name(), parentObj);
        if (linkAttrib.serialisable()) {
          // save change
          childChanged = true;
        }
      }
      // end 3.2:
      
      // now update the link at the parent's end
      DAttr parentAttribute = getLinkAttributeOfParent(); // .name();
      
      boolean updated = parent.addAssociationLink(parentAttribute, currentObj);

      if (updated) {
        /*
         * ask the parent controller of this to inform its parents (if any) why?
         * the current object of an ancestor controller may contain derived
         * attribute whose value depends on the state of currentObj; in such case
         * this request causes the attribute's value to be re-computed
         */
        parent.fireObjectChangeByChild();
        
        // NOTE: a dead-lock may occur here if this operation is currently being performed as part 
        //        of a parent container.updateGUI
        // If this is the case, check the module design between this parent-child pair  
        //  to ensure that this does not happen!
        parent.updateGUI(null);
      }
      
      return childChanged;
    }

    /**
     * @effects update the data container to reflect the addition of <tt>o</tt>
     *          to <tt>this</tt>
     */
    protected void onCauseAdd(C o) {
      // no implementation
    }

    /**
     * @requires the domain class of <tt>this</tt> has a method named
     *           <tt>"derive"+attributeName</tt> whose only parameter is of type
     *           <tt>List</tt> and the values in <tt>vals</tt> match the values
     *           required by this method.
     * 
     * @effects invoke method <tt>"derive"+attributeName</tt> (normal naming
     *          convention applies), of the domain class of <tt>this</tt>, with
     *          <tt>vals</tt> as argument and return the result.
     */
    public Object derive(Object obj, String attributeName, List vals)
        throws NotPossibleException {
      Class cls = getDomainClass();

      String methodName = "derive"
          + (attributeName.charAt(0) + "").toUpperCase()
          + attributeName.substring(1);

      try {
        Method m = cls.getDeclaredMethod(methodName, List.class);

        if (m != null) {
          // method m may be null
          if (obj != null)
            return m.invoke(obj, vals);
          else if (Modifier.isStatic(m.getModifiers()))
            return m.invoke(null, vals);
          else
            return null;
        } else {
          return null;
        }
      } catch (Exception e) {
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_PERFORM, e,
            "Lỗi thực thi phương thức {0}.{1}", cls, methodName);
      }
    }

    /**
     * @effects 
     *  if this.currentObj != null
     *    reload its state from the data source and replace the object in the pool by the new object.
     *    update GUI
     *    
     *  <p>if this is a parent controller and its open policy is to load children objects
     *     <pre> 
     *     for each such child controller
     *        remove browsed objects from the pool
     *        clear the browser's buffer 
     *        reload the objects from data source and replace those in the pool by the new objects
     *        </pre>
     *        
     * @throws NotFoundException if object is not found OR an associated object is not found  
     * @throws NotPossibleException if failed to create object from the data source record
     * @throws DataSourceException if failed to read record from the data source
     *        
     * @version 
     * - 3.0: created <br>
     * - 3.1: improved to support selected objects  
     */
    public void reload() throws NotPossibleException, NotFoundException, DataSourceException {
      /*v3.1: to support selected objects 
      if (currentObj != null) {
        DOMBasic dom = dodm.getDom();
        
        boolean objectSerialised = dodm.isObjectSerialised();
        
        if (objectSerialised && 
            dom.isConnectedToDataSource()) {
          Class<C> cls = getDomainClass();
          
          Oid currId = dom.lookUpObjectId(cls, currentObj);
          
          C reloadObj = dom.reloadObject(cls, currId);
          
          showObject(reloadObj);
        }
      }
      */
      DOMBasic dom = dodm.getDom();
      
      boolean objectSerialised = dodm.isObjectSerialised();
      
      if (objectSerialised && 
          dom.isConnectedToDataSource()) {
        Class<C> cls = getDomainClass();
        
        Oid id;
        C reloadObj;
        Collection<C> selectedObjs = null;
        
        //TODO: remove this try..catch when getSelectedObjects is implemented for DefaultPanel
        try {
          selectedObjs = getSelectedObjects();
        } catch (Exception e) {
          // ignore
        }
        
        if (selectedObjs != null) {
          boolean currentObjIsSelected = false;
          
          // work on all selected objects (including the current)
          for (C obj : selectedObjs) {
            id = dom.lookUpObjectId(cls, obj);
            reloadObj = dom.reloadObject(cls, id);
            
            if (!currentObjIsSelected && obj == currentObj) {
              showObject(reloadObj);  // extra step for currentObj
              currentObjIsSelected = true;
            }
          }
          
          // added this check in case currentObj is not among the selected
          if (!currentObjIsSelected) {
            // work on the current object
            id = dom.lookUpObjectId(cls, currentObj);
            reloadObj = dom.reloadObject(cls, id);
            showObject(reloadObj);            
          }
        } else if (currentObj != null) {
          // work on the current object
          id = dom.lookUpObjectId(cls, currentObj);
          reloadObj = dom.reloadObject(cls, id);
          showObject(reloadObj);
        }
      }
    }
    
    /**
     * @effects refresh this.dataContainer to show updated object(s) data
     * 
     * @modifies {@link #currentObj}
     * 
     * @version 
     * - 2.7.4: updated to refresh buffer first<br>. 
     *   <br>To handle refresh, domain class needs to define a method tagged with {@link Metadata#Type#MethodRefreshState}
     *   
     * - 3.1: requires the refresh of selected bounded data fields
     * 
     * <br>
     *          Sub-types <b>must</b> first invoke this method before doing
     *          their own things
     */
    public void refresh() {
      refreshBuffer();
      
      // v3.1: requires the refresh of selected bounded data fields
      dataContainer.refreshLinkedData();
    }

    /**
     * This method is used as part of {@link #refresh()} to obtain
     * the most up-to-date state of the current object (or objects in the buffer
     * if this is a child controller) before displaying them on the data
     * container. This is necessary especially when these objects have derived
     * attributes, whose values are computed from states of other objects but
     * changes to these objects' state have not yet been effected on these
     * objects.
     * 
     * @modifies {@link #currentObj} OR each object in {@link #getObjectBuffer()}
     * 
     * @effects 
     * <pre>
     *  if this is top-level 
     *    refresh state of {@link #currentObj}
     *    if state changed
     *      update {@link #currentObj} in data source
     *  else 
     *    for each object o in this.buffer 
     *      refresh state of o
     *      if state changed
     *        update o in data source
     * </pre>
     * @version 
     * - 2.7.4 <br>
     * - 3.2: improved to support saving state change 
     */
    private void refreshBuffer() {
      DOMBasic dom = controller.getDodm().getDom();
      Class cls = getDomainClass();

      //v3.2: 
      boolean stateChangeUpdate;
      
      if (parent == null || controller.isSingleton()) {
        // top-level
        if (currentObj != null) {
          try {
            /*v3.2: 
            dom.refreshObjectState(currentObj, cls);
            */
            stateChangeUpdate = dom.refreshObjectState(currentObj, cls);
            if (stateChangeUpdate) {
              try {
                dom.updateObject(currentObj, null);
              } catch (ConstraintViolationException | DataSourceException e) {
                // failed to save for some reasons: ignore
              }
            }
          } catch (NotImplementedException e) {
            // c does not support refresh state
            // ignore
          }
        }
      } else {
        // child controller
        Iterator<C> objs = getObjectBuffer();
        if (objs != null) {
          Object o;
          try {
            while (objs.hasNext()) {
              o = objs.next();
              /*v3.2: 
              dom.refreshObjectState(o, cls);
              */
              stateChangeUpdate = dom.refreshObjectState(o, cls);
              if (stateChangeUpdate) {
                try {
                  dom.updateObject(currentObj, null);
                } catch (ConstraintViolationException | DataSourceException e) {
                  // failed to save for some reasons: ignore
                }
              }
            }
          } catch (NotImplementedException e) {
            // c does not support refresh state
            // ignore
          }
        }
      }
    }

    /**
     * @effects if the current state is new object then reset the GUI
     */
    public void reset() {
      // v2.6.1
      // // confirm
      // boolean confirm = displayConfirm(MessageCode.CONFIRM_DATA_RESET,
      // this,
      // "Bạn có muốn thực sự muốn nhập lại dữ liệu không?");
      // if (confirm) {
      // onReset();
      //
      // setCurrentState(AppState.Reset);
      // }
      reset(false);
    }

    /**
     * @effects if silent = false confirm with user to reset the GUI's data to
     *          its previous state
     * 
     *          if proceed reset GUI data to previous state update current state
     *          of this
     */
    public void reset(boolean silent) {
      boolean confirm = true;

      if (!silent) {
        // confirm
        confirm = controller.displayConfirmFromCode(MessageCode.CONFIRM_DATA_RESET,
            this);
      }

      if (confirm) {
        onReset();

        setCurrentState(AppState.Reset);
      }
    }

    protected void onReset() {
      //
    }

    /**
     * @effects if the current state is new object then cancel it by displaying
     *          the current object
     */
    public void cancel(boolean silent) {
      boolean confirm = true;

      if (!silent) {
        // confirm
        confirm = controller.displayConfirmFromCode(
            MessageCode.CONFIRM_CANCEL_NEW_OBJECT, this);
      }

      if (confirm) {
        onCancel();

        setCurrentState(AppState.Cancelled);
        // fireApplicationStateChanged(AppState.Cancelled, null);
      }
    }

    public abstract void onCancel();

    /**
     * @effects 
     *   perform part of {@link #onCancel()} that concerns {@link #dataContainer}. 
     *    
     * @version 5.1c
     */
    protected void onCancelGUI() {
      JDataContainer dataContainer = getDataContainer();  
      if (dataContainer != null) {
        dataContainer.onCancel();
      }
    }
    
    /**
     * @param myObj
     *          an object of this that needs to be updated in the underlying
     *          data source
     * 
     * @requires <pre>
     * excludeAttribsInPropagation != null -> 
     *        for all a in excludeAttribsInPropagation. a is an attribute of myObj.class
     * </pre>
     * @effects save the state of <tt>myObj</tt> into the underlying data source
     *          and inform other associated domain objects that are linked via
     *          attributes that are not in <tt>excludeAttribsInPropagation</tt>
     *          (if this is specified) (and recursively those associated to them
     *          and so on) to update their states if needed.
     * 
     * <br>
     *          Throws an exception if failed.
     * 
     * @version 2.7.4: support propagation of update through associations with
     *          other objects
     */
    private void updateObjectInDataSource(Object myObj,
        Map<DAttr, Object> changedValMap,
        DAttr... excludeAttribsInPropagation)
        throws NotPossibleException, NotFoundException, DataSourceException {
      DODMBasic dodm = controller.getDodm();

      dodm.getDom().updateObject(myObj, changedValMap);

      // propagate update (if necessary)
      updateAssociatesOnUpdate(myObj, null, excludeAttribsInPropagation);
    }

    /**
     * @param myObj
     *          an object of this that needs to be updated in the underlying
     *          data source
     * 
     * @requires excludeAssociate != null
     * 
     * @effects save the state of <tt>myObj</tt> into the underlying data source
     *          and inform other associated domain objects that are not
     *          <tt>excludeAssociate</tt> (and recursively those associated to
     *          them and so on) to update their states if needed.
     * 
     * <br>
     *          Throws an exception if failed.
     * 
     * @version 2.7.4: support propagation of update through associations with
     *          other objects
     */
    private void updateObjectInDataSourceWithExclusion(Object myObj,
        Map<DAttr, Object> changedValMap, Object excludeAssociate)
        throws NotPossibleException, NotFoundException, DataSourceException {
      DODMBasic dodm = controller.getDodm();

      dodm.getDom().updateObject(myObj, changedValMap);

      // propagate update (if necessary) to associates other than
      // excludeAssociate
      Collection updateBuffer = new ArrayList();
      updateBuffer.add(excludeAssociate);
      updateBuffer.add(myObj);
      updateAssociatesOnUpdate(myObj, updateBuffer);
    }

    /**
     * @requires 
     *  domainAttrib != null
     * @effects 
     *  if currentObj != null
     *    sets <tt>currentObj.domainAttrib = value</tt> by calling {@link #updateObject(LinkedHashMap)} 
     *  else
     *    do nothing
     * @version 3.1
     */
    public void updateObject(DAttr domainAttrib, Object value, boolean silent) throws DataSourceException, ConstraintViolationException {
      if (currentObj != null) {
        LinkedHashMap<DAttr, Object> mutableVals = new LinkedHashMap();
        mutableVals.put(domainAttrib, value);
        
        updateObject(mutableVals, silent);
      }
    }
    
    /**
     * This method is invoked when the user clicks the Update action. It results
     * in the update of the current object of the active data entry GUI.
     * 
     * @effects updates current object <code>currentObj</code> with the new data
     *          values obtained from the data container
     * @version 
     * - 3.1: call a shared method <br>
     * - 3.2: change return type from void to Map <br>
     * - 3.3: support the use of DDataValidator (if specified) <br>
     * - 3.4: added support for DomainWarning
     */
    public Map<DAttr,Object> updateObject() throws DataSourceException, ConstraintViolationException {
      if (currentObj != null) { // v3.2: added this check
        LinkedHashMap<DAttr, Object> mutableVals = dataContainer.getMutableState();
        
        // v3.3: use the data validator to validate vals. Unlike
        // the individual value checks (which were performed as part of getUserSpecifiedState()), 
        // this check is performed against a combination of vals
        DataValidator dataValidator = getDataValidatorInstance();
        
        try {
          dataValidator.validateDomainValues(currentObj, mutableVals);
          
          return updateObject(mutableVals, false);
        } catch (DomainWarning warn) {
          // display warning and proceed
          if (controller.getProperties().getBooleanValue("show.message.popup",true)) {
            controller.displayWarningFromCode(warn.getCode(), this, false);
          }

          return updateObject(mutableVals, false);
        }
      } else {
        return null;
      }
    }

    /**
     * This method is invoked when the user clicks the Update action. It results
     * in the update of the current object of the active data entry GUI.
     * 
     * @requires 
     *  currentObj != null
     * @effects 
     *  updates current object <code>currentObj</code> with the new data
     *  values specified in <tt>attribValMap</tt>; 
     *          
     *  <p>If <tt>silent = false</tt> then do not display any messages.
     *  
     *  <p>return {@link Map} containing attributes (and their values) that were changed. 
     *  
     * @version 
     * - 3.1 <br>
     * - 3.2: change return type from void to Map <br>
     * - 3.3: (1) support post-update
     */
    public Map<DAttr,Object> updateObject(LinkedHashMap<DAttr,Object> attribValMap, boolean silent) throws DataSourceException, ConstraintViolationException {
      if (currentObj != null) {
        final DSMBasic dsm = getDodm().getDsm();
        final DOMBasic dom = getDodm().getDom();
        
        Class cls = getDomainClass();
        // v2.6.1: get old vals affected by newVals
        LinkedHashMap<DAttr, Object> affectedVals = dsm.getAttributeValues(
            getDomainClass(), currentObj, attribValMap);

        // only update if values were changed
        String mesg = null;

        if (affectedVals != null) {

          if (debug)
            controller.log("%s.updateObject():%n old values changed: %s", this,
                affectedVals);

          // determine the newly changed vals: which always contains the same
          // keys as affectedVals,
          // but the values of whom differ
          LinkedHashMap<DAttr, Object> newChangedVals = null;
          if (affectedVals.size() < attribValMap.size()) {
            newChangedVals = new LinkedHashMap<DAttr, Object>();
            DAttr k;
            for (Entry<DAttr, Object> e : affectedVals.entrySet()) {
              k = e.getKey();
              newChangedVals.put(k, attribValMap.get(k));
            }
          } else {
            newChangedVals = attribValMap;
          }

          //
          // TODO: Check cardinality constraints on all forms of update
          // (performed below) that
          // involve the use of associations
          //

          // //////////////////////////////////////////////////////////////////////////////////
          // PHASE 1: Update any (previously) associated domain objects involved
          // in oldVals
          // (this is needed to correctly maintain the states of those objects)
          // //////////////////////////////////////////////////////////////////////////////////
          DAttr attrib;
          Object domainObj;
          Class domainCls;
          ControllerBasic domainCtl;
          DataController domainDctl;

          for (Entry<DAttr, Object> e : affectedVals.entrySet()) {
            attrib = e.getKey();
            domainObj = e.getValue();
            if (attrib.type().isDomainType() && domainObj != null) {
              // update domainObj to remove link to currentObj

              // v2.6.4.a: update using the data controller of the domainType
              // so that we can update the view if needed
              domainCls = dsm.getDomainClassFor(getDomainClass(), // cls,
                  attrib.name());
              domainDctl = lookUpRootDataController(domainCls, domainObj);
              if (domainDctl != null) {
                domainCtl = domainDctl.getCreator();

                Object oldProp = domainCtl.getProperty("show.message.popup");
                // disable pop-up
                domainCtl.setProperty("show.message.popup", false);
                domainDctl.removeLinkToAssociate(domainObj, currentObj, attrib);
                domainCtl.setProperty("show.message.popup", oldProp);
              } else {
                controller.logError("Không tìm thấy trình điều khiển "
                    + domainCls, null);
                // throw new
                // NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
                // "Không tìm thấy trình điều khiển {0}", domainCls);
              }
            }
          } // end for

          // //////////////////////////////////////////////////////////////////////////////////
          // PHASE 2: Commit changes to current object
          // //////////////////////////////////////////////////////////////////////////////////

          /*
           * v2.7.2: pass in affectedVals as argument // v2.6.1: update object
           * using new vals only (without extracting // oldVals)
           * schema.updateObject(currentObj, newChangedVals);
           */
          dom.updateObject(currentObj, affectedVals, newChangedVals,
              true);

          // //////////////////////////////////////////////////////////////////////////////////
          // PHASE 3: Update
          // (1) any newly associated domain objects AND
          // (2) any existing associated domain objects
          // //////////////////////////////////////////////////////////////////////////////////

          // (1): update any newly associated domain objects
          for (Entry<DAttr, Object> e : newChangedVals.entrySet()) {
            attrib = e.getKey();
            domainObj = e.getValue();
            if (attrib.type().isDomainType() && domainObj != null) {
              // update domainObj to add a link to currentObj

              // v2.6.4.a: update using the data controller of the domainType
              // so that we can update the view if needed
              // schema.updateAssociatesOnUpdate(currentObj, domainObj);
              domainCls = dsm.getDomainClassFor(cls, attrib.name());
              domainDctl = lookUpRootDataController(domainCls, domainObj);
              if (domainDctl != null) {
                domainCtl = domainDctl.getCreator();

                Object oldProp = domainCtl.getProperty("show.message.popup");
                // disable pop-up
                domainCtl.setProperty("show.message.popup", false);
                domainDctl.addLinkToAssociate(currentObj, attrib, domainObj);
                domainCtl.setProperty("show.message.popup", oldProp);
              } else {
                controller.logError("Không tìm thấy trình điều khiển "
                    + domainCls, null);
                // throw new
                // NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
                // "Không tìm thấy trình điều khiển {0}", domainCls);
              }
            }
            // }
          } // end for (1)

          // (2) update any existing associated domain objects
          // e.g. if associated object is Product<x> and an updated attrib is
          // OrderLine.quantity then
          // Product<x> needs to be updated so that the value of its
          // Product.totalSales attribute
          // can be changed

          // Note: exclude the parent object from the list (if any) (because it
          // is updated separately below)
          DAttr linkAttrib = getLinkAttribute();
          if (newChangedVals.size() < attribValMap.size()) {
            for (Entry<DAttr, Object> e : attribValMap.entrySet()) {
              attrib = e.getKey();
              domainObj = e.getValue();

              if (attrib == linkAttrib)
                continue; // skip link to parent

              if (attrib.type().isDomainType()
                  && !newChangedVals.containsKey(attrib) // only consider the
                                                         // existing associated
                                                         // objects
                  && domainObj != null) {
                // inform via the data controller of the domainType
                // so that we can update the view if needed
                domainCls = dsm.getDomainClassFor(cls, attrib.name());
                domainDctl = lookUpRootDataController(domainCls, domainObj);
                if (domainDctl != null) {
                  domainCtl = domainDctl.getCreator();

                  Object oldProp = domainCtl.getProperty("show.message.popup");
                  // disable pop-up
                  domainCtl.setProperty("show.message.popup", false);
                  domainDctl.updateAssociateOnUpdate(domainObj, currentObj,
                      cls, attrib);
                  domainCtl.setProperty("show.message.popup", oldProp);
                  // if (updated) { // v2.7.4
                  // domainDctl.updateAssociatesOnUpdate(domainObj, null,
                  // attrib);
                  // }
                } else {
                  controller.logError("Không tìm thấy trình điều khiển "
                      + domainCls, null);
                  // throw new
                  // NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
                  // "Không tìm thấy trình điều khiển {0}", domainCls);
                }
              }
            } // end for (2)
          }

          // v3.3: support post-update
          onUpdateObject(currentObj, affectedVals, newChangedVals);
          
          // //////////////////////////////////////////////////////////////////////////////////
          // PHASE 4: Update parent (recursively) if this is a child controller
          // request parent controller to update
          // (why? in case derived attribute(s) of parent and its parents etc.
          // need to be re-computed)
          // //////////////////////////////////////////////////////////////////////////////////

          fireObjectChangeByChild();

          // //////////////////////////////////////////////////////////////////////////////////
          // PHASE 5: Update GUI, states, etc.
          // //////////////////////////////////////////////////////////////////////////////////

          // update the GUI and recursively the parent GUIS
          // (without updating the children) in case the object has
          // auto-generated values
          // which were not entered by the user
          updateGUI(null);

          if (!silent && controller.getProperties().getBooleanValue("show.message.popup",true)) {
            mesg = controller.displayMessageFromCode(MessageCode.OBJECT_UPDATED, this,
                //currentObj
                controller.getDomainClassLabel()
                );
          }
        } // end if affectedVals

        // change state passing old vals as state data
        AppState state = AppState.Updated;
        setCurrentState(state, mesg, affectedVals);

        // fire the state change event
        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, currentObj);
        }
        
        // v3.2:
        return affectedVals;
      } else {
        // v3.2
        return null;
      }
    }

    /**
     * @requires
     *  <tt>oldChangedVals</tt> contains the former values of the domain attributes that were changed by the user
     *  <tt>newChangedVals</tt> contains the new values of the same domain attributes  
     * @effects 
     *  Perform post-update task on <tt>obj</tt><br>
     *  
     *  if exists extension {@link DataControllerCommand} for {@link DataControllerCommand#Name#OnUpdateObject} 
     *    execute the command
     *    <br>throws NotPossibleException if error occurs
     *  else 
     *    do nothing  
     * @version 3.3
     */
    public void onUpdateObject(C obj, 
        LinkedHashMap<DAttr, Object> oldChangedVals, 
        LinkedHashMap<DAttr, Object> newChangedVals) throws NotPossibleException {
      doOnUpdateCommand(obj, oldChangedVals, newChangedVals);
    }

    /**
     * @effects 
     *  perform post-update command on <tt>obj</tt>, if it is specified in the configuration
     * @version 3.3
     */
    protected void doOnUpdateCommand(C obj, 
        LinkedHashMap<DAttr, Object> oldChangedVals, 
        LinkedHashMap<DAttr, Object> newChangedVals) {
      // v3.3: added support for post-update command
      DataControllerCommand cmd = lookUpCommand(LAName.OnUpdateObject.name());
      
      if (cmd != null) {
        try {
          cmd.execute(this, obj, oldChangedVals, newChangedVals);
        } catch (Exception e) {
          if (e instanceof NotPossibleException)
            throw (NotPossibleException) e;
          else 
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_POST_UPDATE_OBJECT, e);
        }
      }      
    }
    
    /**
     * 
     * @requires 
     *    associate is an object of this.domainCls /\ 
     *    linkAttrib is an attribute of removedObj.class
     * 
     * @effects <pre>
     *  find the correct association between associate.class and deletedObj.class that is via linkAttrib
     *   if association is 1:M
     *      invoke deleter method of associate to remove deletedObj from the association
     *   else
     *      invoke setter method of associate to set value to null 
     *  
     *  if associate is updated
     *    update it 
     *  else
     *    do nothing
     * 
     * <p>e.g. 
     *  <pre>
     *  If associate = Student<1>, removedObj = Enrolment<1>, linkAttrib = Enrolment.student
     *  then
     *    delete method = Student.deleteEnrolment 
     *  </pre>
     *  
     *  @version 
     *  - 3.3: changed to public
     */
    public void removeLinkToAssociate(Object associate, Object removedObj,
        DAttr linkAttrib) throws NotPossibleException,
        NotFoundException {
      boolean updated = dodm.getDom().updateAssociateToRemoveLink(associate,
          removedObj, linkAttrib);

      if (updated) {
        try {
          // v2.7.4: moved to method
          // dodm.getDom().updateObject(associate, null);
          updateObjectInDataSourceWithExclusion(associate, null, removedObj);

          // if linkedObj is the currently displayed object then update the GUI
          // TODO: a better solution is to check if updateObj.class is currently
          // being displayed
          // as part of this GUI. If so then only update the browser of that
          // part of the GUI
          // (remove updateObj from it?)
          if (associate == currentObj) {
            updateGUI();
          }
        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
              "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
              "updateAssociateToRemoveLink", associate);
        }
      }
    }

    /**
     * @effects update <tt>associate</tt> to record a new association link to
     *          <tt>updateObj</tt>, that is linked via the attribute
     *          <tt>attrib</tt> of <tt>updateObj.class</tt>.
     * 
     *          <p>
     *          update <tt>associate</tt> in the data source if it is changed.
     * 
     *          <p>
     *          Throws NotPossibleException if failed to update
     *          <tt>associate</tt> when need to
     * 
     * @version 2.7.2: added parameter attrib
     */
    private void addLinkToAssociate(Object updateObj, DAttr attrib,
        Object associate) throws // NotFoundException,
        NotPossibleException {
      /*
       * v2.7.2: ignore NotFoundException boolean updated =
       * schema.updateAssociateToAddLink(updateObj, associate);
       */
      boolean updated = false;
      try {
        updated = dodm.getDom()
            .addLinkToAssociate(updateObj, attrib, associate);
      } catch (NotFoundException e) {
        // ignore
      }

      // v2.6.4.b: store changes to assciate to data source
      if (updated) {
        try {

          // v2.7.4: moved to method
          // dodm.getDom().updateObject(associate, null);
          updateObjectInDataSourceWithExclusion(associate, null, updateObj);

          // if linkedObj is the currently displayed object then update the GUI
          // TODO: a better solution is to check if updateObj.class is currently
          // being displayed
          // as part of this GUI. If so then only update the browser of that
          // part of the GUI
          // (add updateObj to it?)
          if (associate == currentObj) {
            updateGUI();
          }
        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
              "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
              "updateAssociateToAddLink", associate);
        }
      }
    }

    /**
     * This method invokes
     * {@link #addLinkToAssociate(Object, DAttr, Object)} for each
     * associated object of a given domain object.
     * 
     * @effects update <b>all</b> <tt>associated object</tt> of <tt>myObj</tt>
     *          (excluding those linked via <tt>excludeViaAttrib</tt> if it is
     *          specified)
     *          that are linked to <tt>myObj</tt> in the association state scope 
     *           to record a new association link with it
     *          <p>
     *          Throws NotFoundException if could not find the association
     *          between the two corresponding domain classes;
     *          NotPossibleException if more than one such associations are
     *          found
     * @version 
     * - 2.6.4.b<br>
     * - v3.1: only consider associatiates in the association state scope
     */
    private void addLinkToAssociates(Object myObj,
        DAttr excludeViaAttrib) throws NotFoundException,
        NotPossibleException {
      Class cls = getDomainClass();

      /* v3.1: take into account the association state scope of this
      Collection<Associate> associates = dodm.getDom().getAssociates(myObj, cls);
      */
      AssociationStateScope assocStateScope = getAssociationStateScope();
      
      Collection<Associate> associates = dodm.getDom().getAssociates(myObj, cls, assocStateScope);
      
      if (associates != null) {
        DAttr attrib;
        Object domainObj;
        Class domainCls;
        ControllerBasic domainCtl;
        ControllerBasic.DataController domainDctl;
        for (Associate a : associates) {
          attrib = a.getFarEndAttribute(); // myObj's attribute

          if (attrib == excludeViaAttrib)
            continue;

          domainObj = a.getAssociateObj();
          if (domainObj != null) {
            // update using the data controller of the domainType
            // so that we can update the view if needed
            domainCls = dodm.getDsm().getDomainClassFor(cls, attrib.name());
            domainDctl = lookUpRootDataController(domainCls, domainObj);
            if (domainDctl != null) {
              domainCtl = domainDctl.getCreator();

              Object oldProp = domainCtl.getProperty("show.message.popup");
              // disable pop-up
              domainCtl.setProperty("show.message.popup", false);
              /*
               * v2.7.2: fixed to use myObj and to use link attribute
               * domainDctl.updateAssociateToAddLink(currentObj, domainObj);
               */
              domainDctl.addLinkToAssociate(myObj, attrib, domainObj);
              domainCtl.setProperty("show.message.popup", oldProp);
            } else {
              // update using the schema instead
              throw new NotFoundException(
                  NotFoundException.Code.CONTROLLER_NOT_FOUND,
                  "Không tìm thấy trình điều khiển {0}", domainCls);
            }
          }
        } // end for
      }
    }

    /**
     * @effects <pre>
     *  if exists association between <tt>aCls</tt> and <tt>this.cls</tt> via attrib <tt>assocAttrib</tt> of 
     *  <tt>aCls</tt>
     *    invoke attribute-update method on <tt>myObj</tt> passing in <tt>associate</tt> as argument
     *  
     *  <p>if <tt>myObj</tt> was changed 
     *    if myObj is the current object
     *      update GUI
     *    return true
     *   else
     *    return false
     * </pre>
     * 
     * @example <pre>
     *  myObj = Product("A")
     *  associate = OrderLine(Order(1),Product("A"),20)
     * </pre>
     * 
     *          will invoke <tt>myObj.updateOrderLine(associate)</tt>, which
     *          updates the value of attribute <tt>Product("A").totalSales</tt>
     *          to take into account the new ordered quantity (20).
     */
    private// v2.7.4: void
    boolean updateAssociateOnUpdate(Object myObj, Object associate, Class aCls,
        DAttr assocAttrib // v2.7.2
    ) {
      Class cls = getDomainClass();

      // v2.7.2: look up the corresponding attribute of myObj.class and use it
      // to update
      DAttr myAttrib = null;
      try {
        Tuple2<DAttr, DAssoc> myAssocTuple = dodm.getDsm()
            .getTargetAssociation(aCls, assocAttrib);
        myAttrib = myAssocTuple.getFirst();
      } catch (NotFoundException e) {
        // no association defined -> skip
        return false; // ;
      }

      boolean updated = dodm.getDom().updateAssociateOnUpdate(myObj, cls,
          myAttrib, aCls, associate);

      // if linkedObj is the currently displayed object then update the GUI
      // TODO: a better solution is to check if updateObj.class is currently
      // being displayed
      // as part of this GUI. If so then only update the browser of that part of
      // the GUI
      // (remove updateObj from it?)
      if (updated) {
        try {

          // v2.7.4: moved to method
          // dodm.getDom().updateObject(myObj, null);
          updateObjectInDataSource(myObj, null, myAttrib);

          // System.out.printf("Updated: %s (link attrib: %s) %n", myObj,
          // myAttrib.name());

          if (myObj == currentObj) {
            updateGUI();
          }

          // v2.7.4: updated
          return true;
        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
              "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
              "updateAssociateOnUpdate", myObj);
        }
      }
      // v2.7.4
      else { // no updated
        return false;
      }
    }

    /**
     * 
     * @param myObj
     *          a domain object of this that has been updated and whose update
     *          is causing the associates to update
     * @param excludeLinkAttrib
     *          (optional) the domain attribute through which an associate of
     *          myObj will NOT be considered for update
     * @param updateBuffer
     *          a record of all domain objects that have been caused to update
     *          so far (to avoid update loop)
     * 
     * @requires <pre>
     *  myObj is not null /\ is an instance of this.cls /\ 
     *  updateBuffer is not null -> myObj is in updateBuffer
     *  excludeLinkAttribs is not null -> forall a in excludeLinkAttribs. a is an attribute of myObj.class
     * </pre>
     * 
     * @effects update the domain object associates of <tt>myObj</tt>, if any
     *          (using <tt>updateBuffer</tt> to record them) and recursively
     *          those that are associated to the associates.
     * 
     * @pseudocode <pre>
     *  let C = myObj.class
     *  if there are domain objects associated to myObj whose associations have property \@Association(...,derivedFrom=true)
     *    for each obj o', associated to myObj via attribute C.a s.t C.a notin excludeLinkAttribs /\ o' not in updateBuffer
     *      let C' = o'.class
     *      let L' = DataController(C')
     *      if L' is null
     *        log error
     *      else 
     *        update o': call L'.{@link #updateAssociateOnUpdate(Object, Object, Class, DAttr)} with (o',myObj,C,C.a)
     *        if o' is changed
     *          add o' to updateBuffer
     *          // recursive 
     *          call L'.{@link #updateAssociatesOnUpdate(Object, Collection)} with (o', updateBuffer)
     * </pre>
     * 
     * @version 2.7.4
     */
    private void updateAssociatesOnUpdate(Object myObj,
        Collection updateBuffer, DAttr... excludeLinkAttribs) {
      if (updateBuffer == null) {
        updateBuffer = new ArrayList();
        updateBuffer.add(myObj);
      }

      Class c = getDomainClass();

      DSMBasic dsm = controller.getDomainSchema();
      DOMBasic dom = controller.getDodm().getDom();

      Collection<Associate> associates = dom.getAssociatesDerivedFrom(myObj, c);

      if (associates != null) {
        DAttr attrib;// a
        Object assocObj; // o'
        Class assocCls; // C'
        DataController dctl; // L'
        ControllerBasic ctl;
        boolean updated;

        ASSOCIATE: for (Associate associate : associates) {
          attrib = associate.getFarEndAttribute();
          assocObj = associate.getAssociateObj();
          assocCls = associate.getAssociateClass();

          if (excludeLinkAttribs != null) {
            for (int i = 0; i < excludeLinkAttribs.length; i++)
              if (attrib == excludeLinkAttribs[i])
                continue ASSOCIATE; // skip
          }

          if (assocObj != null && !updateBuffer.contains(assocObj)) {
            // look up L'
            dctl = lookUpRootDataController(assocCls, assocObj);
            if (dctl == null) {
              // log error
              controller.logError("Không tìm thấy trình điều khiển: "
                  + assocCls, null);
              // throw new
              // NotFoundException(NotFoundException.Code.CONTROLLER_NOT_FOUND,
              // "Không tìm thấy trình điều khiển {0}", domainCls);
            } else {
              ctl = dctl.getCreator();
              // update o' (disabling pop-up)
              Object oldProp = ctl.setProperty("show.message.popup", false);
              updated = dctl
                  .updateAssociateOnUpdate(assocObj, myObj, c, attrib);
              ctl.setProperty("show.message.popup", oldProp);

              if (updated) {
                // o' is changed
                updateBuffer.add(assocObj);
                // recursive: no need to exclude attribute b/c we will use
                // updateBuffer instead
                dctl.updateAssociatesOnUpdate(assocObj, updateBuffer, null);
              }
            }
          }
        } // end for ASSOCIATE
      }
    }


    /**
     * @effects
     *  for each object <tt>o</tt> in <tt>objects</tt>
     *    {@link #deleteObjectFromBuffer(Object, boolean)} on <tt>o</tt>
     * @version 3.0 
     */
    public void deleteObjectsFromBuffer(C[] objects, boolean toConfirm) throws NotPossibleException, NotFoundException, DataSourceException {
      if (objects == null)
        return;
      
      int index = 0;
      final int numObjs = objects.length;
      
      for (C o : objects) {
        if (index == numObjs-1)
          deleteObjectFromBuffer(o, toConfirm, true);
        else
          deleteObjectFromBuffer(o, toConfirm, false);
        
        index++;
      }
      
      updateGUIButtons();
      
    }
    
    /**
     * @effects 
     *  remove <tt>obj</tt> from this, without removing it from the data source 
     *  nor removing any association links to other objects.
     *  
     *  <br>if <tt>browseAwayIfCurrent = true</tt> AND <tt>obj eq currentObj</tt>
     *    browse away to another object (if any)
     *  
     * @version 3.0
     */
    private void deleteObjectFromBuffer(C obj, boolean toConfirm, boolean browseAwayIfCurrent) {
      // v3.0: String objStr = obj.toString();

      // confirm with user (if required)
      if (toConfirm) {
        boolean confirm = controller.displayConfirmFromCode(
            MessageCode.CONFIRM_DELETE_OBJECT, this//, objStr
            );

        if (!confirm)
          return;
      }
      
      Class cls = getDomainClass();
      Oid id = dodm.getDom().lookUpObjectId(cls, obj);

      /*
       * v2.7.2: perform post-delete for both top-level and child containers
       * if (parent != null) { onDeleteObject(obj); }
       */
      onDeleteObject(obj);

      // v2.6.2
      // browser.updateOnDelete(obj, currentIndex);
      boolean browsingEnabled = controller.getMainController()
          .isBrowsingEnabled() && isBrowserOpened();
      if (browsingEnabled)
        updateBrowserOnDelete(id, obj);

      if (!isBrowserOpened()) {// isBufferEmpty()) {//objectBuffer.size() ==
                               // 0) {
        // browser is not opened: reset current object and clear GUIs
        currentObj = null;

        /**
         * v2.5.4: Bug: clearGUI() here does not work correctly with
         * JDataTable because this container automatically updates the display
         * and there is no need to clear the GUI afterwards. Solution: use a
         * different method name to avoid invoking clearGUI.
         */
        clearGUIOnDelete();

        // v2.6.4.a: also clear all children containers (if any)
        clearChildren();

//        // also update the parent GUI (if any)
//        if (parent != null) {
//          parent.updateGUI(null);
//        }
      } else if (obj == currentObj) {
        // browser is opened and deleted obj is the current object
        // -> browse away to another object (if any)
        if (browseAwayIfCurrent) {
          C o = browser.getCurrentObject();
          if (o != null) {
            setCurrentObject(o, true);
            /*
             * v2.7.2: TODO: make it flexible here to decide what to do with the
             * children containers setCurrentObject(o, false); updateGUI(true);
             */
          }
        }
      }
    }

    /**
     * @effects if <code>currentObj != null</code> remove it from the object
     *          pool and from the database.
     * 
     */
    public C deleteObject() throws DataSourceException,
        NotPossibleException, NotFoundException {
      if (currentObj != null) {
        deleteObject(currentObj, true);
        return currentObj;
      } else {
        return null;
      }
    }

    /**
     * @requires object != null /\ object.class = C
     * @effects if toConfirm ask user to confirm deletion if user does not
     *          confirm return remove object from each parent (if any) and if
     *          object depend on a parent delete object from the object pool and
     *          database else update object inform other data controllers of the
     *          same type to update accordingly
     */
    public void deleteObject(Object object, boolean toConfirm)
        throws DataSourceException, NotPossibleException, NotFoundException {
      // TODO: unsafe casting
      C obj = (C) object;

      Class cls = getDomainClass();

      DOMBasic dom = getDodm().getDom();
      DSMBasic dsm = getDodm().getDsm();
      
      // TODO: pass in as argument
      Oid id = dom.lookUpObjectId(cls, obj);

      if (obj != null) {  // TODO: v3.3: this check seems unnecessary 
        //v3.0: String objStr = obj.toString();

        if (toConfirm) {
          boolean confirm = controller.displayConfirmFromCode(
              MessageCode.CONFIRM_DELETE_OBJECT, this//, objStr
              );

          if (!confirm)
            return;
        }

        /*
         * TODO: if data field does not perform cardinality constraint check
         * then validate those constraints against the deleted object here
         */

        if (parent != null) {
          /*
           * v2.6: check to make sure that any constraints imposed on the
           * association with parentObj are not violated because of the delete
           */
          DAttr attribute = getLinkAttribute();
          Object parentObj = getParentObject();
          
          // extra check
          preConditionStrict();

          try {
            int currentLinkCount = getLinkCount();
            dom.validateCardinalityConstraintOnDelete(cls, attribute,
                parentObj, currentLinkCount);
          } catch (ConstraintViolationException e) {
            // constraint violated -> warn user and stop if (s)he does not want
            // to proceed
            if (toConfirm
                && !controller
                    .displayWarningFromCode(
                        MessageCode.WARN_ASSOCIATION_CONSTRAINT_VIOLATED_ON_DELETE,
                        this,
                        true, 
                        //parentObj
                        parent.getCreator().getDomainClassLabel()
                        )) {
              // user does not want to proceed -> stop
              return;
            }
          }

          /*
           * if current object is dependent on its parent delete it from the
           * system else remove its link from the parent update the object
           */
          Class parentCls = getParent().getCreator().getDomainClass();
          if (
              /*v3.1: add support for determinant 1-1 association
              dsm.isDependentOn(cls, attribute, parentCls)
              */
              dsm.isDependentOn(cls, attribute, parentCls)
              || dsm.isDeterminedByAssociate(cls, attribute, parentCls)
              || !isUpdateLinkToParent()  // v3.0: added this case
              ) {
            /*
             * to delete: since we are going to delete this object, we need to
             * update all its associations (including the parent)
             */

            // v2.7.2: support returned value; the determined objects to be
            // removed
            Map<Class, Object> determinedObjs = updateAssociatesOnDelete(cls,
                obj);

            /* inform other data controlers of the same type */
            // Controller.this.
            controller.fireObjectDelete(this, id, obj, true);

            /* now delete object */
            dom.deleteObject(obj, id, cls);

            // v2.7.2: support determined objects
            if (determinedObjs != null)
              deleteObjects(determinedObjs, false);
          } else {
            // not dependent on parent AND not is update link to parent
            // remove link to parent ONLY (object not deleted)
            
            updateParentAssociateOnDelete(obj);

            /* to update */
            dom.updateObject(obj, null);
          }

          /*
           * ask the parent controller of this to inform its parents (if any)
           * why? the current object of an ancestor controller may contain
           * derived attribute whose value depends on the state of currentObj;
           * in such case this request causes the attribute's value to be
           * re-computed
           */
          parent.fireObjectChangeByChild();
        } else {
          // top level controller: delete object from the data base
          /*
           * Update all associations between this object and other objects
           */
          // v2.7.2: support returned value; the determined objects to be
          // removed
          Map<Class, Object> determinedObjs = updateAssociatesOnDelete(cls, obj);

          /**
           * for deletion: needs to inform other data controllers of the same
           * type that the object is removed from the pool
           */
          // Controller.this.
          controller.fireObjectDelete(this, id, obj, true);

          /* now delete the object */
          dom.deleteObject(obj, id, cls);

          // v2.7.2: support determined objects
          if (determinedObjs != null)
            deleteObjects(determinedObjs, false);
        }

        // perform any post-deletion tasks
        /* v2.5.4: */
        /*
         * v2.7.2: perform post-delete for both top-level and child containers
         * if (parent != null) { onDeleteObject(obj); }
         */
        onDeleteObject(obj);

        // v2.6.2
        // browser.updateOnDelete(obj, currentIndex);
        boolean browsingEnabled = controller.getMainController()
            .isBrowsingEnabled() && isBrowserOpened();
        if (browsingEnabled)
          updateBrowserOnDelete(id, obj);

        if (!isBrowserOpened()) {// isBufferEmpty()) {//objectBuffer.size() ==
                                 // 0) {
          // browser is not opened: reset current object and clear GUIs
          currentObj = null;

          /**
           * v2.5.4: Bug: clearGUI() here does not work correctly with
           * JDataTable because this container automatically updates the display
           * and there is no need to clear the GUI afterwards. Solution: use a
           * different method name to avoid invoking clearGUI.
           */
          clearGUIOnDelete();

          // v2.6.4.a: also clear all children containers (if any)
          clearChildren();

          // also update the parent GUI (if any)
          if (parent != null) {
            parent.updateGUI(null);
          }
        } else if (obj == currentObj) {
          // browser is opened and deleted obj is the current object
          // -> browse away to another object (if any)
          // v2.6.2
          // browser.refresh();
          C o = browser.getCurrentObject();
          if (o != null) {
            setCurrentObject(o, true);
            /*
             * v2.7.2: TODO: make it flexible here to decide what to do with the
             * children containers setCurrentObject(o, false); updateGUI(true);
             */
          }
        }

        String mesg = null;
        if (controller.getProperties().getBooleanValue("show.message.popup",
            true)) {
          mesg = controller.displayMessageFromCode(MessageCode.OBJECT_DELETED, this,
              //objStr
              controller.getDomainClassLabel()
              );
        }

        AppState state = AppState.Deleted;

        setCurrentState(state, mesg);

        // fire the state change event
        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, obj);
        }
      }
    }

    /**
     * @effects
     *  performs post-delete tasks on <tt>obj</tt> when it is deleted 
     *
     * @version 
     * - 3.3: added support for post-delete command 
     */
    public void onDeleteObject(C obj) {
      // v3.3: do command (if any)
      doOnDeleteCommand(obj);
    }

    /**
     * @effects 
     *  perform post-delete command on <tt>obj</tt>, if it is specified in the configuration
     * @version 3.3
     */
    protected void doOnDeleteCommand(C obj) {
      // v3.3: added support for post-delete command
      DataControllerCommand cmd = lookUpCommand(LAName.OnDeleteObject.name());
      
      if (cmd != null) {
        try {
          cmd.execute(this, obj);
        } catch (Exception e) {
          if (e instanceof NotPossibleException)
            throw (NotPossibleException) e;
          else 
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_POST_DELETE_OBJECT, e);
        }
      }      
    }

    /**
     * @requires objs != null
     * @effects remove the objects in <tt>objs</tt>
     * @version 2.7.2
     */
    private void deleteObjects(Map<Class, Object> objs, boolean confirm)
        throws NotPossibleException, NotFoundException, DataSourceException {
      Class c;
      Object o;
      Oid oid;
      ControllerBasic.DataController dctl;
      for (Entry<Class, Object> e : objs.entrySet()) {
        c = e.getKey();
        o = e.getValue();
        dctl = lookUpRootDataController(c, o);
        if (dctl != null) {
          // delete using the dctl of the type
          // turn off messages
          Object propVal = dctl.getCreator().setProperty("show.message.popup",
              false);
          dctl.deleteObject(o, confirm);
          dctl.getCreator().setProperty("show.message.popup", propVal); // reset
        } else { // no dctl found
          // delete using the schema
          oid = dodm.getDom().lookUpObjectId(c, o);
          dodm.getDom().deleteObject(o, oid, c);
        }
      }
    }

    /**
     * This method is invoked by other <code>DataController</code> objects to
     * force an update of <code>this.currentObjects</code> when their own have
     * been changed by <code>deleteObject</code>.
     * 
     * @effects if <code>obj</code> is an instance of <code>this.cls</code>,
     *          <code>this.objectBuffer != null</code> and <code>obj</code>
     *          satisfies {@link #getParentObjectQuery()} then remove it from
     *          <code>this.objectBuffer</code>
     */
    protected void causeDelete(Oid id, C deletedObj) throws DataSourceException {
      if (isBrowserOpened()) {
        /*
         * v2.6.4.a: improved to support report data controllers boolean
         * isInParentBuffer = (parentQuery != null && parentQuery.eval(schema,
         * deletedObj)); if (parentQuery == null || isInParentBuffer) {
         */
        /*
         * v2.6.4.b: added support for 1:1 association in which parent is the
         * determinant 
         * Query parentQuery = getParentObjectQuery(); if (parent !=
         * null && // a child controller (parentQuery == null || // no parent
         * query (e.g report) !parentQuery.eval(schema, deletedObj)) // obj not
         * valid for this buffer ) { // object not suitable for this controller
         * return; }
         */
        if (parent != null) { // a child controller
          if (isUpdateLinkToParent()) { // v3.0: added this check
            Query parentQuery = getParentObjectQuery(true  // strict
                );
            if (parentQuery != null && !parentQuery.eval(dodm, deletedObj)) {
              // object not suitable for this controller
              return;
            } else if (parentQuery == null && !isDeterminedByParent()) {
              // this is either not determined by parent in a 1:1 association
              // or is so via a different link attribute as obj's to its parent
              return;
            }
          }
        }

        // update the GUI on delete
        onDeleteObject(deletedObj);

        // update browser on delete
        // browser.updateOnDelete(deletedObj);
        boolean browsingEnabled = controller.getMainController()
            .isBrowsingEnabled() && isBrowserOpened();
        if (browsingEnabled)
          updateBrowserOnDelete(id, deletedObj);

        // if the user is viewing the deleted object then refresh
        if (deletedObj == currentObj) {
          if (browsingEnabled) {
            // refresh to show the current object
            // browser.refresh();
            C o = browser.getCurrentObject();
            setCurrentObject(o, true);
            // v2.6.4b: needs to clear GUI if o is null
            if (o == null)
              clearGUIOnDelete();
          } else {
            // clear
            currentObj = null;
            // TODO: should we use clearGUIOnDelete() here instead?
            clearGUI();
          }
        }

        // v2.7.3: if there is a parent then inform it (similar to when
        // deleteObject was invoked)
        // if (parent != null) {
        // deleteAssociationLinkToParent(id, deletedObj);
        // }

        setCurrentState(AppState.CauseDeleted);
      }
    }

    // /**
    // * This method is <b>ONLY</b> invoked by child controllers other than the
    // source child controller
    // * that caused an update to the <tt>child</b> object.
    // *
    // * @requires
    // * parent != null
    // * @effects
    // * update parent object to remove the association link to <tt>child</tt>
    // * @version 2.7.3
    // */
    // private void deleteAssociationLinkToParent(Oid id, C child) throws
    // NotFoundException, NotPossibleException, DataSourceException {
    // // remove link to parent
    // updateParentAssociateOnDelete(child);
    //
    // /* TODO: check this assumption
    // * NO need to update child object here because it will be removed anyway
    // ?!?
    // dodm.getDom().updateObject(child, null);
    // */
    //
    // /*
    // * ask the parent controller of this to inform its parents (if any)
    // * why? the current object of an ancestor controller may contain derived
    // attribute whose value
    // * depends on the state of currentObj; in such case this request
    // * causes the attribute's value to be re-computed
    // */
    // parent.fireObjectChangeByChild();
    // }

    /**
     * This method is used by child controllers to update the links of the
     * current object of this with the objects managed by the child controller
     * 
     * @requires attribute != null /\ currentObj != null
     * @effects update the value of <tt>attribute</tt> of
     *          <tt>this.currentObj</tt> with the linked objects in
     *          <tt>linkedObjs</tt> and if the state of currentObj is changed
     *          then update the GUI.
     * 
     *          <p>
     *          Throws NotFoundException if attribute is not found;
     *          NotPossibleException if fails to update the attribute
     */
    private void updateLinkedAttributeValue(String attribute,
        Collection linkedObjs) throws NotFoundException, NotPossibleException {
      boolean updated = dodm.getDom().setAttributeValue(currentObj, attribute,
          linkedObjs);

      if (updated) {
        updateGUI(null);
      }
    }

    /**
     * This method is used by child controllers to update the current object of
     * this
     * 
     * @requires attribute != null /\ is an attribute of currentObj.class /\
     *           currentObj != null
     * @effects update the value of <tt>attribute</tt> of
     *          <tt>this.currentObj</tt> with the new value <tt>val</tt> (which
     *          can be a domain-type or collection-type attribute); and if
     *          <tt>currentObj</tt>'s state was changed then save to storage.
     * 
     *          Throws NotFoundException if attribute is not found;
     *          NotPossibleException if fails to update the attribute
     * 
     * @version 2.7.3: renamed to updateAssociationLink
     */
    public boolean updateAssociationLink(DAttr attribute, Object val)
        throws NotFoundException, NotPossibleException {
      DOMBasic dom = dodm.getDom();

      boolean updated = dom.updateAssociateLink(currentObj, attribute, val);

      if (updated) {
        // save changes
        try {
          // v2.7.4: moved to method
          // dom.updateObject(currentObj, null);
          updateObjectInDataSource(currentObj, null, attribute);
        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
              "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
              "updateAttributeValue", currentObj);
        }
      }

      return updated;
    }

    /**
     * This method is used by child controllers to add a new assocation link
     * from its current object (which may be <b>newly created</b>) to the
     * current object of this. This method differs from
     * {@link #updateAssociationLink(String, Object)} in that the later method
     * is used to add an <i>existing</i> association link to the parent.
     * 
     * @requires attribute != null /\ currentObj != null
     * @effects update the value of <tt>attribute</tt> of
     *          <tt>this.currentObj</tt> with the new value <tt>val</tt> (which
     *          can be a domain-type or collection-type attribute); and if
     *          <tt>currentObj</tt>'s state was changed then save to storage.
     * 
     *          Throws NotFoundException if attribute is not found;
     *          NotPossibleException if fails to update the attribute
     * 
     * @version 2.7.3
     */
    private boolean addAssociationLink(DAttr attribute, Object val)
        throws NotFoundException, NotPossibleException {
      DOMBasic dom = dodm.getDom();
      boolean updated = dom.addAssociateLink(currentObj, attribute, val);

      if (updated) {
        // save changes
        try {
          // v2.7.4: moved to method
          // dom.updateObject(currentObj, null);
          updateObjectInDataSource(currentObj, null, attribute);

        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
              "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
              "updateAttributeValue", currentObj);
        }
      }

      return updated;
    }

    /**
     * @effects update all objects associated to deletedObj to reflect the fact
     *          that deletedObj is removed from the system.
     * 
     * @version 2.7.2 updated to support the case in which c is the determinant
     *          of an associated class (d) return Map of objects associated to t
     *          via a determinant association so that they can be removed after
     *          t
     */
    private Map<Class, Object> updateAssociatesOnDelete(final Class c,
        final Object t) throws DataSourceException, NotFoundException,
        NotPossibleException {
      /*
       * let t = deletedObj let Table(x) be the database table of the domain
       * class x card(a,n) be the cardinality constraint of a domain class a in
       * an association n
       * 
       * for each association n(c:a,d:b) of an attribute c:a that associates to
       * another attribute d:b (d may eq c) if card(c,n)=card(d,n) = 1:1 /\ c is
       * the determinant let o be in Objects(d) s.t. n(t,o) set t.a = null
       * remove o from data source else let O(d) be a sub-set of Objects(d) s.t.
       * for all o in O(d). n(t,o) for each o in O(d) if d depends-on c remove o
       * [without updating Table(d)] else if card(c,n)=1 set o.b = null [without
       * updating Table(d)] else if card(c,n)=M remove t from o.b [without
       * updating Table(d)]
       * 
       * let S(d) be a sub-set of records in Table(d) s.t. for all r in S(d).
       * n(t,r) for each r in S(d) if d depends-on c remove r else if
       * card(c,n)=1 set r.b = null
       */
      Map<DAttr, DAssoc> associations = dodm.getDsm()
          .getAssociations(c);

      if (associations == null)
        return null;

      DAttr a, b;
      Class d;
      Tuple2<DAttr, DAssoc> yourAssocTuple;
      DAssoc n, m;
      boolean youDependsOnMe;
      Object Od;

      Map<Class, Object> determinedObjs = null; // v2.7.2

      for (Entry<DAttr, DAssoc> e : associations.entrySet()) {
        a = e.getKey();
        n = e.getValue();

        try {
          // get d's end
          d = n.associate().type();
          yourAssocTuple = dodm.getDsm().getTargetAssociation(n);
          b = yourAssocTuple.getFirst();
          m = yourAssocTuple.getSecond();

          // v2.7.2: add support for c is the determinant of d, i.e.
          // (m.associate.determinant = true)
          if (m.associate().determinant()) { // c is the determinant of d
                                             // (assumes: n is 1:1)
            Od = dodm.getDsm().getAttributeValue(t, a.name()); // n(t,o)
            if (Od != null) {
              dodm.getDom().setAttributeValue(t, a.name(), null); // set t.a =
                                                                  // null
              // return o in the result so that it can be removed
              // when t is removed
              if (determinedObjs == null)
                determinedObjs = new HashMap<>();
              determinedObjs.put(d, Od);
            }
          } else {
            // c is not the determinant
            // TODO: assume no Many-To-Many associations
            youDependsOnMe = dodm.getDsm().isDependentOn(d, m);

            // if associate is a collection-type then find in the object-pool
            // all the objects that are not yet added in associate (because they
            // have not
            // be browsed to)
            if (a.type().isCollection()) {
              /*
               * find all those matching myObj in the object pool of yourCls
               * Note: NOT necessary to look up for these in the data source
               * because they are not of interest to the caller of this method
               */
              Query q = new Query();
              q.add(new ObjectExpression(d, b, Op.EQ, t));
              Od = dodm.getDom().getObjects(d, q);
            } else {
              Od = dodm.getDsm().getAttributeValue(t, a.name());
            }

            if (Od != null) {
              updateAssociatesOnDelete(t, c, n.ascType(), n.endType(), a, // v2.7.2
                  Od, d, b, // v2.7.2
                  youDependsOnMe);
            }

            // update the associated object if its link attribute is
            // serialisable
            if (b.serialisable() == true)
              updateDataSourceOnDelete(t, c, n, d, b, youDependsOnMe);
          }
        } // v2.7.2: catch (NotFoundException nfe) {
        catch (NotPossibleException | NotFoundException npe) {
          // ignore: association not found OR no attribute getter method
        }
      } // end for

      return determinedObjs;
    }

    private void updateAssociatesOnDelete(Object me, Class c,
        AssocType assocType, AssocEndType myEndType, DAttr myAttrib, // v2.7.2
        Object you, Class d, DAttr yourAttrib, // v2.7.2
        boolean youDependsOnMe) throws DataSourceException {

      DataController topDctl = lookUpRootDataController(d, you);

      if (topDctl == null) {
        // no such controller is configured for the domain type-> skip
        return;
      }

      ControllerBasic ctl = topDctl.getCreator();

      Object oldProp = ctl.getProperty("show.message.popup");
      // disable pop-up
      ctl.setProperty("show.message.popup", false);

      boolean updateDs;

      if (!youDependsOnMe) {
        // associate does not depend on deletedObj
        // update associates depending on the association type
        if (assocType == AssocType.One2One) {
          // set linked attribute value of associate to null
          /*
           * v2.7.4: to update data source // without updating the object in the
           * data source
           */
          updateDs = true;
          topDctl.updateOneToOneAssociateOnDelete(you, yourAttrib, null,
              updateDs);
        } else if (assocType == AssocType.One2Many) {
          if (myEndType == AssocEndType.One) {
            // one-to-many
            // set linked attribute of each element of associateObj to null
            /*
             * v2.7.4: to update data source // without updating the object in
             * the data source
             */
            updateDs = true;
            Collection col = (Collection) you;
            for (Object obj : col) {
              topDctl.updateOneToOneAssociateOnDelete(obj, yourAttrib, null,
                  updateDs);
            }
          } else {
            // many-to-one: remove deletedObj from linked attribute value of
            // associate
            // topDctl.updateManyAssociateOnDelete(associateObj, cls,
            // deletedObj);
            Oid associateOid = dodm.getDom().lookUpObjectId(d, you);

            /*
             * v2.7.4: to update data source // without updating the data source
             */
            updateDs = true;
            topDctl.updateManyAssociateOnDelete(you, associateOid, me,
                myAttrib, updateDs);
          }
        }
      } else {
        // associate depends on deletedObj -> delete
        if (assocType == AssocType.One2Many) {
          // far-end is always one
          // delete all elements of associatedObj

          // to be sure...
          if (!(you instanceof List)) {
            throw new NotPossibleException(
                NotPossibleException.Code.INVALID_TARGET_ASSOCIATE_OBJECT_TYPE,
                "Kiểu dữ liệu liên quan {0} không đúng, cần kiểu {1}", you,
                "List");
          }

          List col = (List) you;
          // TODO: not thread-safe because col may be modified by invocation
          // rootDctl.updateManyAssociateOnDelete above
          // temporary solution: use a backward for loop to overcome this
          Object obj;
          for (int i = col.size() - 1; i >= 0; i--) {
            obj = col.get(i);
            // recursive call
            topDctl.deleteObject(obj, false);
          }
        } else {
          // one-to-one
          // delete associatedObj
          // recursive call
          topDctl.deleteObject(you, false);
        }
      }

      // reset property
      ctl.setProperty("show.message.popup", oldProp);
    }

    /**
     * This method is used by associate data controllers to update the objects
     * managed by this controller.
     * 
     * @requires myObj != null /\ associateCls != null /\ attrib is an attribute
     *           of myObj.class
     * @effects update the value of the linked attribute of object
     *          <tt>myObj</tt> whose declared type is <tt>associateClass</tt>
     *          with the new value <tt>val</tt>; and if
     *          <tt>updateDS = true /\ currentObj</tt>'s state was changed then
     *          save to storage.
     * 
     *          Throws NotFoundException if attribute is not found;
     *          NotPossibleException if fails to update the attribute
     */
    // private boolean updateAttributeValue(Object myObj,
    // Class associateClass, Object val)
    // throws NotFoundException, NotPossibleException {
    // //TODO: to use additional role information to avoid the case of multiple
    // links
    // // between the objects
    //
    // boolean updated = schema.updateAttributeValue(myObj, associateClass,
    // val);
    //
    // if (updated) {
    // // save changes
    // try {
    // schema.updateObject(myObj, null);
    // } catch (DBException e) {
    // throw new
    // NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
    // "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
    // "updateAttributeValue", myObj);
    // }
    // }
    //
    // return updated;
    // }
    private boolean updateOneToOneAssociateOnDelete(Object myObj,
    // Class associateClass,
        DAttr attrib, Object val, boolean updateDS)
        throws NotFoundException, NotPossibleException {
      /*
       * v2.7.2: use link attribute to avoid the case of multiple links boolean
       * updated = schema.updateAssociateLink(myObj, associateClass, val);
       */
      boolean updated = dodm.getDom().updateAssociateLink(myObj, attrib, val);

      if (updateDS && updated) {
        // save changes
        try {
          // v2.7.4: moved to method
          // dodm.getDom().updateObject(myObj, null);
          updateObjectInDataSource(myObj, null, attrib);
        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e,
              "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
              "updateAttributeValue", myObj);
        }
      }

      return updated;
    }

    /**
     * This method is used by associate data controllers to update the objects
     * managed by this controller.
     * 
     * @requires myObj != null /\ associateClass /\ null /\ attrib is an
     *           attribute of val.class
     * 
     *           <pre>
     * find the correct association between associate.class and deletedObj.class that is via the attribute 
     * myAttrib of deletedObj.class
     *   if association is 1:M
     *      invoke deleter method of associate to remove deletedObj from the association
     *   else
     *      invoke setter method of associate to set value to null 
     *  
     *  if associate is updated
     *    return true
     *  else
     *    return false
     *    
     *  <p>Throws NotPossibleException if failed to update
     *  
     *  <p>e.g. 
     *  If associate = Student<1>, deletedObj = Enrolment<1>
     *  then
     *    collection-type attribute = Student.enrolments (List<Enrolment>)
     *    delete method = deleteEnrolment (as defined in the Update annotation of the attribute)
     * </pre>
     */
    private boolean updateManyAssociateOnDelete(Object myObj, Oid objId,
    // v2.7.2: Class associateClass,
        Object val, DAttr myAttrib, // v2.7.2
        boolean updateDS) throws NotPossibleException {
      // v2.6: make sure that myObj is fully loaded
      // TODO: not use this
      // schema.loadReferencedObjects(myObj);

      /*
       * v2.7.2: use the new method boolean updated =
       * schema.updateOneToManyAssociateOnDelete(myObj, associateClass, val);
       */
      boolean updated = dodm.getDom().updateAssociateToRemoveLink(myObj, val,
          myAttrib);

      if (updateDS && updated) {
        // save changes
        try {
          // v2.7.4: moved to method
          // dodm.getDom().updateObject(myObj, null);
          updateObjectInDataSourceWithExclusion(myObj, null, val);

        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_PERFORM_METHOD,
              "Không thể thực thi phương thức {0}.{1}({2})", "DataController",
              "updateObject", myObj);
        }
      }

      return updated;
    }

    /**
     * This method is used when an object is deleted to update the parent object
     * of the deleted object via a 1:M associations.
     * 
     * @requires deletedObj != null
     * @effects remove deletedObj from the corresponding collection-type
     *          attribute values of the parent domain-type object.
     * 
     * @example <pre>
     *    if deletedObj = Student<1> and parent = SClass<1>
     *    then 
     *      deletedObj is to be removed from the collection:
     *        SClass.students and its value of attribute Student.sclass
     *        is set to null
     * </pre>
     */
    private void updateParentAssociateOnDelete(C deletedObj)
        throws DataSourceException, NotFoundException, NotPossibleException {
      DAttr linkAttribute = getLinkAttribute(); // dataContainer.getLinkAttribute();
      DOMBasic dom = dodm.getDom();
      boolean parentChanged = dom.updateAssociateToRemoveLink(deletedObj,
          linkAttribute);

      /**
       * TODO: the following update would be very costly, so it is not currently
       * implemented. instead, user should manually refresh each data controller
       * of interest using the Refresh command button
       */
      /*
       * refresh the buffer of all the data controllers (excluding the parent
       * controller (if this is nested)) that are bounded to those domain
       * objects that are updated
       */

      /* save each updated object to database */
      if (parentChanged) {
        // update parent
        // TODO: should we use parent controller for this update?
        // v2.7.4: moved to method
        // dom.updateObject(parent.getCurrentObject(), null);
        updateObjectInDataSourceWithExclusion(parent.getCurrentObject(), null,
            deletedObj);
      }
    }

    private void updateDataSourceOnDelete(Object t, Class me, DAssoc n,
        Class you, DAttr b, boolean youDependsOnMe)
        throws DataSourceException {

      boolean cardOne = (n.ascType() == AssocType.One2One || n.ascType() == AssocType.One2Many);

      // OSM dbt = dodm.getDom().getDBManager();//.getDBManager();

      // the search expression: select all records in you that refers to me(t)
      ObjectExpression searchExp = new ObjectExpression(you, b, Op.EQ, t);
      Query<ObjectExpression> searchQuery = new Query<ObjectExpression>(
          searchExp);

      // System.out.printf("searchQuery: %s%n", searchQuery);

      if (youDependsOnMe) {
        // remove all records in you that refers to t
        dodm.getDom().deleteObjects(you, searchQuery);
      } else {
        // update all records in you that refers to t by setting the values of
        // the concerned FK col b to null
        ObjectExpression updateExp = new ObjectExpression(you, b, Op.EQ, null);
        Query<ObjectExpression> updateQuery = new Query<ObjectExpression>(
            updateExp);

        // System.out.printf("updateQuery: %s%n", updateQuery);

        dodm.getDom().updateObjects(you, searchQuery, updateQuery);
      }
    }

    /**
     * This method is used to look up the top-level data controller responsible
     * for the domain objects of a given domain class. It is used to delegate
     * event-handling tasks to that object.
     * 
     * @effects if exists top-level data controller of <tt>c</tt> return it else
     *          look up and return the top-level data controller of
     *          <tt>o.class</tt> (which could be a sub-class of <tt>c</tt>);
     *          return <tt>null</tt> if not found.
     * 
     */
    private DataController lookUpRootDataController(Class c, Object o) {
      ControllerBasic ctl;
      DataController topDctl;
      Object oldProp;

      ctl = lookUp(c, ControllerLookUpPolicy.PrimaryOnly);
      if (ctl == null) {
        // throw new NotFoundException(
        // NotFoundException.Code.CONTROLLER_NOT_FOUND,
        // "Không tìm thấy đối tượng điều khiển (Controller) {0}",
        // c);
        return null;
      }

      // get the top-level data controller
      topDctl = (DataController) ctl.getRootDataController();
      if (topDctl == null) {
        // perhaps d is an abstract super class of associated object
        // which does not have a configured data controller.
        // Try looking up the data controller for the domain class of the
        // associated object
        topDctl = controller.getDataController(o.getClass());
      }

      return topDctl;
    }

    // /**
    // * @effects if the current object index is greater than 0 then returns
    // * <code>true</code>, else returns <code>false</code>
    // */
    // protected boolean hasPrevious() {
    // return browser.hasPrevious();
    // // int currIndex = getObjectIndex(currentObj);
    // //
    // // return (currIndex > 0);
    // }

    // /**
    // * @effects if <code>this.objectList.size() > 0</code>, initialises
    // * <code>this.currentObj</code> to the object <b>before</b> it in
    // * <code>objectList</code>. If there are no such object then do
    // * nothing.
    // *
    // * <p>
    // * Updates the data container
    // */
    // protected void previous() {
    // // if (!preCondition())
    // // return;
    //
    // browser.previous();
    // // if (!isEmpty()) { //(objectBuffer != null && !objectBuffer.isEmpty())
    // {
    // // if (currentObj == null) {
    // // currentObj = getObject(0); //objectBuffer.get(0);
    // // updateGUI();
    // //
    // // setCurrentState(AppState.Previous);
    // // } else {
    // // // move to the current object
    // // int i = getObjectIndex(currentObj);
    // //objectBuffer.indexOf(currentObj);
    // // if (i > 0) {
    // // i--;
    // // currentObj = getObject(i);//objectBuffer.get(i);
    // // updateGUI();
    // // }
    // //
    // // if (i == 0) {
    // // setCurrentState(AppState.First);
    // // } else {
    // // setCurrentState(AppState.Previous);
    // // }
    // // }
    // // }
    // }

    // /**
    // * @effects if <code>preCondition()</code> is met and
    // * <code>objectBuffer</code> is not empty then sets
    // * <code>currentObj</code> to first object in
    // * <code>objectBuffer</code> and updates the data container.
    // */
    // protected void first() {
    // // if (!preCondition())
    // // return;
    //
    // browser.first();
    // // if (!isEmpty()){ //(objectBuffer != null && !objectBuffer.isEmpty()) {
    // // C first = getObject(0); //objectBuffer.get(0);
    // //
    // // if (currentObj != first) {
    // // currentObj = first;
    // // updateGUI();
    // // }
    // // }
    // //
    // // setCurrentState(AppState.First);
    // }

    // /**
    // * @effects if the current object index is less than the last index in
    // * <code>this</code> then returns <code>true</code>, else returns
    // * <code>false</code>
    // */
    // protected boolean hasNext() {
    // return browser.hasNext();
    // // int currIndex = getObjectIndex(currentObj);
    // //
    // // return (currIndex > -1) && (currIndex < getBufferSize()-1);
    // //objectBuffer.size() - 1);
    // }

    // /**
    // * @effects if <code>this.objectList.size() > 0</code>, initialises
    // * <code>this.currentObj</code> to the object <b>after</b> it in
    // * <code>objectList</code>. If there are no more objects, then do
    // * nothing.
    // *
    // * <p>
    // * Updates <code>this.gui</code>.
    // */
    // protected void next() {
    // // if (!preCondition())
    // // return;
    //
    // browser.next();
    //
    // // if (!isEmpty()) { //(objectBuffer != null && !objectBuffer.isEmpty())
    // {
    // // if (currentObj == null) {
    // // currentObj = getObject(0); //objectBuffer.get(0);
    // // updateGUI();
    // //
    // // setCurrentState(AppState.Next);
    // // } else {
    // // // move next
    // // int i = getObjectIndex(currentObj);
    // //objectBuffer.indexOf(currentObj);
    // // int buffSize = getBufferSize();
    // // if (i < buffSize-1) {//objectBuffer.size() - 1) {
    // // i++;
    // // currentObj = getObject(i); //objectBuffer.get(i);
    // // updateGUI();
    // // }
    // //
    // // if (i == buffSize-1) { //objectBuffer.size() - 1) {
    // // setCurrentState(AppState.Last);
    // // } else {
    // // setCurrentState(AppState.Next);
    // // }
    // // }
    // // }
    // } // end next

    // /**
    // * @effects if <code>preCondition()</code> is met and
    // * <code>objectBuffer</code> is not empty then sets
    // * <code>currentObj</code> to last object in
    // * <code>objectBuffer</code> and updates the data container.
    // */
    // protected void last() {
    // // if (!preCondition())
    // // return;
    //
    // browser.last();
    // // if (!isEmpty()) { //(objectBuffer != null && !objectBuffer.isEmpty())
    // {
    // // C last = getObject(getBufferSize()-1);
    // //objectBuffer.get(objectBuffer.size() - 1);
    // //
    // // if (currentObj != last) {
    // // currentObj = last;
    // // updateGUI();
    // // }
    // // }
    // //
    // // setCurrentState(AppState.Last);
    // }

    /**
     * @requires dataContainer is a top-level container
     * 
     * @effects if compact = true show a compact view of the function GUI
     *          containing the data container of this else show a full view of
     *          the GUI
     */
    protected void displayGUI(boolean compact) {
      user.showGUI(compact);
    }

    /**
     * This works similar to {@link #clear(boolean)}(true) except that it only removes objects 
     * from object pool if the domain class's object pool is not configured to be constant.
     *  
     * @effects <pre> 
     *    set {@link #currentObj} = null
     *    if this.domainClass is NOT object-pool-constant 
     *      clear the object pool of the domain class that is managed by this
     *    call {@link #clearBrowser()}
     *    call {@link #clearDataSource()}       
     *  </pre>
     * @version 3.3
     */
    protected void clearOnClose() {
      if (!controller.isSingleton()) {
        currentObj = null;
        
        //if (deep) { deep = true
          // clear any existing objects
        Class domainCls = getDomainClass();
        if (!DSMBasic.isConstantObjectPool(domainCls)) {
          try {
            dodm.getDom().deleteObjects(domainCls, false);
            
            //System.out.println("DataController.clearOnClose: " + domainCls + " object pool cleared");
          } catch (DataSourceException e) {
            // should not happen
          }
        //}
        }
        clearBrowser(); // objectBuffer = null;
        clearDataSource();
      }
    }
    
    /**
     * @effects 
     * clear the GUI;  
     * clear data resources associated to this;
     * clear children (if any)
     * @version 2.7.3
     */
    void clearAll() {
      clearGUI(true);
      clear(false);

      // clear children (if any)
      clearChildren();
    }

    /**
     * <b>IMPORTANT!!!!:</b> BE CAREFUL of calling this method with deep=true. It seems that this parameter
     * is not needed because most invocations of this method only requires deep=false. 
     * 
     * <p>For the effect of deep=true, see {@link #clearOnClose()}. 
     * 
     * @effects <pre> 
     *    set {@link #currentObj} = null
     *    if <tt>deep = true</tt> 
     *      clear the object pool of the domain class that is managed by this
     *    call {@link #clearBrowser()}
     *    call {@link #clearDataSource()}       
     *  </pre>
     * 
     */
    protected void clear(boolean deep) {
      if (!controller.isSingleton()) {
        currentObj = null;
        
        if (deep) {
          // clear any existing objects
          try {
            dodm.getDom().deleteObjects(getDomainClass(), false);
          } catch (DataSourceException e) {
            // should not happen
          }
        }

        clearBrowser(); // objectBuffer = null;
        clearDataSource();
      }
    }

    /**
     * @requires this.currentObj != null
     * 
     * @effects if there are resources (e.g. indices) associated to this.cls
     *          remove them else do nothing
     * @version 
     *  - 2.7.2
     *  - 3.0: add param recursive 
     */
    public void clearDomainClassResources(boolean recursive) {
      /*v3.0: moved to method 
      // reset index counter maped to this controller
      if (isIndexable()
      // && currentObj != null // requires a current object
      ) {
        // ((Indexable) currentObj).resetIndexCounter(this);
        setCurrentStateSimple(AppState.OnClearDomainClassResources, null);
      }
      */
      resetIndexCounter();
      
      if (recursive) {
        Iterator<DataController> childDctls = getChildControllersIterator();
        if (childDctls != null) {
          DataController child;
          while (childDctls.hasNext()) {
            child = childDctls.next();
            child.clearDomainClassResources(recursive);
          }
        }
      }
    }

    /**
     * This is invoked as part of {@link #clearGUI(boolean)}
     * 
     * @version 3.0
     */
    private void clearDomainResourcesOnGUICleared() {
      resetIndexCounter();
    }
    
    /**
     * @effects 
     *  if this is indexable
     *    reset index counter mapped to this
     *  else
     *    do nothing
     *    
     * @version 3.0
     */
    public void resetIndexCounter() {
      // reset index counter maped to this controller
      if (isIndexable()) {
        setCurrentStateSimple(AppState.OnClearDomainClassResources, null);
      }
    }

    /**
     * This method is implemented differently by different data controllers,
     * depending on the behaviours of their data containers in deletion.
     * 
     * <p>
     * DataPanelController treats this the same as <tt>clearGUI</tt> while
     * TableDataController does nothing since tables automatically updates the
     * display on deletion.
     * 
     * @effects clear the GUI on deletion of the current object
     */
    protected void clearGUIOnDelete() {
      // empty
    }

    /**
     * @requires 
     *  dataContainer != null /\ exclAttribs != null
     * @effects 
     *  clear all data components of <tt>this.dataContainer</tt> that are mapped to
     *  ALL domain attributes except those in <tt>exclAttribs</tt>, and if any of these are sub-forms then 
     *  recursively clear the GUIs of them and those of their descendants
     * @version 3.1
     */
    public void clearGUIOnlyExceptFor(Collection<DAttr> exclAttribs) {
      clearGUIOnlyExceptFor(exclAttribs, true);
    }
    
    /**
     * @requires 
     *  dataContainer != null /\ exclAttribs != null
     * @effects 
     *  clear all data components of <tt>this.dataContainer</tt> that are mapped to
     *  ALL domain attributes except those in <tt>exclAttribs</tt>, and 
     *  if <tt>withChildren = true</tt> and any of these are sub-forms then 
     *  recursively clear the GUIs of them and those of their descendants
     * @version 3.1
     */
    public void clearGUIOnlyExceptFor(Collection<DAttr> exclAttribs, boolean withChildren) {
      if (dataContainer == null)
        return;

      DataContainerToolkit.clearExceptFor(dataContainer, exclAttribs, withChildren);
    }

    /**
     * This method differs from {@link #clearGUI()} in that it does not clear 
     * the associated domain resources.
     * 
     * @effects 
     *  clears <b>just the</b> GUI of <code>this.dataContainer</code> and 
     *  those of all the decendant data containers (if any)
     * @version 3.1
     */
    public void clearGUIOnly() {
      clearGUIOnly(true);
    }
    
    /**
     * This method differs from {@link #clearGUI(boolean)} in that it does not clear 
     * the associated domain resources.
     * 
     * @effects 
     *  clears <b>just the</b> GUI of <code>this.dataContainer</code> and 
     *  if <tt>withChildren = true</tt> then also  those of all
     *  the decendant data containers (if any)
     * @version 3.1
     */
    public void clearGUIOnly(boolean withChildren) {
      // v 2.6.1: add this check
      if (dataContainer == null)
        return;

      dataContainer.clear();

      if (withChildren) {
        // also clear children GUIs if any
        List<ControllerBasic.DataController> children = getChildControllers();
        if (children != null) {
          for (DataController dc : children) {
            dc.clearGUIOnly(true);
          }
        }
      }
    }
    
    /**
     * @effects 
     *    clears GUI and the related 
     *    domain resources of <code>this.dataContainer</code> and those of all the decendant data containers (if any)
     */
    protected void clearGUI() {
      clearGUI(true);
    }

    /**
     * @effects 
     *    clear GUI and related domain resources of <code>this.dataContainer</code> and if
     *    <tt>withChildren = true</tt> then also of all the
     *    decendant data containers (recursively)
     */
    protected void clearGUI(boolean withChildren) {
      // v 2.6.1: add this check
      if (dataContainer == null)
        return;

      // System.out.printf("%s.clearGUI(%b)%n",this,withChildren);

      dataContainer.clear();

      // v3.0: clear domain resources associated to the data container 
      clearDomainResourcesOnGUICleared();
      
      if (withChildren) {
        // also clear children GUIs if any
        List<ControllerBasic.DataController> children = getChildControllers();
        if (children != null) {
          for (DataController dc : children) {
            dc.clearGUI(true);
          }
        }
      }
    }

    /**
     * @effects reset GUI of <code>this.dataContainer</code> and those of all
     *          the decendant data containers (if any)
     * @version 2.7.4
     */
    protected void resetGUI() {
      resetGUI(true);
    }

    /**
     * @effects reset GUI of <code>this.dataContainer</code> and if
     *          <tt>withChildren = true</tt> then also the GUIs of all the
     *          descendant data containers (recursively).
     *          
     * @version 2.7.4
     */
    protected void resetGUI(boolean withChildren) {
      // v 2.6.1: add this check
      if (dataContainer == null)
        return;

      dataContainer.reset();

      if (withChildren) {
        // also clear children GUIs if any
        List<ControllerBasic.DataController> children = getChildControllers();
        if (children != null) {
          for (DataController dc : children) {
            dc.resetGUI(true);
          }
        }
      }
    }
    
    /**
     * @effects wait for all tasks to complete (or until a time threshold is
     *          reached)
     */
    void stopTaskMan() {
      final int waitTime = 3; // secs
      taskMan.waitForAll(waitTime);
    }

    /**
     * @effects return this.browser
     */
    public ObjectBrowser<C> getBrowser() {
      return browser;
    }

    /**
     * @effects 
     *  if browser != null
     *    return state of this.browser as String
     *  else
     *    return null
     *    
     * @version 3.1
     */
    public String getBrowserStateAsString() {
      if (browser != null)
        return browser.getBrowserStateAsString();
      else
        return null;
    }
    
    public void first() throws DataSourceException, NotFoundException {
      if (isBrowserOpened()) { // v3.2: added this check because this is a public method
        boolean forceToIndex = false;
        first(true, true
            // waitToFinish (same as updateCurrentObject)
            //v3.2c: , false
            , true
            , forceToIndex);
      }
    }

    // protected void first(boolean updateCurrentObject) throws DBException,
    // NotFoundException {
    // first(updateCurrentObject, true);
    // }

    /**
     * @see {@link MethodName#firstAndWait}
     */
    public void firstAndWait() throws NotFoundException, DataSourceException {
      if (isBrowserOpened()) { // v3.2: added this check because this is a public method
        boolean forceToIndex = false;
        first(true, true, true, forceToIndex);
      }
    }

    /*v3.0: moved to another method 
    protected void first(boolean updateCurrentObject, boolean fireStateChange,
        boolean waitToFinish) throws DataSourceException, NotFoundException {
      // move first in browser without firing state change event,
      // this will be fired by this method (at the end)
      if (debug)
        controller.log(MessageCode.UNDEFINED, "{0}.first(..)", this);

      boolean inCache = browser.first(false);

      // v2.7.2: support indexable objects
      if (isIndexable())
        updateObjectIndex(inCache, browser.getCurrentObject());

      // update state and fire the state change event
      AppState state = AppState.First;

      // for sub-classes
      onBrowserStateChanged(browser, state, browser.getCurrentObject(),
          !inCache);

      if (updateCurrentObject)
        setCurrentObjectAfterBrowsing(waitToFinish);

      if (fireStateChange) {
        setCurrentState(state);

        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, currentObj);
        }
      }
    }
      */
      
    /**
     * @effects 
     *  moves to first object in {@link #browser}
     *  
     *  <p>Note: <tt>updateCurrentObject</tt> and <tt>waitToFinish</tt> have close correspondence! (they should normally take the same value)
     *  
     * @version 3.0
     */    
    protected void first(boolean updateCurrentObject, boolean fireStateChange,
        boolean waitToFinish, 
        boolean forceToIndex    // v3.0
        ) throws DataSourceException, NotFoundException {
      // move first in browser without firing state change event,
      // this will be fired by this method (at the end)
      if (debug) {
        controller.log("%s.first(..)", this);
      }

      final boolean inCache = browser.first(false);

      // v2.7.2: support indexable objects
      if (isIndexable()) {
        // v3.0: support forceToIndex 
        boolean toIndex = (forceToIndex) ? true: !inCache;
        updateObjectIndex(toIndex, browser.getCurrentObject());
      }

      // update state and fire the state change event
      AppState state = AppState.First;

      // for sub-classes
      onBrowserStateChanged(browser, state, browser.getCurrentObject(),
          !inCache);

      if (updateCurrentObject)
        setCurrentObjectAfterBrowsing(waitToFinish);

      if (fireStateChange) {
        setCurrentState(state);

        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, currentObj);
        }
      }
    }
    
    /**
     * @effects 
     *  moves to last object in {@link #browser}
     *  
     *  <p>Note: <tt>updateCurrentObject</tt> and <tt>waitToFinish</tt> have close correspondence! (they should normally take the same value)
     *  
     * @version 3.0
     */    
    protected void last(boolean updateCurrentObject, boolean fireStateChange,
        boolean waitToFinish
        , boolean forceToIndex  // v3.0
        ) throws DataSourceException, NotFoundException {
      boolean inCache = browser.last(false);

      // v2.7.2: support indexable objects
      // if (!inCache) {
      // Boolean indexable = getIndexable();
      // if (indexable != null && indexable) {
      // // a newly load object, updates its index
      // Indexable obj = (Indexable) browser.getCurrentObject();
      // obj.setIndex(this, null);
      // }
      // }
      if (isIndexable()) {
        // v3.0: support forceToIndex
        //boolean toIndex = !inCache;
        boolean toIndex = (forceToIndex) ? true: !inCache;
        
        updateObjectIndex(toIndex, browser.getCurrentObject());
      }

      // update state and fire the state change event
      AppState state = AppState.Last;

      // for sub-classes
      onBrowserStateChanged(browser, state, browser.getCurrentObject(),
          !inCache);

      if (updateCurrentObject)
        setCurrentObjectAfterBrowsing(waitToFinish);

      if (fireStateChange) {
        setCurrentState(state);

        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, currentObj);
        }
      }
    }

    /*v3.0: moved to another method 
    protected void next(boolean updateCurrentObject, boolean fireStateChange,
        boolean waitToFinish) throws DataSourceException, NotFoundException {
      boolean inCache = browser.next(false);

      // v2.7.2: support indexable objects
      if (isIndexable())
        updateObjectIndex(inCache, browser.getCurrentObject());

      // update state and fire the state change event
      AppState state;

      if (browser.isLast()) {
        state = AppState.Last;
      } else {
        state = AppState.Next;
      }

      // for sub-classes
      onBrowserStateChanged(browser, state, browser.getCurrentObject(),
          !inCache);

      if (updateCurrentObject)
        setCurrentObjectAfterBrowsing(waitToFinish);

      if (fireStateChange) {
        setCurrentState(state);

        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, currentObj);
        }
      }
      boolean forceToIndex = false;
      next(updateCurrentObject, fireStateChange, waitToFinish, forceToIndex);
    }
      */

    /**
     * @effects 
     *  move to next object in {@link #browser} (if available) and wait 
     *  for resources (incl. {@link #dataContainer} to be updated
     *  
     * @version 3.2
     */
    public void nextAndWait() throws NotFoundException, DataSourceException {
      if (isBrowserOpened()) { // v3.2: added this check because this is a public method
        boolean forceToIndex = false;
        next(true, true, true, forceToIndex);
      }
    }
    
    /**
     * @effects 
     *  moves to next object in {@link #browser}
     *  
     *  <p>Note: <tt>updateCurrentObject</tt> and <tt>waitToFinish</tt> have close correspondence! (they should normally take the same value)
     *  
     * @version 3.0
     */    
    protected void next(boolean updateCurrentObject, boolean fireStateChange,
        boolean waitToFinish,
        boolean forceToIndex   // v3.0
        ) throws DataSourceException, NotFoundException {
      boolean inCache = browser.next(false);

      // v2.7.2: support indexable objects
      if (isIndexable()) {
        // v3.0: support forceToIndex
        boolean toIndex = (forceToIndex) ? true: !inCache;
        updateObjectIndex(toIndex, browser.getCurrentObject());
      }

      // update state and fire the state change event
      AppState state;

      if (browser.isLast()) {
        state = AppState.Last;
      } else {
        state = AppState.Next;
      }

      // for sub-classes
      onBrowserStateChanged(browser, state, browser.getCurrentObject(),
          !inCache);

      if (updateCurrentObject)
        setCurrentObjectAfterBrowsing(waitToFinish);

      if (fireStateChange) {
        setCurrentState(state);

        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, currentObj);
        }
      }
    }
    
    /**
     * @effects 
     *  moves to previous object in {@link #browser}
     *  
     *  <p>Note: <tt>updateCurrentObject</tt> and <tt>waitToFinish</tt> have close correspondence! (they should normally take the same value)
     *  
     * @version 3.0
     */        
    protected void previous(boolean updateCurrentObject,
        boolean fireStateChange, boolean waitToFinish)
        throws DataSourceException, NotFoundException {
      boolean inCache = browser.prev(false);

      // v2.7.2: support indexable objects
      // if (!inCache) {
      // Boolean indexable = getIndexable();
      // if (indexable != null && indexable) {
      // // a newly load object, updates its index
      // Indexable obj = (Indexable) browser.getCurrentObject();
      // obj.setIndex(this, null);
      // }
      // }
      if (isIndexable()) {
        boolean toIndex = !inCache;
        updateObjectIndex(toIndex, browser.getCurrentObject());
      }

      // update state and fire the state change event
      AppState state;

      if (browser.isFirst()) {
        state = AppState.First;
      } else {
        state = AppState.Previous;
      }

      // for sub-classes
      onBrowserStateChanged(browser, state, browser.getCurrentObject(),
          !inCache);

      if (updateCurrentObject)
        setCurrentObjectAfterBrowsing(waitToFinish);

      if (fireStateChange) {
        setCurrentState(state);

        if (methodListenerMap.containsKey(state)) {
          fireMethodPerformed(state, currentObj);
        }
      }
    }

    protected void browseFirstToLast(boolean updateCurrentObject,
        boolean waitToFinish, 
        boolean forceToIndex  // v3.0
        ) throws DataSourceException, NotFoundException {
      first(false, false, false, forceToIndex);
      while (browser.hasNext()) {
        next(false, false, false, forceToIndex);
      }

      if (updateCurrentObject)
        setCurrentObjectAfterBrowsing(waitToFinish);
    }

    /**
     * This method is performed after one of the browsing functions (e.g. {@link #first()}, ...) is performed. 
     * 
     * @effects update current object and the GUI
     * @version 
     *   - 3.2: improved to support post-setting object task 
     *   - 3.2c: improved to disable browsing buttons while this is in progress
     */
    private void setCurrentObjectAfterBrowsing(boolean waitToFinish) {
      // v3.2c: disable browsing buttons (recording their current states for restore later)
      if (waitToFinish)
        disableBrowsingButtons();
      
      currentObj = browser.getCurrentObject();

      /*v3.2: added extension*/
      try {
        onSetCurrentObject(currentObj);
      } catch (Exception e) {
        controller.logError("Could not perform post-current-object task", e);
      }
      
      /*
       * v2.7.2: create a task queue for gui update and openning the children
       * updateGUI();
       * 
       * if (isNested()) { OpenPolicy pol = getOpenPolicy(); if
       * (pol.contains(OpenPolicy.C)) { // open children (after any GUI update)
       * doTaskAfterGUIUpdate(getOpenChildrenRunnable(true)); } }
       */
      RunnableQueue<Task> queue = null;
      RunUpdateGUI updateGUI = (RunUpdateGUI) taskMan
          .getTask(TaskName.UpdateGUI);
      updateGUI.setWithChildren(false); // clear children with GUI update (to open children later below)

      if (isNested()) {
        OpenPolicy pol = getOpenPolicy();
        if (pol.isWithChildren() // contains(OpenPolicy.C)
        ) {
          // open children (after any GUI update)
          queue = taskMan.createTaskQueue();
          queue.add(updateGUI);
          /* v3.1: moved to method
          RunOpenChildren openChildren = (RunOpenChildren) taskMan
              .getTask(TaskName.OpenChildren);
          if (openChildren == null) {
            // not yet created this task
            openChildren = new RunOpenChildren(true);
            taskMan.registerTask(openChildren);
          } 
          */
          RunOpenChildren openChildren = getTaskOpenChildren();
          
          openChildren.setSilent(true);

          queue.add(openChildren);

          if (waitToFinish)
            taskMan.runTaskQueueAndWait(queue);
          else
            taskMan.runTaskQueue(queue);
        }
      }

      if (queue == null) {
        // just update gui
        if (waitToFinish)
          taskMan.runAndWait(updateGUI);
        else
          taskMan.run(updateGUI);
      }
      
      // v3.2c: restore browsing buttons states
      if (waitToFinish)
        restoreBrowsingButtons();
    }

    /**
     * @requires {@link #isIndexable() = true}
     * 
     * @effects 
     *  if indexable setting is specified 
     *    if toIndex = true 
     *      set o.index to a new value 
     *    else 
     *      set o.indexConsumer = this 
     *  else 
     *    do nothing
     */
    protected void updateObjectIndex(boolean toIndex, C o) {
      IndexManager indexManager = controller.getMainController().getIndexManager();
      Class cls = getDomainClass();
      indexManager.setIndexConsumer(cls, this);
      if (toIndex) {
        // a newly load object, updates its index
        Indexable obj = (Indexable) o;
        
        indexManager.setIndex(cls, obj, this);
      }
    }

    /**
     * Performs post-processing after the browser has navigated to an object
     * 
     * @effects <pre>
     *  if this is a child controller AND object is freshly loaded from data source
     *    add object to parent's buffer
     *  
     *  update this.dataContainer to show the object
     * </pre>
     * @version 
     * - 3.0: added check for update link to parent
     */
    private void onBrowserStateChanged(ObjectBrowser<C> browser,
        AppState state, C obj, boolean objectIsFresh) {
      /*
       * v2.7.2 TODO: to use this code requires updating all domain classes
       * accordingly, in particular, needs to define an adder method which only
       * adds obj if it is not already included in the buffer
       */

      if (parent != null && objectIsFresh 
          && isUpdateLinkToParent() //v3.0
          ) {
        // add object to parent's buffer
        //Object parentObj = getParentObject();
        DAttr linkParentAttrib = getLinkAttributeOfParent(); // .name();
        parent.updateAssociationLink(linkParentAttrib, obj);
      }

      // update display
      updateGUIOnBrowserStateChanged(browser, state);
    }

    // v2.7.2
    /**
     * @effects update this.dataContainer to display the object that
     *          this.browser has just navigated to
     */
    protected void updateGUIOnBrowserStateChanged(ObjectBrowser<C> browser,
        AppState state) {
      // for sub-classes to implement
    }

    /**
     * @effects <pre>
     *   remove id,obj from browser
     *   if no more objects to browse
     *      clear browser
     * </pre>
     * @requires browser sub-module is enabled /\ browser is opened
     */
    private void updateBrowserOnDelete(Oid id, C obj) {
      // now remove object
      try {
        boolean removedAndBrowsed = browser.remove(id, obj);

        // v2.7.2: if removed succeeded and browser succeeded to browse away
        // from the removed object
        // then determine what to next depends on this new object
        if (removedAndBrowsed) {
          AppState state;
          if (browser.isLast()) {
            state = AppState.Last;
          } else if (browser.isFirst()) {
            state = AppState.First;
          } else {
            // either Next or Previous is fine
            state = AppState.Next;
          }

          setCurrentState(state);

          onBrowserStateChanged(browser, state, browser.getCurrentObject(),
              false // must not add object
          );
          //
          // if (methodListenerMap.containsKey(state)) {
          // fireMethodPerformed(state, currentObj);
          // }
        }
      } catch (ObsoleteStateSignal s) {
        // no more objects
        clearBrowser();
      } catch (Exception e) {
        // other exceptions should not happen
        controller.logError(
            "DataController.updateBrowserOnDelete: failed to delete from browser <"
                + id + "," + obj + ">", e);
      }
    }

    /**
     * @effects <pre>
     *   if browser is not opened
     *    open browser with id
     *   else
     *    add (id,obj) to browser
     * </pre>
     * @requires browser sub-module is enabled
     */
    protected void updateBrowserOnCreate(Oid id, C obj) {
      if (!browser.isOpened()) {
        Collection<Oid> oids = new ArrayList();
        oids.add(id);
        browser.open(oids, id, id);
      } else {
        // add object to browser
        browser.add(id, obj);
      }
    }

    /**
     * Handles the on-click event raised by a {@see JDataField} component on the
     * data container of this. Sub-classes can override this behaviour to suite
     * their needs.
     */
    public void onClick_DataField(JDataField df) {
      updateSearchQuery(df);
    }

    /**
     * Handles the on-click event raised by a {@see JDataField} component on the
     * data container of this, to update the search query that allows the user
     * to search for the domain objects maintained by this.
     * 
     * @effects if <code>search-bar.visible=true</code> then update the current
     *          query with <code>df</code>'s name and value.
     */
    private void updateSearchQuery(JDataField df) {

      View userRoot = user.getGUI();
      if (userRoot.isVisibleContainer(SearchToolBar)) {
        // update the current query
        // Note: ignore the actual state values here!
        if (currentQuery == null) {
          /*
           * v2.7.2: support search option Query query = getParentObjectQuery();
           * if (query == null) currentQuery = new Query(); else currentQuery =
           * query;
           */
          boolean searchAll = isSearchAll();
          if (searchAll) {
            // exclude parent query
            currentQuery = new Query();
          } else {
            // include parent query (if any)
            /*v3.0 added check for update link to parent
            Query query = getParentObjectQuery();
            */
            Query query = null; 
            if (parent != null && isUpdateLinkToParent()) { 
              query = getParentObjectQuery(true  // strict
                  );
            }

            if (query == null)
              currentQuery = new Query();
            else
              currentQuery = query;
          }
        }

        DAttr dc = df.getDomainConstraint();
        Object fieldVal = df.getValue(true); // v2.7.3: df.getValue();

        String var = dc.name();
        // op depends on the domain constraint type
        // - non-domain type: match
        // - domain type: equals
        Op op;
        /*
         * v2.6.4b: support auto-identification of query op from attribute type
         * AND auto-augmentation of SQL string pattern '%' where appropriate if
         * (dc.type().isDomainType()) { op = Expression.Op.EQ; } else { op =
         * Expression.Op.MATCH; } Object val = df.getValue();
         */
        op = QueryToolKit.getDefaultOperatorFromAttributeType(dc.type());
        Object val = QueryToolKit.getSQLValuePattern(dc.type(), fieldVal);

        Expression exp = new ObjectExpression(getDomainClass(), // cls,
            dc, op, val); // Expression(var, op, val);

        currentQuery.add(exp);

        // update the search-tool-bar display
        userRoot.updateSearchToolBarState(new String[] { 
            currentQuery.//toString(false)
            toUserFriendlyString(controller.getAttribNameLabelMap())
        });

        setCurrentState(AppState.SearchQueryEditing);
      }
    }

    /**
     * @effects finds in the object pool the objects matching the search query
     *          specified in the search tool bar
     */
    protected void search() throws QueryException, DataSourceException,
        NotFoundException, NotPossibleException {

      if (currentQuery != null) {
        // clear domain class resources
        clearDomainClassResources(false);

        /*
         * v2.7.2: support searching in the object buffer of the parent (if
         * specified) search(currentQuery);
         */
        if (parent != null && getOpenPolicy().isWithAllObjects()) {
          searchInParentBuffer(currentQuery);
        } else {
          search(currentQuery);
        }
      } else {
        controller.displayErrorFromCode(MessageCode.QUERY_REQUIRED, this);
      }
    }

    /**
     * @requires query != null /\ parent != null /\ parent's object buffer is
     *           used to hold all objects
     * 
     * @modifies this
     * 
     * @effects finds in the parent's object buffer those matching the search
     *          query specified in <tt>query</tt>
     * 
     *          <p>
     *          if results were found updates this to enable user to browse the
     *          result browse to the first record else display "No result"
     *          message
     */
    private void searchInParentBuffer(Query query) {
      Collection objBuffer = getLinkedParentObjectBuffer();
      if (objBuffer != null) {
        Map<Oid, C> result = dodm.getDom().getFilteredObjectsFrom(
            getDomainClass(), // cls,
            objBuffer, query);
        if (result != null) {
          // found some

          openObjects(result, true);
          onOpenAndLoad();

          dataContainer.updateDataPermissions();

          // no need to browse to the first record here because
          // open policy is assumed to be to load objects

          clearChildren();
        } else {
          // no match
          controller.displayErrorFromCode(MessageCode.ERROR_NO_QUERY_RESULT, this,
              query.toUserFriendlyString(controller.getAttribNameLabelMap()));
          // clear the gui
          clearGUI();
        }
      } else {
        // buffer is empty or not specified
        // no match
        controller.displayErrorFromCode(MessageCode.ERROR_NO_QUERY_RESULT, this, 
            query.toUserFriendlyString(controller.getAttribNameLabelMap()));
        // clear the gui
      }

      setCurrentState(AppState.Searched);
    }

    /**
     * @requires query != null
     * 
     * @modifies this
     * 
     * @effects finds in the the objects matching the search query specified in
     *          <tt>query</tt>
     * 
     *          <p>
     *          if results were found updates this to enable user to browse the
     *          result browse to the first record return <tt>true</tt> else
     *          display "No result" message return <tt>false</tt>
     */
    public boolean search(Query query) throws DataSourceException,
        NotFoundException, NotPossibleException {
      Collection<Oid> objIds = null;
      DSMBasic dsm = dodm.getDsm();
      DOMBasic dom = dodm.getDom();
      Class cls = getDomainClass();
      
      /*
       * v2.6.4b: consider the special case of 1:1 association determined by
       * parent. (In this case, the query does not contain the term against the
       * parent object) objIds = schema.loadObjectOids(cls, query);
       */
      if (parent != null && isDeterminedByParent()) {
        // the special case of 1:1 association determined by parent
        // search directly against the linked object
        Object linkedObj = getLinkedParentObject();
        if (linkedObj != null && query.eval(dsm, linkedObj)) {
          // match
          objIds = new ArrayList<Oid>();
          objIds.add(dom.lookUpObjectId(linkedObj.getClass(),
              linkedObj));
        }
      } else {
        /*v3.0: support non-serialisable classes (to search in the object pool)
        // search normally
        objIds = dom.retrieveObjectOids(cls,query);
        */
        if (!controller.isSerialisable()) {
          // non-serialisable -> search in object pool
          objIds = dom.getObjectOids(cls, query);
        } else {
          // serialisable -> search normally
          objIds = dom.retrieveObjectOids(cls,query);
        }
      }

      boolean found;
      if (objIds != null) {
        currentObj = null;

        /*
         * v2.7.2 openOid(objIds);
         */
        open(objIds);

        // v2.7.2:
        dataContainer.updateDataPermissions();

        // v2.7.2: browse to the first record (if not specified so in the
        // policy)
        OpenPolicy pol = getOpenPolicy();
        if (pol.isWithObjectIdOnly()
        // !pol.contains(OpenPolicy.O)
        ) {
          first();
        }

        clearChildren();

        found = true;
      } else {
        controller.displayErrorFromCode(MessageCode.ERROR_NO_QUERY_RESULT, this,
            query.toUserFriendlyString(controller.getAttribNameLabelMap()));
        // clear the gui
        clearGUI();

        found = false;
      }

      setCurrentState(AppState.Searched);

      return found;
    }

    /**
     * @effects clear the search state and raises SearchClosed state event
     * @requires this.dataContainer is the active data container on the AppGUI
     *           to which the search tool bar is being attached.
     */
    public boolean closeSearch() {
      // if search query is active -> confirm with the user

      boolean closeSearch = false;
      if (this.currentQuery != null) {
        boolean toClose = controller
            .displayWarningFromCode(
                MessageCode.WARN_CLOSE_SEARCH_RESULT,
                user.getRootDataController(),
                true);
        if (toClose) {
          // clears search resources, including the browser
          clearSearch(false, true, true);
          // clear the gui
          clearGUI(false);
          // clear all child (and decendant) containers
          clearChildren();

          // setCurrentState(AppState.SearchClosed);
          closeSearch = true;
        }
      } else {
        // no search query, clears resources excluding the browser (in case it
        // contains
        // browsing data from the source)
        clearSearch(false, false, false);

        // v2.7.2: invoke closeSearch on the children (if any)
        boolean allClosed = closeSearchOnChildren();

        // setCurrentState(AppState.SearchClosed);
        closeSearch = allClosed;
      }

      // v2.7.2:
      if (closeSearch) {
        // turn off tool bar
        View userGui = getUserGUI();
        // this may be invoked repeatedly by decendant containers of this
        userGui.setVisibleContainer(SearchToolBar, false);

        // reset data container to its original state
        dataContainer.updateDataPermissions();

        // TODO: is this needed?
        // setCurrentState(AppState.SearchToolBarUpdated);

        setCurrentState(AppState.SearchClosed);
      }

      return closeSearch;
    }

    /**
     * @effects if there are child controllers recursively closes search on them
     *          if all children agree to close return true else return false
     *          else return false
     */
    private boolean closeSearchOnChildren() {
      List<ControllerBasic.DataController> children = getChildControllers();

      boolean allClosed = true;

      if (children != null) {
        boolean childClose;
        for (DataController dc : children) {
          // clear the GUI and the object buffer
          childClose = dc.closeSearch();
          if (!childClose) { // one disagree -> break
            allClosed = false;
            break;
          }
        }
      }

      return allClosed;
    }

    /**
     * @effects clears <code>currentQuery</code>, the state state, and the
     *          search-tool-bar. Raise SearchCleared state event
     */
    protected void clearSearch() {
      clearSearch(true, true, true);

      // clear the gui
      clearGUI(false);

      // v2.7.2: prepare for new search
      dataContainer.forceEditable();

      // clear all child (and decendant) containers
      clearChildren();
    }

    /**
     * @effects clears <code>currentQuery</code>, the state state, the domain
     *          class resources, and the search-tool-bar
     * 
     * @version 2.7.2: added option withChildren
     */
    protected void clearSearch(boolean updateState, boolean withBrowser,
        boolean withChildren // v2.7.2
    ) {
      if (withBrowser) {
        // clear data
        // v2.7.2
        clearDomainClassResources(false);

        currentObj = null;
        clearBrowser();
      }

      // clear query
      currentQuery = null;

      // clear the tool bar
      user.getGUI().clear(SearchToolBar);

      if (withChildren) {
        // clear all children's search query (if any)
        clearChildrenSearchQueries();
      }

      if (updateState)
        setCurrentState(AppState.SearchCleared);
    }

    void clearChildrenSearchQueries() {
      List<ControllerBasic.DataController> children = getChildControllers();
      if (children != null) {

        for (DataController dc : children) {
          // clear the GUI and the object buffer
          dc.clearSearch(false, false, true);
        }
      }
    }

//    /**
//     * @effects 
//     *  if this is configured to support sorting
//     *    return <tt>true</tt>
//     *  else
//     *    return <tt>false</tt>
//     * @version 
//     *  3.0
//     */
//    public boolean isSortable() {
//      return (browser instanceof PooledObjectBrowser);
//    }

    /**
     * @effects 
     *  if this is configured to support object sorting
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt>
     */
    public boolean isSortingEnabled() {
      return getObjectSortingConfig() != null;
    }

    /**
     * @effects 
     *  if this.browser is currently in sorting mode
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt>
     *  @version 3.0
     */
    public boolean isSortingOn() {
      return (browser != null && browser.isSortingOn());
    }
    
    /**
     * @effects 
     *  if this.browser is currently sorting objects by <b>id</b> domain attribute AND in ascending order
     *    return <tt>true</tt>
     *  else
     *    return <tt>false</tt>
     *  @version 3.0
     */
    public boolean isSortingIdAttributeAsc() {
      return isSortingOn() && browser.isSortingIdAttributeAsc();
    }
    
    /**
     * @requires 
     *  {@link #isSortable()} /\ 
     *  comparator != null /\ 
     *  this.{@link #browser} contains sufficient objects
     *  
     * @effects 
     *  sort objects in this.{@link #browser} by <tt>comparator</tt>; 
     *  update data container to show sorted objects when finished.
     *  
     *  <p>throws NotPossibleException if a pre-condition is not met or failed to sort objects as required
     *  
     * @version 
     * - 3.0<br>
     * - 3.1: redirect to another method
     */
    public void sort(ObjectComparator comparator) throws NotPossibleException {
      /*v3.1
      // check some pre-conditions
      int sz = browser.size();
      if (sz <= 1) {
        throw new NotPossibleException(NotPossibleException.Code.INVALID_BROWSER_STATE_FOR_SORTING,
            new Object[] {this.toString(), sz, 2});
      }

      browser.sort(comparator);
      
      // update data container to show sorted entries
      clearGUI(true);
      
      // reset index counter (if applicable)
      resetIndexCounter();
      
      // browse again through all objects, forcing to re-index them (if applicable)
      try {
        // browser first without updating the GUI
        boolean forceToIndex = true;
        
        browseFirstToLast(false,false,forceToIndex);
        
        // then back to first, this time do not force to update index (b/c all objects have been 
        // indexed by browsing above) and 
        //    updates the GUI and fire state change
        forceToIndex = false;
        first(true, true,false,forceToIndex);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_BROWSE_ALL,
            e);
      }
      
      // if sorter uses the default sorting (i.e. sort id attribute ASC) then 
      // reset the id range, 
      // this allows user to continue to browse (and load new) objects in case
      // sorting were performed before all objects have been loaded
      if (comparator.isSortingIdAttributeAsc()) {
        browser.endSorting();
      }
      */
      sort(comparator, false);
    }

    /**
     * @requires 
     *  {@link #isSortable()} /\ 
     *  comparator != null /\ 
     *  this.{@link #browser} contains sufficient objects
     *  
     * @effects 
     *  sort objects in this.{@link #browser} by <tt>comparator</tt>; 
     *  update data container to show sorted objects when finished
     *  
     *  <p>If silent = true then skip the initial sorting check 
     *  
     *  <p>throws NotPossibleException if a pre-condition is not met or failed to sort objects as required
     *  
     * @version 
     * - 3.0<br>
     * - 3.1: redirect to another method
     */
    public void sort(ObjectComparator comparator, boolean silent) throws NotPossibleException {
      // check some pre-conditions
      int sz = browser.size();

      // initial sorting check
      if (sz <= 1) {
        if (!silent)
          throw new NotPossibleException(NotPossibleException.Code.INVALID_BROWSER_STATE_FOR_SORTING,
            new Object[] {this.toString(), sz, 2});
        else
          return;
      }

      browser.sort(comparator, silent);
      
      // update data container to show sorted entries
      clearGUI(true);
      
      // reset index counter (if applicable)
      resetIndexCounter();
      
      // browse again through all objects, forcing to re-index them (if applicable)
      try {
        // browser first without updating the GUI
        boolean forceToIndex = true;
        
        browseFirstToLast(false,false,forceToIndex);
        
        // then back to first, this time do not force to update index (b/c all objects have been 
        // indexed by browsing above) and 
        //    updates the GUI and fire state change
        forceToIndex = false;
        first(true, true
            // waitToFinish (same as updateCurrentObject)
            //v3.2c: , false
            , true
            ,forceToIndex);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_BROWSE_ALL,
            e);
      }
      
      // if sorter uses the default sorting (i.e. sort id attribute ASC) then 
      // reset the id range, 
      // this allows user to continue to browse (and load new) objects in case
      // sorting were performed before all objects have been loaded
      if (comparator.isSortingIdAttributeAsc()) {
        browser.endSorting();
      }
    }
    
    /**
     * @effects 
     *  if the model config associated to this is specified with sorter settings
     *    read it to create and return an <tt>ObjectComparator</tt>
     *  else
     *    return <tt>null</tt>
     * @version 
     *  3.0
     */
    public ObjectComparator getObjectSortingConfig() {
      if (comparator == null) {
        if (modelCfg != null) {
          SortBy sortBy = (SortBy) modelCfg.getProperty(PropertyName.model_sort_objects, null);
          if (sortBy != null) {
            // has a pre-configured sorting setting
            Class domainCls = getDomainClass();
            DSMBasic dsm = controller.getDomainSchema();
            
            // look up the sort attribute
            String sortAttribName = (String) modelCfg.getProperty(PropertyName.model_sort_attribute, null);
            DAttr sortAttrib;
            if (sortAttribName == null) {
              // default: sort id attribute
              sortAttrib = dsm.getIDDomainConstraints(domainCls).get(0);            
            } else {
              sortAttrib = dsm.getDomainConstraint(domainCls, sortAttribName);
            }
            
            comparator = new ObjectComparator(dsm, sortAttrib, sortBy);
          }
        }
      }
      
      return comparator;
    }


    /**
     * @effects 
     *  if {@link #updateGUI()} is running
     *    wait for it to complete
     *  
     *   <p>throws NotPossibleException if task does not stop after a pre-determined max amount of time
     * @version 
     * - 3.0 <br>
     * - 3.1: add waiting for other related tasks (e.g. openChildren) if needed 
     */
    public void waitForGuiUpdate(Boolean withChildren) throws NotPossibleException {
      RunUpdateGUI t = (RunUpdateGUI) taskMan.getTask(TaskName.UpdateGUI);
      if (taskMan.isRunning(t)) {
        boolean stopped = taskMan.waitFor(t, RunUpdateGUI.MAX_RUN_TIME);
        if (!stopped) {
          // something wrong, task is till running (e.g. a dead-lock occurs)
          // NOTE: a dead-lock may occur if a parent container.updateGUI is running which causes 
          // a child container to call parent.updateGUI (e.g. as part of its createObject operation)
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WAIT_FOR_UPDATE_GUI, 
              new Object[] {this});
        }
      }
      
      // v3.1: if withChildren is specified then wait for task OpenChildren as well
      if (withChildren != null && withChildren == true) {
        RunOpenChildren openChildTask = getTaskOpenChildren();
        
        if (taskMan.isRunning(openChildTask)) {
          boolean stopped = taskMan.waitFor(openChildTask, RunOpenChildren.MAX_RUN_TIME);
          if (!stopped) {
            // something wrong, task is till running (e.g. a dead-lock occurs)
            // NOTE: a dead-lock may occur if a parent container.updateGUI is running which causes 
            // a child container to call parent.updateGUI (e.g. as part of its createObject operation)
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WAIT_FOR_UPDATE_GUI, 
                new Object[] {this});
          }
        }
      }
    }
    
    /**
     * @effects refresh the data container to re-display the current object of
     *          this; AND <pre>
     *            if withChildren != null 
     *               if withChildren = false
     *                  <b>clear</b> all child containers of this
     *               else
     *                  <b>open</b> all child containers of this
     *          </pre>
     *          
     *          <p>throws NotPossibleException if failed.
     */
    public void updateGUI(Boolean withChildren) throws NotPossibleException {
      RunUpdateGUI guiUpdate = (RunUpdateGUI) taskMan.getTask(TaskName.UpdateGUI);

      /*v3.1: use method
      // if t is running, wait
      if (taskMan.isRunning(guiUpdate)) {
        boolean stopped = taskMan.waitFor(guiUpdate, RunUpdateGUI.MAX_RUN_TIME);
        if (!stopped) {
          // something wrong, task is till running (e.g. a dead-lock occurs)
          // NOTE: a dead-lock may occur if a parent container.updateGUI is running which causes 
          // a child container to call parent.updateGUI (e.g. as part of its createObject operation)
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WAIT_FOR_UPDATE_GUI, 
              new Object[] {this});
        }
      }
      */
      waitForGuiUpdate(withChildren);
      
      /*v3.1: separate the task of opening the children out of GUI update thread because 
       * that may result in a dead-lock situation (explained above) in that opening a child 
       * requires updating the parent's GUI but that update is still busy with opening the child 

      t.setWithChildren(withChildren);

      taskMan.run(t);
      */
      
      guiUpdate.setWithChildren(withChildren);
      
      if (isNested() && withChildren != null && withChildren == true) {
        // has children and to open children: 
        // NOTE: children were already cleared by guiUpdate (above)
        
        // create a task queue (once) for gui update (above) and child opener
        if (guiUpdateQueue == null) {
          guiUpdateQueue = taskMan.createTaskQueue();
        }

        // need to populate queue each time (because queue is emptied after each run)
        if (!guiUpdateQueue.contains(guiUpdate))  // just in case queue still has the task
          guiUpdateQueue.add(guiUpdate);
        RunOpenChildren openChildren = getTaskOpenChildren();
        if (!guiUpdateQueue.contains(openChildren)) // just in case queue still has the task
          guiUpdateQueue.add(openChildren);
        
        // run queue (without waiting for it to finish) 
        taskMan.runTaskQueue(guiUpdateQueue);
      } else {
        // no children: just run the gui update
        taskMan.run(guiUpdate);
      }
    }

    /**
     * @effects updates <code>this.gui</code> and clear all the children's GUIs
     *          (if any)
     */
    public void updateGUI() {
      // TODO: determine whether or not to update children based on the
      // configuration setting

      // default
      boolean withChildren = false;

      updateGUI(withChildren);
    }

    /**
     * @effects updates the
     *          command and menu buttons of the <code>AppGUI</code> owner in a
     *          separate thread of control
     */
    private void updateGUIButtons() {
      Task t = taskMan.getTask(TaskName.UpdateGUIButtons);
      if (t == null) {
        // not yet created this task
        t = new RunUpdateGUIButtons();

        taskMan.registerTask(t);
      }

      // SwingUtilities.invokeLater(runUpdateGUIButtons);
      // run task
      taskMan.run(t);
    }
    
    /**
     * @requires 
     *  {@link #disableBrowsingButtons()} were previously invoked.
     * 
     * @modifies {@link #browsingButtonStatesMap}
     * 
     * @effects 
     *  restore the old states of the browsing buttons of {@link #user}'s GUI before 
     *  they were disabled by {@link #disableBrowsingButtons()} 
     *  
     * @version 3.2c
     */
    private void restoreBrowsingButtons() {
      user.getGUI().setComponentsEnabled(browsingButtonStatesMap);      
    }

    /**
     * @modifies {@link #browsingButtonStatesMap}
     *   
     * @effects 
     *  disable the browsing buttons on the {@link #user}'s GUI, recording the old states 
     *  of these buttons in {@link #browsingButtonStatesMap}
     *  
     * @version 3.2c
     */
    private void disableBrowsingButtons() {
      if (browsingButtonStatesMap == null) {
        browsingButtonStatesMap = new HashMap();      
        browsingButtonStatesMap.put(First, Boolean.FALSE);
        browsingButtonStatesMap.put(Previous, Boolean.FALSE);
        browsingButtonStatesMap.put(Next, Boolean.FALSE);
        browsingButtonStatesMap.put(Last, Boolean.FALSE);
      }
      user.getGUI().setComponentsEnabled(browsingButtonStatesMap);      
    }


    // /**
    // * @effects if <code>this.objectBuffer</code> maintains object
    // * <code>obj</code> then refreshes the GUI (possibily) to
    // * show/clear it (depending on the operation).
    // */
    // protected void refreshBuffer(C obj) throws DBException {
    // // for sub-classes
    // }

    public String toString() {
      return this.getClass().getSimpleName() + "(" + controller.// Controller.this.
          getName() + "," + user.getName() + ")";
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      String str = toString();
      result = prime * result + getCreator().hashCode();
      result = prime * result + ((str == null) ? 0 : str.hashCode());
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
      DataController other = (DataController) obj;
      // if (!getOuterType().equals(other.getOuterType()))
      // return false;

      if (!(getCreator().getDomainClass() == other.getCreator()
          .getDomainClass()) || !user.equals(other.user))
        return false;

      return true;
    }

    /**
     * This method is a passive form of GUI's state-change event handler. It
     * returns a state map of the form {@see ProtectedMap<{@see GUIAction},
     * {@see Boolean}>} which maps a <code>GUIAction</code> to a
     * <code>Boolean</code> value representing its state (enabled or disabled).
     * These states are determined based on the {@link #currentState} of this
     * controller and, in some cases, on the state of its object buffer
     * 
     * <p>
     * The following pseudocode snippet shows an example of the rules used:
     * 
     * <pre>
     *  if guiStateMap = null
     *    guiStateMap = getDefaultMap()
     *  if currentState.isChanged = true
     *    guiStateMap.put(Open, Boolean.TRUE)
     *    guiStateMap.put(New, Boolean.TRUE)
     *    guiStateMap.put(Refresh, Boolean.TRUE)
     *   
     *    if (currentState.equals(AppState.NewObject)) 
     *      guiStateMap.put(Create, Boolean.TRUE)
     *      guiStateMap.put(Cancel, Boolean.TRUE)
     *    else 
     *      guiStateMap.put(Create, Boolean.FALSE)
     *    
     *   // other cases ...
     *  
     *  return guiStateMap
     * </pre>
     * 
     * <p>
     * This method is therefore invoked by GUI <code>update</code> methods
     * (typically the {@link #updateGUI()} of this controller) to update the
     * command and menu buttons of parent the <code>AppGUI</code> object.
     * 
     * <p>
     * Note: the enabled states of certain actions are stored using
     * <code>ProtectedMap</code> so that certain GUI actions that are not
     * supported by the target GUI can be set to disabled (e.g. by the
     * sub-classes) at initialisation time and to stay unchanged through out.
     * 
     * @effects if<code>this.currentState</code> is changed then updates
     *          <code>this.guiStateMap</code> with new state values for the
     *          <b>supported</b> <code>GUIAction</code>s and returns this map,
     *          else returns <code>null</code>.
     * 
     * @modifies {@link #guiStateMap}
     */
    public Map<LAName, Boolean> getGUIStateMap() {
      if (guiStateMap == null) {
        guiStateMap = getDefaultStateMap();
      }

      /*v2.7.4: separate into two cases 
      if (currentState.equals(AppState.Hidden)
          || currentState.equals(AppState.ViewCompact)) {
        for (Iterator<LAName> it = guiStateMap.keySet().iterator(); it
            .hasNext();) {
          guiStateMap.put(it.next(), Boolean.FALSE);
        }

        return guiStateMap;
      }
      */
      // if GUI has been hidden --> disable all buttons
      if (currentState.equals(AppState.Hidden)) {
        for (Iterator<LAName> it = guiStateMap.keySet().iterator(); it
            .hasNext();) {
          guiStateMap.put(it.next(), Boolean.FALSE);
        }

        return guiStateMap;
      }

      // v3.0
      final boolean isSerialisable = controller.isSerialisable();
      boolean toReload = isSerialisable;
      
      // v3.2c: help button
      boolean helpOn = true;  // default
      guiStateMap.put(HelpButton, helpOn);
      
      // if GUI is in compact mode --> disable all but some buttons
      if (currentState.equals(AppState.ViewCompact)) {
        LAName act;
        for (Iterator<LAName> it = guiStateMap.keySet().iterator(); it
            .hasNext();) {
          act = it.next();
          if (act == Refresh || act == Export) {
            // enable
            guiStateMap.put(act, Boolean.TRUE);
          } else if (act == Reload) { // v3.0
            guiStateMap.put(act, toReload);
          } else {
            // disable
            guiStateMap.put(act, Boolean.FALSE);
          }
        }

        return guiStateMap;
      }

      // otherwise...
      // v2.6.4.a: changed to user controller
      final boolean searchOn = user.isSearchOn(); // isSearchOn();
      
      // v3.1: added for reuse
      final boolean isEditing = currentState.equals(AppState.Editing);
      final boolean isCurrentObjNull = (currentObj == null);
      
      // v3.0: support sorting
      final boolean noSortingOrSortingId = !isSortingOn() || isSortingIdAttributeAsc();

      if (currentState.equals(AppState.NewObject)) {
        // new object
        guiStateMap.put(Create, Boolean.TRUE);
        guiStateMap.put(Update, Boolean.FALSE);
        guiStateMap.put(Cancel, Boolean.TRUE);
        guiStateMap.put(Reset, Boolean.TRUE);

        guiStateMap.put(Open, Boolean.FALSE);
        guiStateMap.put(CopyObject, Boolean.FALSE); // v3.0
        guiStateMap.put(New, Boolean.FALSE);
        guiStateMap.put(Refresh, Boolean.FALSE);
        guiStateMap.put(Reload, Boolean.FALSE);
      } else {
        // other cases
        final boolean topLevelCtl = (parent == null);
        final boolean canDoObject = (!topLevelCtl || (topLevelCtl && !searchOn));

        // v5.2: added support for domainClass's setting
        final boolean isAbstract = controller.getDomainSchema().isAbstract(controller.getDomainClass());
        
        // v2.7.2 boolean nullBuffer = !isOpened();

        boolean toNew = 
            /* v5.1c: controller.isEditable() && */ 
            canDoObject 
            && !isAbstract  // v5.2: 
            && noSortingOrSortingId// v3.0
            ;

        // v2.7.2:
        boolean toAdd = searchOn && !topLevelCtl && !isCurrentObjNull; //(currentObj != null);

        // open/refresh when:
        // domain class is serialisable /\
        // search tool bar is not visible
        boolean openOrRefresh = isSerialisable //v3.0: controller.isSerialisable() 
                                && canDoObject;
        boolean toRefresh =
                // v2.6.4.a: changed to allow Refresh whenever currentObj is valid
               !isCurrentObjNull; //(currentObj != null);
        
        toReload = toReload & toRefresh;
        
        /*
         * v2.7.2: support a separate option for open boolean toOpen =
         * (openOrRefresh && nullBuffer);
         */
        boolean toOpen = 
            (openOrRefresh && 
                (notYetOpened || 
                    (!isOpened() && 
                        (!searchOn || 
                          (!topLevelCtl && (dataContainer.getSearchState() == null))))));

        // v3.0
        boolean toCopy = (!isCurrentObjNull //currentObj != null
            // TODO: remove this condition if DefaultPanel is improved to support selected objects
            // and therefore the copy operation
            && (dataContainer instanceof JDataTable)
            ); 
        
        // v3.1: better support toCreate
        // TODO: enable the following after fixing closeSearch() to resetGUI rather to 
        // to clearGUI() (b/c this results in data fields not validating domain values)
        boolean toCreate = false; //!searchOn && isCurrentObjNull && isEditing;
        
        guiStateMap.put(New, toNew);
        guiStateMap.put(Add, toAdd); // v2.7.2
        guiStateMap.put(Open, toOpen);
        guiStateMap.put(Refresh, toRefresh);
        guiStateMap.put(Reload, toReload); // v3.0
        guiStateMap.put(CopyObject, toCopy);

        // v3.1: 
        guiStateMap.put(Create, toCreate);
        
        if (// v3.1: currentState.equals(AppState.Editing)
            isEditing) {
          // v3.1 : moved to above: guiStateMap.put(Create, Boolean.FALSE);
          guiStateMap.put(Update, Boolean.TRUE);
          guiStateMap.put(Cancel, Boolean.FALSE);
          guiStateMap.put(Reset, Boolean.TRUE);
        } else {
          // v3.1: moved to above: guiStateMap.put(Create, Boolean.FALSE);
          guiStateMap.put(Update, Boolean.FALSE);
          guiStateMap.put(Cancel, Boolean.FALSE);
          guiStateMap.put(Reset, Boolean.FALSE);
        }
      }

      // other actions
      boolean del, nex, pre, first, last, search, clrSearch, closeSearch, export, print, chart;

      /*v3.1: temporarily disable this condition (do we need this for delete?)
      del = noSortingOrSortingId;  // v3.0
      */
      del = true;
      
      closeSearch = true; // close search

      if (currentState.equals(AppState.NewObject)) {
        // disable navigations and delete when new object is on
        del = del && false;
        nex = false;
        pre = false;
        first = false;
        last = false;
        search = false;
        clrSearch = false;
      } else {
        if (// v3.1: currentState.equals(AppState.Editing)
            isEditing) {
          del = del && false;
          nex = false;
          pre = false;
          first = false;
          last = false;
        } else {
          del = del && !isCurrentObjNull; //(currentObj != null);

          // next/previous is enabled if:
          // - can move next/previous in the object buffer /\
          // - either search is not on or current state is Searched or OnFocus
          // or any of
          // the record browsing states
          boolean searchedOrBrowsingResult = (searchOn) ? currentState
              .equals(AppState.Searched) : false;
          if (searchOn && !searchedOrBrowsingResult) {
            // takes into account user browsing actions on search results
            searchedOrBrowsingResult = currentState.equals(AppState.Next)
                || currentState.equals(AppState.Last)
                || currentState.equals(AppState.Previous)
                || currentState.equals(AppState.First)
                || currentState.equals(AppState.OnFocus)
                // v2.6.4.a: added to allow resume browsing after Update
                || currentState.equals(AppState.Updated)
                // v2.6.4.a: added to enable browsing after Opened
                || currentState.equals(AppState.Opened);
          }
          boolean searchNotOrSearchedOrBrowsingResult = (!searchOn || searchedOrBrowsingResult);
          nex = browser.hasNext() && searchNotOrSearchedOrBrowsingResult;
          pre = browser.hasPrevious() && searchNotOrSearchedOrBrowsingResult;
          first = browser.isOpened() && (!currentState.equals(AppState.First))
              && searchNotOrSearchedOrBrowsingResult;
          last = browser.isOpened() && (!currentState.equals(AppState.Last))
              && searchNotOrSearchedOrBrowsingResult;
        }
        search = (currentQuery != null);
        clrSearch = (dataContainer.getSearchState() != null);
      }

      // export/chart is enabled only for report type or when next or prev is
      // enabled
      export = !isCurrentObjNull //(currentObj != null)
      // v2.7.3: remove this check -> || controller.isReport()
      ;
      print = export; // v2.7.2
      chart = export && isChartable();

      guiStateMap.put(Delete, del);
      guiStateMap.put(Next, nex);
      guiStateMap.put(Last, last);
      guiStateMap.put(Previous, pre);
      guiStateMap.put(First, first);
      guiStateMap.put(Search, search);
      guiStateMap.put(ClearSearch, clrSearch);
      guiStateMap.put(CloseSearch, closeSearch);
      guiStateMap.put(Export, export);
      guiStateMap.put(Print, print); // v2.7.2
      guiStateMap.put(Chart, chart);

      // debug
//      System.out.format("%s: currentState=%s, Create=%b%n", this,
//        currentState.currentValue.name(), guiStateMap.get(Create));

      return guiStateMap;
    }

    /**
     * @effects returns a <code>Map<GUIAction,Boolean></code> which maps a
     *          <code>GUIAction</code> to its default state (<code>true</code>
     *          means enabled and <code>false</code> means disabled)
     */
    protected Map getDefaultStateMap() {
      ProtectedMap stateMap = new ProtectedMap();

      /**
       * initialised the default states of the actions
       **/
      Class domainClass = getDomainClass();
      String clsName = controller.getDomainSchema().getDomainClassName(
          domainClass);
      final ControllerBasic mainCtl = controller.getMainController();

      // common actions
      // for (LAName a : LogicalAction.LAName.values()) {
      // stateMap.put(a, Boolean.TRUE);
      // }

      // data actions
      // TODO: should need to process the states of the application actions
      // based on the user permissions?
      // boolean state;
      for (LAName a : LAName.values()) {
        stateMap.put(a, Boolean.FALSE);
      }

      return stateMap;
    }

    /**
     * @effects if this.currentState = state return true else return false
     */
    public boolean isState(AppState state) {
      return currentState.equals(state);
    }

    /**
     * @effects 
     *   return {@link #currentState}
     *   
     * @verson 3.1
     */
    public State getCurrentState() {
      return currentState;
    }

// v3.2: moved to AutoDataController    
//    /**
//     * Return a <tt>ChangeListener</tt> object responsible for creating a new
//     * object from the user-specified data on the <tt>dataContainer</tt> when a
//     * change event is fired.
//     * 
//     * <p>
//     * Using code should invoke this method once to obtain the object and
//     * register it to all the data fields.
//     * 
//     * @effects return an anonymous <tt>ChangeListener</tt> object that listens
//     *          for change events fired by the data fields of
//     *          <tt>this.dataContainer</tt> and <b>automatically</b> create a
//     *          new object if all data have been entered on the fields.
//     */
//    public ChangeListener getAutoCreateChangeListener() {
//      return new ChangeListener() {
//        @Override
//        public void stateChanged(ChangeEvent e) {
//          /** invoke createObject ignoring all the exceptions that may be thrown */
//          if (currentState != null && currentState.equals(AppState.NewObject)) {
//            try {
//              createObject();
//            } catch (RuntimeException ex) {
//              // update GUI in case information needs to be updated
//              dataContainer.updateGUI();
//
//              // ignore exception
//              if (debug)
//                ex.printStackTrace();
//            } catch (Exception ex1) {
//              // ignore exception
//              if (debug)
//                ex1.printStackTrace();
//            }
//          }
//        }
//      };
//    }

    /**
     * @effects if this.dataContainer contains data that can be used to
     *          construct chart return true else return false
     */
    protected boolean isChartable() {
      // TODO: determine if dataContainer supports chart
      // assume: only JDataTable container is suitable for chart
      return (controller.isSupportChart() && (dataContainer instanceof JDataTable));
    }

// v3.2    
//    /**
//     * @effects create and display a chart object from the data contained in
//     *          this.dataContainer
//     */
//    protected void createChart() throws Exception {
//      ControllerBasic ctl = controller.getMainController()
//          .lookUpWithPermission(ChartWrapper.class);
//      if (ctl != null) {
//        // create a new chart wrapper object using the controller, and use it
//        // to create the chart. Then show the GUI for the user to view and edit
//        // chart options
//
//        // v2.6.2c: call preRunconfigure
//        ctl.preRunConfigureGUI();
//
//        ControllerBasic.DataController chartDctl = ctl.getRootDataController();
//        chartDctl.newObject();
//        // default chart title is named after the data container's label
//        String title = dataContainer.getLabel();
//        if (title == null) {
//          title = getUser().getGUI().getTitle();
//        }
//
//        chartDctl.setMutableState(new Object[] { title,
//            ChartWrapper.ChartType.values()[0], Boolean.TRUE });
//        chartDctl.createObject();
//
//        // set the data controller to create actual chart object
//        ChartWrapper chartWrapper = (ChartWrapper) chartDctl.getCurrentObject();
//        chartWrapper.setDataCtl(this);
//
//        // v2.7.3: update GUI to show chart object
//        chartDctl.updateGUI(null);
//
//        // display gui
//        ctl.showGUI();
//      }
//    }

    /**
     * This method is used by bounded data fields to obtain access to the data
     * objects managed by this data controller.
     * 
     * @effects return JDataSource that encapsulates the objects managed by this
     */
    public JDataSource getDataSourceInstance() {
      Class cls = getDomainClass();
      if (dataSource == null) {
        // create if not exists
        /*v3.1: use factory method
        dataSource = new JAdaptiveDataSource(getCreator().getMainController(),
            dodm, cls);
        */
        Class<? extends JDataSource> dsType = JAdaptiveDataSource.class;
        dataSource = JDataSourceFactory.createInstance(dsType, getCreator().getMainController(),
            dodm, cls);
        
        /* v3.1: moved to JDataSourceFactory (above)
        // register data source as listener for changes in objects of the domain
        // class
        dodm.getDom().addChangeListener(cls, dataSource);
        */
      }

      return dataSource;
    }

    /**
     * @effects clear the state of data source instance that was created by
     *          {@link #getDataSourceInstance()}.
     */
    void clearDataSource() {
      if (dataSource != null) {
        if (debug) System.out.println("   " + this + ".clearDataSource()...");
        dataSource.clearBuffer();
      }
    }

    /**
     * @effects clear the state of all the data fields bounded to the data
     *          source instance managed by this (i.e. that which was created by
     *          {@link #getDataSourceInstance()})
     */
    void clearDataSourceBindings() {
      if (dataSource != null) {
        dataSource.clearBindings();
      }
    }

    /**
     * This differs from {@link #clearDataSourceBindings()} in that it operates on other <b>data sources</b> 
     * that are bounded to the data fields of its data container;  while the other method operates on 
     * the <i>data fields</i> bounded to the data source instance of the data controller.
     * 
     * @effects <pre>
     *  if exists bounded bounded data fields of {@link #dataContainer}
     *    clear their bindings with their associated data sources
     *    reestablish the bindings (effectively reloading the objects)
     *  else
     *    do nothing </pre>
     * @version 3.0
     */
    public void refreshTargetDataBindings() {
      if (dataContainer != null) {
        dataContainer.refreshTargetDataBindings();
      }
    }

    /**
     * This differs from {@link #refreshTargetDataBindings()} in that it operates ONLY on the  
     * data field of a given attribute, as opposed to all the bounded data fields of the data container
     * 
     * @effects <pre>
     *  if exists bounded bounded data field of <tt>attrib</tt> in {@link #dataContainer}
     *    clear its binding with the associated data source
     *    reestablish the binding (effectively reloading the objects)
     *  else
     *    do nothing </pre>
     * @version 3.1
     */
    public void refreshTargetDataBindingOfAttribute(DAttr attrib) {
      if (dataContainer != null) {
        dataContainer.refreshTargetDataBindingOfAttribute(attrib);
      }
    }
    
    /**
     * @effects return DataValidator that encapsulates the objects managed by
     *          this
     */
    public DataValidator getDataValidatorInstance() {
      Class cls = getDomainClass();
      if (dataValidator == null) {
        // create if not exists
        dataValidator = new DefaultDataValidator(dodm, cls);
      }

      return dataValidator;
    }

    /**
     * @overview A helper class that is responsible for updating the GUI buttons
     *           of the user GUI that uses this data controller
     * 
     * @author dmle
     */
    private class RunUpdateGUIButtons extends Task {
      public RunUpdateGUIButtons() {
        super(TaskName.UpdateGUIButtons);
      }

      public void run() {
        setIsStopped(false);

        // Note: the state values are determined by this container
        // but the actual update run is performed by the user GUI
        Map<LAName, Boolean> stateMap = getGUIStateMap();

        // run update
        View userGUI = user.getGUI();
        userGUI.setEnabled(stateMap);

        setIsStopped(true);
      }
    } // end RunUpdateGUIButtons

    /**
     * @overview A helper class that is responsible for updating the GUI of the
     *           data container associated to this data controller.
     * 
     * @author dmle
     */
    private class RunUpdateGUI extends Task {
      /**
       * maximum run time (in milliseconds)
       * */
      public static final int MAX_RUN_TIME = 30000;
      
      private Boolean withChildren;

      public RunUpdateGUI() {
        super(TaskName.UpdateGUI);
        withChildren = null;
        // stopped = true;
        // taskList = new LinkedList<Runnable>();
      }

      public void setWithChildren(Boolean tf) {
        this.withChildren = tf;
      }


      @Override //Task
      // public synchronized void run() {
      public void run() {
        // stopped = false; // START running
        setIsStopped(false);

        if (debug) System.out.printf("%s.run(): 1) running GUI update...%n", DataController.this);

        try {
          // v2.6.1: add null check
          if (dataContainer != null && currentObj != null) {
            // if (debug)
            // System.out.printf("%s.updateGUI(): updating current object%n",
            // DataController.this);

            dataContainer.update(currentObj);
          }

          // update children (if any)
          if (withChildren != null) {
            /*v3.1: only clearChildren here; separating openChildren out of this thread to avoid 
             * dead-lock (see updateGUI(Boolean) for explanation)
            if (withChildren) {
              try {
                // v2.7.2: clear children first
                clearChildren();

                openChildren(true);
              } catch (DataSourceException ex) {
                controller.logError(null, ex);
              }

              clearChildren();
            } else {
              // v2.6.4.a: clears children
              if (debug)
                System.out.printf("%s.run(): 2) clears children%n",
                    DataController.this);
              clearChildren();
            }
             */
            if (debug) System.out.printf("%s.run(): 2) clears children%n", DataController.this);

            clearChildren();
          }

          // v2.5.4: update parent GUI as well
          if (parent != null) {
            parent.updateGUI(null);
          }
        } catch (NotPossibleException e) {
          controller.displayError(e.getCode(), DataController.this, e);
        }

        setIsStopped(true);
        // stopped = true; // FINISH running
        if (debug) System.out.printf("%n%s.run(): done%n", DataController.this);
      }
    } // end RunUpdateGUI

    /**
     * @overview A helper class for running {@link #openChildren()} from a
     *           thread.
     * 
     * @author dmle
     */
    private class RunOpenChildren extends Task
    // implements Runnable
    {
      /**
       * maximum run time (in milliseconds)
       * @version 3.1
       * */
      public static final int MAX_RUN_TIME = 60000 * 3;
      
      private boolean silent;

      public RunOpenChildren(boolean silent) {
        super(TaskName.OpenChildren);
        this.silent = silent;
      }

      void setSilent(boolean silent) {
        this.silent = silent;
      }

      public void run() throws ApplicationRuntimeException {
        setIsStopped(false);

        // open children
        try {
          // v2.7.2: there is no need to clear children before openning here
          // because this is invoked by the code that takes care of this
          openChildren(silent);
        } catch (DataSourceException e) {
          throw new NotPossibleException(
              NotPossibleException.Code.FAIL_TO_OPEN_OBJECT_FORM, e,
              "Lỗi mởi dữ liệu {0}", "");
        } finally {
          setIsStopped(true);
        }
      }
    } // end RunOpenChildren

    /**
     * @overview A helper class used to create child objects associated to the
     *           the current object of this data controller
     * 
     * @author dmle
     */
    // TODO: move this to sub-type
    protected class RunCreateChildAssociatedObjects extends Task {
      /***
       * max run time (in millisecs) of this
       */
      public static final int MAX_RUN_TIME = 5000;
      
      // record any exceptions that were raised to display them later
      // this is necessary to avoid a 'dead-lock' (causing the app to freeze) if
      // we were to display the errors while running each child
      private Map<DataController, Exception> errors;

      public RunCreateChildAssociatedObjects() {
        super(TaskName.CreateAssociatedChildObjects);
      }

      public void run() {
        setIsStopped(false);

        if (errors != null)
          errors.clear();

        Iterator<ControllerBasic.DataController> children = getChildControllersIterator();

        if (children != null) {
          ControllerBasic.DataController c;
          JDataContainer cont;
          DAttr linkAttrib;
          while (children.hasNext()) {
            c = children.next();
            cont = c.getDataContainer();
            if (c.isEditing()) { // user had entered new data
              // set value of c's linked data field to o
              linkAttrib = cont.getLinkAttribute();
              cont.setMutableState(linkAttrib, currentObj);

              // invoke c.createObject
              try {
                Object oldVal = c.getCreator().setProperty(
                    "show.message.popup", false);
                c.createObject();
                c.getCreator().setProperty("show.message.popup", oldVal);
              } catch (Exception e) {
                if (errors == null)
                  errors = new HashMap<>();
                errors.put(c, e);
                // c.getCreator().displayError(MessageCode.ERROR_HANDLE_COMMAND,
                // c,
                // "Lỗi xử lý lệnh chương trình {0}", e, Create);
              }
            }
            // else {
            // // debug
            // log(MessageCode.UNDEFINED,
            // "{0}.run(): child: {1} is NOT in Editing",
            // RunCreateChildAssociatedObjects.class.getSimpleName(), c);
            // }
          }
        }
        setIsStopped(true);
      }

      /**
       * @effects if there were errors that occured during the execution of the
       *          child controllers display them on the data containers of those
       *          controllers else do nothing
       */
      public void displayErrorsIfAny() {
        if (errors != null && !errors.isEmpty()) {
          DataController dctl;
          Exception e;
          for (Entry<DataController, Exception> entry : errors.entrySet()) {
            dctl = entry.getKey();
            e = entry.getValue();
            dctl.getCreator().displayErrorFromCode(MessageCode.ERROR_HANDLE_COMMAND, dctl, e, Create);
          }
        }
      }
    } // end RunCreateChildAssociatedObjects

    /**
     * This method only returns the objects currently loaded in the buffer
     * (which is in general a sub-set of those whose ids are in the browser)
     * 
     * @effects if this is opened return an Iterator of the domain objects
     *          currently in the buffer else return null
     */
    public Iterator<C> getObjectBuffer() {
      if (isOpened()) {
        return browser.getObjectBuffer();
      } else {
        return null;
      }
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
      return controllerCfg.hasProperty(propName, val);
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
      return controllerCfg.getProperty(propName, defaultVal);
    }
//    /**
//     * @version 3.0
//     */
//    public ControllerConfig getControllerConfig() {
//      return controllerCfg;
//    }

    /* (non-Javadoc)
     * @see domainapp.basics.modules.ModuleService#getContext()
     */
    /**
     * @effects 
     *  call {@link #controller}.getContext()
     * @version 
     */
    @Override
    public Context getContext() {
      return controller.getContext();
    }

    /* (non-Javadoc)
     * @see domainapp.basics.modules.ModuleService#isDataService(java.lang.Class)
     */
    /**
     * @effects
     *  perform the specified check on <tt>serviceCls</tt> against {@link DataController}
     */
    @Override
    public boolean isDataService(Class serviceCls) {
      return serviceCls != null && DataController.class.isAssignableFrom(serviceCls);
    }

    /* (non-Javadoc)
     * @see domainapp.basics.modules.ModuleService#getModule()
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public Module getModule() {
      return getCreator();
    }

    /* (non-Javadoc)
     * @see domainapp.basics.modules.ModuleService#getServiceMethod(domainapp.basics.core.ControllerBasic.MethodName)
     */
    @Override
    public Method getServiceMethod(MethodName methodName, Class[] paramTypes) {
      /* v5.6:
      return Toolkit.getMethod(this.getClass(), methodName.name(), paramTypes);
      */
      return Toolkit.getMethodWithOptionalParams(this.getClass(), methodName.name(), paramTypes);
    }

    /* (non-Javadoc)
     * @see domainapp.basics.modules.ModuleService#activateView()
     */
    /**
     * @effects 
     * 
     * @version 
     */
    @Override
    public void activateView() {
      /* v5.2: moved to method

      if (!dataContainer.isVisible())
        controller.showDataContainer(dataContainer);
      
      controller.activateDataContainer(dataContainer);
      */
      
      controller.activateView(dataContainer);
    }

  } // end DataController

  /* (non-Javadoc)
   * @see domainapp.basics.modules.ModuleService#getContext()
   */
  /**
   * @effects 
   *  return the specified {@link Context} as {@link #mainCtl}. 
   */
  @Override
  public Context getContext() {
    return getMainController();
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.ModuleService#isDataService(java.lang.Class)
   */
  /**
   * @effects
   *  perform the specified check on <tt>serviceCls</tt> against {@link DataController}
   */
  @Override
  public boolean isDataService(Class serviceCls) {
    return serviceCls != null && DataController.class.isAssignableFrom(serviceCls);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.ModuleService#getModule()
   */
  /**
   * @effects 
   *  return this
   */
  @Override
  public Module getModule() {
    return this;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.ModuleService#getServiceMethod(domainapp.basics.core.ControllerBasic.MethodName)
   */
  /**
   * @effects 
   *  return the specified {@link Method} in this.class 
   */
  @Override
  public Method getServiceMethod(MethodName methodName, Class[] paramTypes) throws NotFoundException {
    /* v5.6
    return Toolkit.getMethod(this.getClass(), methodName.name(), paramTypes);
    */
    return Toolkit.getMethodWithOptionalParams(this.getClass(), methodName.name(), paramTypes);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.ModuleService#hasView()
   */
  /**
   * @effects 
   *  call {@link #hasGUI()} 
   */
  @Override
  public boolean hasView() {
    return hasGUI();
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.ModuleService#activateView()
   */
  /**
   * @effects 
   *  call {@link #activateDataContainer(JDataContainer)} on {@link #getRootDataController()}.dataContainer
   */
  @Override
  public void activateView() {
    JDataContainer dataContainer = getRootDataController().getDataContainer();
    
    /* v5.2: moved to method
    if (!dataContainer.isVisible())
      showDataContainer(dataContainer);
    
    activateDataContainer(dataContainer);
    */
    activateView(dataContainer);
  }
  

  /**
   * @effects 
   *  make <tt>dataContainer</tt> visible (if not already) and expanded into view.
   *  
   * @version 5.2 
   */
  public void activateView(JDataContainer dataContainer) {
    if (!dataContainer.isVisible())
      showDataContainer(dataContainer);
    
    activateDataContainer(dataContainer);
  }

  
  /* (non-Javadoc)
   * @see domainapp.basics.modules.Context#lookUpPrimaryService(java.lang.Class)
   */
  /**
   * @effects 
   *  call {@link #lookUpPrimary(Class)}
   */
  @Override
  public ModuleService lookUpPrimaryService(Class domainCls) {
    return lookUpPrimary(domainCls);
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.modules.Module#getDescendantDataService(java.lang.Class)
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public ModuleService getDescendantDataService(Class refCls) {
    /** the descendant data service is the descendant DataController of the root data controller
     * whose domain class is refCls */
    return rootDctl.getDescendantDataControllerOf(refCls);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.Module#getDescendantDataService(java.lang.Class)
   */
  /**
   * @effects 
   * 
   * @version 5.6
   */
  @Override
  public ModuleService getChildDataService(ModuleService parent, Class refCls) throws NotPossibleException {
    /** the descendant data service is the descendant DataController of the root data controller
     * whose domain class is refCls */
    if (parent instanceof DataController) {
      return rootDctl.getChildDataControllerOf((DataController) parent, refCls);
    } else {
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] {"Parent module service not supported: " + parent });
    }
    
  }
  
  /* (non-Javadoc)
   * @see domainapp.basics.modules.Module#getDefaultService()
   */
  /**
   * @effects 
   *  return this
   */
  @Override
  public ModuleService getDefaultService() {
    return this;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.Module#getController()
   */
  /**
   * @effects 
   *  return this
   */
  @Override
  public ControllerBasic getController() {
    return this;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.modules.Module#getModel()
   */
  /**
   * @effects 
   *  call {@link #getDomainClass()} 
   */
  @Override
  public Class getModel() {
    return getDomainClass();
  }
} // end Controller
