package org.jda.example.courseman.software;

import org.jda.example.courseman.software.config.enrolmentmgmt.SequentialSystemClass;

import jda.mosa.software.Software;
import jda.mosa.software.SoftwareFactory;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class SequentialCourseManSoftware {
  
  public static void main(String[] args) {
    // create a default software needed to run the activity
    try {
      Software sw = SoftwareFactory.createSoftwareWithMemoryBasedConfig(SequentialSystemClass.class);
      
      // run the software to show the main GUI
      System.out.printf("Running %s...%n", sw);
      
      sw.run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
