package jda.modules.dcsltool.behaviourspace;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.dcsltool.behaviourspace.generator.BSpaceGen;
import jda.modules.dcsltool.behaviourspace.validator.BSpaceValidator;

/**
 * @overview A shared tool for all operations concerning the behaviour space of a domain class. 
 * These include {@link BSpaceGen} and {@link BSpaceValidator}.
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.2
 */
public class BSpaceTool implements ModuleToolable {

  private String cmd;
  private SourceModel sourceModel;
  private String rootSrcPath;
  //private String clsPkgPath;
  private String clsPkgName;
  private String[] clsSimpleNames;
  
  private static BSpaceTool instance;
  
  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public static BSpaceTool getInstance(
      String cmd, 
      SourceModel sourceModel, 
      String rootSrcPath, 
      String clsPkgName, 
      String... clsSimpleNames) {
    if (instance == null) {
      instance = new BSpaceTool(cmd, sourceModel, rootSrcPath, clsPkgName, clsSimpleNames);
    } else { // update
      instance.cmd = cmd;
      if (sourceModel != null) {
        instance.sourceModel = sourceModel;
      }
      instance.rootSrcPath = rootSrcPath;
      instance.clsPkgName = clsPkgName;
      instance.clsSimpleNames = clsSimpleNames;

      instance.validate();
    }

    return instance;
  }
  
  public static BSpaceTool getInstance(
      String cmd, 
      String rootSrcPath, 
      //String clsPkgPath, 
      String clsPkgName, 
      String... clsSimpleNames) {
    return getInstance(cmd, null, rootSrcPath, clsPkgName, clsSimpleNames);
  }
  
  private BSpaceTool(
      String cmd, 
      SourceModel sourceModel, 
      String rootSrcPath, 
      String clsPkgName, 
      String... clsSimpleNames) {
    this.cmd = cmd;
    if (sourceModel != null) {
      this.sourceModel = sourceModel;
    } else {
      this.sourceModel = new SourceModel(rootSrcPath);
    }
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

  /**
   * @effects 
   *  Performs {@link #cmd} on the behaviour space(es) of the domain class(es) specified by {@link #clsSimpleNames}.
   *  
   *  <p>Result = a single result object if there is only one input domain class OR 
   *      {@link List} of results obtained for all the input domain classes.
   *  
   *  <p>Throws NotPossibleException if failed for some reasons.
   *   
   * @version 2.0
   */
  @Override
  public Object exec(Object...args) throws NotPossibleException {
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
    
    System.out.printf("\nRunning %s...%n", this.getClass().getSimpleName());
    System.out.println("   Command: " + cmd);
    System.out.println("   Root source path: " + rootSrcPath);
    System.out.printf("   Package name: %s (path: %s)%n", clsPkgName, clsPkgPath);
    System.out.println("   Domain classes: " + Arrays.toString(clsSimpleNames) + "\n");
    
    List result = new ArrayList();
    for (String clsName : clsSimpleNames) {
      srcFile = clsPkgPath + File.separator + clsName + ".java";
      String domainCls = clsPkgName +"."+clsName;
      
      System.out.println("   Domain class: " + domainCls);
//      System.out.println("   ...to be overwritten");

      Object res = null;
      if (cmd.equals("gen")) {
        BSpaceGen gen = BSpaceGen.getInstance(sourceModel);
        String[] domainPkgs = null; //{clsPkg};
        res = gen.exec(clsPkgName, clsName, srcFile, domainPkgs, srcOutputDir);
        result.add(res);
      } else if (cmd.equals("validate")){
        BSpaceValidator val = BSpaceValidator.getInstance(sourceModel);
        res = val.exec(clsPkgName, clsName, srcFile);
        result.add(res);
      } else {
        // invalid command
        throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] {"command = '" + cmd + "'"});        
      }
      
      System.out.println("...ok");
    }
    
    return (result.size() == 1) ? result.get(0) : result;
  }
}
