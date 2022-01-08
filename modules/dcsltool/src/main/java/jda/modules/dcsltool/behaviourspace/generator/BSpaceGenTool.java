package jda.modules.dcsltool.behaviourspace.generator;

import java.io.File;
import java.util.Arrays;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.dcsltool.behaviourspace.BSpaceTool;

/**
 * @overview 
 *  Implements the tool interface for clients to use.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 4.0
 * @deprecated: (as of version 5.2)) use {@link BSpaceTool} instead. 
 */
public class BSpaceGenTool implements ModuleToolable {

  private String rootSrcPath;
  //private String clsPkgPath;
  private String clsPkgName;
  private String[] clsSimpleNames;
  
  private static BSpaceGenTool instance;
  
  public static BSpaceGenTool getInstance(String rootSrcPath, 
      //String clsPkgPath, 
      String clsPkgName, 
      String... clsSimpleNames) {
    if (instance == null) {
      instance = new BSpaceGenTool(rootSrcPath, clsPkgName, clsSimpleNames);
    } else { // update
      instance.rootSrcPath = rootSrcPath;
      instance.clsPkgName = clsPkgName;
      instance.clsSimpleNames = clsSimpleNames;

      instance.validate();
    }

    return instance;
  }
  
  protected BSpaceGenTool(String rootSrcPath, 
      //String clsPkgPath, 
      String clsPkgName, 
      String... clsSimpleNames) {
    this.rootSrcPath = rootSrcPath;
    //this.clsPkgPath = clsPkgPath;
    this.clsPkgName = clsPkgName;
    this.clsSimpleNames = clsSimpleNames;
    
    validate();
  }

  private void validate() {
    if (this.clsSimpleNames != null && this.clsSimpleNames.length == 0)
      this.clsSimpleNames = null;
  }
  
  /* (non-Javadoc)
   * @see domainapp.module.ModuleToolable#exec()
   */
  /**
   * @effects 
   * 
   */
  @Override
  public Object exec(Object...ags) throws NotPossibleException {

    if (clsSimpleNames == null) return false;
    
    // output dir of the generated source files (same as srcPath to overwrite the input)
    String srcOutputDir = rootSrcPath;

    if (srcOutputDir == null) {
      // no output dir: use the running dir
      String cwd = System.getProperty("user.dir");
      srcOutputDir = cwd+File.separator+"output";
    }
    
    // process each source file
    String srcFile;
    String clsPkgPath = ToolkitIO.getPath(rootSrcPath, ToolkitIO.splitPackageName(clsPkgName)).toString();
    
    System.out.println("\nRunning BSpaceGen...");
    System.out.println("   Root source path: " + rootSrcPath);
    System.out.printf("   Package name: %s (path: %s)%n", clsPkgName, clsPkgPath);
    System.out.println("   Domain classes: " + Arrays.toString(clsSimpleNames) + "\n");
    
    for (String clsName : clsSimpleNames) {
      srcFile = clsPkgPath + File.separator + clsName + ".java";
      String domainCls = clsPkgName +"."+clsName;
      
      String[] domainPkgs = null; //{clsPkg};
      
      System.out.println("   Domain class: " + domainCls);
      System.out.println("   ...to be overwritten");

      SourceModel srcModel = new SourceModel(rootSrcPath);
      BSpaceGen gen = BSpaceGen.getInstance(srcModel);
      String clazz = gen.exec(clsPkgName, clsName, srcFile, domainPkgs, srcOutputDir);

      System.out.println("...ok");
    }
    
    return true;
  }
  
  /**
   * @requires 
   *  -DrootSrcPath neq null /\ 
   *  args[0] = package-FQN /\ 
   *  args[1] = domain-class-simple-name(s)
   *  
   * @effects 
   *  execute {@link BSpaceGenTool} on the specified domain class(es)
   */
  public static void main(String[] args) throws Exception {
    String rootSrcPath = System.getProperty("rootSrcPath");
    
    if (rootSrcPath == null || args == null || args.length < 2) {
      System.out.println("Usage: " + BSpaceGenTool.class + 
          " -DrootSrcPath=<path-to-root-source-dir> <package-FQN> <domain-class-simple-name(s)>" +
          "\nWhere: \n"
          + "  package-FQN: fully qualified name of the package containing the domain classes \n"
          + "  domain-class-simple-name(s): comma-separated list of simple names of the domain classes in the specified package (e.g. \"Student,Undergrad, Postgrad\")");
      System.exit(0);
    }

    String pkgName = args[0];
    String domainClsNameStr = args[1];
    String[] domainClsNames = domainClsNameStr.split(",");
    
    BSpaceGenTool tool = BSpaceGenTool.getInstance(rootSrcPath, pkgName, domainClsNames);
    
    tool.exec();
  }
}
