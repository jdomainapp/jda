package jda.modules.restfstool.benchmark;

import java.util.concurrent.TimeUnit;

import com.google.caliper.runner.CaliperMain;
import com.google.caliper.util.ShortDuration;

public class Main {
	public static void main(String[] args) {
		CaliperMain.main(CourseManRFSGenBenchmark.class, new String[] {"-l", ShortDuration.of(15, TimeUnit.MINUTES).toString()});
	}
}
