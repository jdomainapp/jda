package jda.mosa.software;

import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.setup.model.SetUpBasic;
import jda.modules.setup.model.SetUpBasic.MessageCode;
import jda.mosa.controller.ControllerBasic;

/**
 * @overview
 *  A sub-type of {@link ApplicationLauncher} that create and launch an application from the 
 *  modules that are already created in memory (i.e. without needing to load them from the data source).  
 *  
 * @author dmle
 */
public class ApplicationLauncherLight extends ApplicationLauncher {

  public ApplicationLauncherLight(SetUpBasic s) {
    super(s);
  }

  @Override
  public ControllerBasic launch(SetUpBasic su, String lang)
      throws Exception {

    /*v2.7.4: catch exception to display error messages 
    */
    try {
      if (su == null) {
        throw new IllegalArgumentException("Setup object is not specified");
      }
      
      // command line args takes precedence over JVM's arguments
      if (lang != null) {
        System.setProperty("Language", lang);
      }
  
      // these lines differ from the superclass implementation
      /*
      loadBaseConfiguration();
      
      loadApplicationConfiguration();
      */
      
      launchCommon();
      
      // v2.6.4.b: added return of this controller
      return getMainController();
    } catch (IllegalArgumentException ex) {
      ApplicationRuntimeException e = new ApplicationRuntimeException(MessageCode.FAIL_TO_LAUNCH_APPLICATION, ex, new Object[] {""});
      ControllerBasic.displayIndependentError(e);
      throw ex;
    } catch (
        //v3.1 ApplicationException | ApplicationRuntimeException
      Exception e
      )
    {
      ControllerBasic.displayIndependentError(e);
      throw e;      
    }
  }
}
