package jda.modules.swtool;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.common.javac.JavaC;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.util.DClassTk;
import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mccl.conceptualmodel.MCCModel;
import jda.modules.mccl.conceptualmodel.MainCC;
import jda.modules.mccl.util.MCCTk;
import jda.modules.mccltool.MCCGenTool;
import jda.modules.mccltool.mainmodule.MCCMainGenTool;
import jda.modules.sccl.conceptualmodel.SCC;
import jda.modules.sccltool.SCCGenTool;
import jda.modules.sccltool.software.SWGenTool;
import jda.util.SwTk;

/**
 * @overview Generates a software variant of a seed software example (which includes a seed domain model) 
 * which contains <code>n</code> copies of the seed domain model. 
 * 
 * <p>The seed software and its number of copies (<code>n</code>) is taken as input parameters.
 * <p>Tasks include:
 * <ol>
 *  <li>generates <code>n</code> classes C1, ..., Cn of each domain class C in the domain model
 *  <li>generates <code>n</code> MCCs MCC1,...,MCCn, one for each Ci
 *  <li>generates SCC <code>SCCn</code> containing all the generated MCCs of the software
 *  <li>generates software <code>SWn</code> from SCCn
 *  <li>execute <code>SWn</code>
 * </ol>
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public class SwGenByCount {

  /**
   * The domain name of the software
   */
  private String domainName; // e.g. "CourseMan"
  /**
   * FQN of the seed model package.
   * This is usually the package named <code>model</code>. For example, 
   * the seed model package of the CourseMan software would be <code>org.example.courseman.model</code>.
   * <p>Assumes: all domain classes of the model are defined in this package 
   */
  private String seedModelPkg;
  
  /** FQN of the package that contains the generated domain model 
   * by {@link #genDomainModel()} using {@link #seedModelPkg} as output. 
   * <p>This is typically another package in the same code project. */
  private String targetModelPkg;
  
  /**
   * Absolute path to the main source code folder of the software. 
   * {@link #seedModelPkg} is defined relative from this folder.
   */
  private String rootSrcPath; // e.g. /data/git/jdomainapp-modules/jdomainappPlugin/casestudies/CourseMan/src

  /** the {@link File} representation of {@link #rootSrcPath}*/
  private File rootSrcDir;
  
  /**
   * FQN of the modules package, where the functional MCCs will be created. 
   * This is usually the package named <code>modules</code> that is at the same level as {@link #seedModelPkg}.
   * For example, 
   * the software package of the CourseMan example would be <code>org.example.courseman.modules</code>. 
   */
  private String modulesPkg;
  
  /**
   * FQN of the software package, where the main MCC and software class will be created. 
   * This is usually the package named <code>software</code> that is at the same level as {@link #seedModelPkg}.
   * For example, 
   * the software package of the CourseMan example would be <code>org.example.courseman.software</code>. 
   */
  private String softwarePkg;

  /** the root output directory of the compiled classes*/
  private File outputDir;

  /** number of copies of the seed domain model of the software */
  private int n;

  private static Logger logger = LoggerFactory.getLogger(SwGenByCount.class.getSimpleName());

  ///// OUTPUT artefacts
  /** the generated domain model source files: 
   * key: FQN of each domain class<br>
   * value: File object of the .java file
   */
  private Map<String,File> targetModelSrcFiles;
  
  /** the generated MCCs of the target src files in {@link #targetModelSrcFiles} 
   * key: FQN of each domain class<br>
   * value: the generated MCC
   */
  private Map<String,MCC> targetMCCs;
  
  private MainCC mainMCC;
  private SCC scc;
  private String swcFqn;
  
  public SwGenByCount(String domainName, 
      String rootSrcPath, 
      String outputPath, 
      String seedModelPkg, 
      String targetModelPkg,
      String modulesPkg, 
      String softwarePkg, 
      int n) {
    this.domainName = domainName;
    this.rootSrcPath = rootSrcPath;
    this.rootSrcDir = new File(rootSrcPath);
    this.outputDir = new File(outputPath);
    this.seedModelPkg = seedModelPkg;
    this.targetModelPkg = targetModelPkg;
    this.modulesPkg = modulesPkg;
    this.softwarePkg = softwarePkg;
    this.n = n;
  }
  
  /**
   * @effects 
   *  generates {@link #n} copies C1, ..., Cn of each domain class C in {@link #seedModelPkg}. 
   *  Store the generated classes in the package with the same "{@link #seedModelPkg}+n"
   */
  public SwGenByCount genDomainModel() throws NotPossibleException {
    logger.info("Generating the domain model "+n+" (copies)...");

    // destination package name
    // constructed from seedModelPkg by adding n to the name
    if (targetModelPkg == null)
      targetModelPkg = seedModelPkg+n;
    
    // create destPkg folder in rootSrcPath
    ToolkitIO.touchPath(rootSrcPath, targetModelPkg);
    
    int min = 1, 
        max = n;
    
    targetModelSrcFiles = new LinkedHashMap<>();
    for (int newNameId = min; newNameId <= max; newNameId++) {
      Map<String, File> newSrcfiles = ToolkitIO.refactorSrcFilesInPkg(
          rootSrcPath, seedModelPkg, newNameId, targetModelPkg);
      if (newSrcfiles == null) {
        throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, 
            new Object[] {"Package: " + seedModelPkg});
      }
      targetModelSrcFiles.putAll(newSrcfiles);
    }
    
    // compile classes
    
    Map<String,Class<?>> compiled = JavaC.javacLoad(
        seedModelPkg, rootSrcDir, targetModelSrcFiles, 
        outputDir, null);
    
//    Class c = compiled.values().iterator().next();
//    System.out.println(c + " -> " + c.getProtectionDomain().getCodeSource().getLocation());
//    System.out.println(c + " -> " + SwToolTk.class.getClassLoader().getSystemResource(c.getName().replace(".", "/").concat(".class")));
    
//    logger.debug("genDomainModel: compiled classes: " + compiled.values());
    
//    ToolkitIO.promptAny("\nIMPORTANT: Ensure the domain model is compiled before moving to the next step. "
//        + "Please REFRESH the target project in the IDE!");

    return this;
  }
  
  /**
   * @effects 
   *  generates MainCC of the software
   */
  public SwGenByCount genMainMCC() throws NotPossibleException {
    logger.info("Generating MainMCC...");

    MCCMainGenTool tool = // new MCCGenTool(rootSrcPath, clsFQN) ;
        MCCMainGenTool.getInstance(domainName, rootSrcPath, softwarePkg);
    this.mainMCC = (MainCC) tool.exec();  
    
    // compile classes
    Class<?> output = JavaC.javacLoad(mainMCC.getPackage(), rootSrcDir, 
        mainMCC.getFqn(), mainMCC.getOutputSrcFile(), outputDir, null);
    
//    ToolkitIO.promptAny("\nIMPORTANT: Ensure the MainMCC is compiled before moving to the next step. "
//        + "Please REFRESH the target project in the IDE!");
    
    return this;
  }
  
  /**
   * @effects 
   *  generates {@link #n} MCCs MCC1,...,MCCn, one for each Ci {@link #seedModelPkg}, and set them to {@link #targetMCCs}
   */
  public SwGenByCount genMCCs() throws NotFoundException {
    logger.info("Generating functional MCCs...");

    /*
     * TODO + (ducmle) generateMCC - use MCCGenTool.getInstance instead - to add
     * the newly generated MCC to the shared MCCModel (this is required by
     * MCCUpdateTool to perform update on the MCCs.)
     */
    targetMCCs = new LinkedHashMap<>();
    Map<String, File> targetMCCSrcs = new LinkedHashMap<>();  // for compilation
    
    MCCModel mccModel = new MCCModel(new Dom(rootSrcPath));
    targetModelSrcFiles.forEach((clsFQN, f) -> {
      // exclude enums and sub-classes
      Class c = DClassTk.findClass(clsFQN);
      if (!DClassTk.isEnum(c) && !DClassTk.isProperSubType(c)) {
        String mccPkgName = 
            (modulesPkg != null) ? modulesPkg :
              MCCTk.getMCCPackage(c.getPackage().getName(), "modules" + n);
        
        MCCGenTool mccGenTool = MCCGenTool.getInstance(mccModel, rootSrcPath, clsFQN, mccPkgName);
        MCC m = (MCC) mccGenTool.exec();
        targetMCCs.put(clsFQN, m);
        
        targetMCCSrcs.put(clsFQN, m.getOutputSrcFile());
      }
    });
    
    // compile classes
    Map<String,Class<?>> compiled = JavaC.javacLoad(
        rootSrcDir, targetMCCSrcs, 
        outputDir, null);
    
//    ToolkitIO.promptAny("\nIMPORTANT: Ensure the MCCs are compiled before moving to the next step. "
//        + "Please REFRESH the target project in the IDE!");
    
    return this;
  }
  
  /**
   * @effects 
   *  generates {@link SCC} of the software and sets it to {@link #scc}. 
   */
  public SwGenByCount genSCC() {
    logger.info("Generating SCC...");

    SCCGenTool tool = getSCCGenToolInstance();
    
    return doGenSCC(tool);
  }
  
  /**
   * @effects 
   *  performs <code>tool.exec()</code> to generate SCC
   */
  protected SwGenByCount doGenSCC(SCCGenTool tool) {
    this.scc = (SCC) tool.exec();
    
    // compile class
//    JavaCInMem.javacInMem(scc.getFqn(), scc.getSourceCode());
    Class<?> output = JavaC.javacLoad(scc.getPackage(), rootSrcDir, 
        scc.getFqn(), scc.getOutputSrcFile(), outputDir, null);
    
    return this;
  }
  
  /**
   * ONLY For sub-types to use
   * @effects 
   *  this.scc = scc
   */
  protected final void setSCC(SCC scc) {
    this.scc = scc;
  }
  
  /**
   * @effects 
   *  create (if not already) and return the {@link SCCGenTool} instance needed to 
   *  perform {@link #genSCC()}.
   */
  protected final SCCGenTool getSCCGenToolInstance() {
    List<String> mccClsFQNs = 
        targetMCCs.values().stream()
          .map(mcc -> mcc.getFqn())
          .collect(Collectors.toList());
    
    SCCGenTool tool = SCCGenTool.getInstance(domainName, rootSrcPath,
        mccClsFQNs,
        mainMCC.getFqn(), null);
    
    return tool;
  }

  /**
   * @effects 
   *  generates software class from {@link #scc} 
   */
  public SwGenByCount genSwClass()  throws NotPossibleException {
    return genSwClass(this.scc);
  }
  
  /**
   * @effects 
   *  generates software class from scc
   */
  public SwGenByCount genSwClass(SCC scc)  throws NotPossibleException {
    logger.info("Generating the software class...");

    SWGenTool tool = SWGenTool.getInstance(domainName, rootSrcPath, scc.getFqn());
    this.swcFqn = (String) tool.exec();
    
    return this;
  }
  
  /**
   * <b>IMPORTANT</b> This cannot be executed immediately after the previous generation steps, unless
   * {@link #scc} has been compiled and made available in the class path. 
   *  
   * @requires {@link #scc} has been compiled in the classpath
   * @effects 
   *  executes the software class 
   */
  public SwGenByCount executeSw() throws NotFoundException, NotPossibleException {
    return executeSw(this.swcFqn);
  }
  
  public SwGenByCount executeSw(String swcFqn) throws NotFoundException, NotPossibleException {
    logger.info("Executing a software instance");
    
    // load the class
    Class swcls;
    try {
      swcls = Class.forName(swcFqn);
    } catch (ClassNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.CLASS_NOT_FOUND, e, new Object[] {swcFqn});
    }
    
    // execute the software class
    try {
      SwTk.executeSoftware(swcls);
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM_METHOD, e, 
          new Object[] {swcFqn, "run", ""});  
    }  
    
    return this;
  }
  
  /**
   * @effects 
   * 
   */
//   static Logger initLogger(String name) {
//     Logger logger = Logger.getLogger(name);
//     String logFolder = ToolkitIO.getPath(System.getProperty("user.home"), "tmp", "logging").toString();
//     ToolkitIO.createFolderIfNotExists(logFolder);
//     
//     String logFile = logFolder + File.separator + SwGenByCount.class.getSimpleName() + ".log";
//     boolean append = true;
//     FileHandler handler;
//     try {
//       handler = new FileHandler(logFile, append);
//       Formatter format;
//       handler.setFormatter(new SimpleFormatter());
//       logger.addHandler(handler);
//     } catch (SecurityException | IOException e) {
//       e.printStackTrace();
//     }
//     
//     return logger;
//   }

  /**
   * @effects 
   * 
   */
  public String getSwcFqn() {
    return swcFqn;
  }

  /**
   * @effects 
   * 
   */
  public SCC getSCC() {
    return scc;
  }
  
  protected Logger getLogger() {
    return logger;
  }
}
