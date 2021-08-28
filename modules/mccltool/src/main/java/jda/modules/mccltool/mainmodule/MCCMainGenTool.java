package jda.modules.mccltool.mainmodule;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptualmodel.MainCC;

/**
 * @overview Generate a <b>standard</b> MCC for Module main. 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class MCCMainGenTool implements ModuleToolable {
  private String rootSrcPath;
  /**FQN name of package where the MCC is stored*/
  private String pkgName;
  /** problem domain name (e.g. CourseMan) */
  private String domainName;

  private static MCCMainGenTool instance;
  
  public static MCCMainGenTool getInstance(
      /** problem domain name (e.g. CourseMan) */
      String domainName,
      String rootSrcPath, 
      /**e.g. "org.jda.example.courseman.software" 
       * */
      String pkgName
      ) {
    if (instance == null) {
      instance = new MCCMainGenTool(domainName, rootSrcPath, pkgName);
    } else { // update
      instance.domainName = domainName;
      instance.rootSrcPath = rootSrcPath;
      instance.pkgName = pkgName;
    }
    
    return instance;
  }
  
  private MCCMainGenTool(
      /** problem domain name (e.g. CourseMan) */
      String domainName,
      String rootSrcPath, /**e.g. /home/dmle/projects/domainapp/modules/mccl/src/example/java */
      /**e.g. "org.jda.example.courseman.software" */
      String pkgName
      ) {
    this.domainName = domainName;
    this.rootSrcPath = rootSrcPath;
    this.pkgName = pkgName;
  }
  
  /* (non-Javadoc)
   * @see domainapp.module.ModuleToolable#exec()
   */
  /**
   * @effects 
   *  generate a software class from <tt>args</tt> and return its FQN.
   *  
   *  Throws NotPossibleException if failed.
   */
  @Override
  public Object exec(Object...args) throws NotPossibleException {

    if (pkgName == null) return null;
    
    // output dir of the generated source files
    // MCC is written in a sub-package directory of this output dir
    String srcOutputDir = rootSrcPath;

    // process each source file
//    String srcFile = ToolkitIO.getPath(rootSrcPath, clsFQNEls).toString() + ToolkitIO.FILE_JAVA_EXT;
    System.out.println("\nRunning "+MCCMainGenTool.class.getSimpleName()+"...");
    System.out.println("   Package name: " + pkgName);
//    System.out.println("   Source file: " + srcFile);
    System.out.println("   Root class output dir: " + srcOutputDir);
    
    MainCC m = genMCCMain(domainName, pkgName, srcOutputDir);

    System.out.println("...ok");
    
    return m;
  }

  /**
   * @effects 
   *  Generate an {@link MainCC} and return it.
   */
  private MainCC genMCCMain(String domainName, String pkgName, String srcOutputDir) throws NotFoundException, NotPossibleException {
    String mccName = getModuleMainName(domainName);

    MainCC maincc = new MainCC(mccName, domainName, pkgName);
    
    maincc.createModuleDesc();
    
    // write m to file
    maincc.save(srcOutputDir);
    
    return maincc;
  }

  /**
   * @effects 
   *  return suitable name for the module main
   */
  private String getModuleMainName(String domainName) {
    return "ModuleMain";
  }

}
