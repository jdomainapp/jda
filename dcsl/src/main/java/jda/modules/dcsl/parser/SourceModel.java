package jda.modules.dcsl.parser;

import java.util.HashMap;
import java.util.Map;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;

/**
 * @overview Represents a source code model, i.e. consisting {@link ClassAST}s of the Java classes
 *  that make up a domain model.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 * @deprecated since 5.4, use {@link Dom} instead.
 */
public class SourceModel extends Dom {

  /** full path to the directory of the root 'src' folder, which contains all classes in this model. 
   * This is usually the 'src' folder of the Java project*/
  private String rootSrcPath;

  private Map<String,ClassAST> clsMap;
  
  public SourceModel(String rootSrcPath) {
    super(rootSrcPath); // v5.4.1
    
    this.rootSrcPath = rootSrcPath;
    
    clsMap = new HashMap<>();
  }
  
  /**
   * @effects 
   *  reset this to be empty
   */
  public void clear() {
    clsMap.clear();
  }

  /**
   * @modifies this
   * 
   * @effects 
   *  create in this a {@link ClassAST} for <tt>pkgName + "." + className</tt> from <tt>javaSrcFile</tt> (if not already exists)
   *  return it
   */
  public ClassAST loadClass(String pkgName, String className, String javaSrcFile) throws NotFoundException {
    String fqn = pkgName + "." + className;
    
    ClassAST ast = clsMap.get(fqn);
    
    if (ast == null) {
      // not yet created
      ast = new ClassAST(className, javaSrcFile);
      clsMap.put(fqn, ast);
    }
    
    return ast;
  }

  /**
   * @requires <tt>fqn</tt> contains <tt>className</tt> in the simple name part 
   * 
   * @modifies this
   * 
   * @effects 
   *  create in this a {@link ClassAST} for <tt>fqn</tt> which is loaded relative to {@link #rootSrcPath} (if not already exists)
   *  return it
   */
  public ClassAST loadClass(String className, String fqn) throws NotFoundException {
    ClassAST ast = clsMap.get(fqn);

    if (ast == null) {
      // not yet created
      String javaSrcFile = ToolkitIO.getPath(rootSrcPath, 
          ToolkitIO.splitPackageName(fqn)).toString() + ".java"; 
          
      ast = new ClassAST(className, javaSrcFile);
      clsMap.put(fqn, ast);
    }
    
    return ast;
  }
  
  /**
   * @requires <tt>fqn</tt> is recorded this /\ the java source file of the class has not been changed
   * 
   * @effects 
   *  reload {@link ClassAST} of the class specified by <tt>fqn</tt> and update it into this.
   *  
   *  <p>Throws {@link NotFoundException} if <tt>fqn</tt> is not recorded in this.
   */
  public void reloadClass(String fqn) throws NotFoundException {
    ClassAST ast = clsMap.get(fqn);
    
    if (ast == null) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, new Object[] {fqn} );
    }
    
    ast.reload();
  }
  
  /**
   * @effects 
   *  if this contains an entry for <tt>fqn</tt>
   *    remove it
   *  else 
   *    do nothing
   */
  public void removeClass(String fqn) {
    clsMap.remove(fqn);
  }

//  /**
//   * @effects 
//   *  update the state space of the class <tt>fqn</tt> when some fields have been renamed, as specified in <tt>updateSpec</tt>.
//   *  
//   *  <tt>updateSpec</tt> is comma-sparated list of (new-field-name,old-field-name) pairs (e.g. "(testA,test1),(testB,test2)")
//   */
//  //TODO
//  public void updateDClassOnFieldRename(String fqn, String updateSpec) {
//    if (fqn == null || updateSpec == null) return;
//    
//    ClassAST ast = clsMap.get(fqn);
//    
//    if (ast != null) {
//      String[][] fieldPairs = parseFieldRenameSpec(updateSpec);
//      
//      for (String[] fieldPair : fieldPairs) {
//        String newField = fieldPair[0];
//        String oldField = fieldPair[1];
//        //ast.renameField(oldField, newField);
//      }
//    }
//  }

  /**
   * @requires <tt>updateSpec</tt> is comma-sparated list of (new-field-name,old-field-name) pairs (e.g. "(testA,test1),(testB,test2)")
   * 
   * @effects 
   *  return {@link String}{}{} of pair lists contained in <tt>updateSpec</tt>
   */
  public static String[][] parseFieldRenameSpec(final String updateSpec) throws NotPossibleException {
    if (updateSpec == null) return null;
    
    String[] fieldPairs = updateSpec.split(";");
    String[][] updatedFields = new String[fieldPairs.length][];

    int idx = 0;
    for (String fieldPair : fieldPairs) {
      String[] els = fieldPair.split(",");
      if (els.length != 2)
        throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] {"field name pair = '" + fieldPair + "'"});
      
      String newField = els[0].substring(1);  // remove the leading '('
      String oldField = els[1].substring(0, els[1].length()-1); // remove the trailing ')'
      updatedFields[idx] = new String[] {newField, oldField};
      idx++;
    }
    
    return updatedFields;
  }

  /**
   * @modifies this
   * @effects 
   *  sets {@link #rootSrcPath} = rootSrcPath 
   */
  public void setRootSrcPath(String rootSrcPath) {
    this.rootSrcPath = rootSrcPath;
  }
  
}
