package jda.modules.restfstool.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.Toolkit;
import jda.modules.restfstool.RFSSoftware;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * The software generator for CourseManApp.
 * 
 * @author ducmle
 */
public class CourseManRFSGenBenchmark {
//  @Param()
//  String sccFqn;

  private static int runCounter = 0;

  private static Logger logger = (Logger) LoggerFactory
      .getLogger("module.restfstool.benchmark");

  @Benchmark
  public void runBenchmarh() {
    logger.info("run#: " + (++runCounter));

      Class scc = Toolkit.loadClass("");
      new RFSSoftware(scc).init().generate();
  }
  
  public static void main(String[] args) throws RunnerException {
      Options opt = new OptionsBuilder()
              .include(CourseManRFSGenBenchmark.class.getSimpleName())
//              .param("arg", "41", "42") // Use this to selectively constrain/override parameters
              .warmupIterations(0)
              .measurementIterations(5)
              .addProfiler(HeapMemoryProfiler.class)
              .forks(1)
              .build();

      new Runner(opt).run();
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
