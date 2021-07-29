package jda.modules.security.def;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;

/**
 * A sub-class of {@see Resource} that represents all the logical resources in an application.<br>
 * Logical resources include classes, groups of classes (called schemas) and objects.
 * 
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class LogicalResource extends Resource {
  // v2.7.2: added support for object group (only applicable if type=Object)
  @DAttr(name="objectGroup",type=DAttr.Type.Domain,length=30)
  @DAssoc(ascName="resource-has",role="resource",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=ObjectGroup.class,cardMin=0,cardMax=1))  
  private ObjectGroup objectGroup;
  
  /**
   * Constructors to create objects from data source
   */
//  public LogicalResource(Integer id, String name, String description, Type type) {
//    super(id,name,description,type);
//  }
  public LogicalResource(Integer id, String name, String description, Type type, ObjectGroup objectGroup) {
    super(id,name,description,type);
    this.objectGroup = objectGroup;
  }

  /**
   * Constructors to create objects to store into the data source
   */
  public LogicalResource(String name, String description, Type type) {
    this(name, description, type, null);
  }
  
  /**
   * Constructors to create objects to store into the data source
   */
  public LogicalResource(String name, String description, Type type, ObjectGroup objectGroup) {
    super(name, description, type);
    this.objectGroup = objectGroup;
  }

  public ObjectGroup getObjectGroup() {
    return objectGroup;
  }

  public void setObjectGroup(ObjectGroup objectGroup) {
    this.objectGroup = objectGroup;
  }
  
}
