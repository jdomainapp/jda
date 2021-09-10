package jda.modules.mccltool;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mccl.conceptualmodel.MCCModel;

/**
 * @overview 
 *  Implements the tool interface to automatically generate MCC from a domain class.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 * - 1.0: created<br>
 * - 2.0: updated to take MCCModel as input
 */
public class MCCGenTool implements ModuleToolable {

  private String rootSrcPath;
  private String clsFQN;
  private MCCModel mccModel;
  private String mccPkgName;

  private static MCCGenTool instance;
  
  public static MCCGenTool getInstance(
      MCCModel mccModel,
      /**e.g. /home/dmle/projects/domainapp/modules/mccl/src/example/java */
      String rootSrcPath, 
      /**e.g. "org.jda.example.courseman.modules.student.model" 
       * @param mccModel2 */
      String clsFQN,
      // mccPackageName: e.g. org.jda.example.courseman.modules.student.modules
      String mccPkgName
      ) {
    if (instance == null) {
      instance = new MCCGenTool(mccModel, rootSrcPath, clsFQN, mccPkgName);
    } else { // update
      instance.mccModel = mccModel;
      instance.rootSrcPath = rootSrcPath;
      instance.clsFQN = clsFQN;
      instance.mccPkgName = mccPkgName;
    }
    
    return instance;
  }
  
  public static MCCGenTool getInstance(
      MCCModel mccModel,
      /**e.g. /home/dmle/projects/domainapp/modules/mccl/src/example/java */
      String rootSrcPath, 
      /**e.g. "org.jda.example.courseman.modules.student.model" 
       * @param mccModel2 */
      String clsFQN
      ) {
    return getInstance(mccModel, rootSrcPath, clsFQN, null);
//    if (instance == null) {
//      instance = new MCCGenTool(mccModel, rootSrcPath, clsFQN);
//    } else { // update
//      instance.mccModel = mccModel;
//      instance.rootSrcPath = rootSrcPath;
//      instance.clsFQN = clsFQN;
//    }
//    
//    return instance;
  }
  
  private MCCGenTool(
      MCCModel mccModel, 
      String rootSrcPath, /**e.g. /home/dmle/projects/domainapp/modules/mccl/src/example/java */
      /**e.g. "org.jda.example.courseman.modules.student.model" */
      String clsFQN,
      // mccPackageName: e.g. org.jda.example.courseman.modules.student.modules
      String mccPkgName
      ) {
    this.mccModel = mccModel;
    this.rootSrcPath = rootSrcPath;
    this.clsFQN = clsFQN;
    this.mccPkgName = mccPkgName;
  }
  
  /* (non-Javadoc)
   * @see domainapp.module.ModuleToolable#exec()
   */
  /**
   * @effects 
   *  generate an MCC from <tt>args</tt> and return it.
   *  
   *  Throws NotPossibleException if failed.
   */
  @Override
  public Object exec(Object...args) throws NotPossibleException {

    if (clsFQN == null) return null;
    
    // output dir of the generated source files
    // MCC is written in a sub-package directory of this output dir
    String srcOutputDir = rootSrcPath;

    //MCCModel mccModel = new MCCModel();
    
    // process each source file
    String[] clsFQNEls = clsFQN.split("\\.");
    String srcFile = ToolkitIO.getPath(rootSrcPath, clsFQNEls).toString() + ToolkitIO.FILE_JAVA_EXT;
    System.out.println("\nRunning MCCGen...");
    System.out.println("   Domain class: " + clsFQN);
    System.out.println("   Source file: " + srcFile);
    System.out.println("   MCC package: " + mccPkgName);
    System.out.println("   Root class output dir: " + srcOutputDir);
    
    String clsName = clsFQNEls[clsFQNEls.length-1];
    String pkgName = clsFQN.substring(0, clsFQN.lastIndexOf(".")); 
        
    MCC m = mccModel.genMCC(pkgName, clsName, srcFile, mccPkgName, srcOutputDir);

    System.out.println("...ok");
    
    return m;
  }
  
  /**
   * @requires 
   *  -DrootSrcPath neq null /\ 
   *  args[0] = fully qualified name of a domain class (e.g. Student)
   *  
   * @effects 
   *  execute MCCGen on the specified domain class
   */
  public static void main(String[] args) throws Exception {
    String rootSrcPath = System.getProperty("rootSrcPath");
    
    
    if (rootSrcPath == null || args == null || args.length == 0) {
      System.out.println("Usage: " + MCCGenTool.class + 
          " -DrootSrcPath=<path-to-root-source-dir> <domain-class-FQN>");
      System.exit(0);
    }

    String clsFQN = args[0];
    
    MCCModel mccModel = new MCCModel(new SourceModel(rootSrcPath));
    
    MCCGenTool tool = MCCGenTool.getInstance(mccModel, rootSrcPath, clsFQN);
    
    tool.exec();
  }
  
}
