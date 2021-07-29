package jda.modules.security.def;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview
 *  A group of domain objects of a domain class (<tt>T</tt>) to which an Object-based logical resource is applied. 
 *  
 *  <p>This is useful for situations in which the logical resource represents not the whole set of domain objects
 *  of a class but only a sub-set of it. 
 *  
 *  <p>The domain objects that are member of an <tt>ObjectGroup</tt> are recorded by the attribute {@link ObjectGroup#memberships}.
 *  This attribute is a Collection-type, which is parameterised to the {@link ObjectGroupMembership} class. This class
 *  represents a membership mapping of a domain object in an <tt>ObjectGroup</tt>. 
 *   
 * @author dmle
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public class ObjectGroup {
  @DAttr(name="id",id=true,auto=true,type=Type.Long,optional=false,mutable=false)
  private long id;
  
  @DAttr(name="resource",type=DAttr.Type.Domain,
      serialisable=false)
  @DAssoc(ascName="resource-has",role="group",
      ascType=AssocType.One2One,endType=AssocEndType.One,
      associate=@Associate(type=LogicalResource.class,cardMin=1,cardMax=1,determinant=true))  
  private LogicalResource resource;

  @DAttr(name=AttributeName_memberships,type=DAttr.Type.Collection,
      serialisable=false, filter=@Select(clazz=ObjectGroupMembership.class))
  @DAssoc(ascName="group-has",role="group",
      ascType=AssocType.One2Many,endType=AssocEndType.One,
      associate=@Associate(type=ObjectGroupMembership.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE))  
  private List<ObjectGroupMembership> memberships;

  private static final String AttributeName_memberships = "memberships";
  
  public ObjectGroup(Long id) {
    this.id = nextID(id);
    memberships = new ArrayList<ObjectGroupMembership>();
  }
  
  public ObjectGroup(LogicalResource resource) {
    this.id = nextID(null);
    memberships = new ArrayList<ObjectGroupMembership>();
    this.resource = resource;
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

  public Collection<ObjectGroupMembership> getMemberships() {
    return memberships;
  }

  public Integer getMembershipsCount() {
    return memberships.size();
  }
  
  /**
   * @effects 
   *  if exists <tt>ObjectGroupMemberShip</tt> whose id hash is <tt>idHash</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean containsMember(int idHash) {
    for (ObjectGroupMembership grpm : memberships) {
      if (grpm.getOIdHashCode() == idHash)
        return true;
    }
    
    return false;
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=AttributeName_memberships)
  public void addMembership(ObjectGroupMembership membership) {
    this.memberships.add(membership);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  @AttrRef(value=AttributeName_memberships)
  public void addMembership(List<ObjectGroupMembership> memberships) {
    this.memberships.addAll(memberships);
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  @AttrRef(value=AttributeName_memberships)
  public void removeMembership(ObjectGroupMembership membership) {
    this.memberships.remove(membership);
  }
  
  public LogicalResource getResource() {
    return resource;
  }

  public void setResource(LogicalResource resource) {
    this.resource = resource;
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
    ObjectGroup other = (ObjectGroup) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ObjectGroup (" + id + ")";
  }
}
