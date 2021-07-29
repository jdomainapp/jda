package jda.test.security.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.security.units.PrintDataMemory;

/**
 * The test case for normal usage
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadData.class,
        PrintDataMemory.class,
        })
public class UsagePrintDataMemory {
  // define a test suite
}
