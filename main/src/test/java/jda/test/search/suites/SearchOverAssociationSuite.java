package jda.test.search.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import jda.test.app.courseman.basic.units.common.Spacer;
import jda.test.dodm.objectpool.units.LoadSClass;
import jda.test.search.units.RegisterClass;
import jda.test.search.units.SearchOverAssociation;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RegisterClass.class,
        Spacer.class,
        LoadSClass.class,
        Spacer.class,
        SearchOverAssociation.class
        })
public class SearchOverAssociationSuite {
  //
}
