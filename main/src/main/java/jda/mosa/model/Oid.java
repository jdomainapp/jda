package jda.mosa.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview
 *  Represents an object id.  
 *  
 *  <p><tt>Oid</tt> is used to uniquely identify a domain object in its object pool so that the same object
 *  can be shared between different application functions.
 *  
 *  <p>During an application's run-time, a domain object may be associated with different <tt>Oid</tt>s
 *  depending on the functions that need to use that object.
 *  
 *  <p>The hash code of an <tt>Oid</tt> is computed based on the name of the domain class
 *  and the id attribute value(s). To ensure that the hash code(s) of the <tt>Oid</tt>s of the same 
 *  object are the same, it is important <b>to maintain the id attribute values if they are mutable</b>.
 *  
 *  <p>It is therefore generally recommended (for performance and safety) to <b>use   
 *  immutable id attribute(s)</b> to form the <tt>Oid</tt>
 *  
 * @author dmle
 */
public class Oid implements Comparable {
  
//  /** v2.7.2: a globally unique, immutable, transient auto-generated id used to identify each Oid
//   *   it is safer to to use this to generate the hash code of the Oids, because the id values can be changed.
//   *   
//   *   Note: this auto-gen id is transient and is not guaranteed to be the same for the same Oid between different
//   *   runs of the application.  
//   */
//  private long autoGenId;
  
  /**
   * the domain class based on which this object id is defined  
   **/
  private Class cls;
  
  /**
   * a list of pairs <attrib,val> that make up the object id, 
   * where attrib is an id attribute and val is the value of that attrib in 
   * {@link #object}
   */
  /* v2.7.2: changed to Map
  private List<Tuple2<DomainConstraint,Comparable>> idAttribVals;
   */
  private Map<DAttr,Comparable> idAttribVals;
  
  // derived: to speed up performance of some lookup operations
  private List<DAttr> idAttribs;
  
  /**
   * @requires 
   *  c != null
   *  
   * @effects
   *  initialise this as <c,List[]>
   */
  public Oid(Class c) {
    //
    this.cls = c;
    idAttribVals = new LinkedHashMap<>();//new LinkedList<Tuple2<DomainConstraint,Comparable>>();
    idAttribs = new ArrayList<>();

//    autoGenId = System.nanoTime();
  }
  
  /**
   * @requires
   *  attrib != null /\ val != null
   * @effects 
   *  add <attrib,val> to this
   */
  public void addIdValue(DAttr attrib, Comparable val) {
    //idAttribVals.add(new Tuple2(attrib,val));
    idAttribVals.put(attrib,val);
    idAttribs.add(attrib);
  }

//  /**
//   * 
//   */
//  public Comparable[] getIdValues() {
//    Comparable[] idVals = new Comparable[idAttribVals.size()];
//    int ind = 0;
//    for (Tuple2<DomainConstraint,Comparable> idt : idAttribVals) {
//      idVals[ind] = idt.getSecond();
//      ind++;
//    }
//    
//    return idVals;
//  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer(cls.getSimpleName());
    sb.append(":<");
    
    int index = 0;
    final int sz = idAttribVals.size();
    //for (Tuple2<DomainConstraint,Comparable> idTuple : idAttribVals) {
    for (Entry<DAttr,Comparable> idTuple : idAttribVals.entrySet()) {
      sb.append(idTuple.getKey().name()).append(":").append(idTuple.getValue());
      if (index < sz-1) {
        sb.append(",");
      }
      index++;
    }
    sb.append(">");
    
    return "Oid (" + sb + ")";
  }

  /**
   * @effects 
   *  return a <tt>String</tt> representation of <tt>this.idValues</tt> of the form <tt>clsName_idVal1_...</tt>
   */
  public String toValueString() {
    StringBuffer sb = new StringBuffer(cls.getSimpleName());
    sb.append("_");
    
    int index = 0;
    final int sz = idAttribVals.size();
    for (Entry<DAttr,Comparable> idTuple : idAttribVals.entrySet()) {
      sb.append(idTuple.getValue());
      if (index < sz-1) {
        sb.append("_");
      }
      index++;
    }
    
    return sb.toString();
  }

  @Override
  public int hashCode() {
    /*v2.7.2: changed to use only the id values: 
    final int prime = 31;
    int result = 1;
    
    result = prime * result + cls.getName().hashCode();
    
    result = prime * result
        + ((idAttribVals == null) ? 0 : idAttribVals.hashCode()); */
    
    
    final int prime = 31;
    int result = 1;

    result = prime * result + cls.getName().hashCode();
    
    if (idAttribVals != null) {
      //for (Tuple2<DomainConstraint,Comparable> idTuple : idAttribVals) {
      for (Entry<DAttr,Comparable> idTuple : idAttribVals.entrySet()) {
        result = prime * result + idTuple.getValue().hashCode();
      }
    }
    
    /* this code does not work because autoGenId differs between different gens that are performed in the same run
    final int prime = 31;
    int result = 1;

    result = prime * result + cls.getName().hashCode();
    result = prime * result + ((int) autoGenId);
    */
    
    return result;
  }

  /**
   * @requires 
   *  <tt>idVals != null</tt>  
   * @effects 
   *  if <tt>idVals</tt> are exactly the id values of this (in that order) 
   *    return true
   *  else
   *    return false
   * @version 2.7.2
   */
  public boolean equals(Object[] idVals) {
    if (idVals.length != size()) {
      return false;
    } else {
      Iterator<Comparable> myIdVals = idAttribVals.values().iterator();
      
      for (Object val : idVals) {
        if (val == null || !val.equals(myIdVals.next())) {
          return false;
        }
      }
      
      return true;
    }
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Oid other = (Oid) obj;

    /* TODO: uncomment the following to have a strict comparison b/w Oids, which requires that 
     * the Oid's domain classes be the same.
     * 
     * For now, the use of Oid in the framework is such that they are always associated with the domain class
     * and thus, it is sufficient to compare Oids based
     * on just the id-attribute values
     */
    //if (cls != other.cls)
    //  return false;
    
    if (idAttribVals == null) {
      if (other.idAttribVals != null)
        return false;
    } else if (!idAttribVals.equals(other.idAttribVals))
      return false;
     return true;
  }

  /**
   * @requires 
   *  idAttribs != null /\ idVals != null /\ 
   *  idAttribs.length = idVals.length
   *  
   * @effects 
   *  if <tt>this.idAttribVals</tt> match <tt>idAttribs</tt> and <tt>idVals</tt>
   *    return true
   *  else
   *    return false 
   *  
   * <br>throws NotPossibleException if a specified id value is not valid 
   */
  public boolean equals(DAttr[] idAttribs, Object[] idVals) throws NotPossibleException {
    Object idVal;
    int index = 0;
    
    Comparable val;
    for (DAttr d : idAttribs) {
      idVal = idVals[index];
      
      if (!(idVal instanceof Comparable))
        throw new NotPossibleException(NotPossibleException.Code.INVALID_OBJECT_ID_TYPE, 
            new Object[] {cls.getSimpleName(),"-",idVal});
      
      //if (!contains(d, (Comparable) idVal)) {
      val = idAttribVals.get(d);
      if (val == null || !val.equals((Comparable) idVal)) {
        return false;
      }
      
      index++;
    }
    
    return true;
  }
  
//  /**
//   * @effects 
//   *  if this contains (idAttrib,idVal) 
//   *    return true
//   *  else
//   *    return false
//   */
//  private boolean contains(DomainConstraint idAttrib, Comparable idVal) {
//    for (Tuple2<DomainConstraint,Comparable> tuple : idAttribVals) {
//      if (tuple.getFirst().equals(idAttrib) && tuple.getSecond().equals(idVal)) {
//        return true;
//      }
//    }
//    
//    return false;
//  }
  
  /**
   * @requires 
   *  o != null  
   * @effects 
   *  if o.class is not Oid 
   *    throws ClassCastException
   *  else if id vals of this are pair-wise smaller than id vals of o
   *    return negative integer
   *  else if id vals of this are pair-wise equal to id vals of o
   *    return 0
   *  else if id vals of this are pair-wise greater than id vals of o
   *    return positive integer
   *  else 
   *    throw NotPossibleException  
   */
  @Override
  public int compareTo(Object o) throws ClassCastException, NotPossibleException {
    if (o.getClass() != Oid.class)
      throw new ClassCastException("Oid.compareTo: not comparable with: " + o);
    
    /* compare by values of id attributes
     * if id vals of this are pair-wise smaller than id vals of o
     *  return -1
     * else if id vals of this are pair-wise greater than id vals of o
     *  return 1
     * else 
     *  throw NotPossibleException // not comparable
     */
    Oid other = (Oid) o;
    Comparable id1, id2;
    //Iterator<Tuple2<DomainConstraint,Comparable>> yourTuples = other.idAttribVals.iterator();
    Iterator<Entry<DAttr,Comparable>> yourTuples = other.idAttribVals.entrySet().iterator();
    
    Boolean smaller = null;
    int comp;
    //for (Tuple2<DomainConstraint,Comparable> idTuple : idAttribVals) {
    for (Entry<DAttr,Comparable> idTuple : idAttribVals.entrySet()) {
      id1 = idTuple.getValue();
      id2 = yourTuples.next().getValue();
      comp = id1.compareTo(id2);
      if (comp < 0) {
        if (smaller == null) 
          smaller = true;
        else if (smaller == false) // greater
          throw new NotPossibleException(null, "Not comparable with: " + o);
      } else if (comp > 0) { 
        if (smaller == null) 
          smaller = false;
        else if (smaller == true) // smaller
          throw new NotPossibleException(null, "Not comparable with: " + o);
      } else {
        // comp = 0 -> ignore
      }
    }
    
    if (smaller == null)
      return 0;
    else if (smaller == true)
      return -1;
    else
      return 1;
  }

//  public Iterator<Tuple2<DomainConstraint, Comparable>> idIterator() {
//    return idAttribVals.iterator();
//  }
  
  public int size() {
    return idAttribVals.size();
  }

  /**
   * @effects
   *  if i < 0 OR i >= size()
   *    throw IndexOutOfBoundsException 
   *  else
   *    return the id value ith
   */
  public Comparable getIdValue(int i) throws IndexOutOfBoundsException {
    if (i < 0 || i >= size())
      throw new IndexOutOfBoundsException("Oid.getIdValue: " + i);
    
    //return idAttribVals.get(i).getSecond();
    DAttr attrib = idAttribs.get(i);
    return idAttribVals.get(attrib);
  }
  
  /**
   * @effects
   *  if i < 0 OR i >= size()
   *    throw IndexOutOfBoundsException 
   *  else
   *    return the name of the id attribute ith
   */
  public String getIdAttributeName(int i) throws IndexOutOfBoundsException {
    return getIdAttribute(i).name();
  }
  
  /**
   * @effects
   *  if i < 0 OR i >= size()
   *    throw IndexOutOfBoundsException 
   *  else
   *    return the id attribute ith as <tt>DomainConstraint</tt>
   */
  public DAttr getIdAttribute(int i) throws IndexOutOfBoundsException {
    if (i < 0 || i >= size())
      throw new IndexOutOfBoundsException("Oid.getIdAttribute: " + i);
    
    return idAttribs.get(i); //idAttribVals.get(i).getFirst();
  }

  /**
   * @effects 
   *  return the domain class based on which <tt>this</tt> is defined, 
   *  i.e. the class that defines the type of object bearing the id defined by <tt>this</tt>
   * @return
   */
  public Class getCls() {
    return cls;
  }

  /**
   * This method is used to update Oid when the id attribute values have changed
   * 
   * @effects 
   *  sets the value of <tt>idAttrib</tt> in this to <tt>val</tt>
   *  
   *  <p><b>CAUTION</b>: this method is dangerous!!!
   *  
   * @version 2.7.2
   */
  public void setIdAttributeValue(DAttr idAttrib, Comparable val) {
    idAttribVals.put(idAttrib, val);
  }
}
