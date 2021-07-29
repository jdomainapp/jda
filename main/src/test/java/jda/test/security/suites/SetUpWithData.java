package jda.test.security.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.security.units.CreateObjects;
import jda.test.security.units.PrintDataDB;
import jda.test.security.units.RegisterClass;


/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        Spacer.class,
        //DeleteObjects.class,
        //Spacer.class,
        CreateObjects.class,
        Spacer.class,
        PrintDataDB.class,
        })
public class SetUpWithData {
  // define a test suite
}
