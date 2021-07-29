package jda.util.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview
 *  Represents a data object that provides access to details about the changes to a domain object. 
 *  
 * @author dmle
 *
 */
public class ObjectUpdateData {
  private List<DAttr> updatedAttribs;
  private List newVals;
  private List oldVals;
  
  /**
   * @requires
   *  attribs != null /\ newVals != null /\ oldVals != null /\ 
   *  size(attribs) = size(newVals) = size(oldVals)
   *  
   * @effects  
   *  initialises this as <tt>(attribs, newVals, oldVals)</tt> 
   */
  public ObjectUpdateData(Collection<DAttr> updatedAttribs, Collection newVals, Collection oldVals) {
    this.updatedAttribs = new ArrayList<>();
    this.newVals = new ArrayList();
    this.oldVals = new ArrayList();
    this.updatedAttribs.addAll(updatedAttribs);
    this.newVals.addAll(newVals);
    this.oldVals.addAll(oldVals);
  }

  public Collection<DAttr> getUpdatedAttribs() {
    return updatedAttribs;
  }

//  public void setAttribs(Collection<DomainConstraint> updatedAttribs) {
//    this.updatedAttribs = updatedAttribs;
//  }

//  public Collection getNewVals() {
//    return newVals;
//  }

//  public void setNewVals(Collection newVals) {
//    this.newVals = newVals;
//  }

//  public Collection getOldVals() {
//    return oldVals;
//  }

  public Object getOldVal(DAttr attrib) {
    int ind = updatedAttribs.indexOf(attrib);
    return oldVals.get(ind);
  }

  public Object getNewVal(DAttr attrib) {
    int ind = updatedAttribs.indexOf(attrib);
    return newVals.get(ind);
  }

//  public void setOldVals(Collection oldVals) {
//    this.oldVals = oldVals;
//  }
  
}
