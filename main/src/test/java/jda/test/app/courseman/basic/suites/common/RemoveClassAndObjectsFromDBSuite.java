package jda.test.app.courseman.basic.suites.common;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.RemoveClassAndObjectsFromDB;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RemoveClassAndObjectsFromDB.class,
        })
public class RemoveClassAndObjectsFromDBSuite {
  // define a test suite
}
