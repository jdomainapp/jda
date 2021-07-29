/**
 * 
 */
package jda.mosa.controller.assets.datacontroller.command;

import java.util.Collection;
import java.util.HashSet;

import jda.modules.dcsl.syntax.DAttr;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.util.MessageCode;

/**
 * @overview 
 *  Opens domain objects already stored in the buffer of owner data controller. This assumes that 
 *  the domain objects had previously been loaded and set in the buffer.
 *  
 *  <p>This command is specifically designed for configuring the open-command of the controller of the 
 *  module object associated to the sub-view of the output attribute of 
 *  a report domain class. The value of this output attribute is usually populated after the report
 *  has been executed.
 *    
 * @author dmle
 *
 * @version 5.0
 */
public class OpenObjectsInBufferDataControllerCommand<C> extends DataControllerCommand {

  /**
   * @effects invoke super
   */
  public OpenObjectsInBufferDataControllerCommand(DataController<C> dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  /* (non-Javadoc)
   * @see domainapp.basics.controller.datacontroller.command.DataControllerCommand#execute(domainapp.basics.core.ControllerBasic.DataController, java.lang.Object[])
   */
  /**
   * @effects <pre>
   *  if the owner data controller is nested /\ 
   *  the parent's attribute is initialised
   *    if the owner data controller is opened
   *      clear it
   *    call open on the owner data controller with the attribute value as input
   *  else
   *    do nothing
   *    </pre>
   */
  @Override
  public void execute(DataController src, Object... args) throws Exception {
    DataController dctl = getDataController();
    
    if (dctl.isNestedIn() // is nested in
        //&& !dctl.isOpened() // is not opened
        ) {
      
      if (dctl.isOpened()) {
        dctl.clearGUIOnly();
      }
      
      DataController parentDctl = dctl.getParent();
      
      DAttr parentAttrib = dctl.getLinkAttributeOfParent();
      Object parentAttribVal = dctl.getDodm().getDsm().getAttributeValue(parentDctl.getDomainClass(), dctl.getParentObject(), 
          parentAttrib);
      
      if (parentAttribVal != null) {
        // parent attribute is initialised
        if (parentAttribVal instanceof Collection) {
          // collection-typed
          dctl.openObjects((Collection) parentAttribVal, false);
        } else {
          // not collection-typed
          Collection col = new HashSet();
          col.add(parentAttribVal);
          dctl.openObjects(col, false);
        }
      } else {
        // display information message
        ControllerBasic controller = dctl.getCreator();
        controller.displayMessageFromCode(MessageCode.NO_CHILD_OBJECTS_FOUND,
            dctl, 
            new Object[] {controller.getDomainClassLabel(), parentDctl.getCreator().getDomainClassLabel()}
            );
      }
    }
  }
}
