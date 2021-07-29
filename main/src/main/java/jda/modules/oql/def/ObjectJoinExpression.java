package jda.modules.oql.def;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  A sub-type of {@link ObjectExpression} that is used for specifying domain-object-based join expression, 
 *  i.e an object expression whose value is another object expression.
 *  
 *  @example
 *  An object exression that holds true for all Student objects whose <tt>sclass</tt> attribute value 
 *  is equal to a second object expression object that holds true for all <tt>SClass</tt> objects
 *  whose names contains the string <tt>2014</tt> 
 *  (i.e. all students who are in the <tt>"x2014y"</tt> classes).
 *  
 *  <pre>
 *    Class sclassCls = SClass.class;
 *    DomainConstraint clsName = schema.getDomainConstraint(sclassCls, "name");
 *    Op op = Op.Match;
 *    ObjectExpression exp2 = new ObjectExpression(sclassCls, clsName, op, "2014");
 *    
 *    Class studentCls = Student.class;
 *    DomainConstraint clsAttrib = schema.getDomainConstraint(studentCls, "sclass");
 *    Op op = Op.EQ;
 *    <b>ObjectExpression exp1 = new ObjectExpression(studentCls, clsAttrib, op, exp2);</b>
 *    </pre>
 *  
 * @author dmle
 *
 */
public class ObjectJoinExpression extends ObjectExpression {
  
  /**
   * @effects   
   *  initialises this as a join expression over the source <tt>domainClass</tt> with 
   *  the join operator <tt>op</tt>, whose join's target is <tt>target</tt> 
   */
  public ObjectJoinExpression(Class domainClass, 
      DAttr attrib, 
      Op op, 
      ObjectExpression target
      ) {
    super(domainClass, attrib, op, target);
  }
  
  /**
   * @effects 
   *  return the target <tt>ObjectExpression</tt> that is joined to <tt>this</tt> 
   */
  public ObjectExpression getTargetExpression() {
    return (ObjectExpression) getVal();
  }
  
  /**
   * @effects 
   *  return the domain class of {@link #getTargetExpression()}
   * @version 3.3
   */
  private Class getTargetDomainClass() {
    return getTargetExpression().getDomainClass();
  }


  /**
   * This is a producer operation.
   * 
   * @requires 
   *  exp != null
   * @effect 
   *  creates a return a new <tt>ObjectJoinExpression</tt> over the domain class <tt>c</tt>
   *  whose domain attribute, operator and target expression are the same as <tt>exp</tt>'s
   */
  public static ObjectJoinExpression createInstance(Class c, ObjectJoinExpression exp) {
    return new ObjectJoinExpression(c, 
        exp.getDomainAttribute(), 
        exp.getOperator(), 
        exp.getTargetExpression());
  }

  /**
   * @effects 
   *  if this contains a join between the two input classes (in the specified order)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.3
   */
  public boolean containsJoin(Class c1, Class c2) {
    return c1.equals(getDomainClass()) && c2.equals(getTargetDomainClass());
  }
  
//  @Override
//  public String toString() {
//    return "ObjectJoinExpression (" + getDomainClass().getSimpleName() + ": " 
//        + super.toStringBasic(true) + ")";
//  }  
//  
//  @Override
//  public String toString(boolean withNulls) {
//    return "ObjectJoinExpression (" + getDomainClass().getSimpleName() + ": " 
//        + super.toStringBasic(withNulls) + ")";
//  } 
  
}
