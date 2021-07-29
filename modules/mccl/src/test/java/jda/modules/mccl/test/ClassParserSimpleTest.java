package jda.modules.mccl.test;

import java.util.Optional;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import jda.modules.dcsl.parser.ParserToolkit;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ClassParserSimpleTest {
  @Test
  public void run() {
    String clsName = "MyClass";
    CompilationUnit cu = ParserToolkit.createJavaParserForClass(clsName, Modifier.PUBLIC);
    
    System.out.println("Compilation unit: ");
    System.out.println(cu);
    
    Optional<ClassOrInterfaceDeclaration> clsOpt = cu.getClassByName(clsName);
    
    if (clsOpt.isPresent())
      System.out.println("Class: \n" + clsOpt.get());
    else
      System.out.println("Class not found");
  }
}
