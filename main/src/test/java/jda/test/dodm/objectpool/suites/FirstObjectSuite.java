package jda.test.dodm.objectpool.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.suites.common.LoadObjectsSuite;
import jda.test.dodm.objectpool.units.GetFirstObject;
import jda.test.dodm.objectpool.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        Spacer.class,
        LoadObjectsSuite.class,
        Spacer.class,
        GetFirstObject.class
        })
public class FirstObjectSuite {
  // define a test suite
}
