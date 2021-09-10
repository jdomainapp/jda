package jda.modules.swtool.util.javac;

import java.io.File;
import java.util.Map;

import org.mdkt.compiler.InMemoryJavaCompiler;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *  A tool kit class for shared tasks concerning the software tool. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public class JavaCInMem {
  //private static final InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

  private JavaCInMem() {}
  
  /**
   * @effects 
   *  compiles <code>clsSrcCode</code> whose FQN is <code>fqnCls</code> to the 
   *  output folder specified by the {@link ClassLoader} of the current execution thread.
   *  
   *  <p>throws NotPossibleException if fails.
   */
  public static Class javac(String fqnCls, String clsSrcCode) throws NotPossibleException {
    InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

    try {
      return compiler.ignoreWarnings()
          .useParentClassLoader(Thread.currentThread().getContextClassLoader())
          .compile(fqnCls, clsSrcCode);
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, e,
          new Object[] { fqnCls });
    }
  }
  
  /**
   * @effects 
   *  compiles <code>clsSrcCode</code> whose FQN is <code>fqnCls</code> 
   *  using the {@link ClassLoader} of the current execution thread.
   *  
   *  <p>The output class is <b>kept in memory</b> but not stored on disk. 
   *  
   *  <p>throws NotPossibleException if fails.
   */
  public static Class javacInMem(String fqnCls, String clsSrcCode) throws NotPossibleException {
    InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

    try {
      return compiler.ignoreWarnings()
          .useParentClassLoader(Thread.currentThread().getContextClassLoader())
          .compile(fqnCls, clsSrcCode);
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, e,
          new Object[] { fqnCls });
    }
  }

  /**
   * @effects 
   *  compiles <code>srcFile</code> whose FQN is <code>fqnCls</code> 
   *  using the {@link ClassLoader} of the current execution thread.
   *  
   *  <p>The output class is <b>kept in memory</b> but not stored on disk. 
   *  
   *  <p>throws NotPossibleException if fails.
   */
  public static Class javacInMem(String fqnCls, File srcFile) throws NotPossibleException {
    String srcFileContent = ToolkitIO.readTextFileContent(srcFile);
    
    return javacInMem(fqnCls, srcFileContent);
  }

  /**
   * @effects 
   *  compile all the classes specified in <code>srcFileMap</code> and return 
   *  them in another {@link Map}.
   *  
   *  <p>The output classes are <b>kept in memory</b> but not stored on disk. 
   *  
   */
  public static Map<String,Class<?>> javacFromFilesInMem(Map<String, File> srcFileMap) throws NotPossibleException {
    InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();
    srcFileMap.forEach((fqn, file) -> {
      try {
        compiler.addSource(fqn, ToolkitIO.readTextFileContent(file));
      } catch (Exception e) {
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, e,
            new Object[] { fqn });
      }
    });
    
    try {
      return compiler.ignoreWarnings()
          .useParentClassLoader(Thread.currentThread().getContextClassLoader())
          .compileAll();
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, e,
          new Object[] { "" });
    }
  }
  
  /**
   * @effects 
   *  compile all the classes specified in <code>srcFileMap</code> and return 
   *  them in another {@link Map}.
   *  
   *  <p>The output classes are <b>kept in memory</b> but not stored on disk. 
   */
  public static Map<String,Class<?>> javacInMem(Map<String, String> srcFileMap) throws NotPossibleException {
    InMemoryJavaCompiler compiler = InMemoryJavaCompiler.newInstance();

    srcFileMap.forEach((fqn, fileContent) -> {
      try {
        compiler.addSource(fqn, fileContent);
      } catch (Exception e) {
        throw new NotPossibleException(
            NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, e,
            new Object[] { fqn });
      }
    });
    
    try {
      return compiler.ignoreWarnings()
          .useParentClassLoader(Thread.currentThread().getContextClassLoader())
          .compileAll();
    } catch (Exception e) {
      throw new NotPossibleException(
          NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, e,
          new Object[] { "" });
    }
  }
}
