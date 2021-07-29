/**
 * @overview
 *
 * @author dmle
 */
package jda.mosa.controller.assets.datacontroller.command.standard;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;

/**
 * @overview
 *  A {@link DataControllerCommand} that is used as a template for customising the {@link LAName#Delete} operation 
 *  of {@link DataController}. 
 *  
 *  <p>Sub-types need to implement the abstract methods. 
 *  
 * @author dmle
 * 
 * @version 3.2
 */
public abstract class DeleteObjectDCtlCommand<C>  extends DataControllerCommand<C> {

  public DeleteObjectDCtlCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    // pre-
    preDeleteObject();
    
    // perform the normal operation and call post-... which needs to be implemented by sub-types 
    C deletedObj = deleteObject();
    
    // post-
    postDeleteObject(deletedObj);
  }

  
  /**
   * @effects 
   *  perform tasks before the current domain object of {@link #getDataController()} is deleted.
   *  
   *   <p>The default implementation is to do nothing
   */
  protected void preDeleteObject() {
    // default: do nothing
  }

  /**
   * Sub-types can customise this method if they need to customise how an object is deleted  
   * 
   * @effects 
   *  delete the current domain object of the type supported by {@link #getDataController()};
   *  return the deleted object
   *  
   *  <p>throws Exception if fails
   */
  protected C deleteObject() throws ConstraintViolationException, DataSourceException {
    DataController<C> dctl = getDataController();

    return dctl.deleteObject();
  }

  /**
   * @effects 
   *  Performs tasks after the current domain object of {@link #getDataController()} is deleted
   */
  protected abstract void postDeleteObject(C deletedObj) throws Exception;
  
}
