package org.jda.example.mosar.benchmark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.Toolkit;
import jda.modules.mosar.software.RFSoftware;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * The software generator for CourseManApp.
 * 
 * @author ducmle
 */
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class CourseManRFSGenBenchmark {
  @Param({"jda.modules.restfstool.test.performance.software.courseman1.config.SCC1"})
  public String sccFqn;

	private static int runCounter = 0;

	private static Logger logger = (Logger) LoggerFactory.getLogger("module.restfstool.benchmark");

	@Benchmark
	public void runBenchmarh() {
		logger.info("run#: " + (++runCounter));
		logger.info("sccFqn: "+sccFqn);
		Class scc = Toolkit.loadClass(sccFqn);
		new RFSoftware(scc).init().generate()
//      .run()
		;
	}

	public static void main(String[] args) throws RunnerException {
		int count = 1;

		String prefix = "jda.modules.restfstool.test.performance.software.courseman%d.config.SCC1";
		for (int i = 1; i <= count; i++) {
			String scc = String.format(prefix, i);
			Options opt = new OptionsBuilder().include(CourseManRFSGenBenchmark.class.getSimpleName())
						  .param("sccFqn", scc)
						  .warmupIterations(0)
						  .measurementIterations(5)
						  .addProfiler(HeapMemoryProfiler.class)
						  .forks(1)
						  .build();

			new Runner(opt).run();
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
