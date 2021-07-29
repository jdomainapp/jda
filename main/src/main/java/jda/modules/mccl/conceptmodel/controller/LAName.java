package jda.modules.mccl.conceptmodel.controller;

import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  Represents names of standard actions that are performed by an application.
 *   
 * @author dmle
 */
public enum LAName {
  /** view action: to view an application module (i.e. to access it via tool menu item)
   * @version 3.1
   */
  View,
  //// class-related actions
  RegisterClass,  // v2.7.3
  // application-related actions
  Login, Logout, Exit, 
  // data-related actions
  Open,
  OpenOnNew, // v3.3
  CopyObject, // v3.0
  Export,
  Print,  // v2.7.2
  Chart,
  New,
  Add,  // v2.7.2
  Refresh,
  Reload, // v3.0
  Create,
  OnCreateObject, // v3.3
  Cancel,
  Update,
  OnUpdateObject, // v3.3
  Delete,
  OnDeleteObject, // v3.3
  Reset,
  First,
  Previous,
  Last,
  Next,
  Search,ClearSearch, CloseSearch, ViewCompact,
  ObjectScroll, //2.8
  //RunController, // 3.0
  //
  LogicalAny,
  None,
  //
  Null,
  /**v3.2: added*/
  OnSetCurrentObject,
  /**Help action
   * @version 3.2c
   */
  HelpButton,
  ;
  
  /**
   * List of read-only action names
   * @version 3.1
   */
  public static final LAName[] READ_ONLY_ACTION_NAMES = {
    Open, Export, Print, First, Next, Previous, Last, Refresh, Reload, ViewCompact, 
    ObjectScroll, CopyObject, HelpButton, View      
  };
  
  /**
   * List of wriable action names
   * @version 3.2c
   */
  public static final LAName[] WRITABLE_ACTION_NAMES = {
    New, Create, Add, Update, Delete, LogicalAny      
  };

    /**
   * List of action names specific for data fields
   * @version 3.4
   */
  public static final LAName[] DATA_FIELD_ACTION_NAMES = {
    Update
  };
  
  private Boolean readOnly; // v3.1
  
  private Boolean writable; // v3.2c

  @DAttr(name ="name",id = true,type=jda.modules.dcsl.syntax.DAttr.Type.String,length = 10)
  public String getName() {
    return name();
  }

  /**
   * @effects 
   *  if <tt>actionName != null</tt> AND {@link #name}<tt>.equals(actionName)</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public boolean isEqualByName(String actionName) {
    return actionName != null && name().equals(actionName);
  }

  /**
   * @effects 
   *  if exists {@link LAName} <tt>ln</tt> s.t. <tt>equals(ln.name, name)</tt> 
   *    return <tt>ln.</tt>{@link #isReadOnly()}
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public static boolean isReadOnly(String name) {
    LAName ln = valueOf(name);
    if (ln != null) {
      return ln.isReadOnly();
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  if this represents a read-only action
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public boolean isReadOnly() {
    if (readOnly == null) {
      boolean ro = false;
      for (LAName rn : READ_ONLY_ACTION_NAMES) {
        if (rn.equals(this)) {
          // read only
          ro = true;
          break;
        }
      }
      
      readOnly = ro;
    }
    
    return readOnly;
  }
  
  /**
   * @effects 
   *  if <tt>this</tt> represents a writable action
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2c
   */ 
  public boolean isWritable() {
    if (writable == null) {
      boolean w = false;
      for (LAName rn : WRITABLE_ACTION_NAMES) {
        if (rn.equals(this)) {
          // read only
          w = true;
          break;
        }
      }
      
      writable = w;
    }
    
    return writable;
  }

  /**
   * @effects 
   *  if <tt>this</tt> represents {@link LAName#LogicalAny} action
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2c
   */ 
  public boolean isAny() {
    return this.equals(LogicalAny);
  } 
  
    
  /**
   * @effects
   *  if actName is a data field action name
   *    return true
   *  else 
   *    return false
   * 
   * @version 3.4
   */
  public static boolean isDataFieldAction(String actionName) {    
    for (LAName a : DATA_FIELD_ACTION_NAMES) {
      if (a.name().equals(actionName)) {
        return true;
      }
    }  
    
    // not a data field action
    return false;
  }
} // end LAName