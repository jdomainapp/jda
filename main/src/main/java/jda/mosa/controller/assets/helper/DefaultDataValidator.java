package jda.mosa.controller.assets.helper;

import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.warning.DomainWarning;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.DDataValidator;
import jda.mosa.model.assets.DataValidatorFunction;
import jda.mosa.view.assets.datafields.JBindableField;

/**
 * 
 * @overview 
 *  Represents a data validator for a domain class (specified by {@link DefaultDataValidator#domainClass}).  
 *  
 * @author dmle
 *
 * @version 
 * - 3.4: added type parameter <tt>T</tt>
 */
public class DefaultDataValidator<T> implements DataValidator<T> {
  private DODMBasic dodm;
  private Class<T> domainClass;
  
  /**
   * the {@link DataValidatorFunction} that may be defined for {@link #domainClass}
   */
  private DataValidatorFunction<T> dataValidatorFunc; // v3.3
  private Boolean hasDataValidatorFunc;
  
  public DefaultDataValidator(DODMBasic schema, Class<T> domainClass) {
    this.dodm = schema;
    this.domainClass = domainClass;
  }
  
  @Override
  public Object validateDomainValue(DAttr d, Object value)
      throws ConstraintViolationException {
    return dodm.getDom().validateDomainValue(
        domainClass,    // v2.7.4
        d, value);
  }
  
  /**
   * @effects
   * if exists DataValidatorFunction f defined for {@link #domainClass}
   *   invoke f.eval(valsMap)
   * else
   *  do nothing
   */
  /* (non-Javadoc)
   * @see domainapp.basics.controller.helper.DataValidator#validateDomainValues(java.util.Map)
   */
  @Override
  public void validateDomainValues(T currentObj, Map<DAttr, Object> valsMap)
      throws ConstraintViolationException, NotPossibleException, DomainWarning {
    DataValidatorFunction f = getDataValidatorFunction();
    
    if (f != null) {
      f.eval(currentObj, valsMap);
    }
  }

  /**
   * @effects 
   *  if exists DataValidatorFunction f defined for {@link #domainClass}
   *    return it
   *  else
   *    return null
   *    
   *  <p>throws NotPossibleException if failed to create an instance of the function class.
   *  
   * @version 3.3
   */
  public DataValidatorFunction<T> getDataValidatorFunction() throws NotPossibleException {
    if (dataValidatorFunc == null && hasDataValidatorFunc == null) {
      DDataValidator dclassDef = dodm.getDsm().getDomainClassAnnotation(domainClass, DDataValidator.class);
      if (dclassDef != null) {
        Class<DataValidatorFunction<T>> dataValidatorFuncCls = (Class<DataValidatorFunction<T>>) dclassDef.type();
        
        dataValidatorFunc = DSMBasic.newInstance(dataValidatorFuncCls, new Object[] {dodm, domainClass});
      }
      
      hasDataValidatorFunc = dataValidatorFunc != null;
    }
    
    return dataValidatorFunc;
  }

  @Override
  public void validateBoundedValueOnCreate(JBindableField bdf, Object val)
      throws ConstraintViolationException, NotFoundException,
      NotPossibleException {
    if (domainClass != null) {
      DAttr attrib = bdf.getDomainConstraint();
      int currentLinkCount = -1;
      dodm.getDom().validateCardinalityConstraintOnCreate(domainClass, attrib, val, currentLinkCount);
    }
  }

  @Override
  public void validateBoundedValueOnDelete(JBindableField bdf, Object val)
      throws ConstraintViolationException, NotFoundException,
      NotPossibleException {
    if (domainClass != null) {
      DAttr attrib = bdf.getDomainConstraint();
      int currentLinkCount = -1;
      dodm.getDom().validateCardinalityConstraintOnDelete(domainClass, attrib, val, currentLinkCount);
    }    
  }
  
  @Override
  public String toString() {
    return String.format("DefaultDataValidator(%s)%n", (domainClass != null) ? domainClass.getSimpleName() : null);
  }
}
