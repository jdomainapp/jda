/**
 * 
 */
package jda.test.modules.modulegen;

import org.junit.Test;

import jda.modules.jdatool.modulegen.ModuleClassGen;
import jda.test.dodm.DODMMemoryBasedTester;
import jda.test.model.examples.courseman.Enrolment;

/**
 * @overview 
 *
 * @author dmle
 *
 * @version 
 */
public class ModuleEnrolmentGenTest extends DODMMemoryBasedTester {
  @Test
  public void moduleGenTest() throws Exception {
    Class domainCls = Enrolment.class;
    
    registerClass(domainCls, false);
    
    ModuleClassGen moduleGen = new ModuleClassGen(getDODM());
    String packageName = "org.jda.example.coursemanGEN";
    Class moduleCls = moduleGen.generateFunctionalModuleClass(domainCls, packageName);
    
    String sourceCode = moduleGen.getModuleClassSource();
    
    printf("Module class generator: %n  domain class: %s%n  -> module class: %s%n%n  Source code: %n%s", 
        domainCls, moduleCls, sourceCode);
  }
}
