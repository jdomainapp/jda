package jda.modules.security.def;

//import domainapp.basics.model.security.Action;

/**
 * @overview
 *  Permission type used to set up security permissions on application modules.
 *  
 *  <p>Each permission type will be mapped to a set of application's {@link Action}s that the users are
 *  allowed to perform with that permission. 
 *  
 * @author dmle
 * @version 3.1 (moved to a separate class)
 */
public enum PermType {
  /** all permissions */
  ANY(4),
  /** read module, e.g. loading domain objects, but 
   * cannot change the domain objects and 
   * cannot view the module on the interface */
  READ_ONLY(0),
  /**
   * can view the module on the interface (also implies READ_ONLY), 
   * but cannot change the domain objects
   */
  VIEW(1),
  /** read module, e.g. loading domain objects, AND  
   * can change the domain objects BUT  
   * cannot view the module on the interface */
  READ_WRITE (2),
  
  /***
   * {@link #READ_ONLY} & {@link VIEW}
   */
  READ_ONLY_AND_VIEW(READ_ONLY, VIEW),

  /***
   * {@link #READ_WRITE} & {@link VIEW}
   */
  READ_WRITE_AND_VIEW(READ_WRITE, VIEW),
  ;

  /**
   * to record the access level for use in comparison (by {@link #isHigherThan(PermType)}) 
   * as well as in merging (by {@link #mergedTo(PermType)}):
   * <br>
   * - a higher number means a higher-level <br>
   * - level is bit-wise operable
   * 
   * @version 3.3
   */
  private int level;
  
  private PermType(int level) {
    this.level = level;
  }
  
  /**
   * 
   * @effects 
   *  initialise this with level set to bit-wise combination of <tt>components</tt>
   */
  private PermType(PermType...components) {
    this.level= components[0].level;
    for (int i = 1; i < components.length; i++) {
      this.level ^= components[i].level;
    }
  }
  
  /**
   * @requires pt != null
   * @effects 
   *  return new {@link PermType} that is created by merging this with <tt>pt</tt> 
   * @version 3.3
   */
  public PermType mergedTo(PermType pt) {
    // if either is ANY then ANY is used 
    if (this.equals(ANY)) return this;
    if (pt.equals(ANY)) return pt;
    
    // if both are the same then return either
    if (this.equals(pt)) {
      return this;
    }
    
    // if this differs from pt then use their levels to determine...
    int mergedLevel = this.level ^ pt.level;
    
    PermType newPt = PermType.valueOf(mergedLevel);
    if (newPt != null) {
      // one of the defined combinations
      return newPt;
    } else {
      // the two cannot be merged: use the higher-level of the two
      if (this.isHigherThan(pt)) {
        return this;
      } else {
        return pt;
      }
    }
  }

  /**
   * @effects 
   *  if exists {@link PermType} whose level is <tt>level</tt>
   *    return it
   *  else
   *    return null
   * @version 3.3
   */
  private static PermType valueOf(int level) {
    for (PermType pt : values()) {
      if (pt.level == level) {
        return pt;
      }
    }
    
    // not found
    return null;
  }

  /**
   * @effects 
   *  if this.level &gt; pt.level
   *    return true
   *  else
   *    return false  
   * @version 3.3
   */
  private boolean isHigherThan(PermType pt) {
    if (this.level > pt.level) {
      return true;
    } else {
      return false;
    }
  }
}