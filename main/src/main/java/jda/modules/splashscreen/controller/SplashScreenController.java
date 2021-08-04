package jda.modules.splashscreen.controller;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dodm.DODMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.SplashInfo;
import jda.modules.mccl.conceptmodel.controller.ControllerConfig;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.mosa.controller.ControllerBasic;
import jda.util.SwTk;

/**
 * @overview
 *  The controller of the splash screen module. This controller runs in two stages:
   *  <ul>
   *    <il> <b>first time</b> (i.e. when it is first loaded): as a background thread waiting for the application 
   *      is initialised AND a specified run time is passed before stopping this controller.
   *    <li> <b>subsequent times</b> (i.e. when it is invoked via the application menu): 
   *      runs normally
 *   </ul>
 * @author dmle
 *
 */
public class SplashScreenController extends ControllerBasic<SplashInfo> {

  /**
   * @overview
   *  A helper class that runs as a background thread waiting for the application 
   *  is initialised AND a specified run time is passed before stopping this controller.
   *  
   * @author dmle
   */
  private static class RunSplashScreen extends Thread {
    
    private long runTime;
    private ControllerBasic splashScreenCtl;

    public RunSplashScreen(ControllerBasic splashScreenCtl, long runTime) {
      this.splashScreenCtl = splashScreenCtl;
      this.runTime = runTime;
    }

    @Override
    public void run() {
      ControllerBasic mainCtl = splashScreenCtl.getMainController();
      long waitTime = runTime;
      final long WAIT_CYCLE = 500;  // millis
      while (!mainCtl.isInitialised()
          // use this to wait for either
          //&&
          // use this to wait for both:
          || 
          waitTime > 0) {
        SwTk.sleep(WAIT_CYCLE);
        waitTime -= WAIT_CYCLE;
      }
      
      // finished -> stop this module
      splashScreenCtl.stop();
    }
  } // end RunSplashScreen

  private boolean firstTime;

  public SplashScreenController(DODMBasic dodm, ApplicationModule module,
      Region moduleGui, ControllerBasic parent, Configuration config)
      throws NotPossibleException {
    super(dodm, module, moduleGui, parent, config);
    firstTime = true;
  }

  /**
   * @effects 
   *  call <tt>Controller.isInitialised</tt> on the main controller to check initialisation status of the application 
   *  <br>call <tt>this.stop()</tt> after <tt>runTime</tt> AND the application is initialised
   */
  @Override
  protected void postRun() throws ApplicationRuntimeException {
    super.postRun();

    if (firstTime) {
      // run background thread
      ControllerConfig ctrlCfg = getControllerConfig();
      
      long runTime = ctrlCfg.getRunTime();
      
      RunSplashScreen runner = new RunSplashScreen(this, runTime);
      runner.start();
      
      firstTime = false;
    }
  }
  
  /**
   * @version 3.1
   */
  @Override // ControllerBasic
  public void close() throws Exception {
    super.close();

    // reset firsttime
    firstTime = true;
  }
}
