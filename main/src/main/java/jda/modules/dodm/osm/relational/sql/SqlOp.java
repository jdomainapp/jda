package jda.modules.dodm.osm.relational.sql;

import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dodm.osm.relational.RelationalOSMBasic;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;
import jda.modules.oql.def.FlexiQuery;
import jda.modules.oql.def.ObjectAttributeExpression;
import jda.modules.oql.def.ObjectExpression;

/**
 * @overview
 *  The standard SQL operators
 *  
 * @author dmle
 *
 */
public enum SqlOp { 
// implements DataSourceQueryOp {
  /**equal*/
  EQ("=", Op.EQ) {
    @Override
    public String toString(RelationalOSMBasic osm,  DAttr.Type type, Object domainVal, boolean isSelectQuery) {
      if (domainVal == null) {  // special cases for null values
        if (isSelectQuery)
          return " is Null ";
        else
          return super.toString(osm, type, domainVal, isSelectQuery);
      } else {
        return super.toString(osm, type, domainVal, isSelectQuery);
      }
    }
  }, //
  
  /**less than*/
  LT("<", Op.LT), //
  
  /**less than or equal*/
  LTEQ("<=", Op.LTEQ), //
  
  /**greater than*/
  GT(">", Op.GT), //
  
  /**greater than or equal*/
  GTEQ(">=", Op.GTEQ), //
  
  /**SQL: not in*/
  NOIN("not in", Op.NOIN) {
    @Override
    public String toString(RelationalOSMBasic osm, DAttr.Type type, Object domainVal, boolean isSelectQuery) {
      /*v3.0: improved to support ObjectAttributeExpression
      // dont need to convert domainVal as it is already in a format ready to be used 
      // (e.g. a sub-query)
      return " " + getName() + " (" + domainVal + ")";
      */
      String notInSql = " %s (%s)";
      if (domainVal instanceof ObjectAttributeExpression) {
        ObjectAttributeExpression exp = (ObjectAttributeExpression) domainVal;
        /*v3.1: use method
        String targetSql = "Select %s from %s";
        Class targetCls = exp.getDomainClass();
        DomainConstraint targetAttrib = exp.getDomainAttribute();
        String colName = osm.getColName(targetCls, targetAttrib);
        String tabName = osm.getDom().getDsm().getDomainClassName(targetCls);
        targetSql = String.format(targetSql, colName, tabName);
        */
        String targetSql = attributeExp2SQL(osm, exp);
        
        notInSql = String.format(notInSql, getName(), targetSql);
      } else if (domainVal instanceof FlexiQuery) {
        FlexiQuery q = (FlexiQuery) domainVal;
        /* v3.1: use method
        String targetSql = "Select %s from %s %s";
        String where = "Where %s";
        
        Class targetCls = q.getSrcDomainClass();
        DomainConstraint targetAttrib = q.getDomainAttribute(0);
        String colName = osm.getColName(targetCls, targetAttrib);
        String tabName = osm.getDom().getDsm().getDomainClassName(targetCls);
        
        if (q.isEmpty()) {
          where = "";
        } else {
          //TODO: support all terms
          ObjectExpression exp0 = (ObjectExpression) q.getTerm(0);
          String expSql = osm.toSQLExpression(exp0, true);
          where = String.format(where, expSql);
        }
        
        targetSql = String.format(targetSql, colName, tabName, where);
        */
        String targetSql = flexiQuery2SQL(osm, q);
        
        notInSql = String.format(notInSql, getName(), targetSql);
      }  
      else if (domainVal instanceof Object[]) {
        // array of values
        Object[] vals = (Object[]) domainVal;
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < vals.length; i++) {
          sb.append(toSqlString(osm, type, vals[i]));
          if (i < vals.length-1) sb.append(",");
        }
        notInSql = String.format(notInSql, getName(), sb.toString());
      } else {
        // dont need to convert domainVal as it is already in a format ready to be used 
        // (e.g. a sub-query)
        //return " " + getName() + " (" + domainVal + ")";
        notInSql = String.format(notInSql, getName(), domainVal);
      }
      
      return notInSql;
    }
  }, //
  
  /**SQL: in
   * <br> conversion is similar to {@link #NOIN}
   * @version 3.1
   */
  IN("in", Op.IN) {
    @Override
    public String toString(RelationalOSMBasic osm, DAttr.Type type, Object domainVal, boolean isSelectQuery) {
      String inSql = " %s (%s)";
      if (domainVal instanceof ObjectAttributeExpression) {
        ObjectAttributeExpression exp = (ObjectAttributeExpression) domainVal;
        String targetSql = attributeExp2SQL(osm, exp);
        
        inSql = String.format(inSql, getName(), targetSql);
      } else if (domainVal instanceof FlexiQuery) {
        FlexiQuery q = (FlexiQuery) domainVal;
        String targetSql = flexiQuery2SQL(osm, q);
        
        inSql = String.format(inSql, getName(), targetSql);
      }  
      else if (domainVal instanceof Object[]) {
        // array of values
        Object[] vals = (Object[]) domainVal;
        StringBuffer sb=new StringBuffer();
        for (int i = 0; i < vals.length; i++) {
          sb.append(toSqlString(osm, type, vals[i]));
          if (i < vals.length-1) sb.append(",");
        }
        inSql = String.format(inSql, getName(), sb.toString());
      } else {
        // dont need to convert domainVal as it is already in a format ready to be used 
        // (e.g. a sub-query)
        //return " " + getName() + " (" + domainVal + ")";
        inSql = String.format(inSql, getName(), domainVal);
      }
      
      return inSql;
    }
  }, //
  
  /**SQL: like */
  MATCH("like", Op.MATCH), //

  /** SQL: not equal to (&lt;&gt;)*/
  NOTEQ("<>", Op.NOTEQ) {
    @Override 
    public String toString(RelationalOSMBasic osm, DAttr.Type type, Object domainVal, boolean isSelectQuery) {
      if (domainVal == null) {  // special cases for null values
        if (isSelectQuery)
          return " is Not Null ";
        else
          return super.toString(osm, type, domainVal, isSelectQuery);
      } else {
        return super.toString(osm, type, domainVal, isSelectQuery);
      }
    }
  }, //
  
  /**SQL: between*/
  BETWEEN("between", Op.BETWEEN) {
    @Override
    public String toString(RelationalOSMBasic osm, DAttr.Type type, Object domainVal, boolean isSelectQuery) throws IllegalArgumentException {
      // special conversion
      // ASSUMES: val is a 2-element Object-compatible array
      if (domainVal instanceof Object[]) {
        Object[] range = (Object[]) domainVal;
        return " " + getName() + " " + range[0] + " and " + range[1];
      } else {
        throw new IllegalArgumentException(this.getClass().getSimpleName()+"toString: not an Object[]-typed value: " + domainVal);
      }
    }
  }    //
  ;    
  
  // the object expression operator to which this operator is mapped
  private Op objOp;
  // SQL standard name
  private String name;

  private SqlOp(String n, Op mappedToObjOp) {
    objOp = mappedToObjOp;
    name = n;
  }
  
  /**
   * @effects 
   *  convert <tt>exp</tt> to the equivalent SQL (nested) query and return it
   */
  protected String attributeExp2SQL(RelationalOSMBasic osm,
      ObjectAttributeExpression exp) {
    String targetSql = "Select %s from %s";
    Class targetCls = exp.getDomainClass();
    DAttr targetAttrib = exp.getDomainAttribute();
    String colName = osm.getColName(targetCls, targetAttrib);
    String tabName = osm.getDom().getDsm().getDomainClassName(targetCls);
    targetSql = String.format(targetSql, colName, tabName);
    
    return targetSql;
  }

  /**
   * @effects 
   *  convert <tt>q</tt> to the equivalent SQL query and return it
   */
  protected String flexiQuery2SQL(RelationalOSMBasic osm, FlexiQuery q) {
    String targetSql = "Select %s from %s %s";
    String where = "Where %s";
    
    Class targetCls = q.getSrcDomainClass();
    DAttr targetAttrib = q.getDomainAttribute(0);
    // v3.1: support attribute function, e.g. max(dob)
    Function targetAttribFunc = q.getDomainAttributeFunction(targetAttrib);    
    String colName = osm.getColName(targetCls, targetAttrib, targetAttribFunc);
    String tabName = osm.getDom().getDsm().getDomainClassName(targetCls);
    
    if (q.isEmpty()) {
      where = "";
    } else {
      //TODO: support all terms
      ObjectExpression exp0 = (ObjectExpression) q.getTerm(0);
      String expSql = osm.toSQLExpression(exp0, true);
      where = String.format(where, expSql);
    }
    
    targetSql = String.format(targetSql, colName, tabName, where);
    
    return targetSql;
  }

  public Op getMapping() {
    return objOp;
  }
  
  public String getName() { return name; }
  
  /**
   * @effects 
   *  convert this to a string representation of the equivalent SQL-friend operator
   */
  public String toString(RelationalOSMBasic osm,   // v3.0 
      DAttr.Type type, Object domainVal, boolean isSelectQuery) {
    // general conversion
    // convert val to Sql-friendly
    String sqlVal = toSqlString(osm, type, domainVal);
    return " " + name + " " + sqlVal;
  }

  /**
   * This differs from {@link #toString(RelationalOSMBasic, jda.modules.dcsl.syntax.DAttr.Type, Object, boolean)} in 
   * that it operates over two variables instead of a variable and a value. 
   * 
   * @effects 
   *  convert this to a string representation of the equivalent SQL operator that is between <b>two variables</tt>
   * @version 3.3
   */
  public String toString(RelationalOSMBasic osm,  String var1, String var2, boolean isSelectQuery) {
    // general conversion (sub-types can override if needed)
    return var1 + " " + name + " " + var2 ;
  }
  
//  /**
//   * Unlike {@link #toString(RelationalOSMBasic, Type, Object, boolean)}, this method takes an additional argument <tt>dsFunc</tt>
//   * which it uses to determine whether to apply the function to the domain value when converting it. 
//   * 
//   * <p>Normally, data source function <tt>dsFunc</tt> is only applied to the domain attribute part of the query expression and then combined with 
//   * the to-string result of the value (performed by this) to form a data source expression. However, there are special cases
//   * (e.g. function dsDate) where the data source function needs to also apply to the value in order for the query expression 
//   * to be correct. 
//   * 
//   * <p>For example, the query expression: date dateOfBirth <= date '16/3/2016'. Here dateOfBirth is a domain attribute and '16/3/2016' is 
//   * the domain value. 
//   * 
//   * @effects 
//   *   if <tt>dsFunc != null</tt> then use it (if needed) to convert <tt>domainVal</tt> to SQL value. 
//   *   If <tt>dsFunc</tt> is used, 
//   *   the conversion will convert <tt>domainVal</tt> into a data source function over <tt>domainVal</tt> (e.g. date '16/3/2016') 
//   *     
//   * @version 3.2c
//   */
//  public String toString(RelationalOSMBasic osm,
//      DataSourceFunction dsFunc, Type type, Object domainVal, boolean isSelectQuery) {
//    boolean applyFuncToVal = dsFunc != null && dsFunc.isAppliedToRHSValue();
//    
//    String sqlVal = toSqlString(type, domainVal);
//    
//    if (applyFuncToVal) {
//      // apply dsFunc to domainVal
//      sqlVal = dsFunc.toString(sqlVal); 
//    } 
//    
//    return " " + name + " " + sqlVal; 
//  }
  
  /**
   * @effects 
   *   convert <tt>domainVal</tt> to suitable SQL value based on <tt>type</tt> and return the result
   * @version 
   * - 3.2c: added <tt>osm</tt> 
   */
  protected String toSqlString(RelationalOSMBasic osm, // v3.2c 
      DAttr.Type type, Object domainVal) {
    if (domainVal == null) {
      return "DEFAULT"; // update query: use DEFAULT
    } else {
      /* v3.2: support the case of date comparison using inequality op (e.g. <, <=, >, >=)
      if (type.isString() 
          || type.isBoolean()
          || type.isDate()  // v3.0: date value as string, e.g. '08/06/2015'
          ) {
        return ("'" + domainVal + "'");
      */
      if (type.isDate() && this.isInEqualityOperator()) {
        // date type with inequality operator: convert domainVal to sql date for comparison
        // @requires: domainVal is a date string, e.g. "08/06/2015"
        DataSourceFunction dateFunc = osm.getDataSourceFunctionFor(Function.dsDate);
        return dateFunc.toString("'" + domainVal + "'");
      } else if (type.isString() 
          || type.isBoolean()
          || type.isDate()  // v3.0: date value as string, e.g. "08/06/2015"
          ) {
        return ("'" + domainVal + "'");
      } else { // if (type.isPrimitive()) {
        return (domainVal + "");
      }
    }
  }

  /**
   * Note: this must not include operator {@link #NOTEQ}.
   *  
   * @effects 
   *  if this is one of the following operators {@link #LT}, {@link #LTEQ}, {@link #GT}, {@link #GTEQ}
   *    return true
   *  else
   *    return false
   * @version 3.2c
   */
  private boolean isInEqualityOperator() {
    return this.equals(LT) || this.equals(LTEQ) || this.equals(GT) || this.equals(GTEQ);
  }
}
