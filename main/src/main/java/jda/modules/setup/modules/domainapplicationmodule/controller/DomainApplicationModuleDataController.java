package jda.modules.setup.modules.domainapplicationmodule.controller;

import java.util.LinkedHashMap;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mccl.conceptmodel.module.DomainApplicationModule;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.datacontroller.DataPanelController;

/**
 * @overview
 *  A data controller responsible for running domain application modules
 * @author dmle
 */
public class DomainApplicationModuleDataController<C> extends DataPanelController<C> {
  private static final boolean debug = Toolkit.getDebug(DomainApplicationModuleDataController.class);
  private static final boolean loggingOn = Toolkit.getLoggingOn(DomainApplicationModuleDataController.class);

  public DomainApplicationModuleDataController(ControllerBasic creator, ControllerBasic user, ControllerBasic.DataController parent)
      throws NotPossibleException {
    super(creator, user, parent);
  }

// v3.2: removed  
//  @Override
//  public ChangeListener getAutoCreateChangeListener() {
//    return new ChangeListener() {
//      @Override
//      public void stateChanged(ChangeEvent e) {
//        /** invoke createObject ignoring all the exceptions that may be thrown */
//        try {
//          // create new object regardless of the application state 
//          createObject();
//        } catch (RuntimeException ex) {
//          // update GUI in case information needs to be updated
//          dataContainer.updateGUI();
//          
//          // ignore exception
//          if (debug)
//            ex.printStackTrace();
//        } catch (Exception ex1) {
//          // ignore exception              
//          if (debug)
//            ex1.printStackTrace();
//        }
//      }
//    };
//  }
  
  /**
   * @effects 
   *  create an object to run the application module 
   */
  @Override
  public C createObject() throws DataSourceException {
    LinkedHashMap<DAttr,Object> vals = dataContainer.getUserSpecifiedState();

    /*dont need to create the object, just get the selected module from the input data 
    Tuple2<Oid,Object> t = schema.createObject(cls, vals.values().toArray());
    currentObj = (C) t.getSecond();
    */

    // run the selected application module
    //DomainApplicationModuleWrapper mw = (DomainApplicationModuleWrapper) currentObj;
    DomainApplicationModule module = (DomainApplicationModule) vals.values().iterator().next();//mw.getDomainModule();
    
    ControllerBasic controller = getCreator();
    
    controller.getMainController().runModule(module);
    
    return (C) module;
    
    /*no need to update state
    String mesg = null;
    AppState state = AppState.Created;
    setCurrentState(state, mesg);
     *
     */
  }
} // end ChartController