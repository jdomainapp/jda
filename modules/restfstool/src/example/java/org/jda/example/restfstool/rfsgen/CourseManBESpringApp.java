package org.jda.example.restfstool.rfsgen;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import jda.modules.restfstool.backend.BESpringApp;

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
  public CourseManBESpringApp(Class<?>[] models) {
    super(models);
  }
}
