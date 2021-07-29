/**
 * 
 */
package jda.modules.dcsl.test;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.parser.statespace.metadef.MetaAttrDef;
import jda.modules.dcsl.parser.statespace.parser.StateSpaceVisitor;

/**
 * @overview
 * 
 * @author dmle
 *
 * @version
 */
public class StateSpaceParserTest {
  @Test
  public void run() {
    // package containing the class to parse
    String pkgName = "org.jda.example.courseman.model";

    // source code file of the class to parse
    String srcPath = ToolkitIO.getMavenRootSrcPath(this.getClass(), false).getPath();
    String srcFile = ToolkitIO.getJavaFilePath(srcPath, pkgName, "Student");
    
    try {
      CompilationUnit cu = ParserToolkit.createJavaParser(srcFile);

      // StateSpaceParser ssparser = new StateSpaceParser(pkgName, cu);
      //
      // // a map to record the state space elements
      // Map<DAttrDef, FieldDef> stateSpace = ssparser.getStateSpace();
      LinkedHashMap<DAttrDef, FieldDef> stateSpace = new LinkedHashMap<>();
      List<String> imports = ParserToolkit.getImports(cu);

      // walk the AST to extract the state space elements into stateSpace
      new StateSpaceVisitor(pkgName, imports).visit(cu, stateSpace);

      // print state space
      DAttrDef attr;
      FieldDef field;
      System.out.println("Java source file: " + srcFile);
      System.out.println("\nState Space: \n");
      for (Entry<DAttrDef, FieldDef> e : stateSpace.entrySet()) {
        attr = e.getKey();
        field = e.getValue();
        System.out.println(field);
        System.out.println("--> " + attr);

        Collection<MetaAttrDef> anos = field.getAnnotations();
        if (anos != null) {
          System.out.println("Annotations:");
          for (MetaAttrDef metaAttr : anos) {
            System.out.println("  " + metaAttr);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
