package jda.modules.swtool;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
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
 * @overview Generates a software from its domain model. 
 * 
 * <p>Tasks include:
 * <ol>
 *  <li>generates <code>n</code> MCCs MCC1,...,MCCn, one for each domain class Ci
 *  <li>generates an SCC containing all the generated MCCs of the software
 *  <li>generates software <code>SW</code> from SCC
 *  <li>execute <code>SW</code>
 * </ol>
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public class SwGen {
  private static Logger logger = (Logger) LoggerFactory.getLogger(SwGen.class.getSimpleName());
  
  /**
   * The domain name of the software
   */
  private String domainName; // e.g. "CourseMan"
  
  /**
   * Sub-packages in {@link #modulesPkg} that contain the the domain classes of each module
   */
  private Collection<String> modelPkgs;
  
  /**
   * The model package name (typically <code>model</code>) of each module
   */
  private String modelPkgName;

  /**
   * The official domain model object containing the source code files of the 
   * domain classes of the domain model (i.e. contained in {@link #modelPkgs}).
   */
  private Dom dom;

  /**
   * Absolute path to the main source code folder of the software. 
   * {@link #seedModelPkg} is defined relative from this folder.
   */
  private String rootSrcPath; // e.g. /data/git/jdomainapp-modules/jdomainappPlugin/casestudies/CourseMan/src

  /** the {@link File} representation of {@link #rootSrcPath}*/
  private File rootSrcDir;
  
  /**
   * FQN of the modules package, where the modules reside. 
   * This is usually the package named <code>services</code>.
   * For example, 
   * the software package of the CourseMan example would be <code>org.example.courseman.services</code>. 
   */
  private String modulesPkg;
  
  /**
   * FQN of the software package, where the main MCC and the SCC will be created. 
   * This is usually the package named <code>software</code> that is at the same level as {@link #modulesPkg}.
   * For example, 
   * the software package of the CourseMan example would be <code>org.example.courseman.software</code>. 
   */
  private String softwarePkg;

  private boolean includesSubTypes;

  private boolean compileSrc;

  
  /** the root output directory of the compiled classes*/
  private File outputDir;

  ///// OUTPUT artefacts

  /** the generated MCCs of the target src files in {@link #targetModelSrcFiles} 
   * key: FQN of each domain class<br>
   * value: the generated MCC
   */
  private Map<String,MCC> targetMCCs;
  
  private MainCC mainMCC;
  private SCC scc;
  private String swcFqn;

  public SwGen(String domainName, 
      String modelPkgName,
      String rootSrcPath, 
      String outputPath,
      String modulesPkg,
      String softwarePkg,
      boolean includesSubTypes,
      boolean compileSrc
      ) {
    this.domainName = domainName;
    this.modelPkgName = modelPkgName;
    this.rootSrcPath = rootSrcPath;
    this.rootSrcDir = new File(rootSrcPath);
    this.outputDir = new File(outputPath);
    this.modulesPkg = modulesPkg;
    this.softwarePkg = softwarePkg;
    this.includesSubTypes = includesSubTypes;
    this.compileSrc = compileSrc;
  }
  
  /**
   * @effects 
   *  initialises this by loading all the domain classes into {@link #dom}. 
   */
  public SwGen init() throws NotPossibleException {
    // read the domain classes into Dom
    // the domain classes are located under the "model" subpackages of rootSrcPath
    modelPkgs = ToolkitIO.getSubPackagesMatching(rootSrcPath, modulesPkg, modelPkgName);
    if (modelPkgs == null)
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {"Domain model specification: " + modulesPkg + "..." + modelPkgName});
  
    dom = new Dom(rootSrcPath);
    modelPkgs.forEach(pkgName -> dom.loadClassesInPackage(pkgName));
    
    return this;
  }
  
  /**
   * @effects 
   *  generates MainCC of the software
   */
  public SwGen genMainMCC() throws NotPossibleException {
    logger.info("Generating MainMCC...");

    MCCMainGenTool tool = // new MCCGenTool(rootSrcPath, clsFQN) ;
        MCCMainGenTool.getInstance(domainName, rootSrcPath, softwarePkg);
    this.mainMCC = (MainCC) tool.exec();  
    
    // compile classes
    if (compileSrc) {
      logger.info("Compiling MainMCC.....");
      Class<?> output = JavaC.javacLoad(mainMCC.getPackage(), rootSrcDir, 
        mainMCC.getFqn(), mainMCC.getOutputSrcFile(), outputDir, null);
    }
    
    return this;
  }
  
  /**
   * @requires 
   *  the domain model classes are available in the classpath
   *  
   * @effects 
   *  generates MCCs MCC1,...,MCCn, one for each domain class Ci in the domain model
   */
  public SwGen genMCCs() throws NotFoundException {
    logger.info("Generating functional MCCs...");

    targetMCCs = new LinkedHashMap<>();
    Map<String, File> targetMCCSrcs = new LinkedHashMap<>();  // for compilation
    
    MCCModel mccModel = new MCCModel(dom);
    Map<String, String> domFiles = dom.getClassFilesMap();
    
    domFiles.forEach((clsFQN, f) -> {
      // exclude enums and sub-classes
      Class c = DClassTk.findClass(clsFQN);
      if (!DClassTk.isEnum(c) && 
          (includesSubTypes || !DClassTk.isProperSubType(c))) {
        /* v5.4.1: fixed
        String mccPkgName = MCCTk.getMCCPackage(DClassTk.getPackageName(clsFQN));
        */
        String clsPkgName = DClassTk.getPackageName(clsFQN);
        if (clsPkgName == null) clsPkgName = clsFQN;  // why?
        String mccPkgName = MCCTk.getMCCPackage(clsPkgName);
        // END 5.4.1
        
        logger.info(mccPkgName);
        
        MCCGenTool mccGenTool = MCCGenTool.getInstance(mccModel, rootSrcPath, clsFQN, mccPkgName);
        MCC m = (MCC) mccGenTool.exec();
        targetMCCs.put(clsFQN, m);
        
        targetMCCSrcs.put(clsFQN, m.getOutputSrcFile());
      }
    });
    
    // compile classes
    if (compileSrc) {
      logger.info("Compiling MCCs.....");
      Map<String,Class<?>> compiled = JavaC.javacLoad(
        rootSrcDir, targetMCCSrcs, 
        outputDir, null);
    }
    
    return this;
  }
  
  /**
   * @effects 
   *  generates {@link SCC} of the software and sets it to {@link #scc}. 
   */
  public SwGen genSCC() {
    logger.info("Generating SCC...");

    SCCGenTool tool = getSCCGenToolInstance();
    
    return doGenSCC(tool);
  }
  
  /**
   * @effects 
   *  performs <code>tool.exec()</code> to generate SCC
   */
  protected SwGen doGenSCC(SCCGenTool tool) {
    this.scc = (SCC) tool.exec();
    
    // compile class
    if (compileSrc) {
      Class<?> output = JavaC.javacLoad(scc.getPackage(), rootSrcDir, 
        scc.getFqn(), scc.getOutputSrcFile(), outputDir, null);
    }
    
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
  public SwGen genSwClass()  throws NotPossibleException {
    return genSwClass(this.scc);
  }
  
  /**
   * @effects 
   *  generates software class from scc
   */
  public SwGen genSwClass(SCC scc)  throws NotPossibleException {
    logger.info("Generating the software class...");

    SWGenTool tool = SWGenTool.getInstance(domainName, rootSrcPath, scc.getFqn());
    this.swcFqn = (String) tool.exec();
    
    return this;
  }
  
  /**
   * @effects 
   *  generates software class from scc's FQN
   */
  public SwGen genSwClass(String sccFqn)  throws NotPossibleException {
    logger.info("Generating the software class...");

    SWGenTool tool = SWGenTool.getInstance(domainName, rootSrcPath, sccFqn);
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
  public SwGen executeSw() throws NotFoundException, NotPossibleException {
    return executeSw(this.swcFqn);
  }
  
  public SwGen executeSw(String swcFqn) throws NotFoundException, NotPossibleException {
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
