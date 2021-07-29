package jda.modules.oql.def;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.QueryToolKit;
import jda.mosa.model.Oid;

/**
 * @overview 
 *  A flexible query that supports a range of expressions. When these expressions are added, 
 *  the query's structure is automatically updated. 
 * 
 * @example
 * <pre>
 *     SQL query: "find all childs registered to the class type whose id is 2"
 *     
 *     select t0.* 
 *      from Child t0, Enrolment t1, RegistrationInfo t2
 *      where t0.id=t1.child_id and t1.registrationinfo_id=t2.id
 *            and t2.classprefered_id=2
 *            and t0.id not in (select child_id from cclassentry);
 *  </pre>
 * 
 * @author dmle
 * @version 
 *  3.0: a basic implementation without using the query structure
 * 
 */
public class FlexiQuery extends Query {

  private DSMBasic dsm;
  /**the domain class among {@link #baseDomainClasses} whose objects are to be retrieved by this query
   * , e.g. for SQL query, this is the class whose attributes appear in the SELECT clause
   * */
  private Class srcDomainClass;

  /**
   * List of domain attributes of {@link #srcDomainClass} whose values are to be retrieved
   * by this query,
   * <br>e.g. for SQL query, these are the attributes that appear in the SELECT clause
   * 
   * <p>if not specified (i.e. <tt>null</tt>) then all attributes (e.g. '*" for SQL) are assumed.
   */
  private List<DAttr> srcAttributes;

  /**
   * List of functions defined for {@link #srcAttributes} 
   * 
   */
  private Map<DAttr,Function> srcAttributeFuncs;

  /**
   * the base domain classes are the ones that form the base structure of the query, 
   * e.g. for SQL these are the classes that appear in the FROM clause
   */
  private List<Class> baseDomainClasses;
  
  public FlexiQuery(DSMBasic dsm, Class srcDomainClass) {
    super();
    this.dsm = dsm;
    this.srcDomainClass = srcDomainClass;
    
    baseDomainClasses = new ArrayList();
    baseDomainClasses.add(srcDomainClass);
  }

  /**
   * @requires 
   *  attrib is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add <tt>attrib</tt> to the list of source attributes of {@link #srcDomainClass}, whose 
   *  values are to be retrieved by this query  
   *  
   *  <p>throws NotFoundException if attribute with name <tt>attribName</tt> is not found
   */
  public DAttr addDomainAttribute(String attribName) throws NotFoundException {
    if (srcAttributes == null) {
      srcAttributes = new ArrayList();
    }
    
    DAttr attrib = dsm.getDomainConstraint(srcDomainClass, attribName);
    
    if (!srcAttributes.contains(attrib))
      srcAttributes.add(attrib);
    
    return attrib;
  }
  
  /**
   * @requires 
   *  attrib is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add <tt>attrib</tt> to the list of source attributes of {@link #srcDomainClass}, whose 
   *  values are to be retrieved by this query  
   *  
   * @version 3.3  
   */
  public void addDomainAttribute(DAttr attrib) throws NotFoundException {
    if (srcAttributes == null) {
      srcAttributes = new ArrayList();
    }
    
    if (!srcAttributes.contains(attrib))
      srcAttributes.add(attrib);
  }
  
  /**
   * @requires 
   *  attrib is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add <tt>attrib</tt> to the list of source attributes of {@link #srcDomainClass}, whose 
   *  values are to be retrieved by this query; and 
   *  add to this a mapping <tt>(attrib,func)</tt>
   *  
   *  <p>throws NotFoundException if attribute with name <tt>attribName</tt> is not found
   * @version 3.1
   */
  public void addDomainAttributeWithFunction(String attribName, Function func) throws NotFoundException {
    DAttr attrib = this.addDomainAttribute(attribName);
    
    if (srcAttributeFuncs == null)
      srcAttributeFuncs = new HashMap();
    
    srcAttributeFuncs.put(attrib, func);
  }
  
  /**
   * @requires 
   * <tt>c1, c2</tt> are valid domain classes /\ 
   * <tt>attrib1, attrib2</tt> are names of domain attributes of <tt>c1, c2</tt> (resp.) 
   * 
   * @effects <pre>
   *  create and add to this an {@link AttributeExpression} <tt>c1.attrib1 op c2.attrib2</tt> 
   *  (add c1, c2 to {@link #baseDomainClasses} if not already)   
   *  <br>return the expression
   *  <pre>
   *  
   *  <p>throws NotFoundException if attrib1, attrib2 are not valid domain attributes 
   * @version 3.3
   */
  public AttributeExpression addAttributeExpression(Class c1, String attrib1, Op op, Class c2, String attrib2) throws NotFoundException {
    AttributeExpression exp = QueryToolKit.createAttributeExpression(dsm, c1, attrib1, op, c2, attrib2);
    add(exp);
    
    // add c1, c2 to base (if not already)
    if (!baseDomainClasses.contains(c1)) {
      baseDomainClasses.add(c1);
    }
    
    if (!baseDomainClasses.contains(c2)) {
      baseDomainClasses.add(c2);
    }
    
    return exp;
  }
  
  /**
   * @effects 
   *  create and add to this an object join expression for the <tt>joinClasses</tt> based on their associations whose names 
   *  are <tt>assocNames</tt> and, if <tt>attribName != null</tt> then, is constrained by <tt>a op val</tt> 
   *  where a is an attribute of the <b>last</b> class in <tt>joinClasses</tt> whose name is <tt>attribName</tt>; 
   *  add <tt>joinClasses</tt> to {@link #baseDomainClasses} (if not yet done so) 
   * 
   * <p>throws IllegalArgumentException if arguments are not valid
   *  
   * @requires 
   *  <tt>assocNames</tt> are names of valid associations of <tt>joinClasses</tt>   
   */
  public ObjectJoinExpression addJoinExpressionWithValueConstraint(Class[] joinClasses, String[] assocNames,
      String attribName, Op op, Object val) throws IllegalArgumentException {
      ObjectJoinExpression exp = QueryToolKit.createJoinExpressionWithValueConstraint(dsm, joinClasses, assocNames, 
          attribName, op, val);
      add(exp);
      
      // add join classes to the base (if not already)
      for (Class c : joinClasses) {
        if (!baseDomainClasses.contains(c)) {
          baseDomainClasses.add(c);
        }
      }
      
      return exp;
  }

  /**
   * @effects 
   *  create an object join expression for the specified arguments and add it to this
   *  add <tt>joinClasses</tt> to {@link #baseDomainClasses} (if not yet done so) 
   * 
   * <p>throws IllegalArgumentException if arguments are not valid 
   */
  public ObjectJoinExpression addJoinExpression(Class[] joinClasses, String[] assocNames) throws IllegalArgumentException {
    ObjectJoinExpression exp = QueryToolKit.createJoinExpression(dsm, joinClasses,
        assocNames);
    add(exp);
      
    // add join classes to the base (if not already)
    for (Class c : joinClasses) {
      if (!baseDomainClasses.contains(c)) {
        baseDomainClasses.add(c);
      }
    }
    
    return exp;
  }
  

  /**
   * This method differs from {@link #addJoinExpression(Class[], String[]) in that 
   * it supports join on non-id attributes.
   *  
   * <p>This method is <b>especially used</b> for cases where associations are not defined for join classes (e.g. in the 
   * case of reflexive associations), and the caller <b>knows exactly</b> which attributes that form the joins.
   * 
   * @effects 
   *  create and add to this a {@link ObjectJoinExpression} between <tt>classes</tt> using the attributes whose names are
   *  <tt>joinAttribNames</tt>.
   *  
   * @requires 
   *  attributes of <tt>classes</tt> whose names are <tt>joinAttribNames</tt> are valid join attributes 
   * @version 3.3 
   */
  public ObjectJoinExpression addJoinOnAttributes(Class[] classes, String[] joinAttribNames) {
    ObjectJoinExpression exp = QueryToolKit.createJoinOnAttributes(dsm, classes, joinAttribNames);
    
    add(exp);
      
    // add join classes to the base (if not already)
    for (Class c : classes) {
      if (!baseDomainClasses.contains(c)) {
        baseDomainClasses.add(c);
      }
    }
    
    return exp;    
  }
  
  /**
   * This method differs from {@link #addJoinExpressionWithValueConstraint(Class[], String[], String, Op, Object) in that 
   * it supports join on non-id attributes.
   *  
   * <p>This method is <b>especially used</b> for cases where associations are not defined for join classes (e.g. in the 
   * case of reflexive associations), and the caller <b>knows exactly</b> which attributes that form the joins.
   * 
   * @effects 
   *  create and add to this a {@link ObjectJoinExpression} between <tt>classes</tt> using the attributes whose names are
   *  <tt>joinAttribNames</tt> and if <tt>attribName != null</tt> then is constrained by <tt>a op val</tt> where a is an attribute of the <b>last</b> 
   *  class in <tt>classes</tt> whose name is <tt>attribName</tt>
   *  
   * @requires 
   *  attributes of <tt>classes</tt> whose names are <tt>joinAttribNames</tt> are valid join attributes 
   *  
   * @version 3.3 
   */
  public ObjectJoinExpression addJoinOnAttributesWithValueConstraint(Class[] classes,
      String[] joinAttribNames, String attribName, Op op, Object attribVal) {
    ObjectJoinExpression exp = QueryToolKit.createJoinOnAttributesWithValueConstraint(dsm, classes,
        joinAttribNames,
        // attribFunctor
        null, 
        attribName, op, attribVal);
    
    add(exp);
      
    // add join classes to the base (if not already)
    for (Class c : classes) {
      if (!baseDomainClasses.contains(c)) {
        baseDomainClasses.add(c);
      }
    }
    
    return exp;
  }
  
  /**
   * @requires 
   *  srcCls is in {@link #baseDomainClasses} /\ srcCls.srcAttrib and targetCls.targetAttrib are valid
   *  
   * @effects 
   *  create a NotIn expression for <tt>srcCls.srcAttrib</tt> whose target query is 
   *  <tt>targetCls.targetAttrib</tt> and add to this. 
   *  
   *  <p>throws IllegalArgumentException if arguments are not valid. 
   *  
   * @example
   *  <pre>
   *  srcCls = Student
   *  srcAttrib = Student.id
   *  targetCls = Enrolment
   *  targetAttrib = Enrolment.student
   *  
   *  SQL:
   *  -> result = "Student.id not in (select student from Enrolment)" 
   *  
   *  </pre>
   */
  public ObjectExpression addNotInSimpleExpression(Class srcCls, String srcAttrib,
      Class targetCls, String targetAttrib) throws IllegalArgumentException {
    // validate: src class must be in the base
    if (!baseDomainClasses.contains(srcCls)) {
      throw new IllegalArgumentException(this.getClass().getSimpleName()+".addNotInExpression: class not registered in the query: " + srcCls.getSimpleName());
    }
    
    DAttr srcAttribObj = dsm.getDomainConstraint(srcCls, srcAttrib);
    DAttr targetAttribObj = dsm.getDomainConstraint(targetCls, targetAttrib);
    
    // create a Query that selects the values of targetAttrib from targetCls
    ObjectAttributeExpression targetExpression = new ObjectAttributeExpression(targetCls, targetAttribObj);
    
    // create a not in Query for srcCls.srcAttrib whose value is the above query
    ObjectExpression exp = new ObjectExpression(srcCls, srcAttribObj, Op.NOIN, targetExpression); 
    
    add(exp);
    
    return exp;
  }
  

  /**
   * @requires 
   *  srcCls is in {@link #baseDomainClasses} /\ srcCls.srcAttrib is valid
   *  
   * @effects 
   *  create a NotIn expression for <tt>srcCls.srcAttrib</tt> whose target query is 
   *  <tt>nestedQuery</tt> and add to this. 
   *  
   *  <p>throws IllegalArgumentException if arguments are not valid. 
   */
  public ObjectExpression addNotInExpression(Class srcCls, String srcAttrib,
      Query nestedQuery) {
    // validate: src class must be in the base
    if (!baseDomainClasses.contains(srcCls)) {
      throw new IllegalArgumentException(this.getClass().getSimpleName()+".addNotInExpression: class not registered in the query: " + srcCls.getSimpleName());
    }
  
    DAttr srcAttribObj = dsm.getDomainConstraint(srcCls, srcAttrib);
    
    // create a not in Query for srcCls.srcAttrib whose value is the nested query
    ObjectExpression exp = new ObjectExpression(srcCls, srcAttribObj, Op.NOIN, nestedQuery); 
    
    add(exp);
    
    return exp;
  }

  /**
   * @requires 
   *  attribName is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add to this an {@link ObjectExpression} over <tt>(attribName,op,val)</tt>
   *  <p>throws NotFoundException if attribute with name <tt>attribName</tt> is not found
   */
  public ObjectExpression addConstraintExpression(String attribName, Op op, Object val) throws NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(srcDomainClass, attribName);

    ObjectExpression exp = new ObjectExpression(srcDomainClass, attrib, op, val);
    
    add(exp);
    
    return exp;
  }

  /**
   * @requires 
   *  attrib is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add to this an {@link ObjectExpression} over <tt>(attrib,op,val)</tt>
   */
  public ObjectExpression addConstraintExpression(DAttr attrib, Op op, Object val) {
    ObjectExpression exp = new ObjectExpression(srcDomainClass, attrib, op, val);
    
    add(exp);
    
    return exp;
  }
  
  /**
   * Use this method to add a constraint expression for one of the base classes (not just {@link #srcDomainClass}). 
   * 
   * @requires 
   *  domainCls is one of the base classes in this /\ 
   *  attribName is a valid attribute of domainCls
   *  
   * @effects 
   *  add to this an {@link ObjectExpression} over <tt>(domainCls, attribName,op,val)</tt>
   *  <p>throws NotFoundException if attribute with name <tt>attribName</tt> is not found
   * @version 3.1
   */
  public ObjectExpression addConstraintExpression(Class domainCls, 
      String attribName, Op op, Object val) throws NotFoundException {
    if (!baseDomainClasses.contains(domainCls))
      throw new IllegalArgumentException("Not a base class of this query: " + domainCls);
    
    DAttr attrib = dsm.getDomainConstraint(domainCls, attribName);

    ObjectExpression exp = new ObjectExpression(domainCls, attrib, op, val);
    
    add(exp);
    
    return exp;
  }
  
  
  /**
   * Use this method to add a constraint expression for one of the base classes (not just {@link #srcDomainClass}). 
   * @requires 
   *  attribName is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add to this an {@link ObjectExpression} over <tt>(attribFunc,attribName,op,val)</tt>
   *  <p>throws NotFoundException if attribute with name <tt>attribName</tt> is not found
   */
  public ObjectExpression addConstraintExpression(AttribFunctor attribFunc, String attribName, Op op, Object val) throws NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(srcDomainClass, attribName);
    
    ObjectExpression exp = new ObjectExpression(srcDomainClass, attribFunc, attrib, op, val);
    
    add(exp);
    
    return exp;
  }

  /**
   * Use this method to add a constraint expression for one of the base classes (not just {@link #srcDomainClass}). 
   * @requires 
   *  attrib is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add to this an {@link ObjectExpression} over <tt>(attribFunc,attrib,op,val)</tt>
   */  
  public ObjectExpression addConstraintExpression(AttribFunctor attribFunc, DAttr attrib, Op op, Object val) {
    ObjectExpression exp = new ObjectExpression(srcDomainClass, attribFunc, attrib, op, val);
    
    add(exp);
    
    return exp;
  }
  
  /**
   * @requires
   *  domainCls is one of the base classes in this /\ 
   *  attribName is a valid attribute of {@link #srcDomainClass}
   *  
   * @effects 
   *  add to this an {@link ObjectExpression} over <tt>(attribFunc,attribName,op,val)</tt>
   *  <p>throws NotFoundException if attribute with name <tt>attribName</tt> is not found
   */
  public ObjectExpression addConstraintExpression(Class domainCls, 
      AttribFunctor attribFunc, String attribName, Op op, Object val) throws NotFoundException {
    if (!baseDomainClasses.contains(domainCls))
      throw new IllegalArgumentException("Not a base class of this query: " + domainCls);
    
    DAttr attrib = dsm.getDomainConstraint(domainCls, attribName);
    
    ObjectExpression exp = new ObjectExpression(domainCls, attribFunc, attrib, op, val);
    
    add(exp);
    
    return exp;
  }
  
  public Class getSrcDomainClass() {
    return srcDomainClass;
  }

  /**
   * @effects 
   *  if exists {@link DAttr} (domain attribute) <tt>i</tt>th of this
   *    return it
   *  else
   *    throws IllegalArgumentException
   *  
   * @version 3.1
   */
  public DAttr getDomainAttribute(int index) throws IllegalArgumentException {
    if (srcAttributes == null || index < 0 || index >= srcAttributes.size()) {
      throw new IllegalArgumentException(this.getClass().getSimpleName()+".getDomainAttribute: invalid attribute index " + index);
    }
    
    return srcAttributes.get(index);
  }


  /**
   * @effects 
   *  if this has domain attributes 
   *    return the number of them
   *  else 
   *    return 0
   * @version 3.3
   */
  public int getDomainAttributeCount() {
    if (srcAttributes != null)
      return srcAttributes.size();
    else
      return 0;
  }
  
  /**
   * @effects 
   *  if exists {@link Function} defined for the domain attribute <tt>attrib</tt> of this
   *    return it
   *  else
   *    return <tt>null</tt>
   * @version 3.1
   */
  public Function getDomainAttributeFunction(DAttr attrib) {
    if (srcAttributeFuncs == null) {
      return null;
    } else {
      return srcAttributeFuncs.get(attrib);
    }
  }

  /**
   * @effects 
   *
   * @version
   */
  public Expression addIdExpression(String aId, Op in, Oid[] idArr) {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @effects 
   *  if this contains a {@link ObjectJoinExpression} between the two input classes (in the specified order)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.3
   */
  public boolean containsJoinExpression(final Class c1, final Class c2) {
    Iterator<Expression> terms = terms();
    Expression exp;
    while (terms.hasNext()) {
      exp = terms.next();
      if (exp instanceof ObjectJoinExpression && ((ObjectJoinExpression) exp).containsJoin(c1, c2)) {
        // contains
        return true;
      }
    }
    
    // not contains
    return false;
  }
}
