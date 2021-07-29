package org.jda.example.courseman.software.ref;

import org.jda.example.courseman.software.config.ref.CourseManWithScopeDescConfigClass;

import jda.mosa.software.Software;
import jda.mosa.software.SoftwareFactory;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManWithScopeDescSoftware {
  
  public static void main(String[] args) {
    try {
      // create a default software needed to run the activity
      Software sw = SoftwareFactory.createSoftwareWithMemoryBasedConfig(CourseManWithScopeDescConfigClass.class);
      
      // run the software to show the main GUI
      System.out.printf("Running %s...%n", sw);
      
      sw.run();
    } catch (Exception e ) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
