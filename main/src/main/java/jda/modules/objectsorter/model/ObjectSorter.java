package jda.modules.objectsorter.model;

import java.util.Collection;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.util.ObjectComparator.SortBy;

/**
 * @overview
 *  Capture information (e.g. sort by ASC or DESC, the domain attribute used for sorting) 
 *  that is needed to sort domain objects.  
 *  
 * @author dmle
 *
 */
@DClass(serialisable=false)
public class ObjectSorter {
    
  @DAttr(name = "id", id = true, auto = true, type = Type.Integer, length = 5, 
      optional = false, mutable = false)
  private int id;
  private static int idCounter = 0;
  
  @DAttr(name="sortBy",type=DAttr.Type.Domain,optional=false)
  private SortBy sortBy;

  @DAttr(name="attributes",type=DAttr.Type.Collection,optional=false)
  private Collection<DomainConstraintType> attributes;
  
  /**
   * derived from {@link #attributes} after user selected one attribute from the view
   */
  @DAttr(name="selectedAttrib",type=DAttr.Type.Domain,optional=false,
      auto=true,serialisable=true,
      sourceAttribute="attributes")
  private DomainConstraintType selectedAttrib;
  
  private DataController targetModule;
  
  public ObjectSorter(SortBy sortBy,
      Collection<DomainConstraintType> attributes, DomainConstraintType selectedAttrib, DataController targetModule) {
    this.id = nextID(null);
    this.sortBy = sortBy;
    this.attributes = attributes;
    this.selectedAttrib = selectedAttrib;
    this.targetModule = targetModule;
  }

  private static int nextID(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();
      
      if (num > idCounter) {
        idCounter=num;
      }   
      return currID;
    }
  }
  
  
  public int getId() {
    return id;
  }


  public SortBy getSortBy() {
    return sortBy;
  }

  public void setSortBy(SortBy sortBy) {
    this.sortBy = sortBy;
  }

  public Collection<DomainConstraintType> getAttributes() {
    return attributes;
  }

  public void setAttributes(Collection<DomainConstraintType> attributes) {
    this.attributes = attributes;
  }

  public DataController getTargetModule() {
    return targetModule;
  }

  public void setTargetModule(DataController targetModule) {
    this.targetModule = targetModule;
  }

  public DomainConstraintType getSelectedAttrib() {
    return selectedAttrib;
  }

  public void setSelectedAttrib(DomainConstraintType selectedAttrib) {
    this.selectedAttrib = selectedAttrib;
  }

  public DAttr getSelectedAttribDc() {
    return (selectedAttrib != null) ? selectedAttrib.getDc() : null;
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
    ObjectSorter other = (ObjectSorter) obj;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ObjectSorter (" + id + ")";
  }

}
