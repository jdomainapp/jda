package jda.test.dodm.objectpool.units.browse.withbuffer;

import java.util.Collection;

import org.junit.BeforeClass;
import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.model.basic.City;
import jda.test.model.basic.Student;

public class BrowseAndAdd extends DODMTesterWithBrowse {

  @BeforeClass
  public static void data() throws DataSourceException { instance.initData(); }
  
  @Test
  public void doTest() throws Exception { 
    
//    // initialise object buffer
//    initObjectBuffer();
//    
//    // an initial browsing sequence
//    BrowsingStep[] browseSeq = {
//        FIRST,
//        LAST,
//    };
//    
//    Boolean[] expected = null;
//    browse(browseSeq, expected);
    
    /*  
     * add a few new objects and re-test browsing
     */
    // create object
    Collection<City> cities = instance.getData().get(City.class);
    
//    DomainSchema schema = instance.getDomainSchema();
    
    Class c = Student.class;
    
    Student o = new Student("New student","1/1/1990",getObject(cities,0),"new@gmail.com");
    
    System.out.printf("Created object %s%n", o);
    
    browseAndAdd(c, o);
    
//    Oid id = schema.addObject(o);
//    
//    System.out.printf("Added %s -> %s%n", id, o);
//    
//    // add object to buffer
//    addToBuffer(id,o);
//    
//    // browse again just to check the new object
//    browseSeq = new BrowsingStep[] {
//      LAST, // the new object 
//      PREV,
//      NEXT,
//      PREV,
//      PREV,
//      PREV 
//    };
//
//    expected = new Boolean[] {
//        TRUE,
//        TRUE,
//        TRUE,
//        TRUE,
//        FALSE,
//        FALSE
//    };
//    
//    browse(browseSeq, expected);
  }
}
