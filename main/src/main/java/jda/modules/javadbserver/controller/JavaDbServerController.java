package jda.modules.javadbserver.controller;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.net.ProtocolSpec;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.osm.OSMFactory;
import jda.modules.javadbserver.model.JavaDbServer;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.composite.TaskDaemonController;

public class JavaDbServerController extends TaskDaemonController<JavaDbServer> {

  public JavaDbServerController(DODMBasic dodm, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    super(dodm, module, moduleGui, parent, config);
  }

  @Override
  public void doTask(JavaDbServer object) throws ApplicationRuntimeException,
      DataSourceException {
//    // i. create (if not already) a JavaDbServer object from a server URL (provided by the config) or try to start it
//    startDbServerIfNotAlready();
      
//    // periodically check if GUI is initialised and, if so, invoke updateGUI to update the status
//    updateGUI();
    
    DataController<JavaDbServer> dctl = getRootDataController();
    
    JavaDbServer dbServer = dctl.getCurrentObject();
    
    // start server if not already
    if (!dbServer.isRunning()) {
      // created but not yet started (or perhaps failed)
      // try to start...
      if (!dbServer.isPortAvailable()) {
        // a server is already running by another JVM
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_USE_PORT, 
            new Object[] {dbServer.getPort()});
      } else {
        dbServer.start();
      }
    }
    
    if (hasGUI()) {
      // GUI might not have been created the first time
      showGUI();  // if not already
      
      // update gui to show current status
      dctl.updateGUI(true);
      getGUI().updateSizeOnComponentChange();
    }      
  }

  /**
   * @effects 
   *  create (if not already) a JavaDbServer object from a server URL (provided by the config) 
   *  or if it has already been created but not started try to start it
   *  
   * <p>throws NotPossibleException if failed.
   */
  @Override
  public void createObjectActively() throws ApplicationRuntimeException, DataSourceException {
    DataController<JavaDbServer> dctl = getRootDataController();
    
    JavaDbServer dbServer; //v3.0 = dctl.getCurrentObject();
    
    // create client-server protocol for config
//    Configuration config = getConfig();
//    
//    OsmConfig osmCfg = config.getDodmConfig().getOsmConfig();
//    
//    if (!(osmCfg instanceof OsmClientServerConfig)) {
//      throw new NotPossibleException(NotPossibleException.Code.NO_SERVER_PROTOCOL);
//    }
//    
//    OsmClientServerConfig clientServerCfg = (OsmClientServerConfig) config.getDodmConfig().getOsmConfig();
//    
//    ProtocolSpec serverProt = clientServerCfg.getServerProtocolSpec();
//    
//    if (serverProt == null) {
//      throw new NotPossibleException(NotPossibleException.Code.NO_SERVER_PROTOCOL);
//    }
    
    ProtocolSpec serverProt = OSMFactory.getStandardClientServerProtocolSpec("derby", JavaDbServer.DEFAULT_SERVER_URL);
    
    dbServer = dctl.createObject(new Object[] { serverProt});
    
    if (!dbServer.isPortAvailable()) {
      // a server is already running by another JVM
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_USE_PORT, 
          new Object[] {dbServer.getPort()});
    } else {
      dbServer.start();
    }
  }
}
