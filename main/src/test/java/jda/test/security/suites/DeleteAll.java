package jda.test.security.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.security.units.RemoveAndDeleteFromDB;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RemoveAndDeleteFromDB.class,
        })
public class DeleteAll {
  // define a test suite
}
