package jda.test.dodm.objectpool.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.dodm.objectpool.units.GetStudentOidRange;
import jda.test.dodm.objectpool.units.common.RegisterClass;

/**
 * The setup test case
 * @author dmle
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
          RegisterClass.class,
          GetStudentOidRange.class
        })
public class IdRangeStudentSuite {
  // define a test suite
}
