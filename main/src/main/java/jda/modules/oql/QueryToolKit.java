package jda.modules.oql;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.expression.Op;
import jda.modules.common.types.Null;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Selectx;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.function.AttribFunctor;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dcsl.syntax.query.AttribExp;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.DODMToolkit;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.AttributeExpression;
import jda.modules.oql.def.FlexiQuery;
import jda.modules.oql.def.IdExpression;
import jda.modules.oql.def.ObjectAttributeExpression;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.modules.oql.def.ObjectJoinOnAttributeExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;

/**
 * @overview
 *  A tool kit class specifically for manipulating {@link Query} objects. 
 *  
 * @author dmle
 *
 */
public class QueryToolKit {
  
  private QueryToolKit() {}
  
  /**
   * @requires 
   *  <tt>c</tt> is a valid domain class registered in <tt>schema</tt> /\ 
   *  <tt>joinDef</tt> must define a valid class join
   *  
   * @effects 
   * if <tt>joinDef</tt> is <tt>null</tt>
   *  return null
   * else if <tt>joinDef</tt> is not a valid class join
   *  throws IllegalArgumentException
   * else
   *  create and return an <tt>ObjectJoinExpression</tt> for the join definition 
   *  <tt>joinDef</tt> with the target attribute value <tt>val</tt>.
   */
  public static ObjectJoinExpression createJoinExpressionWithValueConstraint(DSMBasic dsm, 
      Selectx joinDef, 
      Object val) throws IllegalArgumentException {
    if (joinDef == null)
      return null;
    
    Class[] joinClasses = joinDef.classJoin();
    String[] assocNames = joinDef.joinAssocs();
    AttribFunctor attribFunc = joinDef.attribFunc();
    String attribName = attribFunc.attrib();
    Op op = attribFunc.operator();

    return createJoinExpressionWithValueConstraint(dsm, joinClasses, assocNames, attribFunc, attribName, op, val);
  }

  /**
   * This method works the same as method {@link #createJoinExpressionWithValueConstraint(DODMBasic, Selectx, Object)}. 
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class registered in <tt>schema</tt> /\ 
   *  <tt>joinDef</tt> must define a valid class join
   *  
   * @effects 
   * if <tt>joinDef</tt> is <tt>null</tt>
   *  return null
   * else if <tt>joinDef</tt> is not a valid class join
   *  throws IllegalArgumentException
   * else
   *  create and return an <tt>ObjectJoinExpression</tt> for the join definition 
   *  <tt>joinDef</tt> with the target attribute value <tt>val</tt>.
   */
  public static ObjectJoinExpression createJoinExpressionWithValueConstraint(DSMBasic dsm, 
      Class[] joinClasses,
      String[] assocNames,
      String attribName,
      Op op,
      Object val) throws IllegalArgumentException {
    return createJoinExpressionWithValueConstraint(dsm, joinClasses, assocNames,
        // attribFunc
        null, 
        attribName, op, val);
  }
  
  /**
   * This method works the same as method {@link #createJoinExpressionWithValueConstraint(DODMBasic, Selectx, Object)}. 
   * 
   * @requires 
   *  <tt>c</tt> is a valid domain class registered in <tt>schema</tt> /\ 
   *  <tt>joinDef</tt> must define a valid class join
   *  
   * @effects 
   * if <tt>joinDef</tt> is <tt>null</tt>
   *  return null
   * else if <tt>joinDef</tt> is not a valid class join
   *  throws IllegalArgumentException
   * else
   *  create and return an <tt>ObjectJoinExpression</tt> for the join definition 
   *  <tt>joinDef</tt> with the target attribute value <tt>val</tt>.
   */
  public static ObjectJoinExpression createJoinExpression(DSMBasic dsm, 
      Class[] joinClasses,
      String[] assocNames) throws IllegalArgumentException {
    return createJoinExpressionWithValueConstraint(dsm, joinClasses, assocNames,
        // attribFunc
        null, 
        // attribName
        null, 
        // op
        null, 
        // attribValue
        null);
  }
  
  /**
   * @requires 
   *  <tt>c</tt> is a valid domain class registered in <tt>schema</tt> /\ 
   *  <tt>joinDef</tt> must define a valid class join /\ 
   *  <tt>attribName != null => attribName</tt> is name of a valid domain attribute of the last class in <tt>joinClasses</tt>
   *  
   * @effects 
   * if <tt>joinDef</tt> is <tt>null</tt>
   *  return null
   * else if <tt>joinDef</tt> is not a valid class join
   *  throws IllegalArgumentException
   * else
   *  create and return an <tt>ObjectJoinExpression</tt> for the join definition 
   *  <tt>joinDef</tt> and if (attribName != null) is constrained by <tt>a op val</tt> where a is an attribute of the <b>last</b> 
   *  class in <tt>joinClasses</tt> whose name is <tt>attribName</tt>
   */
  public static ObjectJoinExpression createJoinExpressionWithValueConstraint(DSMBasic dsm, 
      Class[] joinClasses,
      String[] assocNames,
      AttribFunctor attribFunc,
      String attribName,
      Op op,
      Object val) throws IllegalArgumentException {
    int numClasses = joinClasses.length;
    int numAssocs = assocNames.length;
    // validate joinDef
    if (numClasses <= 1)
      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of classes ("+numClasses+") (expected: >= 2)");
    else if (numAssocs != numClasses-1)
      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of associations ("+numAssocs+") (expected: "+(numClasses-1)+")");
    
    // process the class list backward, creating a component join expression for each new class
    Class c0, c1;
    ObjectJoinExpression exp1 = null;
    Function func = (attribFunc != null) ? attribFunc.function() : Function.nil;

    c0 = joinClasses[numClasses-1];
    
    ObjectExpression exp0; 
    
    if (attribName != null) {
      // attribute name is specified
      // create the last expression first: it is an object expression over the referenced attribute
      DAttr attrib = dsm.getDomainConstraint(c0, attribName);
      if (func.isNil()) {
        // just the attribute, no function
        exp0 = new ObjectExpression(c0, null, attrib, op, val);
      } else {
        // attribute and function
        exp0 = new ObjectExpression(c0, attribFunc, attrib, op, val);
      }
    } else {
      // no attrib name for the last expression: use the pair (class,attribute)
      exp0 = null;
    }
    
    // create the chain of join expressions
    DAttr jattrib, attrib1, attrib0;
    Class jc; // the class that contains jattrib
    String assocName;
    DAssoc assoc;
    Tuple2<DAttr,DAssoc> assocTuple;
    Tuple2<DAttr,DAssoc> targetAssoc; 
    
    //TODO: support other join operator
    final Op joinOp = Op.EQ;
    
    for (int i = numClasses-2; i >= 0; i--) {
      c1 = joinClasses[i];
      assocName = assocNames[i];
      /*
       *  join attribute a is chosen either from c1 or c0 as follows:
       *    (1) 1-1 association: (c1:a,1,c0:b,1)
       *        c1:a if c1:a is serialisable OR c0:b if c1:a is not serialisable and c0:b is serialisable 
       *  OR  
       *    (2) 1-M association: (c1:a,M,c0:b,1) OR (c0:a,M,c1:b,1) 
       *      if (c1:a,M,c0:b,1) then c1:a is selected 
       *      if (c0:a,M,c1:b,1) then c0:a is selected
       */
      assocTuple = dsm.getAssociation(c1, assocName, c0);
      
      if (assocTuple == null)
        // invalid association name
        throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid association name "+assocName+" between "+c1.getSimpleName()+" and "+c0.getSimpleName());
      
      attrib1 = assocTuple.getFirst();
      assoc = assocTuple.getSecond();
      
      if (exp0 == null) {
        // the previous expression needs to be initialised to a normal expression 
        // over (c0,attrib0)
        targetAssoc = dsm.getTargetAssociation(assoc);
        if (targetAssoc == null) {
          // invalid
          throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: no target association defined in "+c0.getSimpleName()+" w.r.t "+c1.getSimpleName()+" (expected: at least one)");
        }
        exp0 = new ObjectAttributeExpression(c0, targetAssoc.getFirst());
      }
      
      if (assoc.ascType().equals(AssocType.One2One)) {
        // first case
        /*v3.0: check serialisable
        jattrib = attrib1; 
        */
        if (attrib1.serialisable()) {
          jattrib = attrib1;
          jc = c1;
        } else {
          // use attrib0
          //attrib0 = schema.getTargetAssociation(assoc).getFirst();
          //jattrib = attrib0;
          //jc = c0;
          // TODO: not yet supported (this case results in a more complex join query over the data source)
          throw new NotImplementedException(
              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
              ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is 1-1 but "+c1.getSimpleName()+"."+attrib1.name()+" is not serialisable");
          
        }
      } else if (assoc.ascType().equals(AssocType.One2Many)) {
        // second case: join attribute is from c1 or c0 depending on which one is the 1 end 
        if (assoc.endType().equals(AssocEndType.Many)) {
          // c1:a
          targetAssoc = dsm.getTargetAssociation(assoc);
          if (targetAssoc == null) {
            // invalid
            throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: no target association defined in "+c0.getSimpleName()+" w.r.t "+c1.getSimpleName()+" (expected: at least one)");
          }
          jattrib = assocTuple.getFirst();
          jc = c1;
        } else {
          // c0:a
          // TODO: not yet supported (this case results in a more complex join query over the data source)
          throw new NotImplementedException(
              NotImplementedException.Code.FEATURE_NOT_SUPPORTED, new Object[] {
              ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is 1-M (expected: 1-1 or M-1)"});
        }
      } else {
        // invalid
        throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is not valid (Details: "+assoc+")\n (expected: at least one 1-1 or M-1)");        
      }
     
      /*v3.0: support c1 or c0
      exp1 = new ObjectJoinExpression(c1, jattrib, joinOp, exp0);
      */
      exp1 = new ObjectJoinExpression(jc, jattrib, joinOp, exp0);
      
      c0 = c1;
      exp0 = exp1;
    }
    
    // return the last join expression
    return exp1;
  }
  

  /**
   * This method differs from {@link #createJoinExpression(Class[], String[]) in that 
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
  public static ObjectJoinExpression createJoinOnAttributes(DSMBasic dsm,
      Class[] classes, String[] joinAttribNames) {
    return createJoinOnAttributesWithValueConstraint(dsm, classes, joinAttribNames,
        // attribFunc
        null, 
        // attribName
        null, 
        // op
        null, 
        // attribValue
        null);
  }

  /**
   * This method differs from {@link #createJoinExpressionWithValueConstraint(DSMBasic, Class[], String[], String, Op, Object) in that 
   * it supports join on non-id attributes.
   *  
   * <p>This method is <b>especially used</b> for cases where associations are not defined for join classes (e.g. in the 
   * case of reflexive associations), and the caller <b>knows exactly</b> which attributes that form the joins.
   * 
   * @requires 
   *  <tt>attribName != null => attribName</tt> is name of a a valid domain attribute of the last class in <tt>classes</tt>
   * @effects 
   *  create and add to this a {@link ObjectJoinExpression} between <tt>classes</tt> using the attributes whose names are
   *  <tt>joinAttribNames</tt> and is constrained by <tt>a op val</tt> where a is an attribute of the <b>last</b> 
   *  class in <tt>classes</tt> whose name is <tt>attribName</tt>
   *  
   * @requires 
   *  attributes of <tt>classes</tt> whose names are <tt>joinAttribNames</tt> are valid join attributes 
   *  
   * @version 3.3
   */
  public static ObjectJoinExpression createJoinOnAttributesWithValueConstraint(
      DSMBasic dsm, Class[] classes, String[] joinAttribNames,
      AttribFunctor attribFunc,
      String attribName, Op op, Object val) {
    int numClasses = classes.length;
    int numJoinAttribs = joinAttribNames.length;
    // validate joinDef
    if (numClasses <= 1)
      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of classes ("+numClasses+") (expected: >= 2)");
    else if (numJoinAttribs != numClasses)
      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of join attributes ("+numJoinAttribs+") (expected: "+(numClasses)+")");
    
    // process the class list backward, creating a component join expression for each new class
    Class c0, c1;
    ObjectJoinExpression exp1 = null;
    Function func = (attribFunc != null) ? attribFunc.function() : Function.nil;

    c0 = classes[numClasses-1];
    
    ObjectExpression valExp; 
    
    if (attribName != null) {
      // attribute name is specified
      // create the last expression first: it is an object expression over the referenced attribute
      DAttr attrib = dsm.getDomainConstraint(c0, attribName);
      if (func.isNil()) {
        // just the attribute, no function
        valExp = new ObjectExpression(c0, null, attrib, op, val);
      } else {
        // attribute and function
        valExp = new ObjectExpression(c0, attribFunc, attrib, op, val);
      }
    } else {
      // no attrib name for the last expression: use the pair (class,attribute)
      valExp = null;
    }
    
    // create the chain of join expressions
    DAttr attrib1, attrib0;
    String jattribName1, jattribName0;
    
    //TODO: support other join operator
    final Op joinOp = Op.EQ;
    
    final boolean isUsingAttributeForJoin = true;
    
    ObjectExpression exp0 = null;
    
    int maxIndex = numClasses-2; 
    for (int i = maxIndex; i >= 0; i--) {
      c1 = classes[i];
      jattribName1 = joinAttribNames[i];
      attrib1 = dsm.getDomainConstraint(c1, jattribName1);
      
      if (i == maxIndex) { 
        // the last expression: either an ObjectJoinExpression or an ObjectJoinOnAttributeExpression
        jattribName0 = joinAttribNames[numJoinAttribs-1];
        attrib0 = dsm.getDomainConstraint(c0, jattribName0);
        
        if (valExp == null) {
          // join on attributes without value expression
          exp0 = new ObjectAttributeExpression(c0, attrib0, isUsingAttributeForJoin);
          exp1 = new ObjectJoinExpression(c1, attrib1, joinOp, exp0); 
        } else {
          // join on attributes with a value expression
          exp1 = new ObjectJoinOnAttributeExpression(c1, attrib1, c0, attrib0, joinOp, valExp);
        }
      } else { // the subsequently backward expressions: normal join expression
        exp1 = new ObjectJoinExpression(c1, attrib1, joinOp, exp0);        
      }
      
      c0 = c1;
      exp0 = exp1;
    }
    
    // return the last join expression
    return exp1;
  }
  
  // v3.0: old
//  /**
//   * @requires 
//   *  <tt>c</tt> is a valid domain class registered in <tt>schema</tt> /\ 
//   *  <tt>joinDef</tt> must define a valid class join
//   *  
//   * @effects 
//   * if <tt>joinDef</tt> is <tt>null</tt>
//   *  return null
//   * else if <tt>joinDef</tt> is not a valid class join
//   *  throws IllegalArgumentException
//   * else
//   *  create and return an <tt>ObjectJoinExpression</tt> for the join definition 
//   *  <tt>joinDef</tt> with the target attribute value <tt>val</tt>.
//   */
//  public static ObjectJoinExpression createJoinExpression(DSMBasic schema, 
//      Selectx joinDef, 
//      Object val) throws IllegalArgumentException {
//    if (joinDef == null)
//      return null;
//    
//    Class[] joinClasses = joinDef.classJoin();
//    String[] assocNames = joinDef.joinAssocs();
//    AttribFunctor attribFunc = joinDef.attribFunc();
//    int numClasses = joinClasses.length;
//    int numAssocs = assocNames.length;
//    // validate joinDef
//    if (numClasses <= 1)
//      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of classes ("+numClasses+") (expected: >= 2)");
//    else if (numAssocs != numClasses-1)
//      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of associations ("+numAssocs+") (expected: "+(numClasses-1)+")");
//    
//    // process the class list backward, creating a component join expression for each new class
//    Class c0, c1;
//    ObjectJoinExpression exp1 = null;
//
//    // create the last expression first: it is an object expression over the referenced attribute
//    c0 = joinClasses[numClasses-1];
//    DomainConstraint attrib = schema.getDomainConstraint(c0, attribFunc.attrib());
//    Function func = attribFunc.function();
//    Op op = attribFunc.operator();
//    
//    ObjectExpression exp0;
//    if (func.isNil()) {
//      // just the attribute, no function
//      exp0 = new ObjectExpression(c0, null, attrib, op, val);
//    } else {
//      // attribute and function
//      exp0 = new ObjectExpression(c0, attribFunc, attrib, op, val);
//    }
//    
//    // create the chain of join expressions
//    DomainConstraint jattrib, attrib1, attrib0;
//    Class jc; // the class that contains jattrib
//    String assocName;
//    Association assoc;
//    Tuple2<DomainConstraint,Association> assocTuple;
//    Tuple2<DomainConstraint,Association> targetAssoc; 
//    
//    //TODO: support other join operator
//    final Op joinOp = Op.EQ;
//    
//    for (int i = numClasses-2; i >= 0; i--) {
//      c1 = joinClasses[i];
//      assocName = assocNames[i];
//      /*
//       *  join attribute a is chosen either from c1 or c0 as follows:
//       *    (1) 1-1 association: (c1:a,1,c0:b,1)
//       *        c1:a if c1:a is serialisable OR c0:b if c1:a is not serialisable and c0:b is serialisable 
//       *  OR  
//       *    (2) 1-M association: (c1:a,M,c0:b,1) OR (c0:a,M,c1:b,1) 
//       *      if (c1:a,M,c0:b,1) then c1:a is selected 
//       *      if (c0:a,M,c1:b,1) then c0:a is selected
//       */
//      assocTuple = schema.getAssociation(c1, assocName, c0);
//      
//      if (assocTuple == null)
//        // invalid association name
//        throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid association name "+assocName+" between "+c1.getSimpleName()+" and "+c0.getSimpleName());
//      
//      attrib1 = assocTuple.getFirst();
//      assoc = assocTuple.getSecond();
//      
//      if (assoc.type().equals(AssocType.One2One)) {
//        // first case
//        /*v3.0: check serialisable
//        jattrib = attrib1; 
//        */
//        if (attrib1.serialisable()) {
//          jattrib = attrib1;
//          jc = c1;
//        } else {
//          // use attrib0
//          //attrib0 = schema.getTargetAssociation(assoc).getFirst();
//          //jattrib = attrib0;
//          //jc = c0;
//          // TODO: not yet supported (this case results in a more complex join query over the data source)
//          throw new NotImplementedException(
//              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//              ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is 1-1 but "+c1.getSimpleName()+"."+attrib1.name()+" is not serialisable");
//          
//        }
//      } else if (assoc.type().equals(AssocType.One2Many)) {
//        // second case: join attribute is from c1 or c0 depending on which one is the 1 end 
//        if (assoc.endType().equals(AssocEndType.Many)) {
//          // c1:a
//          targetAssoc = schema.getTargetAssociation(assoc);
//          if (targetAssoc == null) {
//            // invalid
//            throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: no target association defined in "+c0.getSimpleName()+" w.r.t "+c1.getSimpleName()+" (expected: at least one)");
//          }
//          jattrib = assocTuple.getFirst();
//          jc = c1;
//        } else {
//          // c0:a
//          // TODO: not yet supported (this case results in a more complex join query over the data source)
//          throw new NotImplementedException(
//              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//              ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is 1-M (expected: 1-1 or M-1)");
//        }
//      } else {
//        // invalid
//        throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is not valid (Details: "+assoc+")\n (expected: at least one 1-1 or M-1)");        
//      }
//     
//      /*v3.0: support c1 or c0
//      exp1 = new ObjectJoinExpression(c1, jattrib, joinOp, exp0);
//      */
//      exp1 = new ObjectJoinExpression(jc, jattrib, joinOp, exp0);
//      
//      c0 = c1;
//      exp0 = exp1;
//    }
//    
//    // return the last join expression
//    return exp1;
//  }  
  // v3.0: old
//  /**
//   * This method works the same as method {@link #createJoinExpression(DODMBasic, Selectx, Object)}. 
//   * 
//   * @requires 
//   *  <tt>c</tt> is a valid domain class registered in <tt>schema</tt> /\ 
//   *  <tt>joinDef</tt> must define a valid class join
//   *  
//   * @effects 
//   * if <tt>joinDef</tt> is <tt>null</tt>
//   *  return null
//   * else if <tt>joinDef</tt> is not a valid class join
//   *  throws IllegalArgumentException
//   * else
//   *  create and return an <tt>ObjectJoinExpression</tt> for the join definition 
//   *  <tt>joinDef</tt> with the target attribute value <tt>val</tt>.
//   */
//  public static ObjectJoinExpression createJoinExpression(DSMBasic dsm, 
//      Class[] joinClasses,
//      String[] assocNames,
//      String attribName,
//      Op op,
//      Object val) throws IllegalArgumentException {
//    int numClasses = joinClasses.length;
//    int numAssocs = assocNames.length;
//    // validate joinDef
//    if (numClasses <= 1)
//      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of classes ("+numClasses+") (expected: >= 2)");
//    else if (numAssocs != numClasses-1)
//      throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid number of associations ("+numAssocs+") (expected: "+(numClasses-1)+")");
//    
//    // process the class list backward, creating a component join expression for each new class
//    Class c0, c1;
//    ObjectJoinExpression exp1 = null;
//
//    // create the last expression first: it is an object expression over the referenced attribute
//    c0 = joinClasses[numClasses-1];
//    DomainConstraint attrib = dsm.getDomainConstraint(c0, attribName);
//    
//    ObjectExpression exp0 = new ObjectExpression(c0, null, attrib, op, val);
//    
//    // create the chain of join expressions
//    DomainConstraint jattrib;
//    String assocName;
//    Association assoc;
//    Tuple2<DomainConstraint,Association> assocTuple;
//    Tuple2<DomainConstraint,Association> targetAssoc; 
//    
//    //TODO: support other join operator
//    final Op joinOp = Op.EQ;
//    
//    for (int i = numClasses-2; i >= 0; i--) {
//      c1 = joinClasses[i];
//      assocName = assocNames[i];
//      /*
//       *  join attribute a is chosen either from c1 or c0 as follows:
//       *    (1) c1:a that realises c1's end of a 1-1 association with c0, i.e. 
//       *        exists attribute c0:b and the association (c1:a,1,c0:b,1)
//       *  OR  
//       *    (2) c1:a (c0:a) that realises c1 (resp. c0)'s end of a M-1 association with c0 (c1), i.e. 
//       *        exists attribute c0(c1):b and the association (c1:a,M,c0:b,1) (resp. (c0:a,M,c1:b,1))
//       *        
//       */
//      assocTuple = dsm.getAssociation(c1, assocName, c0);
//      
//      if (assocTuple == null)
//        // invalid association name
//        throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: invalid association name "+assocName+" between "+c1.getSimpleName()+" and "+c0.getSimpleName());
//      
//      assoc = assocTuple.getSecond();
//      
//      if (assoc.type().equals(AssocType.One2One)) {
//        // first case
//        jattrib = assocTuple.getFirst(); 
//      } else if (assoc.type().equals(AssocType.One2Many)) {
//        // second case: join attribute is from c1 or c0 depending on which one is the 1 end 
//        if (assoc.endType().equals(AssocEndType.Many)) {
//          // c1:a
//          targetAssoc = dsm.getTargetAssociation(assoc);
//          if (targetAssoc == null) {
//            // invalid
//            throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: no target association defined in "+c0.getSimpleName()+" w.r.t "+c1.getSimpleName()+" (expected: at least one)");
//          }
//          jattrib = assocTuple.getFirst();
//        } else {
//          // c0:a
//          // TODO: not yet supported (this case results in a more complex join query over the data source)
//          throw new NotImplementedException(
//              NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
//              ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is 1-M (expected: 1-1 or M-1)");
//        }
//      } else {
//        // invalid
//        throw new IllegalArgumentException(ObjectJoinExpression.class.getSimpleName()+".createJoinExpression: association ("+c1.getSimpleName()+","+c0.getSimpleName()+") is not valid (Details: "+assoc+")\n (expected: at least one 1-1 or M-1)");        
//      }
//     
//      exp1 = new ObjectJoinExpression(c1, jattrib, joinOp, exp0);
//      
//      c0 = c1;
//      exp0 = exp1;
//    }
//    
//    // return the last join expression
//    return exp1;
//  }

  /**
   * @requires 
   *  type != null
   * @effects 
   *  return the default <tt>Expression.Op</tt> that is used for searching values of 
   *  the attribute whose type is <tt>type</tt>
   */
  public static Op getDefaultOperatorFromAttributeType(
      jda.modules.dcsl.syntax.DAttr.Type type) {
    Op op;
    if (type.isDomainType()) {
      op = Op.EQ;
    } else if (type.isString()){
      op = Op.MATCH;
    } else {
      op = Op.EQ;
    }
    
    return op;
  }

  /**
   * 
   * @requires 
   *  op != null /\ value != null
   * @effects
   *  if <tt>type</tt> matches an SQL data type
   *    return a suitable <tt>SQL</tt> value pattern that is used to formulate an SQL query term  
   *    over <tt>val</tt>, which is is a domain value whose type is specified by <tt>type</tt>
   *  else
   *    return <tt>val</tt> (unchanged)
   */
  public static Object getSQLValuePattern(jda.modules.dcsl.syntax.DAttr.Type type, Object val) {
    if (type.isString()) {
      // SQL string pattern
      if (val == null) {
        return "%%";
      } else {
        return "%" + val + "%";
      }
    } 
    // add other cases here
    else {
      return val;
    }
  }

  /**
   * This method is the reverse of {@link #getSQLValuePattern(jda.modules.dcsl.syntax.DAttr.Type, Object)}.
   * 
   * @requires 
   *  valStr != null
   * @effects 
   *  if <tt>valStr</tt> is an SQL value pattern (as generated by {@link #getSQLValuePattern(jda.modules.dcsl.syntax.DAttr.Type, Object)}
   *    return the sub-string of <tt>valStr</tt> that is without the pattern symbols
   *  else
   *    return <tt>valStr</tt> (unchanged) 
   */
  public static String fromSQLValuePattern(String valStr) {
    // prefix
    if (valStr.startsWith("%")) {
      if (valStr.length() > 1)
        valStr = valStr.substring(1);
      else
        valStr = "";
    }
    
    // suffix
    if (valStr.endsWith("%")) {
      if (valStr.length() > 1)
        valStr = valStr.substring(0,valStr.length()-1);
      else
        valStr = "";
    }
    
    return valStr;
  }

  /**
   * @effects 
   *  create a return an {@link ObjectExpression#ObjectExpression(Class, DAttr, Op, Object)}
   *  
   *  <p>throws <code>NotFoundException</code> if attribute with the specified name does not exist
   * @version 3.0
   */
  public static ObjectExpression createObjectExpression(DSMBasic dsm,
      Class c, String attribName, Op op, Object val) throws NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    
    return new ObjectExpression(c, attrib, op, val);
  }
  
  /**
   * @effects 
   *  create a return an {@link ObjectExpression#ObjectExpression(Class, AttribFunctor, DAttr, Op, Object)}
   *  
   *  <p>throws <code>NotFoundException</code> if attribute with the specified name does not exist
   * @version 3.0
   */
  public static ObjectExpression createObjectExpression(DSMBasic dsm,
      Class c, AttribFunctor attribFunc, String attribName, Op op, Object val) throws NotFoundException {
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    
    return new ObjectExpression(c, attribFunc, attrib, op, val);
  }

  /**
   * @effects 
   *  return a simple {@link IdExpression} between an object of <tt>c</tt> and some  
   *  object whose <tt>Oid</tt> is <tt>linkedOid</tt> via an attribute of <tt>c1</tt> whose name eq <tt>c1RefAttribName</tt>
   *  
   *  <p>throws <code>NotFoundException</code> if attribute with the specified name does not exist
   */
  public static IdExpression createIdExpression(DSMBasic dsm,
      Class c1, String c1RefAttribName, Oid linkedOid) throws NotFoundException {
    DAttr c1RefAttrib = dsm.getDomainConstraint(c1, c1RefAttribName);
    return new IdExpression(c1, c1RefAttrib, linkedOid);
  }

  /**
   * @requires
   * <tt>c1, c2</tt> are valid domain classes /\ 
   * <tt>attrib1, attrib2</tt> are names of domain attributes of <tt>c1, c2</tt> (resp.) 
   * 
   * @effects
   *  create and return {@link AttributeExpression} <tt>c1.attrib1 op c2.attrib2</tt> 
   *  
   *  <p>throws NotFoundException if attrib1, attrib2 are not valid domain attributes
   *   
   * @version 3.3
   */
  public static AttributeExpression createAttributeExpression(DSMBasic dsm,
      Class c1, String attrib1, Op op, Class c2, String attrib2) throws NotFoundException {
    DAttr a1 = dsm.getDomainConstraint(c1, attrib1);
    DAttr a2 = dsm.getDomainConstraint(c2, attrib2);
    
    return new AttributeExpression(c1, a1, op, c2, a2);
  }

  /**
   * @requires 
   *  queryDesc is not empty
   * @effects 
   *  create and return a {@link Query} from <tt>queryDesc</tt>
   *  
   *  <p>
   *  throws IllegalArgumentException if <tt>queryDesc</tt> is empty or invalid; 
   *  NotFoundException if <tt>queryDesc.clazz</tt> is not a registered domain class or 
   *  a domain attribute specified in <tt>queryDesc</tt> is not found
   */
  public static Query createQuery(DSMBasic dsm, QueryDef queryDesc) 
      throws IllegalArgumentException, NotFoundException {
    Class cls = queryDesc.clazz();
    AttribExp[] attribExps = queryDesc.exps();
    String[] selector = queryDesc.selector();
    
    if (cls == Null.class || (selector.length == 0 && attribExps.length == 0)) {
      // invalid 
      throw new IllegalArgumentException(QueryToolKit.class.getSimpleName()+".createQuery: invalid query description: " + queryDesc);
    } 
    
    //TODO: improve the following if more complex query description is used
    // for now works only with simple query description
    FlexiQuery query = new FlexiQuery(dsm, cls);
    
    // add selector
    for (String selectAttrib : selector) {
      query.addDomainAttribute(selectAttrib);
    }
    
    // add constraint expressions
    Function func; String attribName; DAttr attrib; Op op; String valStr; Class valType; Object val;
    AttribFunctor functor;
    for (AttribExp exp : attribExps) {
      func = exp.function();
      attribName = exp.attrib();
      attrib = dsm.getDomainConstraint(cls, attribName);
          
      op = exp.op();
      valStr = exp.value();
      valType = exp.valueType();
      if (valType == CommonConstants.NullType) valType = null;
      
      val = DODMToolkit.parseDomainValue(attrib, valType, valStr);
      
      if (!func.isNil()) {
        // attribute expression with function
        functor = createAttributeFunctor(func, attribName, op);
        query.addConstraintExpression(functor, attrib, op, val);
      } else {
        // attribute expression without function
        query.addConstraintExpression(attrib, op, val);
      }
    }
    
    return query;
  }


  /**
   * @effects 
   *  Create and return a <b>search query</b> for objects of <tt>c</tt> that satisfy the criteria <tt>a op v</tt>
   *  for all <tt>a in attribs, op in opts, v in vals</tt>, where <tt>attribs</tt> are attributes of <tt>c</tt> 
   *  whose names are <tt>attribNames</tt> 
   *  
   * @version 3.3
   */
  public static Query createSearchQuery(DSMBasic dsm, Class c, String[] attribNames, Op[] opts, Object[] vals) {
    Query query = new Query();
    DAttr attrib;
    Op op;
    Object val;
    int index = 0;
    for (String attribName : attribNames) {
      attrib = dsm.getDomainConstraint(c, attribName);
      op = opts[index];
      val = vals[index];
      query.add(new ObjectExpression(c, attrib, op, val));
      
      index++;
    }
    
    return query;
  }
  

  /**
   * A special form of {@link #createSearchQuery(DSMBasic, Class, String[], Op[], Object[])}.
   * 
   * @effects 
   *  return a simple search query based on one attribute and its value.
   *  
   * @version 5.4
   */
  public static Query createSearchQuery(DSM dsm, Class<?> c,
      String attribName, Op op, Object attribVal) {
    Query query = new Query();
    DAttr attrib = dsm.getDomainConstraint(c, attribName);
    query.add(new ObjectExpression(c, attrib, op, attribVal));
    
    return query;
  }
  
  /**
   * @effects 
   *  create and return an {@link AttribFunctor} from the arguments
   *  
   * @requires 
   *  arguments are valid
   * @version 3.1
   */
  public static AttribFunctor createAttributeFunctor(final Function func,
      final String attribName, final Op op) {
    return new AttribFunctor() {
      
      @Override
      public Class<? extends Annotation> annotationType() {
        return AttribFunctor.class;
      }
      
      @Override
      public Function function() {
        return func;
      }
      
      @Override
      public Op operator() {
        return op;
      }
      
      @Override
      public String attrib() {
        return attribName;
      }
    };
  }

  /**
   * @requires 
   *  <tt>c1 join c2 on joinAttrib</tt> /\ 
   *  <tt>c2.targetAttrib is valid</tt> 
   * @effects 
   *  create and return a binary join query (i.e. between 2 classes): 
   *    <tt>c1 join c2 on joinAttrib where c2.targetAttrib op val</tt> 
   *    
   * @example <pre>
   *  c1 = Student, c2 = City
   *  c1 join c2 on c1.address (=joinAttrib) with City.name (=targetAttrib) matches (=op) 'Ha%' (=val)
   *  </pre>    
   * @version 5.3
   * 
   */
  public static Query createSimpleJoinQuery(DSMBasic dsm, Class c1,
      Class c2, String joinAttrib, String targetAttrib, Op op,
      Object val) {
    Query q = new Query();

    
    DAttr da = dsm.getDomainConstraint(c2, targetAttrib);
    ObjectExpression valExp = new ObjectExpression(c2, da, op, val);
    
    DAttr dj = dsm.getDomainConstraint(c1, joinAttrib);
    op = Op.EQ;
    ObjectJoinExpression exp = new ObjectJoinExpression(c1, dj, op,
        valExp);
    
    q.add(exp);
    
    return q;
  }
  
  /**
   * @requires 
   *  <tt>c join c1 on joinAttrib1</tt> /\ <tt>c join c2 on joinAttrib2</tt> 
   *  <tt>c1.targetAttrib1 is valid</tt> /\  <tt>c2.targetAttrib2 is valid</tt>
   * @effects 
   *  create and return a ternery join query (i.e. between 3 classes): 
   *    <tt>c1 join c on joinAttrib1 where c1.targetAttrib1 op1 val1</tt> and 
   *    <tt>c2 join c on joinAttrib2 where c2.targetAttrib2 op2 val2</tt> 
   *    
   * @example <pre>
   *  c = Enrolment, c1 = Student, c2 = CourseModule
   *  c join c1 on c.student (=joinAttrib1) with Student.name (=targetAttrib1) matches (=op1) 'A%' (=val1)
   *  c join c2 on c.module (=joinAttrib2) with Module.name (=targetAttrib2) matches (=op2) 'IPG' (=val2)
   *  </pre>
   * @version 5.3
   * 
   */
  public static Query createTerneryJoinQuery(DSMBasic dsm, Class c,
      Class c1, Class c2, 
      String joinAttrib1, String joinAttrib2, 
      String targetAttrib1, Op op1, Object val1,
      String targetAttrib2, Op op2, Object val2
      ) {
    Query q = new Query();


    // value expression on c1.targetAttrib1
    DAttr da1 = dsm.getDomainConstraint(c1, targetAttrib1);
    ObjectExpression val1Exp = new ObjectExpression(c1, da1, op1, val1);

    // value expression on c2.targetAttrib2
    DAttr da2 = dsm.getDomainConstraint(c2, targetAttrib2);
    ObjectExpression val2Exp = new ObjectExpression(c2, da2, op2, val2);
    
    // join expression: c join c1 with val1Exp
    DAttr dj1 = dsm.getDomainConstraint(c, joinAttrib1);
    ObjectJoinExpression exp1 = new ObjectJoinExpression(c, dj1, Op.EQ,
        val1Exp);
    q.add(exp1);

    // join expression: c join c2 with val2Exp
    DAttr dj2 = dsm.getDomainConstraint(c, joinAttrib2);
    ObjectJoinExpression exp2 = new ObjectJoinExpression(c, dj2, Op.EQ,
        val2Exp);
    q.add(exp2);

    return q;
  }
}
