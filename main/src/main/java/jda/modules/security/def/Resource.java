package jda.modules.security.def;

import java.util.ArrayList;
import java.util.List;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
@DClass(schema=DCSLConstants.SECURITY_SCHEMA)
public abstract class Resource {
  @DAttr(name="id",id=true,auto=true,type=DAttr.Type.Integer,mutable=false,length=6)
  private int id; 
  @DAttr(name="name",type=DAttr.Type.String,mutable=true,length=100)
  private String name;
  @DAttr(name="description",type=DAttr.Type.String,length=50)
  private String description;
  @DAttr(name="type",type=DAttr.Type.Domain,length=10)
  private Type type;
 
  // the permissions that apply to this resource
  @DAttr(name="permissions",type=DAttr.Type.Collection,
      serialisable=false,
      filter=@Select(clazz=Permission.class)//,role="resource"
      )
  //@Update(add="addPermission",delete="removePermission")
  @DAssoc(ascName="resource-has-permissions",role="resource",
    ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Permission.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE))  
  private List<Permission> permissions;
  
  private static int idCounter = 0;
  
  // some pre-defined resources
  public static final LogicalResource LogicalAny = new LogicalResource(Resource.Type.Any.name(), "everything",
      Resource.Type.Any); 
  
  // constructor methods
  public Resource(Integer id, String name, String description, Type type 
      //List<? extends Permission> permissions
      ) {
    this.id = nextID(id);
    this.name = name;
    this.description = description;
    this.type = type;
    this.permissions = new ArrayList();
  }

//  public Resource(Integer id, String name, String description, Type type) {
//    this(id,name,description,type,null);
//  }
  
  public Resource(String name, String description, Type type
//      , List<? extends Permission> permissions
      ) {
    this(null, name, description, type);
  }
  
  /** Resource types */
  public static enum Type {
    /** group of domain classes*/
    Schema, 
    /** a domain class */
    Class, 
    /** domain attribute of a domain class */
    Attribute, 
    /** domain objects of a domain class */
    Object, 
    /** menu items*/
    Menu,
    /** any resource*/
    Any; 
    
    @DAttr(name="name",id=true,type=DAttr.Type.String,length=15)
    public String getName() { return name();}
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

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  public void addPermission(Permission perm) {
    permissions.add(perm);
  }

  @DOpt(type=DOpt.Type.LinkAdder)
  public void addPermission(List<Permission> perms) {
    permissions.addAll(perms);
  }

  @DOpt(type=DOpt.Type.LinkRemover)
  public void removePermission(Permission perm) {
    permissions.remove(perm);
  }

  public void setPermissions(List<Permission> permissions) {
    this.permissions = permissions;
  }

  public List<Permission> getPermissions() {
    return permissions;
  }

  public Integer getPermissionsCount() {
    return permissions.size();
  }
  
//  public void setPermissions(List<? extends Permission> permissions) {
//    this.permissions = permissions;
//  }

//  public List<? extends Permission> getPermissions() {
//    return permissions;
//  }
  
  public boolean isType(Type t) {
    return (t != null && t.equals(this.type));
  }
  
  public boolean equals(Object o) {
    return (o != null &&
        (o instanceof Resource) && 
        ((Resource)o).id == this.id);
  }
  
  /**
   * @effects 
   *  if this equals <tt>other</tt> by {@link #name}
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean equalsByName(Resource o) {
    return (o != null && this.name.equals(o.getName()));
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
    return this.getClass().getSimpleName()+"("+id+","+name+","+type+")";
  }

}
