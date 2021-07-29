package jda.test.search.units;

import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.NEXT;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.PREV;

import java.util.Collection;

import org.junit.Test;

import jda.mosa.model.Oid;
import jda.test.dodm.DODMTesterWithBrowse.BrowsingStep;
import jda.test.search.TestSearch;

public class SearchAndBrowse extends TestSearch {

  @Test
  public void doTest() {
    System.out.printf("%s:%n",this.getClass().getSimpleName());
    
    TestSearch me = (TestSearch) instance;
    
    // search for objects
    try {
      Collection<Oid> result = me.searchBasic();
          
      // print result
      if (result == null) {
        System.out.println("Result: <no objects matching>");
      } else {
        System.out.printf("Result: %n%s%n", result);
        System.out.println("Browsing...");
        
        //browseFirstToLast(c, result);
        BrowsingStep[] browseSeq = getDefaultBrowsingSequence(result);
        browse(c, result, browseSeq, null);
      }
    } catch (Exception e) {
      System.err.println("An ERROR occured");
      throw new AssertionError("", e);
    }
  }
}
