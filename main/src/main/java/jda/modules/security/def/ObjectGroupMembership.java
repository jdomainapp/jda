package jda.modules.security.def;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *  Represents a membership mapping of a domain object of a domain class (<tt>T</tt>) in an {@link ObjectGroup}
 *  
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class ObjectGroupMembership {
  @DAttr(name="id",id=true,auto=true,type=Type.Long,optional=false,mutable=false)
  private long id;
  
  @DAttr(name="group",type=DAttr.Type.Domain)
  @DAssoc(ascName="group-has",role="member",
  ascType=AssocType.One2Many,endType=AssocEndType.Many,
  associate=@Associate(type=ObjectGroup.class,cardMin=1,cardMax=1))
  private ObjectGroup group;

  @DAttr(name="oIdHashCode",type=DAttr.Type.Integer,optional=false)
  private int oIdHashCode;

  /**
   * This constructor is used to create objects from the data source
   */
  public ObjectGroupMembership(Long id, ObjectGroup group, Integer objectIdHash) {
    this.id = nextID(id);
    this.group = group;
    this.oIdHashCode = objectIdHash;
  }

  /**
   * This constructor is used to create objects to store into the data source
   */
  public ObjectGroupMembership(ObjectGroup group, Integer objectIdHash) {
    this(null, group, objectIdHash);
  }

  private long nextID(Long id) {
    if (id == null)
      return System.nanoTime();
    else
      return id;
  }

  public long getId() {
    return id;
  }

  public ObjectGroup getGroup() {
    return group;
  }

  public void setGroup(ObjectGroup group) {
    this.group = group;
  }


  public int getOIdHashCode() {
    return oIdHashCode;
  }

  public void setOIdHashCode(int oIdHashCode) {
    this.oIdHashCode = oIdHashCode;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
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
    ObjectGroupMembership other = (ObjectGroupMembership) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ObjectGroupMembership (" + group + ", " + oIdHashCode + ")";
  }
}
