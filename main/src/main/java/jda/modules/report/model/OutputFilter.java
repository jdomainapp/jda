package jda.modules.report.model;

import java.util.Collection;

import jda.modules.common.exceptions.NotPossibleException;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;

/**
 * @overview
 *  Represents a filter function used to filter some intermediate result.
 *  
 * @author dmle
 */
public 
//interface OutputFilter 
abstract class OutputFilter {
  private Class outputClass;

//  /**
//   * @effects 
//   *  Filter <tt>result</tt> which have been obtained for the specified <tt>report</tt> 
//   *  to return a collection of <tt>Oid</tt> of the final result
//   *  
//   *  <p>Throws NotPossibleException if fails to filter.
//   */
//  public Collection<Oid> filter(Collection<Oid> result, Report report) throws NotPossibleException;

  /**
   * @effects 
   *  Filter <tt>result</tt> which have been obtained for the specified <tt>report</tt> 
   *  to return a collection of <tt>Oid</tt> of the final result
   *  
   *  <p>Throws NotPossibleException if fails to filter.
   *  
   *  @version 2.7.4
   */
  public abstract Collection<Oid> filter(ControllerBasic rptCtl, Collection<Oid> result, Report report) throws NotPossibleException;

  public void setOutputClass(Class outputClass) {
    this.outputClass = outputClass;
  }
  
  public Class getOutputClass() {
    return outputClass;
  }
}
