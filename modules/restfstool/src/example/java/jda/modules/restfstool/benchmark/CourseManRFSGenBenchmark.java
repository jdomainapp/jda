package jda.modules.restfstool.benchmark;

import java.io.File;

import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import com.hanu.courseman.SCCCourseMan;

import jda.modules.restfstool.RFSSoftware;

/**
 * The software generator for CourseManApp.
 * 
 * @author ducmle
 */
public class CourseManRFSGenBenchmark {
	@Param({ "1" })
	int number;

	@Benchmark
	void createApp(int reps) {
		int number = this.number;
		for (int i = 0; i < reps; i++) {
			for (int n = 0; i < number; i++) {
				Class scc = SCCCourseMan.class;
				new RFSSoftware(scc).init().generate().run();
			}
		}
	}
}
