package jda.mosa.software;

import jda.modules.setup.model.SetUpBasic;
import jda.mosa.controller.ControllerBasic;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 */
public class SoftwareLauncher extends ApplicationLauncher {

  private Software sw;

  /**
   * @effects 
   *
   * @version 
   */
  public SoftwareLauncher(Software sw) {
    super(sw.getSetUp());
    this.sw = sw;
  }

  /**
   * @requires <tt>sw</tt> has been created with all the modules.
   * 
   * @effects 
   *  launches this.{@link #sw}.
   *  
   * @version 
   * - 4.0: <br>
   * - v5.2: support startUpModules
   */
  public void launch() {

    ControllerBasic mainCtl = sw.getMainModule().getController();
    
    mainCtl.showGUI();
    
    mainCtl.postCreateGUI(); 
    
    // v2.7.3: update main GUI again after creating the functional modules 
    mainCtl.postCreateFunctionalModules();
    
    if (mainCtl.isSecurityEnabled())
      mainCtl.postLogin();
    else 
      mainCtl.startUpModules(); // v5.2
      
    if (mainCtl.isLoggedIn())
      mainCtl.runDefaultModule();
  }
}
