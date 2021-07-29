package jda.modules.dcsl.test.jtransform;

import static jda.modules.dcsl.parser.ParserConstants.TypeDouble;
import static jda.modules.dcsl.parser.ParserConstants.TypeString;
import static jda.modules.dcsl.parser.ParserConstants.TypeVoid;

import java.io.File;

import org.junit.Test;

import com.github.javaparser.ast.Modifier;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.jtransform.JTransform;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class JTransformBasicTest {
  @Test
  public void test() {
    boolean mainSrc = false;
    File rootSrcPath = ToolkitIO.getMavenRootSrcPath(JTransform.class, mainSrc);
    System.out.println("Root src path: " + rootSrcPath);
    
    Dom dom = new Dom(rootSrcPath.getPath());
    
    JTransform transf = new JTransform(dom);
    //TODO: 
    // - test adding package
    
    transf.begin();
    transf.addClass("Test", Modifier.PUBLIC)
    .addField("Test", "testAttr", TypeString, Modifier.PRIVATE)
    .addMethod("Test", "testMethod", TypeVoid, Modifier.PUBLIC)
      .addMethodParam("Test", "testMethod", "param1", TypeString)
      .addMethodParam("Test", "testMethod", "param2", TypeDouble)
      .addMethodBody("Test", "testMethod")
        .addMethodStmt("Test", "testMethod", "System.out.println(\"hello\");")
        .addMethodStmt("Test", "testMethod", "System.out.println(testAttr);")
        .pushMethodStmt("Test", "testMethod", "int temp = 0;")
        .addMethodBlock("Test", "testMethod", "temp++; \n System.out.println(temp);");
    
    transf.print("Test");
    transf.end();
  }
  
}
