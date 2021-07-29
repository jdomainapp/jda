package jda.modules.iexport.model;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.iexport.model.dodm.OSMType;

@DClass(serialisable=false)
public class Import {
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;
  
  @DAttr(name="osmType",type=DAttr.Type.Domain,optional=false)
  private OSMType osmType;
  
  @DAttr(name="domainClass",type=DAttr.Type.Domain,optional=false)
  private DomainClassType domainClass;

  public Import(OSMType osmType, DomainClassType domainClass) {
    id = nextID(null);
    this.osmType = osmType;
    this.domainClass = domainClass;
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

  public OSMType getOsmType() {
    return osmType;
  }

  public void setOsmType(OSMType osmType) {
    this.osmType = osmType;
  }

  public DomainClassType getDomainClass() {
    return domainClass;
  }

  public void setDomainClass(DomainClassType domainClass) {
    this.domainClass = domainClass;
  }
}
