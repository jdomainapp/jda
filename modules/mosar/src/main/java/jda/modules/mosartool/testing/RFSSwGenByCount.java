package jda.modules.mosartool.testing;

import javax.json.JsonObject;

import jda.modules.mosar.config.RFSGenDesc;
import jda.modules.sccl.conceptualmodel.SCC;
import jda.modules.sccltool.SCCGenTool;
import jda.modules.swtool.SwGenByCount;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class RFSSwGenByCount extends SwGenByCount {

  private JsonObject rfsGenConfig;
  
  public RFSSwGenByCount(String domainName, 
      String rootSrcPath, 
      String outputPath,
      String seedModelPkg, 
      String targetModelPkg,
      String modulesPkg,
      String softwarePkg,
      int n, JsonObject rfsGenConfig) {
    super(domainName, rootSrcPath, 
        outputPath, seedModelPkg, targetModelPkg, modulesPkg, softwarePkg, n);
    
    this.rfsGenConfig = rfsGenConfig;
  }
  
  /**
   * @effects 
   *  extends super.{@link #genSCC()} with adding {@link RFSGenDesc} to the generated SCC.
   */
  @Override
  public SwGenByCount genSCC() {
    getLogger().info("Generating SCC...");

    SCCGenTool tool = getSCCGenToolInstance();
    
    // add RFSGenDesc configuration into the tool
    tool.addConfig(RFSGenDesc.class, rfsGenConfig);
    
    return doGenSCC(tool);
  }
}
