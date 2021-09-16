package org.jda.example.restfstool.rfsgen;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import jda.modules.restfstool.backend.BESpringApp;
import jda.modules.restfstool.config.RFSGenConfig;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version
 * @deprecated do not use. Use {@link BESpringApp} instead. 
 */
@SpringBootApplication
//@ComponentScan(basePackages = {
////  "com.hanu.courseman.backend"
//    "jda.modules.restfstool.test.performance.backend"
//})
@Deprecated
public class CourseManBESpringApp extends BESpringApp {
  
  /**
   * @effects 
   *
   * @version 
   */
  public CourseManBESpringApp() {
    super();
  }

  public CourseManBESpringApp(RFSGenConfig cfg) {
    super(cfg);
  }
}
