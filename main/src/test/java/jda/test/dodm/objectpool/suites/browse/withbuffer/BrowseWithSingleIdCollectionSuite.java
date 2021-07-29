package jda.test.dodm.objectpool.suites.browse.withbuffer;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.LoadStudents;
import jda.test.dodm.objectpool.units.browse.withbuffer.BrowseWithSingleIdCollection;
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
        LoadStudents.class,
        Spacer.class,
        BrowseWithSingleIdCollection.class
        })
public class BrowseWithSingleIdCollectionSuite {
  // define a test suite
}
