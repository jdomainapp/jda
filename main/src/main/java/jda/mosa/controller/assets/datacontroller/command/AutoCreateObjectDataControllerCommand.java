package jda.mosa.controller.assets.datacontroller.command;

import jda.modules.common.Toolkit;
import jda.mosa.controller.ControllerBasic.DataController;

/**
 * @overview 
 *  A {@link DataControllerCommand} that automatically creates a new domain object when 
 *  the object form of the owner's data controller is in the <tt>object-creating</tt> state
 *  and, presumably, the user is inputing data on the form.
 *  
 * @author dmle
 *
 * @version 3.2
 */
public class AutoCreateObjectDataControllerCommand<C> extends DataControllerCommand {

  private static boolean debug = Toolkit.getDebug(AutoCreateObjectDataControllerCommand.class);

  public AutoCreateObjectDataControllerCommand(DataController dctl) {
    super(dctl);
    // TODO Auto-generated constructor stub
  }

  @Override
  public void execute(DataController src, Object... args) throws Exception {
    /** invoke createObject ignoring all the exceptions that may be thrown */
    DataController dctl = getDataController();
    
    if (dctl.isCreating()) {
      try {
        dctl.createObject();
      } catch (RuntimeException ex) {
        // update GUI in case information needs to be updated
        dctl.getDataContainer().updateGUI();
  
        // ignore exception
        if (debug )
          ex.printStackTrace();
      } catch (Exception ex1) {
        // ignore exception
        if (debug)
          ex1.printStackTrace();
      }
    }    
  }

}
