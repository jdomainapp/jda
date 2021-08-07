package jda.modules.setup.model;

import static jda.modules.setup.init.LogicalActionConstants.Any;
import static jda.modules.setup.init.LogicalActionConstants.CopyObject;
import static jda.modules.setup.init.LogicalActionConstants.Export;
import static jda.modules.setup.init.LogicalActionConstants.First;
import static jda.modules.setup.init.LogicalActionConstants.HelpButton;
import static jda.modules.setup.init.LogicalActionConstants.Last;
import static jda.modules.setup.init.LogicalActionConstants.Next;
import static jda.modules.setup.init.LogicalActionConstants.None;
import static jda.modules.setup.init.LogicalActionConstants.ObjectScroll;
import static jda.modules.setup.init.LogicalActionConstants.Open;
import static jda.modules.setup.init.LogicalActionConstants.Previous;
import static jda.modules.setup.init.LogicalActionConstants.Print;
import static jda.modules.setup.init.LogicalActionConstants.Refresh;
import static jda.modules.setup.init.LogicalActionConstants.Reload;
import static jda.modules.setup.init.LogicalActionConstants.Update;
import static jda.modules.setup.init.LogicalActionConstants.View;
import static jda.modules.setup.init.LogicalActionConstants.ViewCompact;

import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import jda.modules.chart.model.ChartWrapper;
import jda.modules.common.Toolkit;
import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.collection.Map;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.exportdoc.htmlpage.model.HtmlPage;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Organisation;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.module.DomainApplicationModule;
import jda.modules.mccl.conceptmodel.module.DomainApplicationModule.DomainApplicationModuleWrapper;
import jda.modules.mccl.conceptmodel.view.RegionName;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.Query;
import jda.modules.security.def.Action;
import jda.modules.security.def.DomainClassPerm;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.LogicalAction;
import jda.modules.security.def.LogicalPermission;
import jda.modules.security.def.LogicalResource;
import jda.modules.security.def.LoginUser;
import jda.modules.security.def.ModuleClassPerm;
import jda.modules.security.def.ObjectGroup;
import jda.modules.security.def.ObjectGroupMembership;
import jda.modules.security.def.PermType;
import jda.modules.security.def.Permission;
import jda.modules.security.def.PhysicalAction;
import jda.modules.security.def.PhysicalPermission;
import jda.modules.security.def.PhysicalResource;
import jda.modules.security.def.Resource;
import jda.modules.security.def.Role;
import jda.modules.security.def.RolePermission;
import jda.modules.security.def.UserRole;
import jda.modules.security.def.Resource.Type;
import jda.modules.setup.init.LogicalActionConstants;
import jda.modules.setup.model.SetUpBasic.MessageCode;
import jda.modules.setup.security.PermDefs;
import jda.mosa.model.Oid;
import jda.util.SwTk;

/**
 * A setup helper class responsible for setting up the security resources.
 * 
 * @author dmle
 * 
 */
public class SetUpSecurity {
  private DODMBasic dodm;
  
  private List<LogicalPermission> defaultPermissions;

  // v3.1: 
  //private static LogicalResource domainModuleClsResource;

  // v3.1: 
  //private static LogicalResource moduleClsResoure = null; 

  /** the security domain classes */
  public static Class[] SecurityClasses = new Class[] { //
  Action.class, LogicalAction.class, PhysicalAction.class, //
  ObjectGroup.class, ObjectGroupMembership.class, // v2.7.2
      Resource.class, LogicalResource.class, PhysicalResource.class, //
      Permission.class, LogicalPermission.class, PhysicalPermission.class, //
      LoginUser.class, DomainUser.class, //
      Role.class, //
      UserRole.class, //
      RolePermission.class, //
  };
  
  private static final boolean debug = Toolkit.getDebug(SetUpSecurity.class);
  private static final boolean loggingOn = Toolkit.getLoggingOn(SetUpSecurity.class);

  public SetUpSecurity(DODMBasic schema) {
    this.dodm = schema;
    defaultPermissions = new ArrayList();
  }

  void setUpSecurity(boolean serialisedConfig) throws DataSourceException {
    /* v2.8:
    if (serialisedConfig) {
      deleteSecuritySchema();
    }
    */

    // register the domain classes
    registerSecuritySchema(serialisedConfig);

    if (serialisedConfig) { // v2.8
      clearSecuritySchema();
    }

    createSecurityConfiguration(serialisedConfig);

    log(MessageCode.UNDEFINED,
        //"Hoàn thành!"
        "Completed!"
        );
  }

  public void deleteSecuritySchema() throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Xóa mô hình bảo mật"
        "Deleting security configuration"
        );
    
    /* v2.8: use dodm
    if (debug) log(MessageCode.UNDEFINED,
        //"  Xóa các ràng buộc"
        "Deleting constraints"
        );
    deleteDataSourceConstraints(SecurityClasses);
    
    if (debug) log(MessageCode.UNDEFINED,
        //"  Xóa các lớp"
        "Deleting security classes"
        );
    dodm.getDom().deleteClasses(SecurityClasses, true);
    */
    List<Class> lstSecurityClasses = new ArrayList();
    Collections.addAll(lstSecurityClasses, SecurityClasses);
    dodm.deleteClasses(lstSecurityClasses);
  }
  
  // v2.8: not used
//  protected void deleteDataSourceConstraints(Class[] classes) throws DataSourceException {
//    List<String> consNames;
//    for (Class c : classes) {
//      consNames = dodm.getDom().loadDataSourceConstraints(c);
//      if (consNames != null) {
//        for (String cons : consNames) {
//          if (debug) log(MessageCode.UNDEFINED,
//              //"  ràng buộc {0}"
//              "  constraint {0}"
//              , cons);
//          dodm.getDom().deleteDataSourceConstraint(c, cons);
//        }
//      }
//    }
//  }

  public void registerSecuritySchema(boolean serialisedConfig) throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Đăng ký mô hình bảo mật"
        "Registering security classes"
        );
    log(MessageCode.UNDEFINED,
        //"Tạo các phụ thuộc"
        "Creating dependencies"
        );
    dodm.registerEnumInterface(Resource.Type.class);

    /** v2.7.2: use the new api
    for (Class c : SecurityClasses) {
      schema.getDom().addClass(c, true, false);
    }*/
    // v2.7.4: added this line
    dodm.registerClasses(SecurityClasses);
    
    if (serialisedConfig)
      dodm.addClasses(SecurityClasses, false);
  }
  
  public void clearSecuritySchema() throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Xóa mô hình hiện tại"
        "Removing security configuration data"
        );
    boolean strict = false; // v3.0
    List<Class> classList = new ArrayList();
    Collections.addAll(classList, SecurityClasses);
    //v3.0: dodm.getDom().deleteObjects(SecurityClasses, strict);
    dodm.getDom().deleteObjects(classList, strict);
  }
  
  public void createSecurityConfiguration(boolean serialisedConfig) throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Tạo các chức năng"
        "Creating security configuration"
        );
    DOMBasic dom = dodm.getDom();
    
    //Iterator<LogicalAction> ait = LogicalActionConstants.actionIterator();
    List<LogicalAction> actions = Toolkit.getConstantObjects(
        LogicalActionConstants.class, LogicalAction.class);
    for (LogicalAction action: actions) {
      dom.addObject(action, serialisedConfig);
    }
    
    log(MessageCode.UNDEFINED,
        //"Tạo các tài nguyên"
        "Creating resources"
        );
    /**
     * RESOURCES
     */
    // any resource: resources that every user can access
    LogicalResource rAny = Resource.LogicalAny;
    dom.addObject(rAny, serialisedConfig);
    
    Object[][] anyResourceDefs = {
        {LoginUser.class, "Log into the system", Resource.Type.Object},
        {Configuration.class, "About the program", Resource.Type.Object},
        {Organisation.class, "About the organisation", Resource.Type.Object},
        {ApplicationModule.class, "About the modules", Resource.Type.Object},
        {ControllerConfig.class, "About the module controller configs", Resource.Type.Object},
        //{DomainApplicationModule.class, "Domain application module", Resource.Type.Object},
        {DomainApplicationModuleWrapper.class, "Domain application module wrapper", Resource.Type.Object},
        // v3.1: added Document for export module
        {DataDocument.class, "Văn bản xuất", Resource.Type.Object},
          {Page.class, "Văn bản web xuất", Resource.Type.Object},
          {HtmlPage.class, "Văn bản web xuất", Resource.Type.Object},
        {ChartWrapper.class, "", Resource.Type.Object},
    };
    Object[][] menuResourceDefs = {
        {RegionName.SearchToolBar.name(), "Search", Resource.Type.Menu}
    };
    
    LogicalResource rLogin = null, logRes;
    Class resCls;
    List<LogicalResource> anyResources = new ArrayList<LogicalResource>();
    List<LogicalResource> menuBasedResources = new ArrayList<LogicalResource>();

    for (Object[] anyResourceDef: anyResourceDefs) {
      resCls = (Class) anyResourceDef[0];
      logRes = createLogicalResource(
          resCls, 
          (String) anyResourceDef[1], 
          (Resource.Type) anyResourceDef[2], serialisedConfig);
      anyResources.add(logRes);

      if (resCls == LoginUser.class) { 
        // record login resource for later use
        rLogin = logRes;
      }
    }
    
    for (Object[] menuResourceDef: menuResourceDefs) {
      menuBasedResources.add(
          createLogicalResource(
              (String) menuResourceDef[0], 
              (String) menuResourceDef[1], 
              (Resource.Type) menuResourceDef[2], serialisedConfig)
          );
    }
    
    /**
     * PERMISSIONS
     */
    log(MessageCode.UNDEFINED,
        //"Tạo quyền"
        "Creating permissions"
        );
    // - create all permissions for the resource ANY
    //    two of which will be used later to assign to roles 
    Iterator<LogicalAction> ait = actions.iterator(); //LogicalAction.actionIterator();
    
    LogicalPermission perm;
    LogicalPermission disallowAny = null;
    LogicalPermission allowAny = null;
    LogicalAction a;
    while (ait.hasNext()) {
      a = ait.next();
      perm = createLogicalPermission(a,rAny, serialisedConfig);
      if (a == Any) {
        allowAny = perm;
      } else if (a == None) {
        disallowAny = perm;
      }
    }
    
    // default permissions
    LogicalPermission anyAllowLogin = null;
    LogicalPermission lperm;
    for (LogicalResource lres : anyResources) {
      lperm = createLogicalPermission(Any, lres, serialisedConfig); 
      defaultPermissions.add(lperm);
      if (lres == rLogin)
        anyAllowLogin = lperm;
    }
    
    // - open search tool bar
    /*v2.7
    LogicalPermission openSearchToolBar = createLogicalPermission(LogicalActionConstants.Open, rSearchToolBar);
    defaultPermissions.add(openSearchToolBar);
    */
    for (LogicalResource lres : menuBasedResources) {
      defaultPermissions.add(
          createLogicalPermission(LogicalActionConstants.Open, lres, serialisedConfig));
    }
    
    /**
     * ROLES and ROLE-PERMISSIONS
     */
    log(MessageCode.UNDEFINED,
        //"Tạo các vai trò mặc định"
        "Creating default roles"
        );    
    Role grole = createRole("guest","Đăng nhập ở chế độ khách", serialisedConfig); 
    // guest can login 
    RolePermission rp  = createRolePermission(grole, anyAllowLogin, serialisedConfig); 
    // but cannot access any protected resources
    rp = createRolePermission(grole,disallowAny, serialisedConfig);
    
    Role arole = createRole("admin", "Quản trị chương trình", serialisedConfig); 
    // admin can access any protected resources
    rp = createRolePermission(arole, allowAny, serialisedConfig); 
    
    log(MessageCode.UNDEFINED,
        //"Tạo tài khoản khởi động"
        "Creating guest account"
        );    
    DomainUser user = createUser("Khách","guest","guest", serialisedConfig);
    // guest user has guest role
    UserRole ur = createUserRole(user,grole,serialisedConfig); 
    user = createUser("Quản trị viên", "admin", "admin",serialisedConfig); 
    // admin user has admin role
    ur = createUserRole(user,arole,serialisedConfig); 
  }
  
  /**
   * @requires domainClass != null
   * @modifies {@link #dodm}
   * @effects 
   *    create and return a logical resource typed Object for domain class <tt>c</tt> in the data source
   *    <br>Throw DBException if fails
   * @version 3.1 
   */
  public LogicalResource createDomainClassResource(
      Class c, boolean serialisedConfig) throws DataSourceException {
    LogicalResource lr = createLogicalResource(c, "Objects: " + c.getSimpleName(), Resource.Type.Object, serialisedConfig);
    return lr;
  }
  
  /**
   * @requires domainClasses != null
   * @modifies {@link #dodm}
   * @effects 
   *  for each domain class c in domainClasses
   *    create a logical resource typed Object for c in the data source
   *    Throw DBException if fails
   *    add resource to map
   *  return map 
   *  
   */
  public java.util.Map<Object, LogicalResource> createDomainResources(
      Class[] domainClasses, boolean serialisedConfig) throws DataSourceException {
    // create logical resources from the domain classes
    log(MessageCode.UNDEFINED,
        //"Tạo tài nguyên dữ liệu"
        "Creating domain resources"
        );
    LogicalResource lr;
    java.util.Map<Object,LogicalResource> dataResources = new HashMap();    
    for (Class c : domainClasses) {
      if (c != null) {
        lr = createLogicalResource(c, "Objects: " + c.getSimpleName(), Resource.Type.Object, serialisedConfig);
        dataResources.put(c,lr);
        if (debug)
          log(MessageCode.UNDEFINED,"...{0}", lr);
      }
    }
    
    return dataResources;
  }
  
  /**
   * @effects 
   *    create a logical resource typed <tt>type</tt> for <tt>resourceClass</tt> in the data source. 
   *    Throws DBException if fails
   */
  public LogicalResource createLogicalResource(Class resourceClass, String desc, Type type, boolean serialisedConfig) throws DataSourceException {
    return createLogicalResource(
        //v2.7.2: schema.getDom().getDomainClassName(resourceClass), 
        dodm.getDsm().getResourceNameFor(resourceClass),
        desc, type, serialisedConfig);
  }

  /**
   * @effects 
   *    create a logical resource typed <tt>type</tt> for <tt>resourceClass</tt> in the data source. 
   *    Throws DBException if fails
   */
  public LogicalResource createLogicalResource(String resourceName, String desc, Type type, boolean serialisedConfig) throws DataSourceException {
    LogicalResource res = new LogicalResource(resourceName, desc, type);
    dodm.getDom().addObject(res, serialisedConfig);
    return res;
  }
  
  public LogicalPermission createLogicalPermission(LogicalAction act, LogicalResource resource, boolean serialisedConfig) 
  throws DataSourceException {
    LogicalPermission perm = new LogicalPermission(act, resource);
    dodm.getDom().addObject(perm, serialisedConfig);
    return perm;
  }
  
  public Role createRole(String name, String desc, boolean serialisedConfig) throws DataSourceException {
    Role role = new Role(name,desc);
    dodm.getDom().addObject(role, serialisedConfig);
    return role;
  }

  /**
   * @modifies <tt>anyPerms, dataResources</tt>
   * @effects 
   *  create <tt>LogicalPermission</tt>s for <tt>role</tt> as defined in <tt>permDefs</tt>, 
   *  which may contain a mixture of module and attribute permissions.
   *  
   *  <p>Create {@link LogicalResource}s as needed for the domain classes and add them to <tt>dataSources</tt>;
   *  also create <tt>ANY-typed</tt> {@link LogicalPermission} on the domain classes if needed and add them to <tt>anyPerms</tt>
   *  
   * @version 
   * - 3.1: created <br>
   * - 3.2c: change dataResources to Map(Object,...)
   */
  public void createRolePermissionsEnhanced(
      SetUpBasic su,
      Role role, PermDefs permDefs, 
      java.util.Map<LogicalResource,LogicalPermission> anyPerms, 
      java.util.Map<Object,LogicalResource> dataResources, final boolean serialisedConfig) throws DataSourceException {
    
    //DSMBasic dsm = this.dodm.getDsm();
    
    // modules map to record the modules and their permissions
    // NOTE: the modulesMap are not shared among the roles because module permissions use 
    // object group which is set in the logical resource of the application module's class.
    // This object group's membership differs between different roles.
    java.util.Map<PermType,Collection<ApplicationModule>> modulesMap = new HashMap();
    
    /*
     * create permissions on modules (if any): 
     *  - the same permission is also set on the domain class of each module 
     */
    Collection<ModuleClassPerm> modulePerms = permDefs.getModulePerms();
    if (modulePerms != null) {
      String moduleName;
      DomainClassPerm clsPerm;
      ApplicationModule module;
      PermType permType;
      for (ModuleClassPerm modPerm : modulePerms) {
        moduleName = modPerm.getModuleName();
        permType = modPerm.getType();
        clsPerm = modPerm.getDomainClsPerm();
        
        // create domain class permission (but without retrieving its module)
        createRolePermissionForDomainClass(su, role, clsPerm, anyPerms, dataResources, null, 
            serialisedConfig);
        
        // look up the actual module object
        module = retrieveModuleByName(moduleName, serialisedConfig);
        CollectionToolkit.updateCollectionBasedMap(modulesMap, permType, module);
      } // end for
    } // end module permissions
        
    // create attribute permissions (if any)
    Collection<DomainClassPerm> attribPerms = permDefs.getAttribPerms();
    if (attribPerms != null) {
      //DomainClassPerm attribPerm;
      Class c;
      PermType permType;
      String attrib;
      //for (int j = 0; j < permDefs.getAttribPerms().size(); j++) {
      for (DomainClassPerm attribPerm : attribPerms) {
        c = attribPerm.getDomainCls();
        permType = attribPerm.getType();
        attrib = attribPerm.getAttrib();
        createRolePermissionForAttribute(role, c, permType, attrib, dataResources, serialisedConfig);
      }
    } // end attribute perms

    // record modules into role as the allowed modules 
    // NOTE: the modulesMap are not shared among the roles because module permissions use 
    // object group which is set in the logical resource of the application module's class.
    // This object group's membership differs between different roles.
    createRoleAllowedModules(role, modulesMap, serialisedConfig);
  }

  /**
   * @modifies dataResources
   *  
   * @effects 
   *  create suitable {@link RolePermission}(s) for <tt>role</tt> with permission <tt>permType</tt> 
   *  over the domain attribute: <tt>c.attrib</tt>.
   *  
   *  <p>Create {@link LogicalResource}s as needed for <tt>attrib</tt> and add it to <tt>dataSources</tt>;
   *  
   *  <p>Throws DataSourceException if failed to create in data source
   */
  private void createRolePermissionForAttribute(Role role, Class c, PermType permType, String attrib, java.util.Map<Object, LogicalResource> dataResources, boolean serialisedConfig) throws DataSourceException {
    DSMBasic dsm = dodm.getDsm();
    String attribResName = dsm.getResourceNameFor(c, attrib);
    String desc = "Attribute " + attribResName;
    Resource.Type type = Resource.Type.Attribute;
    
    if (debug) 
      System.out.printf("Attribute permission:%n  domain class: %s%n  attribute: %s%n  resource name: %s%n  perm type: %s%n", 
        c.getSimpleName(), attrib, attribResName, permType);

    LogicalResource r; 
    LogicalAction[] actions = null;
    LogicalPermission perm;
    
    // v3.2c: support shared attribute resource
    r = dataResources.get(attribResName);
    if (r == null) {
      r = createLogicalResource(attribResName, desc, type, serialisedConfig);
      dataResources.put(attribResName, r);
    }
    
    if (permType.equals(PermType.READ_WRITE)) {
      actions = getReadWriteActionsFor(type);
    } else if (permType.equals(PermType.READ_ONLY)) {
      actions = getReadOnlyActionsFor(type);
    } else {
      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
          new Object[] {"permission type = " + permType});
    }
    
    if (actions != null) {
      for (LogicalAction act : actions) {
        perm = createLogicalPermission(act, r, serialisedConfig);
        createRolePermission(role, perm, serialisedConfig);
      }  
    }
  }

  /**
   *  @modifies modules
   *  @effects 
   *    if exists {@link ApplicationModule} whose name is <tt>moduleName</tt>
   *      retrieve and return it 
   *    else
   *      throw NotFoundException 
   */
  private ApplicationModule retrieveModuleByName(String moduleName, boolean serialisedConfig) throws NotPossibleException, NotFoundException, DataSourceException {
    ApplicationModule module;
    if (serialisedConfig)
      module = SwTk.retrieveModuleByName(dodm, moduleName);
    else
      module = SwTk.getModuleByName(dodm, moduleName);

    if (module == null)
      throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, new Object[] {moduleName});

    return module;
  }

  /**
   * @effects 
   *    create an {@link ObjectGroup} permission for <tt>modules</tt>
   * @version 3.1 
   */
  private void createRoleAllowedModules(Role role, 
      java.util.Map<PermType, Collection<ApplicationModule>> modulesMap, boolean serialisedConfig) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    /* for each permission type (permType) in modulesMap 
     *  create new logical resource for moduleCls
     *  create in this resource an object group permission for the modules in modulesMap that are mapped to permType
     *  create suitable logical permission(s) for this resource based on permType 
     */
    Class<ApplicationModule> moduleCls = ApplicationModule.class;
    PermType permType;
    Collection<ApplicationModule> modules;
    LogicalResource moduleClsResoure;
    ObjectGroup grp;
    for (Entry<PermType,Collection<ApplicationModule>> entry : modulesMap.entrySet()) {
      permType = entry.getKey();
      modules = entry.getValue();
      moduleClsResoure = createLogicalResource(moduleCls, 
          "Domain module permissions for: " + role.getName(), Type.Object, serialisedConfig);
      
      grp = createObjectGroupForResource(moduleClsResoure, moduleCls, modules, serialisedConfig);
      
      dom.updateObject(moduleClsResoure, null, serialisedConfig);  // update the resource afterwards
      
      // debug
      // System.out.printf("Object group permission for modules resource:%n  Resource: %s%n  Object group: %s%n", moduleClsResoure, grp);
      
      // create permission for the module resource
      createModulePermission(role, moduleClsResoure, permType, serialisedConfig);
    }
  }

  /**
   * @modifies <tt>anyPerms, dataResources</tt>
   * @effects 
   *  create suitable {@link LogicalPermission} on <tt>clsPerm</tt> for <tt>role</tt>, 
   *  and if <tt>modules != null</tt> then retrieve the first module of <tt>clsPerm.domainClass</tt>
   *  and add it to <tt>modules</tt>
   *  
   *  <p>Create {@link LogicalResource}s as needed for the domain class and add it to <tt>dataSources</tt>;
   *  also create <tt>ANY-typed</tt> {@link LogicalPermission} on the domain class if needed and add it to <tt>anyPerms</tt>
   */
  private void createRolePermissionForDomainClass(
      SetUpBasic su,
      Role role, 
      DomainClassPerm clsPerm,
      java.util.Map<LogicalResource,LogicalPermission> anyPerms, 
      java.util.Map<Object,LogicalResource> dataResources, 
      Collection<DomainApplicationModule> modules,
      final boolean serialisedConfig) throws DataSourceException {
    Class c;
    PermType permType;
    
    c = clsPerm.getDomainCls();
    permType = clsPerm.getType();

    createRolePermissionForDomainClass(su, role, c, permType, anyPerms, dataResources, modules, serialisedConfig);
  }

  /**
   * @modifies <tt>anyPerms, dataResources</tt>
   * @effects 
   *  create suitable {@link LogicalPermission} on <tt>domainCls</tt> (and all of its descendant domain classes (if any)) 
   *  for <tt>role</tt>, 
   *  and if <tt>modules != null</tt> then retrieve the first module of <tt>clsPerm.domainClass</tt>
   *  and add it to <tt>modules</tt>
   *  
   *  <p>Create {@link LogicalResource}s as needed for the domain class and add it to <tt>dataSources</tt>;
   *  also create <tt>ANY-typed</tt> {@link LogicalPermission} on the domain class if needed and add it to <tt>anyPerms</tt>
   *  
   */
  private void createRolePermissionForDomainClass(
      SetUpBasic su,
      Role role, 
      Class domainCls, PermType permType, 
      java.util.Map<LogicalResource,LogicalPermission> anyPerms, 
      java.util.Map<Object,LogicalResource> dataResources, 
      Collection<DomainApplicationModule> modules,
      final boolean serialisedConfig) throws DataSourceException {
    Collection<Class> subTypes;
    DomainApplicationModule module = null;
    LogicalPermission perm;
    //RolePermission rperm;
    LogicalResource lr;
    
    lr = dataResources.get(domainCls);

    if (lr == null) {
      // logical resource of the domain class not yet created, create it
      lr = createDomainClassResource(domainCls, serialisedConfig);
      dataResources.put(domainCls, lr);
    }
    
    if (debug) 
      System.out.printf("Class permission:%n  domain class: %s%n  resource: %s%n  perm type: %s%n", 
        domainCls.getSimpleName(), lr.getName(), permType);
    
    if (modules != null) {
      if (serialisedConfig)
        module = (DomainApplicationModule) SwTk.retrieveModule(dodm, domainCls);
      else
        module = (DomainApplicationModule) SwTk.getModule(dodm, domainCls);
      
      if (module == null)
        throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, new Object[] {domainCls.getSimpleName()});

      if (debug) System.out.printf("  module: %s%n", module.getName());
    }
    
    
    if (lr != null) {
      if (permType.equals(PermType.ANY) 
            || permType.equals(PermType.READ_WRITE)// READ-WRITE for class means ANY
          ) {
        perm = anyPerms.get(lr);
        
        if (perm == null) {
          // any permission not yet created for logical resource: create it
          perm = createAnyPermission(lr, serialisedConfig);
          anyPerms.put(lr, perm);
        }
        
        createRolePermission(role, perm, serialisedConfig);
      } else if (permType.equals(PermType.READ_ONLY)
          || permType.equals(PermType.VIEW)
          ) {
        createReadOnlyPermissions(role, lr, serialisedConfig);
      } else {
        // not supported
        throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, new Object[] {permType.name()});
      }
      /* v2.7.2: record domain application module */
      if (modules != null) modules.add(module);
    }
    
    // process sub-types (if any): apply the same permission on each sub-type
    subTypes = su.getSubClasses(domainCls);
    if (subTypes != null) {
      for (Class sub : subTypes) {
        createRolePermissionForDomainClass(su, role, sub, permType, anyPerms, dataResources, modules, serialisedConfig);
      }
    }    
  }

  /**
   * This method only supports domain class permissions. Use {@link #createRolePermissionsEnhanced(SetUpBasic, Role, PermDefs, java.util.Map, java.util.Map, boolean)}
   * to also enable module descriptor class permissions.
   * 
   * @effects 
   *  create <tt>LogicalPermission</tt>s for <tt>role</tt> as defined in <tt>permDefs</tt>
   *  
   * @deprecated (as of version 3.1) use {@link #createRolePermissionsEnhanced(SetUpBasic, Role, PermDefs, java.util.Map, java.util.Map, boolean)}
   */
  public void createRolePermissions(
      SetUpBasic su,
      Role role, PermDefs permDefs, 
      java.util.Map<LogicalResource,LogicalPermission> anyPerms, 
      java.util.Map<Object,LogicalResource> dataResources, boolean serialisedConfig) throws DataSourceException {
    LogicalPermission perm;
    RolePermission rperm;
    LogicalResource lr;
    
    DSMBasic dsm = this.dodm.getDsm();
    DOMBasic dom = this.dodm.getDom();
    
    // v2.7.2: support permissions on the domain module
    Class<DomainApplicationModule> moduleCls = DomainApplicationModule.class;
    List<DomainApplicationModule> modules;
    //SetUpConfigBasic sufg = su.createSetUpConfigurationBasicInstance();

    //int classPermsCount = permDefs.getClassPerms().size();
    Collection<DomainClassPerm> classPerms = permDefs.getClassPerms();
    
    modules = new ArrayList<DomainApplicationModule>();
    
    //DomainClassPerm clsPerm;
    Class c;
    Collection<Class> subTypes;
    DomainApplicationModule module;
    PermType permType;
    
    /* create class permissions (if any)
     * for each domain class c in permDefs
     *    create a role permission for specified role and the data resource of c 
     */
    //for (int i = 0; i < classPermsCount; i++) {
    for (DomainClassPerm clsPerm : classPerms) {
      //clsPerm = permDefs.getClassPerms().get(i);
      c = clsPerm.getDomainCls();
      permType = clsPerm.getType();
      lr = dataResources.get(c);
      if (debug) 
        System.out.printf("Class permission:%n  domain class: %s%n  resource: %s%n  perm type: %s%n", 
          c.getSimpleName(), lr.getName(), permType);
      
      if (serialisedConfig)
        module = (DomainApplicationModule) SwTk.retrieveModule(dodm, c);
      else
        module = (DomainApplicationModule) SwTk.getModule(dodm, c);
      
      if (module == null)
        throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, new Object[] {c.getSimpleName()});
      
      if (debug) System.out.printf("  module: %s%n", module.getName());
      
      if (lr != null) {
        if (permType.equals(PermType.ANY)) {
          perm = anyPerms.get(lr);
          createRolePermission(role, perm, serialisedConfig);
        } else if (permType.equals(PermType.READ_ONLY)) {
          createReadOnlyPermissions(role, lr, serialisedConfig);
        } 
        // READ-WRITE for class means ANY
        /* v2.7.2: record domain application module */
        if (module != null) modules.add(module);
      }
      
      // process sub-types (if any): apply the same permission on each sub-type
      subTypes = su.getSubClasses(c);
      if (subTypes != null) {
        for (Class sub : subTypes) {
          lr = dataResources.get(sub);
          if (debug) System.out.printf("Sub-class permission:%n  domain class: %s%n  resource: %s%n  perm type: %s%n", 
              sub.getSimpleName(), lr.getName(), permType);
          
          //module = (DomainApplicationModule) sufg.retrieveModule(sub);
          if (serialisedConfig)
            module = (DomainApplicationModule) SwTk.retrieveModule(dodm, sub);
          else
            module = (DomainApplicationModule) SwTk.getModule(dodm, sub);

          if (module == null)
            throw new NotFoundException(NotFoundException.Code.MODULE_NOT_FOUND, new Object[] {sub.getSimpleName()});

          if (debug) System.out.printf("  module: %s%n",module.getName());

          if (lr != null) {
            if (permType.equals(PermType.ANY)) {
              perm = anyPerms.get(lr);
              createRolePermission(role, perm, serialisedConfig);
            } else if (permType.equals(PermType.READ_ONLY)) {
              createReadOnlyPermissions(role, lr, serialisedConfig);
            } 
            // READ-WRITE for class means ANY
            /* v2.7.2: record domain application module */
            if (module != null) 
              modules.add(module);
          }
        }
      }
    } // end class perms
    
    // create attribute permissions (if any)
    Collection<DomainClassPerm> attribPerms = permDefs.getAttribPerms();
    if (attribPerms != null) {
      //DomainClassPerm attribPerm;
      String attrib;//, attribName;
      //String desc;
      //for (int j = 0; j < permDefs.getAttribPerms().size(); j++) {
      for (DomainClassPerm attribPerm : attribPerms) {
        //attribPerm = permDefs.getAttribPerms().get(j);
        c = attribPerm.getDomainCls();
        permType = attribPerm.getType();
        attrib = attribPerm.getAttrib();
        /* v3.1: use new method
        attribName = dsm.getResourceNameFor(c, attrib);
        desc = "Attribute " + attribName;
        Resource.Type type = Resource.Type.Attribute;
        
        if (debug) System.out.printf("Attribute permission:%n  domain class: %s%n  attribute: %s%n  resource name: %s%n  perm type: %s%n", 
            c.getSimpleName(), attrib, attribName, permType);

        if (permType.equals(PermType.READ_WRITE))
          createReadWritePermission(role, attribName, desc, type, serialisedConfig);
        */
        createRolePermissionForAttribute(role, c, permType, attrib, dataResources, serialisedConfig);
      }
    } // end attribute perms

    //v2.7.2: create group permission for the application modules
    LogicalResource domainModuleClsResource = createLogicalResource(moduleCls, 
        "Domain module permissions for: " + role.getName(), Type.Object, serialisedConfig);
    
    createObjectGroupForResource(domainModuleClsResource, moduleCls, modules, serialisedConfig);
    dom.updateObject(domainModuleClsResource, null, serialisedConfig);  // update the resource afterwards
    
    // create permission for the module resource
    createReadOnlyPermissions(role, domainModuleClsResource, serialisedConfig);
  }
  
  public RolePermission createRolePermission(Role role, LogicalPermission perm, boolean serialisedConfig) throws DataSourceException {
    RolePermission rp = new RolePermission(role, perm);
    dodm.getDom().addObject(rp, serialisedConfig);
    
    return rp;
  }
  
  /**
   * @throws DataSourceException 
   * @effects 
   *  creates suitable {@link LogicalPermission}(s) for <tt>permType</tt> 
   *  on module resource <tt>resource</tt> and 
   *          assign them to <tt>role</tt>; throws DataSourceException if an error occured
   */
  private void createModulePermission(Role role,
      LogicalResource resource, PermType permType,
      boolean serialisedConfig) throws DataSourceException {
    Collection<LogicalAction> acts = new ArrayList();
    
    if (permType.equals(PermType.VIEW)) {
      // just use View action
      acts.add(View);
    } else if (permType.equals(PermType.READ_ONLY)
        || permType.equals(PermType.READ_WRITE) ){
      // use Open action
      acts.add(Open);
    } else if (permType.equals(PermType.ANY)){
      // all of the above
      acts.add(Open);
      acts.add(View);
    }

    LogicalPermission perm;
    for (LogicalAction act : acts) {
      perm = createLogicalPermission(act, resource, serialisedConfig);
      createRolePermission(role, perm, serialisedConfig);      
    }
  }

//  /**
//   * @effects creates all read-only logical permissions on module resource <tt>resource</tt> and 
//   *          assign them to <tt>role</tt>; throws DataSourceException if an error occured
//   */
//  public void createReadOnlyModulePermission(Role role, LogicalResource resource, boolean serialisedConfig) throws DataSourceException {
//    // just need to use one of the actions
//    final LogicalAction act = Open; 
//    LogicalPermission perm;
//    perm = createLogicalPermission(act, resource, serialisedConfig);
//    createRolePermission(role, perm, serialisedConfig);
//  }
  
  /**
   * @effects creates all read-only logical permissions on <tt>resource</tt> and 
   *          assign them to <tt>role</tt>; throws DBException if an error occured
   */
  private void createReadOnlyPermissions(Role role, LogicalResource resource, boolean serialisedConfig) throws DataSourceException {
    
    final LogicalAction[] READ_ONLY = getReadOnlyActionsFor(resource.getType()); 
      //{Open, First, Next, Previous, Last, Refresh };
    LogicalPermission perm;
    for (LogicalAction act : READ_ONLY) {
      perm = createLogicalPermission(act, resource, serialisedConfig);
      createRolePermission(role, perm, serialisedConfig);
    }    
  }
  
//  /**
//   * @effects 
//   *  create logical resource r for <tt>attribName</tt> with description <tt>desc</tt>
//   *  and create a logical permission <tt>< role, r > </tt>.
//   *  Throws DBException if fails.
//   */
//  private void createReadWritePermission(Role role, String attribName,
//      String desc, Type type, boolean serialisedConfig) throws DataSourceException {
//    LogicalResource r = createLogicalResource(attribName, desc, type, serialisedConfig);
//    
//    LogicalAction[] actions = getReadWriteActionsFor(type);
//    if (actions != null) {
//      LogicalPermission perm;
//      for (LogicalAction act : actions) {
//        perm = createLogicalPermission(act, r, serialisedConfig);
//        createRolePermission(role, perm, serialisedConfig);
//      }  
//    }
//  }

  /**
   * @modifies res 
   * @effects
   *  create <tt>ObjectGroup</tt> for <tt>objects</tt> of the class <tt>c</tt>, 
   *  store them into the data source; and updates 
   *  <tt>res</tt> to contain this group. 
   *  
   *  <p>Return the created object group
   */
  private <T> ObjectGroup createObjectGroupForResource(LogicalResource res, Class<T> c, Collection<T> objects, boolean serialisedConfig) throws DataSourceException {
    Collection<Integer> idHashes = new ArrayList();
    Oid oid;
    int hash;
    
    for (T o : objects) {
      oid = dodm.getDom().lookUpObjectId(c, o);
      hash = oid.hashCode();
      idHashes.add(hash);
    }
    
    ObjectGroup objectGroup1 = new ObjectGroup(res);
    
    if (debug) log(MessageCode.UNDEFINED,"Object group: {0}", objectGroup1);
      
    res.setObjectGroup(objectGroup1);
    
    dodm.getDom().addObject(objectGroup1, serialisedConfig);
    
    ObjectGroupMembership om1;
    for (int idHash : idHashes) {
      om1 = new ObjectGroupMembership(objectGroup1, idHash);
      
      if (debug) log(MessageCode.UNDEFINED,"  Membership: {0}", om1);
      
      dodm.getDom().addObject(om1, serialisedConfig);
    }
    
    return objectGroup1;
  }

//  /**
//   * @effects 
//   *  return suitable set of {@link LogicalAction}s for <tt>permType</tt> and <tt>type</tt>; 
//   *  or return <tt>null</tt> if no such set exists.
//   * @version 3.1
//   */
//  private LogicalAction[] getActionsFor(PermType permType, Type type) {
//    if (permType.equals(PermType.READ_ONLY)) {
//      return getReadOnlyActionsFor(type);
//    } else if (permType.equals(PermType.READ_WRITE)) {
//      return getReadWriteActionsFor(type);
//    } else if (permType.equals(PermType.VIEW)) {
//      return getViewActionsFor(type);
//    } else {
//      // no need to support this
//      //throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, new Object[] {permType.name()});
//      return null;
//    }
//  }

  private LogicalAction[] getViewActionsFor(Type type) {
    // = READ-ONLY actions + View action
    LogicalAction[] actions = null;
    
    if (type == Resource.Type.Object) {
      actions = new LogicalAction[] {
          // read-only actions
          Open, Export, Print, First, Next, Previous, Last, Refresh, Reload, ViewCompact, 
          ObjectScroll, HelpButton, 
          // view action
          View
          };
    }
    
    return actions;
    
  }

  private LogicalAction[] getReadOnlyActionsFor(Resource.Type type) {
    LogicalAction[] actions = null;
    
    if (type == Resource.Type.Object) {
      // domain objects
      actions =
          /**these must be derived from {@link LAName#READ_ONLY_ACTION_NAMES} */
          new LogicalAction[] {
          Open, Export, Print, First, Next, Previous, Last, Refresh, Reload, ViewCompact, 
          ObjectScroll, CopyObject, HelpButton, View
          };
    } else if (type == Resource.Type.Attribute) {
      // attribute: just need one action
      actions = new LogicalAction[] {View};      
    } else {
      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
          new Object[] {"resource type: " + type});
    }
    
    return actions;
  }

  private LogicalAction[] getReadWriteActionsFor(Resource.Type type) {
    LogicalAction[] actions = null;
    
    if (type == Resource.Type.Attribute) {
      actions = new LogicalAction[] {
          Update
      };
    } else {
      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED, 
          new Object[] {"resource type: " + type});
    }
    
    return actions;
  }
  
  /**
   * @effects
   *  if  {@link #defaultPermissions} = null
   *    do nothing
   *  else 
   *    for each permission p in <tt>defaultPermissions</tt>
   *      for each role r in roles
   *        create role-permission <r,p>
   *        throws DBException if fails
   */
  public void createDefaultRolePermissions(boolean serialisedConfig, Role...roles) throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Tạo các quyền mặc định"
        "Creating default permissions"
        );
    if (defaultPermissions == null || roles.length == 0) {
      return;
    }
    
    for (Role role : roles) {
      RolePermission rperm;
      for (LogicalPermission perm : defaultPermissions) {
        rperm = createRolePermission(role, perm, serialisedConfig);
        if (debug)
          log(MessageCode.UNDEFINED,"...{0}", rperm);
      }
    }
    
  }
  
  /**
   * @requires 
   *  <tt>admin != null && dataResources != null</tt>
   * @effects
   *  for each logical resource r in dataResources
   *    create an any-r permission and assign this to role admin
   *    Throws DBException if fails to create
   *    add <r,permission> to map
   *  return map  
   */
  public java.util.Map<LogicalResource, LogicalPermission> createAdminRolePermissions(
      Role admin,
      java.util.Map<Object, LogicalResource> dataResources, boolean serialisedConfig) throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Tạo quyển cho {0}"
        "Creating permission(s) for {0}"
        , admin.getName());
    
    Map<LogicalResource,LogicalPermission> anyPerms = new Map();
    RolePermission rperm;
    LogicalPermission perm;
    for (LogicalResource lres : dataResources.values()) {
      perm = createLogicalPermission(Any, lres, serialisedConfig);
      anyPerms.put(lres, perm);
      rperm = createRolePermission(admin, perm, serialisedConfig);
      if (debug)
        log(MessageCode.UNDEFINED,"...{0}", rperm);
    }
    
    // v2.7.2: add admin permission on the application module
    Class moduleCls = DomainApplicationModule.class;
    LogicalResource domainModuleClsResource = createLogicalResource(moduleCls, 
        "Domain module permissions for: " + admin.getName(), Type.Object, serialisedConfig);
    
    // create permission for the module resource
    perm = createLogicalPermission(Any, domainModuleClsResource, serialisedConfig);
    createRolePermission(admin, perm, serialisedConfig);

    return anyPerms;
  }
  

  /**
   * @effects 
   *  create in the data source and return <tt>ANY</tt> {@link LogicalPermission} on <tt>lr</tt>
   * @version 3.1 
   */
  private LogicalPermission createAnyPermission(LogicalResource lr, boolean serialisedConfig) throws DataSourceException {
    LogicalPermission perm = createLogicalPermission(Any, lr, serialisedConfig);
    return perm;    
  }
  
  /**
   * @requires 
   *  <tt>dataResources != null</tt>
   * @effects
   *  for each logical resource r in dataResources
   *    create an any-r permission and 
   *    add <r,permission> to map
   *  return map  
   */
  public java.util.Map<LogicalResource, LogicalPermission> createAnyPermissions(
      java.util.Map<Class, LogicalResource> dataResources, boolean serialisedConfig) throws DataSourceException {
    log(MessageCode.UNDEFINED,
        //"Tạo quyển cho {0}"
        "Creating ANY permission(s)...");
    
    Map<LogicalResource,LogicalPermission> anyPerms = new Map();
    //RolePermission rperm;
    LogicalPermission perm;
    for (LogicalResource lres : dataResources.values()) {
      perm = createLogicalPermission(Any, lres, serialisedConfig);
      anyPerms.put(lres, perm);
      //rperm = createRolePermission(admin, perm, serialisedConfig);
//      if (debug)
//        log(MessageCode.UNDEFINED,"...{0}", rperm);
    }
    
//    // v2.7.2: add admin permission on the application module
//    Class moduleCls = DomainApplicationModule.class;
//    LogicalResource modRes = createLogicalResource(moduleCls, 
//        "Domain module permissions for: " + admin.getName(), Type.Object, serialisedConfig);
//    // create permission for the module resource
//    perm = createLogicalPermission(Any, modRes, serialisedConfig);
//    createRolePermission(admin, perm, serialisedConfig);

    return anyPerms;
  }
  
  public DomainUser createUser(String name, String login, String password, boolean serialisedConfig) throws DataSourceException {
    DomainUser user = new DomainUser(name, login, password);
    dodm.getDom().addObject(user, serialisedConfig);
    return user;
  }
  
  public UserRole createUserRole(DomainUser user, Role role, boolean serialisedConfig) throws DataSourceException {
    UserRole ur = new UserRole(user,role);
    dodm.getDom().addObject(ur, serialisedConfig);
    return ur;
  }
  
  /**
   * @effects if <tt>Role</tt> named <tt>name</tt> exists in the object pool or data source then return it; 
   *          otherwise throws <tt>NotFoundException</tt>.
   * @version 3.3
   */
  public Role retrieveRole(String name) throws NotFoundException, NotPossibleException, DataSourceException  {
    Role role = dodm.getDom().retrieveObject(Role.class, Role.Attribute_name, Op.EQ, name);
    if (role == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {Role.class, name});
    }
    
    return role;
  }
  
  /**
   * @effects 
   *  if exists objects of class <tt>c</tt> 
   *    return all of them
   *  else  
   *    throws NotFoundException 
   * @version 3.3
   */
  public <T> Collection<T> retrieveObjects(Class<T> c) throws NotPossibleException, NotFoundException, DataSourceException {
    java.util.Map<Oid,T> objs = dodm.getDom().retrieveObjects(c);
    
    if (objs == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {c, ""});
    }
    
    return objs.values();
  }
  
  /**
   * @effects if <tt>DomainUser</tt> whose login is <tt>login</tt> exists in the object pool or data source then return it; 
   *          otherwise throws <tt>NotFoundException</tt>.
   * @version 3.3
   */
  public DomainUser retrieveDomainUser(String login) throws NotFoundException, NotPossibleException, DataSourceException  {
    DomainUser user = dodm.getDom().retrieveObject(DomainUser.class, DomainUser.Attribute_login, Op.EQ, login);
    if (user == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {DomainUser.class, login});
    }
    
    return user;
  }
  
  /**
   * @effects if <tt>Role</tt> named <tt>name</tt> exists in the object pool then return it; 
   *          otherwise throws <tt>NotFoundException</tt>.
   */
  public Role getRole(String name) throws NotFoundException, NotPossibleException  {
    Query q = new Query();
    // admin role
    q.add(new Expression("name", Op.EQ, name,
        Expression.Type.Object));
    
    Collection roles = dodm.getDom().getObjects(Role.class, q);
    if (roles == null || roles.isEmpty()) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {Role.class, name});
    }
    
    return (Role)roles.iterator().next();
  }
  
  /**
   * @effects 
   *  return {@link Role} in <tt>roles</tt> whose login is <tt>roleName</tt>, or return null if not found
   *
   * @version 3.3
   */
  public Role getRole(Collection<Role> roles, String roleName) {
    if (roles  == null || roleName == null)
      return null;
    
    for (Role role : roles) {
      if (role.getName().equals(roleName)) {
        return role;
      }
    }
  
    // not found
    return null;
  }
  
  /**
   * @effects 
   *  if {@link DomainUser} whose login is <tt>login</tt> exists in the object pool then return it, 
   *  else throws NotFoundException 
   * @version 3.3
   */
  public DomainUser getUser(String login) throws NotFoundException, NotPossibleException {
    DomainUser user = dodm.getDom().getObject(DomainUser.class, DomainUser.Attribute_login, Op.EQ, login);
    
    if (user == null) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          new Object[] {DomainUser.class, login});
    }

    return user;
  }

  /**
   * @effects 
   *  return {@link DomainUser} in <tt>users</tt> whose login is <tt>userLogin</tt>, or return null if not found
   * @version 3.3
   */
  public DomainUser getUser(Collection<DomainUser> users, String userLogin) {
    if (users  == null || userLogin == null)
      return null;
    
    for (DomainUser user: users) {
      if (user.getLogin().equals(userLogin)) {
        return user;
      }
    }
  
    // not found
    return null;
  }
  
  public Resource getLogicalResource(String name) {
    Query q = new Query();
    // admin role
    q.add(new Expression("name", Op.EQ, name,
        Expression.Type.Object));
    
    Collection resources = dodm.getDom().getObjects(LogicalResource.class, q);
    if (resources == null || resources.isEmpty()) {
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          "Không tìm thấy đối tượng: {0}", "Resource " + name);
    }
    
    return (LogicalResource) resources.iterator().next();
  }
  
  void loadConfiguration() throws DataSourceException {
    // load the main classes
    for (Class cfgCls : SecurityClasses) {
      if (!dodm.getDsm().isTransient(cfgCls))
        // v2.7: should we use this instead? 
        //schema.getDom().loadObjectHierarchyWithAssociations(cfgCls);
        dodm.getDom().retrieveObjectsWithAssociations(cfgCls);
    }

    // for each class that has collection-type attributes, load the referenced
    // objects
    // for each of the classes object
    for (Class cfgCls : SecurityClasses) {
      if (!dodm.getDsm().isTransient(cfgCls)) {
        dodm.getDom().retrieveAssociatedObjects(cfgCls);
      }
    }
  }
  
  /**
   * @requires 
   *  message's format is as specifed by {@link MessageFormat}
   *  
   * @effects 
   *    print log message for <tt>code</tt> from <tt>message</tt> and (optional) <tt>data</tt>
   *    
   */
  private void log(MessageCode code, String message, Object...data) {
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
