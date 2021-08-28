/**
 * 
 */
package jda.modules.mccltool.test;

import java.nio.file.Path;

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

  /**
   * @requires 
   *  a 'fresh' set of input source code files are available in folder "${PROJECT_PATH}/src/example/java/vn/com/courseman/modelgen"
   *  by copying from "${PROJECT_PATH}/src/example/java/vn/com/courseman/modelgen/input"!
   *  
   * @effects 
   *  the above files are updated with behaviour space specifications.
   */
  @Test
  public void run() {
    // NOTE: Windows need to change path separator to '\\'
    String rootSrcPath = System.getProperty("rootSrcPath");
    
    if (rootSrcPath == null) {
      Path rootSrcP = ToolkitIO.getPath("/home","ducmle","projects","jda","modules","mccl","src", "test", "java");
      rootSrcPath = rootSrcP.toString();
    }
    
//    String[] fqns = {
//        "org.jda.example.courseman.modulesgen.enrolmentmgmt.model.EnrolmentMgmt",
//        "org.jda.example.courseman.modulesgen.student.model.Student",
//    };
    
    // MCCs of sub-classes (after)
    String[] fqns = {
      "org.jda.example.courseman.modulesgen.coursemodule.model.ElectiveModule",
      "org.jda.example.courseman.modulesgen.coursemodule.model.CompulsoryModule",
    };
    
    MCCModel mccModel = new MCCModel(new SourceModel(rootSrcPath));
    
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
