package jda.test.dodm.objectpool.suites.coursemodules;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.dodm.objectpool.units.coursemodule.GetModuleOidRange;
import jda.test.dodm.objectpool.units.coursemodule.RegisterModuleClasses;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
          RegisterModuleClasses.class,
          GetModuleOidRange.class
        })
public class IdRangeModuleSuite {
  // define a test suite
}
