package jda.modules.security.def;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;


/**
 * A super-class of all application-related actions.
 * 
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public abstract class Action {
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,mutable=false,length=6)
  private int id; 
  @DAttr(name="name",type=Type.String,mutable=true,length=20)
  private String name;
  
  private static int idCounter = 0;

  // constructor methods
  public Action(Integer id, String name) {
    this.id = nextID(id);
    this.setName(name);
  }
  
  public Action(String name) {
    this(null, name);
  }
  
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) 
        idCounter=num;
      
      return currID;
    }
  }

  public int getId() {
    return id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  /**
   * @effects invokes <code>getName()</code> (for backward compatibility)
   * @return
   */
  public String name() {
    return getName();
  }
  
  public String toString() {
    return this.getClass().getSimpleName()+"("+getName()+")";
  }
  
  public boolean equals(Object o) {
    return (o != null && 
        (o instanceof Action) && ((Action)o).id == this.id);
  }
  
  public boolean equals(String s) {
    if (s == null)
      return false;
    
    return this.name.equals(s);
  }

  /**
   * @effects 
   *  if <tt>this</tt> is compatible with {@link Action}<tt>(checkAction)</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.1
   */
  public abstract boolean isCompatibleWith(String checkAction);

  /**
   * @effects <pre>
   *  if this and other are comparable
   *    if <tt>this</tt> is less restrictive than <tt>other</tt>
   *      return <tt>true</tt>
   *    else
   *      return <tt>false</tt>
   *  else 
   *    throws NotPossibleException
   *    
   * @version 3.2c
   */
  public abstract boolean isLessRestrictiveThan(Action other) throws NotPossibleException;
}
