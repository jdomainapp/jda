/**
 * 
 */
package jda.modules.mccltool.test;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mccl.conceptualmodel.MCCModel;
import jda.modules.mccltool.MCCGenTool;


/**
 * 
 * @overview
 *  Test {@link MCCGen}
 *  
 * @author dmle
 *
 */
public class MCCGenTest {

  //NOTE: Windows need to change path separator to '\\'
  private String rootSrcPath;
  private MCCModel mccModel;
  
  @Before
  public void init() {
    rootSrcPath = System.getProperty("rootSrcPath");
    
    if (rootSrcPath == null) {
//      Path rootSrcP = ToolkitIO.getPath("/home","ducmle","projects","jda","modules","mccl","src", "test", "java");
      File rootSrcP = ToolkitIO.getMavenRootSrcPath(getClass(), false);
      // use mccl test source
      rootSrcPath = rootSrcP.getPath().replace("mccltool", "mccl");
    }
    
    mccModel = new MCCModel(new SourceModel(rootSrcPath));

  }
  
  /**
   * @requires 
   *  a 'fresh' set of input source code files are available in folder "${PROJECT_PATH}/src/example/java/vn/com/courseman/modelgen"
   *  by copying from "${PROJECT_PATH}/src/example/java/vn/com/courseman/modelgen/input"!
   *  
   * @effects 
   *  the above files are updated with behaviour space specifications.
   */
  @Test
  public void runCourseModule() {
    
//    String[] fqns = {
//        "org.jda.example.courseman.modulesgen.enrolmentmgmt.model.EnrolmentMgmt",
//        "org.jda.example.courseman.modulesgen.student.model.Student",
//    };

    // MCC of superclass
    String[] fqns = {
        "org.jda.example.courseman.modulesgen.coursemodule.model.CourseModule"
    };
    
    // MCCs of sub-classes (after)
//    String[] fqns = {
//      "org.jda.example.courseman.modulesgen.coursemodule.model.ElectiveModule",
//      "org.jda.example.courseman.modulesgen.coursemodule.model.CompulsoryModule",
//    };
    
    for (String fqn : fqns) {
      MCC m = mccGenFor(mccModel, rootSrcPath, fqn);
      
      System.out.println(m);
    }
  }

  @Test
  public void runStudent() {
    String[] fqns = {
        "org.jda.example.courseman.modulesgen.student.model.Student"
    };
    
    for (String fqn : fqns) {
      MCC m = mccGenFor(mccModel, rootSrcPath, fqn);
      
      System.out.println(m);
    }
  }
  
  /**
   * @effects 
   *  run for Student
   */
  private static MCC mccGenFor(final MCCModel mccModel, final String rootSrcPath, 
      final String clsFQN) throws NotPossibleException {
    MCCGenTool tool = MCCGenTool.getInstance(mccModel, rootSrcPath, clsFQN);
    
    MCC m = (MCC) tool.exec();
    
    return m;
  }
  
}
