package jda.modules.setup.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SecurityDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.sccl.syntax.security.DomainSecurityDesc;
import jda.modules.sccl.syntax.security.PermSetDesc;
import jda.modules.sccl.syntax.security.RoleDesc;
import jda.modules.sccl.syntax.security.RolePermSetDesc;
import jda.modules.sccl.syntax.security.UserDesc;
import jda.modules.sccl.syntax.security.UserRolesDesc;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.LogicalPermission;
import jda.modules.security.def.LogicalResource;
import jda.modules.security.def.PermType;
import jda.modules.security.def.Role;
import jda.modules.security.def.RolePermission;
import jda.modules.security.def.UserRole;
import jda.modules.setup.security.PermDefs;
import jda.mosa.view.assets.GUIToolkit;
import jda.util.SwTk;


/**
 * @overview 
 *  A sub-class of {@link SetUp} that supports the generative meta-attributes that are used to 
 *  configure an application.
 *  
 * @author dmle
 * 
 * @version 3.3
 * 
 */
public class SetUpGen extends SetUp {

  private static final boolean debugSecurity = Toolkit.getDebug(SetUpSecurity.class);

  /**
   * @effects <pre>
   *  let c = {@link #getSystemClass()}
   *  if c is not null
   *    return {@link #parseInitApplicationConfiguration(Class)}</tt>(c)</tt>
   *  else
   *    return super.createInitApplicationConfiguration
   *  </pre>
   */
  @Override
  public Configuration createInitApplicationConfiguration() throws NotPossibleException {
    Class sysCls = getSystemClass();
    
    Configuration config; 
    if (sysCls != null) {
      config = parseInitApplicationConfiguration(sysCls);
    } else {
      config = super.createInitApplicationConfiguration();
    }
    
    if (config == null)
      throw new NotPossibleException(NotPossibleException.Code.NO_INIT_CONFIGURATION, new Object[] {"system-class=" + sysCls});
    else
      return config;
   
  }

  /**
   * @effects <pre>
   *  let c = {@link #getSystemClass()}
   *  if c is not null
   *    set config = {@link #parseApplicationConfiguration(Class)}</tt>(c)</tt>
   *  else
   *    call super.createApplicationConfiguration
   *  </pre>
   */
  @Override
  public void createApplicationConfiguration() throws NotPossibleException,
      NotFoundException {
    Class sysCls = getSystemClass();
    if (sysCls != null) {
      Configuration config = parseApplicationConfiguration(sysCls);
      setConfig(config);
      validate();
    } else {
      super.createApplicationConfiguration();
    }
  }
  
  
  @Override
  protected void createSecurityConfigurationFor(SetUpSecurity sus, Class c,
      boolean serialisedConfig) throws DataSourceException {
    if (c.equals(DomainUser.class)) {
      createUsers(sus, serialisedConfig);
    } else if (c.equals(UserRole.class)) {
      createUserRoles(sus, serialisedConfig);
    } else if (c.equals(Role.class)) {
      createRoles(sus, serialisedConfig);
    } else if (c.equals(RolePermission.class)) {
      createRolePermisions(sus, serialisedConfig);
    }
  }

  /**
   * @requires 
   *  the relevant roles and application resources (modules, etc.) have been created 
   *  
   * @effects 
   *  if exists {@link RolePermSetDesc} in domain-security-descriptor
   *    create them
   *  else
   *    do nothing 
   *  
   * @version 3.3
   */
  private void createRolePermisions(SetUpSecurity sus, boolean serialisedConfig) throws NotPossibleException, NotFoundException, DataSourceException {
    DomainSecurityDesc secDesc = getDomainSecurityDesc();
    
    if (secDesc != null) {
      // TODO: 1: delete existing role-permissions
      
      // 2: create role-permissions
      
      log(MessageCode.UNDEFINED, "Creating role-permissions...");
      RolePermSetDesc[] rolePermDescs = secDesc.rolePermDescs();
  
      // create role-permissions (if any)
      if (rolePermDescs.length > 0) {
        Collection<Role> roles = sus.retrieveObjects(Role.class);
        Collection<DomainUser> users = sus.retrieveObjects(DomainUser.class);
        createRolePermissions(sus, roles, users, rolePermDescs, serialisedConfig);
      }
    }
  }

  /**
   * @effects 
   *  Create {@link Role}s as specified in the security configuration of {@link #getDomainSecurityDesc()}
   *
   * @version 3.3
   */
  private void createRoles(SetUpSecurity suSec, boolean serialisedConfig) throws DataSourceException {
    DomainSecurityDesc secDesc = getDomainSecurityDesc();
    
    if (secDesc != null) {
      log(MessageCode.UNDEFINED, "Creating roles...");
      
      /** create user accounts*/
      RoleDesc[] roleDescs = secDesc.roleDescs();
      
      if (roleDescs.length > 0) {
        createRoles(suSec, roleDescs, serialisedConfig);
      }
    }
  }

  /**
   * @effects 
   *  Create {@link UserRole}s as specified in the security configuration of {@link #getDomainSecurityDesc()}
   *  
   * @version 3.3
   */
  private void createUserRoles(SetUpSecurity suSec, boolean serialisedConfig) throws DataSourceException {
    DomainSecurityDesc secDesc = getDomainSecurityDesc();
    
    if (secDesc != null) {
      log(MessageCode.UNDEFINED, "Creating user-role assignments...");
      
      /** create user accounts*/
      UserRolesDesc[] userRoleDescs = secDesc.userRoleDescs();
      
      if (userRoleDescs.length > 0) {
        DomainUser user;
        String[] roleNames;
        Role role;
        for (UserRolesDesc userRoleDesc : userRoleDescs) {
          roleNames = userRoleDesc.roleNames();
          user = suSec.retrieveDomainUser(userRoleDesc.userLogin());
          for (String roleName : roleNames) {
            role = suSec.retrieveRole(roleName);
            suSec.createUserRole(user, role, serialisedConfig);
          }
        }        
      }
    }    
  }

  /**
   * @effects 
   *  Create {@link DomainUser}s as specified in the security configuration of {@link #getDomainSecurityDesc()}
   *
   * @version 3.3
   */
  private void createUsers(SetUpSecurity suSec, boolean serialisedConfig) throws DataSourceException {
    DomainSecurityDesc secDesc = getDomainSecurityDesc();
    
    if (secDesc != null) {
      log(MessageCode.UNDEFINED, "Creating users...");
      
      /** create user accounts*/
      UserDesc[] userDescs = secDesc.userDescs();
      
      if (userDescs.length > 0) {
        createUsers(suSec, userDescs, serialisedConfig);
      }
    }
  }

  /**
   * 
   * @effects <pre>
   *  let c = {@link #getSystemClass()}
   *  let sec = {@link #getDomainSecurityDesc(SystemDesc)}(c)
   *  if sec is not null
   *    create domain configuration defined in sec
   *  else
   *    call super.createDomainSecurityConfiguration
   *  </pre>
   */
  /* (non-Javadoc)
   * @see domainapp.setup.SetUp#createDomainSecurityConfiguration(domainapp.setup.SetUpSecurity, boolean)
   */
  @Override
  protected void createDomainSecurityConfiguration(SetUpSecurity suSec,
      boolean serialisedConfig) throws DataSourceException {
    DomainSecurityDesc secDesc = getDomainSecurityDesc();
    
    log(MessageCode.UNDEFINED, "Creating domain-specific security configuration...");

    if (secDesc != null) {
      /** create user accounts and roles */
      UserDesc[] userDescs = secDesc.userDescs();
      RoleDesc[] roleDescs = secDesc.roleDescs();
      UserRolesDesc[] userRoleDescs = secDesc.userRoleDescs();
      RolePermSetDesc[] rolePermDescs = secDesc.rolePermDescs();

      Collection<DomainUser> users = null;
      Collection<Role> roles = null;
      // create users (if any)
      if (userDescs.length > 0) {
        users = createUsers(suSec, userDescs, serialisedConfig);
      }

      // create roles (if any)
      if (roleDescs.length > 0) {
        roles = createRoles(suSec, roleDescs, serialisedConfig);
      }

      // create user-roles (if any)
      if (userRoleDescs.length > 0) {
        createUserRoles(suSec, roles, users, userRoleDescs, serialisedConfig);
      }

      // create role-permissions (if any)
      if (rolePermDescs.length > 0) {
        createRolePermissions(suSec, roles, users, rolePermDescs, serialisedConfig);
      }
    } else {
      // use default
      super.createDomainSecurityConfiguration(suSec, serialisedConfig);
    }
  }

  /**
   * @requires 
   *  <tt>roleDescs != null /\ roleDescs.length > 0</tt>
   *  
   * @effects 
   *  create {@link Role}s specified in <tt>roleDescs</tt> and return them as {@link Collection}
   *  
   *  <p>throws DataSourceException if failed
   * @version 3.3
   */
  private Collection<Role> createRoles(SetUpSecurity suSec, RoleDesc[] roleDescs,
      boolean serialisedConfig) throws DataSourceException {
    if (roleDescs == null || roleDescs.length == 0) {
      return null;
    }
    
    log(MessageCode.UNDEFINED, "Creating roles ");
    
    Collection<Role> roles = new ArrayList<>();
    Role role;
    for (RoleDesc roleDesc : roleDescs) {
      role = suSec.createRole(roleDesc.name(), roleDesc.descr(), serialisedConfig);
      roles.add(role);
    }
    
    return roles;
  }

  /**
   * @requires 
   *  <tt>userDescs != null /\ userDescs.length > 0 </tt>
   *  
   * @effects 
   *  create {@link DomainUser} specified in <tt>userDescs</tt>  and return them as {@link Collection}
   *  
   *  <p>throws DataSourceException if failed
   * @version 3.3
   */
  private Collection<DomainUser> createUsers(SetUpSecurity suSec, UserDesc[] userDescs,
      boolean serialisedConfig) throws DataSourceException {
    if (userDescs == null || userDescs.length == 0) {
      return null;
    }
    
    log(MessageCode.UNDEFINED, "Creating users ");
    
    Collection<DomainUser> users = new ArrayList<>();
    DomainUser user;
    for (UserDesc userDesc : userDescs) {
      user = suSec.createUser(userDesc.name(), userDesc.login(), userDesc.password(), serialisedConfig);
      users.add(user);
    }    
    
    return users;
  }

  /**
   * @requires 
   *  <tt>userRoleDescs != null /\ userRoleDescs.length > 0 /\ roles contains all concerned {@link Role}s /\ users contain all concerned {@link DomainUser}s</tt>
   * @effects 
   *  create {@link UserRole} for <tt>users</tt> and <tt>roles</tt> as specified in <tt>userRoleDescs</tt> 
   *  
   *  <p>throws DataSourceException if failed
   * @version 3.3
   */
  private void createUserRoles(SetUpSecurity suSec,
      Collection<Role> roles, Collection<DomainUser> users,
      UserRolesDesc[] userRoleDescs, boolean serialisedConfig) throws DataSourceException {
    if (userRoleDescs == null || userRoleDescs.length == 0) {
      return;
    }
    
    log(MessageCode.UNDEFINED, "Creating user-role assignments ");
    
    DomainUser user;
    String[] roleNames;
    Role role;
    for (UserRolesDesc userRoleDesc : userRoleDescs) {
      roleNames = userRoleDesc.roleNames();
      user = suSec.getUser(users, userRoleDesc.userLogin());
      for (String roleName : roleNames) {
        role = suSec.getRole(roles, roleName);
        suSec.createUserRole(user, role, serialisedConfig);
      }
    }
  }

  /**
   * @requires 
   * <tt>rolePermDescs != null /\ rolePermDescs.length > 0 /\ roles contains all concerned {@link Role}s /\ users contain all concerned {@link DomainUser}s</tt>
   * @effects 
   *  create {@link RolePermission}s for <tt>roles</tt> (and <tt>users</tt> if needed) as defined in <tt>rolePermDescs</tt>
   *  
   *  <p>throws DataSourceException if failed
   * @version 3.3
   */
  private void createRolePermissions(SetUpSecurity suSec,
      Collection<Role> roles, 
      Collection<DomainUser> users,
      RolePermSetDesc[] rolePermDescs, boolean serialisedConfig) throws DataSourceException {
    /**
     * default permissions for all the roles
     */
    suSec.createDefaultRolePermissions(serialisedConfig, roles.toArray(new Role[roles.size()]));
    

    /** 
     * domain-specific permissions
     **/
    LogicalResource lr;
    
    // initialise empty collection for domain class resources here and create them inside the methods below
    Map<Object,LogicalResource> dataResources = new HashMap();

    // creat ANY permissions (can access to all domain objects) to share among all roles
    // initialise ONLY empty collection for any permissions here and create them inside the methods below
    //
    java.util.Map<LogicalResource, LogicalPermission> anyPerms = new HashMap();
    
    // ensure that classes are registered
    SetUpConfigBasic sufg = super.createSetUpConfigurationBasicInstance();
    if (!sufg.isRegisteredConfigurationSchema())
      sufg.registerConfigurationSchema(this, false);
    
    // START creating role permissions
    log(MessageCode.UNDEFINED, "Creating domain role permissions");

    // convert each RolePermDesc to PermDef 
    // TODO: improved so that we donot need to convert (i.e. use RolePermDesc directly)

    // FIRST, scan through the configured RolePermDescs to expand them to include the dependent modules
    // so that we can also set permissions on them. If the same module is depended on by more than one 
    // other modules then the highest-level permission type among them is used for the dependent module.
    Map<String[],Collection> expRolePermDescs = new HashMap<>();

    // a map that records the the highest-level permission type for a depended-on resource 
    Map<Class,PermType> modulePermMap = new HashMap<>();
    
    // a map that records the PermDef for each role-name, that is extracted from the PermSetDescs across
    // all those in the configuration
    Map<String,PermDefs> rolePermsMap = new HashMap<>();
    
    PermSetDesc[] permSetDescs; // the configured PermSetDesc
    for (RolePermSetDesc rpd : rolePermDescs) {
      // create/update the role-perms map and module-perm map 
      updateRolePerms(rpd, modulePermMap, rolePermsMap);
    }

    // update role-perms map with the permission types of the depended-on resources recorded in module-perm map
    Collection<PermDefs> permDefsCol = rolePermsMap.values();
    for (PermDefs permDefs : permDefsCol) {
      // add/update (mc, pt) to PermDefs of each role name
      for (Entry<Class,PermType> mpe : modulePermMap.entrySet()) {
        permDefs.setModulePermType(mpe.getKey(), mpe.getValue());
      }
    }
    
    // Now, create the actual role permissions 
    String roleName;
    Role role;
    PermDefs permDefs;
    for (Entry<String, PermDefs> rpe : rolePermsMap.entrySet()) {
      roleName = rpe.getKey();
      permDefs = rpe.getValue();
      
      // create same permissions for each role
      role = suSec.getRole(roles, roleName);

      if (debugSecurity) log(MessageCode.UNDEFINED, String.format("Creating permission(s) for role: %s%n", role.getName()));
      
      // create data resources and any permissions as needed and add them to the collections
      suSec.createRolePermissionsEnhanced(this, role, permDefs, anyPerms, dataResources, serialisedConfig);    
    }
  }

  /**
   * @requires 
   *  all module classes are well-defined with a suitable {@link ModuleDescriptor} 
   *  
   * @modifies modulePermMap, rolePermsMap
   * @effects 
   *  create/update {@link PermDefs} of each role name in <tt>modulePermMap</tt> from the configuration 
   *  in <tt>rpd</tt>; and update <tt>rolePermsMap</tt> (if necessary) to record the highest permission 
   *  of any child module that is referenced in the modules in <tt>rpd</tt>.
   *  
   *  <pre>
   *  let pds = rpd.perms
   *  let roleNames = rpd.roleNames
   *  for each PermSetDesc pd in pds
   *    for each module class m in pd.resourceClasses
   *      let children = m.childModules
   *      for each module class mc in children
   *        let pt = higher(pd.permType, modulePermMap.get(mc))
   *        update modulePermMap if pt is higher
   *        
   *        add (mc,pt) to PermDefs in rolePermsMap of each role name in roleNames 
   *  </pre>
   * @version
   */
  private void updateRolePerms(RolePermSetDesc rpd, 
      Map<Class, PermType> modulePermMap, Map<String,PermDefs> rolePermsMap) {
    String[] roleNames = rpd.roleNames();
    PermSetDesc[] pds = rpd.perms();
    
    PermType resPt;
    Class[] resourceClasses;
    String attribName;
    for (PermSetDesc pd : pds) {
      resPt = pd.permType();
      attribName = pd.attribName();
      if (attribName.equals(CommonConstants.NullString)) attribName = null;
      
      resourceClasses = pd.resourceClasses();
      for (Class resCls : resourceClasses) {
        // add (m,mpt) to PermDefs of each role name
        for (String roleName : roleNames) {
          addToRolePermDef(rolePermsMap, roleName, resCls, attribName, resPt, modulePermMap);
        }
      }
    }
  }

  /**
   * @modifies permDefs, modulePermMap
   * @effects 
   *  update <tt>modulePermMap</tt> with higher-level permissions of child modules of <tt>moduleCls</tt> (if any), 
   *  and recursively with permissions of child modules of these, and so on.
   *  
   *  <p>Higher-level permission is determined based on merging or comparing the current permission type of a module (if any) and <tt>modulePt</tt>
   *  
   * @version 3.3
   */
  private void updateDependedOnModulePermMap(PermDefs permDefs, final Class moduleCls, final PermType modulePt, final Map<Class, PermType> modulePermMap) {
    PermType cpt, mergedPt;
    
    ModuleDescriptor mdescr = (ModuleDescriptor) moduleCls.getAnnotation(ModuleDescriptor.class);
    Class[] childrenClasses = mdescr.childModules();
    if (childrenClasses.length > 0) {
      // has children
      for (Class mc : childrenClasses) {
        cpt = modulePermMap.get(mc);
        
        if (cpt != null) {
          mergedPt = modulePt.mergedTo(cpt);
        } else {
          mergedPt = modulePt;
        }
        
        if (cpt == null || !cpt.equals(mergedPt)) {
          // update modulePermMap
          modulePermMap.put(mc, mergedPt);
        }

        if (!permDefs.containsModulePermFor(mc)) {
          if (debugSecurity) log(MessageCode.UNDEFINED, String.format("      permission for child: %n       resource: %s, %n       permission: %s %n", mc, mergedPt));

          // add module permission for mc to permDefs
          permDefs.insertNonAttribPerms(mergedPt, mc);
        }
        
        // recursive: if mc also has children, then update them 
        if (DSMBasic.isModuleDescrClass(mc)) { 
          updateDependedOnModulePermMap(permDefs, mc, modulePt, modulePermMap);
        }
      }
    }
  }

  /**
   * @effects
   *  create (if not exists) in <tt>rolePermsMap</tt> entry <tt>(roleName,p)</tt> where <tt>p is {@link PermDefs}</tt> of <tt>roleName</tt> <br>
   *  add <tt>(resCls,attribName, pt)</tt> to <tt>p</tt> <br>
   *  also if <tt>resCls</tt> is a module class and this has depended-on (children) modules then add them to p and 
   *  update <tt>modulePermMap</tt> with the highest-level permissions fo each child module    
   *   
   * @version 3.3
   */
  private void addToRolePermDef(Map<String, PermDefs> rolePermsMap, String roleName, Class resCls, String attribName, PermType pt, final Map<Class, PermType> modulePermMap) {

    if (debugSecurity) log(MessageCode.UNDEFINED, String.format("   adding role-permission: %n    role: %s, %n    resource: %s, %n    permission: %s %n", roleName, (attribName != null) ? resCls + "." + attribName : resCls, pt));

    PermDefs p = rolePermsMap.get(roleName);
    if (p == null) {
      // not exist: create new
      if (attribName != null)
        p = new PermDefs(pt, resCls, attribName);
      else
        p = new PermDefs(pt, resCls);
      
      rolePermsMap.put(roleName, p);
    } else {
      if (attribName != null)
        p.insertAttribPerm(pt, resCls, attribName);
      else
        p.insertNonAttribPerms(pt, resCls);
    }
    
    // if m has children then also add them to p
    if (DSMBasic.isModuleDescrClass(resCls)) {
      // m is a module class
      updateDependedOnModulePermMap(p, resCls, pt, modulePermMap);
    }
  }

  /**
   * @effects <pre>
   *  let moduleDescs = {@link #getModuleConfigs()}
   *  if moduleDescs is not null
   *    let modelClasses as new {@link ArrayList}
   *    {@link #getModelClasses(Class[], List)}</tt>(moduleDescs, modelClasses)</tt>
   *    return modelClasses.toArray
   *  else
   *    return super.getModelClasses
   *  </pre>
   */
  @Override
  public Class[] getModelClasses() {
    Class[] moduleDescClses = getModuleConfigs();
    
    if (moduleDescClses != null) {
      List<Class> modelClasses = new ArrayList<>();
      getModelClasses(moduleDescClses, modelClasses);
      
      return modelClasses.toArray(new Class[modelClasses.size()]);
    } else {
      return super.getModelClasses();
    }
  }

  /**
   * @effects 
   *  return the model classes of only the domain-specific modules 
   * @version 3.3
   */
  public Collection<Class> getDomainModelClasses() {
    Class[] domainModuleDescClses = getDomainModuleConfigs();
    
    if (domainModuleDescClses != null) {
      List<Class> modelClasses = new ArrayList<>();
      getModelClasses(domainModuleDescClses, modelClasses);
      
      return modelClasses; //modelClasses.toArray(new Class[modelClasses.size()]);
    } else {
      return null;
    }
  }

  /**
   * @effects <pre>
   *  let moduleDescs = {@link #getModuleConfigs()}
   *  if moduleDescs is not null
   *    let moduleDescList as new ArrayList<Class>
   *    moduleDescList.addAll(moduleDescs)
   *    return new ArrayList containing moduleDescList as the only element
   *  else
   *    return super.getModuleDescriptors
   *  </pre>
   */
  @Override
  public List<List<Class>> getModuleDescriptors() {
    Class[] moduleDescClses = getModuleConfigs();
    
    if (moduleDescClses != null) {
      List<Class> modelDescList = new ArrayList<>();
      Collections.addAll(modelDescList, moduleDescClses);
      
      List<List<Class>> result = new ArrayList<>();
      result.add(modelDescList);
      return result;
    } else {
      return super.getModuleDescriptors();
    }
  }
  
  /**
   * @effects <pre>
   *  let moduleDescs = {@link #getSystemModuleConfigs()}
   *  if moduleDescs is not null
   *    let modelClasses as new {@link ArrayList}
   *    {@link #getModelClasses(Class[], List)}</tt>(moduleDescs, modelClasses)</tt>
   *    return modelClasses.toArray
   *  else
   *    return super.getSystemModelClasses
   *  </pre>
   */
  @Override
  public Class[] getSystemModelClasses() {
    Class[] moduleDescClses = getSystemModuleConfigs();
    
    if (moduleDescClses != null) {
      List<Class> modelClasses = new ArrayList<>();
      getModelClasses(moduleDescClses, modelClasses);
      
      return modelClasses.toArray(new Class[modelClasses.size()]);
    } else {
      return super.getSystemModelClasses();
    }
  }

  /**
   * @effects <pre>
   *  let moduleDescs = {@link #getSystemModuleConfigs()}
   *  if moduleDescs is not null
   *    let moduleDescList as new ArrayList<Class>
   *    moduleDescList.addAll(moduleDescs)
   *    return new ArrayList containing moduleDescList as the only element
   *  else
   *    return super.getSystemModuleDescriptors
   *  </pre>
   */
  @Override
  public List<List<Class>> getSystemModuleDescriptors() {
    Class[] moduleDescClses = getSystemModuleConfigs();
    
    if (moduleDescClses != null) {
      List<Class> modelDescList = new ArrayList<>();
      Collections.addAll(modelDescList, moduleDescClses);
      
      List<List<Class>> result = new ArrayList<>();
      result.add(modelDescList);
      return result;
    } else {
      return super.getSystemModuleDescriptors();
    }
  }

  /**
   * @effects <pre>
   *  let c = {@link #getSystemClass()}
   *  if c is not null
   *    return c@SystemDesc.modules
   *  else
   *    return null  
   * </pre>
   */
  private Class[] getModuleConfigs() {
    Class c = getSystemClass();
    return SwTk.parseModuleConfigs(c);
  }
  

  /**
   * @effects <pre>
   *  let c = {@link #getSystemClass()}
   *  if c is not null
   *    return a sub-set of c@SystemDesc.modules whose type is not system
   *  else
   *    return null  
   * </pre>
   */
  private Class[] getDomainModuleConfigs() {
    Class c = getSystemClass();
    return SwTk.parseModuleConfigs(c, ModuleType.DomainData, ModuleType.DomainReport);
  }
  
  /**
   * @effects <pre>
   *  let c = {@link #getSystemClass()}
   *  if c is not null
   *    return c@SystemDesc.systemModules
   *  else
   *    return null  
   * </pre>
   */
  private Class[] getSystemModuleConfigs() {
    Class c = getSystemClass();
    return SwTk.parseSystemModuleConfigs(c);
  }

  /**
   * @effects
   *  return a {@link Class} that is used as the system class of the application, 
   *  or <tt>null</tt> if it is not specified or not found
   *  
   * @version 
   * - 3.3: support specification of system class in system property
   *  <br><b>IMPORTANT</b>: sub-classes must first invoke super.getSystemClass before providing 
   *  their own implementation
   */
  public Class getSystemClass() {
    String clsName = null;
    
    String propName = PropertyName.setup_systemClass.getSysPropName();
    try {
      clsName = getSystemProperty(propName);
    } catch (NotFoundException e) {
      // log:
      log(MessageCode.UNDEFINED, "System property not found: {0}", propName);
    }
    
    Class sysCls = null;

    if (clsName != null) {
      try {
        sysCls = Class.forName(clsName);
      } catch (ClassNotFoundException e) {
        // log: throw new IllegalArgumentException("Invalid system class: " + clsName, e);
        log(MessageCode.UNDEFINED, "Invalid system class: {0}", clsName);
      }
      
      return sysCls;
    } else {
      // ignore: throw new IllegalArgumentException("System class is required but not specified");
    }
    
    return null;
  }
  
  /**
   * @requires 
   *  sysCls != null
   * @effects 
   *  if exists {@link SystemDesc} attached to <tt>sysCls</tt>
   *    return it
   *  else
   *    return <tt>null</tt>
   */
  public static SystemDesc getSystemDesc(Class sysCls) {
    if (sysCls == null)
      return null;
    
    SystemDesc sysDesc = (SystemDesc) sysCls.getAnnotation(SystemDesc.class);
    return sysDesc;
  }
  
  
  /**
   * @effects 
   *  if exists {@link SystemDesc} in {@link #getSystemClass()}
   *    return it
   *  else
   *    return null
   * @version 3.3
   */
  private SystemDesc getSystemDesc() {
    Class sysCls = getSystemClass();
    
    if (sysCls != null) {
      return getSystemDesc(sysCls);
    } else {
      return null;
    }
  }
  
  /**
   * @requires
   *  sysDesc != null
   * @effects 
   *  Return the {@link DomainSecurityDesc} object that is defined in <tt>sysDesc</tt>, or 
   *  return <tt>null</tt> if no such object is defined. 
   *  
   * @version 3.3
   */
  private DomainSecurityDesc getDomainSecurityDesc(SystemDesc sysDesc) {
    if (sysDesc == null)
      return null;
    
    Class defCls = sysDesc.securityDesc().domainSecurityDesc();
    
    if (defCls == CommonConstants.NullType)
      return null;
    
    DomainSecurityDesc domSecDesc = (DomainSecurityDesc) defCls.getAnnotation(DomainSecurityDesc.class);
    
    return domSecDesc;
  }

  /**
   * @effects 
   *  if exists {@link DomainSecurityDesc} in {@link #getSystemDesc()}
   *    return it
   *  else
   *    return null
   * @version 3.3
   */
  private DomainSecurityDesc getDomainSecurityDesc() {
    SystemDesc sysDesc = getSystemDesc();
    
    if (sysDesc != null) {
      return getDomainSecurityDesc(sysDesc);
    } else {
      return null;
    }
  }

  /**
   * @requires 
   *  <tt>sysCls != null</tt>
   *  
   * @effects
   *   parse the meta-attributes of <tt>sysCls</tt> to create and return a {@link Configuration} object
   *   throws NotFoundException if no meta-attributes are found attached to <tt>sysCls</tt>
   */
  protected Configuration parseApplicationConfiguration(Class sysCls) throws NotFoundException {
    SystemDesc sysDesc = getSystemDesc(sysCls);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    OrgDesc orgDesc = sysDesc.orgDesc();
    SecurityDesc secDesc = sysDesc.securityDesc();
    
    // create initial config
    Configuration config = parseInitApplicationConfiguration(sysCls);

    //  config.setLanguage(Lang);
    //v3.0: config.setLabelConstantClass(labelConstantClass);
  
    // TODO: support these
    //config.setListSelectionTimeOut(25);
    config.setMainGUISizeRatio(0.75);
    //config.setChildGUISizeRatio(0.75);
    
    // security configuration
    if (secDesc.isEnabled()) {
      //    config.setUseSecurity(false);
      config.setUseSecurity(true);
      
      /* comment these out will cause the display of Login dialog*/
      DomainSecurityDesc securityDesc = getDomainSecurityDesc(sysDesc);
      
      if (securityDesc == null) {
        throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {DomainSecurityDesc.class, sysDesc});
      }
      
      String user = securityDesc.appUser();
      String password = securityDesc.appPassword();
      if (!user.equals(CommonConstants.NullString)) {
        config.setUserName(user);
        if (!password.equals(CommonConstants.NullString)) {
          config.setPassword(password);
        }
      }
    } else {
      config.setUseSecurity(false);
    }
    
    // init GUI tool kit
    GUIToolkit.initInstance(config);
    
    // TODO
    //config.setDefaultModule("ModuleDomainApplicationModule");
    
    // organisation
    /*
     * If organisation uses a logo picture (preferred format: gif) then 
     * GUIToolkit.initInstance(conig) must be invoked first (see below) 
     */
    ImageIcon appIcon = GUIToolkit.getImageIconOptional(orgDesc.logo(), null);
    
    config.setOrganisation(
        //name: "Faculty of IT",
        orgDesc.name(),
        // logo: GUIToolkit.getImageIcon("hanu.gif", null),
        appIcon,
        // address: "Km9 Đường Nguyễn Trãi, Quận Thanh Xuân",
        orgDesc.address(),
        // url: "http://fit.hanu.edu.vn"
        orgDesc.url()
        );
    
    // v2.7.4: create splash screen info
    createSplashInfo(config, 
        // splash-logo: "coursemanapplogo.jpg"
        sysDesc.splashScreenLogo()
        //"coursemanapplogo.png"
        );
  
    return config;
  }

  /**
   * @requires 
   *  <tt>sysCls != null</tt>
   *  
   * @effects
   *   parse the {@link DSDesc} meta-attribute of <tt>sysCls</tt> to create and return a {@link Configuration} object
   *   that contains the data source specific configuration and number of other basic configuration (e.g. language, etc.)
   *   
   * @todo
   * - support other data source configuration modes: client/server, embedded, etc.
   */
  protected Configuration parseInitApplicationConfiguration(Class sysCls) throws NotFoundException {
    SystemDesc sysDesc = getSystemDesc(sysCls);
    
    if (sysDesc == null) {
      throw new NotFoundException(NotFoundException.Code.ANNOTATION_NOT_FOUND, new Object[] {SystemDesc.class, sysCls});
    }
    
    String appName = sysDesc.appName();
    DSDesc dsDesc = sysDesc.dsDesc();
    String dsType = dsDesc.type();
    String dsUrl = dsDesc.dsUrl();
    String user = dsDesc.user();
    String pwd = dsDesc.password();
    
    //TODO: support other data source configuration modes: client/server, embedded, etc.
    ConnectionType dsMode = dsDesc.connType();
    //Configuration config = ApplicationToolKit.createClientApplicationConfiguration(AppName, "postgresql", clientUrl, dataSourceUserName, dataSourcePassword);
    
    Configuration config; 
    if (dsMode.equals(ConnectionType.Embedded)) {
      config = SwTk.createDefaultInitApplicationConfiguration(appName, dsType, dsUrl, user, pwd);
    } else {
      // TODO: support other modes here
      config = SwTk.createClientApplicationConfiguration(appName, dsType, dsUrl, user, pwd);
    }
    
    // customise dodm types
    DODMConfig dodmCfg = config.getDodmConfig();
    
    // dodm parts
    Class dsmType = dsDesc.dsmType();
    Class osmType = dsDesc.osmType();
    Class domType = dsDesc.domType();
    dodmCfg.setDsmType(dsmType);
    dodmCfg.setOsmType(osmType);
    dodmCfg.setDomType(domType);
    
    // set language
    Language lang = sysDesc.language();
    config.setLanguage(lang);
    
    // setup config type
    Class suCfgCls = sysDesc.setUpDesc().setUpConfigType();
    config.setSetUpConfigurationType(suCfgCls);    
    
    return config;
  }
  
  @Override // SetUpBasic
  protected void postSetUpDB() throws DataSourceException {
    /* v5.1: updated to call createBasicDomainData
    super.postSetUpDB();
    
    // if exist data file loaders: load them 
    Class c = getSystemClass();
    Class[] dfLoaders = ApplicationToolKit.parseDataFileLoaders(c);
    
    if (dfLoaders != null)
      super.importObjectsFromCSVFile(dfLoaders);
    */
    createBasicDomainData();
  }

  @Override // SetUp
  protected void createBasicDomainData() throws DataSourceException {
    if (dodm == null) {
      initDODM();
    }

    super.postSetUpDB();
    
    // if exist data file loaders: load them 
    Class c = getSystemClass();
    Class[] dfLoaders = SwTk.parseDataFileLoaders(c);
    
    if (dfLoaders != null)
      super.importObjectsFromCSVFile(dfLoaders);

  }
  
  /**
   * 
   * @effects 
   *  initialise an instance of this and  
   *  call {@link #run(domainapp.basics.setup.SetUpBasic, String[])}
   *  
   * @version 3.3
   */
  public static void main(String[] args) {
    SetUpGen su = new SetUpGen();
    try {
      run(su, args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
