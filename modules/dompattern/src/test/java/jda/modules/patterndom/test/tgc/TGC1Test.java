package jda.modules.patterndom.test.tgc;

import java.io.File;

import javax.json.JsonArray;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.TGC;
import jda.modules.patterndom.PatternDomConstants;

/**
 * @overview 
 *  test creating the Entities pattern.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class TGC1Test {
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
    
    TGC transfProg = new TGC("TGC1",
        PatternDomConstants.PatternsConfigPath
        );
    
    System.out.println(transfProg+"...initialised");

    JsonArray config = ToolkitIO.readJSonFile(this.getClass(), 
        JsonArray.class,
        "gmap1.json");
    
    transfProg.exec(dom, config);

    System.out.println("......executed");

    // print result
    System.out.println("DOM...updated");
//    dom.print();

  }
}
