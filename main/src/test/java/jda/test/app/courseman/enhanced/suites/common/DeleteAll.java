package jda.test.app.courseman.enhanced.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.enhanced.units.common.RemoveAndDeleteFromDB;

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
