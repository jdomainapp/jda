package jda.modules.mbsl.test.model;

import org.jda.example.courseman.modules.enrolmentmgmt.decisional.model.EnrolmentMgmt;
import org.jda.example.courseman.software.config.enrolmentmgmt.DecisionalSystemClass;
import org.junit.Test;

import jda.modules.mbsl.model.ActivityModel;
import jda.mosa.module.Module;
import jda.mosa.module.ModuleService;
import jda.mosa.software.Software;
import jda.mosa.software.SoftwareFactory;

/**
 * @overview 
 *  Test executing an {@link ActivityModel}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ActivityModelExecTest {
  @Test
  public void testName() throws Exception {
    // the activity model
    ActivityModel actModel = (new ActivityModelTest()).createActivityModel();
    
    // create a default software needed to run the activity
    Software sw = SoftwareFactory.createSoftwareWithMemoryBasedConfig(DecisionalSystemClass.class);
    
    // run the software to show the main GUI
    System.out.printf("Running %s...%n", sw);
    
    sw.run();
    
    // now run the activity 
    Module actModule = sw.getNonMainModule(EnrolmentMgmt.class);
    
    // first display it on the GUI
    actModule.getController().showGUIAndWait();
    
    // now execute the activity model
    ModuleService mService = actModule.getDefaultService();
    Object[] args = null;
    actModel.exec(mService, args);
  }
  
  public static void main(String[] args) throws Exception {
    new ActivityModelExecTest().testName();
  }
}
