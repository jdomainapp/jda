package jda.modules.setup.commands;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.setup.model.SetUpBasic;

/**
 * @overview
 *  A {@link SetUpCommand} that copy all setup-related resource files of 
 *  the specified application module
 *  to the application folder.
 *  
 * @author dmle
 *
 */
public class CopyResourceFilesCommand extends SetUpCommand {

  public CopyResourceFilesCommand(SetUpBasic su, Class moduleDescriptorCls) {
    super(su, moduleDescriptorCls);
  }

  @Override
  public void run() throws NotPossibleException {
    super.copyResourceFiles();
  }

}
