package jda.modules.iexport.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  Represents a domain class.
 *  
 * @author dmle
 */
@DClass(schema="app_config",serialisable=false)
public class DomainClassType<T> {
  
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  private Class<T> cls;
  
  /**derived fom {@link #cls}*/
  @DAttr(name="simpleName",type=Type.String,length=100,mutable=false,auto=true,serialisable=false)
  private String simpleName;
  
  /**computed fom {@link #cls}*/
  @DAttr(name="classLabel",type=Type.String,length=100)
  private String classLabel;

  public DomainClassType(Class<T> cls, String classLabel) {
    this.id = nextID(null);
    this.cls = cls;
    this.simpleName = cls.getSimpleName();
    this.classLabel = classLabel;
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

  public String getClassLabel() {
    return classLabel;
  }

  public void setClassLabel(String classLabel) {
    this.classLabel = classLabel;
  }

  public Class<T> getCls() {
    return cls;
  }

  public String getSimpleName() {
    return simpleName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((cls == null) ? 0 : cls.getName().hashCode());
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
    
    DomainClassType other = (DomainClassType) obj;
    if (cls == null) {
      if (other.cls != null)
        return false;
    } else if (cls != other.cls)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return DomainClassType.class.getSimpleName()+" (" + simpleName + ")";
  }
}
