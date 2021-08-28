package jda.modules.sccl.parser;

import java.io.File;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ParserConstants;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.mosa.software.SoftwareFactory;
import jda.mosa.software.aio.SoftwareAio;

/**
 * @overview Represents the software class compilation unit
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class SCCAst {
  
  //private static final Expression UNKNOWN_PROP_VAL = new StringLiteralExpr("?");

  /** the AST of this class */
  private CompilationUnit ast;

  /** top-level class of {@link #ast} */ 
  private ClassOrInterfaceDeclaration cls;
  
  /** the simple name of this */
  private String name;

  /** the root output dir for saving {@link #getSourceCode()} */
  private File outputSrcFile;

  /** the SCC */
  private Class scc;

  public SCCAst(String name, Class scc) {
    // get the shared class pool instance (singleton)
    ast = ParserToolkit.createJavaParserForClass(name, Modifier.PUBLIC);

    cls = ParserToolkit.getTopLevelClass(ast);
    
    this.name = name;
    
    this.scc = scc;
  }

  /**
   * @effects 
   *  return simple name of this
   */
  public String getName() {
    return name;
  }

  
  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return String.format("SWC(%s): %n", name);    
  }

  /**
   * @modifies this.{@link #ast}
   * @effects 
   *  changes package declaration of {@link #ast} to pkgName.
   */
  public void setPackageName(String pkgName) {
    ast.setPackageDeclaration(pkgName);
  }

  /**
   * @effects 
   *  return {@link #ast}.toString
   */
  public String getSourceCode() {
    return ast.toString();
  }

  /**
   * @modifies this.{@link #outputSrcFile}
   * @effects 
   *  write source code of {@link #ast} to a file in a sub-package directory of <tt>mccOutputRootDir</tt> 
   *  that corresponds to the package name of this (overwritting existing content, if any).
   *  
   *  <p>sets this.{@link #outputSrcFile} = the file.
   */
  public void save(String mccOutputRootDir) throws NotPossibleException {
    //this.mccOutputRootDir = mccOutputRootDir;
    
    String src = getSourceCode();
    String fqn = getFqn();
    
    // write to file
    boolean overwrite = true;
    outputSrcFile = ToolkitIO.writeJavaSourceFile(mccOutputRootDir, fqn, src, overwrite); 
  }

  /**
   * @effects 
   *  if {@link #outputSrcFile} != null
   *    write source code of {@link #ast} to a file specified in {@link #outputSrcFile} 
   *    (overwritting existing content, if any).
   *  else
   *    do nothing
   */
  public void save() {
    if (outputSrcFile != null) {
      String mccSrc = getSourceCode();
      
      boolean overwrite = true;
      ToolkitIO.writeTextFileWithEncoding(outputSrcFile, mccSrc, "UTF-8", overwrite);
    }
  }


  /**
   * @effects 
   *  return FQN of this
   */
  public String getFqn() {
    String pkg = ParserToolkit.getPackageDeclaration(ast);
    String fqn = pkg + "." + name;
    
    return fqn;
  }
  
  /**
   * @effects 
   *  return FQN of the class represented by <tt>clsExpr</tt> in this
   */
  public String getFqn(ClassExpr clsExpr) {
    // find among the imports...
    String fqn = ParserToolkit.getFqn(ast, clsExpr);
    
    return fqn;
  }

  /**
   * @modifies {@link #ast}, {@link #cls}
   * @effects 
   *  create in {@link #cls} a {@link MethodDeclaration} for the <tt>main</tt> method whose 
   *  behaviour is to execute a standard software from {@link #scc}.
   */
  public void createStandardMainMethod() {
    MethodDeclaration m = 
        ParserToolkit.createSingleParamMethod(cls, 
            ParserConstants.TypeVoid, "main", 
            ParserConstants.TypeStringArr, "args", 
            ParserConstants.modPublicStatic);
    
    // add exception
    ParserToolkit.createMethodException(m, Exception.class);
    
    // add necessary imports
    ParserToolkit.addImport(ast,
        SoftwareFactory.class,
        SoftwareAio.class,  
        scc
        );
    
    // method body
    String body = "    final Class SwCfgCls = %s.class;\n" + 
        "    SoftwareAio sw = SoftwareFactory.createSoftwareAioWithMemoryBasedConfig(SwCfgCls);\n" + 
        "    sw.exec(args);\n" 
        ;
    
    body = String.format(body, scc.getSimpleName());
    
    ParserToolkit.createMethodBody(m, body);
  }
}
