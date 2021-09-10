package jda.modules.common.javac;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @overview 
 *  Toolkit class used for {@link JavaC}.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public class JavaCToolkit {
  private JavaCToolkit() {}
  
  /**
   * @effects 
   *  return all the class path entries of the current class loader.
   *  If the class path is empty then return null.
   *  
   *  <P>IMPORTANT: this method does not work with Eclipse plugin run-time.
   */
  public static String[] getClassPaths() {
    /** Java 9+: 
     * URL:  https://stackoverflow.com/questions/11613988/how-to-get-classpath-from-classloader
     * 
     * List<URI> classpath = new ClassGraph().getClasspathURIs();
     */
    
    // Java 8:
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    URL[] pathUrls = ((URLClassLoader) loader).getURLs();
    
    if (pathUrls != null && pathUrls.length > 0) {
      String[] paths = new String[pathUrls.length];
      int i = 0;
      for (URL u : pathUrls) {
        paths[i++] = u.getPath();
      }
      
      return paths;
    } else {
      return null;
    }
  }

  /**
   * @effects 
   *  if there is context class loader defined 
   *    return it
   *  else
   *    return the class loader of <tt>currentCls</tt> 
   */
  public static ClassLoader getContextClassLoader(Class currentCls) {
    ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();
    if (contextLoader == null) {
      contextLoader = currentCls.getClassLoader();
    }    
    
    return contextLoader;
  }
}
