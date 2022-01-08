/**
 * 
 */
package jda.modules.dcsltool.test.bspacegen;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsltool.behaviourspace.BSpaceTool;
import jda.modules.dcsltool.behaviourspace.generator.BSpaceGen;
import jda.modules.dcsltool.behaviourspace.generator.BSpaceGenTool;

/**
 * 
 * @overview
 *  Test {@link BSpaceGen}
 *  
 * @author dmle
 *
 */
public class BSpaceGenTest {

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
    
    String rootSrcPath = projectPath + ToolkitIO.getSysDependentPath("src/test/java");

    // package of the input source files: must match the srcPkgPath (above)
    String clsPkgName = "org.jda.example.courseman.bspacegen.output";

    String[] clsSimpleNames;
    
    clsSimpleNames = new String[] { 
        "Student",
        "Address",
        "CourseModule",
        "Enrolment"
    };
    
    
    BSpaceTool tool = BSpaceTool.getInstance("gen", rootSrcPath, clsPkgName, clsSimpleNames);
    tool.exec();
    
    // generate sub-classes AFTER
    clsSimpleNames = new String[] { 
        "ElectiveModule",
        "CompulsoryModule",
    };

    
    tool = BSpaceTool.getInstance("gen", rootSrcPath, clsPkgName, clsSimpleNames);
    tool.exec();
    
  }
}
