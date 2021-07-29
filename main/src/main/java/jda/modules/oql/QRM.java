/**
 * 
 */
package jda.modules.oql;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;

/**
 * @overview 
 *  A Query manager that is <b>specifically designed</b> to provide special domain classes (e.g. reports) with 
 *  a query API. A <b>singleton</bb> {@link QRM} is created automatically by the {@link DODMBasic}
 *  the software (via the {@link #createSingleInstance(DODMBasic)} method). 
 *  
 *  <p>To access the QRM, a domain class simply invoke the {@link #getInstance()} method and then 
 *  use either {@link #getDom()} to perform the actual query.
 *  
 *  <p>To create the query, however, it needs to use the {@link QueryToolKit} class.
 *  
 * @author dmle
 *
 * @version 5.0
 */
public class QRM {
  /* TODO: at this stage, this class is simply a wrapper around DODMBasic and not a proper
   * query manager. The main reason is because DOMBasic already provides most of the query API needed.
   * 
   * Should we make this the proper query manager class (i.e. with a query API?)
   * 
   * The answer may be 'No' because this class is ONLY used in by some special domain classes (e.g. reports)
   * that are used with DomainAppTool. It is NOT TO BE USED in production software.
   */
  
  private static QRM instance;
  private DODMBasic dodm;

  /**
   * @effects 
   *  initialise this with <tt>dodm</tt>
   */
  private QRM(DODMBasic dodm) {
    this.dodm = dodm;
  }

  /**
   * 
   * @effects 
   *  if {@link #instance} has not been created
   *    create it from <tt>dodm</tt>
   *  else 
   *    do nothing
   */
  public static QRM createSingleInstance(DODMBasic dodm) {
    if (instance == null) {
      instance = new QRM(dodm);
    }
    // TODO: improve this fix
    // v5.1: a temporary fix for multiple DODMBasic objects being created by setup
    // the last object is the one that we will use!
    else {
      instance.dodm = dodm;
    }
    // end 5.1
    
    return instance;
  }
  
  /**
   * @effects 
   *  if {@link #instance} is initialised (by {@link #createSingleInstance(DODMBasic)})
   *    return {@link #instance}
   *  else
   *    throws NotPossibleException
   */
  public static QRM getInstance() throws NotPossibleException {
    if (instance == null) {
      throw new NotPossibleException(NotPossibleException.Code.NULL_POINTER_EXCEPTION, 
          new Object[] {QRM.class.getSimpleName(), "instance not yet initialised"});
    }
    
    return instance;
  }
  
  /**
   * @effects 
   *  return the {@link DOMBasic} of the underlying {@link DODMBasic}.
   */
  public DOMBasic getDom() {
    return dodm.getDom();
  }
  
  /**
   * @effects 
   *  return the {@link DSMBasic} of the underlying {@link DODMBasic}.
   */
  public DSMBasic getDsm() {
    return dodm.getDsm();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return "QRM (" + dodm + ")";
  }
}
