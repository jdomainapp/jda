package jda.modules.report.model;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dodm.DODMBasic;

/**
 * @overview
 *  Represents a report that is <b>executable</b>, in the sense that it can retrieve 
 *  its own data from the data source.  
 *  
 * @author dmle
 *
 */
@DClass(serialisable=false)
public abstract class ExecutableReport implements Report {
//  private DODMBasic dodm;

  public ExecutableReport(
      //DODMBasic dodm
      ) {
    //this.dodm = dodm;
  }

//  protected DODMBasic getDodm() {
//    return dodm;
//  }

//  /**
//   * @effects 
//   *  create and return an instance of <tt>cls</tt> from argument <tt>dodm</tt>; 
//   *  throws NotPossibleException if failed to do so
//   */
//  public static ExecutableReport createInstance(
//      Class<? extends ExecutableReport> cls, DODMBasic dodm) throws NotPossibleException {
//    try {
//      // invoke the constructor to create object 
//      ExecutableReport instance = cls.getConstructor(DODMBasic.class).newInstance(dodm);
//      
//      return instance;
//    } catch (Exception e) {
//      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
//          new Object[] {cls.getSimpleName(), dodm});
//    }
//  }

  /**
   * @effects
   *  execute the queries to obtain data from the data source 
   *  update the state of this from the result
   *  
   *  <br>Throws NotPossibleException if failed.
   */
  public abstract void run(DODMBasic dodm) throws NotPossibleException;
}
