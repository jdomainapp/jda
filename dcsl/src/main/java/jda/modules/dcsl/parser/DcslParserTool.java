package jda.modules.dcsl.parser;

import com.github.javaparser.ast.CompilationUnit;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.exceptions.DcslMesg;
import jda.modules.dcsl.exceptions.DcslParserException;

/**
 * @overview 
 *  Implements the tool interface for clients to use.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0 
 */
public class DcslParserTool implements ModuleToolable {

  private String dclsFilePath;

  public DcslParserTool(String dclsFilePath) {
    this.dclsFilePath = dclsFilePath;
  }
  
  /* (non-Javadoc)
   * @see domainapp.module.ModuleToolable#exec()
   */
  /**
   * @effects 
   * 
   */
  @Override
  public CompilationUnit exec(Object...args) throws NotPossibleException {

    if (dclsFilePath == null) return null;
    
    // process each source file
    String srcFile;
    
    System.out.println("\nRunning DcslParser...");
    System.out.println("   Source file: " + dclsFilePath + "\n");
    
    DcslParser gen = DcslParser.getInstance();
    try {
      CompilationUnit cu = gen.execute(dclsFilePath);
      System.out.println("...ok");
      
      return cu;
    } catch (DcslParserException e) {
      throw new NotPossibleException(DcslMesg.FAIL_TO_PARSE_UNIT, e, new Object[] {dclsFilePath});
    }
  }
  
  /**
   * @requires 
   *  args[0] = class-file-path (e.g. /tmp/Student.java)
   *  
   * @effects 
   *  execute {@link DcslParserTool} on the specified domain class(es)
   */
  public static void main(String[] args) throws Exception {
    if (args == null || args.length < 1) {
      System.out.println("Usage: " + DcslParserTool.class + " <class-source-file-path>");
      System.exit(0);
    }

    String dclsFilePath = args[0];
    
    DcslParserTool tool = new DcslParserTool(dclsFilePath);
    
    tool.exec();
  }
  
}
