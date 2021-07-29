package jda.modules.mccl.conceptmodel.view;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.security.def.DomainUser;
import jda.util.properties.Property;
import jda.util.properties.PropertySet;
import jda.util.properties.PropertySet.PropertySetType;

/**
 * @overview
 *  Implements the association between {@link DomainUser} and {@link Region}. It stores <tt>Region</tt> attribute 
 *  values that need to be customised for each user in a {@link PropertySet}.
 *   
 * @author dmle
 */
@DClass(schema=DCSLConstants.CONFIG_SCHEMA)
public class UserRegion {
  public static final String AttributeName_User = "user";
  public static final String AttributeName_Region = "region";
  
  @DAttr(name = "id", id = true, auto = true, type = DAttr.Type.Integer, length = 6, optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;

  @DAttr(name=AttributeName_User,type=DAttr.Type.Domain,optional=false)
  private DomainUser user;
  
  @DAttr(name=AttributeName_Region,type=DAttr.Type.Domain,optional=false)
  private Region region;

  @DAttr(name="regionAttributeVals",type=DAttr.Type.Domain)
  private PropertySet regionAttributeVals;
  
  /**
   * @effects 
   *  create object from data source
   */
  public UserRegion(Integer id, DomainUser user, Region region,
      PropertySet regionAttributeVals) {
    this.id = nextID(id);
    this.user = user;
    this.region = region;
    this.regionAttributeVals = regionAttributeVals;
  }

  /**
   * @effects 
   *  create object from application
   */
  public UserRegion(DomainUser user, Region region) {
    this(null, user, region, null);
  }
  
  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter)
        idCounter = num;

      return currID;
    }
  }

  public DomainUser getUser() {
    return user;
  }

  public void setUser(DomainUser user) {
    this.user = user;
  }

  public Region getRegion() {
    return region;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

  public PropertySet getRegionAttributeVals() {
    return regionAttributeVals;
  }

  public void setRegionAttributeVals(PropertySet regionAttributeVals) {
    this.regionAttributeVals = regionAttributeVals;
  }

  /**
   * @effects 
   *  invoke {@link #setAttributeVal(PropertyName, Object)} for each entry <tt>(a,v) in newAttribVals</tt>
   */
  public void setAttributeVals(Map<DAttr, Object> newAttribVals) {
    if (newAttribVals == null)
      return;
    
    for (Entry<DAttr,Object> e : newAttribVals.entrySet()) {
      setAttributeVal(e.getKey().name(), e.getValue());
    }
  }

  /**
   * @effects 
   *  if {@link #regionAttributeVals} is null
   *    initialise it as empty
   *  
   *  set value of attribute named <tt>attributeName</tt> to <tt>val</tt>
   */
  public void setAttributeVal(String attributeName, Object val) {
    if (regionAttributeVals == null) {
      regionAttributeVals = new PropertySet(UserRegion.class.getSimpleName(), PropertySetType.Other);
    }
    
    regionAttributeVals.setProperty(attributeName, val);
  }
  
  /**
   * @requires
   *  regionAttributeVals != null
   * @effects 
   *  return the value of the attribute named <tt>attributeName</tt> or 
   *  return <tt>null</tt> if no such attribute exists.
   */
  public Object getAttributeVal(PropertyName attributeName) {
    if (regionAttributeVals == null)
      return null;
    
    return regionAttributeVals.getPropertyValue(attributeName, null);
  }
  
  /**
   * This method is used to merge the property settings of {@link #regionAttributeVals} into {@link #region}.
   *  
   * @requires 
   *  this is initialised
   *  
   * @effects <pre>
   *  if {@link #regionAttributeVals} != null
   *    for each entry (a,v) in regionAttributeVals
   *      set attribute {@link #region}.a' = v, where equals(a,a')
   *   </pre>
   */
  public void updateRegion(DOMBasic dom) throws NotFoundException, NotPossibleException {
    if (regionAttributeVals == null || region == null)
      return;
    
    Collection<Property> props = regionAttributeVals.getProps();
    String a; Object v;
    for (Property prop : props) {
      a = prop.getPkey();
      v = prop.getValue();
      
      dom.setAttributeValue(region, a, v);
    }
  }
  
  public int getId() {
    return id;
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
    UserRegion other = (UserRegion) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "UserRegion (" + id + ", " + user + ", " + region + ")";
  }
}
