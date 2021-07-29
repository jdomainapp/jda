package jda.mosa.controller.assets.helper;

import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.exceptions.warning.DomainWarning;
import jda.modules.dcsl.syntax.DAttr;
import jda.mosa.view.assets.datafields.JBindableField;

/**
 * @overview An interface used by view components and data controllers to validate data values concerning 
 * attributes of a given domain class (represented by the type <tt>T</tt>) that are entered by the user.  
 * 
 * @author dmle
 */
public interface DataValidator<T> {
  
  /**
   * @effects 
   *  if <tt>d</tt> is not null
   *    if <code>value</code> satisfies <code>d</code> 
   *      returns the validated object
   *      else throws <code>ConstraintViolationException</code>
   *  else
   *    return value
   */
  public Object validateDomainValue(DAttr d, Object value)
      throws ConstraintViolationException;
  
  /**
   * Compared to {@link #validateDomainValue(DAttr, Object)}, this method is used in the later stage of object-creation
   * process to validate a combination of the input values. The former is used while the user is inputing value 
   * for each individual domain attribute.  
   * 
   * @effects 
   *  Validate values in <tt>valsMap</tt> that were either input new (i.e. <tt>currentObj=null</tt>) or input against a <tt>currentObj</tt> 
   *  using more complex data validation rules. 
   *  
   *  <p>Throws ConstraintViolationException if any of these rules is violated.
   *  Throws {@link DomainWarning} if domain-specific rules are violated.
   *   
   * @version 
   * - 3.3: created
   * - 3.4: added parameter currentObj
   */
  public void validateDomainValues(T currentObj, Map<DAttr, Object> valsMap) throws ConstraintViolationException, DomainWarning;
  
//  /**
//   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
//   * of a domain class for the <b>delete</b> action.
//   * 
//   * @requires cls != null /\ attributeCons != null /\ toObj != null
//   * @effects check that the cardinality constraint on the many side of
//   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
//   *          and that is linked to <tt>toObj</tt> is not violated
//   *          if a link is deleted from it.
//   *          
//   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
//   *          NotFoundException if the association can not be found; 
//   *          NotPossibleException if fails to check the constraint. 
//   */
//  public void validateCardinalityConstraintOnDelete(
//      Class cls, 
//      DomainConstraint attribute, 
//      Object toObj, int currentLinkCount) throws ConstraintViolationException, 
//        NotFoundException, NotPossibleException;
  
  /**
   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
   * of a domain class for the <b>create new</b> action. 
   * 
   * @requires cls != null /\ attributeCons != null /\ toObj /\ null /\ objectAction in
   *           {LAName.Create,LAName.Delete}
   * @effects check that the cardinality constraint on the many side of
   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
   *          and that is linked to <tt>toObj</tt> is not violated
   *          if a new link is created for it.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   */
  public void validateBoundedValueOnCreate(JBindableField bdf, Object val) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException;
  
  /**
   * This method is used to validate the cardinality constraint of <b>a specific link attribute</b>
   * of a domain class for the <b>create new</b> action. 
   * 
   * @requires cls != null /\ attributeCons != null /\ toObj /\ null /\ objectAction in
   *           {LAName.Create,LAName.Delete}
   * @effects check that the cardinality constraint on the many side of
   *          the association that is defined for <tt>attribute</tt> of the domain class <tt>cls</tt>
   *          and that is linked to <tt>toObj</tt> is not violated
   *          if a link is deleted from it.
   *          
   *          <p>Throws ConstraintViolationException if constraint is not satisfied; 
   *          NotFoundException if the association can not be found; 
   *          NotPossibleException if fails to check the constraint. 
   */
  public void validateBoundedValueOnDelete(JBindableField bdf, Object val) throws ConstraintViolationException, 
        NotFoundException, NotPossibleException;  
//  /**
//   * This method is used to validate the cardinality constraint of <b>all associations
//   * attached to some attributes</b> of a domain class. 
//   *  
//   * @requires 
//   *  cls != null /\ cls is a domain class /\ 
//   *  dobj != null /\ dobj is a valid domain object of cls /\ 
//   *  vals != null
//   * @effects 
//   *  check that the cardinality constraints of any 1:M associations between 
//   *  other domain classes and <tt>cls</tt> are not violated if an  
//   *  association link is added to them because of <tt>newVals</tt> and so are  
//   *  those between the same domain classes w.r.t <tt>dobj</tt>. If so
//   *  do nothing, else throw ConstraintViolationException. 
//   */
//  public void validateCardinalityConstraintsOnUpdate(
//      Class cls, 
//      Object dobj, 
//      LinkedHashMap<DomainConstraint,Object> newVals) throws ConstraintViolationException, NotFoundException, 
//      NotPossibleException;
}
