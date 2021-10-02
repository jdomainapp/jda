package org.jda.example.courseman.software;

import org.jda.example.courseman.software.config.enrolmentmgmt.ForkedAndJoinedSystemClass;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.mosa.software.Software;
import jda.mosa.software.SoftwareFactory;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ForkedAndJoinedCourseManSoftware {
  
  public static void main(String[] args) {
    try {
      // create a default software needed to run the activity
      Software sw = SoftwareFactory.createSoftwareWithMemoryBasedConfig(ForkedAndJoinedSystemClass.class);
      
      // run the software to show the main GUI
      System.out.printf("Running %s...%n", sw);
      
      sw.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
