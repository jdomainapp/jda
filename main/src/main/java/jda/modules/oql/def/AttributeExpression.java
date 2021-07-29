package jda.modules.oql.def;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview
 *  An {@link Expression} that compare two attributes of two domain classes. It is used in rare cases where
 *  we want to create a pseudo-join expression between two classes that involve non-id attributes.   
 * 
 * @example <pre>
 *  Person:id, name, boss
 *  Department:id, manager
 *  -> AttributeExpression(Person, Person.boss, Op.EQ, Department, Department.manager)  
 *  </pre>
 *  
 * @author dmle
 *
 * @version 3.3
 */
public class AttributeExpression extends Expression {

  private Class class1;
  private DAttr attrib1;
  private Class class2;
  private DAttr attrib2;
  
  public AttributeExpression(Class c1, DAttr attrib1, Op op, Class c2, DAttr attrib2) {
    super(c1.getSimpleName()+"."+attrib1.name(), Op.EQ, c2.getSimpleName()+"."+attrib2.name());
    
    this.class1 = c1;
    this.attrib1 = attrib1;
    this.class2 = c2;
    this.attrib2 = attrib2;
  }

  /**
  /**
   * @requires 
   *  exp != null
   * @effects
   *  creates and return an instance of <tt>AttributeExpression<tt> such that all references in <tt>exp</tt> to the class <tt>oldCls</tt> are replaced by <tt>newCls</tt>;  
   *  the rest of <tt>exp</tt> is copied over to the created instance.
   *     
   * @version 3.3
   */
  public static AttributeExpression createInstance(Class oldCls, Class newCls, AttributeExpression exp) {
    Class c1 = exp.getClass1();
    Class c2 = exp.getClass2();
    Class cls1 = (oldCls.equals(c1)) ? newCls : c1;
    Class cls2 = (oldCls.equals(c2)) ? newCls : c2;
    
    return new AttributeExpression(cls1, exp.getAttrib1(), exp.getOperator(), cls2, exp.getAttrib2());
  }
  
  public Class getClass1() {
    return class1;
  }

  public void setClass1(Class c1) {
    this.class1 = c1;
  }

  public DAttr getAttrib1() {
    return attrib1;
  }

  public void setAttrib1(DAttr attrib1) {
    this.attrib1 = attrib1;
  }

  public Class getClass2() {
    return class2;
  }

  public void setClass2(Class c2) {
    this.class2 = c2;
  }

  public DAttr getAttrib2() {
    return attrib2;
  }

  public void setAttrib2(DAttr attrib2) {
    this.attrib2 = attrib2;
  }

  /**
   * @effects 
   *  if <tt>cls</tt> is the same as one of the domain classes of this
   *    return true
   *  else
   *    return false 
   * @version 3.3
   */
  public boolean isAppliedTo(Class cls) {
    return cls.equals(class1) || cls.equals(class2);
  }
  
  @Override
  public String toString() {
    return "AttributeExpression (" + class1.getSimpleName() + "." + attrib1.name() + getOperator() + class2.getSimpleName() + "."
        + attrib2.name() + ")";
  }
}
