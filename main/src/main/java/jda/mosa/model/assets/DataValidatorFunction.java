package jda.mosa.model.assets;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.warning.DomainWarning;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;

/**
 * @overview
 *  A function interface used for defining data validator functions that may be attached to the 
 *  definition of a domain class.
 *  
 * @author dmle
 *
 * @version 3.3 
 */
public abstract class DataValidatorFunction<T> {
  private DODMBasic dodm;
  private Class<T> domainClass;
  
  
  public DataValidatorFunction(DODMBasic dodm, Class<T> domainClass) {
    this.dodm = dodm;
    this.domainClass = domainClass;
  }

  /**
   * 
   * @effects 
   *  evaluate the data validation rules encapsulated by this against values in <tt>valsMap</tt>
   *  that were input new (i.e. <tt>currentObj = null</tt>) or against <tt>currentObj</tt>
   *  
   *  Throws ConstraintViolationException if validation failed because some of the rule(s) are violated , 
   *  DomainWarning if validation succeeds but a warning should be displayed to the user concerning some part of it  
   */
  public abstract void eval(T currentObj, Map<DAttr, Object> valsMap) throws ConstraintViolationException, DomainWarning;
  
  /**
   * @effects 
   *  return this.dodm
   */
  protected DODMBasic getDodm() {
    return dodm;
  }


  /**
   * @effects 
   *  return this.domainClass
   */
  protected Class<T> getDomainClass() {
    return domainClass;
  }

  /**
   * @requires 
   *  valsMap != null
   * @effects 
   *  if exists entry <tt>(a,v)</tt> in <tt>valsMap</tt> such that <tt>a.name().equals(attribName) /\ v.type = valType</tt>
   *    return <tt>v</tt>
   *  else
   *    return null
   */
  protected <V> V getAttributeVal(Map<DAttr, Object> valsMap, String attribName, Class<V> valType) {
    if (valsMap == null) return null;
    
    Set<Entry<DAttr, Object>> entrySet = valsMap.entrySet();
    
    Object val;
    for (Entry<DAttr, Object> e : entrySet) {
      if (e.getKey().name().equals(attribName)) {
        // found
        val = e.getValue();
        if (val != null && valType.isInstance(val)) {
          return (V) val;
        } else {
          return null;
        }
      }
    }
    
    // not found
    return null;
  }

}
