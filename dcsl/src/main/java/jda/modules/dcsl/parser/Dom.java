package jda.modules.dcsl.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.util.DClassTk;

/**
 * @overview Represents a source code model, i.e. consisting {@link ClassAST}s of the Java classes
 *  that make up a domain model.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class Dom {

  /** full path to the directory of the root 'src' folder, which contains all classes in this model. 
   * This is usually the 'src' folder of the Java project*/
  private String rootSrcPath;

  /**
   * Maps FQN to ClassAST of each class in this model
   */
  private Map<String,ClassAST> clsMap;

  // v5.4
  private String outputDir;
  
  public Dom(String rootSrcPath) {
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
   * @requires
   *  each name in <code>fqnClassNames</code> is a valid FQN of a class
   *  
   * @modifies this
   * @effects 
   *  load each class specified in <code>fqnClassNames</code> into this
   *  
   * @version 5.4
   */
  public void loadClasses(Collection<String> fqnClassNames) throws NotFoundException {
    if (fqnClassNames == null) return;
    
    for (String fqn: fqnClassNames) {
      String clsName = DClassTk.getClassNameFromFqn(fqn);
      loadClass(clsName, fqn);
    };
  }
  
  /**
   * @effects 
   *  read and load all Java classes written in the specified file.
   *  Apply <code>pkg</code> to all of the loaded classes.
   *  
   *  <p>Throws NotFoundException if file is not found, 
   *  NotPossibleException if failed.
   *  
   * @version 5.4
   */
  public void loadClasses(String filePath, String pkg) throws NotFoundException, NotPossibleException {
    String actualFilePath = ToolkitIO.getPath(rootSrcPath, filePath).toString();
    
    final CompilationUnit cu;
    if (ToolkitIO.isFileUrl(actualFilePath)) {
      // jar file path
      InputStream ins = null;
      try {
        ins = ToolkitIO.readJarFileEntry(actualFilePath);
        cu = ParserToolkit.createJavaParser(ins);
      } catch (Exception e) {
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_PARSE_SOURCE_CODE, e,
            new Object[] { actualFilePath });
      } finally {
        // close it
        if (ins != null) {
          try {
            ins.close();
          } catch (IOException e) {
            // ignore
          }
        }
      }
    } else {
      // normal file path
      try {
        cu = ParserToolkit.createJavaParser(actualFilePath);
      } catch (FileNotFoundException e) {
        throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e,
            new Object[] { actualFilePath });
      }
    }

      
    List<ClassOrInterfaceDeclaration> classes = 
        cu.getChildNodesByType(ClassOrInterfaceDeclaration.class);
    final List<String> imports = ParserToolkit.getImports(cu);
    classes.forEach(cd -> {
      CompilationUnit cdu = ParserToolkit.createCompilationUnit(cd);
      // transfer all imports
      // TODO ? ONLY transfer imports specific to cd
      ParserToolkit.addImports(cdu, imports);
      String clsName = ParserToolkit.getName(cd);
      String fqnClsName = pkg + "." + clsName;
      ClassAST ast = new ClassAST(clsName, cdu);
      clsMap.put(fqnClsName, ast);
    });
  }

  /**
   * @requires
   *  The name of each Java (.java) file in the folder of the specified package 
   *  is the Java class name. 
   *  
   * @effects 
   *  load in this all the Java classes located in the specified package.
   *  Return the FQNs of all the classes.
   *  
   *  Throws NotFoundException if package is not found or no Java files were found.
   *  
   * @version 5.4
   * @return 
   */
  public List<String> loadClassesInPackage(String pkg) throws NotFoundException {
    String pkgFolderPath = ToolkitIO.getPackagePath(rootSrcPath, pkg);
    Map<String,String> javFiles = ToolkitIO.getFilePaths(pkgFolderPath);
    if (javFiles != null) {
      final List<String> fqns = new ArrayList<>();
      javFiles.forEach((fileName, javFile) -> {
        // remove file extention
        String className = fileName.substring(0, fileName.indexOf(ToolkitIO.FILE_JAVA_EXT));
        String fqn = pkg + "." + className;
        fqns.add(fqn);
        try {
          ClassAST ast = new ClassAST(className, javFile);
          clsMap.put(fqn, ast);
        } catch (NotFoundException e) {
          // javFile is not a class (ignore?)
          // TODO ? support other types (e.g. interface, enum)
        }
      });
      
      return fqns;
    } else {
      throw new NotFoundException(NotFoundException.Code.FOLDER_NOT_FOUND, 
          new Object[] {pkg});
    }
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
  
  /**
   * @modifies {@link #classMap}
   * @effects 
   *  create in this and return a {@link ClassOrInterfaceDeclaration}, whose name is <tt>className</tt> 
   * @version 5.4
   */
  public ClassOrInterfaceDeclaration addClass(
      String fqn, Modifier...modifiers) {
    String[] nameElems = fqn.split("\\.");
    String className = nameElems[nameElems.length-1];
    
    int lastDot = fqn.lastIndexOf(".");
    String pkgName = (lastDot > -1) ? fqn.substring(0, lastDot) : null;
    
    CompilationUnit cu = ParserToolkit.createClass(className, modifiers);
    
    if (pkgName != null) cu.setPackageDeclaration(pkgName);
    
    ClassAST ast = new ClassAST(className, cu);

    // construct src file path
    String srcFilePath = ToolkitIO.getJavaFilePath(rootSrcPath, pkgName, className); 
    ast.setSrcFile(srcFilePath);
    
    clsMap.put(fqn, ast);
    return ParserToolkit.getTopLevelClass(cu);
  }

  /**
   * @modifies <code>cls</code> in this
   * @effects 
   *  update <code>cls</code> by replacing all type name references to <code>name</code> by <code>newName</code>.
   * @version 5.4
   */
  public void updateTypeNameRef(String fqnClsName, String name, String newName) {
    ClassAST ast = getDClass(fqnClsName);
    ast.updateTypeNameRef(name, newName);
  }
  
  /**
   * @modifies the {@link ClassAST} of <code>Class(fqnClsName)</code>
   * @effects 
   *  create in the class whose FQN is <code>fqnClsName</code>
   *  a {@link FieldDeclaration}, from the specified arguments.
   *  Return the result.
   */
  public FieldDeclaration addField(String fqnClsName, String fieldName, Type type, Modifier[] mods) {
    ClassAST ast = clsMap.get(fqnClsName);
    FieldDeclaration fd = ast.addField(type, fieldName, mods);
    return fd;
  }

  /**
   * @modifies the {@link ClassAST} of <code>Class(fqnClsName)</code>
   * @effects 
   *  create in the class whose FQN is <code>fqnClsName</code>
   *  a {@link MethodDeclaration}, from the specified arguments.
   *  Return the result.
   */
  public MethodDeclaration addMethod(String fqnClsName, String methodName, Type type,
      Modifier... mods) {
    ClassAST ast = clsMap.get(fqnClsName);
    MethodDeclaration md = ast.addMethod(type, methodName, mods);
    return md;
  }

  /**
   * @effects 
   *  return the {@link ClassAST} in this whose FQN is fqnClsName, 
   *  or return null if it is not found.
   *  
   * @version 5.4
   * 
   */
  public ClassAST getDClass(String fqnClsName) {
    return clsMap.get(fqnClsName);
  }


  /**
   * @effects 
   *  return FQNs of all domain classes currently contained in this
   *  or null if this is empty
   * @version 5.4
   */
  public Collection<String> getDClassFqns() {
    if (isEmpty()) return null;
    
    return clsMap.keySet();
  }
  
  /**
   * @effects return outputDir
   */
  public String getOutputDir() {
    return outputDir;
  }

  /**
   * @effects set outputDir = outputDir
   */
  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  /**
   * @effects 
   *  print the ASTs of all domain classes in this.
   *   
   * @version 5.4
   */
  public void print() {
    System.out.println(toString());
  }

  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    if (isEmpty()) {
      return "Dom("+rootSrcPath+"): empty";
    } else {
      final StringBuilder sb = new StringBuilder("Dom("+rootSrcPath+")");
      
      clsMap.forEach((fqn, ast) -> {
        sb.append("\n===================\nClass: " + fqn)
          .append(ast);
      });
      
      return sb.toString();      
    }
  }

  /**
   * @effects 
   *  if this is empty
   *    return true
   *  else
   *    return false
   * @version 5.4
   * 
   */
  public boolean isEmpty() {
    return clsMap == null || clsMap.isEmpty();
  }

  /**
   * @effects 
   *  Save contents of all classes into storage
   *  
   * @version 5.4
   */
  public void save() throws NotPossibleException {
    if (outputDir == null) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_SAVE_FILE, 
          new Object[] {"Output dir is required but not specified"});
    }
    if (!isEmpty()) {
      clsMap.values().forEach(ast -> {
        ast.save(outputDir);
      });
    }
  }

}
