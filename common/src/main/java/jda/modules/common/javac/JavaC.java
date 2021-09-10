package jda.modules.common.javac;

import java.io.File;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *  Use the Java Compiler API to programmatically compile source codes to a designated
 *  output directory.
 *  
 * @author ducmle
 * @version 5.4.1
 */
public class JavaC {

  /**
   * @requires 
   *  <tt>srcPkg != null => srcPkg</tt> is the FQN of package in <tt>srcDir.path</tt> /\ 
   *  <tt>srcDir</tt> contains the source code files <tt>srcMap.values</tt> /\ 
   *  <tt>fqn</tt> is the FQN of the class source code in <tt>srcFile</tt>
   *  
   * @effects 
   *  compile <tt>srcFile</tt> whose corresponding class name is <tt>fqn</tt>, 
   *  and return the compiled class; 
   *  throws NotPossibleException if fails
   */
  public static Class<?> javacLoad(String srcPkg, File srcDir, 
      String fqn, File srcFile, 
      File outputDir, Writer nonDiagOutputWriter, String...vmArgs) throws NotPossibleException {
    Map<String, File> srcMap = new HashMap<>();
    srcMap.put(fqn, srcFile);
    
    Map<String, Class<?>> output = javacLoad(srcPkg, srcDir, srcMap, outputDir, nonDiagOutputWriter, vmArgs);
    
    return output.values().iterator().next();
  }
  
  /**
   * @requires 
   *  <tt>srcPkg != null => srcPkg</tt> is the FQN of package in <tt>srcDir.path</tt> /\ 
   *  <tt>srcDir</tt> contains the source code files <tt>srcMap.values</tt> /\ 
   *  <tt>srcMap.keys</tt> are FQN of the class source codes in <tt>srcMap.values</tt>
   *  
   * @effects 
   *  compile <tt>srcMap.values</tt> whose corresponding class names are <tt>srcMap.keys</tt>, 
   *  and return the compiled classes as {@link Map}; 
   *  throws NotPossibleException if fails
   */
  public static Map<String, Class<?>> javacLoad(String srcPkg, File srcDir, 
      Map<String, File> srcMap, 
      File outputDir, Writer nonDiagOutputWriter, String...vmArgs) throws NotPossibleException {
    // compile
    Collection<File> srcFiles = srcMap.values();
    
    String relSrcPath = ToolkitIO.getPackagePath(null, srcPkg);
    
    javac(relSrcPath, srcDir, srcFiles, outputDir, nonDiagOutputWriter, vmArgs);
    
    // load
    return loadClasses(outputDir, srcMap.keySet().toArray(new String[0]));
  }
  
  /**
   * @requires 
   *  <tt>srcDir</tt> contains the source code files <tt>srcMap.values</tt> /\ 
   *  <tt>srcMap.keys</tt> are FQN of the class source codes in <tt>srcMap.values</tt>
   *  
   * @effects 
   *  compile <tt>srcMap.values</tt> whose corresponding class names are <tt>srcMap.keys</tt>, 
   *  and return the compiled classes as {@link Map}; 
   *  throws NotPossibleException if fails
   */
  public static Map<String, Class<?>> javacLoad(
      File srcDir, Map<String, File> srcMap, 
      File outputDir, Writer nonDiagOutputWriter, String...vmArgs) 
      throws NotPossibleException {
    
    return javacLoad(null, srcDir, srcMap, outputDir, nonDiagOutputWriter, vmArgs);
//    // compile
//    Collection<File> srcFiles = srcMap.values();
//    compile(srcDir, srcFiles, outputDir, nonDiagOutputWriter, vmArgs);
//    
//    // load
//    return loadClasses(outputDir, srcMap.keySet().toArray(new String[0]));
  }
  
  /**
   * @requires 
   *  <tt>srcPkg != null => srcPkg</tt> is the FQN of package in <tt>srcDir.path</tt> /\ 
   *  <tt>srcDir</tt> contains the source code files to compile 
   * 
   * @effects 
   *  use the Java's built-in compiler and the VM arguments <tt>vmArgs</tt> 
   *  to compile the entire source directory <tt>srcDir</tt>. 
   *  Store the class files to <tt>outputDir</tt>.
   *  
   *  <p>Throws {@link NotPossibleException} if fails.
   */
  public static void javac(String srcPkg, File srcDir, File outputDir, Writer nonDiagOutputWriter, String...vmArgs) 
      throws NotPossibleException, NotFoundException {
    Collection<File> files = ToolkitIO.readJavaFiles(srcDir);
    if (files == null) {
      throw new NotFoundException(
          NotFoundException.Code.FILE_NOT_FOUND, new Object[] {srcDir + ": Java source files"});
    }
    
    String relSrcPath = ToolkitIO.getPackagePath(null, srcPkg);

    javac(relSrcPath, srcDir, files, outputDir, nonDiagOutputWriter, vmArgs);
  }
  
  /**
   * @effects 
   *  use the Java's built-in compiler and the VM arguments <tt>vmArgs</tt> 
   *  to compile the entire source directory <tt>srcDir</tt>. 
   *  Store the class files to <tt>outputDir</tt>.
   *  
   *  <p>Throws {@link NotPossibleException} if fails.
   */
  public static void javac(File srcDir, File outputDir, Writer nonDiagOutputWriter, String...vmArgs) throws NotPossibleException {
    javac(null, srcDir, outputDir, nonDiagOutputWriter, vmArgs);
    
//    Collection<File> files = ToolkitIO.readJavaFiles(srcDir);
//    if (files == null) {
//      throw new NotPossibleException("Invalid source code directory or no Java files found: " + srcDir);
//    }
//    
//    compile(null, srcDir, files, outputDir, nonDiagOutputWriter, vmArgs);
  }

  /**
   * @requires 
   *  <tt>srcPkg != null => srcPkg</tt> is the FQN of package in <tt>srcDir.path</tt> /\ 
   *  <tt>srcFiles</tt> contain a sub-set of files stored under <tt>srcDir</tt>.
   * 
   * @effects 
   *  use the Java's built-in compiler and the VM arguments <tt>vmArgs</tt> 
   *  to compile the source code files <tt>srcFiles</tt> in <tt>srcDir</tt>. 
   *  Store the class files to <tt>outputDir</tt>.
   *  
   *  <p>In addition, copy all non-Java resource files to the respective directories under <tt>outputDir</tt> 
   *  
   *  <p>Throws {@link NotPossibleException} if fails.
   */
  public static void javac(String srcPkg, File srcDir, Collection<File> srcFiles, File outputDir, Writer nonDiagOutputWriter,
      String[] vmArgs) throws NotPossibleException {
    
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    
    // VM arguments
    List<String> vmArguments = new ArrayList<>();
    Collections.addAll(vmArguments, vmArgs);
    vmArguments.add("-d");
    vmArguments.add(outputDir.getPath());
    
    //  e.g. ["-sourcepath", "src"]
    StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
    Collection<String> srcFilePaths = new ArrayList<>();
    for (File file : srcFiles) {
      srcFilePaths.add(file.getPath());
    }
    Iterable<? extends JavaFileObject> fileObjects = fileManager.getJavaFileObjectsFromStrings(srcFilePaths);
    
    CompilationTask task = compiler.getTask(nonDiagOutputWriter, 
        fileManager, null,
        vmArguments, null, fileObjects);
    boolean ok = task.call();
    
    if (!ok) throw new 
    NotPossibleException(NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, 
        new Object[] {"Directory: " + srcDir});

    String relSrcPath = ToolkitIO.getPackagePath(null, srcPkg);

    // copy all non-Java resource files to the respective directories under output dir
    ToolkitIO.copyJavaResources(relSrcPath, srcDir, srcFiles, outputDir);
  }

  /**
   * @requires <tt>classNames</tt> contain the FQNs of class files that are stored 
   * in <tt>outputDir</tt>
   * 
   * @effects 
   *  load from <tt>outputDir</tt> the classes whose names are specified in <tt>classNames</tt>.
   *  Throws NotFoundException if some classes are not found.
   */
  public static Map<String,Class<?>> loadClasses(final File outputDir, String[] classNames) throws NotFoundException {
    // load classes
    try {
      return loadClassesRaw(outputDir, classNames);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException (
          NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {""});
    }
  }


  /**
   * @effects 
   *  if successfully load a class named <tt>clsName<tt> from the directory <tt>outputPath</tt>
   *    return it
   *  else
   *    throws NotFoundException
   */
  public static Class loadClass(File outputPath, String clsName) throws NotFoundException {
    ClassLoader loader = createLoader(outputPath);
    try {
      Class cls = loader.loadClass(clsName);
      return cls;
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(
          NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {clsName});
    }
  }
  
  /**
   * @effects 
   *  if successfully load a class named <tt>clsName<tt> from the directory <tt>outputPath</tt>
   *    return true
   *  else
   *    return false
   */
  public static boolean loadTest(File outputPath, String clsName) {
    try {
      loadClassesRaw(outputPath, new String[] {clsName});
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }
  
  /**
   * @requires <tt>classNames</tt> contain the FQNs of class files that are stored 
   * in <tt>outputPath</tt>
   * 
   * @effects 
   *  load from <tt>outputPath</tt> the classes whose names are specified in <tt>classNames</tt>.
   *  Throws ClassNotFoundException  if a class is not found.
   */
  public static Map<String,Class<?>> loadClassesRaw(final File outputPath, String[] classNames) throws ClassNotFoundException {
    // load classes
    ClassLoader loader = createLoader(outputPath);
    
    Map<String,Class<?>> clsMap = new LinkedHashMap<>();
    for (String clsName : classNames) {
      Class<?> cls = loader.loadClass(clsName);
      
      // debug
      // DClassUtils.printClassFields(cls);
      
      clsMap.put(clsName, cls);
    }
      
    return clsMap;
  }
  
  /**
   * @effects 
   *  return a {@link URLClassLoader} for the specified <tt>outputPaths</tt>. 
   *  Throws {@link NotPossibleException} if a path is invalid. 
   */
  private static URLClassLoader createLoader(File...outputPaths) throws NotPossibleException {
    File[] files = new File[outputPaths.length];
    URL[] urls = new URL[files.length];
    int i = 0;
    for (File file : outputPaths) {
      //File file = new File(path);
      try {
        urls[i++] = file.toURI().toURL();
      } catch (MalformedURLException e) {
        throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, e, 
            new Object[] {file});
      }
    }
    
    // IMPORTANT: get the parent class loader
    ClassLoader context = JavaCToolkit.getContextClassLoader(JavaC.class);
    
    URLClassLoader loader = new URLClassLoader(urls, context);
    
    return loader;
  }
}
