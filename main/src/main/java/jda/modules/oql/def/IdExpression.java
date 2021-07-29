package jda.modules.oql.def;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.mosa.model.Oid;

/**
 * @overview A sub-type of {@link Expression} that is used for specifying
 *           domain-object-based expression whose value is an <tt>Oid</tt>.
 *           
 *           <p>Thus, this sub-type differs from <tt>ObjectExpression</tt> only in the 
 *           data-type of the value expression.
 * 
 *            <p>The expression operator is always {@link Op#EQ}.
 *            
 * @example An Id exression that holds true for all <tt>Enrolment</tt> objects whose
 *          <tt>student</tt> attribute value "refers" to the <tt>Student</tt> object identified by 
 *          <tt>Oid(Student,(id="S2014"))</tt> (i.e. all enrolment records of the Student whose <tt>id</tt> is 
 *          "S2014").
 *          
 * <pre>
 * Class domainCls = Student.class;
 * DomainConstraint attrib = schema.getDomainConstraint(domainCls, &quot;sclass&quot;);
 * Op op = Op.EQ;
 * SClass val = new SClass(1, &quot;1c11&quot;);
 * ObjectExpression exp = new ObjectExpression(domainCls, attrib, op, val);
 * </pre>
 */
public class IdExpression extends Expression {
  private DAttr domainAttrib;
  private Class domainClass;
  
  public IdExpression(Class domainClass, 
      DAttr attrib, 
      Oid refOid
      ) {
    super(attrib.name(), Op.EQ, refOid);
    
    this.domainClass = domainClass;
    this.domainAttrib = attrib;
  }
  
  public DAttr getDomainAttribute() {
    return domainAttrib;
  }
  
  public Class getDomainClass() {
    return domainClass;
  }

  @Override
  public Oid getVal() {
    return (Oid) super.getVal();
  }
  
  @Override
  public String toString() {
    return "IdExpression (" + domainClass + ": " 
        + super.toString() + ")";
  }  
}
