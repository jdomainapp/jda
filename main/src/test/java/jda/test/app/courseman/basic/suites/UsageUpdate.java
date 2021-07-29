package jda.test.app.courseman.basic.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.suites.common.LoadObjectsSuite;
import jda.test.app.courseman.basic.units.UpdateCompoundDomainTypeKeyObject;
import jda.test.app.courseman.basic.units.UpdateObject;
import jda.test.app.courseman.basic.units.common.PrintData;

/**
 * The test case for normal usage
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        LoadObjectsSuite.class,
        UpdateObject.class,
        UpdateCompoundDomainTypeKeyObject.class,
        PrintData.class,
        })
public class UsageUpdate {
  // define a test suite
}
