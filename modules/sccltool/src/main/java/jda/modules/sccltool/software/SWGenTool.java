package jda.modules.sccltool.software;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.sccltool.software.model.SWC;

/**
 * @overview Generate a <b>standard</b> software class from an input SCC. 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2 
 */
public class SWGenTool implements ModuleToolable {
  private String rootSrcPath;
  /**FQN of the SCC */
  private String sccFQN;
  /** problem domain name (e.g. CourseMan) */
  private String domainName;

  private static SWGenTool instance;
  
  public static SWGenTool getInstance(
      /** problem domain name (e.g. CourseMan) */
      String domainName,
      String rootSrcPath, 
      /**e.g. "vn.com.courseman.software.config.SCC1" 
       * */
      String sccFQN
      ) {
    if (instance == null) {
      instance = new SWGenTool(domainName, rootSrcPath, sccFQN);
    } else { // update
      instance.domainName = domainName;
      instance.rootSrcPath = rootSrcPath;
      instance.sccFQN = sccFQN;
    }
    
    return instance;
  }
  
  private SWGenTool(
      /** problem domain name (e.g. CourseMan) */
      String domainName,
      String rootSrcPath, /**e.g. /home/dmle/projects/domainapp/modules/mccl/src.examples */
      /**e.g. "vn.com.courseman.software.config.SCC1" */
      String sccFQN
      ) {
    this.domainName = domainName;
    this.rootSrcPath = rootSrcPath;
    this.sccFQN = sccFQN;
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

    if (sccFQN == null) return null;
    
    // output dir of the generated source files
    // MCC is written in a sub-package directory of this output dir
    String srcOutputDir = rootSrcPath;

    // process each source file
//    String srcFile = ToolkitIO.getPath(rootSrcPath, clsFQNEls).toString() + ToolkitIO.FILE_JAVA_EXT;
    System.out.println("\nRunning SwGen...");
    System.out.println("   SCC: " + sccFQN);
//    System.out.println("   Source file: " + srcFile);
    System.out.println("   Root source dir: " + srcOutputDir);
    
    String clsFqn = genSWC(domainName, sccFQN, srcOutputDir);

    System.out.println("...ok");
    
    return clsFqn;
  }

  /**
   * @effects 
   *  
   */
  private String genSWC(String swName, String sccClsFqn, String srcOutputDir) throws NotFoundException, NotPossibleException {
    String swcName = getSoftwareClsName(swName);
    
    Class scc = null;
    try {
      scc = Class.forName(sccClsFqn);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new String[] {sccClsFqn});
    }
    
    SWC swc = new SWC(swcName, scc);
    
    // create method
    swc.createStandardMainMethod();
    
    // set swc's package
    String sccPkg = scc.getPackage().getName();

    String swcPkg;
    if (sccPkg != null) {
      swcPkg = sccPkg.substring(0, sccPkg.lastIndexOf(".")); // exclude ".config" 
    } else {
      // no package
      swcPkg = "software";
    }
    swc.setPackageName(swcPkg);
    
    // write m to file
    swc.save(srcOutputDir);
    
    return swc.getFqn();
  }

  /**
   * @effects 
   *  return <tt>swName</tt> + prefix
   */
  private String getSoftwareClsName(String swName) {
    return swName + "Software";
  }

}
