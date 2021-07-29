package jda.modules.dcsl.syntax.function;

import jda.modules.dcsl.syntax.function.AttribFunctor;

/**
 * @overview
 *  Defines names of common functions used in {@link AttribFunctor}.
 *  
 *  <p><b>IMPORTANT</b>: changes to the values of this enum must be reflected in the SQL Function enum(s) 
 *  of the underlying data sources.
 *   
 * @author dmle
 */
public enum Function {
  /** nil (no function) */
  nil,
  // arithmetic
  sum,
  avg,
  min,
  max,
  // date-time
  /**data source-specific date parsing function: e.g. for SQL: date '16/03/2016'.
   * 
   * <br>Unlike other functions, e.g. {@link Function#dateToString}, which produces string-typed date output, this function
   * produces a date object output specific for the data source. Such date object is needed in query expressions
   * that compare date values using inequality operators (e.g. <=, >=, etc). For expressions using the equality (=) operator, 
   * the string-typed date output is a simpler and acceptable alternative.
   *  
   * @version 3.2c
   */
  dsDate, 
  numMillies,
  month, year, age, 
  /**date conversion function: e.g. Date(06/15/2015) -> "15/06/2015" */
  dateToString,
  /**date's month conversion function: e.g. Date(11/06/2015) -> "06/2015" */
  dateToMonthOfYearString, 
  /**
   * equivalence of the SQL's distinct function
   * @version 3.3
   */
  distinct,
  ;

  public boolean isNil() {
    return this == nil;
  }
}
