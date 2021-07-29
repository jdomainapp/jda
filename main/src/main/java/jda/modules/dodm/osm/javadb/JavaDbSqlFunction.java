package jda.modules.dodm.osm.javadb;

import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;

public enum JavaDbSqlFunction implements DataSourceFunction {
  // arithmetic
  sum(Function.sum),
  avg(Function.avg),
  min(Function.min),
  max(Function.max),
  // date-time 
  month(Function.month),
  year(Function.year),  
  // v3.3
  distinct(Function.distinct),
  ;
  
  // the object expression operator to which this operator is mapped
  private Function objFunc;
  // SQL standard name
  //private String name;

  private JavaDbSqlFunction(Function mappedToObjFunc) {
    objFunc = mappedToObjFunc;
  }
  
  @Override
  public Function getMapping() {
    return objFunc;
  }
  
  @Override
  public String toString(String var) {
    return " " + name() + "(" + var + ") ";
  }
}
