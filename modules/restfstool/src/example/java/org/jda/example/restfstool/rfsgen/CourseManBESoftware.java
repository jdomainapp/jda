package org.jda.example.restfstool.rfsgen;

import static org.jda.example.restfstool.rfsgen.Resources.*;

import jda.modules.restfstool.backend.BESoftware;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class CourseManBESoftware {
  
  public static void main(String[] args) {
    new BESoftware().run(
        backendTargetPackage, 
        backEndAppCls, 
        model
        );
  }
}
