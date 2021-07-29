package jda.modules.oql.def;

import java.util.Map;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.function.AttribFunctor;

/**
 * @overview 
 *  A sub-type of {@link Expression} that is used for specifying domain-object-based expression.
 *  
 *  @example
 *  An object exression that holds true for all Student objects whose <tt>sclass</tt> attribute value 
 *  is equal to the object <tt>Sclass(1,"1c11")</tt> (i.e. all students who are in the "1c11" class).
 *  
 *  <pre>
 *    Class domainCls = Student.class;
 *    DomainConstraint attrib = schema.getDomainConstraint(domainCls, "sclass");
 *    Op op = Op.EQ;
 *    SClass val = new SClass(1,"1c11");
 *    ObjectExpression exp = new ObjectExpression(domainCls, attrib, op, val);
 *    </pre>
 *  
 * @author dmle
 *
 */
public class ObjectExpression extends Expression {
  
  private DAttr domainAttrib;
  
  private Class domainClass;
  
  private AttribFunctor attribFunc;

// v3.1: force the use of an op parameter
//  /**
//   * @effects 
//   *  initialise this to be a simple expression <tt>(domainClass, attrib)</tt>
//   *  
//   * @version 3.0
//   */
//  protected ObjectExpression(Class domainClass, DomainConstraint attrib) {
//    this(domainClass, attrib, null, null);
//  }
  
  public ObjectExpression(Class domainClass, 
      DAttr attrib, 
      Op op, 
      Object val
      ) {
    this(domainClass, null, attrib, op, val);
  }
  
  /**
   * @effects 
   *  initialises this as an object expression over the attribute defined in 
   *  <tt>attribFunc</tt> that belongs to the domain class <tt>domainClass</tt>
   *  using operator <tt>op</tt> and value <tt>val</tt>
   */
  public ObjectExpression(Class domainClass, 
      AttribFunctor attribFunc, 
      DAttr attrib, 
      Op op, Object val) {
    super(attrib.name(), op, val);
    
    this.domainClass = domainClass;
    this.domainAttrib = attrib;
    this.attribFunc = attribFunc;
  }

  public DAttr getDomainAttribute() {
    return domainAttrib;
  }
  
  public AttribFunctor getAttributeFunctor() {
    return attribFunc;
  }
  
  public Class getDomainClass() {
    return domainClass;
  }

  @Override
  protected String getPrefixString() {
    return getClassName()+ "<" + domainClass.getSimpleName() +">"; 
  }
  
  @Override
  protected String getVarString() {
    if (attribFunc != null) {
      return attribFunc.function()+"("+attribFunc.attrib()+")";
    } else {
      return domainAttrib.name();
    }
  }

  @Override
  protected String getVarString(Map<String, String> queryDict) {
    String attrib = (attribFunc != null) ? attribFunc.attrib() : domainAttrib.name();
    String label = (queryDict != null) ? queryDict.get(attrib) : null;
    
    if (label == null) {
      // no label, use default 
      return getVarString();
    } else {
      // use the specified label 
      return label;
    }
  }

  /**
   * @requires 
   *  exp != null
   * @effects
   *  creates and return an instance of <tt>ObjectExpression<tt> over the domain class <tt>c</tt> 
   *  whose domain attribute, operator and value are the same as <tt>exp</tt>'s. 
   */
  public static ObjectExpression createInstance(Class c, ObjectExpression exp) {
    return new ObjectExpression(c, exp.getAttributeFunctor(), exp.getDomainAttribute(), exp.getOperator(), exp.getVal());
  }
  
//  protected String toStringBasic(boolean withNulls) {
//    return super.toString(withNulls);
//  }
}
