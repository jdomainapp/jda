package org.jda.example.courseman.software;

import jda.modules.setup.model.Cmd;
import jda.modules.setup.model.SetUpGen;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.aio.SoftwareAio;
import org.jda.example.courseman.software.config.SWC1;
import org.jda.example.courseman.software.config.SWC2_Embedded;

/**
 * @overview 
 *  Standard ProcessMan software.
 *  
 *  <p>Note: 
 *  Informative logging information can be observed by setting the VM argument:<br>
 *  <tt>-Dlogging=true</tt> 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseMan {

//  public ProcessManSoftware(Class setUpCls, Class systemCls) {
//    super(setUpCls, systemCls);
//  }
  
  /**
   * @requires 
   *  args.length > 0 /\ args[0] = name of a {@link Cmd}
   */
  public static void main(String[] args) {
    if (args.length == 0) {
      args = new String[] {"run"};
    }

    mainEmbedded(args);
    
//    mainClientServer(args);
  }
  
  private static void mainClientServer(String[] args) {
 // software config class
    final Class SwCfgCls = SWC1.class // PostgreSQL
        ;
    // setup class (same)
    final Class SetUpCls = SetUpGen.class;
    
//    SoftwareAio sw = new SoftwareStandardAio(SetUpCls, SwCfgCls);
    SoftwareAio sw = SoftwareFactory.createSoftwareAioWithMemoryBasedConfig(SwCfgCls);

    try {
      sw.exec(args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  private static void mainEmbedded(String[] args) {
 // software config class
    final Class SwCfgCls = 
        SWC2_Embedded.class // Embedded (Derby)
        ;
    // setup class (same)
    final Class SetUpCls = SetUpGen.class;
    
//    SoftwareAio sw = new SoftwareStandardAio(SetUpCls, SwCfgCls);
    SoftwareAio sw = SoftwareFactory.createSoftwareAioWithMemoryBasedConfig(SwCfgCls);

    try {
      sw.exec(args);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
