package jda.util;

import java.util.Comparator;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.dsm.DSMBasic;

/**
 * @overview
 *  A sub-type of {@link Comparator} that compares objects based on the values of a specific 
 *  domain attribute in a given sort-by order. 
 *  
 * @author dmle
 *
 */
public class ObjectComparator implements Comparator {
  
  public static enum SortBy {
    ASC,
    DESC;
    
    // these constants are used in the configuration to refer to the corresponding SortBy enums (above)
    public static final String ASC_NAME = "ASC";
    public static final String DESC_NAME = "DESC";
    
    @DAttr(name="name",type=DAttr.Type.String,id=true,length=10,optional=false)
    public String getName() {
      return name();
    }
  }
  
  private DAttr sortAttrib;
  private SortBy sortBy;
  private DSMBasic dsm;
  
  public ObjectComparator(DSMBasic dsm, DAttr sortAttrib, SortBy sortBy) {
    this.dsm = dsm;
    this.sortAttrib = sortAttrib;
    this.sortBy = sortBy;
  }

  public DAttr getSortAttrib() {
    return sortAttrib;
  }

  public void setSortAttrib(DAttr sortAttrib) {
    this.sortAttrib = sortAttrib;
  }


  public String getSortAttribName() {
    return sortAttrib.name();
  }

  public void setSortBy(SortBy sortBy) {
    this.sortBy = sortBy;
  }
  
  public SortBy getSortBy() {
    return sortBy;
  }

  @Override
  public int compare(Object o1, Object o2) {
    String attribName = getSortAttribName();
    
    Object v1 = dsm.getAttributeValue(o1, attribName);
    Object v2 = dsm.getAttributeValue(o2, attribName);
    
    int ascendOrDescend = (sortBy == SortBy.ASC) ? 1 : -1;
    
    // DESC: reverse the comparison
    if (v1 instanceof Comparable && v2 instanceof Comparable) {
      return ascendOrDescend * ((Comparable)v1).compareTo((Comparable)v2);
    } else {
      // undeterminstic
      return ascendOrDescend * (v1+"").compareTo(v2+"");
    }
  }

  /**
   * @effects 
   *  if this is currently sorting objects by <b>id</b> domain attribute AND in ascending order
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isSortingIdAttributeAsc() {
    return sortAttrib.id() && sortBy == SortBy.ASC;
  }

  @Override
  public String toString() {
    return "ObjectComparator (" + getSortAttribName() + ": " + sortBy + ")";
  }
}
