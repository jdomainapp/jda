package jda.modules.dodm.osm.relational.sql.function;

import jda.modules.dcsl.syntax.function.Function;

/**
 * @overview
 *  A generic interface for defining RDBMS-specific SQL function table.
 *  
 * @author dmle
 */
public interface DataSourceFunction {
  
  /**
   * @effects 
   *  return the {@link Function} object that is mapped to this 
   */
  public Function getMapping();

  /**
   * @effects 
   *  return the SQL function taken from this.name and <tt>var</tt>
   *  (e.g. if this function is <tt>year()</tt> and <tt>var=dob</tt>
   *  then <tt>toString(var) = year(dob)</tt> 
   */
  public String toString(String var);
}
