package jda.mosa.controller.assets.helper;

import jda.modules.common.exceptions.ApplicationException;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.InfoCode;
import jda.modules.security.authentication.controller.SecurityController;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.assets.util.AppState;
import jda.util.SwTk;
import jda.util.events.StateChangeListener;

/**
 * @overview 
 *  A helper class to handle security-related events which are raised by {@link SecurityController}
 *  operations (e.g. login, logout).
 *  
 * @author dmle
 */
public class SecurityStateListener implements StateChangeListener {

  private ControllerBasic mainCtl;

  private boolean isInit;
  
  /**
   * @requires 
   *  mainCtl is the main controller
   */
  public SecurityStateListener(ControllerBasic mainCtl) {
    this.mainCtl = mainCtl;
    isInit = true;
  }
  
  @Override
  public void stateChanged(Object src, AppState state, String messages,
      Object... data) {
    if (state == AppState.LoggedIn) {
      // logged in
      // if this is the first login then do nothing; otherwise do postLogin
      if (isInit) {
        isInit = false;
      } else {
        // subsequent logins
        // update stuff and run default module after log in
        try {
          
          // v3.1: start splash screen (if available)
          SwTk.startSplashScreen(mainCtl); 
          
          SwTk.createAllFunctionalModules(mainCtl);
  
          mainCtl.postLogin();
          
          mainCtl.runDefaultModule();
        } catch (ApplicationException | ApplicationRuntimeException e) {
          InfoCode code = (e instanceof ApplicationException) ? 
              ((ApplicationException)e).getCode() :
                ((ApplicationRuntimeException)e).getCode();
          mainCtl.displayError(code, e, "");
        }
      }
    } else {
      // logged out
      mainCtl.postLogout();
    }
  }

  @Override
  public AppState[] getStates() {
    return new AppState[] {
      AppState.LoggedIn, 
      AppState.LoggedOut
    };
  }
  
} // end SecurityStateListener

