package jda.modules.patterndom.test.tpc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.dcsl.parser.jtransform.assets.ParamName;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.patterndom.test.dom.mnormaliser.CourseModule;
import jda.modules.patterndom.test.dom.mnormaliser.Student;
import jda.modules.patterndom.transform.tpc.TPCMNormaliser;
import jda.modules.patterndom.transform.tpc.TPCMNormaliser.Params;

/**
 * @overview 
 *  test creating the Entities pattern.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCMNormaliserTest {
  @Test
  public void test() {
    boolean mainSrc = false;
    File rootSrcPath = ToolkitIO.getMavenRootSrcPath(this.getClass(), mainSrc);
    String outputDir;
    if (rootSrcPath == null) {
      // possibly being run from a jar file
      // use the current directory
      String currDir = ToolkitIO.getCurrentDir();
      rootSrcPath = ToolkitIO.getPathAsFile(currDir, "src");
      outputDir = currDir;
    } else {
      // move up one level to get output dir
      outputDir = ToolkitIO.getMavenProjectRootPath(rootSrcPath);
    }
    
    System.out.println("Root src path: " + rootSrcPath);
    
    outputDir = ToolkitIO.touchPath(outputDir, "output");
    System.out.println("Root output path: " + outputDir);
    
    Dom dom = new Dom(rootSrcPath.getPath());
    dom.setOutputDir(outputDir);
    System.out.println("DOM...initialised");

    TPC transfProc = new TPCMNormaliser();
    System.out.println(transfProc+"...initialised");

    JsonObject config = ToolkitIO.readJSonObjectFile(this.getClass(), 
        "pmap-mnormaliser.json");

    transfProc.exec(dom, config);
    System.out.println("TPC...executed");

    System.out.println("DOM...saved");

    // print result
//    String c1Fqn = config.getString(Params.C1.name());
//    String cNormFqn = config.getString(Params.CNorm.name());
//    
//    transfProc.print(cNormFqn);
//    transfProc.print(c1Fqn);
    
//    dom.print();
    
// todo   dom.save();
  }
  
  private Map<ParamName, Object> getConfigMap(Dom dom) {
    Map<ParamName,Object> paramValMap = new HashMap<>();
    
    Class c1 = Student.class;
    String c1Fqn = 
//        "TestEntity"
        c1.getName()
        ;
    String c2Fqn = 
      CourseModule.class.getName()
      ;
    String cNormFqn = DClassTk.getPackageName(c1) + "." + "Enrolment";

    // load c1, c2 into dom
    String c1Name = DClassTk.getClassNameFromFqn(c1Fqn),
           c2Name = DClassTk.getClassNameFromFqn(c2Fqn);
    dom.loadClass(c1Name, c1Fqn);
    dom.loadClass(c2Name, c2Fqn);
    
    paramValMap.put(Params.C1, c1Fqn);
    paramValMap.put(Params.C2, c2Fqn);
    paramValMap.put(Params.CNorm, cNormFqn);
    paramValMap.put(Params.symmetrical, false);
    
    return paramValMap;
  }
}
