package jda.modules.restfstool.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.caliper.Benchmark;
import com.google.caliper.Param;

import jda.modules.common.Toolkit;
import jda.modules.restfstool.RFSSoftware;

/**
 * The software generator for CourseManApp.
 * 
 * @author ducmle
 */
public class CourseManRFSGenBenchmark {
  @Param()
  String sccFqn;

  private static int runCounter = 0;

  private static Logger logger = (Logger) LoggerFactory
      .getLogger("module.restfstool.benchmark");

  @Benchmark
//  @Macrobenchmark
  void runScenario1A(int reps) {
    logger.info("run#: " + (++runCounter));
    logger.info("reps: " + reps);

    // using reps
    for (int i = 0; i < reps; i++) {
      logger.info(String.format("rep#: %d/%d (run#%d)", i+1, reps, runCounter));
      // - Load scc class
      // - run RFSSoftware with this SCC
      Class scc = Toolkit.loadClass(sccFqn);
      new RFSSoftware(scc).init().generate();
    }
  }

  // @Benchmark
  void runScenario1B(int reps) {
    logger.info("run#: " + (++runCounter));
    logger.info("benchmark repetitions: " + reps);

    // using reps
    for (int i = 0; i < reps; i++) {
      // - Load scc class
      // - run RFSSoftware with this SCC
      Class scc = Toolkit.loadClass(sccFqn);
      new RFSSoftware(scc).init().generate().run();
    }
  }

// for testing ONLY  
//  @Benchmark
//  void timeNanoTime(int reps) {
//    logger.info("benchmark repetitions: " + reps);
//    for (int i = 0; i < reps; i++) {
//      System.nanoTime();
//    }
//  }
}
