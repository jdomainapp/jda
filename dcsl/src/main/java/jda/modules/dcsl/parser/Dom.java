package jda.modules.dcsl.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
   * @effects 
   *  Works similar to {@link #loadClass(String, String, String)} except that it only loads the source file
   *  if it has not been loaded.
   * @version 5.4.1
   * 
   */
  public ClassAST loadClassIfNotExists(String pkgName, String className, String javaSrcFile) throws NotFoundException {
    String fqn = pkgName + "." + className;
    if (clsMap.containsKey(fqn)) {
      return getDClass(fqn);
    } else {
      return loadClass(pkgName, className, javaSrcFile);
    }
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
   * @modifies {@link #classMap}
   * @effects 
   *  adds <code>ast</code> to this 
   * @version 5.4.1
   */
  public void addClass(ClassAST ast) {
    String pkg = ast.getPackageDeclaration();
    if (pkg == null) {
      pkg = getAnyPackage();
      if (pkg != null)
        ast.setPackage(pkg);
    }
    
    if (ast.getSrcFile() == null && pkg != null) {
      String srcFilePath = ToolkitIO.getJavaFilePath(rootSrcPath, pkg, ast.getName()); 
      ast.setSrcFile(srcFilePath);
    }
    
    clsMap.put(ast.getFqn(), ast);
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
   * @effects 
   *  if class(<code>fqnClsName</code>) has field named <code>fieldName</code>
   *    remove it
   * @version 5.4.1
   */
  public void removeFieldIfExists(String fqnClsName, String fieldName) {
    ClassAST ast = clsMap.get(fqnClsName);
    FieldDeclaration fd = ast.getField(fieldName);
    if (fd != null) {
      // todo: add a comment ?
      //Comment c = new LineComment("Removed: " + fieldName);
      fd.remove();
    }
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

  /**
   * @effects 
   *  returns a half-deep copy of this.
   *  
   * @version 5.4.1
   */
  @Override
  public Dom clone() {
    Dom newDom = new Dom(rootSrcPath);
    newDom.outputDir = outputDir;
    
    return newDom;
  }
  
  /**
   * @effects 
   *  if this is not empty
   *    return {@link Map}(String,String): FQN -&gt; Class-file-name of {@link #clsMap}
   *  else
   *    return null
   * @version 5.4.1
   * 
   */
  public Map<String, String> getClassFilesMap() {
    if (clsMap.isEmpty())
      return null;
    
    return clsMap.entrySet().stream()
      .collect(Collectors.toMap(
          e -> e.getKey(),
          e -> e.getValue().getSrcFile()
      ));
  }

  /**
   * @modifies this
   * 
   * @effects 
   *  create and return a clone of this in which 
   *  all the domain classes are renamed as specified in <code>classNameMap</code>.
   *  
   * @version 5.4.1 
   */
  public Dom createIModel(Map<String, String> classNameMap) {
    Dom imodel = clone();
    
    forEach((fqn, oldAst) -> {
      // update classes in classNameMap
      ClassAST ast = oldAst.clone();
      final String clsName = DClassTk.getClassNameFromFqn(fqn);
      for (Entry<String, String> centry: classNameMap.entrySet()) {
        String currName = centry.getKey();
        String newName = centry.getValue();
        String simpleName;
        int lastDot = newName.lastIndexOf(".");
        if (lastDot > -1) {
          // fqn
          simpleName = newName.substring(lastDot+1);
        } else {
          // simple name
          simpleName = newName;
        }
        // rename class 
        if (clsName.equals(currName)) {
          ast.rename(simpleName);
        } 

        // rename class references
        ast.updateTypeNameRef(currName, simpleName);
      }


      imodel.addClass(ast);
    });

    return imodel;
  }
  
  /**
   * @modifies this
   * 
   * @effects 
   * create and return a clone of this in which 
   *  all the domain classes are renamed as specified in <code>classNameMap</code>.
   *  and all the domain fields are renamed as specified in <code>fieldsNameMap</code>.
   *  
   * @version 5.4.1 
   */
  public Dom createIModel(Map<String, String> classNameMap, 
      Map<String, String> fieldsNameMap) {
    Dom imodel = clone();
    
    forEach((fqn, ast) -> {
      // update classes in classNameMap
      final String clsName = DClassTk.getClassNameFromFqn(fqn);
      for (Entry<String, String> centry: classNameMap.entrySet()) {
        String currName = centry.getKey();
        String newName = centry.getValue();

        // rename class 
        if (clsName.equals(currName)) {
          ast.rename(newName);
        } 

        // rename class references
        ast.updateTypeNameRef(currName, newName);
      }


      imodel.addClass(ast);

      // rename fields in fieldsNameMap
      for (Entry<String, String> fentry: fieldsNameMap.entrySet()) {
        String currName = fentry.getKey();
        String newName = fentry.getValue();
        ast.renameField(currName, newName);
      }
    });

    return imodel;
  }

  /**
   * @effects 
   *  performs action on {@link #clsMap}.entries
   *  
   * @version 5.4.1
   */
  public void forEach(BiConsumer<? super String, ? super ClassAST> action) {
    clsMap.forEach((fqn, ast) -> {
      action.accept(fqn, ast);
    });
  }

  /**
   * @effects 
   *  return {@link Stream}(String) contains FQNs of all classes in this.
   *  
   * @version 5.4.1
   */
  public Stream<String> clsFqnStreams() {
    return clsMap.keySet().stream();
  }
  
  /**
   * @effects 
   *  if exists a class in this whose FQN's simple name equals <code>clsSimpleName</code>
   *    return {@link ClassAST} of that class
   *  else
   *    return null
   * @version 5.4.1
   * 
   */
  public ClassAST getDClassByName(String clsSimpleName) {
    Optional<Entry<String,ClassAST>> clsEntry = clsMap.entrySet().stream()
        .filter(e -> DClassTk.getClassNameFromFqn(e.getKey()).equals(clsSimpleName))
        .findFirst();
    
    if (clsEntry.isPresent()) {
      return clsEntry.get().getValue();
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  get FQN of the package of any domain class in this
   *  
   * @version 5.4.1
   */
  public String getAnyPackage() {
    if (clsMap.isEmpty())
      return null;
    
    String anyPkg = DClassTk.getPackageName(clsMap.entrySet().iterator().next().getKey());
    return anyPkg;
    
  }
}
