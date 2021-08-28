/**
 * 
 */
package jda.modules.mccltool.test;

import java.nio.file.Path;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.mccl.conceptualmodel.MainCC;
import jda.modules.mccltool.mainmodule.MCCMainGenTool;


/**
 * 
 * @overview
 *  Test {@link MCCMainGenTool}
 *  
 * @author dmle
 *
 */
public class MCCMainGenTest {

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
      Path rootSrcP = ToolkitIO.getPath("/home","dmle","projects","jda","modules","mccl","src", "test", "java");
      rootSrcPath = rootSrcP.toString();
    }

    String domainName = "CourseMan";
    String pkgName = "org.jda.example.courseman.software";
    
    MCCMainGenTool tool = MCCMainGenTool.getInstance(domainName, rootSrcPath, pkgName);
    
    MainCC m = (MainCC) tool.exec();
    
    System.out.println(m);
  }
}
