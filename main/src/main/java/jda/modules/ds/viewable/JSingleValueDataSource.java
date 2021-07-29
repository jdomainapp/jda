package jda.modules.ds.viewable;

import java.util.Iterator;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.oql.def.Query;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JBoundedComponent;

/**
 * @overview 
 *  A {@see JDataSource} bounded to a the domain objects of a domain class that provides 
 *  access to the bounded attribute of a <b>single</b> object of this class. 
 *  
 *  <p>The object is identified by invoking the method {@link #iterator()}, which supports the execution 
 *  of an object {@link Query}. This query must return <b>at most one</b> domain object.
 *  
 *  <p>This data source is used to serve bounded data fields ({@link JBindableField}) that are known
 *  to access a single object of the domain class. Typical examples of these fields are those that 
 *  provide some form of statistics (e.g. count) for a report.
 *  
 *  <p>To access the object, a bounded data field invokes the method {@link JDataSource#getBoundedValues(DAttr)} to obtain
 *  the display values. It then retrieves the first non-empty item from these to use as the bounded value to 
 *  display to the user. It then retrieves the actual value object by invoking 
 *  the method {@link JDataSource#reverseLookUp(DAttr, Object)} passing in the display value 
 *  as argument (or invoking directly the method {@link #getObject()}). 
 *      
 * @author dmle
 * 
 * @version 2.7.2
 */
public abstract class JSingleValueDataSource extends JDataSource {
  
  private Object singleton;
  private DAttr boundedAttrib;
  
  public JSingleValueDataSource(DODMBasic dodm, Class domainClass) {
    super(null, dodm, domainClass);
  }
  
  @Override
  public Iterator iterator() throws NotPossibleException {
    singleton = loadObject();
    
    // create an Iterator over the object (to conform to the standard API) 
    // which provides access to the object
    Iterator iterator = null;
    if (singleton != null) {
      iterator = new Iterator() {
        boolean canNext = true;
        
        @Override
        public boolean hasNext() {
          // can move next once only
          if (canNext) {
            canNext = false;
            return true;
          } else
            return false;
        }

        @Override
        public Object next() {
          return singleton;
        }

        @Override
        public void remove() {}
      };
      
      isEmpty = false;
    } else {
      isEmpty = true;
    }
    
    return iterator;
  }
  
  @Override
  public boolean isEmpty() {
    return (singleton != null);
  }
  
  @Override
  public Object reverseLookUp(DAttr boundAttribute,
      Object boundedVal) throws NotPossibleException, NotFoundException {
    if (singleton != null)
      return singleton;
    else 
      // not found
      throw new NotFoundException(NotFoundException.Code.OBJECT_NOT_FOUND, 
          "Không tìm thấy đối tượng {0}<{1}>", getDomainClass().getSimpleName(), boundAttribute.name() +"="+boundedVal);
  }
  
  /**
   * @effects 
   *  load the object from the data source.
   *  if succeeded
   *    return the object
   *  else
   *    return <tt>null</tt>
   *    
   *  <p>Throws NotPossibleException if failed to load the object.
   */
  protected abstract Object loadObject() throws NotPossibleException;
  
  /**
   * @effects 
   *  return the domain attribute of the data field that is bounded to this OR
   *  throw NotPossibleException if this is not the case.
   * @version 2.7.4
   */
  protected DAttr getBoundedAttribute() throws NotPossibleException {
    if (boundedAttrib == null) {
      Iterator<JBoundedComponent> bcomps = getBoundedComponents();
      if (bcomps != null) { 
        // just one bounded component
        JBoundedComponent bcomp = bcomps.next();
        boundedAttrib = bcomp.getDomainConstraint(); //bcomp.getBoundConstraint();
      }

      if (boundedAttrib == null) {
        throw new NotPossibleException(NotPossibleException.Code.NO_BOUND_ATTRIBUTES, new Object[] {""});
      }
    }
    
    return boundedAttrib;
  }
  
  /**
   * @effects 
   *  if the single object of this is available
   *    return the object
   *  else
   *    return <tt>null</tt>
   */
  public Object getObject() {
    return singleton;
  }
  
  @Override
  public void clearBuffer() {
    super.clearBuffer();
    singleton=null;
  }
}
