package jda.test.search.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.search.units.RegisterClass;
import jda.test.search.units.SearchBasic;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        Spacer.class,
        SearchBasic.class
        })
public class SearchBasicSuite {
  //
}
