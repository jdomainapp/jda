package jda.util.events;

import java.util.ArrayList;
import java.util.List;

import jda.modules.mccl.conceptmodel.controller.LAName;

/**
 * @overview 
 * The source object for the {@link #ChangeEvent} of a domain class.
 * 
 * <p>
 * It is also a sub-class of {@see ArrayList} so that it can hold the changed
 * objects. When the object pool of a domain class is changed (e.g. by new
 * objects being added to it), the changed objects are added to the
 * <code>ChangeEventSource</code> object of the class.
 * 
 * <p>
 * The changed objects (more precisely their references) are effectively
 * passed along via its enclosing <code>ChangeEvent</code> object to all the
 * listeners. These listeners can access the changed objects via the standard
 * <code>List</code> interface.
 */
public class ChangeEventSource<T> extends ArrayList<T> {
  private Class<T> c;
  private LAName act;
  
  private Object data;
  
  public ChangeEventSource(Class<T> c) {
    super();
    this.c = c;
  }
  
  public ChangeEventSource() {
    this(null);
  }

  /**
   * @effects 
   *  return the domain class of this
   * @version 2.7.3
   */
  public Class<T> getDomainClass() {
    return c;
  }
  
  /**
   * @effects 
   *  return the objects contained in this
   */
  public List<T> getObjects() {
    return this;
  }

  public void setChangeAction(LAName act) {
    this.act = act;
  }

  public boolean isAddNew() {
    return act == LAName.New;
  }
  
  public boolean isDelete() {
    boolean eq = (act == LAName.Delete); 
    return eq;
  }

  public boolean isUpdate() {
    boolean eq = (act == LAName.Update); 
    return eq;
  }
  
  public LAName getAct() {
    return act;
  }
  
  /**
   * @effects   
   *  return this.data (the additional data associated to the objects contained in this); 
   *  or return null if no data is specified
   */
  public Object getEventData() {
    return data;
  }
  
  /**
   * @effects   
   *  sets this.data  = data (the additional data associated to the objects contained in this)
   */
  public void setEventData(Object data) {
    this.data = data;
  }
  
  @Override
  public void clear() {
    super.clear();
    this.data = null;
    act = null;
  }
}
