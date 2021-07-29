/**
 * @overview
 *
 * @author dmle
 */
package jda.mosa.controller.assets.datacontroller.command.standard;

import java.util.Map;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.command.DataControllerCommand;

/**
 * @overview
 *  A {@link DataControllerCommand} that is used as a template for customising the {@link LAName#Update} operation 
 *  of {@link DataController}. 
 *  
 *  <p>Sub-types need to implement the abstract methods. 
 *  
 * @author dmle
 * 
 * @version 3.2
 */
public abstract class UpdateObjectDCtlCommand<C>  extends DataControllerCommand<C> {

  public UpdateObjectDCtlCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    // pre-update
    preUpdateObject();
    
    // perform the normal operation and call post-... which needs to be implemented by sub-types 
    Map<DAttr,Object> updatedValMap = updateObject();
    
    // post-update
    postUpdateObject(updatedValMap);
  }

  
  /**
   * @effects 
   *  perform tasks before the current domain object of {@link #getDataController()} is updated.
   *  
   *   <p>The default implementation is to do nothing
   */
  protected void preUpdateObject() {
    // default: do nothing
  }

  /**
   * Sub-types can customise this method if they need to customise how an object is updated  
   * 
   * @effects 
   *  update the current domain object of the type supported by {@link #getDataController()} and 
   *  return <tt>Map</tt> of the domain attributes and their values that were changed by the user.
   *  
   *  <p>throws Exception if fails
   */
  protected Map<DAttr,Object> updateObject() throws ConstraintViolationException, DataSourceException {
    DataController<C> dctl = getDataController();

    return dctl.updateObject();
  }

  /**
   * @effects 
   *  Performs tasks after the attributes in <tt>updatedValMap</tt> if the current domain object of {@link #getDataController()} is updated
   */
  protected abstract void postUpdateObject(Map<DAttr,Object> oldValMap) throws Exception;
  
}
