package jda.modules.iexport.setup;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.setup.commands.SetUpCommand;
import jda.modules.setup.model.SetUpBasic;

public class ImportPostSetUp extends SetUpCommand {

  public ImportPostSetUp(SetUpBasic su, Class moduleDescriptorCls) {
    super(su, moduleDescriptorCls);
  }

  @Override
  public void run() throws NotPossibleException {
    // create dirs: export, export/templates, export/images 
    SetUpBasic su = getSetUp();
    
    Configuration config = su.getConfig();
    
    String importDir = config.getImportFolder();
    su.createApplicationSubDir(importDir, true);
    
    //v3.0 copy other resources
    copyResourceFiles();
    
  }
}
