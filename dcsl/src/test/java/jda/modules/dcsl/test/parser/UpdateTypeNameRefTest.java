package jda.modules.dcsl.test.parser;

import java.io.FileNotFoundException;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ParserToolkit;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class UpdateTypeNameRefTest {
  @Test
  public void test() {
    String path = ToolkitIO.getPathExt(getClass(), "MyClass.jav");
    try {
      CompilationUnit cu = ParserToolkit.createJavaParser(path);
      
      ClassOrInterfaceDeclaration cd = ParserToolkit.getTopLevelClass(cu);
      
      String name = "Object", newName = "String";
      
      System.out.printf("Update type name references: %s -> %s%n", name, newName);
      System.out.printf("BEFORE: %n%s%n", cd);
      ParserToolkit.updateTypeNameRef(cd, name, newName);
      System.out.printf("AFTER: %n%s%n", cd);
      
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }
}
