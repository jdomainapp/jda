package jda.modules.mccl.conceptmodel.view;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
@DClass(schema="app_config")
public class RegionMap {
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,length=6,optional=false,mutable=false)
  private int id;
  
  @DAttr(name="parent",type=Type.Domain,length=6,optional=false)
  @DAssoc(ascName=Region.Assoc_hasChildren // v5.1: "parent-has"
    ,role="children",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Region.class,cardMin=1,cardMax=1))
  private Region parent;
  
  @DAttr(name="child",type=Type.Domain,length=6,optional=false)
  @DAssoc(ascName="child-has",role="parents",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Region.class,cardMin=1,cardMax=1))
  private Region child;
  
  @DAttr(name="displayOrder",type=Type.Integer,length=4)
  private Integer displayOrder;
  
  private static int idCounter = 0;

  
  /**
   * Constructor methods
   */
  public RegionMap(Integer id, Region parent, Region child, Integer displayOrder) {
    this.id = nextID(id);
    this.parent = parent;
    this.child = child;
    this.displayOrder = displayOrder;
  }

  public RegionMap(Region parent, Region child, Integer displayOrder) {
    this(null,parent,child,displayOrder);
  }
  
  public Region getParent() {
    return parent;
  }


  public void setParent(Region parent) {
    this.parent = parent;
  }


  public Region getChild() {
    return child;
  }


  public void setChild(Region child) {
    this.child = child;
  }


  public Integer getDisplayOrder() {
    return displayOrder;
  }


  public void setDisplayOrder(int displayOrder) {
    this.displayOrder = displayOrder;
  }


  public int getId() {
    return id;
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
  
  public String toString() {
    return "RegionMap("+id+","+parent.getName()+","+child.getName()+")";
  }
  
  /**
   * @effects 
   *  creates a <b>shallow</b> copy of this, i.e. all primitive-typed attributes are 
   *  copied, while all object-typed attributes have their references copied.
   * @version 2.8 
   */
  public RegionMap clone() {
    RegionMap m = new RegionMap(id, parent, child, displayOrder);
    
    return m;
  }
}
