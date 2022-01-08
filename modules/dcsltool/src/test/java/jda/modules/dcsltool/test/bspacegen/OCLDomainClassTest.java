package jda.modules.dcsltool.test.bspacegen;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsltool.behaviourspace.generator.BSpaceGenTool;

/**
 * @overview 
 *  Automatically generate OCL expressions for operations of a domain class.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class OCLDomainClassTest {
  @Test
  public void run() {
    String pkgFQN = "org.jda.example.courseman.bspacegen.output";
    String clsName  = 
        "Student"
        //"Enrolment"
        //"Address"
        //"CourseModule"
        ;
    
    String rootSrcPath = ToolkitIO.getSysDependentPath("/data/projects/jda/modules/dcsltool/src/test/java");

    System.setProperty("rootSrcPath", rootSrcPath);
    try {
      BSpaceGenTool.main(new String[] {
          pkgFQN, clsName
      });
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
