package jda.modules.sccl.conceptualmodel;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;


public class SCCModel {
  
  private static final boolean debug = Toolkit.getDebug(SCCModel.class);

  private static final String File_Module_Main_Descriptor= "MCCMain.temp";

  public SCCModel() {
  }
  
  /**
   * @effects 
   *  generate SCC of the software from the input args and return it.
   *  
   *  Throws NotFoundException or NotPossibleException if failed.
   *   
   */
  public SCC genSCC(String domainName, 
      List<String> mccClsFQNs, String moduleMainClsFQN, String swcOutputRootDir, int swcCounter, Map<String,Object> newTemplateData) throws NotFoundException, NotPossibleException {
    return genSCC(domainName, mccClsFQNs, moduleMainClsFQN, swcOutputRootDir, 
        swcCounter, newTemplateData, null);
  }
  
  /**
   * This method supports <code>addedConfig</code> that can be added to the generated SCC. 
   * 
   * @effects 
   *  generate SCC of the software from the input args and return it.
   *  
   *  Throws NotFoundException or NotPossibleException if failed.
   * 
   * @version 5.4.1
   */
  public SCC genSCC(String domainName, 
      List<String> mccClsFQNs, String moduleMainClsFQN, String swcOutputRootDir, int swcCounter, 
      Map<String,Object> newTemplateData, 
      Map<Class<? extends Annotation>, JsonObject> addedConfig
      ) throws NotFoundException, NotPossibleException {
    
    // create m's header
    String swcName = getSCCName(swcCounter);
    
    SCC scc = new SCC(domainName, mccClsFQNs,moduleMainClsFQN, swcName,newTemplateData);
    
    // create SystemDesc
    scc.createSystemDesc();

    // support additional config
    if (addedConfig != null) {
      addedConfig.forEach((anoType, data) -> {
        scc.createCfgAnnotation(anoType, data);
      });
    }
    
    String swcPkg;
    if (!moduleMainClsFQN.equals("") && moduleMainClsFQN.contains(".")) {
    	swcPkg = moduleMainClsFQN.substring(0,moduleMainClsFQN.lastIndexOf('.'));
    	swcPkg = swcPkg+".config";
    } else {
      // no package
    	swcPkg = "sofware.config";
    }
    scc.setPackageName(swcPkg);
    
    // write m to file
    scc.save(swcOutputRootDir);
    
    if (debug)
      System.out.println(scc);
    
    return scc;
  }
  
  /**
   * @effects 
   *  return the MCC's name generated from <tt>className</tt> of the domain class.
   */
  private String getSCCName(int swcCounter) {
    return "SCC" + swcCounter;
  }

}

