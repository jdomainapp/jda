package jda.test.security.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.security.units.PrintDataDB;

/**
 * The test case for normal usage
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        PrintDataDB.class,
        })
public class UsagePrintDataDB {
  // define a test suite
}
