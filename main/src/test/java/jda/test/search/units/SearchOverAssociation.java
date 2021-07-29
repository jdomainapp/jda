package jda.test.search.units;

import java.util.Collection;

import org.junit.Test;

import jda.mosa.model.Oid;
import jda.test.dodm.DODMTesterWithBrowse.BrowsingStep;
import jda.test.search.TestSearch;

public class SearchOverAssociation extends TestSearch {

  @Test
  public void doTest() {
    System.out.printf("%s:%n",this.getClass().getSimpleName());
    
    TestSearch me = (TestSearch) instance;
    try {
      
      Collection<Oid> result = me.searchOverAssociation();
          
      // print result
      if (result == null) {
        System.out.println("Result: <no objects matching>");
      } else {
        System.out.printf("Result: %n%s%n", result);
        System.out.println("Browsing...");
        
        BrowsingStep[] browseSeq = getDefaultBrowsingSequence(result);
        browse(c, result, browseSeq, null);
      }
    } catch (Exception e) {
      System.err.println("An ERROR occured");
      throw new AssertionError("", e);
    }  
  }
}
