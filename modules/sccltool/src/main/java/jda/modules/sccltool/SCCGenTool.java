package jda.modules.sccltool;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.sccl.conceptualmodel.SCC;
import jda.modules.sccl.conceptualmodel.SCCModel;
import jda.modules.sccltool.util.Utils;

/**
 * @overview Implements the tool interface for clients to use.
 * 
 * @author Ha Vu Thanh (havt)
 *
 * @version - 1.0: created <br>
 *          - 2.0: updated (ducmle)
 */
public class SCCGenTool implements ModuleToolable {

  private static SCCGenTool instance;
  private String rootSrcPath;
  private List<String> mccClsFQNs;
  private String moduleMainClsFQN;
  private Map<String, Object> newTemplateData;
  /** problem domain name (e.g. CourseMan) */
  private String domainName;
  
  /**
   * additional configuration data for SCC<br>
   * - Annotation class: to create the annotation element to be added to SCC<br>
   * - JsonObject: the JsonObject that holds data for the annotation element
   */
  private Map<Class<? extends Annotation>, JsonObject> addedConfig;

  public static SCCGenTool getInstance(
      /** problem domain name (e.g. CourseMan) */
      String domainName,
      String rootSrcPath,
      List<String> mccClsFQNs, String moduleMainClsFQN,
      Map<String, Object> newTemplateData) {
    if (instance == null) {
      instance = new SCCGenTool(domainName, rootSrcPath, mccClsFQNs, moduleMainClsFQN,
          newTemplateData);
    } else {
      // ducmle: update instance
      instance.domainName = domainName;
      instance.rootSrcPath = rootSrcPath;
      instance.mccClsFQNs = mccClsFQNs;
      instance.moduleMainClsFQN = moduleMainClsFQN;
      instance.newTemplateData = newTemplateData;
    }
    

    return instance;
  }

  public SCCGenTool(String domainName, String rootSrcPath, List<String> mccClsFQNs,
      String moduleMainClsFQN, Map<String, Object> newTemplateData) {
    this.domainName = domainName;
    this.rootSrcPath = rootSrcPath;
    this.mccClsFQNs = mccClsFQNs;
    this.moduleMainClsFQN = moduleMainClsFQN;
    this.newTemplateData = newTemplateData;
  }

  /**
   * @effects 
   *  
   * @version 5.4.1
   */
  public void addConfig(Class<? extends Annotation> anoType, JsonObject anoData) {
    if (addedConfig == null) {
      addedConfig = new HashMap<>();
    }
    
    addedConfig.put(anoType, anoData);
  }
  
  @Override
  public Object exec(Object... args) throws NotPossibleException {
    if (moduleMainClsFQN == null)
      return false;
    String swcOutputDirPath = "";
    if (!moduleMainClsFQN.equals("") && moduleMainClsFQN.contains(".")) {
      swcOutputDirPath = moduleMainClsFQN.substring(0,
          moduleMainClsFQN.lastIndexOf('.'));
      swcOutputDirPath = ToolkitIO
          .getPath(rootSrcPath, swcOutputDirPath.split("\\.")).toString();
    }
    swcOutputDirPath = swcOutputDirPath + File.separator + 
        //"software" + File.separator + 
        "config";

    // getCounter
    File swcOutputRootDir = new File(swcOutputDirPath);
    int swcCounter = 0;
    if (!swcOutputRootDir.exists()) {
      swcCounter = 0;
    } else {
      File[] listFile = Utils.listFileInFolder(swcOutputDirPath,
          "SCC*\\d*\\d\\.java");
      int id;
      for (File f : listFile) {
        id = Integer
            .parseInt(f.getName().replace("SCC", "").replace(".java", ""));
        if (id > swcCounter) {
          swcCounter = id;
        }
      }
    }

    swcCounter = swcCounter + 1;
    String swcOutputRootDirPath = rootSrcPath;
    System.out.println("\nRunning SCCGen...");
    System.out.println("  Domain name: " + domainName);
    System.out.println("  Root Output dir: " + swcOutputRootDirPath);
    System.out.println("SCC Counter =" + swcCounter);
    SCCModel swcModel = new SCCModel();
    SCC swc = swcModel.genSCC(domainName, mccClsFQNs, moduleMainClsFQN,
        swcOutputRootDirPath, swcCounter, newTemplateData, addedConfig);

    System.out.println("...ok");

    return swc;
  }
}
