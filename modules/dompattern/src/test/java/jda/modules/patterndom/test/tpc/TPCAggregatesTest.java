package jda.modules.patterndom.test.tpc;

import java.io.File;

import javax.json.JsonObject;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.patterndom.transform.tpc.TPCAggregates;
import jda.modules.patterndom.transform.tpc.TPCAggregates.Params;

/**
 * @overview 
 *  test creating the Entities pattern.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCAggregatesTest {
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

    // load an existing class
    // String fqnClsName = "jda.modules.jtransform.test.Test2";
    // dom.loadClass("Test2", fqnClsName);
    
    TPC transfProc = new TPCAggregates();
    
    System.out.println(transfProc+"...initialised");

    JsonObject config = ToolkitIO.readJSonObjectFile(this.getClass(), 
        "pmap-aggregates.json");
    
    transfProc.exec(dom, config);
    System.out.println("......executed");

    System.out.println("DOM...saved");

//    String root = config.getString(Params.root.name());
//    transfProc.print(root);
    
//    dom.print();
  }
}
