package org.jda.example.restfstool.rfsgen;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import jda.modules.restfstool.backend.BESpringApp;
import jda.modules.restfstool.config.RFSGenConfig;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
@SpringBootApplication
@ComponentScan(basePackages = {
  "com.hanu.courseman.backend"
})
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
