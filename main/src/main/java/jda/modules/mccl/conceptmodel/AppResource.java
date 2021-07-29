/**
 * 
 */
package jda.modules.mccl.conceptmodel;

import jda.modules.common.exceptions.NotPossibleException;

/**
 * @overview
 *  represent each sub-directory or file of an application structure (i.e. below {@link Configuration#getAppFolder()}
 *   
 * @version 3.2c
 *
 * @author dmle
 */
public enum AppResource {
  config,
    modules(config),
      dvarConfigModule(modules, true),
        moduleResources("resources", dvarConfigModule),
          moduleHelp("help", moduleResources),
            dvarModuleHelpLang(moduleHelp, true),
              fvarHelp(dvarModuleHelpLang, true),
    resources(config),
  export,
    images(export),
    templates(export),
      styles(templates),
  importDir("import"),
  ;

  // the parent dir
  private AppResource parent;

  /**computed by {@link #getName()} (thus must use this method to obtain) */
  private String myName;
  
  // the computed absolute app path
  private String path;
  
  // whether this dir is a variable whose name must be replaced by a run-time argument 
  private boolean isVar; 

  private AppResource() {
    this(null, null, false);
  }
  
  private AppResource(AppResource parent) {
    this(null, parent, false);
  }

  private AppResource(AppResource parent, boolean isVar) {
    this(null, parent, isVar);
  }

  private AppResource(String dirName) {
    this(dirName, null, false);
  }
  
  private AppResource(String dirName, AppResource parent) {
    this(dirName, parent, false);
  }
  
  private AppResource(String dirName, AppResource parent, boolean isVar){
    this.myName = dirName;
    this.parent = parent;
    this.isVar = isVar;
  }

  /**
   * @effects 
   *  return the actual resource name of this
   */
  public String getName() {
    if (myName == null) {
      myName = name();
    }
    
    return myName;
  }
  
  /**
   * @effects 
   *  if this is a variable directory (i.e. the name of which must be replaced at run-time by an argument)
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  public boolean isVariableName() {
    return isVar;
  }
  
  /**
   * Use this method when the path to this <b>does not</b> contain variable directories.
   * 
   * @requires 
   *  {@link #isVariableName()} = false AND none of the directories in the path of this have variable names
   *  
   * @effects 
   *  return the absolute, application-specific path of <tt>this</tt> with the root being <tt>appFolder</tt>.
   *  
   *   <p><b>Note</b>: for performance reason, path is computed once for the first call and is not changed (regardless of what the <tt>appFolder</tt> of subsequent calls are)
   */
  public String getPath(final String appFolder) {
    // compute path and cache for later use
    if (path == null) {
      // build path in reverse order

      StringBuffer pathSb = new StringBuffer(getName());
      
      AppResource myParent = parent;
      while (myParent != null) {
        // prepend parent to buffer 
        pathSb.insert(0, myParent.getName()+Configuration.FILE_SEPARATOR);
        myParent = myParent.getParent();
      }
      
      // the app folder is inserted last
      pathSb.insert(0, appFolder+Configuration.FILE_SEPARATOR);
      path = pathSb.toString();
    }
    
    return path;
  }

  /**
   * Use this method when the path to this <b>does</b> contain variable directories.
   * 
   * @requires 
   *  <tt>varDirNames != null </tt> AND are listed in the traversal order from the root (<tt>appFolder</tt>)
   *  
   * @effects 
   *  return the absolute, application-specific path of <tt>this</tt> with the root being <tt>appFolder</tt>
   *  and with the variable dir names in the path are specified by <tt>varDirNames</tt>
   *  
   *  <p><b>Note</b>: unlike {@link #getPath(String)} which caches the generated path, this method does not cache 
   *  the path as each new one is generated for each <tt>varDirNames</tt> 
   */
  public String getPathWithVars(final String appFolder, final String[] varDirNames) throws NotPossibleException {
    // compute path and cache for later use
    // build path in reverse order
    StringBuffer pathSb;
    
    int varInd = varDirNames.length-1;
    if (isVariableName()) {
      // use first var dir name for this
      pathSb = new StringBuffer(varDirNames[varInd--]);
    } else {
      // use dir name for this
      pathSb = new StringBuffer(getName());
    }
    
    AppResource myParent = parent;
    while (myParent != null) {
      // prepend parent to buffer (use variable dir name is needed)
      if (myParent.isVariableName()) {
        // parent is a variable dir
        pathSb.insert(0, varDirNames[varInd--]+Configuration.FILE_SEPARATOR);
      } else {
        // parent is not a var dir
        pathSb.insert(0, myParent.getName()+Configuration.FILE_SEPARATOR);
      }
      myParent = myParent.getParent();
    }
    
    // the app folder is inserted last
    pathSb.insert(0, appFolder+Configuration.FILE_SEPARATOR);
    
    return pathSb.toString();
  }
  
  /**
   * @effects 
   *  return parent
   */
  private AppResource getParent() {
    return parent;
  }
}