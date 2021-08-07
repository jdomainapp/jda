package jda.modules.patterndom.assets.aggregates.complex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.conceptmodel.constraints.Constraint;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMToolkit;
import jda.modules.dodm.dsm.DSM;
import jda.modules.patterndom.assets.domevents.Publisher;

/**
 * @overview 
 *  Represents Aggregate in the DDD Pattern: AGGREGATES.
 *  
 * An AGGREGATE is a cluster of associated objects that we treat as a unit for
 * the purpose of data changes. Aggregates has the following properties:
 * <li>has a root and a boundary
 * <li>root has global identity (unique and accessible to outside objects)
 * <li>nonroot objects have local identity, unique only within the AGGREGATE
 * <li>nonroot objects may hold references to each other
 * <li>reference to root is accessible to outside objects and can be persisted
 * <li>references to nonroot objects can also be available to outside objects
 * (through root) but only transiently (within a transaction or operation call)
 * <li>value object can be handed (by root) to outside object
 * <li>(corollary to the previous rule), only root can be obtained directly with
 * database queries. Nonroot objects must be found by traversal of associations.
 * <li>objects can hold references to other aggregate roots.
 * <li>root is responsible for checking invariants. Each invariant must hold at
 * the end of every transaction. Invariant is a consistency rules involving
 * relationships between members of the AGGREGATE.
 * <li>deleting root also deletes other nonroot objects
 *
 * @author Duc Minh Le (ducmle)
 *
 * @param <T> the domain class of the aggregate root
 */
public abstract class Aggregate<T extends AGRoot> {
  /**
   * A unique name
   */
  private String name;
  
  /**
   * Aggregate's root
   */
  private T root;
  
  /**
   * Aggregate.members is not needed as most of the operations can be performed
   * using other attributes (e.g. {@link #boundary}). 
   * Further, because constraints are defined directly on the objects and are evaluated
   * outside of aggregates, there is no need to keep the member object references here.
   * Doing so would unnecessarily increase object management overhead and impact performance. 
   */
  @Deprecated
  private Collection members;
  
  /**
   * Constraints representing the invariants of an aggregate
   */
  private Collection<Constraint> constraints;
  
  /**
   * Domain model manager 
   */
  private DSM dsm;
  
  /**
   *  the domain classes of the nonroot members ({@link #members}). 
   *  These classes define the boundary of this aggregate
   */
  private Class<? extends Publisher>[] boundary;
  
  /**
   * @effects 
   *   initialises this with the arguments.
   * @param name a unique name
   * @param root  the aggregate root object
   * @param boundary  the classes that define the member objects of the aggregate
   */
  public Aggregate(String name, T root
      ,Class<? extends Publisher>[] boundary) 
      throws NotPossibleException {
    this.name = name;
    this.root = root;
    this.boundary = boundary;
    this.members = new ArrayList<>();
    this.constraints = new ArrayList<>();
    
    this.dsm = new DSM(DODMToolkit.createMemoryBasedDODMConfig(name));
  }

  /**
   * @effects 
   *    return {@link #name} 
   */
  public String getName() {
    return name;
  }
  
  /**
   * @modifies this
   * @effects 
   *   add <code>cons</code> to this 
   */
  public void addConstraint(Constraint cons) {
    constraints.add(cons);
  }
  
  /**
   * @effects 
   *   return {@link #constraints} 
   */
  public Collection<Constraint> getConstraints() {
    return constraints;
  }
  
  /**
   * @effects 
   *   return {@link #root} 
   */
  public T getRoot() {
    return root;
  }
  
  // TODO: how to ensure this rule: 
  // references to nonroot objects can also be available to outside objects 
  // (through root) but only transiently (within a transaction or operation call) 
  
  /**
   * Delete operation must deletes all aggregate objects.
   * 
   * @effects delete all objects in aggregate and other resources used by this
   * 
   */
  public void delete() {
    root = null;
    members.clear();
    members = null;
    if (constraints != null) {
      constraints.clear();
      constraints = null;
    }
    dsm = null;
    boundary = null;
  }
  
  /**
   * @modifies this
   * @effects 
   *  add to this m and recursively any the objects that are linked directly and indirectly 
   *  to m that are within {@link #boundary}
   */
  @Deprecated
  public void addMember(Object m) {
    members.add(m);
    
    Class<?> mcls = m.getClass();
    dsm.registerClasses(mcls);
    
    // find associated members within the boundary and add them
    Map<DAttr, DAssoc> assocMap = dsm.getAssociations(mcls, 
        (DAssoc assoc, Object... args) -> {
          boolean accepted;
          Class assocCls = assoc.associate().type();
          if (assocCls.equals(root.getClass())) {
            // points back to root: skip
            accepted = false;
          } else {
            // to other classes: is it part of the boundary?
            accepted = CollectionToolkit.isInArray(assocCls, boundary);
          }
          
          return accepted;
        }
    /*new Filter<DAssoc>() {
      @Override
      public boolean check(DAssoc assoc, Object... args) {
        boolean inBoundary = CollectionToolkit.isInArray(assoc.associate().type(), boundary);
        return inBoundary;
      }
    }*/
    );
    
    if (assocMap != null) {
      assocMap.forEach((attrib, assoc) -> {
        Object associate = dsm.getAttributeValue(mcls, m, attrib);
        if (associate != null) {
          // recursively add associate to aggregate
          if (associate instanceof Collection) {
            Collection col = (Collection) associate;
            col.forEach(o -> addMember(o));
          } else {
            addMember(associate);
          }
        }
      });
    }
  }
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  @Deprecated
  public void removeMember(Object member) {
    boolean removed = members.remove(member);
    if (removed) {
      // TODO ? remove member's associates and unregister member's class from dsm
    }
  }

  /**
   * @requires root != null
   * @effects 
   *  retrieve nonroot objects through association traversal from the root 
   *  
   */
  @Deprecated
  public Collection getMembers() {
    return members;
  }

  /**
   * @effects 
   *  if {@link #boundary} contains all the classes specified in <code>bound</code>
   *    return true
   *  else
   *    return false
   */
  public boolean contains(Class<?>[] bound) {
    boolean match;
    for (Class<?> c : bound) {
      match = false;
      for (Class<?> cls : boundary) {
        if (cls.equals(c)) {
          match = true;
          break;
        }
      }
      
      if (!match) return false;
    }
    
    return true;
  }


  /**
   * @effects 
   *  return {@link #dsm} 
   */
  public DSM getDsm() {
    return dsm;
  }
  
  @Override
  public String toString() {
    return "Aggregate (" + name + ", " + root + ")";
  }

}