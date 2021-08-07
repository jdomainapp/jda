package jda.modules.patterndom.test.tpc;

import java.io.File;

import javax.json.JsonObject;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.patterndom.transform.tpc.TPCEntities;
import jda.modules.patterndom.transform.tpc.TPCEntities.Params;

/**
 * @overview 
 *  test creating the Entities pattern.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCEntitiesTest {
  @Test
  public void test() {
    boolean mainSrc = false;
    File rootSrcPath = ToolkitIO.getMavenRootSrcPath(TPCEntitiesTest.class, mainSrc);
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
    
    TPC transfProc = new TPCEntities();
    System.out.println(transfProc+"...initialised");

    JsonObject config = ToolkitIO.readJSonObjectFile(this.getClass(), 
        "pmap-entities.json");
    
    transfProc.exec(dom, config);
    System.out.println("......executed");

    System.out.println("DOM...saved");

//    String entityFqn = config.getString(Params.Entity.name());
//    transfProc.print(entityFqn);
  }
}
