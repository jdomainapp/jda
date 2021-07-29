package jda.test.app.courseman.basic.metadata;

import java.util.List;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.test.app.courseman.basic.CourseManBasicTester;
import jda.test.model.basic.Student;

public class LoadDataSourceConstraintsTest extends CourseManBasicTester {
  
  @Test
  public void doTest() throws DataSourceException {
    DODMBasic schema = instance.getDODM();
    
    //OSM db = schema.getDom().getOsm();
    DOMBasic db  = schema.getDom();
    
    Class c = Student.class;
  
    // assumes c has already been created
    registerClass(c, false);
    
    System.out.println("Domain class: " + c.getSimpleName());
    
    List<String> constraints = db.loadDataSourceConstraints(c); //readDataSourceConstraint(c);
    
    if (constraints == null)
      System.out.println("No constraints");
    else
      System.out.println(constraints);
  }
}
