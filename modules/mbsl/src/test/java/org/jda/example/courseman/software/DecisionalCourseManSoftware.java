package org.jda.example.courseman.software;

import org.jda.example.courseman.software.config.enrolmentmgmt.DecisionalSystemClass;
import org.jda.example.courseman.swclasses.cls2.setup.SystemClass2;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.setup.model.SetUpGen;
import jda.mosa.software.Software;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.aio.SoftwareAio;
import jda.mosa.software.aio.SoftwareStandardAio;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class DecisionalCourseManSoftware {
  
  public static void main(String[] args) {
    try {
      // create a default software needed to run the activity
      Software sw = SoftwareFactory.createSoftwareWithMemoryBasedConfig(
          DecisionalSystemClass.class);
      
      // run the software to show the main GUI
      System.out.printf("Running %s...%n", sw);
      
      sw.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    /* new code: but requires SystemClass to contain all dependency modules
    final Class SystemCls = DecisionalSystemClass.class;
    
    SoftwareAio sw = SoftwareFactory.createSoftwareAioWithMemoryBasedConfig(SystemCls);
    
    try {
      sw.exec(args);
    } catch (NotPossibleException e) {
      e.printStackTrace();
      System.exit(1);
    }
    */
  }
}
