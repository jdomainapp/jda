package jda.modules.mbsl.test.model;

import java.util.List;

import org.jda.example.courseman.modules.enrolmentmgmt.decisional.model.EnrolmentMgmt;
import org.jda.example.courseman.modules.enrolmentmgmt.decisional.model.control.DHelpOrSClass;
import org.jda.example.courseman.modules.helprequest.model.HelpRequest;
import org.jda.example.courseman.modules.sclass.model.SClass;
import org.jda.example.courseman.modules.student.model.Student;
import org.junit.Test;

import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.dodm.dsm.DSMFactory;
import jda.modules.mbsl.model.ActivityModel;
import jda.modules.mccl.conceptmodel.dodm.DODMConfig;
import jda.util.SwTk;

/**
 * @overview 
 *  Test creating an {@link ActivityModel}
 *  
 * @author Duc Minh Le (ducmle)
 */
public class ActivityModelTest {
  
  @Test
  public void testName() throws Exception {
    createActivityModel();
  }
  
  public ActivityModel createActivityModel() throws Exception {
    // create a memory-based config
    DODMConfig cfg = SwTk.createMemoryBasedConfiguration("Test").getDodmConfig();
    DSMBasic dsm = DSMFactory.createDSM(cfg);
    
    System.out.println("Creating activity model...");
    ActivityModel m = new ActivityModel("Enrolment Management", dsm);
    
    Class actCls = EnrolmentMgmt.class;

    // register activity class
    dsm.registerClass(actCls);
    
    m.setActivityCls(actCls);
    
    System.out.println(m);
    
    List<Class> initClses = m.getInitClses();
    System.out.printf("Initial classes: %s%n", initClses);
    
    return m;
  }
}
