package jda.modules.mccl.test;

import java.nio.file.Path;
import java.util.Collection;

import org.junit.Test;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mccl.syntax.view.AttributeDesc;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class MCCReader {
  @Test
  public void run() {
    // change this to a file path
    Path rootSrcPath = ToolkitIO.getPath("/home","ducmle",
        "projects","jda","modules","mccl","src", "test", "java");
    String rootSrcPathStr = rootSrcPath.toString();

    String dclsFqn = "org.jda.example.courseman.modulesupdate.student.model.Student";
    String mccFqn = "org.jda.example.courseman.modulesupdate.student.ModuleStudent";
    
    String[] cels = dclsFqn.split("\\.");
    String cname = cels[cels.length-1];
    String[] mels = mccFqn.split("\\.");
    String mname = mels[mels.length-1];
    
    Path dclsFile = ToolkitIO.getPath(rootSrcPathStr, cels);
    Path mccFile = ToolkitIO.getPath(rootSrcPathStr, mels);
    
    ClassAST dcls = new ClassAST(cname, dclsFile.toString() + ToolkitIO.FILE_JAVA_EXT);
    MCC mcc = new MCC(mname, mccFile.toString() + ToolkitIO.FILE_JAVA_EXT, dcls);
    
    System.out.printf("%s%n", mcc);
    
    // get view fields
    Collection<FieldDeclaration> vfields = mcc.getViewFields();
    System.out.printf("View fields: %n");
    for (FieldDeclaration vfield : vfields) {
      System.out.println(vfield);
      NormalAnnotationExpr attribDesc = ParserToolkit.getAnnotation(vfield, AttributeDesc.class);
      System.out.printf(" --> View config: %s%n", attribDesc);
    }
  }
}
