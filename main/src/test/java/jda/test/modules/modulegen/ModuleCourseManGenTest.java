/**
 * 
 */
package jda.test.modules.modulegen;

import org.junit.Test;

import jda.modules.jdatool.modulegen.ModuleClassGen;
import jda.test.dodm.DODMMemoryBasedTester;

/**
 * @overview 
 *
 * @author dmle
 *
 * @version 
 */
public class ModuleCourseManGenTest extends DODMMemoryBasedTester {
  @Test
  public void moduleGenTest() throws Exception {
    ModuleClassGen moduleGen = new ModuleClassGen(getDODM());
    String packageName = "org.jda.example.coursemanGEN";
    String appName = "CourseMan";
    Class moduleCls = moduleGen.generateMainModuleClass(appName, packageName);
    
    String sourceCode = moduleGen.getModuleClassSource();
    
    printf("Module class generator: %n  domain class: %s%n  -> module class: %s%n%n  Source code: %n%s", 
        appName, moduleCls, sourceCode);
  }
}
