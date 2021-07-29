package jda.modules.oql.def;

import java.util.Collection;
import java.util.Map;

import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.exceptions.NotImplementedException;
import jda.modules.common.expression.Op;
import jda.modules.oql.QueryToolKit;

/**
 * Represents an expression, for example: name = 'Nguyen Van An' or id = 1.
 */
public class Expression {
  private String var;
  private Op op;
  private Object val;
  private Type type;

  /** Expression type constants*/
  public static enum Type {
    /** value expressions (default) */
    Data,
    /** object expressions */
    Object,
    /**
     * expressions involving table columns only (e.g. the join expression
     * student.city=city.name)
     */
    Metadata,
    /**
     * RHS expression is a nested SQL
     */
    Nested, //
  };

  /**
   * @effects 
   *  initialise this as Expression<var,op,val> whose type is <tt>type</tt> 
   */
  public Expression(String var, Op op, Object val, Type type) {
    this.var = var;
    this.op = op;
    this.val = val;
    this.type = type;
  }

  /**
   * @effects 
   *  initialise this as Expression<var,op,val> whose type is {@link RegionType.Data} 
   */
  public Expression(String var, Op op, Object val) {
    this(var, op, val, Type.Data);
  }

  public String getVar() {
    return var;
  }
  
  public Object getVal() {
    return val;
  }


  public Expression.Type getType() {
    return type;
  }
  
  public Op getOperator() {
    return op;
  }
  
  /**
   * Evaluates this expression for a given value of the expression variable. 
   * 
   * @effects if <code>actualVal</code> or <code>val</code> is <code>null</code> then returns false, 
   *          else if <code>actualVal</code> satisfies this expression, 
   *          i.e. <pre>actualVal op val</pre> is true, then return <code>true</code>, else
   *          return <code>false</code>
   */
  boolean eval(Object actualVal) {
    if (actualVal == null || val == null)
      return false;
    
    if (op.equals(Op.EQ)) {
      /** 
       * if actual value is a primitive type then can use toString to compare, if not 
       * use equals() */
      if (actualVal.toString().equals(val.toString()))
        return true;
      else
        return actualVal.equals(val);
    } else if (op.equals(Op.MATCH)) {
      /**
       * Only effective for strings and primitive types
       * assumes that arguments support this operation*/
      //TODO: improves this (?)
      // convert to strings and use indexOf to test:
      // returns true if actualVal contains val
      /*v2.6.4.b: added support for SQL value pattern
      return (actualVal.toString().indexOf(val.toString()) > -1);
      */
      String valStr = val.toString();
      valStr = QueryToolKit.fromSQLValuePattern(valStr);
      return (actualVal.toString().indexOf(valStr) > -1);
    } else if (op.equals(Op.CONTAINS)) {
      /** Only applied to collection-type values */
      if (!(actualVal instanceof Collection))
        return false;
      else {
        // returns true of actualVal contains val
        Collection vcol = (Collection) actualVal;
        return (vcol.contains(val));
      }
    }
    /* v5.2: added support
     */
    else if (op.equals(Op.IN)) {
      /** reverse of CONTAINS: also applied only to collection-type values */
      if ((val instanceof Collection) || CollectionToolkit.isArrayType(val.getClass())) {
        // returns true of actualVal contains val
        if (val instanceof Collection) {
          Collection vcol = (Collection) val;
          return (vcol.contains(actualVal));
        } else {  // array
          Object[] arr = (Object[]) val;
          for (Object item : arr) {
            if (item.equals(actualVal))
              return true;
          }
          return false;
        }
      } else {
        return false;
      }
    }
    else {
      throw new NotImplementedException(NotImplementedException.Code.FEATURE_NOT_SUPPORTED,
          new Object[] {op.getName()});
    }
  }
  
  public String toString() {
    return toString(true);
  }
  
  /**
   * @effects 
   *  return expression-style string of this, in which the term value is replaced by <tt>""</tt> 
   *  if it is null and <tt>withNuls = false</tt> 
   */
  public final String toString(boolean withNulls) {
    Object v;
    if (val == null && !withNulls)
      v = "";
    else 
      v = val;
    
    String varStr = getVarString();
    String prefixStr = getPrefixString();
    
    if (type.equals(Type.Data)) {
      // convert values
      if (val instanceof Number) {
        return prefixStr + "(" +varStr + op.getName() + v + ")";
      } else {
        return prefixStr + "(" + varStr + op.getName() + "'" + v + "')";
      }
    } else if (type.equals(Type.Nested)) {
      // add a pair of brackets
      // IMPORTANT: extra spaces around the operator 
      return prefixStr + "(" + varStr + " " + op.getName() + " (" + v + "))";
    } else { // other types
      // keep the same
      return prefixStr + "(" + varStr + op.getName() + v + ")";
    }
  }

  /**
   * @requires 
   *  <tt>queryDict != null -> 
   *    for all (s1,s2) in queryDict. s1 is a domain attribute name, s2 is a label of the attribute named s1</tt> 
   * @effects 
   *  create and return a <b>user-friendly</b> string representation of <tt>this</tt>, 
   *  in which the term's variable name is replaced by a language-specific label 
   *  in <tt>queryDict</tt>, if it is specified.
   *  
   * @version 3.1
   */
  public final String toUserFriendlyString(Map<String,String> queryDict) {
    Object v;
    if (val == null)
      v = "";
    else 
      v = val;
    
    String varStr = getVarString(queryDict);
    String prefixStr = "";  // no prefix 
    
    if (type.equals(Type.Data)) {
      // convert values
      if (val instanceof Number) {
        return prefixStr + "(" +varStr + op.getName() + v + ")";
      } else {
        return prefixStr + "(" + varStr + op.getName() + "'" + v + "')";
      }
    } else if (type.equals(Type.Nested)) {
      // add a pair of brackets
      // IMPORTANT: extra spaces around the operator 
      return prefixStr + "(" + varStr + " " + op.getName() + " (" + v + "))";
    } else { // other types
      // keep the same
      return prefixStr + "(" + varStr + op.getName() + v + ")";
    }
  }

  protected String getPrefixString() {
    return "";
  }
  
  protected String getVarString() {
    return var;
  }
  
  /**
   * @requires 
   *  <tt>queryDict != null -> 
   *    for all (s1,s2) in queryDict. s1 is a domain attribute name, s2 is a label of the attribute named s1</tt> 
   * @effects 
   *  create and return a <b>user-friendly</b> string representation of <tt>this.var</tt>, 
   *  using name mappings in <tt>queryDict</tt>, if it is specified.
   *  
   * @version 3.1
   */
  protected String getVarString(Map<String, String> queryDict) {
    // queryDict is not used here
    return var;
  }
  
  protected String getClassName() {
    return this.getClass().getSimpleName();
  }
  
  /**
   * @effects returns an <code>Op</code> <code>op</code> from its name <code>opName</code>, i.e. 
   *          <code>op = Op.valuesOf(opName)</code>, or <code>null</code> if <code>opName</code> 
   *          is not a valid name.
   *          
   * @requires <code>opName != null &</code> one of the valid <code>Op</code>'s names
   * 
   */
  public static Op toOp(String opName) {
    try {
      Op op = Op.valueOf(opName);
      return op;
    } catch (IllegalArgumentException e) {
      // ignore
      return null;
    }
  }

  /**
   * @requires  
   *  exp != null
   * @effects 
   *  create and return an <tt>Expression</tt> instance whose content is the same as <tt>exp</tt>'s.
   */
  public static Expression createInstance(Expression exp) {
    return new Expression(exp.getVar(), exp.getOperator(), exp.getVal());
  }
} // end class Expression
