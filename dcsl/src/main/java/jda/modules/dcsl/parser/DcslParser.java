package jda.modules.dcsl.parser;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.dcsl.exceptions.DcslMesg;
import jda.modules.dcsl.exceptions.DcslParserException;
import jda.modules.dcsl.parser.statespace.metadef.DAttrDef;
import jda.modules.dcsl.parser.statespace.metadef.FieldDef;
import jda.modules.dcsl.parser.statespace.parser.StateSpaceVisitor;

/**
 * @overview 
 *  A {@link JavaParser} that additionally validates the {@link CompilationUnit} with DCSL's specific 
 *  rules. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 1.0 
 */
public class DcslParser {

  private static DcslParser instance;

  /**
   * @effects 
   *  return the singleton {@link #instance}
   */
  public static DcslParser getInstance() {
    if (instance == null) {
      instance = new DcslParser();
    }
    
    return instance;
  }

  /**
   * @effects 
   *  use {@link JavaParser} to parse the Java source file specified by <tt>srcFilePath</tt> into a {@link CompilationUnit}.
   *  Then check this unit for conformance with DCSL's design rules.
   *  If succeeded then return the unit, else return null
   *  
   *  <p>Throws DcslParserException if any of the design rule is violated,
   *  NotFoundException if <tt>srcFilePath</tt> is not found.
   * 
   */
  public CompilationUnit execute(String srcFilePath) throws DcslParserException, NotFoundException {
    // use {@link JavaParser} to parse the Java source file specified by <tt>srcFilePath</tt> into a {@link CompilationUnit}
    CompilationUnit cu;
    try {
      cu = ParserToolkit.createJavaParser(srcFilePath);
    } catch (FileNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e, new Object[] {srcFilePath});
    }
    
    // check this unit for conformance with DCSL's design rules
    // get the class declaration
    ClassOrInterfaceDeclaration clazz = ParserToolkit.getTopLevelClass(cu);
    
    // read state space spec
    // a map to record the state space elements
    LinkedHashMap<DAttrDef, FieldDef> stateSpaceMap = new LinkedHashMap<>();
    List<String> imports = ParserToolkit.getImports(cu);
    String pkgName = ParserToolkit.getPackageDeclaration(cu);
        
    // walk the AST to extract the state space elements into stateSpace
    new StateSpaceVisitor(pkgName, imports).visit(cu, stateSpaceMap);
    
    if (stateSpaceMap.isEmpty()) {
      // no domain attributes defined
      throw new DcslParserException(DcslMesg.STATE_SPACE_EMPTY, new Object[] {srcFilePath});
    }
    
    // use the state space and DCSL design rules to check the behaviour space
    // TODO: implements this
    
    return cu;
  }
}
