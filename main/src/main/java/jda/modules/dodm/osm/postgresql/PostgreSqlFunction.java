package jda.modules.dodm.osm.postgresql;

import jda.modules.dcsl.syntax.function.Function;
import jda.modules.dodm.osm.relational.sql.function.DataSourceFunction;

/**
 * @overview
 *  Each function definition takes a format string <tt>%s</tt> which is to be replaced in {@link #toString(String)} 
 *  by the value of input argument. This input argument is the name of a table column.
 *    
 * @author dmle
 *
 */
public enum PostgreSqlFunction implements DataSourceFunction {
  // arithmetic
  sum("sum(%s)", Function.sum),
  avg("avg(%s)",Function.avg),
  min("min(%s)",Function.min),
  max("max(%s)",Function.max),
  // date-time 
  /**
   * see {@link Function#dsDate}
   * 
   * @version 3.2c
   */
  date("date %s", Function.dsDate),
  numMillis("extract(epoch from %s) * 1000",Function.numMillies),
  month("extract(month from %s)",Function.month),
  year("extract(year from %s)",Function.year),
  age("extract(year from CURRENT_DATE) - extract(year from %s)", Function.age),
  /**e.g. Date(06/15/2015) -> "15/06/2015" */
  dateToString("to_char(%s,'DD/MM/YYYY')", Function.dateToString),
  /**e.g. Date(11/06/2015) -> "06/2015" */
  dateToMonthOfYearString("to_char(%s,'MM/YYYY')", Function.dateToMonthOfYearString),

  // v3.3
  distinct("distinct(%s)",Function.distinct),
  ;
  
  // the object expression operator to which this operator is mapped
  private Function objFunc;
  // SQL standard name
  private String nameTemplate;

  private PostgreSqlFunction(String nameTemplate, Function mappedToObjFunc) {
    this.nameTemplate = nameTemplate;
    objFunc = mappedToObjFunc;
  }
  
  @Override
  public Function getMapping() {
    return objFunc;
  }
  
  @Override
  public String toString(String var) {
    return //" " + name() + "(" + var + ") ";
        " " + String.format(nameTemplate, var) + " ";
  }
}
