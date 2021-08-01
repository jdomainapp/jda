package org.jda.example.restfstool.rfsgen;

import jda.modules.restfstool.BackEndSoftware;
import static org.jda.example.restfstool.rfsgen.Resources.*;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManBESoftware {
  
  public static void main(String[] args) {
    new BackEndSoftware().run(
        model, auxModel,
        backendTargetPackage);
  }
}
