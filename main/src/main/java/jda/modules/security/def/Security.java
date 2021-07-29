package jda.modules.security.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.SecurityException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;

/**
 * @overview  a model class for use by <tt>SecurityController</tt>.
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA,serialisable=false)
public class Security {
  
  private DODMBasic schema;
  
  /** derived from the permissions held by {@link #domainUser} 
   * @version 3.2c:
   *   - changed to hold Permissions that are extracted from RolePermissions 
   */ 
  //private List<RolePermission> userPermissions;
  private List<Permission> userPermissions;
  
  /** authenticated user */
  @DAttr(name="domainUser",type=Type.Domain)
  private DomainUser domainUser;

  private static boolean debug = Toolkit.getDebug(Security.class);

  public Security(DODMBasic schema) {
    //
    this.schema = schema;
  }
  
  public void setUser(DomainUser user) {
    this.domainUser = user;
    getPermissions();
  }

  /**
   * @requires 
   *  {@link #hasObjectGroupPermission(Class)}(<tt>ApplicationModule.class</tt>) /\ 
   *  <tt>action != null  -> action </tt> is a valid {@link Action} name
   *  
   * @effects 
   *  if exists an object group permission for <tt>action</tt> (if <tt>action = null</tt>) or for any action (if <tt>action = null</tt>) 
   *  and whose group membership contains <tt>module</tt>, whose {@link Oid} is <tt>mOid</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt> 
   *   
   * @version 
   * - 3.1
   */
  public boolean getResourcePermissionOfModule(String action, ApplicationModule module, Oid mOid) {
    //return getObjectPermission(ApplicationModule.class, mOid);
    Class c = ApplicationModule.class;
    String resourceName = schema.getDsm().getResourceNameFor(c);
    
    //v3.2c: Permission p;
    Resource r;
    LogicalResource lr;
    String rname;
    Action a;
    ObjectGroup grp;
    int idHash = mOid.hashCode();

    final String any = LAName.LogicalAny.name();
    final String none = LAName.None.name();
    final Resource rAny = Resource.LogicalAny;
    
    boolean allowed = false;
    
    // debug
    if (debug)
      System.out.printf("Checking module permission: %s%n", module);
    
    /*v3.2c: 
     SEARCH: for (RolePermission rp : userPermissions) {
      p = rp.getPermission();
      */
    SEARCH: for (Permission p : userPermissions) {
      r = p.getResource();
      a = p.getAction();
      rname = r.getName();

      // any-permission on any-resource
      if (a.equals(any) && (r.equals(rAny))) {
        // any-any: allowed (stop immediately)
        allowed = true; break SEARCH;
      } else if (r instanceof LogicalResource) {
        // logical resource
        lr = (LogicalResource) r;
        
        if (rname.equals(resourceName) && 
            a != null && 
            ((action == null && !a.equals(none)) ||         // action is null
             (action != null && a.getName().equals(action)) // action is not null
            )
        ) {
          // found application module resource permission
          grp = lr.getObjectGroup();
          
          if (grp != null && grp.containsMember(idHash)) {
            // hashCode(oid) in objectGroup: allowed (stop immediately)
            // debug
            if (debug)
              System.out.printf("   -> IN group: %s%n", lr, grp);
            
            allowed = true; break SEARCH;
          } else {
            // no object group OR hashCode(oid) NOT in objectGroup -> disallow based on this resource
            // but carry on to search other resources (there may be multiple permissions set on the same
            // resource name)
          }
        }
      }
    }
    
    // if none of the above rules match: then default is to disallow
    
    // debug
    if (debug)
      System.out.printf("   allowed: %b%n", allowed);
    
    return allowed;
  }
  
  /**
   * This method is used to determine which logical resources the current domain
   * user is allowed to access.
   * 
   * @effects <pre>
   *            if exists < action.Any,resourceName > or < action.Any,resource.Any > in 
   *            security.userPermissions
   *                returns true
   *            else if action is not null and exists < action,resourceName >  
   *            in security.userPermissions
   *              returns true
   *            else if action is null (i.e. action is the command string of a menu item (see {@link ControllerBasic#getResourceState(String, String)}))
   *            AND exists < a,resourceName > 
   *              if a is not LogicalAction.None
   *                returns true
   *              else
   *                return false
   *            else
   *              if strictChecking = true
   *                throws NotFoundException
   *              else 
   *                return true/false based on a default permission  
   *           </pre>       
   * @requires <tt>security.userPermissions != null</tt>
   * @deprecated (as of version 3.1): use {@link #getResourcePermission(String, String, boolean)} or one 
   *  of the other methods
   */
  public boolean getResourcePermission(final String action, final String resourceName) {
    //Permission p;
    Resource r;
    String rname;
    Action a;
    final String any = LAName.LogicalAny.name();
    final String none = LAName.None.name();
    final Resource rAny = Resource.LogicalAny;

    /* v3.2c: for (RolePermission rp : userPermissions) {
      p = rp.getPermission();
    */
    for (Permission p : userPermissions) {
      r = p.getResource();
      a = p.getAction();
      rname = r.getName();

      // permission on any resource
      if (a.equals(any) && (r.equals(rAny) || rname.equals(resourceName))) {
        // any-any \/ any-resourceName permission
        return true;
      }

      if (action != null) {
        // action and resource must match
        if (a.equals(action) && rname.equals(resourceName)) {
          // exists <action,resourcename> permission
          return true;
        }
      } else {  // action = null
        if (rname.equals(resourceName)) {
          if (!a.equals(none))
            return true;
          else  // disallow
            return false;
        }     
      }
    }
    
    // none of the above rules match
    // default: disallow
    return false;
  }
  
  /**
   * This method is used to <b>strictly</tt> identify which logical resources the current domain
   * user is allowed to access.
   * 
   * @effects <pre>
   *            if exists < action.Any,resourceName > or < action.Any,resource.Any > in 
   *            security.userPermissions
   *                returns true
   *            else if action is not null and exists < action,resourceName >  
   *            in security.userPermissions
   *              returns true
   *            else if action is null (i.e. action is the command string of a menu item (see {@link ControllerBasic#getResourceState(String, String)}))
   *            AND exists < a,resourceName > 
   *              if a is not LogicalAction.None
   *                returns true
   *              else
   *                return false
   *            else
   *              throws NotFoundException
   *           </pre>    
   * @requires <tt>security.userPermissions != null</tt>
   */
  public boolean getResourcePermissionStrict(String action, String resourceName) throws NotFoundException {
    boolean strictChecking = true;
    return getResourcePermission(action, resourceName, strictChecking);
  }

  /**
   * This method is used to identify which logical resources the current domain user is allowed to access.
   * 
   * @effects <pre>
   *            if exists < action.Any,resourceName > or < action.Any,resource.Any > in 
   *            security.userPermissions
   *                returns true
   *            else if action is not null and exists < action,resourceName >  
   *            in security.userPermissions
   *              returns true
   *            else if action is null (i.e. action is the command string of a menu item (see {@link ControllerBasic#getResourceState(String, String)}))
   *            AND exists < a,resourceName > 
   *              if a is not LogicalAction.None
   *                returns true
   *              else
   *                return false
   *            else
   *              return true/false based on a default permission  
   *           </pre>    
   * @requires <tt>security.userPermissions != null</tt>
   */
  public boolean getResourcePermissionWithDefault(String action, String resourceName) {
    boolean strictChecking = false;
    return getResourcePermission(action, resourceName, strictChecking);
  }

  /**
   * This method is a shared method used by other methods to determine which logical resources the current domain
   * user is allowed to access.
   * 
   * @effects <pre>
   *            if exists < action.Any,resourceName > or < action.Any,resource.Any > in 
   *            security.userPermissions
   *                returns true
   *            else if action is not null and exists < action,resourceName >  
   *            in security.userPermissions
   *              returns true
   *            else if action is null (i.e. action is the command string of a menu item (see {@link ControllerBasic#getResourceState(String, String)}))
   *            AND exists < a,resourceName > 
   *              if a is not LogicalAction.None
   *                returns true
   *              else
   *                return false
   *            else
   *              if strictChecking = true
   *                throws NotFoundException
   *              else 
   *                return true/false based on a default permission  
   *           </pre>       
   * @requires <tt>security.userPermissions != null</tt>
   * @version 3.1
   */
  private boolean getResourcePermission(final String action, final String resourceName, boolean strictChecking) 
  throws NotFoundException {
    //Permission p;
    Resource r;
    String rname;
    Action a;
    final String any = LAName.LogicalAny.name();
    final String none = LAName.None.name();
    final Resource rAny = Resource.LogicalAny;

    /* v3.2c: for (RolePermission rp : userPermissions) {
      p = rp.getPermission();
     */
    for (Permission p : userPermissions) {
      r = p.getResource();
      a = p.getAction();
      rname = r.getName();

      // permission on any resource
      if (a.equals(any) && (r.equals(rAny) || rname.equals(resourceName))) {
        // any-any \/ any-resourceName permission
        return true;
      }

      if (action != null) {
        // action is specified
        /*v3.1: improved to compare actions 
        if (a.equals(action) && rname.equals(resourceName)) {
          // exists <action,resourcename> permission
          return true;
        }
        */
        if (rname.equals(resourceName)) {
          // exists <?,resourcename>: compare actions
          if (a.isCompatibleWith(action)){
            // actions are compabile: allowed
            return true;
          } else {
            // actions not compatible: disallowed
            return false;
          }
        }
      } else {  // action = null
        if (rname.equals(resourceName)) {
          if (!a.equals(none))
            return true;
          else  // disallow
            return false;
        }     
      }
    }
    
    // none of the above rules match
    if (strictChecking) {
      // strict checking
      throw new NotFoundException(NotFoundException.Code.RESOURCE_PERMISSION_NOT_FOUND, 
          new Object[] {action, resourceName});
    } else  {
      // non-strict checking: use default = disallow
      return false;
    }
  }
  
  /**
   * @requires 
   *  c is a registered domain class 
   * @effects 
   *  if user has objectgroup-typed permission on objects of <tt>c</tt>
   *  (i.e. there exists a user's permission over a logical resource of <tt>c</tt> whose object group is not null)
   *    return true
   *  else
   *    return false
   *  
   * @version 
   *  v2.7.2
   */
  public boolean hasObjectGroupPermission(Class c) {
    String resourceName = schema.getDsm().getResourceNameFor(c);
    
    //Permission p;
    Resource r;
    LogicalResource lr;
    String rname;
    
    /* v3.2c: 
      for (RolePermission rp : userPermissions) {
      p = rp.getPermission();
     */
    for (Permission p : userPermissions) {
      r = p.getResource();
      rname = r.getName();
      
      if (r instanceof LogicalResource && rname.equals(resourceName)) {
        lr = (LogicalResource) r;
        
        if (lr.getObjectGroup() != null) {
          // found a permission
          return true;
        }
      }
    }    
    
    // if we get here then there are no such permissions
    return false;
  }
  
  /**
   * @requires 
   *  domainUser != null /\ oid is a valid object id of c
   *  
   * @effects 
   *  if <tt>domainUser</tt> has permission to access domain object of 
   *  the domain class <tt>c</tt> whose <tt>Oid</tt> is <tt>oid</tt> 
   *    return true
   *  else
   *    return false
   *  
   *  The general procedure for checking the permission proceeds as follows:
   *  <p><pre>
   *     if exists (action.Any,resource.Any) in userPermissions
   *       return true
   *     else if exists (action,resource=LogicalResource(c.name)) in userPermissions s.t. action != LogicalAction.None
   *       if resource.objectGroup != null /\ hashCode(o.oid) in resource.objectGroup
   *         return true
   *       else if resource.objectGroup is null
   *         return true
   *       else
   *         return false
   *     else  
   *       return false     
   *  </pre>  
   */
  public boolean getObjectPermission(Class c, Oid oid) {
    String resourceName = schema.getDsm().getResourceNameFor(c);
    
    // Permission p;
    Resource r;
    LogicalResource lr;
    String rname;
    Action a;
    ObjectGroup grp;
    int idHash;
    
    final String any = LAName.LogicalAny.name();
    final String none = LAName.None.name();
    final Resource rAny = Resource.LogicalAny;
    
    /*v3.2c: for (RolePermission rp : userPermissions) {
      p = rp.getPermission();
     */
    for (Permission p : userPermissions) {
      r = p.getResource();
      a = p.getAction();
      rname = r.getName();

      // any-permission on any-resource
      if (a.equals(any) && (r.equals(rAny))) {
        // any-any
        return true;
      } else if (r instanceof LogicalResource) {
        // logical resource
        lr = (LogicalResource) r;
        idHash = oid.hashCode();
        
        if (a != null && !a.equals(none) && rname.equals(resourceName)) {
          // exists (a,rname)
          grp = lr.getObjectGroup();
          if (grp != null && grp.containsMember(idHash)) {
            // hashCode(oid) in objectGroup
            return true;
          } else if (grp == null) {
            // no object group
            return true;
          } else {
            // hashCode(oid) NOT in objectGroup
            return false;
          }
        }
      }
    }
    
    // none of the above rules match
    // default: disallow
    return false;
  }

  /**
   * @requires 
   *  idVals != null /\ {@link #hasObjectGroupPermission(Class)} (<tt>cls</tt>) 
   * @effects 
   *  return a sub-set of <tt>objectMap</tt> containing only those objects that are in the object group 
   *  permission of <tt>cls</tt>; or return <tt>null</tt> if no objects are in the group
   * @version 3.1
   */
  public Map<Oid, Object> filterObjectsByPermission(Class cls, Map<Oid, Object> idVals) {
    Map<Oid,Object> filteredIdVals = null;
    try {
      filteredIdVals = idVals.getClass().newInstance();
    } catch (Exception ex) {
      // should not happen
      return idVals;
    }
    
    Oid id;
    for (Entry<Oid,Object> e : idVals.entrySet()) {
      id = e.getKey();
      if (getObjectPermission(cls, id)) {
        // allowed
        filteredIdVals.put(id, e.getValue());
      }
    }
    
    return (filteredIdVals.isEmpty()) ? null : filteredIdVals;  
  }
  
  /**
   * 
   * @effects if this.domainUser is valid reads all permissions associated to
   *          the roles of domainUser else throws SecurityException
   */
  private void getPermissions() throws SecurityException {
    //validate();
    if (!isLoggedIn()) {
      throw new SecurityException(SecurityException.Code.NOT_LOGGED_IN);
    }

    userPermissions = new ArrayList();

    // user roles
    Collection<UserRole> uRoles = domainUser.getRoles();
    // get all the permissions applied to roles
    Role role;
    Collection<RolePermission> rolePerms;
    for (UserRole ur : uRoles) {
      role = ur.getRole();
      rolePerms = role.getPerms();
      // v3.2c: userPermissions.addAll(rolePerms);
      for (RolePermission rp : rolePerms) {
        mergeUserPermission(rp.getPermission());
      }
    } // end for loop (user-roles)
  }

  /**
   * @requires {@link #userPermissions} is initialised
   * @modifies {@link #userPermissions}
   * @effects <pre>
   *  if exists p' in {@link #userPermissions} s.t p'.resource eq p.resource
   *    compare p.action and p' action to use p or p' depending on whose action is less restrictive
   *  else 
   *    add p to {@link #userPermissions}
   *  </pre>
   * @version 3.2c
   */
  private void mergeUserPermission(Permission p) {
    Action a, a1;
    // either add p or replace an existing permission by p
    int toBeReplaced = -1;
    boolean toAdd = true;
    
    int index = 0;
    for (Permission p1 : userPermissions) {
      if (p1.getResource().equalsByName(p.getResource())) {
        // p.resource eq p1.resource
        // compare actions to decide which one is less restrictive and use that
        a = p.getAction();
        a1 = p1.getAction();
        if (a.isLessRestrictiveThan(a1)) {
          // replace p1 by p
          toBeReplaced = index;
          toAdd = false;
        } else if (a.equals(a1)) {
          // identical -> not to add
          toAdd = false;
        }
        
        break;  // no need to check other permissions (only one match possible)
      }
      
      index++;
    }
    
    if (toAdd) {
      // p not in userPermissions
      userPermissions.add(p);
    } else if (toBeReplaced > -1) {
      userPermissions.set(toBeReplaced, p);
    }
  }

  public void clear() {
    domainUser = null;
    userPermissions = null;
  }
  
  public boolean isLoggedIn() {
    return (domainUser != null); 
  }

  /**
   * @version 2.8
   */
  public DomainUser getDomainUser() {
    return domainUser;
  }

}
