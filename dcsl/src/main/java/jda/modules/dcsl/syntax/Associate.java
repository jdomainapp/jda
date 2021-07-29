package jda.modules.dcsl.syntax;

import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;

/**
 * @overview
 *  Represents an end of an association link to a specified object.  
 *  
 * @author dmle
 */
public class Associate {
  private Object associateObj;
  
  /** the association object of the {@link #associateObj}'s end*/
  private DAssoc myEndAssociation;
  
  /** domain constraint of the attribute associated to {@link #myEndAssociation}*/
  private DAttr myEndAttribute;
  
  /** the association object of the opposite end to {@link #associateObj}*/
  private DAssoc farEndAssociation;
  
  /** domain constraint of the attribute associated to {@link #farEndAssociation}*/
  private DAttr farEndAttribute;
  
  private boolean isDependent;
  
  public Associate(Object obj, 
      DAssoc myEndAssociation,
      DAttr myEndAttribute,
      DAssoc farEndAssociation,
      DAttr farEndAttribute,
      boolean isDependent) {
    this.associateObj = obj;
    this.myEndAssociation = myEndAssociation;
    this.myEndAttribute = myEndAttribute;
    this.farEndAssociation = farEndAssociation;
    this.farEndAttribute = farEndAttribute;
    this.isDependent = isDependent;
  }

  /**
   * @effects 
   * return the <tt>Association</tt> object of the opposite end of the <tt>associateObj</tt>'s
   */
  public DAssoc getFarEndAssociation() {
    return farEndAssociation;
  }

  /**
   * @effects 
   *  return the <b>type</b> of the <tt>Association</tt> of the <b>opposite end</b> of the <tt>associateObj</tt>'s
   */  
  public AssocType getAssociationType() {
    return farEndAssociation.ascType();
  }

  /**
   * @effects 
   * if the <b>type</b> of the <tt>Association</tt> of the <b>opposite end</b> of the <tt>associateObj</tt>'s is <tt>type</tt>
   *  return <tt>true</tt>
   * else
   *  return <tt>false</tt>
   */
  public boolean isAssociationType(AssocType type) {
    return getAssociationType() == type;
  }

  /**
   * @effects 
   * return the <b>end type</b> of the <tt>Association</tt> of the opposite end of the <tt>associateObj</tt>'s
   */
  public AssocEndType getFarEndType() {
    return farEndAssociation.endType();
  }
  
  /**
   * @effects 
   * if the <b>end type</b> of the <tt>Association</tt> at the <b>opposite end</b> of <tt>associateObj</tt>'s is <tt>endType</tt>
   *  return <tt>true</tt>
   * else
   *  return <tt>false</tt>
   */
  public boolean isFarEndType(AssocEndType endType) {
    return farEndAssociation.endType() == endType;
  }
  
  /**
   * @effects 
   * return the domain attribute of the <tt>Association</tt> of the opposite end of the <tt>associateObj</tt>'s
   */
  public DAttr getFarEndAttribute() {
    return farEndAttribute;
  }

  /**
   * @effects 
   * return the name of the domain attribute of the <tt>Association</tt> of the opposite end of the <tt>associateObj</tt>'s
   */
  public String getFarEndAttributeName() {
    return farEndAttribute.name();
  }
  
  /**
   * @effects 
   *  return the <tt>associateObj</tt>
   */
  public Object getAssociateObj() {
    return associateObj;
  }

  /**
   * @effects 
   *  return the domain class of <tt>associateObj</tt>
   */  
  public Class getAssociateClass() {
    return farEndAssociation.associate().type();
  }
  
  /**
   * @effects 
   * return the <tt>Association</tt> object of the <tt>associateObj</tt>'s end
   */
  public DAssoc getMyEndAssociation() {
    return myEndAssociation;
  }

  /**
   * @effects 
   * return the domain attribute of the <tt>associateObj</tt>'s end
   */
  public DAttr getMyEndAttribute() {
    return myEndAttribute;
  }
  
  /**
   * @effects 
   * return the <b>end type</tt> of the <tt>Association</tt> of the <tt>associateObj</tt>'s end
   */
  public AssocEndType getMyEndType() {
    return myEndAssociation.endType();
  }

  /**
   * @effects 
   * if the <b>end type</b> of the <tt>Association</tt> of the <tt>associateObj</tt>'s end is <tt>endType</tt>
   *  return <tt>true</tt>
   * else
   *  return <tt>false</tt>
   */
  public boolean isMyEndType(AssocEndType endType) {
    return myEndAssociation.endType() == endType;
  }

  /**
   * @effects 
   * if the <tt>associateObj</tt> depends on the association
   *  return <tt>true</tt>
   * else
   *  return <tt>false</tt>
   */  
  public boolean getIsDependent() {
    return isDependent;
  }

  @Override
  public String toString() {
    return "Associate (" + associateObj + ", " + 
        getAssociateClass() + ", " + getAssociationType() + ", " + isDependent
        + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((associateObj == null) ? 0 : associateObj.hashCode());
    result = prime * result
        + ((farEndAssociation == null) ? 0 : farEndAssociation.hashCode());
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
    Associate other = (Associate) obj;
    if (associateObj == null) {
      if (other.associateObj != null)
        return false;
    } else if (!associateObj.equals(other.associateObj))
      return false;
    if (farEndAssociation == null) {
      if (other.farEndAssociation != null)
        return false;
    } else if (!farEndAssociation.equals(other.farEndAssociation))
      return false;
    return true;
  }
}
