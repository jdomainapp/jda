package jda.modules.restfstool.benchmark;

import java.util.concurrent.TimeUnit;

import com.google.caliper.runner.CaliperMain;
import com.google.caliper.util.ShortDuration;

public class Main {
	public static void main(String[] args) {
	  // 1. Loop through each SCC
	  // 2. each iteration for SCCi 
	  //   calls CaliperMain.main(CourseManRFSGenBenchmark.class, SCCi)
	  
	  int count = 100;
	  
	  String prefix = "jda.modules.restfstool.test.performance.software.config";
	  for (int i = 1; i <= count; i++) {
	    String scc = prefix + "SCC" + i;
	    
	    CaliperMain.main(CourseManRFSGenBenchmark.class, 
	        // TODO: add value for parameter "scc"
	        new String[] {"-l", ShortDuration.of(15, TimeUnit.MINUTES).toString()});
	  }
	  
	}
}
