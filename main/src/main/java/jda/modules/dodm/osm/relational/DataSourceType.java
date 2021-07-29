package jda.modules.dodm.osm.relational;

import java.sql.Types;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * @overview 
 *  A generic interface that defines the Sql type mapping for {@link DAttr.Type}.
 *  
 * @author dmle
 * @version 
 * - 3.0 <br>
 * - 3.2: added {@link #isSizableFor(int)}
 */
public interface DataSourceType {

  /**
   * @effects
   *  return the Java's <tt>Type</tt> constant equivalent to this  
   */
  Type getMapping();

  /**
   * @effects 
   *  return the SQL-friendly string of this for <tt>args</tt>, which is used in the 
   *  SQL's CREATE statement  
   */
  String toString(Object...args);

  /**
   * @effects 
   *  return the equivalent {@link Types} constant of this
   */
  int getIntValue();
  
  /**
   * @effects 
   *  if this supports the specified <tt>size</tt>
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   * @version 3.2
   */
  boolean isSizableFor(int size);
}
