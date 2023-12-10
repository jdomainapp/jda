package org.jda.example.coursemanrestful.software;

import ch.qos.logback.classic.Logger;
import jda.modules.mosar.software.RFSoftware;
import org.jda.example.coursemanrestful.software.config.SCCCourseMan;
import org.jda.example.coursemanrestful.software.config.SCCCourseManDerby;
import org.jda.example.coursemanrestful.utils.DToolkit;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * The software generator for CourseManApp.
 * @author ducmle
 */
public class CourseManRFSGen {

  private static final Logger logger = (Logger) LoggerFactory.getLogger("CourseManRFS");

  public static Class getSCCFromEnv() {
    Optional<String> appType = DToolkit.getEnvProp("app.type");
    Class scc;
    if (appType.isEmpty() || appType.get().equals("standard")) {
      logger.info("Creating a standard DOM Software (using PostgreSQL database)...");
      scc = SCCCourseMan.class;
    } else {
      logger.info("Creating a default DOM Software (with embedded Derby database)...");
      scc = SCCCourseManDerby.class;
    }

    return scc;
  }

    public static void main(String[] args) {
      Class scc = getSCCFromEnv(); //SCCCourseManDerby.class;

      new RFSoftware(scc)
        .init()
        .generate()
        ;
    }
}
