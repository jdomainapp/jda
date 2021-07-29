package jda.modules.security.authentication.controller;

import java.util.Collection;
import java.util.Map;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.SecurityException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.oql.QueryToolKit;
import jda.modules.oql.def.Query;
import jda.modules.security.authentication.login.controller.LoginController;
import jda.modules.security.authentication.logout.controller.LogoutController;
import jda.modules.security.def.DomainUser;
import jda.modules.security.def.LogicalResource;
import jda.modules.security.def.LoginUser;
import jda.modules.security.def.ObjectGroup;
import jda.modules.security.def.Resource;
import jda.modules.security.def.Role;
import jda.modules.security.def.RolePermission;
import jda.modules.security.def.Security;
import jda.modules.security.def.UserRole;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.CompositeController;
import jda.mosa.controller.assets.util.AppState;
import jda.mosa.controller.assets.util.MethodName;
import jda.mosa.model.Oid;

/**
 * A sub-class of {@see Controller} to handle the security functions, such as
 * logging in and out of the system and authorising user's actions.
 * 
 * @author dmle
 * 
 */
public class SecurityController extends ControllerBasic {
//  private Map<Action, Controller> controllerMap;

  private Security security;

  private ApplicationModule loginModule;
  private ApplicationModule logoutModule;
  
  // v3.1: private static DomainUser guestUser;

  private static final boolean debug = Toolkit.getDebug(SecurityController.class);
  
//  private static final String ModuleLoginName = "ModuleLogin";  // LAName.Login.name()
//  private static final String ModuleLogoutName = "ModuleLogout";  // LAName.Logout.name()
  
  public SecurityController(DODMBasic dodm, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config) {
    super(dodm, module, moduleGui, parent, config);

    security = new Security(dodm);
  }

//  @Override
//  public void actionPerformed(Region src) throws NotPossibleException {
//    if (src == null)
//      return;
//    else {
//      String cmd = src.getName();
//      LogicalAction action = LogicalAction.getAction(cmd);
//
//      // System.out.println("user action: " + action);
//
//      Controller controller = controllerMap.get(action);
//
//      if (controller == null) {
//        throw new NotPossibleException(
//            NotPossibleException.Code.FAIL_TO_PERFORM,
//            "Không thể thực hiện {0}", action);
//      }
//
//      controller.run();
//    }
//  }

  /**
   * @effects sets this to the initial security state (i.e. before login)
   */
  public void init() throws NotFoundException {
    // v3.1
    initCommon();
    
    ControllerBasic main = getMainController();

    security.clear();
    
    /*v2.7.2: moved to Controller 
    // update GUI components
    updateMenuBarPermissions();
    */
    
    // hide logout, show login
    main.getGUI().setVisibleComponent(loginModule.getName(), true);
    main.getGUI().setVisibleComponent(logoutModule.getName(), false);

    // show login screen     
    CompositeController loginCtl = (CompositeController) 
        getMainController().lookUpByControllerType(LoginController.class);
    /* v2.7.2: wait for login to finish 
    loginCtl.run();
    */
    loginCtl.run(true);
  }

  /**
   * @effects initialise this with user login (<tt>login, password</tt>)
   */
  public void init(String login, String password) throws SecurityException, NotFoundException {
    initCommon(); // v3.1
    login(login, password, null);
  }
  
  /**
   * @effects 
   *  initialise common resources used by this
   * @version 3.1
   */
  private void initCommon() throws NotFoundException {
    // look up login and logout module for use
    ControllerBasic main = getMainController();
    ControllerBasic loginCtl = main.lookUpByControllerType(LoginController.class);
    ControllerBasic logoutCtl = main.lookUpByControllerType(LogoutController.class);

    loginModule = loginCtl.getApplicationModule();
    logoutModule = logoutCtl.getApplicationModule();    
  }

  /**
   * This method is invoked by {@link LoginController}.
   * 
   * @effects if user <code>loginUser</code> matches a <code>DomainUser</code>
   *          set application state to logged-in and update the GUI else throws
   *          <code>SecurityException</code>
   * @see {@link MethodName#login}
   */
  public void login(final LoginUser loginUser) throws SecurityException {
    String login = loginUser.getLogin();
    String pwd = loginUser.getPassword();
    Role role = loginUser.getRole();

    login(login, pwd, role);
  }

  /**
   * @effects 
   *  retrieve from the data source {@link DomainUser} with authentication <tt>(login,pwd)</tt> and matching 
   *  <tt>role</tt>. 
   *  
   *  <p>If succeeded
   *    retrieve load all user permissions and update main GUI
   *    else
   *    throws SecurityException 
   */
  private void login(String login, String pwd, Role role) throws SecurityException {
    final DODMBasic dodm = getDodm();
    DSMBasic dsm = dodm.getDsm();
    DOMBasic dom = dodm.getDom();
    
    Query q = new Query();
    /*v3.1: replaced by object expression
    q.add(new Expression(DomainUser.Attribute_login, Expression.Op.EQ, login,
        Expression.Type.Object));
    q.add(new Expression(DomainUser.Attribute_password, Expression.Op.EQ, pwd,
        Expression.Type.Object));
        */
    q.add(QueryToolKit.createObjectExpression(
            dsm, DomainUser.class, DomainUser.Attribute_login, Op.EQ, login));
    q.add(QueryToolKit.createObjectExpression(
            dsm, DomainUser.class, DomainUser.Attribute_password, Op.EQ, pwd));

    try {
      /*v3.1: FIXED bug with using this method
      Collection<DomainUser> users = schema.getDom().retrieveObjectsWithAssociations(DomainUser.class, q);
      
      if (users == null || users.isEmpty()) {
        throw new SecurityException(SecurityException.Code.USER_NOT_VALID,
            "Tên, mật khẩu không đúng. Vui lòng thử lại.");
      }
      
      DomainUser domainUser = users.iterator().next();
      */
      
      //dom.setDebugOn(true);
      
      Map<Oid,DomainUser> users = dom.retrieveObjects(DomainUser.class, q);
      
      //dom.setDebugOn(false);
      
      if (users == null) {
        throw new SecurityException(SecurityException.Code.USER_NOT_VALID);
      }

      DomainUser domainUser = users.entrySet().iterator().next().getValue();

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
      
       /* v2.7.2: improved to perform a deep loading of all associated objects 
      Collection<DomainUser> users = schema.loadObjectsWithAllAssociations(DomainUser.class, q);*/

      /** retrieve all the user's roles and permissions */
      validateUser(domainUser, role);
      security.setUser(domainUser);
      
      // update the GUI
      ControllerBasic main = getMainController();
      String name = domainUser.getName();
      if (name == null)
        name = domainUser.getLogin();
      
      main.fireApplicationStateChanged(this, AppState.LoggedIn, name);
      
      /*v2.7.2: moved to Controller and use isInit 
      updateMenuBarPermissions();
      updateAppDataPermissions();
      */
      
      // hide login, show logout
      main.getGUI().setVisibleComponent(loginModule.getName(), false);
      main.getGUI().setVisibleComponent(logoutModule.getName(), true);
    } catch (DataSourceException e) {
      // displayError(e);
      throw new SecurityException(
          SecurityException.Code.FAILED_TO_AUTHENTICATE, e, new Object[] {this.toString(), login});
    }    
  }
  
  /*v2.7.2: moved to Controller 
   **
   * Invokes this method only if this is a main controller.
   * 
   * @effects <pre>
   *              updates the states of the menu components of
   *              mainCtl.gui based on the current user permissions
   * </pre>
   *
  private void updateMenuBarPermissions() {
    Controller mainCtl = getMainController();
    mainCtl.getGUI().updateMenuBarPermissions();
  }
  
  **
   * @effects 
   *  update the data field components of all the controllers
   *  based on the current user permissions.
   *
  private void updateAppDataPermissions() {
    Controller mainCtl = getMainController();
    Iterator<Controller> childControllers = mainCtl.getFunctionalControllers();
    
    if (childControllers != null) {
      Controller c;
      while (childControllers.hasNext()) {
        c = childControllers.next();
        c.updateDataPermissions();
      }
    }
  }
  */
  
  /**
   * @effects 
   *  perform logout
   *  
   * @see {@link MethodName#logout}
   */
  public void logout() throws Exception {
    if (!security.isLoggedIn()) {
      return;
    }
    
    /**
     * close the running tasks and clear all security resources <br>
     * reset the states of the GUI
     * */
    // close the running tasks
    ControllerBasic main = getMainController();
    
    // v2.7.2
    main.preLogout();
    
    main.close();

    security.clear();
    
    // unload domain user class
    final DODMBasic schema = getDodm();
    schema.getDom().unloadObjects(DomainUser.class);
  
    /*v2.7.2: moved to Controller 
    // update GUI components
    updateMenuBarPermissions();
    */
    
    // hide logout, show login
    main.getGUI().setVisibleComponent(loginModule.getName(), true);
    main.getGUI().setVisibleComponent(logoutModule.getName(), false);

    main.fireApplicationStateChanged(this, AppState.LoggedOut, "Log in to use the system"); 
    
    // restart login screen     
    CompositeController loginCtl = (CompositeController) 
        getMainController().lookUpByControllerType(LoginController.class);

    loginCtl.restart();
  }

  public Security getSecurity() {
    return security;
  }

// v3.1: not used  
//  private DomainUser getGuestUser() {
//    if (guestUser == null) {
//      final DODMBasic schema = getDodm();
//      Query q = new Query(new Expression("login", Expression.Op.EQ, "guest"));
//      try {
//        Collection<DomainUser> users = schema.getDom().retrieveObjectsWithAssociations(DomainUser.class, q);
//        if (users != null && !users.isEmpty()) {
//          guestUser = users.iterator().next();
//          validateUser(guestUser, null);
//        }
//      } catch (Exception e) {
//        // ignore
//      }
//    }
//
//    return guestUser;
//  }

  /**
   * @effects   
   *  load from data source all the associated permissions of <tt>user</tt>
   */
  private void loadPermissions(DomainUser user) throws NotFoundException, NotPossibleException {
    DODMBasic dodm = getDodm();
    DOMBasic dom = dodm.getDom();
    
    // load user roles (if needed)
    Collection<UserRole> uroles = user.getRoles();
    if (uroles == null || uroles.isEmpty()) {
      /*v3.3: specify the role-permission association to load (otherwise this will lead to long chains of object loadings
       * some of which may involve other domain classes)
      dom.retrieveAssociatedObjects(user);
      */
      try {
        dom.retrieveAssociatedObjects(user, DomainUser.class, UserRole.class, DomainUser.Association_WithUserRole);
      } catch (DataSourceException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, new Object[] {user + "'s user-roles"});
      }
      uroles = user.getRoles();
    }
  
    if (uroles != null) {
      // load permissions associated to each role
      Collection<RolePermission> rperms;
      Role role;
      for (UserRole r : uroles) {
        if (debug) 
          System.out.printf("   %s%n", r);
        role = r.getRole();
        rperms = role.getPerms();
        if (rperms == null || rperms.isEmpty()) {
          /*v3.3: specify the role-permission association to load (otherwise this will lead to long chains of object loadings
           * some of which may involve other domain classes)
          dom.retrieveAssociatedObjects(role);
          */
          try {
            dom.retrieveAssociatedObjects(role, Role.class, RolePermission.class, Role.Assoc_RoleAndRolePermission);
          } catch (DataSourceException e) {
            throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_DB, e, new Object[] {role + "'s role-permissions"});
          }
          rperms = role.getPerms();
        }
        
        if (rperms != null) {
          // load the resources associated to each permission
          Resource res;
          LogicalResource lres;
          ObjectGroup grp;
          for (RolePermission rperm : rperms) {
            if (debug) 
              System.out.printf("   %s%n", rperm.getPermission());
            res = rperm.getPermission().getResource();
            if (res instanceof LogicalResource) {
              // load the object groups (if any) associated to each logical resource
              lres = (LogicalResource) res;
              grp = lres.getObjectGroup();
              if (grp != null) {
                if (grp.getMembershipsCount() == 0) {
                  //TODO: should specify association-name to load (as was the case for role-permission above)
                  dom.retrieveAssociatedObjects(grp);
                }

                if (debug) System.out.printf("       %s.%s has %d members %n", lres, grp, grp.getMembershipsCount());
              }
            }
          }
        }
      }
    }
  }
  
  /**
   * @effects loads role and permissions of the domain user <code>user</code>;
   *          throws <code>DBException</code> if an error occured
   * @modifies <code>user</code>
   */
  private void validateUser(final DomainUser user, final Role role)
      throws DataSourceException, SecurityException {
    //final DODMBasic schema = getDodm();

    /** retrieve all the user's roles and permissions */
    Collection<UserRole> userRoles = user.getRoles();
    
    Role r;
    UserRole userRole = null;
    // check user's role
    boolean roleOK = true;
    if (role != null) {
      roleOK = false;
      for (UserRole ur : userRoles) {
        r = ur.getRole();
        if (r.equals(role)) {
          roleOK = true;
          userRole = ur;
          break;
        }
      }
    }

    if (roleOK) {
      if (role != null) {
        // remove all user roles, except for the valid one
        /* v3.3: use method of DomainUser to update other data 
         userRoles.clear();
         user.addUserRole(userRole);
         */
        user.setSingleRole(userRole);
      } else {
        // use all roles
        user.setRoles(userRoles);
      }
    } else {
      throw new SecurityException(SecurityException.Code.ROLE_NOT_VALID);
    }

    // this is not needed since permissions are loaded automatically 
    // get permissions
    // - if role is specified then for the specified role only 
    //    else get for all roles
//    List<RolePermission> perms;
//    Query q;
//    if (role != null) {
//      // specified role
//      q = new Query();
//      q.add(new Expression("role", Expression.Op.EQ, role, Expression.Type.Object));
//      perms = schema.loadObjects(RolePermission.class, q);
//      if (perms != null) {
//        userRole.getRole().setPerms(perms);
//      }
//    } else {
//      // all roles
//      for (UserRole ur : userRoles) {
//        r = ur.getRole();
//        q = new Query();
//        q.add(new Expression("role", Expression.Op.EQ, r, Expression.Type.Object));
//        perms = schema.loadObjects(RolePermission.class, q);
//        if (perms != null) {
//          r.setPerms(perms);
//        }
//      }      
//    }
  }
  
  public boolean isLoggedIn() {
    return security.isLoggedIn();
  }
}
