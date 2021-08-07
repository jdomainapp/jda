package jda.modules.setup.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jda.modules.common.CommonConstants;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.sccl.syntax.security.PermSetDesc;
import jda.modules.security.def.DomainClassPerm;
import jda.modules.security.def.ModuleClassPerm;
import jda.modules.security.def.PermType;

/**
 * @overview
 *  A helper class used to ease the definition of an array of permissions  
 *  
 * @author dmle
 * 
 * @version 
 * - 3.1: improved to support permission definition on module descriptor class
 */
public class PermDefs {

  private List<DomainClassPerm> classPerms;
  private List<ModuleClassPerm> modulePerms;
  private List<DomainClassPerm> attribPerms;

  public PermDefs(Object[][] permEntries) throws IllegalArgumentException {
    Object el0; Class cl0;
    for (Object[] e : permEntries) {
      if (e.length < 2) {
        throw new IllegalArgumentException("class entry must have length >= 2");
      }
      
      if (e.length == 2) {
        // class permission
        el0 = e[0];
        if (el0 instanceof Collection) {
          // an array of classes
          Collection<Class> clsArr = (Collection<Class>) el0;
          for (Class ec : clsArr) {
            // v3.1: support module descriptor
            //classPerms.add(new DomainClassPerm(ec, (PermDefs.PermType)e[1]));
            if (DSMBasic.isModuleDescrClass(ec)) {
              if (modulePerms == null) modulePerms = new ArrayList();
              modulePerms.add(new ModuleClassPerm(ec,  (PermType)e[1]));
            } else {
              if (classPerms == null) classPerms = new ArrayList();
              classPerms.add(new DomainClassPerm(ec, (PermType)e[1]));
            }
          }
        } else if (el0 instanceof Class) {
          // a single class
          // v3.1: support module descriptor
          //classPerms.add(new DomainClassPerm((Class)e[0], (PermDefs.PermType)e[1]));
          cl0 = (Class) el0;
          if (DSMBasic.isModuleDescrClass(cl0)) {
            if (modulePerms == null) modulePerms = new ArrayList();
            modulePerms.add(new ModuleClassPerm(cl0, (PermType)e[1]));
          } else {
            if (classPerms == null) classPerms = new ArrayList();
            classPerms.add(new DomainClassPerm(cl0, (PermType)e[1]));
          }
        } else {
          throw new IllegalArgumentException("invalid element 0: " +el0+ " (must be a Class or Class[])");
        }
      } else {
        // attribute permissions
        if (attribPerms == null) attribPerms = new ArrayList();
        
        attribPerms.add(new DomainClassPerm((Class)e[0], (String) e[1], (PermType)e[2]));
      }
    }
    
    //if (attribPerms.isEmpty()) attribPerms = null;
  }

  /**
   * @effects  
   *  initialise this to data specified in <tt>permDescs</tt>
   *  
   * @version 3.3
   */
  public PermDefs(PermSetDesc[] permDescs) throws IllegalArgumentException {
    Class[] resClasses;
    String attribName;
    PermType pt;
    for (PermSetDesc pd : permDescs) {
      resClasses = pd.resourceClasses();
      attribName = pd.attribName();
      pt = pd.permType();
      
      if (resClasses.length > 1) {
        // more than one resources
        insertNonAttribPerms(pt, resClasses);
      } else if (resClasses.length == 1) {
        // could be a single perm or an attribute perm
        if (!attribName.equals(CommonConstants.NullString)) {
          // attribute perm
          insertAttribPerm(pt, resClasses[0], attribName);
        } else {
          // non-attribute perm
          insertNonAttribPerms(pt, resClasses[0]);
        }
      } else {
        // invalid
        throw new IllegalArgumentException("Resource classes are required but not specified");
      }
    }
  }

  /**
   * @effects 
   *  initialises this to contain <tt>(resCls,pt)</tt> as the first resource permission
   */
  public PermDefs(PermType pt, Class resCls) {
    insertNonAttribPerms(pt, resCls);
  }

  /**
   * @effects 
   *  initialises this to contain <tt>(resCls,attribName, pt)</tt> as the first resource permission
   */
  public PermDefs(PermType pt, Class resCls, String attribName) {
    insertAttribPerm(pt, resCls, attribName);
  }

  /**
   *  @effects 
   *  add to this an attribute permission for attribute <tt>attribName</tt> in <tt>resClass</tt>
   *  using {@link PermType} <tt>pt</tt>. 
   *  
   *
   * @version 3.3
   */
  public void insertAttribPerm(PermType pt, Class resClass, String attribName) {
    if (attribPerms == null) attribPerms = new ArrayList<>();
    
    attribPerms.add(new DomainClassPerm(resClass, attribName, pt));    
  }

  /**
   * @effects 
   *  add to this either a module permission or a class permission for each resource in <tt>resClasses</tt>
   *  and using {@link PermType} <tt>pt</tt>. 
   *  
   * @version 3.3
   */
  public void insertNonAttribPerms(PermType pt, Class...resClasses) {
    for (Class ec : resClasses) {
      if (DSMBasic.isModuleDescrClass(ec)) {
        if (modulePerms == null) modulePerms = new ArrayList<>();
        modulePerms.add(new ModuleClassPerm(ec,  pt));
      } else {
        if (classPerms == null) classPerms = new ArrayList<>();
        classPerms.add(new DomainClassPerm(ec, pt));
      }
    }
  }

  public Collection<DomainClassPerm> getClassPerms() {
    return classPerms;
  }

//  public void setClassPerms(List<DomainClassPerm> classPerms) {
//    this.classPerms = classPerms;
//  }

  public Collection<ModuleClassPerm> getModulePerms() {
    return modulePerms;
  }

  public Collection<DomainClassPerm> getAttribPerms() {
    return attribPerms;
  }

  /**
   * @effects <pre>
   *  if exists {@link ModuleClassPerm} mp in {@link #modulePerms} s.t mp.moduleDescrClass = moduleCls
   *    if mp.type != permType
   *      set mp.type = permType
   *  </pre>
   * @version 3.3
   */
  public void setModulePermType(Class moduleCls, PermType permType) {
    if (modulePerms != null) {
      for (ModuleClassPerm mp : modulePerms) {
        if (mp.getModuleDescrClass().equals(moduleCls)) {
          // exists 
          if (!mp.getType().equals(permType)) {
            mp.setType(permType);
          }
          break;
        }
      }
    }
  }

  /**
   * @effects 
   *  if exists in {@link #modulePerms} a module permission for <tt>moduleCls</tt>
   *    return true
   *  else
   *    return false
   * @version 3.3
   */
  public boolean containsModulePermFor(final Class moduleCls) {
    if (modulePerms != null) {
      for (ModuleClassPerm mp : modulePerms) {
        if (mp.getModuleDescrClass().equals(moduleCls)) {
          // exists 
          return true;
        }
      }
    } 
    
    // not exist
    return false;
  }

//  public void setAttribPerms(List<DomainClassPerm> attribPerms) {
//    this.attribPerms = attribPerms;
//  }
} // end PermDefs