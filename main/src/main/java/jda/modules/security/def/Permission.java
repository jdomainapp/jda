package jda.modules.security.def;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public abstract class Permission {
  @DAttr(name="id",id=true,auto=true,type=DAttr.Type.Integer,mutable=false,length=6)
  private int id;
  @DAttr(name="action",type=DAttr.Type.Domain,length=15)
  private Action action;
  
  @DAttr(name="resource",type=DAttr.Type.Domain,length=6)
  @DAssoc(ascName="resource-has-permissions",role="permission",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Resource.class,cardMin=1,cardMax=1)) 
  private Resource resource;
  
  private static int idCounter;
  
  //constructor methods
  public Permission(Integer id, Action action, Resource resource) {
    this.id = nextID(id);
    this.setAction(action);
    this.setResource(resource);
  }
  
  public Permission(Action action, Resource resource) {
    this(null, action, resource);
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

  public void setAction(Action action) {
    this.action = action;
  }

  public Action getAction() {
    return action;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public Resource getResource() {
    return resource;
  }
  
  public String toString() {
    return this.getClass().getSimpleName()+"("+id+","+action.getName()+","+resource+")";
  }
}
