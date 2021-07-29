/**
 * @overview
 *
 * @author dmle
 */
package jda.mosa.controller.assets.datacontroller.command;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * @overview
 *  A {@link DataControllerCommand} that is used as a template for customising the {@link LAName#Create} action. 
 *  
 *  <p>Sub-types need to implement the abstract methods. 
 *  
 * @author dmle
 */
public abstract class CreateObjectCommand<C>  extends DataControllerCommand<C> {

  public CreateObjectCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    // perform the normal create and call post-create which needs to be implemented by sub-types 
    C object = createObject();
    
    postCreateObject(object);
  }

  
  /**
   * Sub-types can customise this method to support the creation of sub-type objects of the type 
   * directly supported by {@link #getDataController()}. 
   * 
   * @effects 
   *  create and return a domain object of the type supported by {@link #getDataController()}
   *  
   *  <p>throws Exception if fails
   */
  protected C createObject() throws ConstraintViolationException, NotPossibleException, NotFoundException, DataSourceException {
    DataController<C> dctl = getDataController();

    return dctl.createObject();
  }

  /**
   * @effects 
   *  Performs tasks after <tt>object</tt> is created by {@link #getDataController()}
   */
  protected abstract void postCreateObject(C object);
  
}
