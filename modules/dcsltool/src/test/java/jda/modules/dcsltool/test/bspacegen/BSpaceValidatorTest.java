/**
 * 
 */
package jda.modules.dcsltool.test.bspacegen;

import java.util.Collection;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsltool.behaviourspace.BSpaceTool;
import jda.modules.dcsltool.behaviourspace.generator.BSpaceGen;
import jda.modules.dcsltool.behaviourspace.validator.ValidationReport;

/**
 * 
 * @overview
 *  Test {@link BSpaceGen}
 *  
 * @author dmle
 *
 */
public class BSpaceValidatorTest {

  /**
   * @requires 
   *  a 'fresh' set of input source code files are available in the 
   *    folder "${PROJECT_PATH}/src/example/java/vn/com/courseman/bspacegen/output"
   *  by copying from "${PROJECT_PATH}/src/example/java/vn/com/courseman/bspacegen/input"!
   *  
   * @effects 
   *  the above files are updated with behaviour space specifications.
   */
  @Test
  public void doTest() throws Exception {
    // NOTE: Windows need to change path separator to '\\'
    String projectPath = ToolkitIO.getSysDependentPath("/home/dmle/projects/jda/modules/dcsltool/");
    
    String rootSrcPath = projectPath + ToolkitIO.getSysDependentPath("src/test/java/");

    // package of the input source files: must match the srcPkgPath (above)
    String clsPkgName = "org.jda.example.courseman.bspacevalidator";

    String[] clsSimpleNames = { 
        "Student",
//        "Address",
        "CourseModule",
//        "Enrolment"
    };
    
    BSpaceTool tool = BSpaceTool.getInstance("validate", rootSrcPath, clsPkgName, clsSimpleNames);
    
    Object result = tool.exec();
    
    if (clsSimpleNames.length == 1) {
      ValidationReport report = (ValidationReport) result; 
      System.out.println(report);
    } else {
      Collection<ValidationReport> reports = (Collection<ValidationReport>) result;
      
      for (ValidationReport report: reports) {
        System.out.println(report);
      }
    }
  }
}
