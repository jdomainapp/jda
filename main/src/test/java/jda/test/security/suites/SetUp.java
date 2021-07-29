package jda.test.security.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.security.units.AddAndCreate;
import jda.test.security.units.PrintDataDB;
import jda.test.security.units.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        //RemoveAndDeleteFromDB.class,
        RegisterClass.class,
        //Spacer.class,
        //AddAndCreate.class,
        Spacer.class,
        PrintDataDB.class,
        })
public class SetUp {
  // define a test suite
}

