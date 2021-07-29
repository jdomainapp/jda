package jda.test.security;

import static jda.modules.setup.init.LogicalActionConstants.Any;
import static jda.modules.setup.init.LogicalActionConstants.Create;
import static jda.modules.setup.init.LogicalActionConstants.Export;
import static jda.modules.setup.init.LogicalActionConstants.First;
import static jda.modules.setup.init.LogicalActionConstants.Last;
import static jda.modules.setup.init.LogicalActionConstants.Next;
import static jda.modules.setup.init.LogicalActionConstants.Open;
import static jda.modules.setup.init.LogicalActionConstants.Previous;
import static jda.modules.setup.init.LogicalActionConstants.Print;
import static jda.modules.setup.init.LogicalActionConstants.Refresh;
import static jda.modules.setup.init.LogicalActionConstants.Reload;
import static jda.modules.setup.init.LogicalActionConstants.Update;
import static jda.modules.setup.init.LogicalActionConstants.ViewCompact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.collection.Map;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.SecurityException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.Expression;
import jda.modules.oql.def.Query;
import jda.modules.security.def.Action;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.LogicalAction;
import jda.modules.security.def.LogicalPermission;
import jda.modules.security.def.LogicalResource;
import jda.modules.security.def.ObjectGroup;
import jda.modules.security.def.ObjectGroupMembership;
import jda.modules.security.def.Permission;
import jda.modules.security.def.PhysicalAction;
import jda.modules.security.def.PhysicalPermission;
import jda.modules.security.def.PhysicalResource;
import jda.modules.security.def.Resource;
import jda.modules.security.def.Role;
import jda.modules.security.def.RolePermission;
import jda.modules.security.def.Security;
import jda.modules.security.def.UserRole;
import jda.modules.security.def.Resource.Type;
import jda.modules.setup.init.LogicalActionConstants;
import jda.mosa.model.Oid;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.City;
import jda.test.model.basic.Module;
import jda.test.model.basic.SClass;
import jda.test.model.basic.Student;

public class TestMainSecurity extends CourseManBasicTester {
  protected Class[] dataClasses;

  public TestMainSecurity() throws NotPossibleException {
    super();
  }

  protected void initClasses() {

    schema.registerEnumInterface(Resource.Type.class);

    domainClasses = new Class[] { //
        Action.class, LogicalAction.class, PhysicalAction.class, 
        Resource.class, //
        ObjectGroup.class,
        ObjectGroupMembership.class,
        LogicalResource.class, //
        PhysicalResource.class, //
        Permission.class, //
        LogicalPermission.class, //
        PhysicalPermission.class, //
        DomainUser.class, //
        Role.class, //
        UserRole.class, //
        RolePermission.class, //
    };

    dataClasses = new Class[] { Student.class, Module.class, };
  }

  // /// Testable methods /////
  // /// To be used by individual test case sub-classes ////
  public void initData() throws DataSourceException {
    // only invoked once for all test cases
    method("initData()");

    List<LogicalAction> actions = initLogicalActions();

    //List<LogicalResource> resources = initLogicalResources();

    //initLogicalPermissions(resources, actions);
    
    initUserRoles();
    
    initResourceAndRolePermissions();
  }

  protected void initUserRoles() throws DataSourceException {
    /** create user accounts and roles */
    System.out.println("Tạo tài khoản người sử dụng");
   
    java.util.Map<Class,Collection> data = instance.getData();
    
    // users
    DomainUser dadmin = createUser("Lê Minh Đức","duclm","duclm");
    DomainUser lect = createUser("Trần Quang Anh","anhtq","anhtq");
    DomainUser lect2 = createUser("Trần Văn Kiều","kieutv","kieutv");
    DomainUser coord = createUser("Trần Thị Linh","linhtt","linhtt");
    Collection users = new ArrayList();
    users.add(dadmin);
    users.add(lect);
    users.add(lect2);
    users.add(coord);
    data.put(DomainUser.class, users);
    
    // roles
    Role daRole = createRole("domainAdmin", "Domain administrator role");
    Role tRole1 = createRole("Senior Lecturer", "Senior lecturers");
    Role tRole2 = createRole("Lecturer", "Lecturers");
    Role cRole = createRole("coordinator", "Academic coordinator");
    Collection roles = new ArrayList();
    roles.add(daRole);
    roles.add(tRole1);
    roles.add(tRole2);
    roles.add(cRole);
    data.put(Role.class, roles);
    
    // users -> roles
    Collection urs = new ArrayList();
    data.put(UserRole.class, urs);
    UserRole ur = createUserRole(dadmin, daRole);
    urs.add(ur);
//    ur = createUserRole(dadmin, tRole1);
//    urs.add(ur);
//    ur = createUserRole(dadmin, tRole2);
//    urs.add(ur);
//    ur = createUserRole(dadmin, cRole);
//    urs.add(ur);
    ur = createUserRole(lect, tRole1);
    urs.add(ur);
    ur = createUserRole(lect2, tRole2);
    urs.add(ur);
    ur = createUserRole(coord, cRole);
    urs.add(ur);
  }
  
  protected List<LogicalAction> initLogicalActions() {
    method("initLogicalActions()");

    List<LogicalAction> actions = Toolkit.getConstantObjects(LogicalActionConstants.class, LogicalAction.class);

    data.put(Action.class, actions);

    return actions;
  }

  protected List<LogicalResource> initLogicalResources() throws DataSourceException {
    method("initLogicalResources()");
    List<LogicalResource> res = new ArrayList();

    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    LogicalResource r;
    String name;

    // any resource
    r = new LogicalResource(Resource.Type.Any.name(), "everything",
        Resource.Type.Any);
    res.add(r);

    // the domain classes and their objects
    for (Class ec : dataClasses) {
      name = schema.getResourceNameFor(ec);
      r = new LogicalResource(name, "Domain class: " + name,
          Resource.Type.Class);
      res.add(r);
      r = new LogicalResource(name, "Domain objects of " + name,
          Resource.Type.Object);
      res.add(r);
    }
    
    // v2.7.2: add object group-based resource for Student
    Class c = Student.class;
    // obtain the Student objects 
    Collection<Student> students = getStudentObjects();
    Iterator<Student> sit = students.iterator();
    
    name = schema.getResourceNameFor(c);
    r = new LogicalResource(name, "Group-based resource for: " + name,
        Resource.Type.Object);
    res.add(r);
    
    Student s1 = sit.next();
    Student s2 = sit.next();
    Oid oid1 = dom.genObjectId(c, s1);
    Oid oid2 = dom.genObjectId(c, s2);
    int oid1Hash = oid1.hashCode();
    int oid2Hash = oid2.hashCode();
    
    List<ObjectGroup> objectGroups = new ArrayList();
    List<ObjectGroupMembership> objectGroupMemberships = new ArrayList();
    
    ObjectGroup objectGroup = new ObjectGroup(r);
    objectGroups.add(objectGroup);
    
    ObjectGroupMembership om1 = new ObjectGroupMembership(objectGroup, oid1Hash);
    ObjectGroupMembership om2 = new ObjectGroupMembership(objectGroup, oid2Hash);
    objectGroupMemberships.add(om1);
    objectGroupMemberships.add(om2);
    
    objectGroup.addMembership(om1);
    objectGroup.addMembership(om2);

    data.put(LogicalResource.class, res);

    data.put(ObjectGroup.class, objectGroups);
    
    data.put(ObjectGroupMembership.class, objectGroupMemberships);


    return res;
  }

  protected List<Permission> initLogicalPermissions(List<LogicalResource> res,
      List<LogicalAction> actions) {
    method("initLogicalPermissions()");

    List<Permission> perms = new ArrayList();
    Permission p;

    // create permissions from pairs of resources and actions
    List<LogicalAction> classActions = new ArrayList();
    Collections.addAll(classActions, LogicalActionConstants.Open, 
        LogicalActionConstants.Create, LogicalActionConstants.Update, LogicalActionConstants.Delete);

    for (LogicalResource r : res) {
      for (LogicalAction a : actions) {
        if (r.isType(Type.Class) && !classActions.contains(a)) {
          continue;
        }
        p = new LogicalPermission(a, r);
        perms.add(p);
      }
    }

    data.put(LogicalPermission.class, perms);
    return perms;
  }
  
  protected void initResourceAndRolePermissions() throws DataSourceException {
    method("initResourceAndRolePermissions()");
    
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    java.util.Map<Class,Collection> data = instance.getData();
    
    Collection<Role> roles = data.get(Role.class);
    
    Role daRole = getObject(roles,0);
    Role tRole1 = getObject(roles,1);
    Role tRole2 = getObject(roles,2);
    Role cRole = getObject(roles,3);

    Collection<LogicalPermission> perms = new ArrayList();
    LogicalPermission perm;
    Collection<RolePermission> rperms = new ArrayList();
    RolePermission rperm;
    Collection<LogicalResource> res = new ArrayList();
    List<ObjectGroup> objectGroups = new ArrayList();
    List<ObjectGroupMembership> objectGroupMemberships = new ArrayList();
    
    data.put(ObjectGroup.class, objectGroups);
    data.put(ObjectGroupMembership.class, objectGroupMemberships);
    data.put(LogicalResource.class, res);
    data.put(LogicalPermission.class, perms);
    data.put(RolePermission.class, rperms);

    // any resource
    LogicalResource rAny = LogicalResource.LogicalAny;
    res.add(rAny);

    // the domain classes and their objects
    String name = schema.getResourceNameFor(Student.class);
    LogicalResource rStudent = new LogicalResource(name, "Object permissions: " + name,
        Resource.Type.Object);
    res.add(rStudent);
    
    LogicalResource rStudent1 = new LogicalResource(name, "Group-based 1 for: " + name,
          Resource.Type.Object);
    res.add(rStudent1);
    LogicalResource rStudent2 = new LogicalResource(name, "Group-based 2 for: " + name,
        Resource.Type.Object);
    res.add(rStudent2);
    name = schema.getResourceNameFor(Module.class);
    LogicalResource rModule= new LogicalResource(name, "Domain objects of " + name,
          Resource.Type.Object);
    res.add(rModule);
    
    Class c = Student.class;
    // obtain the Student objects 
    Collection<Student> students = getStudentObjects();
    Iterator<Student> sit = students.iterator();
    
    Student s1 = sit.next();
    Student s2 = sit.next();
    Student s3 = sit.next();
    Student s4 = sit.next();
    Student s5 = sit.next();
    
    createObjectGroupForResource(rStudent1, c, new Student[] {s1, s2});
    createObjectGroupForResource(rStudent2, c, s3, s4, s5);
    
    /*
     *  admins: any permission on any resource 
     */
    perm = new LogicalPermission(Any, rAny);
    perms.add(perm);
    
    rperm = createRolePermission(daRole, perm);
    rperms.add(rperm);
    
    /*
     * teachers type 1: 
     *  student: read object-group:{Student(S2014),Student(S2015)}
     *  module: read
     */
    perm = new LogicalPermission(Open, rStudent1);
    perms.add(perm);
    rperm = createRolePermission(tRole1, perm);
    rperms.add(rperm);
    
    LogicalPermission permMod = new LogicalPermission(Open, rModule);
    perms.add(permMod);
    rperm = createRolePermission(tRole1, permMod);
    rperms.add(rperm);
    
    /*
     * teachers type 2: 
     *  student: read object-group:{Student(S2016),Student(S2017),Student(S2018)}
     *  module: read
     */
    perm = new LogicalPermission(Open, rStudent2);
    perms.add(perm);
    rperm = createRolePermission(tRole2, perm);
    rperms.add(rperm);
    
    rperm = createRolePermission(tRole2, permMod);
    rperms.add(rperm);

    /*
     * coordinators: 
     *  student: read/write
     *  module: read
     */
    perm = new LogicalPermission(Open, rStudent);
    rperm = createRolePermission(cRole, perm);
    perms.add(perm);
    rperms.add(rperm);
    
    perm = new LogicalPermission(Create, rStudent);
    rperm = createRolePermission(cRole, perm);
    perms.add(perm);
    rperms.add(rperm);
    
    rperm = createRolePermission(cRole, permMod);
    rperms.add(rperm);    
  }

  /**
   * @requires 
   *  ObjectGroup, ObjectGroupMembership are registered in this.data
   */
  public <T> void createObjectGroupForResource(LogicalResource res, Class<T> c, T...objects) {
    Collection<Integer> idHashes = new ArrayList();
    Oid oid;
    int hash;
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    for (T o : objects) {
      oid = dom.lookUpObjectId(c, o);//schema.genObjectId(c, o);
      hash = oid.hashCode();
      idHashes.add(hash);
    }
    
    java.util.Map<Class,Collection> data = instance.getData();
    Collection<ObjectGroup> objectGroups = data.get(ObjectGroup.class);
    Collection<ObjectGroupMembership> objectGroupMemberships = data.get(ObjectGroupMembership.class);
    
    ObjectGroup objectGroup1 = new ObjectGroup(res);
    res.setObjectGroup(objectGroup1);
    objectGroups.add(objectGroup1);

    ObjectGroupMembership om1;
    for (int idHash : idHashes) {
      om1 = new ObjectGroupMembership(objectGroup1, idHash);
      objectGroup1.addMembership(om1);
      objectGroupMemberships.add(om1);
    }
  }
  
  public DomainUser login(String login, String pwd) throws SecurityException {
    //final DODM schema = getDomainSchema();
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    Query q = new Query();
    q.add(new Expression("login", Op.EQ, login,
        Expression.Type.Object));
    q.add(new Expression("password", Op.EQ, pwd,
        Expression.Type.Object));

    try {
      Collection<DomainUser> users = dom.retrieveObjectsWithAssociations(DomainUser.class, q);

      if (users == null || users.isEmpty()) {
        throw new SecurityException(SecurityException.Code.USER_NOT_VALID,
            "Tên, mật khẩu không đúng. Vui lòng thử lại.");
      }

      DomainUser domainUser = users.iterator().next();
      
      /* v2.7.2: improved to perform a deep loading of all associated objects (incl. permissions and resources)
       * v2.5.3 
       * make sure that all the referenced objects of domain user are loaded
       * this is necessary after a logout (which unloads all domain user objects)
       * Note: remove this step if a more intelligent loadObjects method was 
       * performed in the previous step
       * 
      schema.loadAssociatedObjects(domainUser);
      
       */
      loadPermissions(domainUser);
            
      return domainUser;
    } catch (DataSourceException e) {
      // displayError(e);
      throw new SecurityException(
          SecurityException.Code.FAILED_TO_AUTHENTICATE, e,
          "Không thể thực hiện xác thực: {0}.authenticate({1})",
          this.toString(), login);
    }    
  }

  /**
   * @effects   
   *  load from data source all the associated permissions of <tt>user</tt>
   */
  private void loadPermissions(DomainUser user) throws NotFoundException, NotPossibleException {
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    // load user roles (if needed)
    Collection<UserRole> uroles = user.getRoles();
    if (uroles == null || uroles.isEmpty()) {
      dom.retrieveAssociatedObjects(user);
      uroles = user.getRoles();
    }
  
    if (uroles != null) {
      // load permissions associated to each role
      Collection<RolePermission> rperms;
      Role role;
      for (UserRole r : uroles) {
        System.out.printf("   %s%n", r);
        role = r.getRole();
        rperms = role.getPerms();
        if (rperms == null || rperms.isEmpty()) {
          dom.retrieveAssociatedObjects(role);
          rperms = role.getPerms();
        }
        
        if (rperms != null) {
          // load the resources associated to each permission
          Resource res;
          LogicalResource lres;
          ObjectGroup grp;
          for (RolePermission rperm : rperms) {
            System.out.printf("   %s%n", rperm.getPermission());
            res = rperm.getPermission().getResource();
            if (res instanceof LogicalResource) {
              // load the object groups (if any) associated to each logical resource
              lres = (LogicalResource) res;
              grp = lres.getObjectGroup();
              if (grp != null && grp.getMembershipsCount() == 0) {
                dom.retrieveAssociatedObjects(grp);
                System.out.printf("   %s has %d members %n", grp, grp.getMembershipsCount());
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * @requires 
   *  o is a domain object of c /\ o != null
   * @effects 
   *  if <tt>user</tt> has permission to access object <tt>o</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean isObjectPermitted(DomainUser user, Class c, Object o) throws NotFoundException {
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    Security sec = new Security(getDODM());
    sec.setUser(user);
    
    
    Oid oid = dom.lookUpObjectId(c, o);
    return sec.getObjectPermission(c, oid);
  }
  
  protected Collection<Student> getStudentObjects() throws DataSourceException {
    // register it first
    schema.registerClass(City.class);
    schema.registerClass(SClass.class);
    schema.registerClass(Student.class);

    Class<Student> c = Student.class;
    
    // use two Student objects 
    Collection<Student> students = loadObjectsFromSource(c);
    return students;
  }
  
  
  ///////////////////// security helper methods 
  /**
   * @requires domainClasses != null
   * @modifies {@link #schema}
   * @effects 
   *  for each domain class c in domainClasses
   *    create a logical resource typed Object for c
   *    Throw DBException if fails
   *    add resource to map
   *  return map 
   *  
   */
  public java.util.Map<Class, LogicalResource> createDomainResources(
      Class[] domainClasses) throws DataSourceException {
    // create logical resources from the domain classes
    System.out.println("Tạo tài nguyên dữ liệu");
    LogicalResource lr;
    java.util.Map<Class,LogicalResource> dataResources = new HashMap();    
    for (Class c : domainClasses) {
      if (c != null) {
        lr = createLogicalResource(c, "Objects: " + c.getSimpleName(), Resource.Type.Object);
        dataResources.put(c,lr);
        System.out.println("..." + lr);
      }
    }
    
    return dataResources;
  }
  
  public LogicalResource createLogicalResource(Class resourceClass, String desc, Type type) throws DataSourceException {
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    return createLogicalResource(
        //v2.7.2: schema.getDomainClassName(resourceClass), 
        schema.getResourceNameFor(resourceClass),
        desc, type);
  }

  public LogicalResource createLogicalResource(String resourceName, String desc, Type type) throws DataSourceException {
    LogicalResource res = new LogicalResource(
        resourceName, desc, type);
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    dom.addObject(res);
    return res;
  }
  
  public LogicalPermission createLogicalPermission(LogicalAction act, LogicalResource resource) 
  throws DataSourceException {
    DSMBasic schema = getDsm();
    DOMBasic dom = getDom();
    
    LogicalPermission perm = new LogicalPermission(act, resource);
    dom.addObject(perm);
    return perm;
  }
  
  public Role createRole(String name, String desc) throws DataSourceException {
    Role role = new Role(name,desc);
    //schema.addObject(role);
    return role;
  }
  
  public RolePermission createRolePermission(Role role, LogicalPermission perm) {
    RolePermission rp = new RolePermission(role, perm);
    //schema.addObject(rp);
    
    return rp;
  }
  
  /**
   * @effects creates all read-only logical permissions on <tt>resource</tt> and 
   *          assign them to <tt>role</tt>; throws DBException if an error occured
   */
  public void createReadOnlyPermissions(Role role, LogicalResource resource) throws DataSourceException {
    
    final LogicalAction[] READ_ONLY = getReadOnlyActionsFor(resource.getType()); 
      //{Open, First, Next, Previous, Last, Refresh };
    LogicalPermission perm;
    for (LogicalAction act : READ_ONLY) {
      perm = createLogicalPermission(act, resource);
      createRolePermission(role, perm);
    }    
  }
  
  /**
   * @effects 
   *  create logical resource r for <tt>attribName</tt> with description <tt>desc</tt>
   *  and create a logical permission <tt>< role, r > </tt>.
   *  Throws DBException if fails.
   */
  public void createReadWritePermission(Role role, String attribName,
      String desc, Type type) throws DataSourceException {
    LogicalResource r = createLogicalResource(attribName, desc, type);
    
    LogicalAction[] actions = getReadWriteActionsFor(type);
    if (actions != null) {
      LogicalPermission perm;
      for (LogicalAction act : actions) {
        perm = createLogicalPermission(act, r);
        createRolePermission(role, perm);
      }  
    }
  }

  private LogicalAction[] getReadOnlyActionsFor(Resource.Type type) {
    LogicalAction[] actions = null;
    
    if (type == Resource.Type.Object) {
      actions = new LogicalAction[] {
          Open, Export, Print, First, Next, Previous, Last, Refresh, Reload, ViewCompact
          };
    }
    
    return actions;
  }

  private LogicalAction[] getReadWriteActionsFor(Resource.Type type) {
    LogicalAction[] actions = null;
    
    if (type == Resource.Type.Attribute) {
      actions = new LogicalAction[] {
          Update
      };
    }
    
    return actions;
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
  public Map<LogicalResource, LogicalPermission> createAdminRolePermissions(
      Role admin,
      java.util.Map<Class, LogicalResource> dataResources) throws DataSourceException {
    System.out.println("Tạo quyển cho " + admin.getName());
    
    Map<LogicalResource,LogicalPermission> anyPerms = new Map();
    RolePermission rperm;
    for (LogicalResource lres : dataResources.values()) {
      LogicalPermission perm = createLogicalPermission(Any, lres);
      anyPerms.put(lres, perm);
      rperm = createRolePermission(admin, perm);

      System.out.println("..."+rperm);
    }
    
    return anyPerms;
  }
  
  public DomainUser createUser(String name, String login, String password) throws DataSourceException {
    DomainUser user = new DomainUser(name, login, password);
    //schema.addObject(user);
    return user;
  }
  
  public UserRole createUserRole(DomainUser user, Role role) throws DataSourceException {
    UserRole ur = new UserRole(user,role);
    //schema.addObject(ur);
    return ur;
  }

  protected void printUserPermissions(DomainUser user) {
    Collection<UserRole> uroles = user.getRoles();
    if (uroles != null) {
      Collection<RolePermission> rperms;
      for (UserRole r : uroles) {
        System.out.printf("%s%n", r.getRole());
        rperms = r.getRole().getPerms();
        if (rperms != null) {
          Resource res;
          ObjectGroup grp;
          for (RolePermission rperm : rperms) {
            System.out.printf("   %s%n", rperm.getPermission());
            res = rperm.getPermission().getResource();
            if (res instanceof LogicalResource) {
              grp = ((LogicalResource)res).getObjectGroup();
              if (grp != null)
                System.out.printf("     resource group members: %d%n", grp.getMembershipsCount());
            }
          }
        }
      }
    }
  }
}
