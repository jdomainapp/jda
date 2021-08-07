package jda.modules.patterndom.test.tpc;

import java.io.File;

import javax.json.JsonObject;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.TPC;
import jda.modules.patterndom.transform.tpc.TPCDataSourceAttrib;
import jda.modules.patterndom.transform.tpc.TPCDataSourceAttrib.Params;

/**
 * @overview 
 *  test creating the Entities pattern.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TPCDataSourceAttribTest {
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

    TPC transfProc = new TPCDataSourceAttrib();
    System.out.println(transfProc+"...initialised");

    JsonObject config = ToolkitIO.readJSonObjectFile(this.getClass(), 
        "pmap-dsource-attribute.json");
    
    transfProc.exec(dom, config);
    System.out.println("......executed");

    System.out.println("DOM...saved");
//    String c1Fqn = config.getString(Params.C1.name());
//    transfProc.print(c1Fqn);
  }
}
