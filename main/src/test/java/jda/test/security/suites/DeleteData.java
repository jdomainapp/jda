package jda.test.security.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.security.units.DeleteObjects;
import jda.test.security.units.PrintDataDB;


/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadData.class,
        Spacer.class,
        DeleteObjects.class,
        Spacer.class,
        PrintDataDB.class,
        })
public class DeleteData {
  // define a test suite
}
