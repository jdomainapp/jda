package jda.modules.oql.def;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview 
 *  A sub-type of {@link ObjectJoinExpression} that supports join on two attributes of the two classes involved. 
 *  
 *  It differs from {@link ObjectJoinExpression} in that it requires the specification of the attributes on both sides of the join.
 *    
 * @author dmle
 *
 * @version 3.3
 */
public class ObjectJoinOnAttributeExpression extends ObjectJoinExpression {
  
  private static final char JOIN = '\u22c8';
  
  // this must be equal to target.getDomainClass
  private Class c2;
  private DAttr attrib2;

  /**
   * @requires 
   *  <tt>c2 = target.getDomainClass()</tt>
   *  
   * @effects   
   *  initialises this as a join expression over the domain attributes <tt>attrib1, attrib2</tt> of two 
   *  domain classes <tt>c1, c2</tt> with 
   *  the join operator <tt>op</tt>, and whose join's target is <tt>target</tt> 
   */
  public ObjectJoinOnAttributeExpression(Class c1,
      DAttr attrib1, 
      Class c2,
      DAttr attrib2,
      Op op, 
      ObjectExpression target
      ) throws IllegalArgumentException {
    super(c1, attrib1, op, target);
    
    if (!c2.equals(target.getDomainClass())) {
      throw new IllegalArgumentException(getClassName()+".init: the 2nd join class ("+c2.getSimpleName()+") is NOT the same as domain class ("+target.getDomainClass().getSimpleName()+") of the target expression");
    }
    
    this.c2 = c2;
    this.attrib2 = attrib2;
  }

  public Class getC2() {
    return c2;
  }

  public void setC2(Class c2) {
    this.c2 = c2;
  }

  public DAttr getAttrib2() {
    return attrib2;
  }

  public void setAttrib2(DAttr attrib2) {
    this.attrib2 = attrib2;
  }

  @Override
  protected String getPrefixString() {
    return getClassName()+ "<" + getDomainClass().getSimpleName() + " " + JOIN + " " + c2.getSimpleName()+" >"; 
  }

  @Override
  protected String getVarString() {
    return getDomainAttribute().name() + " " + JOIN + " " + attrib2.name();
  }

//  /**
//   * This is a producer operation.
//   * 
//   * @requires 
//   *  exp != null
//   * @effect 
//   *  creates a return a new <tt>ObjectJoinExpression</tt> over the domain class <tt>c</tt>
//   *  whose domain attribute, operator and target expression are the same as <tt>exp</tt>'s
//   */
//  public static ObjectJoinOnAttributeExpression createInstance(Class c, ObjectJoinOnAttributeExpression exp) {
//    return new ObjectJoinOnAttributeExpression(c, 
//        exp.getDomainAttribute(), 
//        exp.getOperator(), 
//        exp.getTargetExpression());
//  }

  
}
