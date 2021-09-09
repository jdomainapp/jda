package jda.modules.restfstool.benchmark;

import java.io.File;

import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import com.hanu.courseman.SCCCourseMan;

import jda.modules.common.io.ToolkitIO;
import jda.modules.restfstool.RFSSoftware;

/**
 * The software generator for CourseManApp.
 * 
 * @author ducmle
 */
public class CourseManRFSGenBenchmark {
  // TODO: 
  // - find out how to specify this number for one SCC, e.g. number = 1 -> SCC1, ... 
  // - how to automate the run for all the SCC1, ..., SCCn
//	@Param({ "1" })
//	int number;

	@Param()
	String scc; 
	
	@Benchmark
	void createApp(int reps) {
		for (int i = 0; i < reps; i++) {
		  // TODO: 
		  // - Load scc class
		  // - run RFSSoftware with this SCC
			Class scc = SCCCourseMan.class;
			new RFSSoftware(scc).init().generate().run();
		}
	}
}
