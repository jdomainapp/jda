package jda.modules.objectsorter.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  Represents a domain constraint as a type.
 *  
 * @author dmle
 */
@DClass(schema="app_config",serialisable=false)
public class DomainConstraintType {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  private DAttr dc;
  
  /**derived fom {@link #dc}*/
  @DAttr(name="name",type=Type.String,length=20,mutable=false,auto=true)
  private String name;
  
  @DAttr(name="label",type=Type.String,length=30,mutable=false)
  private String label;
  
  
  public DomainConstraintType(DAttr dc, String label) {
    this.id = nextID(null);
    this.dc = dc;
    this.name = dc.name();
    this.label=label;
  }

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) {
        idCounter=num;
      }   
      return currID;
    }
  }
  
  public int getId() {
    return id;
  }
  
  public String getName() {
    return name;
  }

  public DAttr getDc() {
    return dc;
  }
  
  public String getLabel() {
    return label;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
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
    DomainConstraintType other = (DomainConstraintType) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "DomainConstraintType (" + id + ", " + name + ")";
  }
}
