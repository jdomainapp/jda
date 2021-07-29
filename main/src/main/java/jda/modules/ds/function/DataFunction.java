package jda.modules.ds.function;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.AttrRef;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.Query;

/**
 * @overview
 *  Represents a data function that performs a query over the data source to return data.
 *  
 * @author dmle
 *
 */
public abstract class DataFunction {

  private DODMBasic dodm;
  private DSMBasic dsm;
  private DOMBasic dom;
  
  private static Map<String,Method> funcTable = new HashMap<>();
  
  public DataFunction(DODMBasic dodm) {
    this.dodm = dodm;
    this.dsm = dodm.getDsm();
    this.dom = dodm.getDom();
    
    // register this into the function table
    Class funcCls = this.getClass();
    if (!isRegistered(funcCls))
      registerFunction(funcCls);
  }

  public DODMBasic getDodm() {
    return dodm;
  }

  public DSMBasic getDsm() {
    return dsm;
  }

  public DOMBasic getDom() {
    return dom;
  }
  
  
  /**
   * @effects 
   *  perform function <tt>count</tt> objects of a given domain class <tt>c</tt> that satisfy the 
   *  requirement defined in the input query <tt>q</tt>
   */
  protected int count(Class c, Query q) {
    return dom.loadObjectCount(c, q);
  }

  /**
   * @effects 
   *  create and return an instance of <tt>funcCls</tt> from argument <tt>dodm</tt>; 
   *  throws NotPossibleException if failed to do so
   */
  public static DataFunction createInstance(
      Class<? extends DataFunction> funcCls, DODMBasic dodm) throws NotPossibleException {
    try {
      // invoke the constructor to create object 
      DataFunction instance = funcCls.getConstructor(DODMBasic.class).newInstance(dodm);
      
      return instance;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          new Object[] {funcCls.getSimpleName(), dodm});
    }
  }

  private boolean isRegistered(Class funcCls) {
    return dsm.isRegistered(funcCls);
  }

  /**
   * 
   * Sub-types must use this method to register its functions. 
   * 
   * @requires 
   *  {@link #isRegistered(Class)}=<tt>false</tt> with <tt>funcCls</tt> 
   * @effects 
   *  register all the function-typed methods of <tt>funcCls</tt> to this.
   *  
   *  <br>Throws NotPossibleException if <tt>funcCls</tt> is not well defined.
   */
  private void registerFunction(Class funcCls) throws NotPossibleException {
    Collection<Method> methods = dsm.findMetadataAnnotatedMethods(funcCls, DOpt.Type.DataFunctionImpl); 
    
    if (methods != null) {
      AttrRef attribRef;
      for (Method m : methods) {
        attribRef = m.getAnnotation(DSMBasic.MEMBER_REF);
        
        if (attribRef != null) {
          // found a method
          funcTable.put(attribRef.value(), m);
        } else {
          throw new NotPossibleException(NotPossibleException.Code.DATA_FUNCTION_NOT_WELL_DEFINED, 
              new Object[] {funcCls.getSimpleName(), m.getName()});
        }
      }
    }
  }
  
  /**
   * @effects 
   *  find a method of <tt>this</tt> that implements a data function for the domain attribute <tt>attrib</tt>,
   *  perform the method and return the result 
   *  (if method is not defined with a return type then return <tt>null</tt>).
   *  
   *  
   *  <br>Throws NotFoundException if method is not found; 
   *  NotPossibleException if failed to perform the method. 
   */
  public Object perform(String attrib) throws NotPossibleException {
    Method m = funcTable.get(attrib);
    
    if (m != null)
      return dsm.doMethod(this.getClass(), m, this);
    else
      throw new NotFoundException(NotFoundException.Code.DATA_FUNCTION_NOT_FOUND, new Object[] {attrib});
  }
}
