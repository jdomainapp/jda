package jda.test.search.units;

import java.util.Collection;

import org.junit.Test;

import jda.mosa.model.Oid;
import jda.test.search.TestSearch;

public class SearchBasic extends TestSearch {

  @Test
  public void doTest() {
    System.out.printf("%s:%n",this.getClass().getSimpleName());
    
    TestSearch me = (TestSearch) instance;
    try {
      
      Collection<Oid> result = me.searchBasic();
          
      // print result
      if (result == null) {
        System.out.println("Result: <no objects matching>");
      } else {
        System.out.printf("Result: %n%s%n", result);
      }
    } catch (Exception e) {
      System.err.println("An ERROR occured");
      throw new AssertionError("", e);
    }  
  }
}
