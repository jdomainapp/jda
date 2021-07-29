package jda.modules.mccl.conceptmodel.view;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  This class represents an exclusion that specifies how a target region is excluded
 *  from being used at the same time as the source region.
 *  
 *  <p>This is used by the <tt>Region</tt> class to specify GUI components that are 
 *  excluded from viewing in certain GUIs.
 *  
 * @author dmle
 *
 */
@DClass(schema="app_config")
public class ExclusionMap {
  @DAttr(name="id",id=true,auto=true,type=Type.Integer,length=6,optional=false,mutable=false)
  private int id;
  
  @DAttr(name="source",type=Type.Domain,length=6,optional=false)
  @DAssoc(ascName="source-has",role="exclusion",
    ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=Region.class,cardMin=1,cardMax=1))
  private Region source;
  
  @DAttr(name="target",type=Type.Domain,length=6,optional=false)
  //TODO: do we need this?
//  @Association(name="target-excludedBy",role="exclusion",
//    type=AssocType.One2Many,endType=AssocEndType.Many,
//    associate=@AssocEnd(type=Region.class,cardMin=1,cardMax=1))
  private Region target;
  
  private static int idCounter = 0;

  
  /**
   * Constructor methods
   */
  public ExclusionMap(Integer id, Region source, Region target) {
    this.id = nextID(id);
    this.source = source;
    this.target = target;
  }

  public ExclusionMap(Region source, Region target) {
    this(null,source,target);
  }
  
  public Region getSource() {
    return source;
  }


  public void setSource(Region source) {
    this.source = source;
  }


  public Region getTarget() {
    return target;
  }


  public void setTarget(Region target) {
    this.target = target;
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
    return "ExclusionMap("+id+","+source.getName()+","+target.getName()+")";
  }
  
  /**
   * @effects 
   *  creates a <b>shallow</b> copy of this, i.e. all primitive-typed attributes are 
   *  copied, while all object-typed attributes have their references copied.
   * @version 2.8 
   */
  public ExclusionMap clone() {
    return new ExclusionMap(id, source, target);
  }
}
