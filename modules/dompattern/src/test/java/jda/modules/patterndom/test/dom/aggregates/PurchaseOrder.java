package jda.modules.patterndom.test.dom.aggregates;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.dcsl.syntax.DCSLConstants;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class PurchaseOrder {
  private static final String ATTRIB_TOTAL = "total";

  private static final String ATTRIB_APPROVED_LIMIT = "approvedLimit";

  @DAttr(name = "id",type=Type.Integer, id=true)
  private int id;

  @DAttr(name=ATTRIB_APPROVED_LIMIT,type=Type.Double, mutable=false, 
      optional=false)
  private double approvedLimit;
  
  @DAttr(name = ATTRIB_TOTAL,type=Type.Double, mutable=false, optional=false)
  private double total;
  
  
//  /**
//   * @effects 
//   *
//   * @version 
//   */
//  public PurchaseOrder(int id, int approvedLimit) {
//    this.id = id;
//    this.approvedLimit = approvedLimit;
//  }
  
  /**
   * @effects return approvedLimit
   */
  public double getApprovedLimit() {
    return approvedLimit;
  }
  
  /**
   * @effects set approvedLimit = approvedLimit
   */
  public void setApprovedLimit(double approvedLimit) {
    this.approvedLimit = approvedLimit;
  }
  
  /**
   * @effects return total
   */
  public double getTotal() {
    return total;
  }
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "PurchaseOrder (" + id + ", " + approvedLimit + ", " + total + ")";
  }
  
  
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PurchaseOrder other = (PurchaseOrder) obj;
    return id == other.id;
  }

  /**
   * @effects return id
   */
//  @Override
  public Serializable getId() {
    return id;
  }
}
