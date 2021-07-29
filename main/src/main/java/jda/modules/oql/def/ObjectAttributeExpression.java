package jda.modules.oql.def;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;

/**
 * @overview
 *  A sub-type of {@link ObjectExpression} that specifically supports a specification for  
 *  all domain objects of a domain class, projected over a given domain attribute.
 *  
 *  <p>Thus, this expression implies in a bigger set of objects than the normal {@link ObjectExpression}
 *  because it does not constrain the values that the attribute may take.
 *  
 * @author dmle
 *
 */
public class ObjectAttributeExpression extends ObjectExpression {

  // whether or not to use this.attrib as part of the join
  private boolean isUsingAttributeForJoin;

  /**
   * 
   * @effects 
   *  initialises this with {@link #isUsingAttributeForJoin} = false
   */
  public ObjectAttributeExpression(Class domainClass, DAttr attrib) {
    /*v3.1: force the use of an operator, even if it is not actually used for data source query
    super(domainClass, attrib);
    */
    //v3.3: super(domainClass, attrib, Op.Nil, null);
    this(domainClass, attrib, false);
  }

  /**
   * @effects
   *  initialises this 
   *   
   * @version 3.3
   */
  public ObjectAttributeExpression(Class domainClass, DAttr attrib, final boolean isUsingAttributeForJoin) {
    super(domainClass, attrib, Op.Nil, null);
    
    this.isUsingAttributeForJoin = isUsingAttributeForJoin;
  }
  
  /**
   * This only returns true if this is part of an ObjectJoinExpression that uses attributes rather than association to form join.
   * The default is false.
   * 
   * @effects 
   *  if <tt>this.attrib</tt> is used in the join
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.3
   */
  public boolean isUsingAttributeForJoin() {
    return isUsingAttributeForJoin;
  }

}
