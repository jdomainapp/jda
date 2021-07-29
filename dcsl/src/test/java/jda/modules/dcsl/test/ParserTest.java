/**
 * 
 */
package jda.modules.dcsl.test;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.syntax.DOpt;

/**
 * @overview
 * 
 * @author dmle
 *
 * @version
 */
public class ParserTest {

  @Test
  public void run() {
    // package containing the class to parse
    String pkgName = "org.jda.example.courseman.model";

    // source code file of the class to parse
    String srcPath = ToolkitIO.getMavenRootSrcPath(this.getClass(), false).getPath();
    String srcFile = ToolkitIO.getJavaFilePath(srcPath, pkgName, "Student");
        //"/home/dmle/projects/jda/modules/dcsl/src/test/java/vn/com/courseman/model/Student.java";

    try {

      CompilationUnit cu = ParserToolkit.createJavaParser(srcFile);

      ClassOrInterfaceDeclaration c = ParserToolkit.getTopLevelClass(cu);

      MethodDeclaration m = ParserToolkit.getDomainMethodByOptType(c,
          DOpt.Type.AutoAttributeValueSynchroniser);

      Class[] throwables = ParserToolkit.getMethodThrowsClause(cu, m);

      System.out.printf("Class: %s%n Method: %s%n Throwables: %s%n",
          c.getNameAsString(), m.getNameAsString(),
          Arrays.toString(throwables));

      ConstructorDeclaration cons = ParserToolkit
          .getDomainConstructorByOptType(c, DOpt.Type.DataSourceConstructor);
      AnnotationExpr ano = cons.getAnnotationByClass(DOpt.class).get();
      System.out.println(cons);
      System.out.println("  " + ano);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}
